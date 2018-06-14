update RUN_LOG
set end_ts = CURRENT_TIMESTAMP,
	status = 1
where end_ts is NULL