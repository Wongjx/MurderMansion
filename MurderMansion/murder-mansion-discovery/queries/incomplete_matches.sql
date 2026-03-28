SELECT end_reason, COUNT(*) AS matches
FROM v_match_outcomes
WHERE concluded = 0
GROUP BY end_reason
ORDER BY matches DESC;
