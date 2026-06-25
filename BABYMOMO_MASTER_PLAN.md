# BABYMOMO — MASTER PLANNING DOCUMENT
## Version 1.0 · Android · Kotlin + Jetpack Compose
**Status:** APPROVED — Ready to build  
**Package:** `com.babymomo.app`  
**Date:** June 25, 2026  
**Sources:** babymomo (structure/architecture) + Kai (AI integration pattern)

---

## TABLE OF CONTENTS

1. Vision & North Star
2. Decisions Log (all questions answered)
3. Full Feature Set by Tier
4. Architecture Overview
5. Module & Package Layout
6. Database Schema (all Room entities)
7. AI Integration Design
8. Memory System Design
9. LLM Provider Chain
10. Interactive UI System
11. Tool Execution System
12. Linux Sandbox
13. Heartbeat System
14. UI Screens Specification
15. Tech Stack & Dependencies
16. Build & CI Configuration
17. Build Phases (Phase 0–7)
18. Coding Conventions
19. Scope Guard (what will NOT be built)

---

## 1. VISION & NORTH STAR

> *"One AI. One memory. One brain. Forever. Running on your device."*

Babymomo is a **private AI companion for Android** that:
- Remembers everything important across all conversations using a bi-temporal memory graph
- Grows smarter the longer you use it — memories that prove useful get promoted into permanent context
- Runs fully on-device when possible, with optional remote LLM fallback
- Executes real tools: web search, calendar, notifications, shell commands
- Checks on you autonomously every 30 minutes via a silent heartbeat
- Generates full interactive native screens on demand (quizzes, dashboards, recipes, games)

It is **not** a chatbot. It is not an assistant. It is a digital mind that lives on your device.

---

## 2. DECISIONS LOG

All decisions are final and locked before any code is written.

| Decision | Answer |
|---|---|
| App name | **Babymomo** |
| Package name | **com.babymomo.app** |
| Platform | **Android only** |
| UI framework | **Jetpack Compose** |
| Language | **Kotlin** |
| On-device inference | **LiteRT only** (Kai's approach) |
| On-device models | **Gemma 2B + Phi-3 Mini** |
| Remote LLM providers | **OpenAI, NVIDIA NIM, OpenRouter** (all optional) |
| Memory types | **All 4: Working, Episodic, Semantic, Procedural** |
| Memory promotion | **hitCount ≥ 5 → permanent system prompt** |
| Interactive AI screens | **Tier 1 — ships in v1.0** |
| Linux sandbox | **Yes — proot Alpine Linux** |
| AI integration pattern | **Kai's pattern** (provider chain, tool loop, heartbeat) |
| Dependency injection | **Hilt** |
| Database | **Room + SQLCipher** |
| Background jobs | **WorkManager** |
| Streaming | **Real SSE** (not simulated) |
| Min SDK | **26 (Android 8.0)** |
| Target SDK | **35** |
| Build system | **Gradle 8.4 + libs.versions.toml** |
| CI | **GitHub Actions** |

---

## 3. FULL FEATURE SET BY TIER

### TIER 1 — Ships in v1.0

**Core Chat**
- Streaming chat with real SSE
- Routing reason chip (shows which agents handled the turn)
- Conversation history persisted in Room
- Auto-persisted conversations with titles

**Memory System**
- Bi-temporal memory graph (valid-from / valid-to timestamps on all facts)
- All 4 memory types: Working, Episodic, Semantic, Procedural
- LLM-powered entity + relation + fact extraction on every turn
- 4-signal memory reranker (cosine + graph proximity + confidence + recency decay)
- Memory citations in responses `[m_abc]`
- Memory browser tab: filter by type, search, view stats
- Memory promotion: hitCount ≥ 5 → injected into permanent system prompt, removed from volatile store

**LLM Provider Chain**
- On-device: LiteRT (Gemma 2B, Phi-3 Mini)
- Remote: OpenAI, NVIDIA NIM, OpenRouter (all optional, user configures API keys)
- Mock: deterministic hash-based, for dev/CI/offline
- Fallback order: LiteRT → Remote → Mock
- WrappedLlmProvider: enriches every system prompt with recalled memories + project contexts

**Agent System**
- MomoKernel: brain stem, routes every turn
- RequestClassifier: decides route (chat / skill / agent / tool)
- AgentOrchestrator: coordinates 5 specialist agents
- PlannerAgent, ResearcherAgent, MemoryAgent, CriticAgent, ExecutorAgent

**Skills**
- WriteArticle, Summarize, WebSearch, Calendar, Shell, PDF (status stub in v1)
- Skill triggered from natural language via ExecutorAgent keyword matching

**Projects**
- Create projects with name + description + initial tasks
- Each project auto-creates a knowledge graph node
- Project context injected into LLM system prompt during chat

**Interactive AI Screens**
- AI generates full JSON screen descriptors on demand
- App renders them as native Compose UI
- Supported widgets: Button, Text, List, Input, Card, Grid, ProgressBar
- Use cases: quizzes, recipe cards, brainstorm boards, dashboards, mini games

**Tool Execution**
- Tool loop: AI calls tools, gets results, loops until final answer (Kai pattern)
- WebSearchTool: fetches search results
- NotificationTool: posts Android notifications
- CalendarTool: reads/creates calendar events
- ShellTool: runs commands in Linux sandbox

**Linux Sandbox**
- proot Alpine Linux, ~3MB download, no root needed
- Optional packages: bash, curl, wget, git, jq, python3, pip, Node.js
- Built-in terminal screen alongside AI chat
- Secure: sandboxed inside app, no host filesystem access

**Heartbeat**
- WorkManager periodic job, every 30 minutes, 8am–10pm
- Reviews memories, tasks, calendar
- Silent if nothing needs attention
- Posts notification if action needed
- Heartbeat log screen (history of autonomous checks)

**Models**
- Model catalog: Gemma 2B + Phi-3 Mini (LiteRT)
- Download worker with progress
- Activate / deactivate models

**Settings**
- Privacy controls: internet off by default
- Remote LLM config: OpenAI / NVIDIA NIM / OpenRouter (API key + base URL + model string)
- Customizable system prompt ("soul")
- Export all settings as JSON
- Import settings from JSON
- Linux sandbox toggle

**Storage & Privacy**
- SQLCipher encrypted Room database
- EncryptedSharedPreferences for API keys
- No analytics, no telemetry, no ads
- All data stays on device

**Other**
- Text-to-speech: listen to AI responses
- Image attachments: attach images to any conversation (vision models)
- MCP server support: connect to Streamable HTTP MCP endpoints
- Curated MCP server list (Fetch, DeepWiki, Context7, etc.)

### TIER 2 — v1.1 (post-launch)

- ONNX BGE-small-en-v1.5 real embeddings (replacing hash-based mock)
- PDF analysis skill (full document ingestion)
- Fine-tuning tooling
- Voice input (STT + TTS full duplex)
- Home screen widget (quick memory capture)
- Additional LiteRT models (Llama 3.2 3B, Qwen 2.5 1.5B)

---

## 4. ARCHITECTURE OVERVIEW

### The Babymomo Loop (every user turn)

```
User Input
   │
   ▼
┌──────────────────┐
│  RequestClassifier│  ← classifies intent: chat / skill / agent / interactive
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  MemoryRecaller  │  ← pulls top-k memories: cosine + graph + confidence + recency
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ WrappedLlmProvider│ ← injects memories + promoted memories + project context into system prompt
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│   LlmChain       │  ← LiteRT → OpenAI/NIM/OpenRouter → Mock
└────────┬─────────┘
         │  (tool calls loop if needed)
         ▼
┌──────────────────┐
│  ToolRegistry    │  ← executes WebSearch / Calendar / Notifications / Shell
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  MemoryExtractor │  ← LLM extracts entities + relations + facts from response
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  MemoryService   │  ← persists bi-temporal memories + updates graph + checks hitCount
└────────┬─────────┘
         │  hitCount ≥ 5?
         ▼
┌──────────────────┐
│  MemoryPromoter  │  ← promotes memory into permanent system prompt section
└──────────────────┘
         │
         ▼
Response (streamed, with [m_abc] citations)
```

### Layer Diagram

```
┌─────────────────────────────────────────────────┐
│                  UI (Compose)                    │
│  Chat · Memory · Projects · Skills · Models      │
│  Settings · Heartbeat · Interactive · Terminal   │
└───────────────────────┬─────────────────────────┘
                        │ ViewModels (Hilt-injected)
┌───────────────────────▼─────────────────────────┐
│                   MomoKernel                     │
│      RequestClassifier · streamProcess           │
│      AgentOrchestrator · SkillRegistry           │
└────┬──────────────────┬──────────────────┬───────┘
     ▼                  ▼                  ▼
┌─────────┐      ┌────────────┐    ┌──────────────┐
│  Agent  │      │  LlmChain  │    │    Memory    │
│  Layer  │      │            │    │    Layer     │
│Planner  │      │ LiteRT     │    │ Recaller     │
│Research │      │ OpenAI     │    │ Extractor    │
│Memory   │      │ NVIDIA NIM │    │ Promoter     │
│Critic   │      │ OpenRouter │    │ Graph        │
│Executor │      │ Mock       │    │ VectorIndex  │
└─────────┘      └────────────┘    └──────┬───────┘
     ▼                                    ▼
┌─────────┐                       ┌──────────────┐
│  Skills │                       │  Room DB     │
│  Tools  │                       │  (encrypted) │
│  MCP    │                       └──────────────┘
│  Shell  │
└─────────┘
     ▼
┌──────────────────────┐
│    WorkManager       │
│  HeartbeatWorker     │
│  MemoryMaintenance   │
│  ModelDownload       │
└──────────────────────┘
```

---

## 5. MODULE & PACKAGE LAYOUT

```
babymomo/
├── app/
│   └── src/main/
│       ├── java/com/babymomo/app/
│       │   │
│       │   ├── BabymomoApp.kt              # Application, Hilt entry, WorkManager init
│       │   ├── MainActivity.kt             # Single-activity, Compose NavHost
│       │   │
│       │   ├── core/
│       │   │   │
│       │   │   ├── llm/
│       │   │   │   ├── LlmProvider.kt          # interface: streamChat(), complete(), isAvailable()
│       │   │   │   ├── LlmChain.kt             # fallback: LiteRT → Remote → Mock
│       │   │   │   ├── LocalLlmProvider.kt     # LiteRT runtime bridge
│       │   │   │   ├── RemoteLlmProvider.kt    # OpenAI-compat SSE (OpenAI / NIM / OpenRouter)
│       │   │   │   ├── MockLlmProvider.kt      # deterministic, dev/CI
│       │   │   │   ├── WrappedLlmProvider.kt   # decorator: injects memory+context into prompt
│       │   │   │   └── model/
│       │   │   │       ├── LlmChunk.kt         # streaming token
│       │   │   │       ├── Message.kt          # role + content
│       │   │   │       └── Tool.kt             # tool definition for function calling
│       │   │   │
│       │   │   ├── memory/
│       │   │   │   ├── MemoryService.kt        # orchestrates all memory ops
│       │   │   │   ├── MemoryRecaller.kt       # 4-signal reranker
│       │   │   │   ├── MemoryExtractor.kt      # LLM-powered entity/fact extraction
│       │   │   │   ├── MemoryPromoter.kt       # hitCount ≥ 5 → system prompt
│       │   │   │   ├── MemoryGraph.kt          # bi-temporal knowledge graph ops
│       │   │   │   ├── VectorIndex.kt          # flat cosine search over embeddings
│       │   │   │   ├── EmbedderProvider.kt     # routes to OnnxEmbedder or MockEmbedder
│       │   │   │   ├── OnnxEmbedder.kt         # BGE-small ONNX (v1.1)
│       │   │   │   └── MockEmbedder.kt         # hash-based 384-dim (v1.0 fallback)
│       │   │   │
│       │   │   ├── kernel/
│       │   │   │   ├── MomoKernel.kt           # brain stem: routes every turn
│       │   │   │   └── RequestClassifier.kt    # classifies intent
│       │   │   │
│       │   │   ├── agents/
│       │   │   │   ├── AgentOrchestrator.kt
│       │   │   │   ├── PlannerAgent.kt
│       │   │   │   ├── ResearcherAgent.kt
│       │   │   │   ├── MemoryAgent.kt
│       │   │   │   ├── CriticAgent.kt
│       │   │   │   └── ExecutorAgent.kt        # skill dispatch via keyword match
│       │   │   │
│       │   │   ├── skills/
│       │   │   │   ├── Skill.kt                # interface: triggers, execute()
│       │   │   │   ├── WriteArticleSkill.kt
│       │   │   │   ├── SummarizeSkill.kt
│       │   │   │   ├── WebSearchSkill.kt
│       │   │   │   ├── CalendarSkill.kt
│       │   │   │   ├── ShellSkill.kt
│       │   │   │   └── PdfSkill.kt             # stub in v1.0
│       │   │   │
│       │   │   ├── tools/
│       │   │   │   ├── ToolRegistry.kt         # registers all tools for LLM function calling
│       │   │   │   ├── WebSearchTool.kt
│       │   │   │   ├── NotificationTool.kt
│       │   │   │   ├── CalendarTool.kt
│       │   │   │   └── ShellTool.kt            # delegates to Linux sandbox
│       │   │   │
│       │   │   ├── mcp/
│       │   │   │   ├── McpClient.kt            # Streamable HTTP MCP client
│       │   │   │   ├── McpServerRegistry.kt    # user-configured + curated servers
│       │   │   │   └── McpTool.kt              # adapts MCP tool → ToolRegistry
│       │   │   │
│       │   │   ├── interactive/
│       │   │   │   ├── InteractiveScreenParser.kt  # JSON → ScreenDescriptor
│       │   │   │   └── ScreenDescriptor.kt         # sealed classes for each widget type
│       │   │   │
│       │   │   ├── sandbox/
│       │   │   │   ├── LinuxSandbox.kt         # proot process lifecycle
│       │   │   │   ├── SandboxInstaller.kt     # Alpine bootstrap download + install
│       │   │   │   └── SandboxSession.kt       # run command, stream output
│       │   │   │
│       │   │   └── projects/
│       │   │       ├── ProjectService.kt
│       │   │       └── ProjectContextProvider.kt  # formats project → system prompt segment
│       │   │
│       │   ├── data/
│       │   │   └── db/
│       │   │       ├── AppDatabase.kt          # Room, SQLCipher
│       │   │       ├── entities/               # 12 Room entity data classes
│       │   │       └── dao/                    # 12 DAOs
│       │   │
│       │   ├── ui/
│       │   │   ├── theme/
│       │   │   │   ├── Color.kt
│       │   │   │   ├── Type.kt
│       │   │   │   ├── Shape.kt
│       │   │   │   └── Theme.kt
│       │   │   ├── nav/
│       │   │   │   ├── NavGraph.kt             # NavHost + all routes
│       │   │   │   └── Route.kt                # sealed class of all routes
│       │   │   └── screens/
│       │   │       ├── chat/
│       │   │       │   ├── ChatScreen.kt
│       │   │       │   └── ChatViewModel.kt
│       │   │       ├── memory/
│       │   │       │   ├── MemoryScreen.kt     # browse, filter, search, stats
│       │   │       │   └── MemoryViewModel.kt
│       │   │       ├── projects/
│       │   │       │   ├── ProjectsScreen.kt
│       │   │       │   └── ProjectsViewModel.kt
│       │   │       ├── skills/
│       │   │       │   ├── SkillsScreen.kt
│       │   │       │   └── SkillsViewModel.kt
│       │   │       ├── models/
│       │   │       │   ├── ModelsScreen.kt     # catalog + download progress
│       │   │       │   └── ModelsViewModel.kt
│       │   │       ├── settings/
│       │   │       │   ├── SettingsScreen.kt
│       │   │       │   └── SettingsViewModel.kt
│       │   │       ├── interactive/
│       │   │       │   ├── InteractiveScreen.kt        # renders AI-generated screens
│       │   │       │   └── InteractiveScreenRenderer.kt # ScreenDescriptor → Compose
│       │   │       ├── heartbeat/
│       │   │       │   ├── HeartbeatScreen.kt  # log of autonomous checks
│       │   │       │   └── HeartbeatViewModel.kt
│       │   │       ├── terminal/
│       │   │       │   ├── TerminalScreen.kt   # built-in Linux terminal
│       │   │       │   └── TerminalViewModel.kt
│       │   │       └── mcp/
│       │   │           ├── McpScreen.kt        # manage MCP servers
│       │   │           └── McpViewModel.kt
│       │   │
│       │   ├── work/
│       │   │   ├── HeartbeatWorker.kt
│       │   │   ├── MemoryMaintenanceWorker.kt
│       │   │   └── ModelDownloadWorker.kt
│       │   │
│       │   └── model/
│       │       ├── ModelManager.kt
│       │       └── ModelCatalog.kt             # Gemma 2B + Phi-3 Mini LiteRT entries
│       │
│       ├── res/
│       │   ├── values/strings.xml
│       │   ├── values/colors.xml
│       │   ├── xml/network_security_config.xml
│       │   └── drawable/                       # launcher icon, splash
│       │
│       └── AndroidManifest.xml
│
├── gradle/
│   ├── libs.versions.toml                      # single version catalog
│   └── wrapper/
│       └── gradle-wrapper.properties           # Gradle 8.4
│
├── .github/
│   └── workflows/
│       └── android.yml                         # CI: build APK on every push
│
├── docs/                                       # architecture decision records
├── build.gradle.kts                            # root build config
├── settings.gradle.kts
├── gradle.properties
├── CHANGELOG.md
├── LICENSE                                     # MIT
└── README.md
```

---

## 6. DATABASE SCHEMA

All tables live in a single SQLCipher-encrypted Room database (`babymomo.db`).

```sql
-- Conversations
conversations (
    id          TEXT PRIMARY KEY,   -- UUID
    title       TEXT NOT NULL,
    createdAt   INTEGER NOT NULL,   -- epoch ms
    updatedAt   INTEGER NOT NULL
)

-- Messages
messages (
    id              TEXT PRIMARY KEY,
    conversationId  TEXT NOT NULL REFERENCES conversations(id),
    role            TEXT NOT NULL,  -- "user" | "assistant" | "tool"
    content         TEXT NOT NULL,
    timestamp       INTEGER NOT NULL,
    routingReason   TEXT,           -- e.g. "PlannerAgent + WebSearchTool"
    imageUri        TEXT            -- optional attached image
)

-- Memories (bi-temporal)
memories (
    id              TEXT PRIMARY KEY,
    content         TEXT NOT NULL,
    type            TEXT NOT NULL,  -- "WORKING" | "EPISODIC" | "SEMANTIC" | "PROCEDURAL"
    confidence      REAL NOT NULL DEFAULT 1.0,
    hitCount        INTEGER NOT NULL DEFAULT 0,
    isInSystemPrompt INTEGER NOT NULL DEFAULT 0,  -- 1 when promoted
    validFrom       INTEGER NOT NULL,             -- epoch ms (transaction time)
    validTo         INTEGER,                      -- null = currently valid
    createdAt       INTEGER NOT NULL,
    sourceMessageId TEXT REFERENCES messages(id)
)

-- Memory vectors (flat cosine index)
memory_vectors (
    id          TEXT PRIMARY KEY,
    memoryId    TEXT NOT NULL REFERENCES memories(id),
    embedding   BLOB NOT NULL,      -- FloatArray serialized, 384-dim
    dimension   INTEGER NOT NULL DEFAULT 384
)

-- Knowledge graph entities
entities (
    id          TEXT PRIMARY KEY,
    name        TEXT NOT NULL,
    type        TEXT NOT NULL,      -- "PERSON" | "PLACE" | "CONCEPT" | "PROJECT" | "THING"
    description TEXT,
    createdAt   INTEGER NOT NULL,
    projectId   TEXT REFERENCES projects(id)
)

-- Knowledge graph relations (bi-temporal)
relations (
    id              TEXT PRIMARY KEY,
    fromEntityId    TEXT NOT NULL REFERENCES entities(id),
    toEntityId      TEXT NOT NULL REFERENCES entities(id),
    type            TEXT NOT NULL,  -- e.g. "WORKS_AT", "KNOWS", "OWNS"
    weight          REAL NOT NULL DEFAULT 1.0,
    validFrom       INTEGER NOT NULL,
    validTo         INTEGER         -- null = currently valid
)

-- Projects
projects (
    id              TEXT PRIMARY KEY,
    name            TEXT NOT NULL,
    description     TEXT,
    status          TEXT NOT NULL DEFAULT "ACTIVE",
    tasks           TEXT,           -- JSON array of task strings
    graphEntityId   TEXT REFERENCES entities(id),
    createdAt       INTEGER NOT NULL,
    updatedAt       INTEGER NOT NULL
)

-- Model catalog
model_catalog (
    id              TEXT PRIMARY KEY,
    name            TEXT NOT NULL,  -- "Gemma 2B", "Phi-3 Mini"
    filename        TEXT NOT NULL,  -- e.g. "gemma-2b-it.bin"
    sizeBytes       INTEGER NOT NULL,
    downloadUrl     TEXT NOT NULL,
    isDownloaded    INTEGER NOT NULL DEFAULT 0,
    isActive        INTEGER NOT NULL DEFAULT 0,
    downloadedAt    INTEGER
)

-- Settings (key-value)
settings (
    key     TEXT PRIMARY KEY,
    value   TEXT NOT NULL           -- JSON-encoded value
)

-- Heartbeat log
heartbeat_log (
    id          TEXT PRIMARY KEY,
    timestamp   INTEGER NOT NULL,
    summary     TEXT NOT NULL,      -- what the heartbeat checked/found
    notified    INTEGER NOT NULL DEFAULT 0,
    message     TEXT                -- notification message if notified
)

-- MCP servers
mcp_servers (
    id          TEXT PRIMARY KEY,
    name        TEXT NOT NULL,
    url         TEXT NOT NULL,
    isEnabled   INTEGER NOT NULL DEFAULT 1,
    isCurated   INTEGER NOT NULL DEFAULT 0,
    addedAt     INTEGER NOT NULL
)
```

---

## 7. AI INTEGRATION DESIGN (Kai Pattern)

### 7A. LlmProvider Interface

```kotlin
interface LlmProvider {
    // Streams tokens as Flow. Supports tool_use blocks.
    fun streamChat(
        systemPrompt: String,
        messages: List<Message>,
        tools: List<Tool> = emptyList()
    ): Flow<LlmChunk>

    // Single blocking completion (used by extractor, heartbeat, classifier)
    suspend fun complete(prompt: String): String

    fun isAvailable(): Boolean
    fun providerName(): String
}

sealed class LlmChunk {
    data class Token(val text: String) : LlmChunk()
    data class ToolCall(val id: String, val name: String, val input: JsonObject) : LlmChunk()
    data class ToolResult(val callId: String, val result: String) : LlmChunk()
    object Done : LlmChunk()
    data class Error(val message: String) : LlmChunk()
}
```

### 7B. LlmChain (fallback)

```
Priority 1: LocalLlmProvider  (LiteRT, if model downloaded + active)
Priority 2: RemoteLlmProvider (OpenAI / NVIDIA NIM / OpenRouter — first configured)
Priority 3: MockLlmProvider   (always available, deterministic)
```

`LlmChain` tries each in order, skips unavailable providers, never throws — always falls back.

### 7C. WrappedLlmProvider (Kai's decorator pattern)

Before every call, assembles system prompt in this order:

```
[CORE SOUL]
{user-defined personality / base instructions}

[PROMOTED MEMORIES — PERMANENT]
{memories with isInSystemPrompt=1, formatted as bullet facts}

[RECALLED MEMORIES — THIS TURN]
{top-8 memories from 4-signal reranker, with citations [m_abc]}

[ACTIVE PROJECTS]
{name + description + current tasks for all ACTIVE projects}

[CONTEXT]
Current date: {date}
Current time: {time}
```

### 7D. Tool Execution Loop (Kai pattern)

```
User message
  → LLM streams response
    → if ToolCall chunk appears:
        → ToolRegistry.execute(name, input) → result
        → append tool_result to message history
        → loop: continue streaming same conversation
    → if Done chunk appears:
        → final answer delivered to UI
```

This loop continues until the LLM produces a final text answer with no tool calls.

### 7E. Remote Provider Config

All three remote providers share the OpenAI-compatible `/v1/chat/completions` API shape:

| Provider | Base URL | Notes |
|---|---|---|
| OpenAI | `https://api.openai.com` | Standard |
| NVIDIA NIM | `https://integrate.api.nvidia.com` | OpenAI-compat |
| OpenRouter | `https://openrouter.ai/api` | OpenAI-compat, model routing |

User configures per-provider: API key + model string. Keys stored in `EncryptedSharedPreferences`. Only enabled providers are tried in the fallback chain.

### 7F. Interactive UI Generation

When RequestClassifier routes a turn to `InteractiveSkill`:

1. LLM is prompted with structured JSON schema instructions
2. LLM returns a `ScreenDescriptor` JSON blob — no markdown, no prose
3. `InteractiveScreenParser` deserializes it to sealed Kotlin classes
4. `InteractiveScreenRenderer` renders it as native Compose UI

**ScreenDescriptor widget types:**
```
BabyText(text, style)
BabyButton(label, actionId)
BabyList(items: List<BabyListItem>)
BabyInput(hint, inputId)
BabyCard(title, body, children)
BabyGrid(columns, children)
BabyProgress(value, max, label)
BabyDivider
```

Button taps send `actionId` back to the LLM as a new user message, creating interactive multi-turn screen flows.

---

## 8. MEMORY SYSTEM DESIGN

### 8A. Memory Types

| Type | Purpose | Examples | Retention |
|---|---|---|---|
| **Working** | Current session context, short-lived | "User is currently asking about X" | Auto-expires after 24h |
| **Episodic** | Specific events / conversations | "On June 3, user mentioned their sister is getting married" | Long-term, decays slowly |
| **Semantic** | Facts and knowledge about the user | "User is a software engineer", "User prefers dark mode" | Long-term, high confidence |
| **Procedural** | How-to knowledge and user preferences for doing things | "User prefers concise bullet-point answers", "User's daily standup is at 9am" | Long-term, very high confidence |

### 8B. 4-Signal Reranker

```
final_score(memory) =
    (0.40 × cosine_similarity(query_embedding, memory_embedding))
  + (0.30 × graph_proximity(query_entities, memory_entities))   // 1/hop_distance
  + (0.20 × memory.confidence)
  + (0.10 × recency_decay(memory.createdAt))                   // e^(-λt), λ = 0.01/day
```

Top-8 memories by final_score are injected into the system prompt for each turn.

### 8C. Memory Extraction Prompt

After every AI response, `MemoryExtractor` runs a separate LLM call:

```
System: "You are a memory extraction agent. From the conversation below, extract:
1. ENTITIES: named people, places, projects, or concepts mentioned
2. RELATIONS: how entities relate to each other
3. FACTS: specific facts about the user worth remembering long-term
4. TYPE: classify each fact as WORKING / EPISODIC / SEMANTIC / PROCEDURAL

Respond ONLY with JSON. No prose. Schema:
{
  entities: [{name, type, description}],
  relations: [{from, to, type}],
  memories: [{content, type, confidence}]
}"
```

### 8D. Memory Promotion

```
On every memory hitCount increment:
  if memory.hitCount >= 5 AND memory.isInSystemPrompt == false:
    memory.isInSystemPrompt = true
    memory.validTo = now()     // mark as no longer in volatile store
    → included in [PROMOTED MEMORIES] section of every future call
```

---

## 9. LLM PROVIDER CHAIN DETAIL

### Model Catalog (v1.0)

| Model | Provider | Size | Format | Use case |
|---|---|---|---|---|
| Gemma 2B IT | LiteRT | ~1.5GB | `.bin` | General chat, fast |
| Phi-3 Mini 3.8B | LiteRT | ~2.4GB | `.bin` | Reasoning, more capable |

Both are downloadable from the Models screen. `ModelManager` activates one at a time. `LocalLlmProvider` detects active model and loads it. If no model downloaded, it skips to Remote tier.

### Remote Provider Priority

User sets preference order in Settings. Default: OpenAI → NVIDIA NIM → OpenRouter. Any unconfigured provider is skipped transparently.

---

## 10. INTERACTIVE UI SYSTEM (Full Spec)

The AI can generate full interactive screens when the user asks for something visual and interactive. The system works in two modes:

**Mode A — Inline Interactive Block**  
Rendered inside the chat bubble. Small widgets (quiz question, recipe step, quick poll).

**Mode B — Full Interactive Screen**  
Navigates to `InteractiveScreen`, fills the whole viewport. Used for dashboards, games, brainstorm boards, multi-step recipes.

### JSON Schema the LLM must follow

```json
{
  "mode": "inline" | "fullscreen",
  "title": "Screen title (fullscreen only)",
  "widgets": [
    {"type": "text", "text": "...", "style": "title|body|caption"},
    {"type": "button", "label": "...", "actionId": "..."},
    {"type": "list", "items": [{"text": "...", "actionId": "..."}]},
    {"type": "card", "title": "...", "body": "...", "children": [...]},
    {"type": "input", "hint": "...", "inputId": "..."},
    {"type": "grid", "columns": 2, "children": [...]},
    {"type": "progress", "value": 3, "max": 10, "label": "Step 3 of 10"},
    {"type": "divider"}
  ],
  "actions": {
    "action_id": "Description of what this action means"
  }
}
```

When a button is tapped or input submitted, the app sends the `actionId` + any input values back to the LLM as a new message, and the LLM generates the next screen. This creates fully interactive multi-turn experiences with no extra app code per use case.

---

## 11. TOOL EXECUTION SYSTEM

### Tool Interface

```kotlin
interface Tool {
    val name: String
    val description: String
    val parameters: JsonObject      // JSON Schema of parameters
    suspend fun execute(input: JsonObject): String
}
```

### Registered Tools (v1.0)

| Tool | Name | Description |
|---|---|---|
| WebSearchTool | `web_search` | Search the web for current information |
| NotificationTool | `send_notification` | Post a local Android notification |
| CalendarTool | `calendar_read` | Read upcoming calendar events |
| CalendarTool | `calendar_create` | Create a new calendar event |
| ShellTool | `shell_exec` | Run a shell command in the Linux sandbox |
| MemoryTool | `memory_store` | Explicitly store a memory the AI decides is important |
| MemoryTool | `memory_recall` | Retrieve specific memories by keyword |

MCP tools are also registered dynamically from connected MCP servers, each exposed as a Tool in the same registry.

---

## 12. LINUX SANDBOX (proot Alpine)

### Setup Flow

```
User enables sandbox in Settings
  → SandboxInstaller downloads Alpine Linux rootfs (~3MB)
  → Extracts to app private storage
  → proot bootstraps the environment
  → Sandbox ready
```

### Optional Packages (one-tap install)

```
bash, curl, wget, git, jq, python3, pip, nodejs
```

### ShellTool Execution

```
ShellTool.execute(command)
  → LinuxSandbox.run(command)
  → proot process: /bin/sh -c "<command>"
  → streams stdout/stderr back as string
  → returns result to LLM tool loop
```

### Terminal Screen

Separate screen from chat. Full interactive terminal: user types commands, sees live output, AI can also issue commands via ShellTool during a conversation.

---

## 13. HEARTBEAT SYSTEM

### WorkManager Schedule

```kotlin
PeriodicWorkRequest(
    worker = HeartbeatWorker::class,
    repeatInterval = 30, TimeUnit.MINUTES,
    constraints = Constraints(
        requiresBatteryNotLow = false,
        requiresCharging = false
    )
)
```

Active window: 8:00am – 10:00pm. Outside this window, the worker runs but immediately exits without calling the LLM.

### Heartbeat Prompt

```
System: "You are Babymomo's autonomous background agent. Your job is to check
         on the user's world and surface anything that needs attention.

         Review:
         - Recent memories (last 48h)
         - Pending project tasks
         - Upcoming calendar events (next 24h)
         - Any memory that has been accessed frequently but not acted on

         Respond with EXACTLY ONE of:
         A) The single word: SILENT
         B) A short (≤2 sentence) notification message for the user

         Do not explain your reasoning. Do not add preamble."
```

If response is not "SILENT", `HeartbeatWorker` posts an Android notification.
Every run is logged to `heartbeat_log` table regardless of outcome.

---

## 14. UI SCREENS SPECIFICATION

### Navigation Structure

Bottom navigation bar with 5 top-level destinations:

```
Chat  |  Memory  |  Projects  |  Models  |  Settings
```

Additional screens (accessed from within flows):
- Skills (from Settings or Chat)
- Heartbeat Log (from Settings)
- Terminal (from Settings or Chat overflow)
- Interactive Screen (navigated to from Chat when AI generates fullscreen)
- MCP Servers (from Settings → Tools)

### ChatScreen

- Message list (LazyColumn, reverse-scrolled)
- Each message bubble: role label + content + routing reason chip
- Memory citations `[m_abc]` rendered as tappable chips → shows memory detail
- Streaming indicator (animated dots)
- Input bar: text field + send button + image attach button + mic button (TTS output toggle)
- Overflow menu: new conversation, view memories from this chat, export chat

### MemoryScreen

- Tab row: All / Working / Episodic / Semantic / Procedural
- Search bar
- Stats card: active memories, total ever, entities count, relations count, promoted count
- Memory list: each card shows content, type badge, confidence bar, hitCount, age
- Swipe to delete a memory
- Tap to view full detail + related entities + related memories

### ProjectsScreen

- Project list cards: name, status badge, task count
- FAB to create new project
- Project detail: name, description, task checklist, linked memories, graph node

### ModelsScreen

- Catalog list: Gemma 2B, Phi-3 Mini
- Each row: name, size, download progress bar, activate toggle
- Active model badge
- Download runs via `ModelDownloadWorker` (background, survives screen exit)

### SettingsScreen

Grouped sections:
- **AI Providers**: OpenAI (key + model), NVIDIA NIM (key + model), OpenRouter (key + model), provider priority order
- **On-Device**: active model, manage downloads
- **Privacy**: internet off toggle, encrypted storage info
- **Soul**: editable system prompt textarea
- **Tools**: MCP servers list + add server, Linux sandbox toggle + package installer
- **Backup**: export JSON, import JSON
- **About**: version, licenses

### HeartbeatScreen

- Timeline of all heartbeat runs
- Each entry: timestamp, outcome (SILENT / notified), message preview if notified
- "Trigger now" button for manual heartbeat

### TerminalScreen

- Output scrollview (monospace font, dark background)
- Input bar at bottom
- Clear button
- Package manager shortcut (install bash/python/etc.)

### InteractiveScreen

- Title bar (if fullscreen mode)
- Widget renderer fills viewport
- Back button returns to chat
- All button interactions send back to chat AI turn

---

## 15. TECH STACK & DEPENDENCIES

### libs.versions.toml

```toml
[versions]
kotlin = "1.9.22"
agp = "8.3.0"
compose-bom = "2024.02.00"
hilt = "2.50"
room = "2.6.1"
sqlcipher = "4.5.4"
ktor = "2.3.8"
litert = "1.0.1"
onnxruntime = "1.17.0"
workmanager = "2.9.0"
kotlinx-serialization = "1.6.3"
kotlinx-coroutines = "1.8.0"
security-crypto = "1.1.0-alpha06"
accompanist = "0.34.0"
coil = "2.6.0"
junit = "4.13.2"
mockk = "1.13.10"

[libraries]
# Compose
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-navigation = { group = "androidx.navigation", name = "navigation-compose", version = "2.7.7" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version = "1.2.0" }

# Room + SQLCipher
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
sqlcipher = { group = "net.zetetic", name = "android-database-sqlcipher", version.ref = "sqlcipher" }

# Networking (SSE streaming)
ktor-client-core = { group = "io.ktor", name = "ktor-client-core", version.ref = "ktor" }
ktor-client-okhttp = { group = "io.ktor", name = "ktor-client-okhttp", version.ref = "ktor" }
ktor-client-content-negotiation = { group = "io.ktor", name = "ktor-client-content-negotiation", version.ref = "ktor" }
ktor-serialization-json = { group = "io.ktor", name = "ktor-serialization-kotlinx-json", version.ref = "ktor" }

# On-device AI
litert = { group = "com.google.ai.edge.litert", name = "litert", version.ref = "litert" }
litert-gpu = { group = "com.google.ai.edge.litert", name = "litert-gpu", version.ref = "litert" }

# Embeddings
onnxruntime-android = { group = "com.microsoft.onnxruntime", name = "onnxruntime-android", version.ref = "onnxruntime" }

# WorkManager
workmanager-ktx = { group = "androidx.work", name = "work-runtime-ktx", version.ref = "workmanager" }
hilt-work = { group = "androidx.hilt", name = "hilt-work", version = "1.2.0" }

# Serialization + Coroutines
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinx-serialization" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "kotlinx-coroutines" }

# Security
security-crypto = { group = "androidx.security", name = "security-crypto", version.ref = "security-crypto" }

# Image loading
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }

# TTS (Android built-in, no dep needed)
# Testing
junit = { group = "junit", name = "junit", version.ref = "junit" }
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "kotlinx-coroutines" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-kapt = { id = "org.jetbrains.kotlin.kapt", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
```

---

## 16. BUILD & CI CONFIGURATION

### AndroidManifest permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />          <!-- optional, off by default -->
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.READ_CALENDAR" />
<uses-permission android:name="android.permission.WRITE_CALENDAR" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.VIBRATE" />
```

### GitHub Actions CI (`.github/workflows/android.yml`)

```yaml
name: Build APK
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { java-version: '17', distribution: 'temurin' }
      - uses: gradle/actions/setup-gradle@v3
      - run: ./gradlew assembleDebug
      - uses: actions/upload-artifact@v4
        with:
          name: babymomo-debug-apk
          path: app/build/outputs/apk/debug/app-debug.apk
```

### Release tagging

Tag `v1.x.x` triggers a release build with APK attached to GitHub Release.

---

## 17. BUILD PHASES

### Phase 0 — Planning ✅ COMPLETE
- [x] Both repos analysed
- [x] All decisions locked
- [x] Architecture defined
- [x] DB schema defined
- [x] AI integration designed
- [x] This document approved

### Phase 1 — Project Skeleton
- Gradle setup: AGP, libs.versions.toml, Hilt, Room, Compose, Ktor
- `BabymomoApp.kt` (Application class + Hilt entry)
- `MainActivity.kt` (single-activity, NavHost)
- Theme: dark-first color palette, typography, shapes
- Navigation graph with all routes
- Empty screen composables (Chat, Memory, Projects, Models, Settings)
- Bottom nav bar
- CI (GitHub Actions) — build green on first push

### Phase 2 — Core AI Loop
- `LlmProvider` interface + all model classes
- `MockLlmProvider` (fully functional, word-by-word simulated streaming)
- `RemoteLlmProvider` (real SSE, OpenAI-compat — handles OpenAI + NIM + OpenRouter)
- `LlmChain` (fallback logic)
- `WrappedLlmProvider` (system prompt assembly, no memory yet — just date/time + soul)
- `MomoKernel` + `RequestClassifier` (basic routing: chat vs skip)
- `ChatViewModel` + full streaming chat UI
- Conversations + messages persisted to Room

### Phase 3 — Memory System
- All Room entities + DAOs
- `MockEmbedder` (384-dim hash-based)
- `VectorIndex` (flat cosine)
- `MemoryExtractor` (LLM-powered, runs after every turn)
- `MemoryGraph` (entity + relation CRUD)
- `MemoryRecaller` (4-signal reranker)
- `WrappedLlmProvider` updated to inject recalled memories + promoted memories
- `MemoryService` (orchestrates all above)
- `MemoryPromoter` (hitCount ≥ 5 check on every recall)
- Memory tab UI: all 4 type filters, search, stats card, memory cards with citations
- Memory citations `[m_abc]` rendered as chips in chat

### Phase 4 — Agents + Skills + Projects
- `AgentOrchestrator` + all 5 specialist agents
- `RequestClassifier` upgraded to route to agents for complex turns
- `Skill` interface + 5 skills (Write, Summarize, WebSearch, Calendar, Shell)
- `SkillRegistry` (Hilt multi-binding)
- `ExecutorAgent` keyword matching → skill dispatch
- `ProjectService` + `ProjectContextProvider`
- Projects screen (create, list, detail, tasks checklist)
- Project context injected into `WrappedLlmProvider`

### Phase 5 — Tool Execution + Heartbeat + Interactive
- `ToolRegistry` + all tools (WebSearch, Notification, Calendar, Shell)
- Tool loop in `MomoKernel` (call → result → loop until done)
- `HeartbeatWorker` + heartbeat prompt + notification
- Heartbeat log screen
- `InteractiveScreenParser` + `ScreenDescriptor` sealed classes
- `InteractiveScreenRenderer` (all widget types in Compose)
- `InteractiveScreen` full-screen composable
- Routing: `RequestClassifier` detects interactive intent → `InteractiveSkill`

### Phase 6 — Kai Features Pack
- Encrypted storage: SQLCipher wired to Room, `EncryptedSharedPreferences` for API keys
- Settings export/import (JSON serialization of all settings + memory + projects)
- TTS integration (`TextToSpeech` API, toggle in chat)
- Image attachments (Coil for display, image sent as base64 to vision models)
- MCP client + server registry + MCP screen
- Linux sandbox: `SandboxInstaller` + `LinuxSandbox` + `SandboxSession`
- Terminal screen

### Phase 7 — On-Device AI + Models
- `LocalLlmProvider` (LiteRT bridge)
- `ModelManager` + `ModelCatalog` (Gemma 2B + Phi-3 Mini)
- `ModelDownloadWorker` with progress reporting
- Models screen with download progress bars + activate toggle
- `LlmChain` wired to use local model when active
- Remote provider settings UI (API key + model string per provider)
- Provider priority order UI (drag-to-reorder)

### Phase 8 — Polish + Release
- Proguard / R8 rules
- Network security config (certificate pinning for known providers)
- Screenshot tests
- README with screenshots + installation instructions
- CHANGELOG
- v1.0.0 tag → GitHub Release with APK

---

## 18. CODING CONVENTIONS

- **Kotlin idioms**: coroutines + Flow everywhere, no callbacks, no RxJava
- **Unidirectional data flow**: ViewModel holds `StateFlow<UiState>`, Composables observe
- **Hilt everywhere**: no manual DI, no service locator
- **Single-activity**: all navigation via Compose NavHost
- **Sealed classes** for all discriminated unions (LlmChunk, Route, MemoryType, etc.)
- **No hardcoded strings** in UI: all in `strings.xml`
- **Commits**: Conventional Commits (`feat:`, `fix:`, `refactor:`, `docs:`, `chore:`, `test:`)
- **Branch strategy**: `main` always green · `dev` for in-progress · merge to main when stable
- **File size**: if a file exceeds ~200 lines, split it
- **No force-pushes to main**

---

## 19. SCOPE GUARD

The following will **NOT** be built in v1.0 and are not planned unless explicitly requested:

- iOS / Web / Desktop (Android only)
- Cloud sync or server-side storage
- User accounts or authentication
- Voice-to-text input (TTS output only in v1.0)
- Analytics, telemetry, or crash reporting
- Ads or monetization
- Splinterlands integration
- Multi-user / shared conversations
- ONNX real embeddings (hash-based mock in v1.0, real ONNX in v1.1)
- PDF analysis (stub skill only — returns status message)
- Fine-tuning tooling
- Additional LiteRT models beyond Gemma 2B + Phi-3 Mini

---

*This document is the single source of truth. It is updated at the start of each Phase to reflect any decisions made during implementation. Phase 1 begins on your signal.*
