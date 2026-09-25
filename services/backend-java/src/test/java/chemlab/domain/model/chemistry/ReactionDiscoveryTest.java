package chemlab.domain.model.chemistry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the recordDiscovery domain method that encapsulates discovery event logic.
 */
class ReactionDiscoveryTest {

    @Test
    @DisplayName("recordDiscovery with title updates first and last discovered fields")
    void recordDiscovery_withFirstDiscovery() {
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("H", 2);
        elements.put("O", 1);
        Reaction reaction = new Reaction(elements);

        // Verify initial state (all discovery fields are null/0)
        assertNull(reaction.getFirstDiscoveredWhen());
        assertNull(reaction.getFirstDiscoveredBy());
        assertNull(reaction.getLastDiscoveredWhen());
        assertNull(reaction.getLastDiscoveredBy());
        assertEquals(0, reaction.getDiscoveredCount());
        assertNull(reaction.getTitle());

        // Perform first discovery with test user
        Instant now = java.time.Instant.now();
        reaction.recordDiscovery("user-100", Optional.of("Water - Essential for Life"), now);

        // Verify all fields are set correctly
        assertNotNull(reaction.getFirstDiscoveredWhen());
        assertEquals("user-100", reaction.getFirstDiscoveredBy());
        assertNotNull(reaction.getLastDiscoveredWhen());
        assertEquals("user-100", reaction.getLastDiscoveredBy());
        assertEquals(1, reaction.getDiscoveredCount());
        assertEquals("Water - Essential for Life", reaction.getTitle());
    }

    @Test
    @DisplayName("recordDiscovery without title updates only last discovered fields")
    void recordDiscovery_withSubsequentDiscovery() {
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("Na", 1);
        elements.put("Cl", 1);
        Reaction reaction = new Reaction(elements);

        // First discovery (with title)
        Instant now = java.time.Instant.now();
        reaction.recordDiscovery("user-100", Optional.of("Sodium Chloride"), now);

        // Perform subsequent discovery (no title change) - use plusSeconds for proper Instant creation
        Instant later = now.plusSeconds(1);
        reaction.recordDiscovery("user-200", Optional.empty(), later);

        // Verify first discovery fields are unchanged
        assertEquals("user-100", reaction.getFirstDiscoveredBy());
        assertNotNull(reaction.getFirstDiscoveredWhen());
        assertEquals("Sodium Chloride", reaction.getTitle());

        // Verify last discovery fields are updated
        assertEquals("user-200", reaction.getLastDiscoveredBy());
        assertNotNull(reaction.getLastDiscoveredWhen());
        // Count should increment
        assertEquals(2, reaction.getDiscoveredCount());
    }

    @Test
    @DisplayName("recordDiscovery ensures consistency between first and last discovered timestamps")
    void recordDiscovery_firstIsAlsoLast() {
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("H", 1);
        Reaction reaction = new Reaction(elements);

        Instant now = java.time.Instant.now();
        reaction.recordDiscovery("user-100", Optional.of("Hydrogen"), now);

        // For a first discovery, the first and last discovered timestamps should be the same
        assertNotNull(reaction.getFirstDiscoveredWhen());
        assertNotNull(reaction.getLastDiscoveredWhen());
        assertEquals(now, reaction.getFirstDiscoveredWhen());
        assertEquals(now, reaction.getLastDiscoveredWhen());
    }

    @Test
    @DisplayName("recordDiscovery handles null elements gracefully")
    void recordDiscovery_withNullElements() {
        Reaction reaction = new Reaction(null);

        // Verify that formula is null when elements are null
        assertNull(reaction.getFormula());

        Instant now = java.time.Instant.now();
        reaction.recordDiscovery("user-100", Optional.of("Test"), now);

        // Should still handle discovery metadata even with null formula
        assertNotNull(reaction.getLastDiscoveredWhen());
    }

    @Test
    @DisplayName("recordDiscovery initializes count to 0 for new reactions")
    void recordDiscovery_initialCount() {
        HashMap<String, Integer> elements = new HashMap<>();
        Reaction reaction = new Reaction(elements);

        // New reaction should have discoveredCount of 0 before any discovery
        assertEquals(0, reaction.getDiscoveredCount());

        // First discovery increments to 1
        Instant now = java.time.Instant.now();
        reaction.recordDiscovery("user-100", Optional.of("Test"), now);

        assertEquals(1, reaction.getDiscoveredCount());
    }
}
