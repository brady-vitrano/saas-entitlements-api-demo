# SaaS Subscription Entitlements API

One private spec set produced two working, Dockerized REST services for SaaS subscription entitlement management: a Spring Boot app and a FastAPI app. Both implement the same plans, features, plan entitlements, account subscriptions, usage metering, account overrides, audit events, and route-level authorization checks.

The apps are generated from the same private spec set and published here as a standalone public demo. Raw input specs are intentionally not included because their schema headers contain private generation identifiers; this repo contains the generated app code, neutral metrics, Docker proof evidence, endpoint receipts, and Spring-vs-FastAPI parity receipts.

## Highlights

- **One spec set, two apps**: the same 37-file private spec set generated a Spring Boot REST API and a Python FastAPI REST API.
- **15 seconds of Spring generation** produced 88 files / 12,715 lines of Java (68 main + 15 test) implementing 15 REST operations.
- **35 seconds of FastAPI generation** produced 78 files / 10,515 lines of Python implementing the same 15 REST operations.
- **Docker-built tests passed in both stacks**: Spring test/build passed in the Docker image build, and FastAPI pytest reported `15 passed` in the Docker image build.
- **Over-the-wire parity verified**: both Docker-started apps ran the same 10-call API flow; `10 / 10` statuses matched and `10 / 10` canonical JSON payloads matched.

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
| Python app files | 78 |
| Python source files | 74 |
| Python LOC | 10,515 |
| Spring generation run elapsed | 15 seconds |
| FastAPI generation run elapsed | 35 seconds |
| Check + run window | approximately 46 seconds |
| End-to-end task window | approximately 40,524 seconds |

Generation command shape:

```bash
validate-set --dir <private-spec-dir>
compile-preview --spec-dir <private-spec-dir> --stack java-spring
run --spec-ref <private-spec-dir> --stack java-spring --output-root <private-output-root> --watch
run --spec-ref <private-spec-dir> --stack python-fastapi --output-root <private-output-root> --watch --skip-doctor
```

Timing details are in `generation-metrics.json`. The broader task timer starts at the earliest reliable private spec artifact timestamp, `2026-06-03T03:35:43Z`, and ends during parity README/metrics handoff at `2026-06-03T14:51:07Z`; it is marked approximate for that reason.

## Run With Docker

Docker is the supported runtime path for this demo.

```bash
cd app
APP_PORT=18080 docker compose up --build
curl http://127.0.0.1:18080/healthz
```

FastAPI:

```bash
cd app-python
APP_PORT=18081 docker compose up --build
curl http://127.0.0.1:18081/healthz
```

The Dockerfile runs the generated test suite during image build:

```bash
cd app
APP_PORT=18080 docker compose build --no-cache

cd ../app-python
APP_PORT=18081 docker compose build --no-cache
```

## API Smoke Flow

The proof run starts both apps with Docker Compose and exercises these endpoints against each service:

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

## Wire Parity

Both Docker-started services were exercised with the same HTTP flow:

- Spring Boot on `http://127.0.0.1:18080`
- FastAPI on `http://127.0.0.1:18081`

The parity proof compares HTTP status codes and canonicalized JSON response bodies for ten calls. Result: `10 / 10` status matches and `10 / 10` payload matches.

Parity evidence:

- `evidence/parity/report.txt`
- `evidence/parity/java/`
- `evidence/parity/python/`

## Verification

Completed Docker-only proof commands:

```bash
cd app
APP_PORT=18080 docker compose config
APP_PORT=18080 docker compose build --no-cache
APP_PORT=18080 docker compose up -d --force-recreate
curl http://127.0.0.1:18080/healthz

cd ../app-python
APP_PORT=18081 docker compose config
APP_PORT=18081 docker compose build --no-cache
APP_PORT=18081 docker compose up -d --force-recreate
curl http://127.0.0.1:18081/healthz
```

The final endpoint and parity proofs returned `0`; all ten API calls returned matching 2xx statuses and matching canonical JSON payloads. See:

- `evidence/compose-config.txt`, `evidence/docker-endpoint-proof.txt`, `evidence/health.json`, `evidence/endpoints/` (Spring)
- `evidence/python-compose-config.txt`, `evidence/python-endpoint-proof.txt`, `evidence/python-health.json` (FastAPI)
- `evidence/parity/` (cross-stack comparison)

## Notes

The generation run converged, but reported `0 / 8` prose command rules translated. The generated code still includes structured persistence, REST routes, events, tests, and authorization guard checks derived from typed spec metadata.

The FastAPI preflight reported unsupported role/scope guard capabilities. The submitted run used the documented `--skip-doctor` flag, and the Docker runtime plus payload parity proof passed.

## Disclaimer

This repository is a demonstration only. It is illustrative of generation output and runtime verification; it is not production software and is not maintained, supported, or warrantied.

- **Authentication is for local demo use only.** HTTP Basic with in-memory users and the literal password `secret` is wired in `app/src/main/java/com/example/spring/SecurityConfig.java` (Spring) and `app-python/entitlements_app/auth.py` (FastAPI). Do not deploy this configuration anywhere reachable from a public network.
- **Persistence is in-memory.** All state is lost when the container stops. The SQLite volume declared in `app/compose.yaml` and `app-python/compose.yaml` is reserved but unused by the in-memory adapters shipped here.
- **No SLAs, no support.** The author makes no representations about correctness, security, or fitness for any purpose. Use at your own risk.

## License

Copyright © 2026 Brady Vitrano. All rights reserved.

No license is granted to use, copy, modify, distribute, or create derivative works of this code. The repository is published for viewing only; for any other use, contact the author.
