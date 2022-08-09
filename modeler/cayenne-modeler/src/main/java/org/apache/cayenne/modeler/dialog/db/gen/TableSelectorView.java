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

package org.apache.cayenne.modeler.dialog.db.gen;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;
import org.apache.cayenne.modeler.util.CayenneTable;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 *
 */
public class TableSelectorView extends JPanel {

    protected JTable tables;
    protected JCheckBox checkAll;
    protected JLabel checkAllLabel;

    public TableSelectorView() {

        this.checkAll = new JCheckBox();
        this.checkAllLabel = new JLabel("Check All Tables");

        checkAll.addItemListener(event -> {
            if (checkAll.isSelected()) {
                checkAllLabel.setText("Uncheck All Tables");
            } else {
                checkAllLabel.setText("Check All Tables");
            }
        });

        // assemble
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        topPanel.add(checkAll);
        topPanel.add(checkAllLabel);

        tables = new CayenneTable();
        tables.setRowHeight(25);
        tables.setRowMargin(3);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(getPanel(), BorderLayout.CENTER);
    }

    private JPanel getPanel() {
        return FormBuilder.create()
                .columns("fill:min(50dlu;pref):grow")
                .rows("p, 3dlu, fill:40dlu:grow")
                .addSeparator("Select Tables").xy(1, 1)
                .add(new JScrollPane(
                        tables,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)).xy(1, 3)
                .padding(Paddings.TABBED_DIALOG)
                .build();
    }

    public JTable getTables() {
        return tables;
    }

    public JCheckBox getCheckAll() {
        return checkAll;
    }
}
