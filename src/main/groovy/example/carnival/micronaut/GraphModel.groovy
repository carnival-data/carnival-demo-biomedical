package example.carnival.micronaut



import carnival.graph.VertexDefinition
import carnival.graph.PropertyDefinition
import carnival.graph.EdgeDefinition

import carnival.core.graph.Core


class GraphModel {



    @VertexDefinition
    static enum VX {
        DOGGIE(
            propertyDefs:[
                PX.IS_ADORABLE.withConstraints(index:true, required:true),
            ]
        ),

        NAME(
            propertyDefs:[
                PX.TEXT.withConstraints(index:true, unique:true, required:true),
            ]
        )
    }



    @EdgeDefinition
    static enum EX {
        HAS_BEEN_CALLED(
            domain:[VX.DOGGIE],
            range:[VX.NAME]
        ),
    }



    @PropertyDefinition
    static enum PX {
        IS_ADORABLE,
        TEXT
    }

    
}