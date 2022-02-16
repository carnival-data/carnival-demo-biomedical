Derived from synthea output. Contains entries from the observations table with the category "survey".


| Column | Name | Data | Type |Required? | Description |
| -------|------|------|------|----------|-------------|
| Date |	iso8601 UTC Date (yyyy-MM-dd'T'HH:mm'Z') |	true |	The date and time the observation was performed. |
| :old_key: |	Patient |	UUID |	true |	Foreign key to the Patient. |
| Code |	String |	true |	Observation or Lab code from LOINC |
| Description |	String |	true |	Description of the observation or lab. |
| Value |	String |	true |	The recorded value of the observation. |
| Units |	String |	false |	The units of measure for the value. |
| Type |	String |	true |	The datatype of Value: text or numeric |

Original data dictionary located here:
https://github.com/synthetichealth/synthea/wiki/CSV-File-Data-Dictionary#observations