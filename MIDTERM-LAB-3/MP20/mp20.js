/**
 * =====================================================
 * Student Name    : MACALAM, CAMRY S.
 * Course          : Programming 2
 * Assignment      : Machine Problem 20 — Convert CSV Dataset to JSON
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : March 16, 2026
 * GitHub Repo     : https://github.com/macalam-camry/uphsd-cs-macalam-camry
 * Runtime         : Node.js — run with: node mp20.js
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

const fs       = require('fs');
const readline = require('readline');
const path     = require('path');

// Human-readable property names mapped to each meaningful CSV column index.
// The always-empty spacer at index 2 is deliberately excluded.
const FIELD_MAP = [
    { name: 'candidate', col: 0 },
    { name: 'type',      col: 1 },
    { name: 'exam',      col: 3 },
    { name: 'language',  col: 4 },
    { name: 'examDate',  col: 5 },
    { name: 'score',     col: 6 },
    { name: 'result',    col: 7 },
    { name: 'timeUsed',  col: 8 }
];

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

// ── Load the CSV and convert every data row into a plain JS object ───
function processFile(filePath) {
    const content = fs.readFileSync(filePath, 'utf8');
    const lines   = content.split(/\r?\n/);

    // Build the array of record objects
    const records = [];

    for (let i = 0; i < lines.length; i++) {
        if (i <= METADATA_LINES) continue;          // skip metadata + header

        const cols = parseCSVLine(lines[i]);
        if (cols.every(c => c.trim() === '')) continue; // skip blank rows

        // Map each meaningful column to its friendly property name
        const record = {};
        for (const { name, col } of FIELD_MAP) {
            record[name] = col < cols.length ? cols[col].trim() : '';
        }
        records.push(record);
    }

    if (records.length === 0) {
        console.log('No records found.');
        return;
    }

    // ── Serialise to JSON using built-in JSON.stringify ───────────────
    // The third argument (2) adds two-space indentation for readability.
    const jsonOutput = JSON.stringify(records, null, 2);

    // ── Write the JSON file ───────────────────────────────────────────
    const outputFile = 'dataset.json';
    fs.writeFileSync(outputFile, jsonOutput, 'utf8');

    // ── Print confirmation and a 5-record preview ─────────────────────
    const SEP  = '='.repeat(70);
    const DASH = '-'.repeat(68);

    console.log(SEP);
    console.log('  MP20 — CSV TO JSON CONVERSION');
    console.log('  Student: MACALAM, CAMRY S.');
    console.log(SEP);
    console.log(`  Records converted : ${records.length}`);
    console.log(`  Output file       : ${outputFile}`);
    console.log(SEP);
    console.log('\n  PREVIEW — First 5 JSON Objects:');
    console.log('  ' + DASH);

    // Show just the first 5 records in pretty-printed form
    const preview = records.slice(0, 5);
    console.log('  [');
    preview.forEach((rec, i) => {
        console.log('    {');
        const keys = Object.keys(rec);
        keys.forEach((key, ki) => {
            const comma = ki < keys.length - 1 ? ',' : '';
            console.log(`      "${key}": "${rec[key]}"${comma}`);
        });
        const objComma = i < preview.length - 1 ? ',' : '';
        console.log('    }' + objComma);
    });
    if (records.length > 5) {
        console.log(`    ... and ${records.length - 5} more records`);
    }
    console.log('  ]');
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
