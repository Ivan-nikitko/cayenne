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

package org.apache.cayenne.modeler.editor.dbimport;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.Vector;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;
import org.apache.cayenne.dbsync.reverse.dbimport.ReverseEngineering;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.util.NameGeneratorPreferences;
import org.apache.cayenne.modeler.util.TextAdapter;
import org.apache.cayenne.modeler.util.combo.AutoCompletion;
import org.apache.cayenne.validation.ValidationException;

/**
 * @since 4.1
 */
public class ReverseEngineeringConfigPanel extends JPanel {

    private static final String DATA_FIELDS_LAYOUT = "right:pref, 3dlu, fill:235dlu";

    private JComboBox<String> strategyCombo;
    private TextAdapter meaningfulPk;
    private TextAdapter stripFromTableNames;
    private JCheckBox skipRelationshipsLoading;
    private JCheckBox skipPrimaryKeyLoading;
    private JCheckBox forceDataMapCatalog;
    private JCheckBox forceDataMapSchema;
    private JCheckBox usePrimitives;
    private JCheckBox useJava7Types;

    private TextAdapter tableTypes;

    private ProjectController projectController;

    private DbImportView dbImportView;

    ReverseEngineeringConfigPanel(ProjectController projectController, DbImportView dbImportView) {
        this.projectController = projectController;
        this.dbImportView = dbImportView;
        initFormElements();
        initListeners();
        buildView();
    }

    private void buildView() {
        add(getPanel());
    }

    private JPanel getPanel() {
        return FormBuilder.create()
                .columns(DATA_FIELDS_LAYOUT)
                .rows("10*(p, 3dlu)")
                .add("Tables with Meaningful PK Pattern:").xy(1, 1)
                .add(meaningfulPk.getComponent()).xy(3, 1)
                .add("Strip from table names:").xy(1, 3)
                .add(stripFromTableNames.getComponent()).xy(3, 3)
                .add("Skip relationships loading:").xy(1, 5)
                .add(skipRelationshipsLoading).xy(3, 5)
                .add("Skip primary key loading:").xy(1, 7)
                .add(skipPrimaryKeyLoading).xy(3, 7)
                .add("Force datamap catalog:").xy(1, 9)
                .add(forceDataMapCatalog).xy(3, 9)
                .add("Force datamap schema:").xy(1, 11)
                .add(forceDataMapSchema).xy(3, 11)
                .add("Use Java primitive types:").xy(1,13)
                .add(usePrimitives).xy(3,13)
                .add("Use java.util.Date type:").xy(1,15)
                .add(useJava7Types).xy(3,15)
                .add("Naming strategy:").xy(1,17)
                .add(strategyCombo).xy(3,17)
                .add("Table types:").xy(1,19)
                .add(tableTypes.getComponent()).xy(3,19)
                .padding(Paddings.DIALOG)
                .build();
    }


    void fillCheckboxes(ReverseEngineering reverseEngineering) {
        skipRelationshipsLoading.setSelected(reverseEngineering.getSkipRelationshipsLoading());
        skipPrimaryKeyLoading.setSelected(reverseEngineering.getSkipPrimaryKeyLoading());
        forceDataMapCatalog.setSelected(reverseEngineering.isForceDataMapCatalog());
        forceDataMapSchema.setSelected(reverseEngineering.isForceDataMapSchema());
        usePrimitives.setSelected(reverseEngineering.isUsePrimitives());
        useJava7Types.setSelected(reverseEngineering.isUseJava7Types());
    }

    void initializeTextFields(ReverseEngineering reverseEngineering) {
        meaningfulPk.setText(reverseEngineering.getMeaningfulPkTables());
        stripFromTableNames.setText(reverseEngineering.getStripFromTableNames());
    }

    ReverseEngineering getReverseEngineeringBySelectedMap() {
        DataMap dataMap = projectController.getCurrentDataMap();
        return projectController.getApplication().getMetaData().get(dataMap, ReverseEngineering.class);
    }

    void initStrategy(ReverseEngineering reverseEngineering) {
        Vector<String> arr = NameGeneratorPreferences
                .getInstance()
                .getLastUsedStrategies();
        strategyCombo.setModel(new DefaultComboBoxModel<>(arr));
        strategyCombo.setSelectedItem(reverseEngineering.getNamingStrategy());
    }

    private void initFormElements() {
        strategyCombo = Application.getWidgetFactory().createComboBox();
        AutoCompletion.enable(strategyCombo, false, true);
        strategyCombo.setToolTipText("Naming strategy to use");

        JTextField meaningfulPkField = new JTextField();
        meaningfulPkField.setToolTipText("<html>Regular expression to filter tables with meaningful primary keys.<br>" +
                "Multiple expressions divided by comma can be used.<br>" +
                "Example: <b>^table1|^table2|^prefix.*|table_name</b></html>");
        meaningfulPk = new TextAdapter(meaningfulPkField) {
            protected void updateModel(String text) {
                getReverseEngineeringBySelectedMap().setMeaningfulPkTables(text);
                if (!dbImportView.isInitFromModel()) {
                    projectController.setDirty(true);
                }
            }
        };

        JTextField stripFromTableNamesField = new JTextField();
        stripFromTableNamesField.setToolTipText("<html>Regex that matches the part of the table name that needs to be stripped off " +
                "when generating ObjEntity name</html>");
        stripFromTableNames = new TextAdapter(stripFromTableNamesField) {
            protected void updateModel(String text) {
                getReverseEngineeringBySelectedMap().setStripFromTableNames(text);
                if (!dbImportView.isInitFromModel()) {
                    projectController.setDirty(true);
                }
            }
        };

        JTextField tableTypesField = new JTextField();
        tableTypesField.setToolTipText("<html>Default types to import is TABLE and VIEW.");
        tableTypes = new TextAdapter(tableTypesField) {
            @Override
            protected void updateModel(String text) throws ValidationException {
                ReverseEngineering reverseEngineering = getReverseEngineeringBySelectedMap();
                if (text == null || text.isEmpty()) {
                    String[] tableTypesFromReverseEngineering = reverseEngineering.getTableTypes();
                    tableTypes.setText(String.join(",", tableTypesFromReverseEngineering));
                    JOptionPane.showMessageDialog(
                            Application.getFrame(),
                            "Table types field can't be empty.",
                            "Error setting table types",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    reverseEngineering.getTableTypesCollection().clear();
                    String[] types = text.split("\\s*,\\s*");
                    for (String type : types) {
                        if (!type.isEmpty()) {
                            reverseEngineering.addTableType(type.trim());
                        }
                    }
                    if (!dbImportView.isInitFromModel()) {
                        projectController.setDirty(true);
                    }
                }
            }
        };

        skipRelationshipsLoading = new JCheckBox();
        skipRelationshipsLoading.setToolTipText("<html>Whether to load relationships.</html>");
        skipPrimaryKeyLoading = new JCheckBox();
        skipPrimaryKeyLoading.setToolTipText("<html>Whether to load primary keys.</html>");
        forceDataMapCatalog = new JCheckBox();
        forceDataMapCatalog.setToolTipText("<html>Automatically tagging each DbEntity with the actual DB catalog/schema" +
                "(default behavior) may sometimes be undesirable.<br>  If this is the case then setting <b>forceDataMapCatalog</b> " +
                "to <b>true</b> will set DbEntity catalog to one in the DataMap.</html>");
        forceDataMapSchema = new JCheckBox();
        forceDataMapSchema.setToolTipText("<html>Automatically tagging each DbEntity with the actual DB catalog/schema " +
                "(default behavior) may sometimes be undesirable.<br> If this is the case then setting <b>forceDataMapSchema</b> " +
                "to <b>true</b> will set DbEntity schema to one in the DataMap.</html>");
        useJava7Types = new JCheckBox();
        useJava7Types.setToolTipText("<html>Use <b>java.util.Date</b> for all columns with <i>DATE/TIME/TIMESTAMP</i> types.<br>" +
                "By default <b>java.time.*</b> types will be used.</html>");
        usePrimitives = new JCheckBox();
        usePrimitives.setToolTipText("<html>Use primitive types (e.g. int) or Object types (e.g. java.lang.Integer)</html>");
    }

    private void initListeners() {
        skipRelationshipsLoading.addActionListener(e -> {
            getReverseEngineeringBySelectedMap().setSkipRelationshipsLoading(skipRelationshipsLoading.isSelected());
            if (!dbImportView.isInitFromModel()) {
                projectController.setDirty(true);
            }
        });
        skipPrimaryKeyLoading.addActionListener(e -> {
            getReverseEngineeringBySelectedMap().setSkipPrimaryKeyLoading(skipPrimaryKeyLoading.isSelected());
            if (!dbImportView.isInitFromModel()) {
                projectController.setDirty(true);
            }
        });
        forceDataMapCatalog.addActionListener(e -> {
            getReverseEngineeringBySelectedMap().setForceDataMapCatalog(forceDataMapCatalog.isSelected());
            if (!dbImportView.isInitFromModel()) {
                projectController.setDirty(true);
            }
        });
        forceDataMapSchema.addActionListener(e -> {
            getReverseEngineeringBySelectedMap().setForceDataMapSchema(forceDataMapSchema.isSelected());
            if (!dbImportView.isInitFromModel()) {
                projectController.setDirty(true);
            }
        });
        usePrimitives.addActionListener(e -> {
            getReverseEngineeringBySelectedMap().setUsePrimitives(usePrimitives.isSelected());
            if (!dbImportView.isInitFromModel()) {
                projectController.setDirty(true);
            }
        });
        useJava7Types.addActionListener(e -> {
            getReverseEngineeringBySelectedMap().setUseJava7Types(useJava7Types.isSelected());
            if (!dbImportView.isInitFromModel()) {
                projectController.setDirty(true);
            }
        });
        strategyCombo.addActionListener(e -> {
            String strategy = (String) ReverseEngineeringConfigPanel.this.getStrategyCombo().getSelectedItem();
            checkStrategy(strategy);
            getReverseEngineeringBySelectedMap().setNamingStrategy(strategy);
            NameGeneratorPreferences.getInstance().addToLastUsedStrategies(strategy);
            if (!dbImportView.isInitFromModel()) {
                projectController.setDirty(true);
            }
        });
    }

    private void checkStrategy(String strategy) {
        try {
            Thread.currentThread().getContextClassLoader().loadClass(strategy);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    strategy + " not found. Please, add naming strategy to classpath.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    JComboBox<String> getStrategyCombo() {
        return strategyCombo;
    }

    TextAdapter getMeaningfulPk() {
        return meaningfulPk;
    }

    TextAdapter getStripFromTableNames() {
        return stripFromTableNames;
    }

    JCheckBox getSkipRelationshipsLoading() {
        return skipRelationshipsLoading;
    }

    JCheckBox getSkipPrimaryKeyLoading() {
        return skipPrimaryKeyLoading;
    }

    JCheckBox getForceDataMapCatalog() {
        return forceDataMapCatalog;
    }

    JCheckBox getForceDataMapSchema() {
        return forceDataMapSchema;
    }

    JCheckBox getUsePrimitives() {
        return usePrimitives;
    }

    JCheckBox getUseJava7Types() {
        return useJava7Types;
    }

    TextAdapter getTableTypes() {
        return tableTypes;
    }

}
