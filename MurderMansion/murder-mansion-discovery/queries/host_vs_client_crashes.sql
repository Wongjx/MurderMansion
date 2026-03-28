SELECT role, kind, COUNT(*) AS crashes
FROM crash_reports
GROUP BY role, kind
ORDER BY crashes DESC;
