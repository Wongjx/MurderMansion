SELECT crash_id, occurred_at, platform, role, exception_class, message, stacktrace, recent_log_tail
FROM crash_reports
WHERE match_id = 'YOUR_MATCH_ID'
ORDER BY occurred_at DESC;
