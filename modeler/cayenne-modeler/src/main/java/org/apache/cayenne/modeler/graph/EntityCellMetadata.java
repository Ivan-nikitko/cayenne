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
package org.apache.cayenne.modeler.graph;

import java.io.Serializable;
import java.util.Objects;

import org.apache.cayenne.map.Attribute;
import org.apache.cayenne.map.Entity;
import org.apache.cayenne.map.Relationship;

/**
 * Abstract class to describe entity's cell 
 */
abstract class EntityCellMetadata<E extends Entity<E, A, R>, A extends Attribute<E, A, R>, R extends Relationship<E, A, R>>
        implements Serializable {
    final GraphBuilder<E,A,R> builder;
    final String entityName;
    final String label;
    
    EntityCellMetadata(GraphBuilder<E,A,R> builder, Entity<E,A,R> entity) {
        this.builder = Objects.requireNonNull(builder);
        this.entityName = Objects.requireNonNull(entity).getName();
        this.label = createLabel(entity);
    }
        
    /**
     * Resolves entity
     */
    public abstract Entity<E, A, R> fetchEntity();

    public String toString() {
        return label;
    }
    
    /**
     * Creates label for this cell
     */
    String createLabel(Entity<E, A, R> entity) {
        StringBuilder label = new StringBuilder("<html><center><u><b>")
                .append(entity.getName())
                .append("</b></u></center>");
        for (A attr : entity.getAttributes()) {
            if (isPrimary(attr)) {
                label.append("<br><i>").append(attr.getName()).append("</i>");
            }
        }
        for (A attr : entity.getAttributes()) {
            if (!isPrimary(attr)) {
                label.append("<br>").append(attr.getName());
            }
        }
        return label.append("</html>").toString();
    }
    
    /**
     * Returns whether attribute is "primary" and should therefore be written in italic
     */
    protected abstract boolean isPrimary(A attr);
}
