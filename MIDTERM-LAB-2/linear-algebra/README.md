# Programming Assignment 1 — 3×3 Matrix Determinant Solver

| Field | Details |
|---|---|
| **Student** | MACALAM, CAMRY S. |
| **Course** | Math 101 – Linear Algebra |
| **School** | University of Perpetual Help System DALTA, Molino Campus |
| **Assignment** | Programming Assignment 1 |
| **Repository** | `uphsd-cs-macalam-camry` |

---

## Assigned Matrix

```
┌            ┐
│  3   1   5 │
│  2   4   3 │
│  6   2   1 │
└            ┘
```

---

## How to Run

### Java

```bash
# Compile
javac DeterminantSolver.java

# Run
java DeterminantSolver
```

### JavaScript (Node.js)

```bash
node determinant_solver.js
```

---

## Sample Output

```
====================================================
  3x3 MATRIX DETERMINANT SOLVER
  Student: MACALAM, CAMRY S.
  Assigned Matrix:
====================================================
┌                ┐
│   3    1    5  │
│   2    4    3  │
│   6    2    1  │
└                ┘
====================================================

Expanding along Row 1 (cofactor expansion):

  Step 1 — Minor M₁₁: det([4,3],[2,1]) = (4×1) - (3×2) = 4 - 6 = -2
  Step 2 — Minor M₁₂: det([2,3],[6,1]) = (2×1) - (3×6) = 2 - 18 = -16
  Step 3 — Minor M₁₃: det([2,4],[6,2]) = (2×2) - (4×6) = 4 - 24 = -20

  Cofactor C₁₁ = (+1) × 3 × -2 = -6
  Cofactor C₁₂ = (-1) × 1 × -16 = 16
  Cofactor C₁₃ = (+1) × 5 × -20 = -100

  det(M) = -6 + (16) + -100
====================================================
  ✓  DETERMINANT = -90
====================================================
```

> Both the Java and JavaScript programs produce the same determinant value.

---

## Final Determinant

**det(M) = −90**

The matrix is **non-singular** — it has an inverse.

---

## Repository Structure

```
uphsd-cs-macalam-camry/
├── linear-algebra/
│   └── assignment-01/
│       ├── DeterminantSolver.java      ← Java solution
│       ├── determinant_solver.js       ← JavaScript solution
│       └── README.md                   ← This file
└── README.md
```
