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

package org.apache.cayenne.gen;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.cayenne.configuration.ConfigurationNodeVisitor;
import org.apache.cayenne.gen.xml.CgenExtension;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Embeddable;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.util.XMLEncoder;
import org.apache.cayenne.util.XMLSerializable;
import org.apache.cayenne.validation.ValidationException;

/**
 * Stores configuration for the code generation tool.
 * CgenConfiguration is stored in the dataMap xml file and used by Modeler and CLI tools (Maven, Gradle and Ant).
 *
 * @since 4.1
 */
public class CgenConfiguration implements Serializable, XMLSerializable {

    private Collection<Artifact> artifacts;
    private Set<String> entityArtifacts;
    private Collection<String> excludeEntityArtifacts;
    private Set<String> embeddableArtifacts;
    private Collection<String> excludeEmbeddableArtifacts;

    private String superPkg;
    private DataMap dataMap;

    private ArtifactsGenerationMode artifactsGenerationMode;
    private boolean makePairs;

    private Path rootPath;
    private Path relPath;
    private boolean overwrite;
    private boolean usePkgPath;

    private String template;
    private String superTemplate;
    private String embeddableTemplate;
    private String embeddableSuperTemplate;
    /**
     * @since 5.0 renamed from queryTemplate
     */
    private String dataMapTemplate;
    /**
     * @since 5.0 renamed from querySuperTemplate
     */
    private String dataMapSuperTemplate;
    private long timestamp;
    private String outputPattern;
    private String encoding;
    private boolean createPropertyNames;
    private boolean force; // force run generator
    /**
     * @since 4.1
     */
    private boolean createPKProperties;

    /**
     * @since 4.2
     */
    private String externalToolConfig;

    public CgenConfiguration() {
        /*
         * {@link #isDefault()} method should be in sync with the following values
         */
        this.outputPattern = "*.java";
        this.timestamp = 0L;
        this.usePkgPath = true;
        this.makePairs = true;
        setArtifactsGenerationMode("entity");

        this.artifacts = new ArrayList<>();
        this.entityArtifacts = new HashSet<>();
        this.excludeEntityArtifacts = new ArrayList<>();
        this.embeddableArtifacts = new HashSet<>();
        this.excludeEmbeddableArtifacts = new ArrayList<>();
        this.artifactsGenerationMode = ArtifactsGenerationMode.ENTITY;

        this.template = TemplateType.ENTITY_SUBCLASS.pathFromSourceRoot();
        this.superTemplate = TemplateType.ENTITY_SUPERCLASS.pathFromSourceRoot();
        this.dataMapTemplate = TemplateType.DATAMAP_SUBCLASS.pathFromSourceRoot();
        this.dataMapSuperTemplate = TemplateType.DATAMAP_SUPERCLASS.pathFromSourceRoot();

        this.embeddableTemplate = TemplateType.EMBEDDABLE_SUBCLASS.pathFromSourceRoot();
        this.embeddableSuperTemplate = TemplateType.EMBEDDABLE_SUPERCLASS.pathFromSourceRoot();
    }

    public void resetCollections() {
        embeddableArtifacts.clear();
        entityArtifacts.clear();
    }

    public String getSuperPkg() {
        return superPkg;
    }

    public void setSuperPkg(String superPkg) {
        this.superPkg = superPkg;
    }

    public DataMap getDataMap() {
        return dataMap;
    }

    public void setDataMap(DataMap dataMap) {
        this.dataMap = dataMap;
    }

    public void setArtifactsGenerationMode(String mode) {
        if (ArtifactsGenerationMode.ENTITY.getLabel().equalsIgnoreCase(mode)) {
            this.artifactsGenerationMode = ArtifactsGenerationMode.ENTITY;
        } else {
            this.artifactsGenerationMode = ArtifactsGenerationMode.ALL;
        }
    }

    public String getArtifactsGenerationMode() {
        return artifactsGenerationMode.getLabel();
    }


    public boolean isMakePairs() {
        return makePairs;
    }

    public void setMakePairs(boolean makePairs) {
        this.makePairs = makePairs;
    }

    public Path getRootPath() {
        return rootPath;
    }

    public void setRootPath(Path rootPath) {
        this.rootPath = rootPath;
    }

    public void setRelPath(Path relPath) {
        this.relPath = relPath;
    }

    public void setRelPath(String pathStr) {
        Path path = Paths.get(pathStr);

        if (rootPath != null) {

            if (!rootPath.isAbsolute()) {
                throw new ValidationException("Root path : " + '"' + rootPath + '"' + "should be absolute");
            }

            if (path.isAbsolute() && rootPath.getRoot().equals(path.getRoot())) {
                this.relPath = rootPath.relativize(path);
                return;
            }
        }
        this.relPath = path;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public boolean isUsePkgPath() {
        return usePkgPath;
    }

    public void setUsePkgPath(boolean usePkgPath) {
        this.usePkgPath = usePkgPath;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getSuperTemplate() {
        return superTemplate;
    }

    public void setSuperTemplate(String superTemplate) {
        this.superTemplate = superTemplate;
    }

    public String getEmbeddableTemplate() {
        return embeddableTemplate;
    }

    public void setEmbeddableTemplate(String embeddableTemplate) {
        this.embeddableTemplate = embeddableTemplate;
    }

    public String getEmbeddableSuperTemplate() {
        return embeddableSuperTemplate;
    }

    public void setEmbeddableSuperTemplate(String embeddableSuperTemplate) {
        this.embeddableSuperTemplate = embeddableSuperTemplate;
    }

    public String getDataMapTemplate() {
        return dataMapTemplate;
    }

    public void setDataMapTemplate(String dataMapTemplate) {
        this.dataMapTemplate = dataMapTemplate;
    }

    public String getDataMapSuperTemplate() {
        return dataMapSuperTemplate;
    }

    public void setDataMapSuperTemplate(String dataMapSuperTemplate) {
        this.dataMapSuperTemplate = dataMapSuperTemplate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOutputPattern() {
        return outputPattern;
    }

    public void setOutputPattern(String outputPattern) {
        this.outputPattern = outputPattern;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public boolean isCreatePropertyNames() {
        return createPropertyNames;
    }

    public void setCreatePropertyNames(boolean createPropertyNames) {
        this.createPropertyNames = createPropertyNames;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public boolean isCreatePKProperties() {
        return createPKProperties;
    }

    public void setCreatePKProperties(boolean createPKProperties) {
        this.createPKProperties = createPKProperties;
    }

    public Path getRelPath() {
        return relPath;
    }

    public String buildRelPath() {
        if (relPath == null || relPath.toString().isEmpty()) {
            return ".";
        }
        return relPath.toString();
    }

    Collection<Artifact> getArtifacts() {
        return artifacts;
    }

    public Set<String> getEntities() {
        return entityArtifacts;
    }

    public Set<String> getEmbeddables() {
        return embeddableArtifacts;
    }

    public String getExternalToolConfig() {
        return externalToolConfig;
    }

    public void setExternalToolConfig(String config) {
        this.externalToolConfig = config;
    }

    void addArtifact(Artifact artifact) {
        artifacts.add(artifact);
    }

    public Path buildPath() {
        if(rootPath == null) {
            return relPath;
        }
        if(relPath == null) {
            return rootPath;
        }
        return rootPath.resolve(relPath).toAbsolutePath().normalize();
    }

    public void loadEntity(ObjEntity entity) {
        if (!entity.isGeneric()) {
            entityArtifacts.add(entity.getName());
        }
    }

    public void loadEmbeddable(String name) {
        embeddableArtifacts.add(name);
    }

    public void loadEntities(String entities) {
        excludeEntityArtifacts.addAll(Arrays.asList(entities.split(",")));
    }

    private String getExcludeEntites() {
        Collection<String> excludeEntities = dataMap.getObjEntities()
                .stream()
                .map(ObjEntity::getName)
                .filter(name -> !entityArtifacts.contains(name))
                .collect(Collectors.toList());
        return String.join(",", excludeEntities);
    }

    public void loadEmbeddables(String embeddables) {
        excludeEmbeddableArtifacts.addAll(Arrays.asList(embeddables.split(",")));
    }

    private String getExcludeEmbeddables() {
        Collection<String> excludeEmbeddable = dataMap.getEmbeddables()
                .stream()
                .map(Embeddable::getClassName)
                .filter(className -> !embeddableArtifacts.contains(className))
                .collect(Collectors.toList());
        return String.join(",", excludeEmbeddable);
    }

    public void resolveExcludeEntities() {
        entityArtifacts = dataMap.getObjEntities()
                .stream()
                .map(ObjEntity::getName)
                .filter(name -> !excludeEntityArtifacts.contains(name))
                .collect(Collectors.toSet());
    }

    public void resolveExcludeEmbeddables() {
        embeddableArtifacts = dataMap.getEmbeddables()
                .stream()
                .map(Embeddable::getClassName)
                .filter(className -> !excludeEmbeddableArtifacts.contains(className))
                .collect(Collectors.toSet());
    }

    public Collection<String> getExcludeEntityArtifacts() {
        return excludeEntityArtifacts;
    }

    public Collection<String> getExcludeEmbeddableArtifacts() {
        return excludeEmbeddableArtifacts;
    }

    @Override
    public void encodeAsXML(XMLEncoder encoder, ConfigurationNodeVisitor delegate) {
        encoder.start("cgen")
                .attribute("xmlns", CgenExtension.NAMESPACE)
                .simpleTag("excludeEntities", getExcludeEntites())
                .simpleTag("excludeEmbeddables", getExcludeEmbeddables())
                .simpleTag("destDir", separatorsToUnix(buildRelPath()))
                .simpleTag("mode", this.artifactsGenerationMode.getLabel())
                .simpleTag("template", separatorsToUnix(this.template))
                .simpleTag("superTemplate", separatorsToUnix(this.superTemplate))
                .simpleTag("embeddableTemplate", separatorsToUnix(this.embeddableTemplate))
                .simpleTag("embeddableSuperTemplate", separatorsToUnix(this.embeddableSuperTemplate))
                .simpleTag("dataMapTemplate", separatorsToUnix(this.dataMapTemplate))
                .simpleTag("dataMapSuperTemplate", separatorsToUnix(this.dataMapSuperTemplate))
                .simpleTag("outputPattern", this.outputPattern)
                .simpleTag("makePairs", Boolean.toString(this.makePairs))
                .simpleTag("usePkgPath", Boolean.toString(this.usePkgPath))
                .simpleTag("overwrite", Boolean.toString(this.overwrite))
                .simpleTag("createPropertyNames", Boolean.toString(this.createPropertyNames))
                .simpleTag("superPkg", separatorsToUnix(this.superPkg))
                .simpleTag("createPKProperties", Boolean.toString(this.createPKProperties))
                .simpleTag("externalToolConfig", this.externalToolConfig)
                .end();
    }

    /**
     * @return is this configuration with all values set to the default
     */
    public boolean isDefault() {
        // this must be is sync with actual default values
        return isMakePairs()
                && usePkgPath
                && !overwrite
                && !createPKProperties
                && !createPropertyNames
                && "*.java".equals(outputPattern)
                && template.equals(TemplateType.ENTITY_SUBCLASS.pathFromSourceRoot())
                && superTemplate.equals(TemplateType.ENTITY_SUPERCLASS.pathFromSourceRoot())
                && (superPkg == null || superPkg.isEmpty())
                && (externalToolConfig == null || externalToolConfig.isEmpty());
    }

    private String separatorsToUnix (String path){
        if (path!=null) {
            return path.replace('\\', '/');
        }
        return null;
    }
}
