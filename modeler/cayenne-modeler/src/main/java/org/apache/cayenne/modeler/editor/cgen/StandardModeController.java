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

package org.apache.cayenne.modeler.editor.cgen;

import org.apache.cayenne.gen.CgenConfiguration;
import org.apache.cayenne.gen.ClassGenerationAction;
import org.apache.cayenne.gen.TemplateType;
import org.apache.cayenne.modeler.CodeTemplateManager;
import org.apache.cayenne.modeler.dialog.cgen.TemplateDialog;
import org.apache.cayenne.modeler.dialog.pref.PreferenceDialog;
import org.apache.cayenne.modeler.pref.DataMapDefaults;
import org.apache.cayenne.swing.BindingBuilder;

import javax.swing.DefaultComboBoxModel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * @since 4.1
 */
public class StandardModeController extends GeneratorController {

    protected StandardModePanel view;
    protected DataMapDefaults preferences;

    public StandardModeController(CodeGeneratorController parent) {
        super(parent);
        bind();
        initListeners();
    }

    private void bind() {
        BindingBuilder builder = new BindingBuilder(getApplication().getBindingFactory(), this);
        builder.bindToAction(view.getTemplateManagerButton(), "popPreferencesAction()");

    }

    protected void initListeners() {
        this.view.getPairs().addActionListener(val -> {
            cgenConfiguration.setMakePairs(view.getPairs().isSelected());
            if (!view.getPairs().isSelected()) {
                cgenConfiguration.setTemplate(TemplateType.ENTITY_SINGLE_CLASS.pathFromSourceRoot());
                cgenConfiguration.setEmbeddableTemplate(TemplateType.EMBEDDABLE_SINGLE_CLASS.pathFromSourceRoot());
                cgenConfiguration.setDataMapTemplate(TemplateType.DATAMAP_SINGLE_CLASS.pathFromSourceRoot());
            } else {
                cgenConfiguration.setTemplate(TemplateType.ENTITY_SUBCLASS.pathFromSourceRoot());
                cgenConfiguration.setEmbeddableTemplate(TemplateType.EMBEDDABLE_SUBCLASS.pathFromSourceRoot());
                cgenConfiguration.setDataMapTemplate(TemplateType.DATAMAP_SUBCLASS.pathFromSourceRoot());
            }
            initForm(cgenConfiguration);
            getParentController().checkCgenConfigDirty();
        });

        view.getOverwrite().addActionListener(val -> {
            cgenConfiguration.setOverwrite(view.getOverwrite().isSelected());
            getParentController().checkCgenConfigDirty();
        });

        view.getCreatePropertyNames().addActionListener(val -> {
            cgenConfiguration.setCreatePropertyNames(view.getCreatePropertyNames().isSelected());
            getParentController().checkCgenConfigDirty();
        });

        view.getUsePackagePath().addActionListener(val -> {
            cgenConfiguration.setUsePkgPath(view.getUsePackagePath().isSelected());
            getParentController().checkCgenConfigDirty();
        });

        view.getPkProperties().addActionListener(val -> {
            cgenConfiguration.setCreatePKProperties(view.getPkProperties().isSelected());
            getParentController().checkCgenConfigDirty();
        });

    }


    protected void createView() {
        this.view = new StandardModePanel(getParentController());
    }

    public StandardModePanel getView() {
        return view;
    }

    @Override
    public void updateConfiguration(CgenConfiguration cgenConfiguration) {
        cgenConfiguration.setTemplate(TemplateType.ENTITY_SUBCLASS.pathFromSourceRoot());
        cgenConfiguration.setSuperTemplate(TemplateType.ENTITY_SUPERCLASS.pathFromSourceRoot());
    }


    private void updateTemplates() {

        CodeTemplateManager templateManager = getApplication().getCodeTemplateManager();

        List<String> customTemplates = new ArrayList<>(templateManager.getCustomTemplates().keySet());
        Collections.sort(customTemplates);

        List<String> superTemplates = new ArrayList<>();
        superTemplates.add(TemplateType.ENTITY_SUPERCLASS.readableName());
        superTemplates.addAll(customTemplates);

        List<String> subTemplates = new ArrayList<>();
        subTemplates.add(TemplateType.ENTITY_SINGLE_CLASS.readableName());
        subTemplates.add(TemplateType.ENTITY_SUBCLASS.readableName());
        Collections.sort(subTemplates);
        subTemplates.addAll(customTemplates);

        List<String> embeddableSuperTemplates = new ArrayList<>();
        embeddableSuperTemplates.add(TemplateType.EMBEDDABLE_SUPERCLASS.readableName());
        embeddableSuperTemplates.addAll(customTemplates);

        List<String> embeddableTemplates = new ArrayList<>();
        embeddableTemplates.add(TemplateType.EMBEDDABLE_SINGLE_CLASS.readableName());
        embeddableTemplates.add(TemplateType.EMBEDDABLE_SUBCLASS.readableName());
        Collections.sort(embeddableTemplates);
        embeddableTemplates.addAll(customTemplates);

        List<String> dataMapSuperTemplates = new ArrayList<>();
        dataMapSuperTemplates.add(TemplateType.DATAMAP_SUPERCLASS.readableName());
        dataMapSuperTemplates.addAll(customTemplates);

        List<String> dataMapTemplates = new ArrayList<>();
        dataMapTemplates.add(TemplateType.DATAMAP_SINGLE_CLASS.readableName());
        dataMapTemplates.add(TemplateType.DATAMAP_SUBCLASS.readableName());
        Collections.sort(dataMapTemplates);
        dataMapTemplates.addAll(customTemplates);

        this.view.getSubclassTemplate().getComboBox().setModel(new DefaultComboBoxModel<>(subTemplates.toArray(new String[0])));
        this.view.getSuperclassTemplate().getComboBox().setModel(new DefaultComboBoxModel<>(superTemplates.toArray(new String[0])));
        this.view.getDataMapTemplate().getComboBox().setModel(new DefaultComboBoxModel<>(dataMapTemplates.toArray(new String[0])));
        this.view.getDataMapSuperTemplate().getComboBox().setModel(new DefaultComboBoxModel<>(dataMapSuperTemplates.toArray(new String[0])));
        this.view.getEmbeddableTemplate().getComboBox().setModel(new DefaultComboBoxModel<>(embeddableTemplates.toArray(new String[0])));
        this.view.getEmbeddableSuperTemplate().getComboBox().setModel(new DefaultComboBoxModel<>(embeddableSuperTemplates.toArray(new String[0])));
    }

    @SuppressWarnings("unused")
    public void popPreferencesAction() {
        new PreferenceDialog(getApplication().getFrameController()).startupAction(PreferenceDialog.TEMPLATES_KEY);
        updateTemplates();
        updateComboBoxes();
    }

    private void missTemplateDialog(CgenConfiguration cgenConfiguration, String template, String superTemplate) {
        new TemplateDialog(this, cgenConfiguration, template, superTemplate).startupAction();
        updateComboBoxes();
    }

    public void addTemplateAction(String template, String superTemplate) {
        new PreferenceDialog(getApplication().getFrameController()).startupToCreateTemplate(template, superTemplate);
        updateTemplates();
    }

    private String getTemplateName(Supplier<String> supplier, Path path) {
        return getApplication().getCodeTemplateManager().getNameByPath(
                supplier.get(), path);
    }

    private String getPath(Supplier<String> supplier, Path rootPath) {
        if (rootPath != null) {
            return rootPath.resolve(Paths.get(supplier.get())).normalize().toString();
        } else {
            return (Paths.get(supplier.get()).normalize().toString());
        }
    }




    private void updateComboBoxes() {
        Path rootPath = cgenConfiguration.getRootPath();

        String templateName = getTemplateName(cgenConfiguration::getTemplate, rootPath);
        String superTemplateName = getTemplateName(cgenConfiguration::getSuperTemplate, rootPath);
        String embeddableTemplateName = getTemplateName(cgenConfiguration::getEmbeddableTemplate, rootPath);
        String embeddableSuperTemplateName = getTemplateName(cgenConfiguration::getEmbeddableSuperTemplate, rootPath);
        String dataMapTemplateName = getTemplateName(cgenConfiguration::getDataMapTemplate, rootPath);
        String dataMapSuperTemplateName = getTemplateName(cgenConfiguration::getDataMapSuperTemplate, rootPath);

        String path = getPath(cgenConfiguration::getTemplate, rootPath);
        String superPath = getPath(cgenConfiguration::getSuperTemplate, rootPath);
        String embeddableTemplatePath = getPath(cgenConfiguration::getEmbeddableTemplate, rootPath);
        String embeddableSuperTemplatePath = getPath(cgenConfiguration::getEmbeddableSuperTemplate, rootPath);
        String dataMapTemplatePath = getPath(cgenConfiguration::getDataMapTemplate, rootPath);
        String dataMapSuperTemplatePath = getPath(cgenConfiguration::getDataMapSuperTemplate, rootPath);

        if (templateName == null && superTemplateName == null) {
            view.getSubclassTemplate().setItem(null);
            view.getSuperclassTemplate().setItem(null);
            missTemplateDialog(cgenConfiguration, path, superPath);
        } else if (templateName == null) {
            view.getSubclassTemplate().setItem(null);
            missTemplateDialog(cgenConfiguration, path, null);
        } else if (superTemplateName == null) {
            view.getSuperclassTemplate().setItem(null);
            missTemplateDialog(cgenConfiguration, null, superPath);
        } else {
            view.getSubclassTemplate().setItem(templateName);
            view.getSuperclassTemplate().setItem(superTemplateName);
        }

        if(embeddableTemplateName == null && embeddableSuperTemplateName == null) {
            missTemplateDialog(cgenConfiguration, embeddableTemplatePath, embeddableSuperTemplatePath);
        } else if(embeddableTemplateName == null) {
            missTemplateDialog(cgenConfiguration, embeddableTemplatePath, null);
        } else if(embeddableSuperTemplateName == null) {
            missTemplateDialog(cgenConfiguration, null, embeddableSuperTemplatePath);
        }
        view.getEmbeddableTemplate().setItem(embeddableTemplateName);
        view.getEmbeddableSuperTemplate().setItem(embeddableSuperTemplateName);

        if(dataMapTemplateName == null && dataMapSuperTemplateName == null) {
            missTemplateDialog(cgenConfiguration, dataMapTemplatePath, dataMapSuperTemplatePath);
        } else if(dataMapTemplateName == null) {
            missTemplateDialog(cgenConfiguration, dataMapTemplatePath, null);
        } else if(dataMapSuperTemplateName == null) {
            missTemplateDialog(cgenConfiguration, null, dataMapSuperTemplatePath);
        }
        view.getDataMapTemplate().setItem(dataMapTemplateName);
        view.getDataMapSuperTemplate().setItem(dataMapSuperTemplateName);
        view.setDisableSuperComboBoxes(view.getPairs().isSelected());
    }

    @Override
    public void initForm(CgenConfiguration cgenConfiguration) {
        super.initForm(cgenConfiguration);
        updateTemplates();
        view.getOutputPattern().setText(cgenConfiguration.getOutputPattern());
        view.getPairs().setSelected(cgenConfiguration.isMakePairs());
        view.getUsePackagePath().setSelected(cgenConfiguration.isUsePkgPath());
        view.getOverwrite().setSelected(cgenConfiguration.isOverwrite());
        view.getCreatePropertyNames().setSelected(cgenConfiguration.isCreatePropertyNames());
        view.getPkProperties().setSelected(cgenConfiguration.isCreatePKProperties());
        view.getSuperPkg().setText(cgenConfiguration.getSuperPkg());
        updateComboBoxes();
    }
}
