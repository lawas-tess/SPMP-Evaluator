package com.team02.spmpevaluator.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ParserConfiguration entity.
 */
@DisplayName("ParserConfiguration Entity Tests")
class ParserConfigurationTest {

    private ParserConfiguration config;
    private User user;

    @BeforeEach
    void setUp() {
        config = new ParserConfiguration();
        user = new User();
        user.setId(1L);
        user.setUsername("professor");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty entity with no-args constructor")
        void noArgsConstructor_CreatesEmptyEntity() {
            ParserConfiguration entity = new ParserConfiguration();
            assertNotNull(entity);
            assertNull(entity.getId());
            assertNull(entity.getName());
        }

        @Test
        @DisplayName("Should create entity with all-args constructor")
        void allArgsConstructor_CreatesPopulatedEntity() {
            LocalDateTime now = LocalDateTime.now();
            ParserConfiguration entity = new ParserConfiguration(
                    1L, "Default Config", "Standard IEEE 1058 parser",
                    "[{\"clauseId\": \"1.1\"}]", "[{\"ruleId\": \"R1\"}]",
                    user, now, now, true, true);

            assertEquals(1L, entity.getId());
            assertEquals("Default Config", entity.getName());
            assertEquals("Standard IEEE 1058 parser", entity.getDescription());
            assertEquals("[{\"clauseId\": \"1.1\"}]", entity.getClauseMappings());
            assertEquals("[{\"ruleId\": \"R1\"}]", entity.getCustomRules());
            assertEquals(user, entity.getCreatedBy());
            assertEquals(now, entity.getCreatedAt());
            assertEquals(now, entity.getUpdatedAt());
            assertTrue(entity.getIsActive());
            assertTrue(entity.getIsDefault());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void testId() {
            config.setId(100L);
            assertEquals(100L, config.getId());
        }

        @Test
        @DisplayName("Should set and get name")
        void testName() {
            config.setName("Custom Configuration");
            assertEquals("Custom Configuration", config.getName());
        }

        @Test
        @DisplayName("Should set and get description")
        void testDescription() {
            config.setDescription("A custom parser configuration for advanced analysis");
            assertEquals("A custom parser configuration for advanced analysis", config.getDescription());
        }

        @Test
        @DisplayName("Should set and get clause mappings")
        void testClauseMappings() {
            String mappings = "[{\"clauseId\": \"1.1\", \"clauseName\": \"Purpose\", \"weight\": 10}]";
            config.setClauseMappings(mappings);
            assertEquals(mappings, config.getClauseMappings());
        }

        @Test
        @DisplayName("Should set and get custom rules")
        void testCustomRules() {
            String rules = "[{\"ruleId\": \"R1\", \"description\": \"Check completeness\"}]";
            config.setCustomRules(rules);
            assertEquals(rules, config.getCustomRules());
        }

        @Test
        @DisplayName("Should set and get createdBy")
        void testCreatedBy() {
            config.setCreatedBy(user);
            assertEquals(user, config.getCreatedBy());
        }

        @Test
        @DisplayName("Should set and get createdAt")
        void testCreatedAt() {
            LocalDateTime now = LocalDateTime.now();
            config.setCreatedAt(now);
            assertEquals(now, config.getCreatedAt());
        }

        @Test
        @DisplayName("Should set and get updatedAt")
        void testUpdatedAt() {
            LocalDateTime now = LocalDateTime.now();
            config.setUpdatedAt(now);
            assertEquals(now, config.getUpdatedAt());
        }

        @Test
        @DisplayName("Should set and get isActive")
        void testIsActive() {
            config.setIsActive(false);
            assertFalse(config.getIsActive());
            config.setIsActive(true);
            assertTrue(config.getIsActive());
        }

        @Test
        @DisplayName("Should set and get isDefault")
        void testIsDefault() {
            config.setIsDefault(true);
            assertTrue(config.getIsDefault());
            config.setIsDefault(false);
            assertFalse(config.getIsDefault());
        }
    }

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTests {

        @Test
        @DisplayName("Should have isActive default to true")
        void isActiveDefaultTrue() {
            ParserConfiguration entity = new ParserConfiguration();
            assertTrue(entity.getIsActive());
        }

        @Test
        @DisplayName("Should have isDefault default to false")
        void isDefaultDefaultFalse() {
            ParserConfiguration entity = new ParserConfiguration();
            assertFalse(entity.getIsDefault());
        }
    }

    @Nested
    @DisplayName("IEEE 1058 Clause Mapping Tests")
    class ClauseMappingTests {

        @Test
        @DisplayName("Should handle empty clause mappings")
        void emptyClauseMappings() {
            config.setClauseMappings("");
            assertEquals("", config.getClauseMappings());
        }

        @Test
        @DisplayName("Should handle null clause mappings")
        void nullClauseMappings() {
            config.setClauseMappings(null);
            assertNull(config.getClauseMappings());
        }

        @Test
        @DisplayName("Should handle complex JSON clause mappings")
        void complexClauseMappings() {
            String complexMapping = """
                    [
                        {"clauseId": "1.1", "clauseName": "Purpose", "weight": 10, "keywords": ["purpose", "objective"]},
                        {"clauseId": "1.2", "clauseName": "Scope", "weight": 15, "keywords": ["scope", "boundaries"]},
                        {"clauseId": "2.1", "clauseName": "Organization", "weight": 20, "keywords": ["organization", "structure"]}
                    ]
                    """;
            config.setClauseMappings(complexMapping);
            assertNotNull(config.getClauseMappings());
            assertTrue(config.getClauseMappings().contains("clauseId"));
        }
    }

    @Nested
    @DisplayName("Custom Rules Tests")
    class CustomRulesTests {

        @Test
        @DisplayName("Should handle empty custom rules")
        void emptyCustomRules() {
            config.setCustomRules("");
            assertEquals("", config.getCustomRules());
        }

        @Test
        @DisplayName("Should handle complex custom rules")
        void complexCustomRules() {
            String rules = "[{\"ruleId\": \"R1\", \"description\": \"Completeness check\", \"criteria\": \"min_sections >= 10\"}]";
            config.setCustomRules(rules);
            assertTrue(config.getCustomRules().contains("Completeness check"));
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when same id")
        void equalsWithSameId() {
            ParserConfiguration config1 = new ParserConfiguration();
            config1.setId(1L);
            config1.setName("Config 1");

            ParserConfiguration config2 = new ParserConfiguration();
            config2.setId(1L);
            config2.setName("Config 1");

            assertEquals(config1, config2);
            assertEquals(config1.hashCode(), config2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when different ids")
        void notEqualsWithDifferentIds() {
            ParserConfiguration config1 = new ParserConfiguration();
            config1.setId(1L);

            ParserConfiguration config2 = new ParserConfiguration();
            config2.setId(2L);

            assertNotEquals(config1, config2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include key fields in toString")
        void toStringContainsFields() {
            config.setId(1L);
            config.setName("Test Config");
            config.setIsActive(true);

            String str = config.toString();
            assertNotNull(str);
            assertTrue(str.contains("1"));
            assertTrue(str.contains("Test Config"));
        }
    }
}
