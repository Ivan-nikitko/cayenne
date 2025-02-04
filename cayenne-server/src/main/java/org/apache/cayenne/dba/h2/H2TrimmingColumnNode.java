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

package org.apache.cayenne.dba.h2;

import org.apache.cayenne.access.sqlbuilder.sqltree.ColumnNode;
import org.apache.cayenne.access.sqlbuilder.sqltree.Node;
import org.apache.cayenne.access.sqlbuilder.sqltree.NodeType;
import org.apache.cayenne.access.sqlbuilder.sqltree.TrimmingColumnNode;

/**
 * @since 4.3
 */
public class H2TrimmingColumnNode extends TrimmingColumnNode {
    public H2TrimmingColumnNode(ColumnNode columnNode) {
        super(columnNode);
    }

    protected boolean isAllowedForTrimming() {
        Node parent = getParent();
        while(parent != null) {
            if(parent.getType() == NodeType.JOIN
                    || parent.getType() == NodeType.UPDATE_SET
                    || parent.getType() == NodeType.INSERT_COLUMNS) {
                return false;
            }
            parent = parent.getParent();
        }
        return true;
    }
}
