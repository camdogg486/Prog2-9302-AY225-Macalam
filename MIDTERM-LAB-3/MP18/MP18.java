/*
 * =====================================================
 * Student Name    : MACALAM, CAMRY S.
 * Course          : Programming 2
 * Assignment      : Machine Problem 18 — Remove Rows with Empty Fields
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : March 16, 2026
 * GitHub Repo     : https://github.com/macalam-camry/uphsd-cs-macalam-camry
 *
 * Description:
 *   Reads a CSV dataset from a user-supplied file path, skips the
 *   metadata header rows, then scans each data record for any empty
 *   field in the nine meaningful columns (Candidate, Type, Exam,
 *   Language, Date, Score, Result, Time Used). Rows that pass the
 *   check are collected and displayed in a formatted table, while
 *   rows with at least one empty meaningful field are listed
 *   separately so the user knows exactly what was removed.
 * =====================================================
 */

import java.io.*;
import java.util.*;

public class MP18 {

    // Indices of meaningful columns — Column1 (index 2) is a structural
    // blank spacer in this dataset and is intentionally skipped.
    // Trailing empty columns 9, 10, 11 are also excluded.
    static final int[] MEANINGFUL_COLS = { 0, 1, 3, 4, 5, 6, 7, 8 };

    // Number of metadata/title lines before the real header row
    static final int METADATA_LINES = 6;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // ── Step 1: Ask user for the CSV file path and validate it ──────
        File file = null;
        while (true) {
            System.out.print("Enter dataset file path: ");
            String path = input.nextLine().trim();
            file = new File(path);

            if (!file.exists() || !file.isFile()) {
                System.out.println("Error: File not found. Please try again.\n");
            } else if (!file.canRead()) {
                System.out.println("Error: File is not readable. Please try again.\n");
            } else if (!path.toLowerCase().endsWith(".csv")) {
                System.out.println("Error: Not a CSV file. Please try again.\n");
            } else {
                System.out.println("File found. Processing...\n");
                break;
            }
        }

        // ── Step 2: Read and parse the CSV file ─────────────────────────
        String[] headers = null;          // column labels from row 7
        List<String[]> cleanRows  = new ArrayList<>();  // rows with no empty fields
        List<String[]> removedRows = new ArrayList<>(); // rows that had empty fields
        int totalDataRows = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineIndex = 0;  // tracks which line we are reading (0-based)

            while ((line = br.readLine()) != null) {

                // Skip the first 6 metadata/title rows
                if (lineIndex < METADATA_LINES) {
                    lineIndex++;
                    continue;
                }

                // Line 6 (0-based) is the actual column header row
                if (lineIndex == METADATA_LINES) {
                    headers = parseCSVLine(line);
                    lineIndex++;
                    continue;
                }

                // All remaining lines are data rows
                String[] cols = parseCSVLine(line);

                // Skip completely blank rows (e.g., trailing empty row)
                boolean allBlank = true;
                for (String col : cols) {
                    if (!col.trim().isEmpty()) { allBlank = false; break; }
                }
                if (allBlank) { lineIndex++; continue; }

                totalDataRows++;

                // Check if any meaningful column is empty
                if (hasEmptyField(cols)) {
                    removedRows.add(cols);
                } else {
                    cleanRows.add(cols);
                }

                lineIndex++;
            }

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            input.close();
            return;
        }

        // ── Step 3: Display results ──────────────────────────────────────
        System.out.println("=".repeat(90));
        System.out.println("  MP18 — REMOVE ROWS WITH EMPTY FIELDS");
        System.out.println("  Student: MACALAM, CAMRY S.");
        System.out.println("=".repeat(90));
        System.out.printf("  Total data rows scanned  : %d%n", totalDataRows);
        System.out.printf("  Rows removed (has empty) : %d%n", removedRows.size());
        System.out.printf("  Clean rows retained      : %d%n", cleanRows.size());
        System.out.println("=".repeat(90));

        // Display removed rows (if any)
        if (!removedRows.isEmpty()) {
            System.out.println("\n  REMOVED ROWS (contained at least one empty field):");
            System.out.println("  " + "-".repeat(88));
            System.out.printf("  %-30s | %-10s | %-30s | %-6s | %-6s%n",
                    "Candidate", "Type", "Exam", "Score", "Result");
            System.out.println("  " + "-".repeat(88));
            for (String[] row : removedRows) {
                System.out.printf("  %-30s | %-10s | %-30s | %-6s | %-6s%n",
                        safe(row, 0), safe(row, 1), safe(row, 3),
                        safe(row, 6), safe(row, 7));
            }
        } else {
            System.out.println("\n  No rows were removed — all records have complete fields.");
        }

        // Display clean rows table
        System.out.println("\n  CLEAN DATASET (" + cleanRows.size() + " records):");
        System.out.println("  " + "-".repeat(88));
        System.out.printf("  %-30s | %-10s | %-30s | %-6s | %-6s%n",
                "Candidate", "Type", "Exam", "Score", "Result");
        System.out.println("  " + "-".repeat(88));
        for (String[] row : cleanRows) {
            System.out.printf("  %-30s | %-10s | %-30s | %-6s | %-6s%n",
                    safe(row, 0), safe(row, 1), truncate(safe(row, 3), 30),
                    safe(row, 6), safe(row, 7));
        }
        System.out.println("=".repeat(90));

        input.close();
    }

    // ── Check if any meaningful column in a row is empty ────────────────
    static boolean hasEmptyField(String[] cols) {
        for (int idx : MEANINGFUL_COLS) {
            if (idx >= cols.length || cols[idx].trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // ── Safely get a column value, returning "[empty]" if missing ───────
    static String safe(String[] cols, int idx) {
        if (idx >= cols.length || cols[idx].trim().isEmpty()) return "[empty]";
        return cols[idx].trim();
    }

    // ── Truncate long strings for display ────────────────────────────────
    static String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 3) + "..." : s;
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
