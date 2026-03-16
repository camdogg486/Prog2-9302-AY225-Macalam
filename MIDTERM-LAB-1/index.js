/*
 * Macalam, Camry S.
 * PROGRAMMING 2 – MACHINE PROBLEM
 * University of Perpetual Help System DALTA – Molino Campus
 * BS Information Technology - Game Development
 * Dataset: https://www.kaggle.com/datasets/asaniczka/video-game-sales-2024
 */

const fs = require('fs');
const readline = require('readline');
const path = require('path');

// ── DataRecord structure ───────────────────────────────────────────────────────
function createDataRecord(title, console_, genre, publisher, developer,
                           totalSales, naSales, jpSales, palSales, otherSales, releaseDate) {
    return { title, console: console_, genre, publisher, developer,
             totalSales, naSales, jpSales, palSales, otherSales, releaseDate };
}

// ── Utility: parse a single CSV line (handles quoted fields) ──────────────────
function parseCSVLine(line) {
    const result = [];
    let inQuotes = false;
    let current = '';
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

// ── Utility: safe parse float ─────────────────────────────────────────────────
function toFloat(val) {
    const n = parseFloat(val);
    return isNaN(n) ? 0.0 : n;
}

// ── Utility: escape value for CSV output ──────────────────────────────────────
function escapeCSV(val) {
    const str = String(val);
    if (str.includes(',') || str.includes('"') || str.includes('\n')) {
        return '"' + str.replace(/"/g, '""') + '"';
    }
    return str;
}

// ── Utility: pad string right ─────────────────────────────────────────────────
function padRight(str, len) {
    return str.length >= len ? str.substring(0, len) : str + ' '.repeat(len - str.length);
}

function truncate(str, max) {
    return str.length > max ? str.substring(0, max - 3) + '...' : str;
}

function separator() {
    console.log('='.repeat(60));
}

// ── Load and parse the CSV dataset ────────────────────────────────────────────
function loadDataset(filePath) {
    const records = [];
    try {
        const content = fs.readFileSync(filePath, 'utf8');
        const lines = content.split(/\r?\n/);

        if (lines.length < 2) {
            console.log('Error: CSV file is empty or has no data rows.');
            return null;
        }

        // skip header (line 0)
        for (let i = 1; i < lines.length; i++) {
            const line = lines[i].trim();
            if (!line) continue;

            try {
                const cols = parseCSVLine(line);
                // Expected columns (0-based):
                // 0:img, 1:title, 2:console, 3:genre, 4:publisher,
                // 5:developer, 6:total_sales, 7:na_sales, 8:jp_sales,
                // 9:pal_sales, 10:other_sales, 11:release_date, 12:last_update
                if (cols.length < 11) continue;

                const title       = cols[1].trim();
                const console_    = cols[2].trim();
                const genre       = cols[3].trim();
                const publisher   = cols[4].trim();
                const developer   = cols[5].trim();
                const totalSales  = toFloat(cols[6]);
                const naSales     = toFloat(cols[7]);
                const jpSales     = toFloat(cols[8]);
                const palSales    = toFloat(cols[9]);
                const otherSales  = toFloat(cols[10]);
                const releaseDate = cols.length > 11 ? cols[11].trim() : '';

                if (!title) continue;

                records.push(createDataRecord(title, console_, genre, publisher,
                    developer, totalSales, naSales, jpSales, palSales, otherSales, releaseDate));

            } catch (err) {
                // skip malformed rows silently
            }
        }
    } catch (err) {
        console.log('Error reading file: ' + err.message);
        return null;
    }
    return records;
}

// ── Perform analytics and display results ─────────────────────────────────────
function performAnalytics(records) {
    separator();
    console.log('           VIDEO GAME SALES ANALYTICS REPORT');
    separator();

    // Overall totals
    const grandTotal = records.reduce((s, r) => s + r.totalSales, 0);
    const naTotal    = records.reduce((s, r) => s + r.naSales, 0);
    const jpTotal    = records.reduce((s, r) => s + r.jpSales, 0);
    const palTotal   = records.reduce((s, r) => s + r.palSales, 0);
    const otherTotal = records.reduce((s, r) => s + r.otherSales, 0);

    console.log('\n[1] OVERALL SALES SUMMARY');
    console.log('-'.repeat(50));
    console.log('  Total Games in Dataset  : ' + records.length.toLocaleString());
    console.log('  Grand Total Sales       : ' + grandTotal.toFixed(2) + ' million');
    console.log('  North America (NA)      : ' + naTotal.toFixed(2) + ' million');
    console.log('  Japan (JP)              : ' + jpTotal.toFixed(2) + ' million');
    console.log('  PAL Region              : ' + palTotal.toFixed(2) + ' million');
    console.log('  Other Regions           : ' + otherTotal.toFixed(2) + ' million');

    // Top 10 games by total sales
    console.log('\n[2] TOP 10 GAMES BY TOTAL SALES');
    console.log('-'.repeat(50));
    [...records]
        .sort((a, b) => b.totalSales - a.totalSales)
        .slice(0, 10)
        .forEach(r => {
            console.log('  ' + padRight(truncate(r.title, 40), 40) +
                        ' | ' + padRight(r.console, 6) +
                        ' | ' + r.totalSales.toFixed(2) + ' M');
        });

    // Sales by genre
    console.log('\n[3] TOTAL SALES BY GENRE');
    console.log('-'.repeat(50));
    const genreMap = {};
    records.forEach(r => {
        if (!r.genre) return;
        genreMap[r.genre] = (genreMap[r.genre] || 0) + r.totalSales;
    });
    Object.entries(genreMap)
        .sort((a, b) => b[1] - a[1])
        .forEach(([genre, sales]) => {
            console.log('  ' + padRight(genre, 25) + ' : ' + sales.toFixed(2) + ' million');
        });

    // Top 10 consoles by total sales
    console.log('\n[4] TOP 10 CONSOLES BY TOTAL SALES');
    console.log('-'.repeat(50));
    const consoleMap = {};
    records.forEach(r => {
        if (!r.console) return;
        consoleMap[r.console] = (consoleMap[r.console] || 0) + r.totalSales;
    });
    Object.entries(consoleMap)
        .sort((a, b) => b[1] - a[1])
        .slice(0, 10)
        .forEach(([cons, sales]) => {
            console.log('  ' + padRight(cons, 15) + ' : ' + sales.toFixed(2) + ' million');
        });

    // Top 10 publishers by total sales
    console.log('\n[5] TOP 10 PUBLISHERS BY TOTAL SALES');
    console.log('-'.repeat(50));
    const publisherMap = {};
    records.forEach(r => {
        if (!r.publisher) return;
        publisherMap[r.publisher] = (publisherMap[r.publisher] || 0) + r.totalSales;
    });
    Object.entries(publisherMap)
        .sort((a, b) => b[1] - a[1])
        .slice(0, 10)
        .forEach(([pub, sales]) => {
            console.log('  ' + padRight(pub, 35) + ' : ' + sales.toFixed(2) + ' million');
        });

    separator();

    // Export to CSV
    exportSummaryReport(records, grandTotal, naTotal, jpTotal, palTotal, otherTotal, genreMap);
}

// ── Export summary_report.csv ──────────────────────────────────────────────────
function exportSummaryReport(records, grandTotal, naTotal, jpTotal, palTotal, otherTotal, genreMap) {
    let csv = '';

    // Overall summary
    csv += 'SECTION,LABEL,VALUE\n';
    csv += `Overall Summary,Total Games,${records.length}\n`;
    csv += `Overall Summary,Grand Total Sales (M),${grandTotal.toFixed(2)}\n`;
    csv += `Overall Summary,NA Sales (M),${naTotal.toFixed(2)}\n`;
    csv += `Overall Summary,JP Sales (M),${jpTotal.toFixed(2)}\n`;
    csv += `Overall Summary,PAL Sales (M),${palTotal.toFixed(2)}\n`;
    csv += `Overall Summary,Other Sales (M),${otherTotal.toFixed(2)}\n`;
    csv += '\n';

    // Top 10 games
    csv += 'RANK,TITLE,CONSOLE,GENRE,PUBLISHER,TOTAL_SALES_M\n';
    [...records]
        .sort((a, b) => b.totalSales - a.totalSales)
        .slice(0, 10)
        .forEach((r, i) => {
            csv += `${i + 1},${escapeCSV(r.title)},${escapeCSV(r.console)},` +
                   `${escapeCSV(r.genre)},${escapeCSV(r.publisher)},${r.totalSales.toFixed(2)}\n`;
        });
    csv += '\n';

    // Sales by genre
    csv += 'GENRE,TOTAL_SALES_M\n';
    Object.entries(genreMap)
        .sort((a, b) => b[1] - a[1])
        .forEach(([genre, sales]) => {
            csv += `${escapeCSV(genre)},${sales.toFixed(2)}\n`;
        });

    fs.writeFile('summary_report.csv', csv, 'utf8', (err) => {
        if (err) {
            console.log('\nError exporting report: ' + err.message);
        } else {
            console.log('\nSummary report exported to: summary_report.csv');
        }
    });
}

// ── Main: ask for file path, validate, then run ────────────────────────────────
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

function askFilePath() {
    rl.question('Enter dataset file path: ', function(inputPath) {
        inputPath = inputPath.trim();

        if (!fs.existsSync(inputPath)) {
            console.log('Error: File does not exist. Please try again.\n');
            askFilePath();
            return;
        }

        try {
            fs.accessSync(inputPath, fs.constants.R_OK);
        } catch {
            console.log('Error: File is not readable. Please try again.\n');
            askFilePath();
            return;
        }

        if (path.extname(inputPath).toLowerCase() !== '.csv') {
            console.log('Error: File is not a CSV file. Please try again.\n');
            askFilePath();
            return;
        }

        console.log('File found. Loading dataset...\n');

        const records = loadDataset(inputPath);
        if (!records || records.length === 0) {
            console.log('No valid records found in the dataset.');
            rl.close();
            return;
        }

        console.log('Dataset loaded. Total records: ' + records.length + '\n');
        performAnalytics(records);
        rl.close();
    });
}

askFilePath();
