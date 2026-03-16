/**
 * =====================================================
 * Student Name    : MACALAM, CAMRY S.
 * Course          : Programming 2
 * Assignment      : Machine Problem 19 — Generate Dataset Summary Report
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : March 16, 2026
 * GitHub Repo     : https://github.com/macalam-camry/uphsd-cs-macalam-camry
 * Runtime         : Node.js — run with: node mp19.js
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

const fs       = require('fs');
const readline = require('readline');
const path     = require('path');

// Number of metadata/title lines before the real column header row
const METADATA_LINES = 6;

// ── Parse a single CSV line, respecting quoted fields ────────────────
function parseCSVLine(line) {
    const result = [];
    let inQuotes = false;
    let current  = '';
    for (const c of line) {
        if (c === '"') {
            inQuotes = !inQuotes;
        } else if (c === ',' && !inQuotes) {
            result.push(current);
            current = '';
        } else {
            current += c;
        }
    }
    result.push(current);
    return result;
}

// ── Safely return a column value or '[empty]' if absent ──────────────
function safe(cols, idx) {
    if (idx >= cols.length || cols[idx].trim() === '') return '[empty]';
    return cols[idx].trim();
}

// ── Pad / truncate a string to a fixed display width ─────────────────
function padRight(str, len) {
    if (str.length > len) return str.substring(0, len - 3) + '...';
    return str.padEnd(len);
}

// ── Escape a field value for CSV output ──────────────────────────────
function escapeCSV(val) {
    const s = String(val);
    if (s.includes(',') || s.includes('"') || s.includes('\n'))
        return '"' + s.replace(/"/g, '""') + '"';
    return s;
}

// ── Calculate median from a sorted numeric array ─────────────────────
function calcMedian(sorted) {
    const n = sorted.length;
    if (n === 0) return 0;
    return n % 2 === 0
        ? (sorted[n/2 - 1] + sorted[n/2]) / 2
        : sorted[Math.floor(n/2)];
}

// ── Load, compute all stats, display report, and export CSV ──────────
function processFile(filePath) {
    const content = fs.readFileSync(filePath, 'utf8');
    const lines   = content.split(/\r?\n/);

    // Collect all valid data rows into an array of parsed column arrays
    const records = [];
    for (let i = 0; i < lines.length; i++) {
        if (i <= METADATA_LINES) continue;          // skip metadata + header
        const cols = parseCSVLine(lines[i]);
        if (cols.every(c => c.trim() === '')) continue; // skip blank rows
        records.push(cols);
    }

    if (records.length === 0) {
        console.log('No data records found.');
        return;
    }

    // Accumulate statistics across all records
    const scores   = [];   // integer scores from column 6
    let passCount  = 0;    // number of PASS results
    let failCount  = 0;    // number of FAIL results
    const typeMap  = {};   // candidate type → count (column 1)
    const examMap  = {};   // exam title → count (column 3)

    for (const row of records) {
        // Score — parse and store only if it's a valid number
        const scoreStr = safe(row, 6);
        const scoreNum = parseInt(scoreStr, 10);
        if (!isNaN(scoreNum)) scores.push(scoreNum);

        // Result — increment the appropriate counter
        const result = safe(row, 7).toUpperCase();
        if (result === 'PASS')      passCount++;
        else if (result === 'FAIL') failCount++;

        // Candidate type — increment the count for this type
        const type = safe(row, 1);
        typeMap[type] = (typeMap[type] || 0) + 1;

        // Exam title — increment the count for this exam
        const exam = safe(row, 3);
        examMap[exam] = (examMap[exam] || 0) + 1;
    }

    // Sort scores ascending for min / max / median
    scores.sort((a, b) => a - b);
    const minScore = scores.length ? scores[0]                              : 0;
    const maxScore = scores.length ? scores[scores.length - 1]              : 0;
    const avgScore = scores.length ? scores.reduce((s,v) => s+v, 0) / scores.length : 0;
    const medScore = calcMedian(scores);

    // Sort exam list by frequency descending for the ranked table
    const examList = Object.entries(examMap).sort((a, b) => b[1] - a[1]);

    // ── Print the formatted report ────────────────────────────────────
    const SEP  = '='.repeat(70);
    const DASH = '-'.repeat(70);
    const total = records.length;

    console.log(SEP);
    console.log('  MP19 — DATASET SUMMARY REPORT');
    console.log('  Student: MACALAM, CAMRY S.');
    console.log('  Source : ' + path.basename(filePath));
    console.log(SEP);

    // Section 1 – general overview
    console.log('\n  [1] GENERAL OVERVIEW');
    console.log('  ' + DASH);
    console.log(`  ${padRight('Total Records', 35)} : ${total}`);
    console.log(`  ${padRight('PASS', 35)} : ${passCount}`);
    console.log(`  ${padRight('FAIL', 35)} : ${failCount}`);
    console.log(`  ${padRight('Pass Rate', 35)} : ${(passCount * 100 / total).toFixed(1)}%`);

    // Section 2 – score stats
    console.log('\n  [2] SCORE STATISTICS');
    console.log('  ' + DASH);
    console.log(`  ${padRight('Minimum Score', 35)} : ${minScore}`);
    console.log(`  ${padRight('Maximum Score', 35)} : ${maxScore}`);
    console.log(`  ${padRight('Average Score', 35)} : ${avgScore.toFixed(2)}`);
    console.log(`  ${padRight('Median Score', 35)} : ${medScore.toFixed(1)}`);

    // Section 3 – candidate type breakdown
    console.log('\n  [3] CANDIDATE TYPE BREAKDOWN');
    console.log('  ' + DASH);
    for (const [type, count] of Object.entries(typeMap)) {
        const pct = (count * 100 / total).toFixed(1);
        console.log(`  ${padRight(type, 35)} : ${count} (${pct}%)`);
    }

    // Section 4 – top exams by taker count
    console.log('\n  [4] TOP EXAMS BY NUMBER OF TAKERS');
    console.log('  ' + DASH);
    console.log(`  ${padRight('RNK', 4)} ${padRight('Exam Title', 45)}  Count`);
    console.log('  ' + DASH);
    examList.forEach(([exam, count], i) => {
        console.log(`  ${padRight(String(i+1), 4)} ${padRight(exam, 45)}  ${count}`);
    });

    console.log('\n' + SEP);
    console.log('  Report generated. Exporting to summary_report.csv...');
    console.log(SEP);

    // ── Export to summary_report.csv ──────────────────────────────────
    exportReport(total, passCount, failCount, avgScore, medScore,
                 minScore, maxScore, typeMap, examList);
}

// ── Write the summary statistics out to a CSV file ───────────────────
function exportReport(total, pass, fail, avg, med, min, max, typeMap, examList) {
    let csv = '';

    // Section 1: overview and score stats
    csv += 'SECTION,METRIC,VALUE\n';
    csv += `Overview,Total Records,${total}\n`;
    csv += `Overview,PASS,${pass}\n`;
    csv += `Overview,FAIL,${fail}\n`;
    csv += `Overview,Pass Rate (%),${(pass * 100 / total).toFixed(1)}\n`;
    csv += `Score Stats,Minimum,${min}\n`;
    csv += `Score Stats,Maximum,${max}\n`;
    csv += `Score Stats,Average,${avg.toFixed(2)}\n`;
    csv += `Score Stats,Median,${med.toFixed(1)}\n\n`;

    // Section 2: candidate type breakdown
    csv += 'CANDIDATE TYPE,COUNT,PERCENTAGE\n';
    for (const [type, count] of Object.entries(typeMap)) {
        csv += `${escapeCSV(type)},${count},${(count * 100 / total).toFixed(1)}\n`;
    }
    csv += '\n';

    // Section 3: exam frequency ranked list
    csv += 'RANK,EXAM TITLE,COUNT\n';
    examList.forEach(([exam, count], i) => {
        csv += `${i+1},${escapeCSV(exam)},${count}\n`;
    });

    fs.writeFile('summary_report.csv', csv, 'utf8', err => {
        if (err) console.log('Export error: ' + err.message);
        else     console.log('  Exported to: summary_report.csv');
    });
}

// ── Main: prompt for file path, validate, then run ───────────────────
const rl = readline.createInterface({ input: process.stdin, output: process.stdout });

function askFilePath() {
    rl.question('Enter dataset file path: ', inputPath => {
        inputPath = inputPath.trim();

        if (!fs.existsSync(inputPath)) {
            console.log('Error: File not found. Please try again.\n');
            return askFilePath();
        }
        try { fs.accessSync(inputPath, fs.constants.R_OK); }
        catch { console.log('Error: File is not readable. Please try again.\n'); return askFilePath(); }
        if (path.extname(inputPath).toLowerCase() !== '.csv') {
            console.log('Error: Not a CSV file. Please try again.\n');
            return askFilePath();
        }

        console.log('File found. Processing...\n');
        try {
            processFile(inputPath);
        } catch (err) {
            console.log('Error processing file: ' + err.message);
        }
        rl.close();
    });
}

askFilePath();
