/**
 * =====================================================
 * Student Name    : MACALAM, CAMRY S.
 * Course          : Math 101 — Linear Algebra
 * Assignment      : Programming Assignment 1 — 3x3 Matrix Determinant Solver
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : March 16, 2025
 * GitHub Repo     : https://github.com/macalam-camry/uphsd-cs-macalam-camry
 * Runtime         : Node.js (run with: node determinant_solver.js)
 *
 * Description:
 *   JavaScript counterpart to DeterminantSolver.java. This script computes
 *   the determinant of the same 3x3 matrix assigned to Camry S. Macalam
 *   using cofactor expansion along the first row. Every intermediate value —
 *   each 2x2 minor, each signed cofactor, and the final determinant — is
 *   logged to the console step by step using console.log().
 * =====================================================
 */

// ── SECTION 1: Assigned Matrix ───────────────────────────────────────
// The 3x3 matrix assigned to this student, stored as a 2D array.
// Outer array holds the rows; each inner array holds the three column values.
// These values are hardcoded — the program takes no user input.
const matrix = [
    [3, 1, 5],   // Row 1: [ 3  1  5 ]
    [2, 4, 3],   // Row 2: [ 2  4  3 ]
    [6, 2, 1]    // Row 3: [ 6  2  1 ]
];

// ── SECTION 2: Matrix Display Helper ────────────────────────────────
// Prints the 3x3 matrix to the console inside bracket borders.
// Each row is padded so columns line up neatly.
function printMatrix(m) {
    console.log(`┌               ┐`);
    m.forEach(row => {
        const formatted = row.map(v => String(v).padStart(3)).join("  ");
        console.log(`│ ${formatted}  │`);
    });
    console.log(`└               ┘`);
}

// ── SECTION 3: 2×2 Minor Calculator ─────────────────────────────────
// Returns the determinant of a 2x2 matrix whose elements are passed
// as four individual numbers: (a b / c d).
// The formula used is: (a × d) − (b × c).
function computeMinor(a, b, c, d) {
    return (a * d) - (b * c);
}

// ── SECTION 4: Step-by-Step Cofactor Expansion ──────────────────────
// Main solving function. Accepts the 3x3 matrix and works through
// cofactor expansion along the first row:
//   1. Prints the matrix with a header.
//   2. Computes M₁₁, M₁₂, and M₁₃, showing all arithmetic per step.
//   3. Applies alternating signs (+, -, +) and row-1 scale factors
//      to produce each cofactor term.
//   4. Sums the three cofactor terms to get the determinant.
//   5. Warns if the matrix is singular (determinant = 0).
function solveDeterminant(m) {
    const line = "=".repeat(52);

    // Print the header and the assigned matrix
    console.log(line);
    console.log("  3x3 MATRIX DETERMINANT SOLVER");
    console.log("  Student: MACALAM, CAMRY S.");
    console.log("  Assigned Matrix:");
    console.log(line);
    printMatrix(m);
    console.log(line);
    console.log();
    console.log("Expanding along Row 1 (cofactor expansion):");
    console.log();

    // ── Step 1: Minor M₁₁ ──────────────────────────────────────────
    // Remove row 0 and column 0; the remaining four elements are
    // m[1][1], m[1][2] on top and m[2][1], m[2][2] on the bottom.
    const minor11 = computeMinor(m[1][1], m[1][2], m[2][1], m[2][2]);
    console.log(
        `  Step 1 — Minor M₁₁: det([${m[1][1]},${m[1][2]}],[${m[2][1]},${m[2][2]}])` +
        ` = (${m[1][1]}×${m[2][2]}) - (${m[1][2]}×${m[2][1]})` +
        ` = ${m[1][1] * m[2][2]} - ${m[1][2] * m[2][1]} = ${minor11}`
    );

    // ── Step 2: Minor M₁₂ ──────────────────────────────────────────
    // Remove row 0 and column 1; remaining elements are
    // m[1][0], m[1][2] on top and m[2][0], m[2][2] on the bottom.
    const minor12 = computeMinor(m[1][0], m[1][2], m[2][0], m[2][2]);
    console.log(
        `  Step 2 — Minor M₁₂: det([${m[1][0]},${m[1][2]}],[${m[2][0]},${m[2][2]}])` +
        ` = (${m[1][0]}×${m[2][2]}) - (${m[1][2]}×${m[2][0]})` +
        ` = ${m[1][0] * m[2][2]} - ${m[1][2] * m[2][0]} = ${minor12}`
    );

    // ── Step 3: Minor M₁₃ ──────────────────────────────────────────
    // Remove row 0 and column 2; remaining elements are
    // m[1][0], m[1][1] on top and m[2][0], m[2][1] on the bottom.
    const minor13 = computeMinor(m[1][0], m[1][1], m[2][0], m[2][1]);
    console.log(
        `  Step 3 — Minor M₁₃: det([${m[1][0]},${m[1][1]}],[${m[2][0]},${m[2][1]}])` +
        ` = (${m[1][0]}×${m[2][1]}) - (${m[1][1]}×${m[2][0]})` +
        ` = ${m[1][0] * m[2][1]} - ${m[1][1] * m[2][0]} = ${minor13}`
    );

    // ── Signed Cofactor Terms ───────────────────────────────────────
    // Row 1 sign pattern is +, -, +. Multiply each row-0 element
    // by its sign and the corresponding minor to get the cofactor term.
    const c11 =  m[0][0] * minor11;
    const c12 = -m[0][1] * minor12;
    const c13 =  m[0][2] * minor13;

    console.log();
    console.log(`  Cofactor C₁₁ = (+1) × ${m[0][0]} × ${minor11} = ${c11}`);
    console.log(`  Cofactor C₁₂ = (-1) × ${m[0][1]} × ${minor12} = ${c12}`);
    console.log(`  Cofactor C₁₃ = (+1) × ${m[0][2]} × ${minor13} = ${c13}`);

    // ── Final Determinant ───────────────────────────────────────────
    // Sum all three cofactor terms to produce the determinant.
    const det = c11 + c12 + c13;
    console.log();
    console.log(`  det(M) = ${c11} + (${c12}) + ${c13}`);
    console.log(line);
    console.log(`  ✓  DETERMINANT = ${det}`);

    // ── Singular Matrix Check ───────────────────────────────────────
    // A zero determinant means this matrix cannot be inverted.
    if (det === 0) {
        console.log("  ⚠ The matrix is SINGULAR — it has no inverse.");
    }
    console.log(line);
}

// ── SECTION 5: Program Entry Point ──────────────────────────────────
// Run the solver with the student's assigned matrix.
solveDeterminant(matrix);
