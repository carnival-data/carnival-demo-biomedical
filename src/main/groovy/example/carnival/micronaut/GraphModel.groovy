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
                PX.END,
                PX.PATIENT,
                PX.ENCOUNTER,
                PX.CODE,
                PX.DESCRIPTION
                //PX.REASON_CODE,
                //PX.REASON_DESCRIPTION
            ]
        ),

        CONDITION(
            propertyDefs:[
                PX.START.withConstraints(required:true),
                PX.END.withConstraints(required:true),
                PX.PATIENT,
                PX.CODE,
                PX.DESCRIPTION
            ]
        ),

        //ENCOUNTER(PXEncounter),

        // MEDICATION(PXMedication),
        ENCOUNTER(
            propertyDefs:[
                PX.ID,
                PX.START,
                PX.END,
                
                PX_ENCOUNTER.CLASS,
                PX.CODE,
                PX.DESCRIPTION,
                PX_ENCOUNTER.BASE_ENCOUNTER_COST,
                PX_ENCOUNTER.TOTAL_CLAIM_COST,
                PX.PAYER_COVERAGE,
                PX_ENCOUNTER.REASON_CODE,
                PX_ENCOUNTER.REASON_DESCRIPTION
            ]
        ),

        PATIENT(
            propertyDefs:[
                PX.ID.withConstraints(index:true, required:true),
            
                PX_PATIENT.BIRTH_DATE,
                PX_PATIENT.DEATH_DATE,
                PX_PATIENT.FIRST_NAME,
                PX_PATIENT.LAST_NAME,
                PX_PATIENT.LATITUDE,
                PX_PATIENT.LONGITUDE
            ]
        ),

        SURVEY(
            propertyDefs: [
//              PX.ID.withConstraints(index: true, required: true), // Generate unique id?

                PX_SURVEY.DATE,             // 2012-05-04T15:30:18Z

                PX.CODE,             // 72166-2
                PX.DESCRIPTION,      // Tobacco smoking status NHIS

                // Idea: use type to make one of two optional fields
                PX_SURVEY.RESPONSE_NUMERIC, // 9.3
                PX_SURVEY.RESPONSE_TEXT     // Never smoker
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
        PATIENT,
        ENCOUNTER,
        
        CODE,
        DESCRIPTION,
        PAYER_COVERAGE,

        // doggie properties
        IS_ADORABLE,
        TEXT
    }
    
    @PropertyDefinition
    static enum PX_PATIENT {
        BIRTH_DATE,
        DEATH_DATE,
        FIRST_NAME,
        LAST_NAME,
        LATITUDE,
        LONGITUDE
    }

    @PropertyDefinition
    static enum PX_ENCOUNTER {
        CLASS,
        BASE_ENCOUNTER_COST,
        TOTAL_CLAIM_COST,
        REASON_CODE,
        REASON_DESCRIPTION
    }

    @PropertyDefinition
    static enum PX_SURVEY {
        DATE,
        RESPONSE_NUMERIC,
        RESPONSE_TEXT
    }
    /*@PropertyDefinition
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
    }*/
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