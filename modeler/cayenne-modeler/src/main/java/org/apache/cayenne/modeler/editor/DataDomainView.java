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
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.event.DomainEvent;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.DomainDisplayEvent;
import org.apache.cayenne.modeler.event.DomainDisplayListener;
import org.apache.cayenne.modeler.util.TextAdapter;
import org.apache.cayenne.pref.RenamedPreferences;
import org.apache.cayenne.swing.components.JCayenneCheckBox;
import org.apache.cayenne.util.Util;
import org.apache.cayenne.validation.ValidationException;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * Panel for editing DataDomain.
 */
public class DataDomainView extends JPanel implements DomainDisplayListener {

    protected ProjectController projectController;

    protected TextAdapter name;
    protected JCheckBox objectValidation;
    protected JCheckBox sharedCache;

    public DataDomainView(ProjectController projectController) {
        this.projectController = projectController;

        // Create and layout components
        initView();

        // hook up listeners to widgets
        initController();
    }

    protected void initView() {

        // create widgets
        this.name = new TextAdapter(new JTextField()) {

            protected void updateModel(String text) {
                setDomainName(text);
            }
        };

        this.objectValidation = new JCayenneCheckBox();
        this.sharedCache = new JCayenneCheckBox();

        // assemble
        JPanel panel = FormBuilder.create()
                .columns("right:pref, 3dlu, fill:100dlu")
                .rows("4*(p, 3dlu)")
                .addSeparator("DataDomain Configuration").xyw(1,1,3)
                .add("DataDomain Name:").xy(1, 3)
                .add(name.getComponent()).xy(3, 3)
                .add("Object Validation:").xy(1, 5)
                .add(objectValidation).xy(3, 5)
                .add("Use Shared Cache:").xy(1, 7)
                .add(sharedCache).xy(3, 7)
                .padding(Paddings.DIALOG)
                .build();

        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
    }

    protected void initController() {
        projectController.addDomainDisplayListener(this);

        // add item listener to checkboxes
        objectValidation.addItemListener(e -> {
            String value = objectValidation.isSelected() ? "true" : "false";
            setDomainProperty(
                    DataDomain.VALIDATING_OBJECTS_ON_COMMIT_PROPERTY,
                    value,
                    Boolean.toString(DataDomain.VALIDATING_OBJECTS_ON_COMMIT_DEFAULT));
        });

        sharedCache.addItemListener(e -> {
            String value = sharedCache.isSelected() ? "true" : "false";
            setDomainProperty(
                    DataDomain.SHARED_CACHE_ENABLED_PROPERTY,
                    value,
                    Boolean.toString(DataDomain.SHARED_CACHE_ENABLED_DEFAULT));
        });

    }

    /**
     * Helper method that updates domain properties. If a value equals to default, null
     * value is used instead.
     */
    protected void setDomainProperty(String property, String value, String defaultValue) {

        DataChannelDescriptor domain = (DataChannelDescriptor) projectController
                .getProject()
                .getRootNode();

        if (domain == null) {
            return;
        }

        // no empty strings
        if ("".equals(value)) {
            value = null;
        }

        // use NULL for defaults
        if (value != null && value.equals(defaultValue)) {
            value = null;
        }

        Map<String, String> properties = domain.getProperties();
        String oldValue = properties.get(property);
        if (!Util.nullSafeEquals(value, oldValue)) {
            properties.put(property, value);

            DomainEvent e = new DomainEvent(this, domain);
            projectController.fireDomainEvent(e);
        }
    }

    public String getDomainProperty(String property, String defaultValue) {

        DataChannelDescriptor domain = (DataChannelDescriptor) projectController
                .getProject()
                .getRootNode();

        if (domain == null) {
            return null;
        }

        String value = domain.getProperties().get(property);
        return value != null ? value : defaultValue;
    }

    public boolean getDomainBooleanProperty(String property, String defaultValue) {
        return "true".equalsIgnoreCase(getDomainProperty(property, defaultValue));
    }

    /**
     * Invoked on domain selection event. Updates view with the values from the currently
     * selected domain.
     */
    public void currentDomainChanged(DomainDisplayEvent e) {
        DataChannelDescriptor domain = e.getDomain();
        if (null == domain) {
            return;
        }

        // extract values from the new domain object
        name.setText(domain.getName());

        objectValidation.setSelected(getDomainBooleanProperty(
                DataDomain.VALIDATING_OBJECTS_ON_COMMIT_PROPERTY,
                Boolean.toString(DataDomain.VALIDATING_OBJECTS_ON_COMMIT_DEFAULT)));

        sharedCache.setSelected(getDomainBooleanProperty(
                DataDomain.SHARED_CACHE_ENABLED_PROPERTY,
                Boolean.toString(DataDomain.SHARED_CACHE_ENABLED_DEFAULT)));
    }

    void setDomainName(String newName) {

        DataChannelDescriptor dataChannelDescriptor = (DataChannelDescriptor) Application
                .getInstance()
                .getProject()
                .getRootNode();

        if (Util.nullSafeEquals(dataChannelDescriptor.getName(), newName)) {
            return;
        }

        if (newName == null || newName.trim().length() == 0) {
            throw new ValidationException("Enter name for DataDomain");
        }

        Preferences prefs = projectController.getPreferenceForDataDomain();

        DomainEvent e = new DomainEvent(
                this,
                dataChannelDescriptor,
                dataChannelDescriptor.getName());
        dataChannelDescriptor.setName(newName);

        RenamedPreferences.copyPreferences(newName, prefs);
        projectController.fireDomainEvent(e);
    }
}
