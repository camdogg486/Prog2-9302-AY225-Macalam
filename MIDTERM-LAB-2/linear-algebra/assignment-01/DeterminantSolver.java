/**
 * =====================================================
 * Student Name    : MACALAM, CAMRY S.
 * Course          : Math 101 — Linear Algebra
 * Assignment      : Programming Assignment 1 — 3x3 Matrix Determinant Solver
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : March 16, 2025
 * GitHub Repo     : https://github.com/macalam-camry/uphsd-cs-macalam-camry
 *
 * Description:
 *   This program computes the determinant of the 3x3 matrix assigned to
 *   Camry S. Macalam for Math 101. The determinant is found using cofactor
 *   expansion along the first row. Each 2x2 minor, its signed cofactor term,
 *   and the running total are all printed to the console so every step of
 *   the calculation is visible and easy to follow.
 * =====================================================
 */
public class DeterminantSolver {

    // ── SECTION 1: Assigned Matrix ───────────────────────────────────────
    // The 3x3 matrix given to this student on the assignment sheet.
    // Values are stored as a 2D integer array in row-major order
    // and are hardcoded — no user input is taken.
    static int[][] matrix = {
        { 3, 1, 5 },   // Row 1: [ 3  1  5 ]
        { 2, 4, 3 },   // Row 2: [ 2  4  3 ]
        { 6, 2, 1 }    // Row 3: [ 6  2  1 ]
    };

    // ── SECTION 2: 2×2 Minor Calculator ─────────────────────────────────
    // Calculates the determinant of a 2x2 sub-matrix using the
    // standard formula: (a * d) - (b * c), where the inputs are
    // the four corner values of the sub-matrix read left-to-right,
    // top-to-bottom.
    static int computeMinor(int a, int b, int c, int d) {
        return (a * d) - (b * c);
    }

    // ── SECTION 3: Matrix Display Helper ────────────────────────────────
    // Formats and prints the 3x3 matrix inside bracket borders so the
    // problem is presented clearly at the top of the output.
    static void printMatrix(int[][] m) {
        System.out.println("┌               ┐");
        for (int[] row : m) {
            System.out.printf("│  %2d  %2d  %2d   │%n", row[0], row[1], row[2]);
        }
        System.out.println("└               ┘");
    }

    // ── SECTION 4: Step-by-Step Cofactor Expansion ──────────────────────
    // Core solver method. Walks through cofactor expansion along row 1:
    //   1. Displays the original matrix.
    //   2. Computes and labels each of the three 2x2 minors (M₁₁, M₁₂, M₁₃),
    //      printing the sub-matrix elements and the arithmetic involved.
    //   3. Multiplies each minor by its row-1 element and its sign (+/-/+)
    //      to produce each cofactor term.
    //   4. Sums the three terms and reports the final determinant.
    //   5. Flags the result as singular if the determinant is zero.
    static void solveDeterminant(int[][] m) {

        // Print the section header and the assigned matrix
        System.out.println("=".repeat(52));
        System.out.println("  3x3 MATRIX DETERMINANT SOLVER");
        System.out.println("  Student: MACALAM, CAMRY S.");
        System.out.println("  Assigned Matrix:");
        System.out.println("=".repeat(52));
        printMatrix(m);
        System.out.println("=".repeat(52));
        System.out.println();
        System.out.println("Expanding along Row 1 (cofactor expansion):");
        System.out.println();

        // ── Step 1: Minor M₁₁ ──────────────────────────────────────────
        // Delete row 0 and column 0; use m[1][1], m[1][2], m[2][1], m[2][2]
        int minor11 = computeMinor(m[1][1], m[1][2], m[2][1], m[2][2]);
        System.out.printf(
            "  Step 1 — Minor M₁₁: det([%d,%d],[%d,%d]) = (%d×%d) - (%d×%d) = %d - %d = %d%n",
            m[1][1], m[1][2], m[2][1], m[2][2],
            m[1][1], m[2][2], m[1][2], m[2][1],
            m[1][1] * m[2][2], m[1][2] * m[2][1], minor11);

        // ── Step 2: Minor M₁₂ ──────────────────────────────────────────
        // Delete row 0 and column 1; use m[1][0], m[1][2], m[2][0], m[2][2]
        int minor12 = computeMinor(m[1][0], m[1][2], m[2][0], m[2][2]);
        System.out.printf(
            "  Step 2 — Minor M₁₂: det([%d,%d],[%d,%d]) = (%d×%d) - (%d×%d) = %d - %d = %d%n",
            m[1][0], m[1][2], m[2][0], m[2][2],
            m[1][0], m[2][2], m[1][2], m[2][0],
            m[1][0] * m[2][2], m[1][2] * m[2][0], minor12);

        // ── Step 3: Minor M₁₃ ──────────────────────────────────────────
        // Delete row 0 and column 2; use m[1][0], m[1][1], m[2][0], m[2][1]
        int minor13 = computeMinor(m[1][0], m[1][1], m[2][0], m[2][1]);
        System.out.printf(
            "  Step 3 — Minor M₁₃: det([%d,%d],[%d,%d]) = (%d×%d) - (%d×%d) = %d - %d = %d%n",
            m[1][0], m[1][1], m[2][0], m[2][1],
            m[1][0], m[2][1], m[1][1], m[2][0],
            m[1][0] * m[2][1], m[1][1] * m[2][0], minor13);

        // ── Signed Cofactor Terms ───────────────────────────────────────
        // The sign pattern for row 1 is: positive, negative, positive.
        // Multiply each element of row 0 by its sign and its minor.
        int c11 =  m[0][0] * minor11;
        int c12 = -m[0][1] * minor12;
        int c13 =  m[0][2] * minor13;

        System.out.println();
        System.out.printf("  Cofactor C₁₁ = (+1) × %d × %d = %d%n",  m[0][0], minor11, c11);
        System.out.printf("  Cofactor C₁₂ = (-1) × %d × %d = %d%n",  m[0][1], minor12, c12);
        System.out.printf("  Cofactor C₁₃ = (+1) × %d × %d = %d%n",  m[0][2], minor13, c13);

        // ── Final Determinant ───────────────────────────────────────────
        // Add all three cofactor terms together to get the determinant value.
        int det = c11 + c12 + c13;
        System.out.printf("%n  det(M) = %d + (%d) + %d%n", c11, c12, c13);
        System.out.println("=".repeat(52));
        System.out.printf("  ✓  DETERMINANT = %d%n", det);

        // ── Singular Matrix Check ───────────────────────────────────────
        // A determinant of zero means the matrix has no inverse.
        if (det == 0) {
            System.out.println("  ⚠ The matrix is SINGULAR — it has no inverse.");
        }
        System.out.println("=".repeat(52));
    }

    // ── SECTION 5: Program Entry Point ──────────────────────────────────
    // Kicks off the solver using the student's assigned matrix declared
    // at the top of this class.
    public static void main(String[] args) {
        solveDeterminant(matrix);
    }
}
