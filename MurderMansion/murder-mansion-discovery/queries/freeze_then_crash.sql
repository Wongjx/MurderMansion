SELECT c.platform, c.build_number, COUNT(*) AS freeze_then_crash_sessions
FROM crash_reports c
JOIN telemetry_events e ON e.session_id = c.session_id
WHERE e.event_type = 'freeze_watchdog'
  AND e.occurred_at <= c.occurred_at
GROUP BY c.platform, c.build_number
ORDER BY freeze_then_crash_sessions DESC;
