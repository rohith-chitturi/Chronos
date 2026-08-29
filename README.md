# Chronos: Temporal Distributed Systems Debugger & What-If Simulator

**Chronos** is a web-based distributed-systems laboratory that acts as a temporal execution engine. It records the complete event history of a distributed application, dynamically reconstructs its state at any point in time through deterministic replay, and allows developers to branch timelines from historical points to run alternate "what-if" simulations.

---

## 🎯 The Core Problem

Traditional debugging and observability tools primarily expose **current state** (e.g., `Order #101 = CANCELLED`). But developers in distributed systems often need to answer complex causal questions:

- *Why did it become CANCELLED?*
- *What happened immediately before it?*
- *What was the exact system state 30 seconds earlier?*
- *Which specific service caused the failure?*
- ***What would have happened if that failure had not occurred?***

Chronos answers these questions by allowing you to travel through the execution timeline, branch off from historical events, modify conditions, and replay alternate execution paths.

---

## 🏗️ Core Operations

Everything in Chronos revolves around four primary operations:

1. **RECORD**: Capture immutable events as they happen across distributed services (e.g., `ORDER_CREATED`, `PAYMENT_STARTED`, `INVENTORY_RESERVED`).
2. **RECONSTRUCT**: Given an exact timestamp or event sequence number, deterministically rebuild the system state by replaying history up to that point. Stored state is *never* the source of truth—state is strictly a derivation of history.
3. **REPLAY**: Restart execution from any historical state to observe the exact flow of causality.
4. **FORK**: Branch a timeline. Create an alternate history (a child timeline) starting from a specific event, inject a modified condition, and compare the resulting state against the main timeline.

---

## 📐 Architecture

Chronos is currently in its initial **Phase 1-4 prototype** stage, focusing strictly on a rock-solid temporal core.

### Current Stack (Phase 1-4 Vertical Slice)
- **Frontend**: Next.js (React), TypeScript, Tailwind CSS
- **Backend Control Plane**: Spring Boot 3 (Java 21 LTS)
- **Database**: PostgreSQL (Leveraging `JSONB` for immutable, flexible event payloads)

### Future Architecture (Phase 5+)
Once the temporal execution core is thoroughly tested, Chronos will evolve into a full distributed systems debugger:
- **Event Bus**: Apache Kafka (Observing and collecting the distributed event streams)
- **Causality Graph**: Neo4j (Mapping exact causal chains and root-cause traversals)
- **Caching & Ephemeral State**: Redis
- **Containerization**: Kubernetes / Docker

```text
                       ┌──────────────────────┐
                       │      WEB CLIENT      │
                       │   Next.js + React    │
                       └──────────┬───────────┘
                                  │
                                  │ REST / WebSocket
                                  ▼
                       ┌──────────────────────┐
                       │   CHRONOS BACKEND    │
                       │     Spring Boot      │
                       └──────────┬───────────┘
                                  │
              ┌───────────────────┼────────────────────┐
              │                   │                    │
              ▼                   ▼                    ▼
       Timeline Engine       Replay Engine       Causality Engine
              │                   │                    │
              └───────────────────┼────────────────────┘
                                  │
                    ┌─────────────┼──────────────┐
                    ▼             ▼              ▼
               PostgreSQL       Redis          Neo4j
```

---

## 🚀 The E-Commerce Demo Flow

The best way to understand Chronos is to see it in action via the E-Commerce simulation. 

**Main Execution Timeline:**
```
[T1] ORDER_CREATED -> [T2] PAYMENT_STARTED -> [T3] PAYMENT_SUCCESS -> [T4] INVENTORY_RESERVED
```

**Developer Question:** *"What would happen if the Payment Service failed at T3 instead?"*

**The Chronos Solution:**
1. Open the **E-Commerce Demo** timeline.
2. Select event `PAYMENT_STARTED` and view the reconstructed state.
3. Click **Fork Timeline**.
4. Inject the alternate event (`PAYMENT_FAILED`).
5. Replay the simulation in the new child timeline.
6. Chronos automatically derives the alternate future:
```
[T1] ORDER_CREATED -> [T2] PAYMENT_STARTED -> [T3] PAYMENT_FAILED -> [T4] ORDER_CANCELLED
```
7. Visually compare the state differences side-by-side.

---

## 🛠️ Project Structure

The repository is organized into two primary applications:

### `chronos-backend/`
The Spring Boot control plane and execution engine.
- **`timeline/`**: Manages the lifecycle of timelines, including logical forking (parent-child relationship).
- **`event/`**: Records immutable `SystemEvent` entities. Provides the `StateReconstructionService` which merges JSON payloads across the event history to dynamically derive aggregate state.

### `chronos-frontend/`
The Next.js React client providing the visualization layer.
- **`Dashboard`**: High-level overview of environments, total timelines, and event counts.
- **`Timeline Visualizer`**: An interactive UI to travel back in time, inspect event payloads, view deterministic state reconstruction, and run what-if simulations.

---

## 💻 Getting Started (Local Development)

### 1. Start the Database
Ensure Docker is installed and running.
```bash
docker-compose up -d
```

### 2. Start the Spring Boot Backend
Requires Java 21 LTS.
```bash
cd chronos-backend
./gradlew bootRun
```

### 3. Start the Next.js Frontend
Requires Node.js.
```bash
cd chronos-frontend
npm install
npm run dev
```

Navigate to `http://localhost:3000` to access the Chronos dashboard and explore the Temporal Engine!
