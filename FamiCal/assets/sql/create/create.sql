CREATE TABLE wc_record (
      family_id INTEGER
	, wc_record_date TEXT
	, pe_count INTEGER
	, po_count INTEGER
	, comment TEXT
	, PRIMARY KEY(family_id, wc_record_date))
/
CREATE TABLE family (
      family_id INTEGER PRIMARY KEY
     ,family_name TEXT)
/