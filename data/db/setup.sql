CREATE TABLE patients (
    id                  VARCHAR(36), -- fcfe244e-8fd6-3bad-8d9d-c3fcdd174834
    birthdate           DATE,
    deathdate           DATE,
    ssn                 TEXT,
    drivers             TEXT,
    passport            TEXT,
    prefix              TEXT,
    first               TEXT,
    last                TEXT,
    suffix              TEXT,
    maiden              TEXT,
    marital             TEXT,
    race                TEXT,
    ethnicity           TEXT,
    gender              TEXT,
    birthplace          TEXT,
    address             TEXT,
    city                TEXT,
    state               TEXT,
    county              TEXT,
    zip                 INTEGER,
    lat                 DECIMAL,   -- 39.98525733
    lon                 DECIMAL,   -- -75.04579348
    healthcare_expenses DECIMAL,
    healthcare_coverage DECIMAL
);

CREATE TABLE procedures (
    start              TIMESTAMPTZ,   -- 2011-07-03T07:11:24Z
    stop               TIMESTAMPTZ,   -- 2011-07-03T07:26:24Z
    patient             VARCHAR(36), -- 684c92a9-7306-4365-9886-8092e7692ea4
    encounter           VARCHAR(36), -- 1d794fe4-389d-4be7-ac05-9b80e5dd3fd9
    code                TEXT,        -- 428191000124101
    description         TEXT,        -- Documentation of current medications
    base_cost           DECIMAL,       -- 586.51
    reason_code         TEXT,        -- 10509002, may be null
    reason_description  TEXT         -- Acute bronchitis (disorder), may be null
);

CREATE TABLE providers (
    id                 VARCHAR(36), -- 7817891f-7945-4f3d-b3dc-68df38b908cd
    organization       VARCHAR(36), -- fcfe244e-8fd6-3bad-8d9d-c3fcdd174834
    name               TEXT,        -- Keeley419 Yundt842
    gender             TEXT,        -- F
    specialty          TEXT,        -- GENERAL PRACTICE
    address            TEXT,        -- 136 W DIAMOND STREET
    city               TEXT,        -- PHILADELPHIA
    state              TEXT,        -- PA
    zip                TEXT,        -- 19122
    lat                 DECIMAL,   -- 39.98525733
    lon                 DECIMAL,   -- -75.04579348
    utilization        TEXT         -- 17104
);

CREATE TABLE encounters (
    id                 VARCHAR(36), -- 0471ea1c-cdd4-4563-8bf8-4017960c492b
    start              TIMESTAMPTZ,   -- 2011-07-03T07:11:24Z
    stop               TIMESTAMPTZ,   -- 2011-07-03T07:26:24Z

    -- start              TIMESTAMP,   -- 2011-07-03T07:11:24Z
    -- stop               TIMESTAMP,   -- 2011-07-03T07:26:24Z

    patient            VARCHAR(36), -- 684c92a9-7306-4365-9886-8092e7692ea4
    organization       VARCHAR(36), -- ba8bc7a5-9141-3011-a936-000cd23afd59
    provider           VARCHAR(36), -- fcfe244e-8fd6-3bad-8d9d-c3fcdd174834
    payer              VARCHAR(36), -- fcfe244e-8fd6-3bad-8d9d-c3fcdd174834
    encounter_class    TEXT,        -- wellness
    code               TEXT,        -- 185349003
    description        TEXT,        -- Encounter for check up (procedure)
    base_encounter_cost DECIMAL,       -- 105.37
    total_claim_cost   DECIMAL,       -- 105.37
    payer_coverage     DECIMAL,       -- 105.37
    reason_code        TEXT,        -- 444814009, may be null
    reason_description TEXT         -- Viral sinusitis (disorder), may be null
);

CREATE TABLE medications (
    start              DATE,        -- 2013-09-16
    stop               DATE,        -- 2013-09-23
    patient            VARCHAR(36), -- 8c1709db-6d73-4e04-a8a9-25702eeff152
    payer              VARCHAR(36), -- b1c428d6-4f07-31e0-90f0-68ffa6ff8c76
    encounter          VARCHAR(36), -- bf2fd769-8e00-47b6-89eb-88354c0bb4f4
    code               TEXT,        -- 562251
    description        TEXT,        -- Amoxicillin 250 MG / Clavulanate 125 MG Oral Tablet
    base_cost          DECIMAL,       -- 23.93
    payer_coverage     DECIMAL,
    dispenses          SMALLINT,    -- 12
    total_cost         DECIMAL,       -- 23.93
    reason_code        TEXT,        -- 444814009, may be null
    reason_description TEXT         -- Viral sinusitis (disorder), may be null    
);

CREATE TABLE careplans (
    id                 VARCHAR(36), -- 45b2e6d1-7ac1-429d-9592-fdd4fa39c9b1
    start              DATE,        -- 2013-09-16
    stop               DATE,        -- 2013-09-23, may be null
    patient            VARCHAR(36), -- 8bf50347-450f-4158-8d86-5354c20659a3
    encounter          VARCHAR(36), -- 7b49412a-26c2-4274-9473-22fe11303f74
    code               TEXT,        -- 698360004
    description        TEXT,        -- Diabetes self management plan
    reason_code        TEXT,        -- 444814009, may be null
    reason_description TEXT         -- Viral sinusitis (disorder), may be null
);

CREATE TABLE conditions (
    start              DATE,        -- 2011-07-03
    stop               DATE,        -- 2011-07-04, may be null
    patient            VARCHAR(36), -- 684c92a9-7306-4365-9886-8092e7692ea4
    encounter          VARCHAR(36), -- 0471ea1c-cdd4-4563-8bf8-4017960c492b
    code               TEXT,        -- 162864005
    description        TEXT         -- Body mass index 30+ - obesity (finding)
);



COPY patients(id, birthdate, deathdate, ssn, drivers, passport, prefix, first, last, suffix, maiden, marital, race, ethnicity, gender, birthplace, address, city, state, county, zip, lat, lon, healthcare_expenses, healthcare_coverage) 
FROM '/db/patients.csv'
DELIMITER ','
CSV HEADER;

COPY careplans(id, start, stop, patient, encounter, code, description, reason_code, reason_description) 
FROM '/db/careplans.csv'
DELIMITER ','
CSV HEADER;

COPY medications(start, stop, patient, payer, encounter, code, description, base_cost, payer_coverage, dispenses, total_cost, reason_code, reason_description) 
FROM '/db/medications.csv'
DELIMITER ','
CSV HEADER;

COPY encounters(id, start, stop, patient, organization, provider, payer, encounter_class, code, description, base_encounter_cost, total_claim_cost, payer_coverage, reason_code, reason_description) 
FROM '/db/encounters.csv'
DELIMITER ','
CSV HEADER;

COPY procedures(start, stop, patient, encounter, code, description, base_cost, reason_code, reason_description) 
FROM '/db/procedures.csv'
DELIMITER ','
CSV HEADER;

COPY conditions(start, stop, patient, encounter, code, description) 
FROM '/db/conditions.csv'
DELIMITER ','
CSV HEADER;

COPY providers(id, organization, name, gender, specialty, address, city, state, zip, lat, lon, utilization) 
FROM '/db/providers.csv'
DELIMITER ','
CSV HEADER;


