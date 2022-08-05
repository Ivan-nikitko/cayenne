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

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;

/**
 * A generic panel for entering DataSource information.
 */
public class DBConnectionInfoEditorView extends JPanel {

    protected JComboBox adapters;
    protected JTextField driver;
    protected JTextField url;
    protected JTextField userName;
    protected JPasswordField password;

    protected Collection<JLabel> labels;

    public DBConnectionInfoEditorView() {
        adapters = new JComboBox();
        adapters.setEditable(true);

        driver = new JTextField();
        url = new JTextField();
        userName = new JTextField();
        password = new JPasswordField();
        labels = new ArrayList<>();

        JPanel panel = FormBuilder.create()
                .columns("right:pref, 3dlu, fill:160dlu:grow")
                .rows("5*(p, 3dlu)")
                .add("JDBC Driver:").xy(1, 1)
                .add(driver).xy(3, 1)
                .add("DB URL:").xy(1, 3)
                .add(url).xy(3, 3)
                .add("User Name:").xy(1, 5)
                .add(userName).xy(3, 5)
                .add("Password:").xy(1, 7)
                .add(password).xy(3, 7)
                .add("Adapter (optional):").xy(1, 9)
                .add(adapters).xy(3, 9)
                .padding(Paddings.DIALOG)
                .build();

        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
    }

    public JComboBox getAdapters() {
        return adapters;
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

    public void setEnabled(boolean enabled) {
        adapters.setEnabled(enabled);
        driver.setEnabled(enabled);
        url.setEnabled(enabled);
        userName.setEnabled(enabled);
        password.setEnabled(enabled);

    }
}
