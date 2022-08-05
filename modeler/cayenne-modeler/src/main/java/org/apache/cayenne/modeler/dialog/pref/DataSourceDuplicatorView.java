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

package org.apache.cayenne.modeler.dialog.pref;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 */
public class DataSourceDuplicatorView extends JDialog {

    protected JTextField dataSourceName;
    protected JButton okButton;
    protected JButton cancelButton;

    public DataSourceDuplicatorView(String title) {
        setTitle(title);

        this.dataSourceName = new JTextField();
        this.okButton = new JButton("Create");
        this.cancelButton = new JButton("Cancel");

        getRootPane().setDefaultButton(okButton);

        // assemble

        JPanel panel = FormBuilder.create()
                .columns("right:pref, 3dlu, fill:250")
                .rows("p, 3dlu")
                .add("Name:").xy(1, 1)
                .add(dataSourceName).xy(3, 1)
                .padding(Paddings.DIALOG)
                .build();

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelButton);
        buttons.add(okButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JTextField getDataSourceName() {
        return dataSourceName;
    }

    public JButton getOkButton() {
        return okButton;
    }
}
