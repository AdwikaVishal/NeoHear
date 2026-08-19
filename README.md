# NeoHear

**Affordable, phone-first newborn hearing screening — Android app, hackathon prototype.**

NeoHear lets a nurse, ASHA, or Anganwadi worker — not an audiologist — run a guided newborn hearing screen through software, following the same Stage-1 → Stage-2 → referral protocol used in clinical OAE/AABR screening.

> **Disclaimer:** This app is **NOT** a certified medical device and has **NOT** been clinically validated. All screening thresholds and cry-analysis scores are placeholders. Do not use for real clinical decisions.

---

## Table of Contents
- [What's Built](#whats-built)
- [What's Not Built / Stretch](#whats-not-built--stretch)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Running in Demo Mode](#running-in-demo-mode)
- [Running Tests](#running-tests)
- [Project Structure](#project-structure)
- [License](#license)

---

## What's Built

### 🩺 Screening Flow (Probe Mode)
Guided state machine: **Patient Entry → Device Check → Pre-Test Noise Check → DSP Pipeline → Result Classification → Referral (if needed)**.

- **DSP Pipeline** in Kotlin with JNI/C++ stubs for Oboe: `StimulusGenerator` → `ResponseCapture` → `SignalAverager` → `SnrClassifier`
- **2-stage protocol:** Stage 1 REFER triggers an automatic repeat; Stage 2 REFER creates a referral
- **Pre-test noise check:** Traffic-light ambient noise gate via `AmbientNoiseChecker`

### 📋 Risk Questionnaire
9-question JCIH 2019 risk-factor checklist with TTS voice prompts. Computes HIGH / ELEVATED / LOW risk. Used as a fallback screen when no probe is available.

### 🔊 Cry Acoustic Analysis *(Experimental — Research Mode)*
An optional, clearly-labeled **research-only** signal that runs alongside the standard screen — never in place of it.

- Records a short clip of the baby's cry and runs it through a lightweight acoustic feature extractor (pitch, jitter/shimmer, spectral pattern)
- Flags patterns the research literature has associated with elevated neurological/hearing risk, **shown only as an experimental indicator**, not a pass/refer/repeat result
- Gated behind a **"Research Mode"** toggle in Settings; off by default
- Every screen showing a cry-analysis result carries a persistent **"Non-diagnostic — experimental research signal only"** banner, separate from the main screening banner
- Stored as its own entity (`CryAnalysisResult`), decoupled from the clinical `TestSession` record so it can never be mistaken for or merged into an official screening outcome

### 📇 Referral Tracking
Create, update status (PENDING → SCHEDULED / COMPLETED / LOST_TO_FOLLOW_UP), log follow-up notes, simulated SMS reminders for overdue referrals.

### 📊 Dashboard
Live stats from Room DB: test counts, pass/refer rates, referral breakdown, mode usage, 7-day bar chart. Filterable by Today / This Week / All Time.

### 🔐 Data Layer
- Room + SQLCipher encrypted database (entities include Patient, TestSession, Referral, RiskQuestionnaireResponse, and CryAnalysisResult)
- AndroidKeyStore-backed passphrase management
- 6 synthetic OAE waveform fixtures for demo mode

### ⚠️ First-Launch Disclaimer
Full-screen overlay on first launch: *"This app is a hackathon prototype. It is NOT a certified medical device."*

### 🏷️ Persistent Demo Mode Banner
When Demo Mode is active, a visible banner appears on every screen.

---

## What's Not Built / Stretch

| Feature | Status |
|---|---|
| Real Oboe mic capture | JNI stubs wired, C++ placeholder — no real probe integration yet |
| Bluetooth probe pairing | Not started |
| Cry-analysis model validation | Placeholder feature extraction only — not trained/validated on real cry data |
| Multi-language / i18n | English only |
| Data export / CSV | Not built |
| PDF report generation | Not built |
| Onboarding tutorial | Not built |
| Clinical calibration | All thresholds are placeholders |
| Real patient data fixtures | All fixtures are synthetic |

---

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        UI Layer                             │
│  Compose + Material 3                                       │
│  ┌──────────┐ ┌────────────┐ ┌───────────┐ ┌───────────┐  │
│  │HomeScreen│ │Screening   │ │Referrals  │ │Dashboard  │  │
│  │          │ │Screens (7) │ │List/Detail│ │Screen     │  │
│  └────┬─────┘ └─────┬──────┘ └─────┬─────┘ └─────┬─────┘  │
│       │              │              │              │         │
│  ┌────┴──────────────┴──────────────┴──────────────┴─────┐  │
│  │              Navigation Compose                       │  │
│  └───────────────────────────────────────────────────────┘  │
├─────────────────────────────────────────────────────────────┤
│                     ViewModel Layer                         │
│  ScreeningVM │ RiskQuestionnaireVM │ ReferralsVM │ DashVM   │
│  CryAnalysisVM (Research Mode only)                         │
├─────────────────────────────────────────────────────────────┤
│                      Data Layer                             │
│  Room + SQLCipher ─── 5 Entities, 6 DAOs                    │
│  Patient │ TestSession │ Referral │ RiskQuestionnaireResp   │
│  │ CryAnalysisResult                                        │
├─────────────────────────────────────────────────────────────┤
│                     DSP Pipeline                             │
│  StimulusGenerator → ResponseCapture → SignalAverager        │
│                    → SnrClassifier                           │
│  Kotlin + JNI/C++ stubs (Oboe wired for future use)          │
├─────────────────────────────────────────────────────────────┤
│              Cry Acoustic Analysis Pipeline (Research)       │
│  CryCapture → FeatureExtractor (pitch/jitter/shimmer/        │
│  spectral) → ResearchRiskFlagger (non-diagnostic)            │
├─────────────────────────────────────────────────────────────┤
│                    Native Layer                              │
│  Oboe C++ via JNI ─── CMake + NDK                            │
│  native-bridge.cpp (ping + future audio capture)             │
└─────────────────────────────────────────────────────────────┘
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose (bottom nav + deep links) |
| Database | Room + SQLCipher (encrypted at rest) |
| State | AndroidViewModel + StateFlow |
| Audio DSP | Kotlin pipeline + JNI/C++ stubs |
| Cry Analysis | Kotlin feature extraction (pitch/jitter/shimmer), research-mode gated |
| Audio Future | Oboe (wired, CMake build) |
| Security | AndroidKeyStore AES/GCM passphrase |
| Testing | JUnit 4 + Robolectric + Compose UI tests |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 35 |
| NDK | 27.1.12297006 |
| Build | AGP 8.7.3, Kotlin 2.1.0, KSP 2.1.0-1.0.29 |

---

## Running in Demo Mode

No physical probe required. Demo Mode replays synthetic OAE waveforms through the full DSP pipeline.

1. Build and install:
   ```bash
   ./gradlew installDebug
   ```

2. On first launch, accept the disclaimer.

3. Go to **Settings** → enable **Demo Mode**.
   - Optionally also enable **Research Mode** to try the cry-analysis flow.

4. Go to **Home** → tap **New Screening** → enter patient details → the full flow runs using pre-recorded waveforms.

5. The DSP pipeline processes the synthetic waveform identically to how it would process live probe data. If Research Mode is on, a synthetic cry clip is run through the cry-analysis pipeline and shown with its non-diagnostic banner.

---

## Running Tests

```bash
# All unit tests
./gradlew testDebugUnitTest

# Specific test class
./gradlew testDebugUnitTest --tests "com.neohear.ui.screening.ScreeningViewModelTest"
```

---

## Project Structure

```
NeoHear/
├── app/src/main/
│   ├── java/com/neohear/
│   │   ├── MainActivity.kt              # Nav host + disclaimer gate
│   │   ├── NeoHearApp.kt                # Application + DB init
│   │   ├── audio/
│   │   │   ├── NativeBridge.kt          # JNI bridge
│   │   │   ├── pipeline/                # DSP: Stimulus, Capture, Average, Classify
│   │   │   └── waveform/                # Fixture loader + synthetic generator
│   │   ├── cry/                         # Cry capture + feature extraction (research mode)
│   │   ├── data/
│   │   │   ├── AppDatabase.kt           # Room + SQLCipher
│   │   │   ├── converter/               # TypeConverters
│   │   │   ├── dao/                     # DAOs (Patient, TestSession, Referral, Dashboard, RiskQuestionnaire, CryAnalysis)
│   │   │   ├── entity/                  # Entities + enums
│   │   │   └── keystore/                # PassphraseManager
│   │   ├── reminder/                    # FollowUpReminder + SimulatedSmsLog
│   │   └── ui/
│   │       ├── dashboard/               # DashboardViewModel
│   │       ├── questionnaire/           # RiskQuestionnaire VM + Screen + Factors
│   │       ├── referrals/               # ReferralsVM + List/Detail screens
│   │       ├── screening/               # ScreeningVM + 7 screens + state machine
│   │       ├── cryanalysis/             # CryAnalysisVM + Screen (research mode)
│   │       ├── screens/                 # Home, Dashboard, Settings, Disclaimer
│   │       └── theme/                   # Material 3 theme
│   ├── assets/reference_waveforms/      # 6 synthetic OAE fixtures
│   ├── assets/reference_cries/          # Synthetic cry clips for demo mode
│   ├── cpp/                             # C++ native bridge
│   └── res/
├── app/src/test/                        # Unit tests
├── DEMO_SCRIPT.md                       # 3-5 min hackathon presentation script
└── README.md
```

---

## License

TBD
