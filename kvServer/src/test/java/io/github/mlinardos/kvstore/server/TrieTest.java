package io.github.mlinardos.kvstore.server;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Tests Trie insert / search / delete, including shared-prefix and concurrency.
class TrieTest {

    // ----- insert / search -------------------------------------------------

    @Test
    void searchReturnsValueForInsertedKey() {
        Trie trie = new Trie();
        trie.insert("apple", 42);

        assertEquals(42, trie.search("apple"));
    }

    @Test
    void searchMissingKeyThrows() {
        Trie trie = new Trie();
        trie.insert("apple", 1);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> trie.search("banana"));
        assertEquals("Key not found", ex.getMessage());
    }

    @Test
    void searchPrefixOfInsertedKeyThrows() {
        Trie trie = new Trie();
        trie.insert("apple", 1);

        // "app" is a path in the trie but was never marked as a complete key.
        assertThrows(RuntimeException.class, () -> trie.search("app"));
    }

    @Test
    void insertOverwritesExistingValue() {
        Trie trie = new Trie();
        trie.insert("key", "first");
        trie.insert("key", "second");

        assertEquals("second", trie.search("key"));
    }

    @Test
    void keysSharingPrefixCoexist() {
        Trie trie = new Trie();
        trie.insert("app", 1);
        trie.insert("apple", 2);
        trie.insert("apply", 3);

        assertEquals(1, trie.search("app"));
        assertEquals(2, trie.search("apple"));
        assertEquals(3, trie.search("apply"));
    }

    @Test
    void nullValueIsStoredAndDistinctFromAbsentKey() {
        Trie trie = new Trie();
        trie.insert("present", null);

        // A present key with a null value returns null rather than throwing,
        // which is how callers can tell it apart from an absent key.
        assertNull(trie.search("present"));
        assertThrows(RuntimeException.class, () -> trie.search("absent"));
    }

    @Test
    void emptyStringKeyIsSupported() {
        Trie trie = new Trie();
        trie.insert("", "root-value");

        assertEquals("root-value", trie.search(""));
    }

    // ----- delete ----------------------------------------------------------

    @Test
    void deleteRemovesKey() {
        Trie trie = new Trie();
        trie.insert("apple", 1);

        trie.delete("apple");

        assertThrows(RuntimeException.class, () -> trie.search("apple"));
    }

    @Test
    void deletingPrefixKeyKeepsLongerKey() {
        Trie trie = new Trie();
        trie.insert("app", 1);
        trie.insert("apple", 2);

        trie.delete("app");

        assertThrows(RuntimeException.class, () -> trie.search("app"));
        assertEquals(2, trie.search("apple"), "deleting the prefix must not remove the longer key");
    }

    @Test
    void deletingLongerKeyKeepsPrefixKey() {
        Trie trie = new Trie();
        trie.insert("app", 1);
        trie.insert("apple", 2);

        trie.delete("apple");

        assertEquals(1, trie.search("app"), "deleting the longer key must not remove its prefix");
        assertThrows(RuntimeException.class, () -> trie.search("apple"));
    }

    @Test
    void deletingNonExistentKeyLeavesOthersIntact() {
        Trie trie = new Trie();
        trie.insert("apple", 1);

        trie.delete("banana");

        assertEquals(1, trie.search("apple"));
    }

    @Test
    void deletingPrefixPathThatIsNotACompleteKeyIsNoOp() {
        Trie trie = new Trie();
        trie.insert("apple", 1);

        // "app" exists as a path but was never a complete key.
        trie.delete("app");

        assertEquals(1, trie.search("apple"));
    }

    // ----- concurrency -----------------------------------------------------

    @Test
    void concurrentInsertsAreAllRetrievable() throws InterruptedException {
        Trie trie = new Trie();
        int threads = 8;
        int keysPerThread = 500;
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        for (int t = 0; t < threads; t++) {
            final int threadId = t;
            pool.submit(() -> {
                for (int i = 0; i < keysPerThread; i++) {
                    trie.insert("t" + threadId + "k" + i, threadId * keysPerThread + i);
                }
            });
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS), "inserts did not finish in time");

        for (int t = 0; t < threads; t++) {
            for (int i = 0; i < keysPerThread; i++) {
                assertEquals(t * keysPerThread + i, trie.search("t" + t + "k" + i));
            }
        }
    }
}
