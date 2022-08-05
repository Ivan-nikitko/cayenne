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

package org.apache.cayenne.modeler.editor.datanode;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;
import org.apache.cayenne.modeler.util.JTextFieldUndoable;
import org.apache.cayenne.modeler.util.JTextFieldValidator;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;

public class JDBCDataSourceView extends JPanel {

    protected JTextField driver;
    protected JTextField url;
    protected JTextField userName;
    protected JPasswordField password;

    protected JTextField minConnections;
    protected JTextField maxConnections;
    protected JButton syncWithLocal;

    public JDBCDataSourceView() {

        driver = new JTextFieldUndoable();
        url = new JTextFieldUndoable();
        userName = new JTextFieldUndoable();
        password = new JPasswordField();
        minConnections = new JTextFieldUndoable(6);
        maxConnections = new JTextFieldUndoable(6);
        syncWithLocal = new JButton("Sync with Local");
        syncWithLocal.setToolTipText("Update from local DataSource");

        JTextFieldValidator.addValidation(driver, text -> text.length() != text.trim().length(),
                "There are some whitespaces in this field");
        JTextFieldValidator.addValidation(url, text -> text.length() != text.trim().length(),
                "There are some whitespaces in this field");

        // assemble

        JPanel panel = FormBuilder.create()
                .columns("right:80dlu, 3dlu, fill:50dlu, 3dlu, fill:74dlu, 3dlu, fill:93dlu")
                .rows("13*(p,3dlu)")
                .addSeparator("JDBC Configuration").xywh(1, 1, 7, 1)
                .add("JDBC Driver:").xy(1, 3)
                .add(driver).xyw(3, 3, 5)
                .add("DB URL:").xy(1, 5)
                .add(url).xyw(3, 5, 5)
                .add("Username:").xy(1, 7)
                .add(userName).xyw(3, 7, 5)
                .add("Password:").xy(1, 9)
                .add(password).xyw(3, 9, 5)
                .add("Min Connections:").xy(1, 11)
                .add(minConnections).xy(3, 11)
                .add("Max Connections:").xy(1, 13)
                .add(maxConnections).xy(3, 13)
                .add(syncWithLocal).xy(7, 15)
                .padding(Paddings.DIALOG)
                .build();

        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
    }

    public JTextField getDriver() {
        return driver;
    }

    public JPasswordField getPassword() {
        return password;
    }

    public JTextField getUrl() {
        return url;
    }

    public JTextField getUserName() {
        return userName;
    }

    public JTextField getMaxConnections() {
        return maxConnections;
    }

    public JTextField getMinConnections() {
        return minConnections;
    }

    public JButton getSyncWithLocal() {
        return syncWithLocal;
    }
}
