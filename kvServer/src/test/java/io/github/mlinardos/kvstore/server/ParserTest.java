package io.github.mlinardos.kvstore.server;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

// Tests Parser's wire format <-> Map conversion.
// Wire format: one  "key" -> value  per line, where value is a string, a number,
// or a nested group of key/values:  [ "k" -> "v" | "k2" -> 42 ]
class ParserTest {

    private final Parser parser = new Parser();

    // ----- parseString -----------------------------------------------------

    @Test
    void parsesFlatLineIntoMap() throws Exception {
        Map<String, Object> result = parser.parseString("\"name\" -> \"John\" | \"age\" -> 30");

        assertEquals("John", result.get("name"));
        assertEquals(30, result.get("age"));
        assertEquals(2, result.size());
    }

    @Test
    void parsesNestedLineIntoNestedMap() throws Exception {
        Map<String, Object> result = parser.parseString(
                "\"person\" -> [ \"city\" -> \"NYC\" | \"zip\" -> 10001 ]");

        assertInstanceOf(Map.class, result.get("person"));
        @SuppressWarnings("unchecked")
        Map<String, Object> person = (Map<String, Object>) result.get("person");
        assertEquals("NYC", person.get("city"));
        assertEquals(10001, person.get("zip"));
    }

    @Test
    void parsesDeeplyNestedLine() throws Exception {
        Map<String, Object> result = parser.parseString(
                "\"a\" -> [ \"b\" -> [ \"c\" -> \"deep\" ] ]");

        @SuppressWarnings("unchecked")
        Map<String, Object> a = (Map<String, Object>) result.get("a");
        @SuppressWarnings("unchecked")
        Map<String, Object> b = (Map<String, Object>) a.get("b");
        assertEquals("deep", b.get("c"));
    }

    @Test
    void distinguishesIntegerDecimalAndStringValues() throws Exception {
        Map<String, Object> result = parser.parseString(
                "\"count\" -> 7 | \"ratio\" -> 1.5 | \"label\" -> \"hello\"");

        assertInstanceOf(Integer.class, result.get("count"));
        assertInstanceOf(Double.class, result.get("ratio"));
        assertInstanceOf(String.class, result.get("label"));
        assertEquals(7, result.get("count"));
        assertEquals(1.5, result.get("ratio"));
        assertEquals("hello", result.get("label"));
    }

    // ----- resultToString --------------------------------------------------

    @Test
    void serializesScalarNumberValue() throws Exception {
        assertEquals("grade -> 90", parser.resultToString("grade", 90));
    }

    @Test
    void serializesStringValueWithQuotes() throws Exception {
        assertEquals("name -> \"John\"", parser.resultToString("name", "John"));
    }

    @Test
    void serializesNestedMapIntoBracketArrowFormat() throws Exception {
        // LinkedHashMap keeps the key order deterministic for the assertion.
        Map<String, Object> value = new LinkedHashMap<>();
        value.put("city", "NYC");
        value.put("zip", 10001);

        String result = parser.resultToString("person", value);

        assertEquals("person -> [\"city\" -> \"NYC\" | \"zip\" -> 10001]", result);
    }

    // ----- round trip ------------------------------------------------------

    @Test
    void parseThenSerializeRecoversLeafValue() throws Exception {
        // Parse a realistic generator-style line, then serialize a leaf back out.
        Map<String, Object> parsed = parser.parseString(
                "\"person\" -> [ \"username\" -> \"gwfuj\" | \"grade\" -> 90 ]");

        @SuppressWarnings("unchecked")
        Map<String, Object> person = (Map<String, Object>) parsed.get("person");

        assertEquals("username -> \"gwfuj\"", parser.resultToString("username", person.get("username")));
        assertEquals("grade -> 90", parser.resultToString("grade", person.get("grade")));
    }

    // Verbatim line copied from dataToIndex.txt, with the generator's real
    // double-space delimiters, to prove production output parses.
    @Test
    void parsesVerbatimGeneratorLine() throws Exception {
        String line = "\"person3\"  ->  [ \"username\"  ->  \"gwfuj\"   | \"grade\"  ->  90   "
                + "| \"timezone\"  ->  \"yi\"   | \"university\"  ->  \"i\"   | \"city\"  ->  \"hu\"   ]";

        Map<String, Object> result = parser.parseString(line);

        @SuppressWarnings("unchecked")
        Map<String, Object> person = (Map<String, Object>) result.get("person3");
        assertEquals("gwfuj", person.get("username"));
        assertEquals(90, person.get("grade"));
        assertEquals("hu", person.get("city"));
        assertEquals(5, person.size());
    }
}
