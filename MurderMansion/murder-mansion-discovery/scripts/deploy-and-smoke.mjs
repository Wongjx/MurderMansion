import { spawn } from "node:child_process";

function run(command, args) {
  return new Promise((resolve, reject) => {
    const child = spawn(command, args, {
      cwd: process.cwd(),
      stdio: "inherit"
    });
    child.on("exit", (code) => {
      if (code === 0) {
        resolve();
        return;
      }
      reject(new Error(`${command} ${args.join(" ")} exited with code ${code}`));
    });
    child.on("error", reject);
  });
}

try {
  await run("wrangler", ["deploy"]);
  await run("node", ["./scripts/relay-smoke-test.mjs"]);
} catch (error) {
  process.stderr.write(`[deploy-smoke] FAILED: ${error.stack || error.message}\n`);
  process.exitCode = 1;
}
