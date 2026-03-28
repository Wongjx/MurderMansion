# Telemetry Ops

Telemetry is disabled by default in `wrangler.jsonc`:

```json
"vars": {
  "TELEMETRY_ENABLED": "false"
}
```

That means the live Worker will ignore telemetry endpoints unless you explicitly deploy with
`TELEMETRY_ENABLED=true`. Keep it disabled until the Cloudflare free-tier write limit resets.

## Local-only development

Apply migrations to local D1 only:

```bash
npm run d1:migrate:local
```

Run the Worker locally with telemetry enabled against local D1 only:

```bash
npm run dev:telemetry
```

If you only need the discovery Worker without telemetry writes:

```bash
npm run dev
```

## Remote operations

Apply migrations remotely only when you are ready to spend Cloudflare quota again:

```bash
wrangler d1 migrations apply murder-mansion-telemetry --remote
```

Run a canned query against remote D1:

```bash
npm run telemetry:query -- queries/matches_started.sql
```

Typical workflow after a playtest:

1. Run `queries/matches_started.sql`
2. Run `queries/matches_concluded.sql`
3. Run `queries/incomplete_matches.sql`
4. Run `queries/crashes_by_platform_build.sql`
5. If needed, copy a `match_id` and run `queries/match_timeline.sql` after replacing `YOUR_MATCH_ID`

Typical workflow after a release:

1. Run `queries/match_completion_by_build.sql`
2. Run `queries/host_vs_client_crashes.sql`
3. Run `queries/incomplete_matches.sql`
4. Drill into suspicious matches with `queries/match_timeline.sql` and `queries/crash_details_for_match.sql`
