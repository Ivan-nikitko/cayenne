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

package org.apache.cayenne.wocompat;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionException;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.exp.ExpressionParameter;
import org.apache.cayenne.exp.parser.ASTObjPath;
import org.apache.cayenne.map.Entity;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.PrefetchTreeNode;
import org.apache.cayenne.query.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A descriptor of SelectQuery loaded from EOModel. It is an informal
 * "decorator" of Cayenne SelectQuery to provide access to the extra information
 * of WebObjects EOFetchSpecification.
 * 
 * @since 1.1
 * @since 5.0 this query extends {@link ObjectSelect}
 */
@SuppressWarnings("unchecked")
public class EOQuery<T> extends ObjectSelect<T> {

	protected Map<String, ?> plistMap;
	protected Map<String, String> bindings;

	public EOQuery(ObjEntity root, Map<String, ?> plistMap) {
		super();
		entityName(root.getName());
		setRoot(root);
		this.plistMap = plistMap;
		initFromPlist(plistMap);
	}

	protected void initFromPlist(Map<String, ?> plistMap) {

		if("YES".equalsIgnoreCase((String) plistMap.get("usesDistinct"))) {
			distinct();
		}

		Object fetchLimit = plistMap.get("fetchLimit");
		if (fetchLimit != null) {
			try {
				if (fetchLimit instanceof Number) {
					limit(((Number) fetchLimit).intValue());
				} else {
					limit(Integer.parseInt(fetchLimit.toString()));
				}
			} catch (NumberFormatException nfex) {
				// ignoring...
			}
		}

		// sort orderings
		List<Map<String, String>> orderings = (List<Map<String, String>>) plistMap.get("sortOrderings");
		if (orderings != null && !orderings.isEmpty()) {
			for (Map<String, String> ordering : orderings) {
				boolean asc = !"compareDescending:".equals(ordering.get("selectorName"));
				String key = ordering.get("key");
				if (key != null) {
					orderBy(key, asc ? SortOrder.ASCENDING : SortOrder.DESCENDING);
				}
			}
		}

		// qualifiers
		Map<String, ?> qualifierMap = (Map<String, ?>) plistMap.get("qualifier");
		if (qualifierMap != null && !qualifierMap.isEmpty()) {
			where(makeQualifier(qualifierMap));
		}

		// prefetches
		List<?> prefetches = (List<?>) plistMap.get("prefetchingRelationshipKeyPaths");
		if (prefetches != null && !prefetches.isEmpty()) {
			for (Object prefetch : prefetches) {
				prefetch((String) prefetch, PrefetchTreeNode.UNDEFINED_SEMANTICS);
			}
		}

		// data rows - note that we do not support fetching individual columns
		// in the
		// modeler...
		if (plistMap.containsKey("rawRowKeyPaths")) {
			fetchDataRows();
		}
	}

	public Collection<String> getBindingNames() {
		if (bindings == null) {
			initBindings();
		}

		return bindings.keySet();
	}

	public String bindingClass(String name) {
		if (bindings == null) {
			initBindings();
		}

		return bindings.get(name);
	}

	private synchronized void initBindings() {
		if (bindings != null) {
			return;
		}

		bindings = new HashMap<>();

		if (!(root instanceof Entity)) {
			return;
		}

		Map<String, ?> qualifier = (Map<String, ?>) plistMap.get("qualifier");
		initBindings(bindings, (Entity<?,?,?>) root, qualifier);
	}

	private void initBindings(Map<String,String> bindings, Entity<?,?,?> entity, Map<String, ?> qualifier) {
		if (qualifier == null) {
			return;
		}

		if ("EOKeyValueQualifier".equals(qualifier.get("class"))) {
			String key = (String) qualifier.get("key");
			if (key == null) {
				return;
			}

			Object value = qualifier.get("value");
			if (!(value instanceof Map)) {
				return;
			}

			Map<String, ?> valueMap = (Map<String, ?>) value;
			if (!"EOQualifierVariable".equals(valueMap.get("class")) || !valueMap.containsKey("_key")) {
				return;
			}

			String name = (String) valueMap.get("_key");
			String className = null;

			// we don't know whether its obj path or db path, so the expression
			// can blow
			// ... in fact we can't support DB Path as the key is different from
			// external
			// name,
			// so we will use Object type for all DB path...
			try {
				Object lastObject = new ASTObjPath(key).evaluate(entity);

				if (lastObject instanceof ObjAttribute) {
					className = ((ObjAttribute) lastObject).getType();
				} else if (lastObject instanceof ObjRelationship) {
					ObjEntity target = ((ObjRelationship) lastObject).getTargetEntity();
					if (target != null) {
						className = target.getClassName();
					}
				}
			} catch (ExpressionException ex) {
				className = "java.lang.Object";
			}

			if (className == null) {
				className = "java.lang.Object";
			}

			bindings.put(name, className);

			return;
		}

		List<Map<String, ?>> children = (List<Map<String, ?>>) qualifier.get("qualifiers");
		if (children != null) {
			for (Map<String, ?> child : children) {
				initBindings(bindings, entity, child);
			}
		}
	}

	/**
	 * Creates the Expression equivalent of the EOFetchSpecification represented
	 * by the Map.
	 * 
	 * @param qualifierMap
	 *            - FetchSpecification to translate
	 * @return Expression equivalent to FetchSpecification
	 */
	public synchronized Expression makeQualifier(Map<String, ?> qualifierMap) {
		if (qualifierMap == null) {
			return null;
		}

		return EOFetchSpecificationParser.makeQualifier((EOObjEntity) getRoot(), qualifierMap);
	}

	/**
	 * EOFetchSpecificationParser parses EOFetchSpecifications from a
	 * WebObjects-style EOModel. It recursively builds Cayenne Expression
	 * objects and assembles them into the final aggregate Expression.
	 */
	static class EOFetchSpecificationParser {

		// Xcode/EOModeler expressions have a colon at the end of the selector
		// name
		// (just like standard Objective-C syntax). WOLips does not. Add both
		// sets to the hash map to handle both types of models.

		// Selector strings (Java-base).
		static final String IS_EQUAL_TO = "isEqualTo";
		static final String IS_NOT_EQUAL_TO = "isNotEqualTo";
		static final String IS_LIKE = "isLike";
		static final String CASE_INSENSITIVE_LIKE = "isCaseInsensitiveLike";
		static final String IS_LESS_THAN = "isLessThan";
		static final String IS_LESS_THAN_OR_EQUAL_TO = "isLessThanOrEqualTo";
		static final String IS_GREATER_THAN = "isGreaterThan";
		static final String IS_GREATER_THAN_OR_EQUAL_TO = "isGreaterThanOrEqualTo";

		private static final String OBJ_C = ":"; // Objective-C syntax addition.

		private static Map<String, Integer> selectorToExpressionBridge;
		private static final Logger logger = LoggerFactory.getLogger(EOFetchSpecificationParser.class);

		/**
		 * selectorToExpressionBridge is just a mapping of EOModeler's selector
		 * types to Cayenne Expression types.
		 * 
		 * @return HashMap of Expression types, keyed by the corresponding
		 *         selector name
		 */
		static synchronized Map<String, Integer> selectorToExpressionBridge() {
			// Initialize selectorToExpressionBridge if needed.
			if (null == selectorToExpressionBridge) {
				selectorToExpressionBridge = new HashMap<>();

				selectorToExpressionBridge.put(IS_EQUAL_TO, Expression.EQUAL_TO);
				selectorToExpressionBridge.put(IS_EQUAL_TO + OBJ_C, Expression.EQUAL_TO);

				selectorToExpressionBridge.put(IS_NOT_EQUAL_TO, Expression.NOT_EQUAL_TO);
				selectorToExpressionBridge.put(IS_NOT_EQUAL_TO + OBJ_C, Expression.NOT_EQUAL_TO);

				selectorToExpressionBridge.put(IS_LIKE, Expression.LIKE);
				selectorToExpressionBridge.put(IS_LIKE + OBJ_C, Expression.LIKE);

				selectorToExpressionBridge.put(CASE_INSENSITIVE_LIKE, Expression.LIKE_IGNORE_CASE);
				selectorToExpressionBridge.put(CASE_INSENSITIVE_LIKE + OBJ_C, Expression.LIKE_IGNORE_CASE);

				selectorToExpressionBridge.put(IS_LESS_THAN, Expression.LESS_THAN);
				selectorToExpressionBridge.put(IS_LESS_THAN + OBJ_C, Expression.LESS_THAN);

				selectorToExpressionBridge.put(IS_LESS_THAN_OR_EQUAL_TO, Expression.LESS_THAN_EQUAL_TO);
				selectorToExpressionBridge.put(IS_LESS_THAN_OR_EQUAL_TO + OBJ_C, Expression.LESS_THAN_EQUAL_TO);

				selectorToExpressionBridge.put(IS_GREATER_THAN, Expression.GREATER_THAN);
				selectorToExpressionBridge.put(IS_GREATER_THAN + OBJ_C, Expression.GREATER_THAN);

				selectorToExpressionBridge.put(IS_GREATER_THAN_OR_EQUAL_TO, Expression.GREATER_THAN_EQUAL_TO);
				selectorToExpressionBridge.put(IS_GREATER_THAN_OR_EQUAL_TO + OBJ_C, Expression.GREATER_THAN_EQUAL_TO);
			}

			return selectorToExpressionBridge;
		}

		/**
		 * isAggregate determines whether a qualifier is "aggregate" -- has
		 * children -- or "simple".
		 * 
		 * @param qualifier
		 *            - a Map containing the qualifier settings
		 * @return boolean indicating whether the qualifier is "aggregate"
		 *         qualifier
		 */
		static boolean isAggregate(Map<String, ?> qualifier) {
			boolean result = true;

			String theClass = (String) qualifier.get("class");
			if (theClass == null) {
				return false; // should maybe throw an exception?
			}
			if (theClass.equalsIgnoreCase("EOKeyValueQualifier")
					|| theClass.equalsIgnoreCase("EOKeyComparisonQualifier")) {
				result = false;
			}

			return result;
		}

		/**
		 * expressionTypeForQualifier looks at a qualifier containing the
		 * EOModeler FetchSpecification and returns the equivalent Cayenne
		 * Expression type for its selector.
		 * 
		 * @param qualifierMap
		 *            - a Map containing the qualifier settings to examine.
		 * @return int Expression type
		 */
		static int expressionTypeForQualifier(Map<String, ?> qualifierMap) {
			// get selector
			String selector = (String) qualifierMap.get("selectorName");
			return expressionTypeForSelector(selector);
		}

		/**
		 * expressionTypeForSelector looks at a selector from an EOModeler
		 * FetchSpecification and returns the equivalent Cayenne Expression
		 * type.
		 * 
		 * @param selector
		 *            - a String containing the selector name.
		 * @return int Expression type
		 */
		static int expressionTypeForSelector(String selector) {
			Integer expType = selectorToExpressionBridge().get(selector);
			return (expType != null ? expType : -1);
		}

		/**
		 * aggregateExpressionClassForQualifier looks at a qualifer and returns
		 * the aggregate type: one of Expression.AND, Expression.OR, or
		 * Expression.NOT
		 * 
		 * @param qualifierMap
		 *            - containing the qualifier to examine
		 * @return int aggregate Expression type
		 */
		static int aggregateExpressionClassForQualifier(Map<String, ?> qualifierMap) {
			String qualifierClass = (String) qualifierMap.get("class");
			if (qualifierClass != null) {
				if (qualifierClass.equalsIgnoreCase("EOAndQualifier")) {
					return Expression.AND;
				} else if (qualifierClass.equalsIgnoreCase("EOOrQualifier")) {
					return Expression.OR;
				} else if (qualifierClass.equalsIgnoreCase("EONotQualifier")) {
					return Expression.NOT;
				}
			}

			return -1; // error
		}

		/**
		 * makeQualifier recursively builds an Expression for each condition in
		 * the qualifierMap and assembles from them the complex Expression to
		 * represent the entire EOFetchSpecification.
		 * 
		 * @param qualifierMap
		 *            - Map representation of EOFetchSpecification
		 * @return Expression translation of the EOFetchSpecification
		 */
		static Expression makeQualifier(EOObjEntity entity, Map<String, ?> qualifierMap) {
			if (isAggregate(qualifierMap)) {
				// the fetch specification has more than one qualifier
				int aggregateClass = aggregateExpressionClassForQualifier(qualifierMap); // AND,
				// OR,
				// NOT

				if (aggregateClass == Expression.NOT) {
					// NOT qualifiers only have one child, keyed with
					// "qualifier"
					Map<String, ?> child = (Map<String, ?>) qualifierMap.get("qualifier");
					// build the child expression
					Expression childExp = makeQualifier(entity, child);

					return childExp.notExp(); // add the "not" clause and return
												// the
					// result
				} else {
					// AND, OR qualifiers can have multiple children, keyed with
					// "qualifiers"
					// get the list of children
					List<Map<String, ?>> children = (List<Map<String, ?>>) qualifierMap.get("qualifiers");
					if (children != null) {
						ArrayList<Expression> childExpressions = new ArrayList<>();
						// build an Expression for each child
						for (Map<String, ?> child : children) {
							Expression childExp = makeQualifier(entity, child);
							childExpressions.add(childExp);
						}
						// join the child expressions and return the result
						return ExpressionFactory.joinExp(aggregateClass, childExpressions);
					}
				}

			} // end if isAggregate(qualifierMap)...

			// the query has a single qualifier
			// get expression selector type
			String qualifierClass = (String) qualifierMap.get("class");

			// the key or key path we're comparing
			String key = null;
			// the key, keyPath, value, or parameterized value against which
			// we're
			// comparing the key
			Object comparisonValue = null;

			if ("EOKeyComparisonQualifier".equals(qualifierClass)) {
				// Comparing two keys or key paths
				key = (String) qualifierMap.get("leftValue");
				comparisonValue = qualifierMap.get("rightValue");
				// FIXME: I think EOKeyComparisonQualifier style Expressions are not supported...
				return null;
			} else if ("EOKeyValueQualifier".equals(qualifierClass)) {
				// Comparing key with a value or parameterized value
				key = (String) qualifierMap.get("key");
				Object value = qualifierMap.get("value");

				if (value instanceof Map) {
					Map<String, String> valueMap = (Map<String, String>) value;
					String objClass = valueMap.get("class"); // can be a
					// qualifier class or java type
					if ("EOQualifierVariable".equals(objClass) && valueMap.containsKey("_key")) {
						// make a parameterized expression
						String paramName = valueMap.get("_key");
						comparisonValue = new ExpressionParameter(paramName);
					} else {
						Object queryVal = valueMap.get("value");
						if ("NSNumber".equals(objClass)) {
							// comparison to NSNumber -- cast
							comparisonValue = queryVal;
						} else if ("EONull".equals(objClass)) {
							// comparison to null
							comparisonValue = null;
						} else { // Could there be other types? boolean, date,
									// etc.???
									// no cast
							comparisonValue = queryVal;
						}
					}

				} else if (value instanceof String) {
					// value expression
					comparisonValue = value;
				} // end if (value instanceof Map) else...
			}

			// check whether the key is an object path; if at least one
			// component is not,
			// switch to db path..

			Expression keyExp = ExpressionFactory.exp(key);
			try {
				entity.lastPathComponent(keyExp, Collections.emptyMap());
			} catch (ExpressionException e) {
				try {
					keyExp = entity.translateToDbPath(keyExp);
				} catch (Exception dbpathEx) {
					logger.warn("Couldn't find " + keyExp + " in " + entity.getName() + " in EOModel");
				}
			}

			try {
				Expression exp = ExpressionFactory.expressionOfType(expressionTypeForQualifier(qualifierMap));

				exp.setOperand(0, keyExp);
				exp.setOperand(1, comparisonValue);
				return exp;
			} catch (ExpressionException e) {
				logger.warn(e.getUnlabeledMessage());
				return null;
			}
		}
	}
}
