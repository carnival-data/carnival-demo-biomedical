package example.carnival.micronaut



import javax.inject.Singleton

import groovy.transform.CompileStatic

import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

import carnival.graph.VertexDefTrait
import carnival.graph.PropertyDefTrait
import carnival.graph.EdgeDefTrait
import carnival.core.graph.Core



/**
 * The locallyu defined graph model.
 *
 */
class GraphModel {

    static enum VX implements VertexDefTrait {
        PERSON (
            vertexProperties:[
                PX.ID.withConstraints(unique:true),
                Core.PX.NAME
            ]
        ), 
        HAIR (vertexProperties:[PX.COLOR]), 
        EYE (vertexProperties:[PX.COLOR])

        private VX() {}
        private VX(Map m) {m.each { k,v -> this."$k" = v }}
    }


    static enum PX implements PropertyDefTrait {
        COLOR,
        ID
    }


    static enum EX implements EdgeDefTrait {
        IS_FRIENDS_WITH (
            domain:[VX.PERSON],
            range:[VX.PERSON]
        ),
        CONSIDERS_A_FRIEND (
            domain:[VX.PERSON],
            range:[VX.PERSON]
        ),
    }

}