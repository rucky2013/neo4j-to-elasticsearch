/*
 * Copyright (c) 2013-2016 GraphAware
 *
 * This file is part of the GraphAware Framework.
 *
 * GraphAware Framework is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.graphaware.module.es.mapping;

import com.graphaware.common.log.LoggerFactory;
import com.graphaware.module.es.mapping.expression.NodeExpressions;
import com.graphaware.module.es.mapping.expression.RelationshipExpressions;
import io.searchbox.action.BulkableAction;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.logging.Log;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * This mapping indexes all documents in the same ElasticSearch index.
 *
 * Node's neo4j labels are stored in "_labels" field.
 * Relationships's neo4j type is stored in "_type" field.
 */
public class AdvancedMapping extends DefaultMapping {
    private static final Log LOG = LoggerFactory.getLogger(AdvancedMapping.class);

    public static final String NODE_TYPE = "node";
    public static final String RELATIONSHIP_TYPE = "relationship";
    public static final String LABELS_FIELD = "_labels";
    public static final String RELATIONSHIP_FIELD = "_relationship";

    @Override
    protected List<BulkableAction<? extends JestResult>> createOrUpdateNode(NodeExpressions node) {
        Map<String, Object> source = map(node);
        List<BulkableAction<? extends JestResult>> actions = new ArrayList<>();
        actions.add(new Index.Builder(source).index(getIndexFor(Node.class)).type(NODE_TYPE).id(getKey(node)).build());
        return actions;
    }

    @Override
    protected List<BulkableAction<? extends JestResult>> createOrUpdateRelationship(RelationshipExpressions r) {
        return Collections.singletonList(
                new Index.Builder(map(r)).index(getIndexFor(Relationship.class)).type(RELATIONSHIP_TYPE).id(getKey(r)).build()
        );
    }
     
    protected void addExtra(Map<String, Object> data, NodeExpressions node) {
        data.put(LABELS_FIELD, Arrays.asList(node.getLabels()));
    }
    
    protected void addExtra(Map<String, Object> data, RelationshipExpressions relationship) {
        data.put(RELATIONSHIP_FIELD, relationship.getType());
    }

}
