SELECT role, platform,
       SUM(CASE WHEN reached_score_screen = 1 THEN 1 ELSE 0 END) AS reached_score,
       SUM(CASE WHEN reached_score_screen = 0 THEN 1 ELSE 0 END) AS did_not_reach_score
FROM match_participants
GROUP BY role, platform;
