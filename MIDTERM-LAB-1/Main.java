/*
 * Macalam, Camry S.
 * PROGRAMMING 2 – MACHINE PROBLEM
 * University of Perpetual Help System DALTA – Molino Campus
 * BS Information Technology - Game Development
 * Dataset: https://www.kaggle.com/datasets/asaniczka/video-game-sales-2024
 */

import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        File file = null;

        // ── 1. Ask for file path and validate ──────────────────────────────────
        while (true) {
            System.out.print("Enter dataset file path: ");
            String path = input.nextLine().trim();
            file = new File(path);

            if (!file.exists() || !file.isFile()) {
                System.out.println("Error: File does not exist. Please try again.\n");
            } else if (!file.canRead()) {
                System.out.println("Error: File is not readable. Please try again.\n");
            } else if (!path.toLowerCase().endsWith(".csv")) {
                System.out.println("Error: File is not a CSV file. Please try again.\n");
            } else {
                System.out.println("File found. Loading dataset...\n");
                break;
            }
        }

        // ── 2. Load dataset ────────────────────────────────────────────────────
        List<DataRecord> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String headerLine = br.readLine(); // skip header
            if (headerLine == null) {
                System.out.println("Error: CSV file is empty.");
                return;
            }

            String line;
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                try {
                    String[] cols = parseCSVLine(line);
                    // Expected columns (0-based):
                    // 0:img, 1:title, 2:console, 3:genre, 4:publisher,
                    // 5:developer, 6:total_sales, 7:na_sales, 8:jp_sales,
                    // 9:pal_sales, 10:other_sales, 11:release_date, 12:last_update
                    if (cols.length < 11) continue;

                    String title       = cols[1].trim();
                    String console     = cols[2].trim();
                    String genre       = cols[3].trim();
                    String publisher   = cols[4].trim();
                    String developer   = cols[5].trim();
                    double totalSales  = parseDouble(cols[6]);
                    double naSales     = parseDouble(cols[7]);
                    double jpSales     = parseDouble(cols[8]);
                    double palSales    = parseDouble(cols[9]);
                    double otherSales  = parseDouble(cols[10]);
                    String releaseDate = cols.length > 11 ? cols[11].trim() : "";

                    if (title.isEmpty()) continue;

                    records.add(new DataRecord(title, console, genre, publisher,
                            developer, totalSales, naSales, jpSales, palSales,
                            otherSales, releaseDate));

                } catch (Exception e) {
                    // skip malformed rows silently
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return;
        }

        if (records.isEmpty()) {
            System.out.println("No valid records found in the dataset.");
            return;
        }

        System.out.println("Dataset loaded. Total records: " + records.size() + "\n");

        // ── 3. Perform analytics ───────────────────────────────────────────────
        printSeparator();
        System.out.println("           VIDEO GAME SALES ANALYTICS REPORT");
        printSeparator();

        // 3a. Overall totals
        double grandTotal = records.stream().mapToDouble(DataRecord::getTotalSales).sum();
        double naTotal    = records.stream().mapToDouble(DataRecord::getNaSales).sum();
        double jpTotal    = records.stream().mapToDouble(DataRecord::getJpSales).sum();
        double palTotal   = records.stream().mapToDouble(DataRecord::getPalSales).sum();
        double otherTotal = records.stream().mapToDouble(DataRecord::getOtherSales).sum();

        System.out.println("\n[1] OVERALL SALES SUMMARY");
        System.out.println("-".repeat(50));
        System.out.printf("  Total Games in Dataset  : %,d%n", records.size());
        System.out.printf("  Grand Total Sales       : %.2f million%n", grandTotal);
        System.out.printf("  North America (NA)      : %.2f million%n", naTotal);
        System.out.printf("  Japan (JP)              : %.2f million%n", jpTotal);
        System.out.printf("  PAL Region              : %.2f million%n", palTotal);
        System.out.printf("  Other Regions           : %.2f million%n", otherTotal);

        // 3b. Top 10 games by total sales
        System.out.println("\n[2] TOP 10 GAMES BY TOTAL SALES");
        System.out.println("-".repeat(50));
        records.stream()
                .sorted((a, b) -> Double.compare(b.getTotalSales(), a.getTotalSales()))
                .limit(10)
                .forEach(r -> System.out.printf("  %-40s | %s | %.2f M%n",
                        truncate(r.getTitle(), 40), padRight(r.getConsole(), 6), r.getTotalSales()));

        // 3c. Sales by genre
        System.out.println("\n[3] TOTAL SALES BY GENRE");
        System.out.println("-".repeat(50));
        records.stream()
                .filter(r -> !r.getGenre().isEmpty())
                .collect(Collectors.groupingBy(DataRecord::getGenre,
                        Collectors.summingDouble(DataRecord::getTotalSales)))
                .entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .forEach(e -> System.out.printf("  %-25s : %.2f million%n", e.getKey(), e.getValue()));

        // 3d. Top 10 consoles by total sales
        System.out.println("\n[4] TOP 10 CONSOLES BY TOTAL SALES");
        System.out.println("-".repeat(50));
        records.stream()
                .filter(r -> !r.getConsole().isEmpty())
                .collect(Collectors.groupingBy(DataRecord::getConsole,
                        Collectors.summingDouble(DataRecord::getTotalSales)))
                .entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(10)
                .forEach(e -> System.out.printf("  %-15s : %.2f million%n", e.getKey(), e.getValue()));

        // 3e. Top 10 publishers by total sales
        System.out.println("\n[5] TOP 10 PUBLISHERS BY TOTAL SALES");
        System.out.println("-".repeat(50));
        records.stream()
                .filter(r -> !r.getPublisher().isEmpty())
                .collect(Collectors.groupingBy(DataRecord::getPublisher,
                        Collectors.summingDouble(DataRecord::getTotalSales)))
                .entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(10)
                .forEach(e -> System.out.printf("  %-35s : %.2f million%n", e.getKey(), e.getValue()));

        printSeparator();

        // ── 4. Export summary_report.csv ──────────────────────────────────────
        exportSummaryReport(records, grandTotal, naTotal, jpTotal, palTotal, otherTotal);

        input.close();
    }

    // ── Export to CSV ──────────────────────────────────────────────────────────
    private static void exportSummaryReport(List<DataRecord> records,
                                             double grandTotal, double naTotal,
                                             double jpTotal, double palTotal,
                                             double otherTotal) {
        String outputFile = "summary_report.csv";
        try (FileWriter fw = new FileWriter(outputFile)) {

            // Overall summary section
            fw.write("SECTION,LABEL,VALUE\n");
            fw.write("Overall Summary,Total Games," + records.size() + "\n");
            fw.write("Overall Summary,Grand Total Sales (M)," + String.format("%.2f", grandTotal) + "\n");
            fw.write("Overall Summary,NA Sales (M)," + String.format("%.2f", naTotal) + "\n");
            fw.write("Overall Summary,JP Sales (M)," + String.format("%.2f", jpTotal) + "\n");
            fw.write("Overall Summary,PAL Sales (M)," + String.format("%.2f", palTotal) + "\n");
            fw.write("Overall Summary,Other Sales (M)," + String.format("%.2f", otherTotal) + "\n");
            fw.write("\n");

            // Top 10 games
            fw.write("RANK,TITLE,CONSOLE,GENRE,PUBLISHER,TOTAL_SALES_M\n");
            int[] rank = {1};
            records.stream()
                    .sorted((a, b) -> Double.compare(b.getTotalSales(), a.getTotalSales()))
                    .limit(10)
                    .forEach(r -> {
                        try {
                            fw.write(rank[0]++ + ","
                                    + escapeCSV(r.getTitle()) + ","
                                    + escapeCSV(r.getConsole()) + ","
                                    + escapeCSV(r.getGenre()) + ","
                                    + escapeCSV(r.getPublisher()) + ","
                                    + String.format("%.2f", r.getTotalSales()) + "\n");
                        } catch (IOException ignored) {}
                    });
            fw.write("\n");

            // Sales by genre
            fw.write("GENRE,TOTAL_SALES_M\n");
            records.stream()
                    .filter(r -> !r.getGenre().isEmpty())
                    .collect(Collectors.groupingBy(DataRecord::getGenre,
                            Collectors.summingDouble(DataRecord::getTotalSales)))
                    .entrySet().stream()
                    .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                    .forEach(e -> {
                        try {
                            fw.write(escapeCSV(e.getKey()) + "," + String.format("%.2f", e.getValue()) + "\n");
                        } catch (IOException ignored) {}
                    });

            System.out.println("\nSummary report exported to: " + outputFile);

        } catch (IOException e) {
            System.out.println("Error exporting report: " + e.getMessage());
        }
    }

    // ── Utility methods ────────────────────────────────────────────────────────
    private static double parseDouble(String s) {
        try {
            s = s.trim();
            if (s.isEmpty() || s.equalsIgnoreCase("n/a") || s.equalsIgnoreCase("null")) return 0.0;
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static String[] parseCSVLine(String line) {
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

    private static String escapeCSV(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 3) + "..." : s;
    }

    private static String padRight(String s, int size) {
        return String.format("%-" + size + "s", s);
    }

    private static void printSeparator() {
        System.out.println("=".repeat(60));
    }
}
