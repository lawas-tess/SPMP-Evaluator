package com.team02.spmpevaluator.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * IEEE 1058 Standard sections and keywords for SPMP documents.
 * Each section represents required components according to IEEE 1058.
 */
public class IEEE1058StandardConstants {

    // IEEE 1058 Required Sections
    public static final Set<String> OVERVIEW_KEYWORDS = new HashSet<>(Arrays.asList(
            "project overview", "overview", "project summary", "introduction",
            "purpose", "goals", "objectives", "scope"
    ));

    public static final Set<String> DOCUMENTATION_PLAN_KEYWORDS = new HashSet<>(Arrays.asList(
            "documentation plan", "documentation", "document plan",
            "documentation requirements", "documentation standards"
    ));

    public static final Set<String> MASTER_SCHEDULE_KEYWORDS = new HashSet<>(Arrays.asList(
            "master schedule", "schedule", "timeline", "milestones",
            "project schedule", "gantt chart", "critical path"
    ));

    public static final Set<String> ORGANIZATION_KEYWORDS = new HashSet<>(Arrays.asList(
            "organization", "project organization", "organizational structure",
            "project structure", "organizational chart"
    ));

    public static final Set<String> STANDARDS_PRACTICES_KEYWORDS = new HashSet<>(Arrays.asList(
            "standards", "practices", "conventions", "coding standards",
            "design standards", "processes", "methodologies"
    ));

    public static final Set<String> RISK_MANAGEMENT_KEYWORDS = new HashSet<>(Arrays.asList(
            "risk management", "risk", "risk assessment", "risk mitigation",
            "risk analysis", "risk strategy", "risk response"
    ));

    public static final Set<String> STAFF_ORGANIZATION_KEYWORDS = new HashSet<>(Arrays.asList(
            "staff organization", "staff", "team organization", "responsibilities",
            "roles", "team structure", "personnel"
    ));

    public static final Set<String> BUDGET_RESOURCE_KEYWORDS = new HashSet<>(Arrays.asList(
            "budget", "resource", "planning", "cost", "estimation",
            "resource allocation", "financial"
    ));

    public static final Set<String> REVIEWS_AUDITS_KEYWORDS = new HashSet<>(Arrays.asList(
            "reviews", "audits", "review process", "audit process",
            "quality assurance", "qa", "testing", "verification"
    ));

    public static final Set<String> PROBLEM_RESOLUTION_KEYWORDS = new HashSet<>(Arrays.asList(
            "problem resolution", "problem", "issue resolution", "issue",
            "escalation", "trouble shooting", "problem management"
    ));

    public static final Set<String> CHANGE_MANAGEMENT_KEYWORDS = new HashSet<>(Arrays.asList(
            "change management", "change", "change control", "change process",
            "configuration management", "ccb", "change board"
    ));

    public static final Set<String> GLOSSARY_APPENDIX_KEYWORDS = new HashSet<>(Arrays.asList(
            "glossary", "appendix", "appendices", "references", "bibliography",
            "definitions", "terms", "abbreviations"
    ));

    // Scoring thresholds
    public static final double MINIMUM_COMPLIANCE_THRESHOLD = 0.80; // 80% for compliance
    public static final double SECTION_PRESENT_SCORE = 100.0;
    public static final double SECTION_ABSENT_SCORE = 0.0;

    // Weights for scoring
    public static final double STRUCTURE_WEIGHT = 0.3; // 30%
    public static final double COMPLETENESS_WEIGHT = 0.7; // 70%
}
