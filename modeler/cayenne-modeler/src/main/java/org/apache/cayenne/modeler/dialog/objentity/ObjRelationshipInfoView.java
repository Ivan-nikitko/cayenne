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
import org.apache.cayenne.map.DeleteRule;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.util.DefaultWidgetFactory;
import org.apache.cayenne.modeler.util.MultiColumnBrowser;
import org.apache.cayenne.modeler.util.PanelFactory;
import org.apache.cayenne.modeler.util.WidgetFactory;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class ObjRelationshipInfoView extends JDialog {

    private static final Dimension BROWSER_CELL_DIM = new Dimension(130, 200);

    private static final String[] DELETE_RULES = new String[]{
            DeleteRule.deleteRuleName(DeleteRule.NO_ACTION),
            DeleteRule.deleteRuleName(DeleteRule.NULLIFY),
            DeleteRule.deleteRuleName(DeleteRule.CASCADE),
            DeleteRule.deleteRuleName(DeleteRule.DENY),
    };

    private MultiColumnBrowser pathBrowser;

    private Component collectionTypeLabel;
    private JComboBox<String> collectionTypeCombo;
    private Component mapKeysLabel;
    private JComboBox<String> mapKeysCombo;

    private JButton saveButton;
    private JButton cancelButton;
    private JButton newRelButton;

    private JTextField relationshipName;
    private JLabel semanticsLabel;
    private JLabel sourceEntityLabel;
    private JComboBox<String> targetCombo;

    private JComboBox<String> deleteRule;
    private JCheckBox usedForLocking;
    private JTextField comment;

    public ObjRelationshipInfoView() {
        super(Application.getFrame());

        WidgetFactory widgetFactory = new DefaultWidgetFactory();

        this.cancelButton = new JButton("Cancel");
        this.saveButton = new JButton("Done");
        this.newRelButton = new JButton("New DbRelationship");
        this.relationshipName = new JTextField(25);
        this.semanticsLabel = new JLabel();
        this.sourceEntityLabel = new JLabel();

        cancelButton.setEnabled(true);
        getRootPane().setDefaultButton(saveButton);
        saveButton.setEnabled(true);
        newRelButton.setEnabled(true);
        collectionTypeCombo = widgetFactory.createComboBox();
        collectionTypeCombo.setVisible(true);
        this.targetCombo = widgetFactory.createComboBox();
        targetCombo.setVisible(true);

        this.mapKeysCombo = widgetFactory.createComboBox();
        mapKeysCombo.setVisible(true);


        pathBrowser = new ObjRelationshipPathBrowser();
        pathBrowser.setPreferredColumnSize(BROWSER_CELL_DIM);
        pathBrowser.setDefaultRenderer();

        this.deleteRule = Application.getWidgetFactory().createComboBox(DELETE_RULES, false);
        this.usedForLocking = new JCheckBox();
        this.comment = new JTextField();

        setTitle("ObjRelationship Inspector");
        setLayout(new BorderLayout());

        collectionTypeLabel = new JLabel("Collection Type:");
        mapKeysLabel = new JLabel("Map Key:");

        JPanel panel = getPanel();
        add(panel, BorderLayout.CENTER);
        JButton[] buttons = {cancelButton, saveButton};
        add(PanelFactory.createButtonPanel(buttons), BorderLayout.SOUTH);
    }

    private JPanel getPanel() {
        JPanel buttonsPane = new JPanel(new FlowLayout(FlowLayout.LEADING));
        buttonsPane.add(newRelButton);
        return FormBuilder.create()
                .columns("right:max(50dlu;pref), 3dlu, fill:min(150dlu;pref), 3dlu, 300dlu, 3dlu, fill:min(120dlu;pref)")
                .rows("12*(p, 3dlu), top:14dlu, 3dlu, top:p:grow")
                .addSeparator("ObjRelationship Information").xyw(1, 1, 5)
                .add("Source Entity:").xy(1, 3)
                .add(sourceEntityLabel).xywh(3, 3, 1, 1)
                .add("Target Entity:").xy(1, 5)
                .add(targetCombo).xywh(3, 5, 1, 1)
                .add("Relationship Name:").xy(1, 7)
                .add(relationshipName).xywh(3, 7, 1, 1)
                .add("Semantics:").xy(1, 9)
                .add(semanticsLabel).xyw(3, 9, 5)
                .add(collectionTypeLabel).xy(1, 11)
                .add(collectionTypeCombo).xywh(3, 11, 1, 1)
                .add(mapKeysLabel).xy(1, 13)
                .add(mapKeysCombo).xywh(3, 13, 1, 1)
                .add("Delete rule:").xy(1, 15)
                .add(deleteRule).xywh(3, 15, 1, 1)
                .add("Used for locking:").xy(1, 17)
                .add(usedForLocking).xywh(3, 17, 1, 1)
                .add("Comment:").xy(1, 19)
                .add(comment).xywh(3, 19, 1, 1)
                .addSeparator("Mapping to DbRelationships").xyw(1, 21, 5)
                .add(buttonsPane).xywh(1, 23, 5, 1)
                .add(new JScrollPane(
                        pathBrowser,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED)).xywh(1, 25, 5, 3)
                .padding(Paddings.DIALOG)
                .build();
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getNewRelButton() {
        return newRelButton;
    }

    public JTextField getRelationshipName() {
        return relationshipName;
    }

    public JLabel getSemanticsLabel() {
        return semanticsLabel;
    }

    public JLabel getSourceEntityLabel() {
        return sourceEntityLabel;
    }

    public JComboBox<String> getTargetCombo() {
        return targetCombo;
    }

    public JComboBox<String> getCollectionTypeCombo() {
        return collectionTypeCombo;
    }

    public JComboBox<String> getMapKeysCombo() {
        return mapKeysCombo;
    }

    public JComboBox<String> getDeleteRule() {
        return deleteRule;
    }

    public JCheckBox getUsedForLocking() {
        return usedForLocking;
    }

    public JTextField getComment() {
        return comment;
    }

    public Component getMapKeysLabel() {
        return mapKeysLabel;
    }

    public Component getCollectionTypeLabel() {
        return collectionTypeLabel;
    }

    public MultiColumnBrowser getPathBrowser() {
        return pathBrowser;
    }
}
