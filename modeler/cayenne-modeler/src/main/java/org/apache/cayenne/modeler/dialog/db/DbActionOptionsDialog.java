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

package org.apache.cayenne.modeler.dialog.db;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;
import org.apache.cayenne.modeler.util.CayenneDialog;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.util.Collection;

/**
 * @since 4.0
 */
public class DbActionOptionsDialog extends CayenneDialog {

    public static final int CANCEL = 0;
    public static final int SELECT = 1;

    protected int choice;
    private JLabel schemaLabel;
    private JLabel catalogLabel;
    private JComboBox<String> catalogSelector;
    private JComboBox<String> schemaSelector;
    private JButton selectButton;
    private JButton cancelButton;
    protected JPanel buttons;

    public DbActionOptionsDialog(Frame owner, String title, Collection<String> catalogs, Collection<String> schemas,
                                 String currentCatalog, String currentSchema) throws HeadlessException {
        super(owner, title);
        init();
        initController();
        initFromModel(catalogs, schemas, currentCatalog, currentSchema);

        pack();
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        centerWindow();
    }

    protected void init() {
        // create widgets...
        selectButton = new JButton("Continue");
        cancelButton = new JButton("Cancel");
        catalogSelector = new JComboBox<>();
        schemaSelector = new JComboBox<>();

        getRootPane().setDefaultButton(selectButton);

        //TODO test on GUI
        JPanel panel = FormBuilder.create()
                .columns("right:pref, 3dlu, fill:max(170dlu;pref):grow")
                .rows("2*(p, 3dlu)")
                .add("Select Catalog:").xy(1, 1)
                .add(catalogSelector).xy(3, 1)
                .add("Select Schema:").xy(1, 3)
                .add(schemaSelector).xy(3, 3)
                .padding(Paddings.DIALOG)
                .build();

        buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        buttons.add(cancelButton);
        buttons.add(selectButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    protected void initController() {
        selectButton.addActionListener(e -> processSelect());
        cancelButton.addActionListener(e -> processCancel());
    }

    protected void initFromModel(Collection<String> catalogs, Collection<String> schemas, String currentCatalog, String currentSchema) {
        this.choice = CANCEL;

        boolean showSchemaSelector = schemas != null && !schemas.isEmpty();
        schemaSelector.setVisible(showSchemaSelector);
        schemaLabel.setVisible(showSchemaSelector);
        if (showSchemaSelector) {
            schemaSelector.setModel(new DefaultComboBoxModel<>(schemas.toArray(new String[0])));
            if (currentSchema != null) {
                for (String schema : schemas) {
                    if (currentSchema.equalsIgnoreCase(schema)) {
                        schemaSelector.setSelectedItem(schema);
                        break;
                    }
                }
            }
        }

        boolean showCatalogSelector = catalogs != null && !catalogs.isEmpty();
        catalogSelector.setVisible(showCatalogSelector);
        catalogLabel.setVisible(showCatalogSelector);
        if (showCatalogSelector) {
            catalogSelector.setModel(new DefaultComboBoxModel<>(catalogs.toArray(new String[0])));
            if (currentCatalog != null && !currentCatalog.isEmpty()) {
                for (String catalog : catalogs) {
                    if (currentCatalog.equalsIgnoreCase(catalog)) {
                        catalogSelector.setSelectedItem(catalog);
                        break;
                    }
                }
            }
        }
    }

    public int getChoice() {
        return choice;
    }

    private void processSelect() {
        choice = SELECT;
        setVisible(false);
    }

    private void processCancel() {
        choice = CANCEL;
        setVisible(false);
    }

    /**
     * Returns selected catalog.
     */
    public String getSelectedCatalog() {
        String catalog = (String) catalogSelector.getSelectedItem();
        return "".equals(catalog) ? null : catalog;
    }

    /**
     * Returns selected schema.
     */
    public String getSelectedSchema() {
        String schema = (String) schemaSelector.getSelectedItem();
        return "".equals(schema) ? null : schema;
    }
}
