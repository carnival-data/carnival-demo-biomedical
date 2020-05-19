package example.carnival.micronaut



import javax.inject.Singleton

import groovy.transform.CompileStatic

import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

import carnival.graph.VertexDefinition
import carnival.graph.PropertyDefinition
import carnival.graph.EdgeDefinition
import carnival.core.graph.Core



/**
 * The locallyu defined graph model.
 *
 */
class GraphModel {

    @VertexDefinition
    static enum VX {
        PERSON (
            global:true,
            vertexProperties:[
                PX.ID.withConstraints(unique:true, index:true),
                Core.PX.NAME.withConstraints(required:true)
            ]
        ), 
        HAIR (vertexProperties:[PX.COLOR]), 
        EYE (vertexProperties:[PX.COLOR])
    }

    @PropertyDefinition
    static enum PX {
        COLOR,
        ID
    }

    @EdgeDefinition
    static enum EX {
        IS_FRIENDS_WITH (
            domain:[VX.PERSON],
            range:[VX.PERSON]
        ),
        CONSIDERS_A_FRIEND (
            domain:[VX.PERSON],
            range:[VX.PERSON]
        )
    }

}