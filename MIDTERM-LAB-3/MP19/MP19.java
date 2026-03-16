/*
 * =====================================================
 * Student Name    : MACALAM, CAMRY S.
 * Course          : Programming 2
 * Assignment      : Machine Problem 19 — Generate Dataset Summary Report
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : March 16, 2026
 * GitHub Repo     : https://github.com/macalam-camry/uphsd-cs-macalam-camry
 *
 * Description:
 *   Reads the CSV dataset from a user-supplied file path, parses every
 *   data record, and generates a comprehensive summary report. The report
 *   covers total record count, pass/fail breakdown, score statistics
 *   (min, max, average, median), candidate type distribution, and a
 *   ranked table of exam frequency. Results are displayed in a formatted
 *   console report and also exported to summary_report.csv.
 * =====================================================
 */

import java.io.*;
import java.util.*;
import java.util.stream.*;

public class MP19 {

    // Number of metadata/title lines before the real column header row
    static final int METADATA_LINES = 6;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // ── Step 1: Ask for file path and validate ───────────────────────
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

        // ── Step 2: Parse the CSV into record lists ──────────────────────
        // Each record stores the eight meaningful field values.
        List<String[]> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineIndex = 0;

            while ((line = br.readLine()) != null) {
                // Skip metadata rows and the header row
                if (lineIndex <= METADATA_LINES) { lineIndex++; continue; }

                String[] cols = parseCSVLine(line);

                // Skip completely empty rows
                boolean allBlank = true;
                for (String c : cols) { if (!c.trim().isEmpty()) { allBlank = false; break; } }
                if (allBlank) { lineIndex++; continue; }

                records.add(cols);
                lineIndex++;
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            input.close();
            return;
        }

        if (records.isEmpty()) {
            System.out.println("No data records found.");
            input.close();
            return;
        }

        // ── Step 3: Compute all statistics ──────────────────────────────

        // Score list — collected from column 6
        List<Integer> scores = new ArrayList<>();
        // Pass/fail counts — derived from column 7
        int passCount = 0, failCount = 0;
        // Candidate type tallies — column 1 (Student / Faculty / NTE)
        Map<String, Integer> typeMap = new LinkedHashMap<>();
        // Exam frequency — column 3
        Map<String, Integer> examMap = new LinkedHashMap<>();

        for (String[] row : records) {
            // Score
            String scoreStr = safe(row, 6);
            try { scores.add(Integer.parseInt(scoreStr)); } catch (NumberFormatException ignored) {}

            // Result
            String result = safe(row, 7);
            if (result.equalsIgnoreCase("PASS")) passCount++;
            else if (result.equalsIgnoreCase("FAIL")) failCount++;

            // Candidate type
            String type = safe(row, 1);
            typeMap.merge(type, 1, Integer::sum);

            // Exam title
            String exam = safe(row, 3);
            examMap.merge(exam, 1, Integer::sum);
        }

        // Sort scores for median calculation
        Collections.sort(scores);
        int    minScore  = scores.isEmpty() ? 0 : scores.get(0);
        int    maxScore  = scores.isEmpty() ? 0 : scores.get(scores.size() - 1);
        double avgScore  = scores.isEmpty() ? 0 : scores.stream().mapToInt(i -> i).average().orElse(0);
        double medScore  = scores.isEmpty() ? 0 : calcMedian(scores);

        // Sort exam map by frequency descending
        List<Map.Entry<String, Integer>> examList = new ArrayList<>(examMap.entrySet());
        examList.sort((a, b) -> b.getValue() - a.getValue());

        // ── Step 4: Display the summary report ──────────────────────────
        String SEP  = "=".repeat(70);
        String DASH = "-".repeat(70);

        System.out.println(SEP);
        System.out.println("  MP19 — DATASET SUMMARY REPORT");
        System.out.println("  Student: MACALAM, CAMRY S.");
        System.out.println("  Source : " + file.getName());
        System.out.println(SEP);

        // General counts
        System.out.println("\n  [1] GENERAL OVERVIEW");
        System.out.println("  " + DASH);
        System.out.printf("  %-35s : %d%n", "Total Records",       records.size());
        System.out.printf("  %-35s : %d%n", "PASS",                passCount);
        System.out.printf("  %-35s : %d%n", "FAIL",                failCount);
        System.out.printf("  %-35s : %.1f%%%n","Pass Rate",
                records.isEmpty() ? 0.0 : (passCount * 100.0 / records.size()));

        // Score statistics
        System.out.println("\n  [2] SCORE STATISTICS");
        System.out.println("  " + DASH);
        System.out.printf("  %-35s : %d%n",    "Minimum Score",  minScore);
        System.out.printf("  %-35s : %d%n",    "Maximum Score",  maxScore);
        System.out.printf("  %-35s : %.2f%n",  "Average Score",  avgScore);
        System.out.printf("  %-35s : %.1f%n",  "Median Score",   medScore);

        // Candidate type breakdown
        System.out.println("\n  [3] CANDIDATE TYPE BREAKDOWN");
        System.out.println("  " + DASH);
        for (Map.Entry<String, Integer> e : typeMap.entrySet()) {
            double pct = e.getValue() * 100.0 / records.size();
            System.out.printf("  %-35s : %d (%.1f%%)%n", e.getKey(), e.getValue(), pct);
        }

        // Exam frequency table (top 10)
        System.out.println("\n  [4] TOP EXAMS BY NUMBER OF TAKERS");
        System.out.println("  " + DASH);
        System.out.printf("  %-3s  %-45s  %s%n", "RNK", "Exam Title", "Count");
        System.out.println("  " + DASH);
        int rank = 1;
        for (Map.Entry<String, Integer> e : examList) {
            System.out.printf("  %-3d  %-45s  %d%n",
                    rank++, truncate(e.getKey(), 45), e.getValue());
        }

        System.out.println("\n" + SEP);
        System.out.println("  Report generated. Exporting to summary_report.csv...");
        System.out.println(SEP);

        // ── Step 5: Export to summary_report.csv ────────────────────────
        exportReport(records.size(), passCount, failCount, avgScore, medScore,
                     minScore, maxScore, typeMap, examList);

        input.close();
    }

    // ── Calculate median from a sorted list of integers ─────────────────
    static double calcMedian(List<Integer> sorted) {
        int n = sorted.size();
        if (n % 2 == 0) return (sorted.get(n/2 - 1) + sorted.get(n/2)) / 2.0;
        return sorted.get(n / 2);
    }

    // ── Export the summary statistics as a CSV file ──────────────────────
    static void exportReport(int total, int pass, int fail, double avg, double med,
                              int min, int max,
                              Map<String, Integer> typeMap,
                              List<Map.Entry<String, Integer>> examList) {
        String outFile = "summary_report.csv";
        try (FileWriter fw = new FileWriter(outFile)) {
            // Section 1 – overview
            fw.write("SECTION,METRIC,VALUE\n");
            fw.write("Overview,Total Records," + total + "\n");
            fw.write("Overview,PASS," + pass + "\n");
            fw.write("Overview,FAIL," + fail + "\n");
            fw.write(String.format("Overview,Pass Rate (%%),%.1f%n", pass * 100.0 / total));
            fw.write(String.format("Score Stats,Minimum,%d%n", min));
            fw.write(String.format("Score Stats,Maximum,%d%n", max));
            fw.write(String.format("Score Stats,Average,%.2f%n", avg));
            fw.write(String.format("Score Stats,Median,%.1f%n", med));
            fw.write("\n");

            // Section 2 – candidate type breakdown
            fw.write("CANDIDATE TYPE,COUNT,PERCENTAGE\n");
            for (Map.Entry<String, Integer> e : typeMap.entrySet()) {
                fw.write(String.format("%s,%d,%.1f%n",
                        escapeCSV(e.getKey()), e.getValue(),
                        e.getValue() * 100.0 / total));
            }
            fw.write("\n");

            // Section 3 – exam frequency
            fw.write("RANK,EXAM TITLE,COUNT\n");
            int rank = 1;
            for (Map.Entry<String, Integer> e : examList) {
                fw.write(rank++ + "," + escapeCSV(e.getKey()) + "," + e.getValue() + "\n");
            }

            System.out.println("  Exported to: " + outFile);
        } catch (IOException e) {
            System.out.println("  Export error: " + e.getMessage());
        }
    }

    // ── Safely get a column value, returning "[empty]" if missing ────────
    static String safe(String[] cols, int idx) {
        if (idx >= cols.length || cols[idx].trim().isEmpty()) return "[empty]";
        return cols[idx].trim();
    }

    // ── Truncate a string for display ────────────────────────────────────
    static String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 3) + "..." : s;
    }

    // ── Escape a value for CSV output ────────────────────────────────────
    static String escapeCSV(String v) {
        if (v.contains(",") || v.contains("\"") || v.contains("\n"))
            return "\"" + v.replace("\"", "\"\"") + "\"";
        return v;
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
