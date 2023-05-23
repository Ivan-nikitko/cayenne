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

package org.apache.cayenne.project.extension;

import org.apache.cayenne.configuration.ConfigurationNodeVisitor;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.Embeddable;
import org.apache.cayenne.map.EmbeddableAttribute;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.map.Procedure;
import org.apache.cayenne.map.ProcedureParameter;
import org.apache.cayenne.map.QueryDescriptor;
import org.apache.cayenne.map.relationship.DbRelationshipSide;

/**
 * @since 4.1
 */
public class BaseNamingDelegate implements ConfigurationNodeVisitor<String> {

    @Override
    public String visitDataChannelDescriptor(DataChannelDescriptor channelDescriptor) {
        return null;
    }

    @Override
    public String visitDataNodeDescriptor(DataNodeDescriptor nodeDescriptor) {
        return null;
    }

    @Override
    public String visitDataMap(DataMap dataMap) {
        return null;
    }

    @Override
    public String visitObjEntity(ObjEntity entity) {
        return null;
    }

    @Override
    public String visitDbEntity(DbEntity entity) {
        return null;
    }

    @Override
    public String visitEmbeddable(Embeddable embeddable) {
        return null;
    }

    @Override
    public String visitEmbeddableAttribute(EmbeddableAttribute attribute) {
        return null;
    }

    @Override
    public String visitObjAttribute(ObjAttribute attribute) {
        return null;
    }

    @Override
    public String visitDbAttribute(DbAttribute attribute) {
        return null;
    }

    @Override
    public String visitObjRelationship(ObjRelationship relationship) {
        return null;
    }

    @Override
    public String visitDbRelationship(DbRelationship relationship) {
        return null;
    }

    @Override
    public String visitProcedure(Procedure procedure) {
        return null;
    }

    @Override
    public String visitProcedureParameter(ProcedureParameter parameter) {
        return null;
    }

    @Override
    public String visitQuery(QueryDescriptor query) {
        return null;
    }

    @Override
    public String visitDbJoin(org.apache.cayenne.map.relationship.DbRelationship relationship) {
        return null;
    }

    @Override
    public String visitDbRelationship(DbRelationshipSide relationship) {
        return null;
    }

    @Override
    public String visitDbRelationship(org.apache.cayenne.map.relationship.DbRelationship relationship) {
        return null;
    }
}
