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

package org.apache.cayenne.modeler;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;
import org.apache.cayenne.modeler.pref.TableColumnPreferences;
import org.apache.cayenne.modeler.util.CayenneDialog;
import org.apache.cayenne.modeler.util.CayenneTable;
import org.apache.cayenne.modeler.util.PanelFactory;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 * @since 4.2
 */
public class DbRelationshipDialogView extends CayenneDialog {

    private JTextField name;
    private JComboBox<String> targetEntities;
    private JCheckBox toDepPk;
    private JCheckBox toMany;
    private JTextField comment;
    private JLabel sourceName;
    private JTextField reverseName;
    private CayenneTable table;
    private TableColumnPreferences tablePreferences;
    private JButton addButton;
    private JButton removeButton;
    private JButton saveButton;
    private JButton cancelButton;

    private boolean cancelPressed;

    public DbRelationshipDialogView() {
        super(Application.getFrame(), "Create dbRelationship", true);

        initView();
        this.pack();
        this.centerWindow();
    }

    private void initView() {
        name = new JTextField(25);
        targetEntities = new JComboBox<>();
        toDepPk = new JCheckBox();
        toMany = new JCheckBox();
        comment = new JTextField(25);

        sourceName = new JLabel();
        reverseName = new JTextField(25);

        addButton = new JButton("Add");

        removeButton = new JButton("Remove");

        saveButton = new JButton("Done");

        cancelButton = new JButton("Cancel");

        table = new AttributeTable();

        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePreferences = new TableColumnPreferences(getClass(), "dbentity/dbjoinTable");

        getRootPane().setDefaultButton(saveButton);

        getContentPane().setLayout(new BorderLayout());

        JPanel joinButtons = new JPanel(new FlowLayout(FlowLayout.LEADING));
        joinButtons.add(addButton);
        joinButtons.add(removeButton);

        JPanel panel = FormBuilder.create()
                .columns("right:max(50dlu;pref), 3dlu, fill:min(150dlu;pref), 3dlu, fill:min(50dlu;pref)")
                .rows("10*(p, 3dlu),top:14dlu, 3dlu, top:p:grow")
                .addSeparator("Create dbRelationship").xywh(1, 1, 5, 1)
                .add("Relationship Name:").xy(1, 3)
                .add(name).xy(3, 3)
                .addLabel("Source Entity:").xy(1, 5)
                .add(sourceName).xy(3, 5)
                .add("Target Entity:").xy(1, 7)
                .add(targetEntities).xy(3, 7)
                .add("To Dep PK:").xy(1, 9)
                .add(toDepPk).xy(3, 9)
                .add("To Many:").xy(1, 11)
                .add(toMany).xy(3, 11)
                .add("Comment:").xy(1, 13)
                .add(comment).xy(3, 13)
                .addSeparator("DbRelationship Information").xyw(1, 15, 5)
                .add("Reverse Relationship Name:").xy(1, 17)
                .add(reverseName).xy(3, 17)
                .addSeparator("Joins").xyw(1, 19, 5)
                .add(new JScrollPane(table)).xywh(1, 21, 3, 3, "fill, fill")
                .add(joinButtons).xywh(5, 21, 1, 3)
                .padding(Paddings.DIALOG)
                .build();

        getContentPane().add(panel, BorderLayout.CENTER);
        JButton[] buttons = {cancelButton, saveButton};
        getContentPane().add(PanelFactory.createButtonPanel(buttons), BorderLayout.SOUTH);
    }

    public void enableOptions(boolean enable) {
        saveButton.setEnabled(enable);
        reverseName.setEnabled(enable);
        addButton.setEnabled(enable);
        removeButton.setEnabled(enable);
    }

    @Override
    public void setVisible(boolean b) {
        if (b && cancelPressed) {
            return;
        }
        super.setVisible(b);
    }

    public JTextField getNameField() {
        return name;
    }

    public JComboBox<String> getTargetEntities() {
        return targetEntities;
    }

    public JCheckBox getToDepPk() {
        return toDepPk;
    }

    public JCheckBox getToMany() {
        return toMany;
    }

    public JTextField getComment() {
        return comment;
    }

    public JLabel getSourceName() {
        return sourceName;
    }

    public JTextField getReverseName() {
        return reverseName;
    }

    public CayenneTable getTable() {
        return table;
    }

    public TableColumnPreferences getTablePreferences() {
        return tablePreferences;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getRemoveButton() {
        return removeButton;
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public boolean isCancelPressed() {
        return cancelPressed;
    }

    public void setCancelPressed(boolean cancelPressed) {
        this.cancelPressed = cancelPressed;
    }

    final class AttributeTable extends CayenneTable {

        final Dimension preferredSize = new Dimension(203, 100);

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return preferredSize;
        }
    }

}
