# SaaS Subscription Entitlements API

Spring Boot REST service for SaaS subscription entitlement management: plans, features, plan entitlements, account subscriptions, usage metering, account overrides, audit events, and route-level authorization checks.

The app is generated from a private spec set and published here as a standalone public demo. Raw input specs are intentionally not included because their schema headers contain private generation identifiers; this repo contains the generated app, neutral metrics, Docker proof evidence, and endpoint receipts.

## Highlights

- **15 seconds of generation** produced 88 files / 12,715 lines of Java (68 main + 15 test) implementing 15 REST operations across plans, features, entitlements, subscriptions, usage metering, account overrides, and audit events.
- **Spec → running Dockerized service in ~14 minutes** end-to-end (spec authoring → validation → compile preview → generation → Docker build with tests → live endpoint proof).
- **Converged on the first run** with exit code 0; the Docker image built with the full test suite as a build step (exit 0).
- **Runtime verified**: `/healthz` returns `UP` and every smoke-tested endpoint (10 of 15) returned a 2xx status against the Docker-started service. Receipts are in `evidence/endpoints/`.

## Metrics

| Metric | Value |
|---|---:|
| Private source spec files | 37 |
| Private source spec size | 25,179 bytes |
| Compile nodes | 37 |
| Compile artifacts | 93 |
| REST operations | 15 |
| Generated app files | 88 |
| Java source files | 68 main + 15 test |
| Java LOC | 12,715 |
| Generation run elapsed | 15 seconds |
| Check + run window | approximately 46 seconds |
| End-to-end task window | approximately 860 seconds |

Generation command shape:

```bash
validate-set --dir <private-spec-dir>
compile-preview --spec-dir <private-spec-dir> --stack java-spring
run --spec-ref <private-spec-dir> --stack java-spring --output-root <private-output-root> --watch
```

Timing details are in `generation-metrics.json`. The broader task timer starts at the earliest reliable private spec artifact timestamp, `2026-06-03T03:35:43Z`, and ends during README/metrics handoff at `2026-06-03T03:50:03Z`; it is marked approximate for that reason.

## Run With Docker

Docker is the supported runtime path for this demo.

```bash
cd app
APP_PORT=18080 docker compose up --build
curl http://127.0.0.1:18080/healthz
```

The Dockerfile runs the generated test suite during image build:

```bash
cd app
APP_PORT=18080 docker compose build --no-cache
```

## API Smoke Flow

The proof run starts the app with Docker Compose and exercises these endpoints:

| Endpoint | Status |
|---|---:|
| `POST /api/plans` | 201 |
| `POST /api/features` | 201 |
| `POST /api/plans/{planId}/features` | 201 |
| `POST /api/subscriptions` | 201 |
| `POST /api/usage` | 201 |
| `POST /api/entitlements/check` | 200 |
| `GET /api/plans/{planId}` | 200 |
| `GET /api/plans/{planId}/entitlements` | 200 |
| `GET /api/accounts/{accountId}/subscriptions` | 200 |
| `GET /api/subscriptions/{subscriptionId}/usage` | 200 |

Evidence is stored under `evidence/`, including endpoint response bodies and Compose config output.

Demo credentials are intentionally simple and local only:

| User | Password | Use |
|---|---|---|
| `entitlement-admin_tenant_a` | `secret` | Plan, feature, and entitlement administration |
| `account-operator_tenant_a` | `secret` | Subscriptions, usage, overrides, and access checks |

## Verification

Completed Docker-only proof commands:

```bash
cd app
APP_PORT=18080 docker compose config
APP_PORT=18080 docker compose build --no-cache
APP_PORT=18080 docker compose up -d --force-recreate
curl http://127.0.0.1:18080/healthz
```

The final endpoint proof returned `0` and all ten API calls returned 2xx statuses. See:

- `evidence/compose-config.txt`
- `evidence/docker-endpoint-proof.txt`
- `evidence/health.json`
- `evidence/endpoints/`

## Notes

The generation run converged, but reported `0 / 8` prose command rules translated. The generated code still includes structured persistence, REST routes, events, tests, and authorization guard checks derived from typed spec metadata.

## Disclaimer

This repository is a demonstration only. It is illustrative of generation output and runtime verification; it is not production software and is not maintained, supported, or warrantied.

- **Authentication is for local demo use only.** HTTP Basic with in-memory users and the literal password `secret` is wired in `app/src/main/java/com/example/spring/SecurityConfig.java`. Do not deploy this configuration anywhere reachable from a public network.
- **Persistence is in-memory.** All state is lost when the container stops. The SQLite volume declared in `app/compose.yaml` is reserved but unused by the in-memory adapter shipped here.
- **No SLAs, no support.** The author makes no representations about correctness, security, or fitness for any purpose. Use at your own risk.

## License

Copyright © 2026 Brady Vitrano. All rights reserved.

No license is granted to use, copy, modify, distribute, or create derivative works of this code. The repository is published for viewing only; for any other use, contact the author.
