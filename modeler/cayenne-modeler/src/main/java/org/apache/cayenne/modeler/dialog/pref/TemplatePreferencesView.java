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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.cayenne.modeler.util.CayenneTable;

public class TemplatePreferencesView extends JPanel {

    protected JButton addButton;
    protected JButton removeButton;
    protected JTable table;

    public TemplatePreferencesView() {

        // create widgets
        addButton = new JButton("Add Template");
        removeButton = new JButton("Remove Template");

        table = new CayenneTable();
        table.setRowMargin(3);
        table.setRowHeight(25);

        // assemble
        setLayout(new BorderLayout());
        JScrollPane ScrollPane = new JScrollPane(table);
        ScrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(ScrollPane, BorderLayout.CENTER);
        add(getPanel(), BorderLayout.EAST);
    }

    private JPanel getPanel() {
        return FormBuilder.create()
                .columns("fill:min(150dlu;pref)")
                .rows("p, 3dlu, p")
                .add(addButton).xy(1, 1)
                .add(removeButton).xy(1, 3)
                .padding(Paddings.DIALOG)
                .build();
    }

    public JTable getTable() {
        return table;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getRemoveButton() {
        return removeButton;
    }
}
