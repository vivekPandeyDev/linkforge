Here’s a cleaner, more professional usage note you can put in your README or share with your team:

---

# 🚀 k6 Load Test Script Usage Guide

This script supports multiple test types and runtime configuration using environment variables.

---

## 📌 Required Parameters

You must provide:

* `TEST_TYPE` → One of: `warmup`, `constant`, `concurrency`, `capacity`
* `SHORT_CODE` → Cached shortcode to test

### Example

```bash
k6 run ./scripts/k6_script.js \
  -e TEST_TYPE=constant \
  -e SHORT_CODE=k4yFF4rIs
```

---

## 📌 Optional Parameters

### 1️⃣ Cache Miss Ratio

Controls how often a cache miss occurs.

* `MISS_RATIO=10` → Every 10th request is a cache miss
* Default = `15`

```bash
k6 run ./scripts/k6_script.js \
  -e TEST_TYPE=constant \
  -e SHORT_CODE=k4yFF4rIs \
  -e MISS_RATIO=10
```

---

### 2️⃣ Custom Base URL

Override default (`http://localhost:3000`)

```bash
k6 run ./scripts/k6_script.js \
  -e TEST_TYPE=constant \
  -e SHORT_CODE=k4yFF4rIs \
  -e MISS_RATIO=10 \
  -e BASE_URL=http://localhost:8000
```

---

# 🧪 Supported Test Types

| Test Type     | Purpose                                    |
| ------------- | ------------------------------------------ |
| `warmup`      | Light traffic to warm JVM, Redis, DB pools |
| `constant`    | Steady request rate                        |
| `concurrency` | Increasing concurrent users                |
| `capacity`    | Gradual ramp-up to find max throughput     |

---

# ⚙️ Default Values

If not provided:

* `BASE_URL` → `http://localhost:3000`
* `MISS_RATIO` → `15`
* `TEST_TYPE` → `capacity`

---

# 🎯 Quick Examples

### Capacity Test

```bash
k6 run ./scripts/k6_script.js -e TEST_TYPE=capacity -e SHORT_CODE=k4yFF4rIs
```

### Concurrency Test

```bash
k6 run ./scripts/k6_script.js -e TEST_TYPE=concurrency -e SHORT_CODE=k4yFF4rIs
```

### Warmup

```bash
k6 run ./scripts/k6_script.js -e TEST_TYPE=warmup -e SHORT_CODE=k4yFF4rIs
```

---

# 🧠 Best Practice

For realistic performance testing:

1. Run `warmup` first (2–3 minutes)
2. Then run `capacity` or `concurrency`
3. Monitor:

    * CPU usage
    * GC pauses
    * Redis latency
    * DB connection pool
    * `p95` latency in k6 output

---

If you'd like, I can also format this as:

* 📄 README.md section
* 📦 Internal performance testing doc
* 🧾 Confluence-ready documentation
* 🛠 CI/CD pipeline snippet

Just tell me where you want to use it.
