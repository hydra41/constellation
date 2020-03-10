/*
 * Copyright 2010-2019 Australian Signals Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package au.gov.asd.tac.constellation.views.dataaccess.plugins.clean;

import au.gov.asd.tac.constellation.graph.GraphReadMethods;
import au.gov.asd.tac.constellation.graph.GraphWriteMethods;
import au.gov.asd.tac.constellation.schema.visualschema.concept.VisualConcept;
import au.gov.asd.tac.constellation.pluginframework.parameters.PluginParameter;
import au.gov.asd.tac.constellation.schema.analyticschema.concept.AnalyticConcept;
import au.gov.asd.tac.constellation.schema.analyticschema.concept.SpatialConcept;
import au.gov.asd.tac.constellation.utilities.datastructure.Tuple;
import au.gov.asd.tac.constellation.utilities.geospatial.Distance;
import au.gov.asd.tac.constellation.utilities.geospatial.Shape;
import au.gov.asd.tac.constellation.utilities.geospatial.Shape.GeometryType;
import static au.gov.asd.tac.constellation.views.dataaccess.plugins.clean.MergeNodesPlugin.LEAD_PARAMETER_ID;
import static au.gov.asd.tac.constellation.views.dataaccess.plugins.clean.MergeNodesPlugin.MERGER_PARAMETER_ID;
import static au.gov.asd.tac.constellation.views.dataaccess.plugins.clean.MergeNodesPlugin.MERGE_TYPE_PARAMETER_ID;
import static au.gov.asd.tac.constellation.views.dataaccess.plugins.clean.MergeNodesPlugin.SELECTED_PARAMETER_ID;
import static au.gov.asd.tac.constellation.views.dataaccess.plugins.clean.MergeNodesPlugin.THRESHOLD_PARAMETER_ID;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.openide.util.lookup.ServiceProvider;

/**
 * Merge nodes by location
 *
 * @author cygnus_x-1
 */
@ServiceProvider(service = MergeNodeType.class)
public class MergeNodesByLocation implements MergeNodeType {

    private static final String MERGE_TYPE_NAME = "Geospatial Distance";

    @Override
    public String getName() {
        return MERGE_TYPE_NAME;
    }

    @Override
    public void updateParameters(final Map<String, PluginParameter<?>> parameters) {
        parameters.get(MERGE_TYPE_PARAMETER_ID).setEnabled(true);
        parameters.get(THRESHOLD_PARAMETER_ID).setDescription("The geospatial distance (in meters) between two locations to consider");
        parameters.get(THRESHOLD_PARAMETER_ID).setIntegerValue(1000);
        parameters.get(THRESHOLD_PARAMETER_ID).setEnabled(true);
        parameters.get(MERGER_PARAMETER_ID).setEnabled(true);
        parameters.get(LEAD_PARAMETER_ID).setEnabled(false);
        parameters.get(SELECTED_PARAMETER_ID).setEnabled(true);
    }

    @Override
    public final Map<Integer, Set<Integer>> getNodesToMerge(final GraphWriteMethods graph, final Comparator<String> leadVertexChooser, final int threshold, final boolean selectedOnly) throws MergeException {
        final Map<Integer, Set<Integer>> nodesToMerge = new HashMap<>();

        final int identifierAttribute = VisualConcept.VertexAttribute.IDENTIFIER.get(graph);
        final int typeAttribute = AnalyticConcept.VertexAttribute.TYPE.get(graph);
        final int latitudeAttribute = SpatialConcept.VertexAttribute.LATITUDE.get(graph);
        final int longitudeAttribute = SpatialConcept.VertexAttribute.LONGITUDE.get(graph);
        final int shapeAttribute = SpatialConcept.VertexAttribute.SHAPE.get(graph);

        // map the distances between every pair of vertices with valid locations
        final Map<Integer, Map<Integer, Double>> distanceMap = calculateDistances(graph, null, selectedOnly);

        // assign vertices to clusters based on their distance to other vertices
        final Map<Integer, Set<Integer>> clusters = assignClusters(distanceMap, threshold);

        // calculate the centroid of each cluster
        final Map<Integer, Tuple<Float, Float>> centroids = calculateCentroids(graph, clusters);

        // merge clusters if their centroids are within the threshold
        final Map<Integer, Set<Integer>> mergedClusters = mergeClusters(clusters, centroids, threshold);

        // calculate the centroid of each merged cluster
        final Map<Integer, Tuple<Float, Float>> mergedCentroids = calculateCentroids(graph, mergedClusters);

        // add vertices to the graph representing the clusters and assign their children to be merged
        for (int clusterIndex : mergedClusters.keySet()) {
            final String clusterId = String.format("Geospatial Cluster #%d", clusterIndex);
            final Set<Integer> cluster = mergedClusters.get(clusterIndex);

            if (cluster.size() > 1) {
                final int clusterNode = graph.addVertex();
                graph.setStringValue(identifierAttribute, clusterNode, clusterId);
                graph.setObjectValue(typeAttribute, clusterNode, AnalyticConcept.VertexType.LOCATION);
                graph.setFloatValue(latitudeAttribute, clusterNode, mergedCentroids.get(clusterIndex).getFirst());
                graph.setFloatValue(longitudeAttribute, clusterNode, mergedCentroids.get(clusterIndex).getSecond());

                final List<Tuple<Double, Double>> clusterCoordinates = cluster.stream()
                        .map(vertexId -> Tuple.create(
                        (double) graph.getFloatValue(longitudeAttribute, vertexId),
                        (double) graph.getFloatValue(latitudeAttribute, vertexId)))
                        .collect(Collectors.toList());
                try {
                    graph.setStringValue(shapeAttribute, clusterNode, Shape.generateShape(clusterId, GeometryType.BOX, clusterCoordinates));
                } catch (IOException ex) {
                    throw new MergeException("Error creating shape for location cluster.", ex);
                }

                nodesToMerge.put(clusterNode, cluster);
            }
        }

        return nodesToMerge;
    }

    private Map<Integer, Map<Integer, Double>> calculateDistances(final GraphReadMethods graph, final Set<Integer> vertices, final boolean selectedOnly) {
        final Map<Integer, Map<Integer, Double>> distances = new HashMap<>();

        final int latitudeAttribute = SpatialConcept.VertexAttribute.LATITUDE.get(graph);
        final int longitudeAttribute = SpatialConcept.VertexAttribute.LONGITUDE.get(graph);
        final int selectedAttribute = VisualConcept.VertexAttribute.SELECTED.get(graph);

        //if no vertices were provided, get all vertices from the graph
        final Set<Integer> vertexIds;
        if (vertices == null) {
            vertexIds = new HashSet<>();
            final int vertexCount = graph.getVertexCount();
            for (int vertexOnePosition = 0; vertexOnePosition < vertexCount; vertexOnePosition++) {
                final int vertexId = graph.getVertex(vertexOnePosition);
                vertexIds.add(vertexId);
            }
        } else {
            vertexIds = new HashSet<>(vertices);
        }

        // for each vertex one...
        for (int vertexOneId : vertexIds) {
            final Float vertexOneLatitude = graph.getObjectValue(latitudeAttribute, vertexOneId);
            final Float vertexOneLongitude = graph.getObjectValue(longitudeAttribute, vertexOneId);
            final boolean vertexOneSelected = graph.getBooleanValue(selectedAttribute, vertexOneId);
            // ...if vertex one has a location...
            if (vertexOneLatitude != null && vertexOneLongitude != null && (!selectedOnly || vertexOneSelected)) {
                Map<Integer, Double> vertexOneDistances = distances.get(vertexOneId);
                if (vertexOneDistances == null) {
                    vertexOneDistances = new HashMap<>();
                    distances.put(vertexOneId, vertexOneDistances);
                }
                // ...for each vertex two...
                for (int vertexTwoId : vertexIds) {
                    if (vertexTwoId == vertexOneId) {
                        continue;
                    }
                    final Float vertexTwoLatitude = graph.getObjectValue(latitudeAttribute, vertexTwoId);
                    final Float vertexTwoLongitude = graph.getObjectValue(longitudeAttribute, vertexTwoId);
                    final boolean vertexTwoSelected = graph.getBooleanValue(selectedAttribute, vertexTwoId);
                    // ...if vertex two has a location...
                    if (vertexTwoLatitude != null && vertexTwoLongitude != null && (!selectedOnly || vertexTwoSelected)) {
                        // ...calculate the distance between vertex one and vertex two and store it in the distances map
                        final double distance = Distance.Haversine.estimateDistanceInKilometers(vertexOneLatitude, vertexOneLongitude, vertexTwoLatitude, vertexTwoLongitude);
                        vertexOneDistances.put(vertexTwoId, distance);
                    }
                }
            }
        }

        return distances;
    }

    private Map<Integer, Set<Integer>> assignClusters(final Map<Integer, Map<Integer, Double>> distanceMap, final int threshold) {
        final Map<Integer, Set<Integer>> clusters = new HashMap<>();

        // for each vertex one with a location...
        int clusterIndex = 0;
        final Set<Integer> distanceKeys = new HashSet<>(distanceMap.keySet());
        for (Integer vertexOneId : distanceKeys) {
            // ...copy the map of distances between vertex one and all vertex two's...
            if (distanceMap.containsKey(vertexOneId)) {
                final Map<Integer, Double> vertexOneToVertexTwoDistances = new HashMap(distanceMap.get(vertexOneId));
                if (vertexOneToVertexTwoDistances.size() > 0) {
                    // ...then for each vertex two...
                    final Set<Integer> cluster = new HashSet<>();
                    cluster.add(vertexOneId);
                    for (int vertexTwoId : vertexOneToVertexTwoDistances.keySet()) {
                        final double vertexOneToVertexTwoDistance = vertexOneToVertexTwoDistances.get(vertexTwoId);
                        // ...if vertex two's distance to vertex one meets the threshold requirement...
                        if (vertexOneToVertexTwoDistance <= (threshold / 1000)) {
                            // ...check that vertex two doesn't have a closer vertex than vertex one...
                            if (distanceMap.containsKey(vertexTwoId)) {
                                boolean vertexTwoHasCloserVertex = false;
                                final Map<Integer, Double> vertexTwoToOtherDistances = new HashMap(distanceMap.get(vertexTwoId));
                                if (vertexTwoToOtherDistances.size() > 0) {
                                    for (int otherId : vertexTwoToOtherDistances.keySet()) {
                                        final double vertexTwoToOtherDistance = vertexTwoToOtherDistances.get(otherId);
                                        if (vertexTwoToOtherDistance < vertexOneToVertexTwoDistance) {
                                            vertexTwoHasCloserVertex = true;
                                            break;
                                        }
                                    }
                                    if (vertexTwoHasCloserVertex) {
                                        // ...if it does, then remove vertex one from vertex two's distance map as it should merge into another local group
                                        distanceMap.get(vertexOneId).remove(vertexTwoId);
                                        distanceMap.get(vertexTwoId).remove(vertexOneId);
                                    } else {
                                        // ...if it does not, then remove vertex two's distances map altogether and register vertex two to the current local group
                                        distanceMap.remove(vertexTwoId);
                                        cluster.add(vertexTwoId);
                                    }
                                }
                            }
                        }
                    }

                    // ...and store the calculated cluster in the cluster map
                    clusters.put(++clusterIndex, cluster);
                }
            }
        }

        return clusters;
    }

    private Map<Integer, Tuple<Float, Float>> calculateCentroids(final GraphReadMethods graph, final Map<Integer, Set<Integer>> clusters) {
        final Map<Integer, Tuple<Float, Float>> centroids = new HashMap<>();

        final int latitudeAttribute = SpatialConcept.VertexAttribute.LATITUDE.get(graph);
        final int longitudeAttribute = SpatialConcept.VertexAttribute.LONGITUDE.get(graph);

        // for each cluster...
        for (int clusterIndex : clusters.keySet()) {
            final Set<Integer> cluster = clusters.get(clusterIndex);
            // ... calculate the average location for that cluster...
            final float centroidLatitude = cluster.stream().map(memberId -> graph.getFloatValue(latitudeAttribute, memberId)).reduce((lat1, lat2) -> lat1 + lat2).get() / (float) cluster.size();
            final float centroidLongitude = cluster.stream().map(memberId -> graph.getFloatValue(longitudeAttribute, memberId)).reduce((lon1, lon2) -> lon1 + lon2).get() / (float) cluster.size();
            // ...and store that location in the centroids map
            centroids.put(clusterIndex, Tuple.create(centroidLatitude, centroidLongitude));
        }

        return centroids;
    }

    private Map<Integer, Set<Integer>> mergeClusters(final Map<Integer, Set<Integer>> clusters, final Map<Integer, Tuple<Float, Float>> centroids, final int threshold) {
        Map<Integer, Set<Integer>> previousClusters = new HashMap<>();
        Map<Integer, Set<Integer>> mergedClusters = new HashMap<>(clusters);

        // while clusters are still merging...
        while (!previousClusters.equals(mergedClusters)) {
            previousClusters = new HashMap<>(mergedClusters);
            mergedClusters.clear();

            // ...calculate the distances between each cluster centroid...
            final Map<Integer, Map<Integer, Double>> centroidDistanceMap = new HashMap<>();
            for (int currentIndex : previousClusters.keySet()) {
                final Map<Integer, Double> currentCentroidDistances = new HashMap<>();
                final Tuple<Float, Float> currentCentroid = centroids.get(currentIndex);
                for (int otherIndex : previousClusters.keySet()) {
                    if (otherIndex == currentIndex) {
                        continue;
                    }
                    final Tuple<Float, Float> otherCentroid = centroids.get(otherIndex);
                    final double distanceBetweenClusters = Distance.Haversine.estimateDistanceInKilometers(currentCentroid.getFirst(), currentCentroid.getSecond(), otherCentroid.getFirst(), otherCentroid.getSecond());
                    currentCentroidDistances.put(otherIndex, distanceBetweenClusters);
                }
                centroidDistanceMap.put(currentIndex, currentCentroidDistances);
            }

            // ...then assign clusters for those centroids...
            final Map<Integer, Set<Integer>> reassignedClusters = assignClusters(centroidDistanceMap, threshold);
            if (!reassignedClusters.isEmpty()) {
                // ...if there are assigned clusters, then merge all the vertices of each corresponding centroid cluster
                for (int clusterIndex : reassignedClusters.keySet()) {
                    final Set<Integer> mergedClusterIndices = reassignedClusters.get(clusterIndex);
                    if (mergedClusterIndices.size() > 1) {
                        final Set<Integer> mergedCluster = new HashSet<>();
                        for (int mergedClusterIndex : mergedClusterIndices) {
                            mergedCluster.addAll(previousClusters.get(mergedClusterIndex));
                        }
                        mergedClusters.put(clusterIndex, mergedCluster);
                    } else {
                        mergedClusters.put(clusterIndex, previousClusters.get(new ArrayList<>(mergedClusterIndices).get(0)));
                    }
                }
            } else {
                // ...otherwise we are done merging
                mergedClusters = previousClusters;
                break;
            }
        }

        return mergedClusters;
    }
}
