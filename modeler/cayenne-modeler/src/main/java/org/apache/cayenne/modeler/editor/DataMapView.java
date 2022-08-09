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

package org.apache.cayenne.modeler.editor;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.configuration.event.DataMapEvent;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.swing.components.JCayenneCheckBox;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.action.LinkDataMapAction;
import org.apache.cayenne.modeler.dialog.datamap.CatalogUpdateController;
import org.apache.cayenne.modeler.dialog.datamap.LockingUpdateController;
import org.apache.cayenne.modeler.dialog.datamap.PackageUpdateController;
import org.apache.cayenne.modeler.dialog.datamap.SchemaUpdateController;
import org.apache.cayenne.modeler.dialog.datamap.SuperclassUpdateController;
import org.apache.cayenne.modeler.pref.DataMapDefaults;
import org.apache.cayenne.modeler.util.CellRenderers;
import org.apache.cayenne.modeler.util.Comparators;
import org.apache.cayenne.modeler.util.ProjectUtil;
import org.apache.cayenne.modeler.util.TextAdapter;
import org.apache.cayenne.project.extension.info.ObjectInfo;
import org.apache.cayenne.util.Util;
import org.apache.cayenne.validation.ValidationException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.util.Arrays;

/**
 * Panel for editing a DataMap.
 */
public class DataMapView extends JPanel {

    protected ProjectController eventController;

    protected TextAdapter name;
    protected JLabel location;
    protected JComboBox<DataNodeDescriptor> nodeSelector;
    protected JCheckBox defaultLockType;
    protected TextAdapter defaultCatalog;
    protected TextAdapter defaultSchema;
    protected TextAdapter defaultPackage;
    protected TextAdapter defaultSuperclass;
    protected JCheckBox quoteSQLIdentifiers;

    protected TextAdapter comment;

    protected JButton updateDefaultCatalog;
    protected JButton updateDefaultSchema;
    protected JButton updateDefaultPackage;
    protected JButton updateDefaultSuperclass;
    protected JButton updateDefaultLockType;

    public DataMapView(ProjectController eventController) {
        this.eventController = eventController;

        initView();
        initController();
    }

    private void initView() {
        // create widgets
        name = new TextAdapter(new JTextField()) {

            protected void updateModel(String text) {
                setDataMapName(text);
            }
        };

        location = new JLabel();
        nodeSelector = Application.getWidgetFactory().createUndoableComboBox();
        nodeSelector.setRenderer(CellRenderers.listRendererWithIcons());

        updateDefaultCatalog = new JButton("Update...");
        defaultCatalog = new TextAdapter(new JTextField()) {

            protected void updateModel(String text) {
                setDefaultCatalog(text);
            }
        };
        
        updateDefaultSchema = new JButton("Update...");
        defaultSchema = new TextAdapter(new JTextField()) {

            protected void updateModel(String text) {
                setDefaultSchema(text);
            }
        };

        quoteSQLIdentifiers = new JCayenneCheckBox();

        comment = new TextAdapter(new JTextField()) {
            @Override
            protected void updateModel(String text) throws ValidationException {
                updateComment(text);
            }
        };

        updateDefaultPackage = new JButton("Update...");
        defaultPackage = new TextAdapter(new JTextField()) {

            protected void updateModel(String text) {
                setDefaultPackage(text);
            }
        };

        updateDefaultSuperclass = new JButton("Update...");
        defaultSuperclass = new TextAdapter(new JTextField()) {

            protected void updateModel(String text) {
                setDefaultSuperclass(text);
            }
        };

        updateDefaultLockType = new JButton("Update...");
        defaultLockType = new JCayenneCheckBox();

        // assemble
        JPanel panel = FormBuilder.create()
                .columns("right:70dlu, 3dlu, fill:180dlu, 3dlu, fill:120")
                .rows("12*(p, 3dlu)")
                .addSeparator("DataMap Configuration").xyw(1,  1, 5)
                .add("DataMap Name:").xy(1, 3)
                .add(name.getComponent()).xyw(3, 3,2)
                .add("File:").xy(1, 5)
                .add(location).xyw(3, 5,2)
                .add("DataNode:").xy(1, 7)
                .add(nodeSelector).xyw(3, 7,2)
                .add("Quote SQL Identifiers:").xy(1, 9)
                .add(quoteSQLIdentifiers).xyw(3, 9,2)
                .add("Comment:").xy(1, 11)
                .add(comment.getComponent()).xyw(3, 11,2)

                .addSeparator("Entity Defaults").xyw(1,  13, 5)
                .add("DB Catalog:").xy(1, 15)
                .add(defaultCatalog.getComponent()).xy(3, 15)
                .add(updateDefaultCatalog).xy(5, 15)
                .add("DB Schema:").xy(1, 17)
                .add( defaultSchema.getComponent()).xy(3, 17)
                .add(updateDefaultSchema).xy(5, 17)
                .add("Java Package:").xy(1, 19)
                .add( defaultPackage.getComponent()).xy(3, 19)
                .add(updateDefaultPackage).xy(5, 19)
                .add("Custom Superclass:").xy(1, 21)
                .add( defaultSuperclass.getComponent()).xy(3, 21)
                .add(updateDefaultSuperclass).xy(5, 21)
                .add("Optimistic Locking:").xy(1, 23)
                .add( defaultLockType).xy(3, 23)
                .add(updateDefaultLockType).xy(5, 23)
                .padding(Paddings.DIALOG)
                .build();

        this.setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
    }

    private void initController() {
        eventController.addDataMapDisplayListener(e -> {
            DataMap map = e.getDataMap();
            if (map != null) {
                initFromModel(map);
            }
        });

        nodeSelector.addActionListener(e -> setDataNode());
        quoteSQLIdentifiers.addItemListener(e -> setQuoteSQLIdentifiers(quoteSQLIdentifiers.isSelected()));
        defaultLockType.addItemListener(e -> setDefaultLockType(defaultLockType.isSelected()
                ? ObjEntity.LOCK_TYPE_OPTIMISTIC
                : ObjEntity.LOCK_TYPE_NONE));
        updateDefaultCatalog.addActionListener(e -> updateDefaultCatalog());
        updateDefaultSchema.addActionListener(e -> updateDefaultSchema());
        updateDefaultPackage.addActionListener(e -> updateDefaultPackage());
        updateDefaultSuperclass.addActionListener(e -> updateDefaultSuperclass());
        updateDefaultLockType.addActionListener(e -> updateDefaultLockType());
    }

    /**
     * Updates the view from the current model state. Invoked when a currently displayed
     * ObjEntity is changed.
     */
    private void initFromModel(DataMap map) {
        name.setText(map.getName());
        location.setText((map.getLocation() != null) ? map.getLocation() : "(no file)");
        quoteSQLIdentifiers.setSelected(map.isQuotingSQLIdentifiers());
        comment.setText(getComment(map));

        // rebuild data node list

        DataNodeDescriptor nodes[] = ((DataChannelDescriptor) eventController.getProject().getRootNode())
                .getNodeDescriptors().toArray(new DataNodeDescriptor[0]);

        // add an empty item to the front
        DataNodeDescriptor[] objects = new DataNodeDescriptor[nodes.length + 1];

        // now add the entities
        if (nodes.length > 0) {
            Arrays.sort(nodes, Comparators.getNamedObjectComparator());
            System.arraycopy(nodes, 0, objects, 1, nodes.length);
        }

        DefaultComboBoxModel<DataNodeDescriptor> model = new DefaultComboBoxModel<>(objects);

        // find selected node
        for (DataNodeDescriptor node : nodes) {
            if (node.getDataMapNames().contains(map.getName())) {
                model.setSelectedItem(node);
                break;
            }
        }

        nodeSelector.setModel(model);

        // init default fields
        defaultLockType.setSelected(map.getDefaultLockType() != ObjEntity.LOCK_TYPE_NONE);
        defaultPackage.setText(map.getDefaultPackage());
        defaultCatalog.setText(map.getDefaultCatalog());
        defaultSchema.setText(map.getDefaultSchema());
        defaultSuperclass.setText(map.getDefaultSuperclass());
    }

    void setDefaultLockType(int lockType) {
        DataMap dataMap = eventController.getCurrentDataMap();

        if (dataMap == null) {
            return;
        }

        int oldType = dataMap.getDefaultLockType();
        if (oldType == lockType) {
            return;
        }

        dataMap.setDefaultLockType(lockType);
        eventController.fireDataMapEvent(new DataMapEvent(this, dataMap));
    }

    void setQuoteSQLIdentifiers(boolean flag) {
        DataMap dataMap = eventController.getCurrentDataMap();

        if (dataMap == null) {
            return;
        }

        if (dataMap.isQuotingSQLIdentifiers() != flag) {
            dataMap.setQuotingSQLIdentifiers(flag);

            eventController.fireDataMapEvent(new DataMapEvent(this, dataMap));
        }
    }

    void setDefaultPackage(String newDefaultPackage) {
        DataMap dataMap = eventController.getCurrentDataMap();

        if (dataMap == null) {
            return;
        }

        if (newDefaultPackage != null && newDefaultPackage.trim().length() == 0) {
            newDefaultPackage = null;
        }

        String oldPackage = dataMap.getDefaultPackage();
        if (Util.nullSafeEquals(newDefaultPackage, oldPackage)) {
            return;
        }

        dataMap.setDefaultPackage(newDefaultPackage);

        // update class generation preferences
        eventController.getDataMapPreferences("").setSuperclassPackage(
                newDefaultPackage,
                DataMapDefaults.DEFAULT_SUPERCLASS_PACKAGE_SUFFIX);

        eventController.fireDataMapEvent(new DataMapEvent(this, dataMap));
    }

    void setDefaultCatalog(String newCatalog) {
        DataMap dataMap = eventController.getCurrentDataMap();

        if (dataMap == null) {
            return;
        }

        if (newCatalog != null && newCatalog.trim().length() == 0) {
            newCatalog = null;
        }

        String oldCatalog = dataMap.getDefaultCatalog();
        if (Util.nullSafeEquals(newCatalog, oldCatalog)) {
            return;
        }

        dataMap.setDefaultCatalog(newCatalog);
        eventController.fireDataMapEvent(new DataMapEvent(this, dataMap));
    }

    void setDefaultSchema(String newSchema) {
        DataMap dataMap = eventController.getCurrentDataMap();

        if (dataMap == null) {
            return;
        }

        if (newSchema != null && newSchema.trim().length() == 0) {
            newSchema = null;
        }

        String oldSchema = dataMap.getDefaultSchema();
        if (Util.nullSafeEquals(newSchema, oldSchema)) {
            return;
        }

        dataMap.setDefaultSchema(newSchema);
        eventController.fireDataMapEvent(new DataMapEvent(this, dataMap));
    }

    void setDefaultSuperclass(String newSuperclass) {
        DataMap dataMap = eventController.getCurrentDataMap();

        if (dataMap == null) {
            return;
        }

        if (newSuperclass != null && newSuperclass.trim().length() == 0) {
            newSuperclass = null;
        }

        String oldSuperclass = dataMap.getDefaultSuperclass();
        if (Util.nullSafeEquals(newSuperclass, oldSuperclass)) {
            return;
        }

        dataMap.setDefaultSuperclass(newSuperclass);
        eventController.fireDataMapEvent(new DataMapEvent(this, dataMap));
    }

    void setDataMapName(String newName) {
        if (newName == null || newName.trim().length() == 0) {
            throw new ValidationException("Enter name for DataMap");
        }

        DataMap map = eventController.getCurrentDataMap();

        // search for matching map name across domains, as currently they have to be
        // unique globally
        DataChannelDescriptor dataChannelDescriptor = (DataChannelDescriptor) Application
                .getInstance()
                .getProject()
                .getRootNode();

        DataMap matchingMap = dataChannelDescriptor.getDataMap(newName);

        if (matchingMap != null && !matchingMap.equals(map)) {

            // there is an entity with the same name
            throw new ValidationException("There is another DataMap named '"
                    + newName
                    + "'. Use a different name.");
        }
        String oldName = map.getName();
        if (Util.nullSafeEquals(newName, oldName)) {
            return;
        }
        // completely new name, set new name for domain
        DataMapDefaults pref = eventController.getDataMapPreferences("");
        DataMapEvent e = new DataMapEvent(this, map, map.getName());
        ProjectUtil.setDataMapName((DataChannelDescriptor) eventController
                .getProject()
                .getRootNode(), map, newName);
        pref.copyPreferences(newName);
        eventController.fireDataMapEvent(e);
    }

    void setDataNode() {
        DataNodeDescriptor node = (DataNodeDescriptor) nodeSelector.getSelectedItem();
        DataMap map = eventController.getCurrentDataMap();
        LinkDataMapAction action = eventController.getApplication().getActionManager().getAction(LinkDataMapAction.class);
        action.linkDataMap(map, node);
    }

    void updateDefaultCatalog() {
        DataMap dataMap = eventController.getCurrentDataMap();

        if (dataMap == null) {
            return;
        }

        if (dataMap.getDbEntities().size() > 0 || dataMap.getProcedures().size() > 0) {
            new CatalogUpdateController(eventController, dataMap).startupAction();
        }
    }

    void updateDefaultSchema() {
        DataMap dataMap = eventController.getCurrentDataMap();

        if (dataMap == null) {
            return;
        }

        if (dataMap.getDbEntities().size() > 0 || dataMap.getProcedures().size() > 0) {
            new SchemaUpdateController(eventController, dataMap).startupAction();
        }
    }

    void updateDefaultSuperclass() {
        DataMap dataMap = eventController.getCurrentDataMap();

        if (dataMap == null) {
            return;
        }

        if (dataMap.getObjEntities().size() > 0) {
            new SuperclassUpdateController(eventController, dataMap).startupAction();
        }
    }

    void updateDefaultPackage() {
        DataMap dataMap = eventController.getCurrentDataMap();

        if (dataMap == null) {
            return;
        }

        if (dataMap.getObjEntities().size() > 0 || dataMap.getEmbeddables().size() > 0) {
            new PackageUpdateController(eventController, dataMap).startupAction();
        }
    }

    void updateDefaultLockType() {
        DataMap dataMap = eventController.getCurrentDataMap();

        if (dataMap == null) {
            return;
        }

        if (dataMap.getObjEntities().size() > 0) {
            new LockingUpdateController(eventController, dataMap).startup();
        }
    }

    void updateComment(String comment) {
        DataMap dataMap = eventController.getCurrentDataMap();
        if (dataMap == null) {
            return;
        }

        ObjectInfo.putToMetaData(eventController.getApplication().getMetaData(), dataMap, ObjectInfo.COMMENT, comment);
        eventController.fireDataMapEvent(new DataMapEvent(this, dataMap));
    }

    private String getComment(DataMap dataMap) {
        return ObjectInfo.getFromMetaData(eventController.getApplication().getMetaData(), dataMap, ObjectInfo.COMMENT);
    }
}
