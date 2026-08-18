package com.neohear.ui.questionnaire

import com.neohear.data.entity.RiskLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the risk level computation logic.
 *
 * These tests verify that the placeholder scoring rule produces the expected
 * risk levels for various answer combinations.
 */
class RiskLevelComputationTest {

    private fun answerMap(vararg pairs: Pair<String, String>): Map<String, String> =
        mapOf(*pairs)

    private val allNoAnswers: Map<String, String> = RiskFactorCatalog.factors.associate { it.id to "NO" }

    // ── LOW risk tests ───────────────────────────────────────────────────

    @Test
    fun allNoAnswers_returnsLow() {
        val result = computeRiskLevel(allNoAnswers)
        assertEquals(RiskLevel.LOW, result)
    }

    @Test
    fun oneMinorFactor_returnsLow() {
        val answers = allNoAnswers + ("low_apgar" to "YES")
        assertEquals(RiskLevel.LOW, computeRiskLevel(answers))
    }

    @Test
    fun emptyAnswers_returnsLow() {
        assertEquals(RiskLevel.LOW, computeRiskLevel(emptyMap()))
    }

    // ── HIGH risk tests (any single major factor) ────────────────────────

    @Test
    fun familyHistory_returnsHigh() {
        val answers = allNoAnswers + ("family_history" to "YES")
        assertEquals(RiskLevel.HIGH, computeRiskLevel(answers))
    }

    @Test
    fun nicuStay_returnsHigh() {
        val answers = allNoAnswers + ("nicu_stay" to "YES")
        assertEquals(RiskLevel.HIGH, computeRiskLevel(answers))
    }

    @Test
    fun lowBirthWeight_returnsHigh() {
        val answers = allNoAnswers + ("low_birth_weight" to "YES")
        assertEquals(RiskLevel.HIGH, computeRiskLevel(answers))
    }

    @Test
    fun inUteroInfection_returnsHigh() {
        val answers = allNoAnswers + ("in_utero_infection" to "YES")
        assertEquals(RiskLevel.HIGH, computeRiskLevel(answers))
    }

    @Test
    fun craniofacial_returnsHigh() {
        val answers = allNoAnswers + ("craniofacial" to "YES")
        assertEquals(RiskLevel.HIGH, computeRiskLevel(answers))
    }

    @Test
    fun ototoxicMedication_returnsHigh() {
        val answers = allNoAnswers + ("ototoxic_medication" to "YES")
        assertEquals(RiskLevel.HIGH, computeRiskLevel(answers))
    }

    @Test
    fun hyperbilirubinemia_returnsHigh() {
        val answers = allNoAnswers + ("hyperbilirubinemia" to "YES")
        assertEquals(RiskLevel.HIGH, computeRiskLevel(answers))
    }

    @Test
    fun majorFactorWithMinorFactors_stillHigh() {
        // Even with minor factors, one major = HIGH
        val answers = allNoAnswers + mapOf(
            "family_history" to "YES",
            "low_apgar" to "YES",
            "mechanical_ventilation" to "YES"
        )
        assertEquals(RiskLevel.HIGH, computeRiskLevel(answers))
    }

    @Test
    fun multipleMajorFactors_returnsHigh() {
        val answers = allNoAnswers + mapOf(
            "family_history" to "YES",
            "nicu_stay" to "YES",
            "low_birth_weight" to "YES"
        )
        assertEquals(RiskLevel.HIGH, computeRiskLevel(answers))
    }

    // ── ELEVATED risk tests (≥2 minor factors) ──────────────────────────

    @Test
    fun twoMinorFactors_returnsElevated() {
        val answers = allNoAnswers + mapOf(
            "low_apgar" to "YES",
            "mechanical_ventilation" to "YES"
        )
        assertEquals(RiskLevel.ELEVATED, computeRiskLevel(answers))
    }

    @Test
    fun twoMinorFactorsNoMajor_returnsElevated() {
        // Verify major factors are all NO
        val answers = mapOf(
            "family_history" to "NO",
            "nicu_stay" to "NO",
            "low_birth_weight" to "NO",
            "in_utero_infection" to "NO",
            "craniofacial" to "NO",
            "ototoxic_medication" to "NO",
            "hyperbilirubinemia" to "NO",
            "low_apgar" to "YES",
            "mechanical_ventilation" to "YES"
        )
        assertEquals(RiskLevel.ELEVATED, computeRiskLevel(answers))
    }

    // ── Edge cases ───────────────────────────────────────────────────────

    @Test
    fun yesCaseInsensitive_treatedAsYes() {
        val answers = allNoAnswers + ("family_history" to "yes")
        assertEquals(RiskLevel.HIGH, computeRiskLevel(answers))
    }

    @Test
    fun yesUpperCase_treatedAsYes() {
        val answers = allNoAnswers + mapOf("low_apgar" to "YES", "mechanical_ventilation" to "YES")
        assertEquals(RiskLevel.ELEVATED, computeRiskLevel(answers))
    }

    @Test
    fun unknownAnswerKey_ignored() {
        val answers = allNoAnswers + ("nonexistent_factor" to "YES")
        assertEquals(RiskLevel.LOW, computeRiskLevel(answers))
    }

    // ── Risk factor catalog validation ───────────────────────────────────

    @Test
    fun catalogHasNineFactors() {
        assertEquals(9, RiskFactorCatalog.factors.size)
    }

    @Test
    fun catalogHasSevenMajorFactors() {
        val majorCount = RiskFactorCatalog.factors.count { it.severity == RiskFactorSeverity.MAJOR }
        assertEquals(7, majorCount)
    }

    @Test
    fun catalogHasTwoMinorFactors() {
        val minorCount = RiskFactorCatalog.factors.count { it.severity == RiskFactorSeverity.MINOR }
        assertEquals(2, minorCount)
    }

    @Test
    fun allFactorIdsAreUnique() {
        val ids = RiskFactorCatalog.factors.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun allFactorQuestionsAreNonBlank() {
        RiskFactorCatalog.factors.forEach { factor ->
            assertTrue("Question for ${factor.id} should not be blank", factor.question.isNotBlank())
            assertTrue("Short label for ${factor.id} should not be blank", factor.shortLabel.isNotBlank())
        }
    }

    // ── Warning message tests ────────────────────────────────────────────

    @Test
    fun highRiskSummary_containsWarningMessage() {
        val summary = riskLevelSummary(RiskLevel.HIGH)
        assertTrue(
            "HIGH summary should contain the warning message",
            summary.contains(QUESTIONNAIRE_WARNING_MESSAGE)
        )
    }

    @Test
    fun elevatedRiskSummary_containsWarningMessage() {
        val summary = riskLevelSummary(RiskLevel.ELEVATED)
        assertTrue(
            "ELEVATED summary should contain the warning message",
            summary.contains(QUESTIONNAIRE_WARNING_MESSAGE)
        )
    }

    @Test
    fun lowRiskSummary_doesNotContainWarningMessage() {
        val summary = riskLevelSummary(RiskLevel.LOW)
        assertTrue(
            "LOW summary should NOT contain the warning message",
            !summary.contains(QUESTIONNAIRE_WARNING_MESSAGE)
        )
    }

    @Test
    fun warningMessage_containsKeyPhrases() {
        assertTrue(
            "Warning should mention 'cannot confirm hearing status'",
            QUESTIONNAIRE_WARNING_MESSAGE.contains("cannot confirm hearing status")
        )
        assertTrue(
            "Warning should mention 'probe-based' and 'screening'",
            QUESTIONNAIRE_WARNING_MESSAGE.contains("probe-based") &&
                    QUESTIONNAIRE_WARNING_MESSAGE.contains("screening")
        )
        assertTrue(
            "Warning should mention 'audiology referral'",
            QUESTIONNAIRE_WARNING_MESSAGE.contains("audiology referral")
        )
    }
}
