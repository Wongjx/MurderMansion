SELECT mp.platform, mp.app_version, mp.build_number,
       COUNT(DISTINCT m.match_id) AS matches_started,
       COUNT(DISTINCT CASE WHEN m.ended_at IS NOT NULL OR m.score_screen_reached = 1 THEN m.match_id END) AS matches_concluded
FROM matches m
JOIN match_participants mp ON mp.match_id = m.match_id
GROUP BY mp.platform, mp.app_version, mp.build_number
ORDER BY matches_started DESC;
