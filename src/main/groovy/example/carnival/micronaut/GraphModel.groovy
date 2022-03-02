package example.carnival.micronaut



import carnival.graph.VertexDefinition
import carnival.graph.PropertyDefinition
import carnival.graph.EdgeDefinition

import carnival.core.graph.Core


class GraphModel {



    @VertexDefinition
    static enum VX {
        CAREPLAN(
            propertyDefs:[
                PX.ID.withConstraints(index:true, required:true),
                PX.START.withConstraints(required:true),
                PX.STOP.withConstraints(required:true),
                PX.CODE,
                PX.DESCRIPTION,
                PX.REASON_CODE,
                PX.REASON_DESCRIPTION
            ]
        ),

        CONDITION(
            propertyDefs:[
                PX.ID.withConstraints(index:true, required:true),
                PX.START.withConstraints(required:true),
                PX.STOP.withConstraints(required:true),
                PX.CODE,
                PX.DESCRIPTION
            ]
        ),

        //ENCOUNTER(PXEncounter),

        // MEDICATION(PXMedication),
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
        // HAS,
        PATIENT_HAS_ENCOUNTER(
            domain:[VX.PATIENT],
            range:[VX.ENCOUNTER]
        ),
        HAS,
        HAS_BEEN_CALLED(
            domain:[VX.DOGGIE],
            range:[VX.NAME]
        )
    }



    @PropertyDefinition
    static enum PX {
        ID,
        START,
        END,
        STOP,
        PATIENT,
        ENCOUNTER,
        CODE,
        DESCRIPTION,
        REASON_CODE,
        REASON_DESCRIPTION,

        // doggie properties
        IS_ADORABLE,
        TEXT
    }

    @PropertyDefinition
    static enum PXEncounter {
        ID,
        START,
        END,
        PROVIDER,
        ENCOUNTER_CLASS,
        CODE,
        DESCRIPTION,
        COST,
        REASON_CODE,
        REASON_DESCRIPTION
    }
/*
    @PropertyDefinition
    static enum PXMedication {
        START,
        STOP,
        PATIENT,
        ENCOUNTER,
        CODE,
        DESCRIPTION,
        COST,
        DISPENSES,
        TOTAL_COST,
        REASON_CODE,
        REASON_DESCRIPTION
    }
    */
    

    
}