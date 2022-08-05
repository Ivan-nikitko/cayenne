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

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;
import org.apache.cayenne.modeler.util.JTextFieldUndoable;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class CustomDataSourceView extends JPanel {

    protected JTextField factoryName;
    protected JTextField locationHint;

    public CustomDataSourceView() {

        factoryName = new JTextFieldUndoable();
        locationHint = new JTextFieldUndoable();

        // assemble

        JPanel panel = FormBuilder.create()
                .columns("right:[80dlu,pref], 3dlu, fill:223dlu")
                .rows("3*(p, 3dlu)")
                .addSeparator("Custom Data Source Factory").xyw(1,  1, 3)
                .add("Factory Class:").xy(1, 3)
                .add(factoryName).xy(3, 3)
                .add("Location Hint (optional):").xy(1, 5)
                .add(locationHint).xy(3, 5)
                .padding(Paddings.DIALOG)
                .build();

        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
    }

    public JTextField getLocationHint() {
        return locationHint;
    }

    public JTextField getFactoryName() {
        return factoryName;
    }
}
