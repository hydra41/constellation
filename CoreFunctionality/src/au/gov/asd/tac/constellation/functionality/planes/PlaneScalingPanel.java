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
package au.gov.asd.tac.constellation.functionality.planes;

import au.gov.asd.tac.constellation.schema.visualschema.attribute.objects.Plane;

/**
 *
 * @author algol
 */
public class PlaneScalingPanel extends javax.swing.JPanel {

    private final Plane plane;
    private final float originalScale;

    public PlaneScalingPanel(final Plane plane) {
        initComponents();

        this.plane = plane;

        // Since we scale images in proportion, it doesn't matter if we use width or height.
        originalScale = plane.getWidth() / plane.getImageWidth();
        scaleText.setValue(originalScale);
    }

    public float getScale() {
        return Float.parseFloat(scaleText.getText());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        scaleText = new javax.swing.JFormattedTextField();
        iconSizeButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        originalSizeButton = new javax.swing.JButton();
        timesBigButton = new javax.swing.JButton();
        timesSmallButton = new javax.swing.JButton();
        divideSmallButton = new javax.swing.JButton();
        divideBigButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(PlaneScalingPanel.class, "PlaneScalingPanel.jLabel1.text")); // NOI18N

        scaleText.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#.####"))));
        scaleText.setText(org.openide.util.NbBundle.getMessage(PlaneScalingPanel.class, "PlaneScalingPanel.scaleText.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(iconSizeButton, org.openide.util.NbBundle.getMessage(PlaneScalingPanel.class, "PlaneScalingPanel.iconSizeButton.text")); // NOI18N
        iconSizeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iconSizeButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(resetButton, org.openide.util.NbBundle.getMessage(PlaneScalingPanel.class, "PlaneScalingPanel.resetButton.text")); // NOI18N
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(originalSizeButton, org.openide.util.NbBundle.getMessage(PlaneScalingPanel.class, "PlaneScalingPanel.originalSizeButton.text")); // NOI18N
        originalSizeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                originalSizeButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(timesBigButton, org.openide.util.NbBundle.getMessage(PlaneScalingPanel.class, "PlaneScalingPanel.timesBigButton.text")); // NOI18N
        timesBigButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timesBigButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(timesSmallButton, org.openide.util.NbBundle.getMessage(PlaneScalingPanel.class, "PlaneScalingPanel.timesSmallButton.text")); // NOI18N
        timesSmallButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timesSmallButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(divideSmallButton, org.openide.util.NbBundle.getMessage(PlaneScalingPanel.class, "PlaneScalingPanel.divideSmallButton.text")); // NOI18N
        divideSmallButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                divideSmallButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(divideBigButton, org.openide.util.NbBundle.getMessage(PlaneScalingPanel.class, "PlaneScalingPanel.divideBigButton.text")); // NOI18N
        divideBigButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                divideBigButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(timesBigButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(iconSizeButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(timesSmallButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                        .addComponent(originalSizeButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(divideBigButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(resetButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(divideSmallButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scaleText)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(scaleText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iconSizeButton)
                    .addComponent(timesBigButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(originalSizeButton)
                    .addComponent(timesSmallButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(divideSmallButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(divideBigButton)
                    .addComponent(resetButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_resetButtonActionPerformed
    {//GEN-HEADEREND:event_resetButtonActionPerformed
        scaleText.setValue(originalScale);
    }//GEN-LAST:event_resetButtonActionPerformed

    private void iconSizeButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_iconSizeButtonActionPerformed
    {//GEN-HEADEREND:event_iconSizeButtonActionPerformed
        // Icon size is diameter 2.
        final float scale = 2f / plane.getImageWidth();
        scaleText.setValue(scale);
    }//GEN-LAST:event_iconSizeButtonActionPerformed

    private void originalSizeButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_originalSizeButtonActionPerformed
    {//GEN-HEADEREND:event_originalSizeButtonActionPerformed
        scaleText.setValue(1f);
    }//GEN-LAST:event_originalSizeButtonActionPerformed

    private void timesBigButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_timesBigButtonActionPerformed
    {//GEN-HEADEREND:event_timesBigButtonActionPerformed
        scaleText.setValue(getScale() * 2f);
    }//GEN-LAST:event_timesBigButtonActionPerformed

    private void timesSmallButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_timesSmallButtonActionPerformed
    {//GEN-HEADEREND:event_timesSmallButtonActionPerformed
        scaleText.setValue(getScale() * 1.1f);
    }//GEN-LAST:event_timesSmallButtonActionPerformed

    private void divideSmallButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_divideSmallButtonActionPerformed
    {//GEN-HEADEREND:event_divideSmallButtonActionPerformed
        scaleText.setValue(getScale() / 1.1f);
    }//GEN-LAST:event_divideSmallButtonActionPerformed

    private void divideBigButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_divideBigButtonActionPerformed
    {//GEN-HEADEREND:event_divideBigButtonActionPerformed
        scaleText.setValue(getScale() / 2f);
    }//GEN-LAST:event_divideBigButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton divideBigButton;
    private javax.swing.JButton divideSmallButton;
    private javax.swing.JButton iconSizeButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton originalSizeButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JFormattedTextField scaleText;
    private javax.swing.JButton timesBigButton;
    private javax.swing.JButton timesSmallButton;
    // End of variables declaration//GEN-END:variables
}
