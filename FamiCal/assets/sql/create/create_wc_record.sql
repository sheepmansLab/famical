CREATE TABLE wc_record (
      wc_record_date TEXT
	, family_id INTEGER
	, pe_count INTEGER
	, po_count INTEGER
	, comment TEXT
	, PRIMARY KEY(family_id, wc_record_date))
/