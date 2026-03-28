CREATE TABLE IF NOT EXISTS app_sessions (
  session_id TEXT PRIMARY KEY,
  install_id TEXT NOT NULL,
  platform TEXT NOT NULL,
  app_version TEXT NOT NULL,
  build_number TEXT NOT NULL,
  started_at INTEGER NOT NULL,
  ended_at INTEGER,
  end_reason TEXT,
  device_model TEXT,
  os_version TEXT
);

CREATE INDEX IF NOT EXISTS idx_app_sessions_install_started ON app_sessions(install_id, started_at DESC);
CREATE INDEX IF NOT EXISTS idx_app_sessions_platform_build ON app_sessions(platform, build_number);

CREATE TABLE IF NOT EXISTS rooms (
  room_id TEXT PRIMARY KEY,
  room_code TEXT NOT NULL,
  visibility TEXT NOT NULL,
  created_at INTEGER NOT NULL,
  closed_at INTEGER,
  last_phase TEXT NOT NULL,
  host_occupant_id TEXT,
  max_players INTEGER NOT NULL,
  protocol_version INTEGER NOT NULL,
  app_version TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_rooms_code ON rooms(room_code);
CREATE INDEX IF NOT EXISTS idx_rooms_created_at ON rooms(created_at DESC);

CREATE TABLE IF NOT EXISTS matches (
  match_id TEXT PRIMARY KEY,
  room_id TEXT NOT NULL,
  host_occupant_id TEXT NOT NULL,
  started_at INTEGER NOT NULL,
  ended_at INTEGER,
  end_reason TEXT,
  end_source TEXT,
  player_count_expected INTEGER NOT NULL,
  player_count_started INTEGER NOT NULL,
  score_screen_reached INTEGER NOT NULL DEFAULT 0,
  app_version TEXT NOT NULL,
  build_number TEXT NOT NULL,
  protocol_version INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_matches_room_started ON matches(room_id, started_at DESC);
CREATE INDEX IF NOT EXISTS idx_matches_started_at ON matches(started_at DESC);
CREATE INDEX IF NOT EXISTS idx_matches_end_reason ON matches(end_reason);

CREATE TABLE IF NOT EXISTS match_participants (
  match_id TEXT NOT NULL,
  occupant_id TEXT NOT NULL,
  install_id TEXT,
  session_id TEXT,
  role TEXT NOT NULL,
  platform TEXT NOT NULL,
  app_version TEXT NOT NULL,
  build_number TEXT NOT NULL,
  joined_match_at INTEGER NOT NULL,
  left_match_at INTEGER,
  terminal_reason TEXT,
  reached_score_screen INTEGER NOT NULL DEFAULT 0,
  PRIMARY KEY (match_id, occupant_id)
);

CREATE INDEX IF NOT EXISTS idx_match_participants_install ON match_participants(install_id, joined_match_at DESC);
CREATE INDEX IF NOT EXISTS idx_match_participants_platform_build ON match_participants(platform, build_number);
CREATE INDEX IF NOT EXISTS idx_match_participants_terminal_reason ON match_participants(terminal_reason);

CREATE TABLE IF NOT EXISTS telemetry_events (
  event_id TEXT PRIMARY KEY,
  occurred_at INTEGER NOT NULL,
  received_at INTEGER NOT NULL,
  session_id TEXT,
  install_id TEXT,
  room_id TEXT,
  match_id TEXT,
  occupant_id TEXT,
  role TEXT,
  platform TEXT,
  app_version TEXT,
  build_number TEXT,
  event_type TEXT NOT NULL,
  screen_name TEXT,
  payload_json TEXT
);

CREATE INDEX IF NOT EXISTS idx_telemetry_match_time ON telemetry_events(match_id, occurred_at);
CREATE INDEX IF NOT EXISTS idx_telemetry_room_time ON telemetry_events(room_id, occurred_at);
CREATE INDEX IF NOT EXISTS idx_telemetry_install_time ON telemetry_events(install_id, occurred_at DESC);
CREATE INDEX IF NOT EXISTS idx_telemetry_event_type_time ON telemetry_events(event_type, occurred_at DESC);

CREATE TABLE IF NOT EXISTS crash_reports (
  crash_id TEXT PRIMARY KEY,
  occurred_at INTEGER NOT NULL,
  received_at INTEGER NOT NULL,
  session_id TEXT,
  install_id TEXT,
  room_id TEXT,
  match_id TEXT,
  occupant_id TEXT,
  role TEXT,
  platform TEXT NOT NULL,
  app_version TEXT NOT NULL,
  build_number TEXT NOT NULL,
  fatal INTEGER NOT NULL,
  kind TEXT NOT NULL,
  thread_name TEXT,
  exception_class TEXT,
  message TEXT,
  stacktrace TEXT,
  recent_log_tail TEXT,
  metadata_json TEXT
);

CREATE INDEX IF NOT EXISTS idx_crash_match_time ON crash_reports(match_id, occurred_at DESC);
CREATE INDEX IF NOT EXISTS idx_crash_platform_build ON crash_reports(platform, build_number, occurred_at DESC);
CREATE INDEX IF NOT EXISTS idx_crash_kind_time ON crash_reports(kind, occurred_at DESC);

CREATE VIEW IF NOT EXISTS v_match_outcomes AS
SELECT
  m.match_id,
  m.room_id,
  m.started_at,
  m.ended_at,
  CASE WHEN m.ended_at IS NOT NULL OR m.score_screen_reached = 1 THEN 1 ELSE 0 END AS concluded,
  COALESCE(
    m.end_reason,
    CASE
      WHEN EXISTS (
        SELECT 1 FROM crash_reports c
        WHERE c.match_id = m.match_id AND c.role = 'host' AND c.kind = 'uncaught_exception'
      ) THEN 'host_crash'
      WHEN EXISTS (
        SELECT 1 FROM crash_reports c
        WHERE c.match_id = m.match_id AND c.role = 'host' AND c.kind = 'freeze_watchdog'
      ) THEN 'freeze_or_hang'
      WHEN EXISTS (
        SELECT 1 FROM telemetry_events e
        WHERE e.match_id = m.match_id AND e.role = 'host' AND e.event_type = 'app_exit_intent'
      ) THEN 'host_exit'
      WHEN EXISTS (
        SELECT 1 FROM telemetry_events e
        WHERE e.match_id = m.match_id AND e.role = 'host' AND e.event_type = 'relay_disconnected'
      ) THEN 'host_disconnect'
      WHEN EXISTS (
        SELECT 1 FROM crash_reports c
        WHERE c.match_id = m.match_id AND c.role = 'client' AND c.kind = 'uncaught_exception'
      ) THEN 'client_crash'
      WHEN EXISTS (
        SELECT 1 FROM telemetry_events e
        WHERE e.match_id = m.match_id AND e.event_type = 'disconnect_detected'
      ) THEN 'client_disconnect'
      ELSE 'timeout_unknown'
    END
  ) AS end_reason,
  m.build_number AS host_build,
  m.player_count_started
FROM matches m;

CREATE VIEW IF NOT EXISTS v_crash_summary_by_build AS
SELECT platform, app_version, build_number, kind, role, COUNT(*) AS crash_count
FROM crash_reports
GROUP BY platform, app_version, build_number, kind, role;

CREATE VIEW IF NOT EXISTS v_participant_terminal_states AS
SELECT
  mp.match_id,
  mp.occupant_id,
  mp.role,
  mp.platform,
  mp.app_version,
  mp.build_number,
  mp.joined_match_at,
  mp.left_match_at,
  COALESCE(
    mp.terminal_reason,
    CASE
      WHEN mp.reached_score_screen = 1 THEN 'finished'
      WHEN EXISTS (
        SELECT 1 FROM crash_reports c
        WHERE c.match_id = mp.match_id AND c.occupant_id = mp.occupant_id
      ) THEN 'uncaught_exception'
      WHEN EXISTS (
        SELECT 1 FROM telemetry_events e
        WHERE e.match_id = mp.match_id AND e.occupant_id = mp.occupant_id AND e.event_type = 'freeze_watchdog'
      ) THEN 'freeze_watchdog'
      ELSE 'unknown'
    END
  ) AS resolved_terminal_reason,
  mp.reached_score_screen
FROM match_participants mp;
