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

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 */
public class DefaultsPreferencesView extends JDialog {
    
    protected JRadioButton updateAll;
    protected JRadioButton updateEmpty;
    protected JButton updateButton;
    protected JButton cancelButton;
    protected JPanel buttonPanel;

    public DefaultsPreferencesView(String allControl, String uninitializedControl) {
        initView(allControl, uninitializedControl);
    }

    protected void initView(String allControl, String uninitializedControl) {
        updateAll = new JRadioButton(allControl);
        updateAll.setSelected(true);

        updateEmpty = new JRadioButton(uninitializedControl);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(updateAll);
        buttonGroup.add(updateEmpty);

        updateButton = new JButton("Update");
        cancelButton = new JButton("Cancel");

        getRootPane().setDefaultButton(updateButton);
        // assemble
        configureButtonPanel();
        setLayout(new BorderLayout());
        add(getPanel(), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void configureButtonPanel() {
        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        this.buttonPanel.add(cancelButton);
        this.buttonPanel.add(updateButton);
    }

    private JPanel getPanel() {
        return FormBuilder.create()
                .columns("left:max(180dlu;pref)")
                .rows("p, 3dlu, p, 3dlu")
                .add(updateAll).xy(1, 1)
                .add(updateEmpty).xy(1, 3)
                .padding(Paddings.DIALOG)
                .build();
    }

    public JRadioButton getUpdateAll() {
        return updateAll;
    }

    public JRadioButton getUpdateEmpty() {
        return updateEmpty;
    }

    public JButton getUpdateButton() {
        return updateButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }
}
