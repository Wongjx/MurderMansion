SELECT platform, app_version, build_number, COUNT(*) AS crashes
FROM crash_reports
GROUP BY platform, app_version, build_number
ORDER BY crashes DESC;
