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

package org.apache.cayenne.modeler.dialog.db.merge;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 *
 */
public class MergerTokenSelectorView extends JPanel {

    protected JTable tokens;
    protected JCheckBox checkAll;
    protected JLabel checkAllLabel;
    protected JButton reverseAll;

    public MergerTokenSelectorView() {

        this.checkAll = new JCheckBox();
        this.checkAllLabel = new JLabel("Check All Operations");
        this.reverseAll = new JButton("Reverse All Operations");

        checkAll.addItemListener(event -> {
            if (checkAll.isSelected()) {
                checkAllLabel.setText("Uncheck All Operations");
            } else {
                checkAllLabel.setText("Check All Operations");
            }
        });

        // assemble
        JPanel checkAllPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        checkAllPanel.add(checkAll);
        checkAllPanel.add(checkAllLabel);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(checkAllPanel, BorderLayout.WEST);
        topPanel.add(ButtonBarBuilder.create().build(), BorderLayout.EAST);

        tokens = new JTable();
        tokens.setRowHeight(25);
        tokens.setRowMargin(3);

        //TODO check on GUI
        JPanel panel = FormBuilder.create()
                .columns("fill:min(50dlu;pref):grow")
                .rows("p, 3dlu, fill:40dlu:grow")
                .addSeparator("Select Operations").xy(1, 1)
                .add(new JScrollPane(
                        tokens,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER)).xy(1, 3)
                .padding(Paddings.DIALOG)
                .build();

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }

    public JTable getTokens() {
        return tokens;
    }

    public JCheckBox getCheckAll() {
        return checkAll;
    }

    public JButton getReverseAll() {
        return reverseAll;
    }
}
