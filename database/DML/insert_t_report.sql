DELETE t_report;
INSERT INTO t_report( applicant_id, target_ym, applicant_date, status, approver1_id, approver2_id, approver3_id, file_path, del_flg, ins_date, ins_id, upd_date, upd_id) VALUES ( 'user01', '201601', '2016-01-25', '100', 'user02', 'user03', 'user04', '月報.xlsx', 0, now(), 'system', now(), 'system');
INSERT INTO t_report( applicant_id, target_ym, applicant_date, status, approver1_id, approver2_id, approver3_id, file_path, del_flg, ins_date, ins_id, upd_date, upd_id) VALUES ( 'user01', '201602', '2016-02-25', '900', 'user02', 'user03', 'user04', '月報.xlsx', 0, now(), 'system', now(), 'system');
INSERT INTO t_report( applicant_id, target_ym, applicant_date, status, approver1_id, approver2_id, approver3_id, file_path, del_flg, ins_date, ins_id, upd_date, upd_id) VALUES ( 'user01', '201603', '2016-03-25', 'N03', 'user02', 'user03', 'user04', '月報.xlsx', 0, now(), 'system', now(), 'system');
INSERT INTO t_report( applicant_id, target_ym, applicant_date, status, approver1_id, approver2_id, approver3_id, file_path, del_flg, ins_date, ins_id, upd_date, upd_id) VALUES ( 'user01', '201604', '2016-04-25', 'Y03', 'user02', 'user03', 'user04', '月報.xlsx', 0, now(), 'system', now(), 'system');
INSERT INTO t_report( applicant_id, target_ym, applicant_date, status, approver1_id, approver2_id, approver3_id, file_path, del_flg, ins_date, ins_id, upd_date, upd_id) VALUES ( 'user01', '201605', '2016-05-25', 'Y02', 'user02', 'user03', 'user04', '月報.xlsx', 0, now(), 'system', now(), 'system');
INSERT INTO t_report( applicant_id, target_ym, applicant_date, status, approver1_id, approver2_id, approver3_id, file_path, del_flg, ins_date, ins_id, upd_date, upd_id) VALUES ( 'user01', '201606', '2016-06-25', 'Y01', 'user02', 'user03', 'user04', '月報.xlsx', 0, now(), 'system', now(), 'system');
commit;
