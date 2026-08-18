package com.neohear.ui.questionnaire

import com.neohear.data.entity.RiskLevel

/**
 * Represents a single risk factor question in the newborn hearing-loss risk questionnaire.
 *
 * Risk factors are sourced from:
 * - JCIH 2019 (Joint Committee on Infant Hearing) Position Statement:
 *   https://www.ncbi.nlm.nih.gov/pmc/articles/PMC6742595/
 * - RBSK (Rashtriya Bal Swasthya Karyakram) newborn hearing screening protocol (India)
 * - WHO/HAI newborn hearing screening guidelines
 *
 * Each factor is classified as MAJOR or MINOR per JCIH 2019 risk factor categories.
 * MAJOR factors are strongly associated with congenital/permanent hearing loss and warrant
 * immediate audiology referral. MINOR factors elevate risk when multiple are present.
 *
 * @property id Unique string identifier for this risk factor, used as key in the answers map.
 * @property question The question text presented to the user.
 * @property shortLabel A brief label for display (e.g., in summaries or test assertions).
 * @property severity MAJOR or MINOR classification per JCIH guidelines.
 */
enum class RiskFactorSeverity { MAJOR, MINOR }

data class RiskFactor(
    val id: String,
    val question: String,
    val shortLabel: String,
    val severity: RiskFactorSeverity
)

/**
 * Fixed list of9 validated newborn hearing-loss risk factor questions.
 *
 * Based on JCIH 2019 risk factor list (Table 1 of the Position Statement) and
 * adapted for a low-resource, no-hardware screening context.
 *
 * **References:**
 * - JCIH 2019: "Year 2019 Position Statement: Principles and Guidelines for Early
 *   Hearing Detection and Intervention Programs," J Am Acad Audiol, 2019.
 *   PMC6742595 — defines risk factors for late-onset/progressive hearing loss.
 * - RBSK Protocol: Ministry of Health & Family Welfare, Government of India,
 *   Newborn Hearing Screening module under Rashtriya Bal Swasthya Karyakram.
 * - WHO: Recommendations on Newborn Screening, 2023.
 */
object RiskFactorCatalog {

    val factors: List<RiskFactor> = listOf(
        // ── MAJOR risk factors (any one = HIGH) ──────────────────────────
        RiskFactor(
            id = "family_history",
            question = "Is there a family history of childhood hearing loss?",
            shortLabel = "Family history of hearing loss",
            severity = RiskFactorSeverity.MAJOR
        ),
        RiskFactor(
            id = "nicu_stay",
            question = "Was the baby in a Neonatal Intensive Care Unit (NICU) for 5 days or more?",
            shortLabel = "NICU stay ≥5 days",
            severity = RiskFactorSeverity.MAJOR
        ),
        RiskFactor(
            id = "low_birth_weight",
            question = "Was the baby born weighing less than 1500 grams (3.3 pounds)?",
            shortLabel = "Birth weight <1500g",
            severity = RiskFactorSeverity.MAJOR
        ),
        RiskFactor(
            id = "in_utero_infection",
            question = "Was the mother diagnosed with an infection during pregnancy (such as CMV, rubella, syphilis, herpes, or toxoplasmosis)?",
            shortLabel = "In-utero infection",
            severity = RiskFactorSeverity.MAJOR
        ),
        RiskFactor(
            id = "craniofacial",
            question = "Does the baby have any visible abnormalities of the head, face, or ears (e.g., cleft palate, ear malformation, small jaw)?",
            shortLabel = "Craniofacial anomalies",
            severity = RiskFactorSeverity.MAJOR
        ),
        RiskFactor(
            id = "ototoxic_medication",
            question = "Did the baby receive medications that can harm hearing (such as certain strong antibiotics or chemotherapy drugs)?",
            shortLabel = "Ototoxic medication exposure",
            severity = RiskFactorSeverity.MAJOR
        ),
        RiskFactor(
            id = "hyperbilirubinemia",
            question = "Did the baby have severe jaundice (yellow skin) that required a blood exchange transfusion?",
            shortLabel = "Severe jaundice requiring exchange transfusion",
            severity = RiskFactorSeverity.MAJOR
        ),

        // ── MINOR risk factors (≥2 = ELEVATED) ──────────────────────────
        RiskFactor(
            id = "low_apgar",
            question = "Was the baby's Apgar score 0 to 4 at 1 minute, or 0 to 6 at 5 minutes after birth?",
            shortLabel = "Low Apgar score",
            severity = RiskFactorSeverity.MINOR
        ),
        RiskFactor(
            id = "mechanical_ventilation",
            question = "Was the baby on a breathing machine (mechanical ventilation) for 10 days or more?",
            shortLabel = "Mechanical ventilation ≥10 days",
            severity = RiskFactorSeverity.MINOR
        )
    )
}

/**
 * Computes the risk level from a set of yes/no answers to the risk factor questionnaire.
 *
 * **PLACEHOLDER RULE — NOT CLINICALLY VALIDATED.**
 *
 * This scoring rule is a development-stage heuristic for hackathon/demo purposes.
 * It has NOT been validated against clinical outcomes data and must NOT be used for
 * real diagnostic or triage decisions. A production rule would require:
 *   - Calibration against a gold-standard audiometric reference cohort
 *   - Sensitivity/specificity analysis with proper statistical validation
 *   - Regulatory clearance (FDA, CE marking, CDSCO, etc.)
 *   - Consideration of factor interactions and demographic adjustments
 *
 * Current rule:
 * - Any single MAJOR risk factor present → HIGH
 * - Two or more MINOR risk factors present → ELEVATED
 * - Otherwise → LOW
 *
 * @param answers Map of risk factor ID → "YES" or "NO"
 * @return The computed [RiskLevel]
 */
fun computeRiskLevel(answers: Map<String, String>): RiskLevel {
    val majorCount = RiskFactorCatalog.factors
        .filter { it.severity == RiskFactorSeverity.MAJOR }
        .count { answers[it.id]?.uppercase() == "YES" }

    val minorCount = RiskFactorCatalog.factors
        .filter { it.severity == RiskFactorSeverity.MINOR }
        .count { answers[it.id]?.uppercase() == "YES" }

    return when {
        majorCount >= 1 -> RiskLevel.HIGH
        minorCount >= 2 -> RiskLevel.ELEVATED
        else -> RiskLevel.LOW
    }
}

/**
 * Warning message displayed when the risk level is ELEVATED or HIGH.
 *
 * This message is critical — it makes clear that the questionnaire is NOT a hearing test
 * and that a clinical screening is required.
 */
const val QUESTIONNAIRE_WARNING_MESSAGE =
    "This checklist alone cannot confirm hearing status. " +
    "This is NOT a hearing test — it is a risk-factor screening only. " +
    "This baby should receive a probe-based hearing screening or audiology referral " +
    "as soon as possible."

/**
 * Result summary message for each risk level.
 */
fun riskLevelSummary(riskLevel: RiskLevel): String = when (riskLevel) {
    RiskLevel.LOW -> "No major risk factors identified. Continue routine hearing monitoring."
    RiskLevel.ELEVATED -> "Two or more minor risk factors identified. " + QUESTIONNAIRE_WARNING_MESSAGE
    RiskLevel.HIGH -> "One or more major risk factors identified. " + QUESTIONNAIRE_WARNING_MESSAGE
}
