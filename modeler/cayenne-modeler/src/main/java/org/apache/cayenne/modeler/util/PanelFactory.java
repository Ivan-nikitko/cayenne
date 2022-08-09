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


package org.apache.cayenne.modeler.util;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** 
 * Implements a set of utility methods for laying out components on the panels.
 * 
 */

// TODO: get rid of PanelFactory in favor of JGoodies Forms
public class PanelFactory {

    /** 
     * Creates and returns a panel with right-centered buttons.
     */
    public static JPanel createButtonPanel(JButton[] buttons) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(3, 20, 3, 7));
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        for (JButton button : buttons) {
            panel.add(button);
        }

        return panel;
    }

    /** 
     * Creates panel with table within scroll panel and buttons in the bottom.
     * Also sets the resizing and selection policies of the table to
     * AUTO_RESIZE_OFF and SINGLE_SELECTION respectively.
     */
    public static JPanel createTablePanel(final JTable table, JButton[] buttons) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 5));
        // Create table with two columns and no rows.
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(table.isEditing()) {
                    table.getCellEditor().stopCellEditing();
                }
            }
        });

        // Panel to add space between table and EAST/WEST borders
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add Add and Remove buttons
        if (buttons != null) {
            panel.add(createButtonPanel(buttons), BorderLayout.SOUTH);
        }
        return panel;
    }

}
