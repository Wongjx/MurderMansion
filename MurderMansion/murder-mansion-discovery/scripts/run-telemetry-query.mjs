import { spawnSync } from "node:child_process";
import { resolve } from "node:path";

const queryFile = process.argv[2];

if (!queryFile) {
  console.error("Usage: node ./scripts/run-telemetry-query.mjs <query-file.sql>");
  process.exit(1);
}

const absoluteQueryFile = resolve(process.cwd(), queryFile);
const result = spawnSync("wrangler", [
  "d1",
  "execute",
  "murder-mansion-telemetry",
  "--remote",
  "--file",
  absoluteQueryFile
], {
  stdio: "inherit"
});

process.exit(result.status ?? 1);
