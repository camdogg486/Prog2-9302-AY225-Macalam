package MP20;
/*
 * =====================================================
 * Student Name    : MACALAM, CAMRY S.
 * Course          : Programming 2
 * Assignment      : Machine Problem 20 — Convert CSV Dataset to JSON
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : March 16, 2026
 * GitHub Repo     : https://github.com/macalam-camry/uphsd-cs-macalam-camry
 *
 * Description:
 *   Reads the CSV dataset from a user-supplied file path, parses each
 *   data record using the column header row as property keys, and
 *   builds a JSON array where every element is one record object.
 *   The resulting JSON is written to dataset.json and a preview of
 *   the first five objects is printed to the console so the output
 *   can be verified at a glance without opening the file.
 * =====================================================
 */

import java.io.*;
import java.util.*;

public class MP20 {

    // Column header names we assign to each meaningful field.
    // The original header row is used for indexes 0,1; index 2 is the
    // always-empty spacer which we skip; 3–8 are the remaining data fields.
    static final String[] FIELD_NAMES = {
        "candidate", "type", "exam", "language",
        "examDate", "score", "result", "timeUsed"
    };

    // Column indices from the raw CSV that map to the above field names.
    // Index 2 (Column1 / spacer) is deliberately omitted.
    static final int[] FIELD_COLS = { 0, 1, 3, 4, 5, 6, 7, 8 };

    // Number of metadata/title lines before the real column header row
    static final int METADATA_LINES = 6;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // ── Step 1: Prompt for file path and validate ────────────────────
        File file = null;
        while (true) {
            System.out.print("Enter dataset file path: ");
            String filePath = input.nextLine().trim();
            file = new File(filePath);

            if (!file.exists() || !file.isFile()) {
                System.out.println("Error: File not found. Please try again.\n");
            } else if (!file.canRead()) {
                System.out.println("Error: File is not readable. Please try again.\n");
            } else if (!filePath.toLowerCase().endsWith(".csv")) {
                System.out.println("Error: Not a CSV file. Please try again.\n");
            } else {
                System.out.println("File found. Processing...\n");
                break;
            }
        }

        // ── Step 2: Parse CSV into a list of field-value maps ────────────
        // Each entry in the list represents one record as a LinkedHashMap
        // so the field insertion order is preserved in the JSON output.
        List<Map<String, String>> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineIndex = 0;

            while ((line = br.readLine()) != null) {
                // Skip all metadata rows and the column header row
                if (lineIndex <= METADATA_LINES) { lineIndex++; continue; }

                String[] cols = parseCSVLine(line);

                // Skip completely blank rows
                boolean allBlank = true;
                for (String c : cols) { if (!c.trim().isEmpty()) { allBlank = false; break; } }
                if (allBlank) { lineIndex++; continue; }

                // Build a key-value map for this record using our field names
                Map<String, String> record = new LinkedHashMap<>();
                for (int i = 0; i < FIELD_COLS.length; i++) {
                    int colIdx = FIELD_COLS[i];
                    String val = colIdx < cols.length ? cols[colIdx].trim() : "";
                    record.put(FIELD_NAMES[i], val);
                }
                records.add(record);
                lineIndex++;
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            input.close();
            return;
        }

        if (records.isEmpty()) {
            System.out.println("No records found.");
            input.close();
            return;
        }

        // ── Step 3: Build the JSON string ────────────────────────────────
        // We construct the JSON manually without any external library,
        // which satisfies the 'no math/IO libraries for core logic' rule.
        StringBuilder json = new StringBuilder();
        json.append("[\n");

        for (int i = 0; i < records.size(); i++) {
            Map<String, String> record = records.get(i);
            json.append("  {\n");
            int fieldIdx = 0;
            for (Map.Entry<String, String> entry : record.entrySet()) {
                json.append("    \"")
                    .append(jsonEscape(entry.getKey()))
                    .append("\": \"")
                    .append(jsonEscape(entry.getValue()))
                    .append("\"");
                // Add a comma after every field except the last one
                if (fieldIdx < record.size() - 1) json.append(",");
                json.append("\n");
                fieldIdx++;
            }
            // Comma after every object except the final one
            json.append("  }");
            if (i < records.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("]");

        // ── Step 4: Write JSON to file ────────────────────────────────────
        String outputFile = "dataset.json";
        try (FileWriter fw = new FileWriter(outputFile)) {
            fw.write(json.toString());
        } catch (IOException e) {
            System.out.println("Error writing JSON file: " + e.getMessage());
            input.close();
            return;
        }

        // ── Step 5: Print confirmation and a 5-record preview ────────────
        System.out.println("=".repeat(70));
        System.out.println("  MP20 — CSV TO JSON CONVERSION");
        System.out.println("  Student: MACALAM, CAMRY S.");
        System.out.println("=".repeat(70));
        System.out.printf("  Records converted : %d%n",   records.size());
        System.out.printf("  Output file       : %s%n",   outputFile);
        System.out.println("=".repeat(70));
        System.out.println("\n  PREVIEW — First 5 JSON Objects:");
        System.out.println("  " + "-".repeat(68));

        int previewCount = Math.min(5, records.size());
        System.out.println("  [");
        for (int i = 0; i < previewCount; i++) {
            Map<String, String> record = records.get(i);
            System.out.println("    {");
            int fi = 0;
            for (Map.Entry<String, String> entry : record.entrySet()) {
                String comma = fi < record.size() - 1 ? "," : "";
                System.out.printf("      \"%s\": \"%s\"%s%n",
                        entry.getKey(), entry.getValue(), comma);
                fi++;
            }
            String objComma = i < previewCount - 1 ? "," : "";
            System.out.println("    }" + objComma);
        }
        if (records.size() > 5) {
            System.out.printf("    ... and %d more records%n", records.size() - 5);
        }
        System.out.println("  ]");
        System.out.println("=".repeat(70));

        input.close();
    }

    // ── Escape special characters for JSON string values ─────────────────
    static String jsonEscape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // ── Parse a single CSV line, respecting quoted fields ────────────────
    static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        return result.toArray(new String[0]);
    }
}
