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

package org.apache.cayenne.modeler.dialog.objentity;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;

public class EntitySyncDialog extends JDialog {

    protected JCheckBox removeFKs;

    protected JButton updateButton;
    protected JButton cancelButton;

    public EntitySyncDialog() {
        removeFKs = new JCheckBox();
        removeFKs.setSelected(true);

        updateButton = new JButton("Continue");
        cancelButton = new JButton("Cancel");

        getRootPane().setDefaultButton(updateButton);

        // assemble

        //TODO Check on GUI
        JPanel panel = FormBuilder.create()
                .columns("pref, 3dlu, pref")
                .rows("2*(p, 3dlu)")
                .add(removeFKs).xy(1, 1)
                .add("Remove Object Attributes mapped on Foreign Keys?").xy(3,1)
                .padding(Paddings.DIALOG)
                .build();

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelButton);
        buttons.add(updateButton);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panel, BorderLayout.CENTER);
        contentPane.add(buttons, BorderLayout.SOUTH);

        setTitle("Synchronize ObjEntity with DbEntity");
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JCheckBox getRemoveFKs() {
        return removeFKs;
    }

    public JButton getUpdateButton() {
        return updateButton;
    }
}
