/**
 * =====================================================
 * Student Name    : MACALAM, CAMRY S.
 * Course          : Programming 2
 * Assignment      : Machine Problem 18 — Remove Rows with Empty Fields
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : March 16, 2026
 * GitHub Repo     : https://github.com/macalam-camry/uphsd-cs-macalam-camry
 * Runtime         : Node.js — run with: node mp18.js
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

const fs       = require('fs');
const readline = require('readline');
const path     = require('path');

// Indices of meaningful columns. Column1 (index 2) is a structural
// blank spacer in this dataset and is intentionally excluded.
// Trailing empty columns 9-11 are also excluded.
const MEANINGFUL_COLS = [0, 1, 3, 4, 5, 6, 7, 8];

// Number of metadata/title lines before the real header row
const METADATA_LINES = 6;

// ── Parse a single CSV line, handling quoted fields ──────────────────
function parseCSVLine(line) {
    const result = [];
    let inQuotes = false;
    let current  = '';
    for (let i = 0; i < line.length; i++) {
        const c = line[i];
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

// ── Returns true if any meaningful column in the row is empty ────────
function hasEmptyField(cols) {
    return MEANINGFUL_COLS.some(idx => idx >= cols.length || cols[idx].trim() === '');
}

// ── Safely get a column value; returns '[empty]' when missing ────────
function safe(cols, idx) {
    if (idx >= cols.length || cols[idx].trim() === '') return '[empty]';
    return cols[idx].trim();
}

// ── Pad / truncate a string to a fixed display width ─────────────────
function padRight(str, len) {
    if (str.length > len) return str.substring(0, len - 3) + '...';
    return str.padEnd(len);
}

// ── Load the CSV, skip metadata, parse every data row ────────────────
function processFile(filePath) {
    const content = fs.readFileSync(filePath, 'utf8');
    const lines   = content.split(/\r?\n/);

    const cleanRows   = [];   // records with no empty meaningful fields
    const removedRows = [];   // records that contained at least one empty field
    let   totalData   = 0;

    for (let i = 0; i < lines.length; i++) {
        // Skip the first 6 metadata rows
        if (i < METADATA_LINES) continue;

        // Row at index 6 is the column header — skip it for data processing
        if (i === METADATA_LINES) continue;

        const cols = parseCSVLine(lines[i]);

        // Skip completely blank rows (e.g., trailing empty line)
        if (cols.every(c => c.trim() === '')) continue;

        totalData++;

        if (hasEmptyField(cols)) {
            removedRows.push(cols);
        } else {
            cleanRows.push(cols);
        }
    }

    // ── Display results ───────────────────────────────────────────────
    const SEP = '='.repeat(90);
    const DASH = '-'.repeat(88);
    const COL_HDR = `  ${padRight('Candidate', 30)} | ${padRight('Type', 10)} | ${padRight('Exam', 30)} | ${padRight('Score', 6)} | Result`;

    console.log(SEP);
    console.log('  MP18 — REMOVE ROWS WITH EMPTY FIELDS');
    console.log('  Student: MACALAM, CAMRY S.');
    console.log(SEP);
    console.log(`  Total data rows scanned  : ${totalData}`);
    console.log(`  Rows removed (has empty) : ${removedRows.length}`);
    console.log(`  Clean rows retained      : ${cleanRows.length}`);
    console.log(SEP);

    // List removed rows
    if (removedRows.length > 0) {
        console.log('\n  REMOVED ROWS (contained at least one empty field):');
        console.log('  ' + DASH);
        console.log(COL_HDR);
        console.log('  ' + DASH);
        removedRows.forEach(row => {
            console.log(`  ${padRight(safe(row,0),30)} | ${padRight(safe(row,1),10)} | ${padRight(safe(row,3),30)} | ${padRight(safe(row,6),6)} | ${safe(row,7)}`);
        });
    } else {
        console.log('\n  No rows were removed — all records have complete fields.');
    }

    // Display the clean dataset
    console.log(`\n  CLEAN DATASET (${cleanRows.length} records):`);
    console.log('  ' + DASH);
    console.log(COL_HDR);
    console.log('  ' + DASH);
    cleanRows.forEach(row => {
        console.log(`  ${padRight(safe(row,0),30)} | ${padRight(safe(row,1),10)} | ${padRight(safe(row,3),30)} | ${padRight(safe(row,6),6)} | ${safe(row,7)}`);
    });
    console.log(SEP);
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
