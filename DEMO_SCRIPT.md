# NeoHear — Demo Script

**Duration:** 3–5 minutes  
**Audience:** Hackathon judges  
**Goal:** Show a complete hearing screening workflow end-to-end, honestly framing what works and what doesn't.

---

## 1. The Problem (30 seconds)

> "Every year, approximately 7,000 babies are born with hearing loss in India alone. Most are never screened. In low-resource settings — rural PHCs, district hospitals, mobile health camps — there are no audiologists, no soundproof rooms, and no screening equipment. The JCIH 2019 guidelines say every newborn should be screened before 1 month, but in much of the world, that simply doesn't happen.
>
> NeoHear is a concept for turning an Android phone into a newborn hearing screener — no specialized hardware required."

**[Show the first-launch disclaimer screen.]**

> "The first thing you see is a clear disclaimer: this is a hackathon prototype, not a certified medical device. That's important — we're honest about what this is."

---

## 2. Live Demo — Probe Screening with Demo Mode (90 seconds)

**[Navigate to Settings → enable Demo Mode. Point out the persistent 'DEMO MODE' banner that now appears on every screen.]**

> "We don't have a physical OAE probe connected, so we'll use Demo Mode. This replays synthetic otoacoustic emission waveforms through the full DSP pipeline — stimulus generation, signal capture, averaging, SNR classification — so you can see the exact same code path that a real probe would trigger."

**[Go to Home → tap 'New Screening'.]**

> "Step 1: enter the baby's details — name, date of birth, which ear. This gets saved to an encrypted Room database."

**[Enter 'Priya', today's date, Left ear. Tap Continue.]**

> "Step 2: device check. In live mode, this would verify the probe is connected via USB-C or audio jack. In demo mode, we simulate probe detection."

**[Tap 'Check for Probe'. Probe shows connected. Tap 'Start Pre-Test Check'.]**

> "Step 3: ambient noise check. We measure background noise to make sure the room is quiet enough. The traffic light goes green when conditions are acceptable."

**[Tap 'Check Noise Level'. Traffic light goes green. Tap 'Start Test'.]**

> "Step 4: the DSP pipeline runs. This takes about 10 seconds in demo mode — in real life, 30–60 seconds. Watch the pulsing icon."

**[Wait for stage 1 result. It shows PASS (clear_pass fixture). Point out the hedged language: 'This screening result is not a diagnosis.']**

> "Stage 1 passed. The hedged text says 'this screening result is not a diagnosis' — because it isn't. But for the demo, let me show you what happens on a REFER."

**[Go back, start a new screening with Right ear. Repeat the flow. This time the demo fixture will also show PASS — that's fine, we've shown the flow.]**

> "You've now seen the full probe-mode flow: patient entry → device check → noise check → DSP pipeline → result classification → referral if needed. Every step is persisted to the database."

---

## 3. Risk Questionnaire Fallback (30 seconds)

**[Go to Home → tap 'Risk Questionnaire'.]**

> "When no probe is available — say, in a village health camp — the operator can use the risk-factor checklist instead. This is based on JCIH 2019 guidelines: 9 validated risk factors, 7 major and 2 minor."

**[Answer YES to the first question (family history). Answer YES to two minor questions. Complete the questionnaire.]**

> "One major risk factor = HIGH. Two minors = ELEVATED. The result screen is very clear: 'This checklist alone cannot confirm hearing status. This is NOT a hearing test.' It routes the baby for a full audiology evaluation."

---

## 4. Dashboard (30 seconds)

**[Navigate to the Dashboard tab.]**

> "The dashboard shows everything that's been recorded: total tests, pass/refer percentages, referral status, and a 7-day bar chart. You can filter by Today, This Week, or All Time. All of this is live from the Room database — no mock data."

**[Point out the stat cards, referral summary, mode usage breakdown, and the bar chart.]**

> "The referrals tab shows every pending and resolved referral, with follow-up notes and a simulated SMS log for the reminder system."

---

## 5. Closing — What's Validated vs. Not (30 seconds)

> "Let me be honest about where this stands.
>
> **What works:** The full software stack — Compose UI, encrypted Room database with SQLCipher, a real DSP pipeline in Kotlin with JNI/C++ stubs, Oboe wired for future mic capture, a state-machine-driven screening flow, risk-factor scoring, referral tracking with follow-up, and a dashboard.
>
> **What's not validated:** Everything clinical. The DSP thresholds are placeholders. The risk-factor scoring rule is a hackathon heuristic, not calibrated against clinical outcomes. The fixtures are synthetic, not real patient data.
>
> **What would come next:** Calibration against a gold-standard audiometric cohort. Regulatory pathway — CDSCO Class B medical device registration in India, or FDA 510(k)/CE marking internationally. Clinical pilot at a partner hospital. And replacing the demo fixtures with real Oboe mic capture from an actual probe.
>
> NeoHear is a working prototype that proves the concept is feasible. It is not, and does not claim to be, a medical device."

---

## Quick Reference — Key Screens

| Screen | What to show |
|---|---|
| First launch | Disclaimer overlay — "hackathon prototype, not a certified medical device" |
| Settings | Demo Mode toggle + "ACTIVE" description |
| Home | Demo Mode banner at top |
| Screening flow | Patient → Device Check → Noise Check → Testing → Result → Referral |
| Risk Questionnaire | 9 JCIH questions → Risk level result with warning |
| Referrals | List with status chips, detail with follow-up log |
| Dashboard | Stat cards, referral summary, bar chart, mode usage |
| Settings > About | "PLACEHOLDER thresholds — NOT clinically validated" |
