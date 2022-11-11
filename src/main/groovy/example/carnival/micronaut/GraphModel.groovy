package example.carnival.micronaut


import carnival.graph.VertexModel
import carnival.graph.PropertyModel
import carnival.graph.EdgeModel
import carnival.core.Core


/**
 * The graph model used by this demo application.
 *
 */
class GraphModel {

    @VertexModel
    static enum VX {
        PATIENT([
            propertyDefs:[
                PX.ID.withConstraints(index:true, required:true),
            
                PX_PATIENT.BIRTH_DATE,
                PX_PATIENT.AGE,
                PX_PATIENT.DEATH_DATE,
                PX_PATIENT.FIRST_NAME,
                PX_PATIENT.LAST_NAME,
                PX_PATIENT.LATITUDE,
                PX_PATIENT.LONGITUDE,
                PX_PATIENT.ENCOUNTER_COUNT
            ]
        ]),

        ENCOUNTER([
            propertyDefs:[
                PX.ID.withConstraints(index:true, required:true),
                PX.START,
                PX.END,
                
                PX_ENCOUNTER.CLASS,
                PX.CODE,
                PX.DESCRIPTION,
                
                PX_ENCOUNTER.REASON_CODE,
                PX_ENCOUNTER.REASON_DESCRIPTION
            ]
        ]),

        CONDITION([
            propertyDefs:[
                PX.START.withConstraints(required:true),
                PX.END,
                PX.CODE,
                PX.DESCRIPTION
            ]
        ]),

        MEDICATION([
            propertyDefs:[
                PX.START.withConstraints(required:true),
                PX.END,
                
                PX_MEDICATION.COST,
                PX_MEDICATION.DISPENSES,
                PX_MEDICATION.TOTAL_COST,

                PX.CODE,
                PX.DESCRIPTION,
                PX_MEDICATION.REASON_CODE,
                PX_MEDICATION.REASON_DESCRIPTION
            ]
        ]),
        
        SURVEY([
            propertyDefs: [
                PX_SURVEY.DATE,             // 2012-05-04T15:30:18Z

                PX.CODE,             // 72166-2
                PX.DESCRIPTION,      // Tobacco smoking status NHIS

                PX_SURVEY.RESPONSE_NUMERIC, // 9.3
                PX_SURVEY.RESPONSE_TEXT,     // Never smoker
                
                PX_SURVEY.RESPONSE_UNIT    
            ]
        ]),

        COHORT_PATIENTS([propertyDefs:[]]),

        CONTROL_PATIENTS([propertyDefs:[]])
        
    }

    @EdgeModel
    static enum EX {
        HAS,

        DIAGNOSED_WITH([
            domain:[VX.PATIENT],
            range:[VX.CONDITION]
        ]),
        DIAGNOSED_AT([
            domain:[VX.ENCOUNTER],
            range:[VX.CONDITION]
        ]),
        SELF_REPORTED([
            domain:[VX.PATIENT],
            range:[VX.SURVEY]
        ]),
        PRESCRIBED([
            domain:[VX.PATIENT],
            range:[VX.MEDICATION]
        ]),
        PRESCRIBED_AT([
            domain:[VX.ENCOUNTER],
            range:[VX.MEDICATION]
        ])
        
    }

    @PropertyModel
    static enum PX {
        ID,
        START,
        END,        
        CODE,
        DESCRIPTION
    }

    @PropertyModel
    static enum PX_PATIENT {
        BIRTH_DATE,
        AGE,
        DEATH_DATE,
        FIRST_NAME,
        LAST_NAME,
        LATITUDE,
        LONGITUDE,
        ENCOUNTER_COUNT
    }

    @PropertyModel
    static enum PX_ENCOUNTER {
        CLASS,
        REASON_CODE,
        REASON_DESCRIPTION
    }

    @PropertyModel
    static enum PX_SURVEY {
        DATE,
        RESPONSE_NUMERIC,
        RESPONSE_TEXT,
        RESPONSE_UNIT
    }

    @PropertyModel
    static enum PX_MEDICATION {
        COST,
        DISPENSES,
        TOTAL_COST,
        REASON_CODE,
        REASON_DESCRIPTION
    }

}
