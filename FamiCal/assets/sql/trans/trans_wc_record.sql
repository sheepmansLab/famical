INSERT INTO wc_record (wc_record_date, family_id, pe_count, po_count, comment) SELECT wc_record_date, family_id, pe_count, po_count, comment FROM wc_record_tmp
/