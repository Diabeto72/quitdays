package com.example.quitdaystry.utils;

import com.example.quitdaystry.models.HabitDraft;

import java.time.LocalDate;

/**
 * Input validation helpers for habit and log forms.
 * All methods return a {@link ValidationResult} indicating success or the first failing field.
 */
public class ValidationUtils {

    private ValidationUtils() {}

    /** Carries the result of a validation check. */
    public static class ValidationResult {
        public final boolean ok;
        public final String fieldName;
        public final String message;

        private ValidationResult(boolean ok, String fieldName, String message) {
            this.ok = ok;
            this.fieldName = fieldName;
            this.message = message;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null, null);
        }

        public static ValidationResult failure(String field, String msg) {
            return new ValidationResult(false, field, msg);
        }
    }

    /** Name must be 2–50 non-blank characters. */
    public static ValidationResult validateHabitName(String name) {
        if (name == null || name.trim().isEmpty())
            return ValidationResult.failure("name", "שם ההרגל לא יכול להיות ריק");
        if (name.trim().length() < 2)
            return ValidationResult.failure("name", "שם ההרגל חייב להכיל לפחות 2 תווים");
        if (name.trim().length() > 50)
            return ValidationResult.failure("name", "שם ההרגל לא יכול לעלות על 50 תווים");
        return ValidationResult.success();
    }

    /** Daily cost must be a parseable double ≥ 0 and ≤ 10000. */
    public static ValidationResult validateDailyCost(String costStr) {
        if (costStr == null || costStr.trim().isEmpty()) return ValidationResult.success();
        try {
            double v = Double.parseDouble(costStr.trim());
            if (v < 0) return ValidationResult.failure("dailyCost", "עלות יומית לא יכולה להיות שלילית");
            if (v > 10000) return ValidationResult.failure("dailyCost", "עלות יומית לא יכולה לעלות על 10,000");
        } catch (NumberFormatException e) {
            return ValidationResult.failure("dailyCost", "עלות יומית חייבת להיות מספר תקין");
        }
        return ValidationResult.success();
    }

    /** Quit date must not be in the future and not before 1990. */
    public static ValidationResult validateQuitDate(LocalDate date) {
        if (date == null) return ValidationResult.failure("quitDate", "תאריך גמילה חובה");
        if (date.isAfter(LocalDate.now()))
            return ValidationResult.failure("quitDate", "תאריך גמילה לא יכול להיות בעתיד");
        if (date.getYear() < 1990)
            return ValidationResult.failure("quitDate", "תאריך גמילה לא יכול להיות לפני 1990");
        return ValidationResult.success();
    }

    /** Motivation note must be ≤ 500 chars (nullable). */
    public static ValidationResult validateMotivation(String note) {
        if (note != null && note.length() > 500)
            return ValidationResult.failure("motivationNote", "הערת מוטיבציה לא יכולה לעלות על 500 תווים");
        return ValidationResult.success();
    }

    /** Craving level must be 0–10. If required, must not be null. */
    public static ValidationResult validateCravingLevel(Integer level, boolean required) {
        if (required && level == null)
            return ValidationResult.failure("cravingLevel", "דרגת תשוקה היא שדה חובה");
        if (level != null && (level < 0 || level > 10))
            return ValidationResult.failure("cravingLevel", "דרגת תשוקה חייבת להיות בין 0 ל-10");
        return ValidationResult.success();
    }

    /** Break note must not be blank and ≤ 500 chars. */
    public static ValidationResult validateBreakNote(String note) {
        if (note == null || note.trim().isEmpty())
            return ValidationResult.failure("breakNote", "הערה היא שדה חובה בעת נפילה");
        if (note.length() > 500)
            return ValidationResult.failure("breakNote", "הערה לא יכולה לעלות על 500 תווים");
        return ValidationResult.success();
    }

    /** Validates a full habit draft; returns first failing result or success. */
    public static ValidationResult validateHabit(HabitDraft draft) {
        ValidationResult r;
        r = validateHabitName(draft.name);
        if (!r.ok) return r;
        r = validateQuitDate(draft.quitDate);
        if (!r.ok) return r;
        r = validateDailyCost(draft.dailyCostStr);
        if (!r.ok) return r;
        r = validateMotivation(draft.motivationNote);
        if (!r.ok) return r;
        return ValidationResult.success();
    }
}
