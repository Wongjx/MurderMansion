SELECT occurred_at, event_type, role, occupant_id, platform, payload_json
FROM telemetry_events
WHERE match_id = 'YOUR_MATCH_ID'
ORDER BY occurred_at ASC;
