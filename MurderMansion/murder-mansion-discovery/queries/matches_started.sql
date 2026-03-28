SELECT date(started_at/1000, 'unixepoch') AS day, COUNT(*) AS matches_started
FROM matches
GROUP BY day
ORDER BY day DESC;
