/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.apache.cayenne.map;

import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.util.XMLEncoder;

import java.util.Map;

/**
 * @since 4.0
 */
public class EJBQLQueryDescriptor extends QueryDescriptor {

    protected String ejbql;

    public EJBQLQueryDescriptor() {
        super(EJBQL_QUERY);
    }

    /**
     * Returns EJBQL query string for this query.
     */
    public String getEjbql() {
        return ejbql;
    }

    /**
     * Sets EJBQL query string for this query.
     */
    public void setEjbql(String ejbql) {
        this.ejbql = ejbql;
    }

    @Override
    public EJBQLQuery buildQuery() {
        EJBQLQuery ejbqlQuery = new EJBQLQuery(this.getEjbql());
        ejbqlQuery.setName(this.getName());
        ejbqlQuery.setDataMap(dataMap);
        ejbqlQuery.initWithProperties(this.getProperties());

        return ejbqlQuery;
    }

    @Override
    public void encodeAsXML(XMLEncoder encoder) {
        encoder.print("<query name=\"");
        encoder.print(getName());
        encoder.print("\" type=\"");
        encoder.print(type);
        encoder.println("\">");

        encoder.indent(1);

        // print properties
        encodeProperties(encoder);

        if (ejbql != null) {
            encoder.print("<ejbql><![CDATA[");
            encoder.print(ejbql);
            encoder.println("]]></ejbql>");
        }

        encoder.indent(-1);
        encoder.println("</query>");
    }
}
