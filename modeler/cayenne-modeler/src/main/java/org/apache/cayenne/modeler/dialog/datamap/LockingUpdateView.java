/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.modeler.dialog.datamap;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;

public class LockingUpdateView extends JDialog {

    protected JCheckBox entities;
    protected JCheckBox attributes;
    protected JCheckBox relationships;

    protected JButton cancelButton;
    protected JButton updateButton;

    public LockingUpdateView() {

        this.entities = new JCheckBox("Update all Entities");
        this.attributes = new JCheckBox("Update all Attributes");
        this.relationships = new JCheckBox("Update all Relationships");

        this.cancelButton = new JButton("Cancel");
        this.updateButton = new JButton("Update");

        // check all by default until we start storing this in preferences.
        entities.setSelected(true);
        attributes.setSelected(true);
        relationships.setSelected(true);

        // do layout...
        JPanel panel = FormBuilder.create()
                .columns("left:120dlu,3dlu")
                .rows("3*(p,3dlu)")
                .add(entities).xy(1, 1)
                .add(attributes).xy(1, 3)
                .add(relationships).xy(1, 5)
                .padding(Paddings.DIALOG)
                .build();

        getRootPane().setDefaultButton(updateButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(updateButton);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getUpdateButton() {
        return updateButton;
    }

    public JCheckBox getAttributes() {
        return attributes;
    }

    public JCheckBox getEntities() {
        return entities;
    }

    public JCheckBox getRelationships() {
        return relationships;
    }
}
