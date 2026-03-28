SELECT date(started_at/1000, 'unixepoch') AS day, COUNT(*) AS matches_concluded
FROM matches
WHERE ended_at IS NOT NULL OR score_screen_reached = 1
GROUP BY day
ORDER BY day DESC;
