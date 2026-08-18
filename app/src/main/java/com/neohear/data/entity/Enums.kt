package com.neohear.data.entity

enum class Ear { L, R }

enum class Mode { PROBE, RISK_QUESTIONNAIRE, DEMO }

enum class TestResult { PASS, REFER, REPEAT }

enum class ReferralStatus { PENDING, SCHEDULED, COMPLETED, LOST_TO_FOLLOW_UP }

enum class RiskLevel { LOW, ELEVATED, HIGH }

enum class SyncState { LOCAL_ONLY, PENDING_SYNC, SYNCED, SYNC_FAILED }
