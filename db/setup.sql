CREATE TABLE procedures (
    procedure_date      DATE,        -- 2013-10-06
    patient             VARCHAR(36), -- 684c92a9-7306-4365-9886-8092e7692ea4
    encounter           VARCHAR(36), -- 1d794fe4-389d-4be7-ac05-9b80e5dd3fd9
    code                TEXT,        -- 428191000124101
    description         TEXT,        -- Documentation of current medications
    cost                MONEY,       -- 586.51
    reason_code         TEXT,        -- 10509002, may be null
    reason_description  TEXT         -- Acute bronchitis (disorder), may be null
);

CREATE TABLE organizations (
    id                 VARCHAR(36), -- fcfe244e-8fd6-3bad-8d9d-c3fcdd174834
    name               TEXT,        -- KENSINGTON HOSPITAL
    address            TEXT,        -- 136 W DIAMOND STREET
    city               TEXT,        -- PHILADELPHIA
    state              TEXT,        -- PA
    zip                TEXT,        -- 19122
    phone              TEXT,        -- 2154268100
    utilization        TEXT         -- 17104
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
    utilization        TEXT         -- 17104
);

CREATE TABLE encounters (
    id                 VARCHAR(36), -- 0471ea1c-cdd4-4563-8bf8-4017960c492b
    start              TIMESTAMPTZ,   -- 2011-07-03T07:11:24Z
    stop               TIMESTAMPTZ,   -- 2011-07-03T07:26:24Z

    -- start              TIMESTAMP,   -- 2011-07-03T07:11:24Z
    -- stop               TIMESTAMP,   -- 2011-07-03T07:26:24Z

    patient            VARCHAR(36), -- 684c92a9-7306-4365-9886-8092e7692ea4
    provider           VARCHAR(36), -- fcfe244e-8fd6-3bad-8d9d-c3fcdd174834
    encounter_class    TEXT,        -- wellness
    code               TEXT,        -- 185349003
    description        TEXT,        -- Encounter for check up (procedure)
    cost               MONEY,       -- 105.37
    reason_code        TEXT,        -- 444814009, may be null
    reason_description TEXT         -- Viral sinusitis (disorder), may be null
);

CREATE TABLE medications (
    start              DATE,        -- 2013-09-16
    stop               DATE,        -- 2013-09-23
    patient            VARCHAR(36), -- 8c1709db-6d73-4e04-a8a9-25702eeff152
    encounter          VARCHAR(36), -- bf2fd769-8e00-47b6-89eb-88354c0bb4f4
    code               TEXT,        -- 562251
    description        TEXT,        -- Amoxicillin 250 MG / Clavulanate 125 MG Oral Tablet
    cost               MONEY,       -- 23.93
    dispenses          SMALLINT,    -- 12
    total_cost         MONEY,       -- 23.93
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


COPY careplans(id, start, stop, patient, encounter, code, description, reason_code, reason_description) 
FROM '/db/careplans.csv'
DELIMITER ','
CSV HEADER;

COPY medications(start, stop, patient, encounter, code, description, cost, dispenses, total_cost, reason_code, reason_description) 
FROM '/db/medications.csv'
DELIMITER ','
CSV HEADER;

COPY encounters(id, start, stop, patient, provider, encounter_class, code, description, cost, reason_code, reason_description) 
FROM '/db/encounters.csv'
DELIMITER ','
CSV HEADER;

COPY procedures(procedure_date, patient, encounter, code, description, cost, reason_code, reason_description) 
FROM '/db/procedures.csv'
DELIMITER ','
CSV HEADER;

COPY organizations(id, name, address, city, state, zip, phone, utilization) 
FROM '/db/organizations.csv'
DELIMITER ','
CSV HEADER;

COPY conditions(start, stop, patient, encounter, code, description) 
FROM '/db/conditions.csv'
DELIMITER ','
CSV HEADER;

COPY providers(id, organization, name, gender, specialty, address, city, state, zip, utilization) 
FROM '/db/providers.csv'
DELIMITER ','
CSV HEADER;


