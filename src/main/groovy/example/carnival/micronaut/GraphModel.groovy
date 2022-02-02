package example.carnival.micronaut



import carnival.graph.VertexDefinition
import carnival.graph.PropertyDefinition
import carnival.graph.EdgeDefinition

import carnival.core.graph.Core


class GraphModel {



    @VertexDefinition
    static enum VX {
        ENCOUNTER(
            propertyDefs:[
                PX.ID.withConstraints(index:true, required:true),
                PX.START.withConstraints(required:true),
                PX.END.withConstraints(required:true)
            ]
        ),

        PATIENT(
            propertyDefs:[
                PX.ID.withConstraints(index:true, required:true)
            ]
        ),

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
        PATIENT_HAS_ENCOUNTER(
            domain:[VX.PATIENT],
            range:[VX.ENCOUNTER]
        ),
        HAS_BEEN_CALLED(
            domain:[VX.DOGGIE],
            range:[VX.NAME]
        ),
    }



    @PropertyDefinition
    static enum PX {
        ID,
        START,
        END,
        IS_ADORABLE,
        TEXT
    }

    
}