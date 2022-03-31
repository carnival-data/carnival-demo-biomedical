package example.carnival.micronaut



import carnival.graph.VertexDefinition
import carnival.graph.PropertyDefinition
import carnival.graph.EdgeDefinition

import carnival.core.graph.Core


class GraphModel {



    @VertexDefinition
    static enum VX {

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

        ENCOUNTER(
            propertyDefs:[
                PX.ID.withConstraints(index:true, required:true),

                PX.START.withConstraints(required:true),
                PX.END.withConstraints(required:true),

                PX_ENCOUNTER.CLASS,
                PX.CODE,
                PX.DESCRIPTION,

                PX_ENCOUNTER.REASON_CODE,
                PX_ENCOUNTER.REASON_DESCRIPTION
            ]
        ),

        CONDITION(
            propertyDefs:[
                PX.ID.withConstraints(index:true, required:true),
                PX.START.withConstraints(required:true),
                PX.END.withConstraints(required:true),
                PX.CODE,
                PX.DESCRIPTION
            ]
        ),

        // ENCOUNTER(PXEncounter),

        // MEDICATION(PXMedication),

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

    /*
    patient has encounters
    encounter has surveys
    encounter has procedures
    encounter has conditions
    encounter has medications
     */

    @EdgeDefinition
    static enum EX {
        HAS,

        // // remove (need to convert any tests?)
        // PATIENT_HAS_ENCOUNTER(
        //     domain:[VX.PATIENT],
        //     range:[VX.ENCOUNTER]
        // ),

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
        // STOP,
        // PATIENT,
        // ENCOUNTER,
         CODE,
         DESCRIPTION,
        // REASON_CODE,
        // REASON_DESCRIPTION,

        // doggie properties
        IS_ADORABLE,
        TEXT
    }

    @PropertyDefinition
    static enum PX_PATIENT {
        // ID,
        BIRTH_DATE,
        DEATH_DATE,
        FIRST_NAME,
        LAST_NAME,
        LATITUDE,
        LONGITUDE
    }

    @PropertyDefinition
    static enum PX_ENCOUNTER {
        // START,
        // END,

        CLASS,
//        CODE,
//        DESCRIPTION,

        REASON_CODE,
        REASON_DESCRIPTION
    }

    @PropertyDefinition
    static enum PX_SURVEY {
        DATE,
//        CODE,
//        DESCRIPTION,
        RESPONSE_NUMERIC,
        RESPONSE_TEXT
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