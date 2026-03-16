# Programming 2 — Machine Problem Set
## MP18 · MP19 · MP20

| Field | Details |
|---|---|
| **Student** | MACALAM, CAMRY S. |
| **Course** | Programming 2 |
| **School** | University of Perpetual Help System DALTA, Molino Campus |
| **Repository** | `uphsd-cs-macalam-camry` |

---

## Dataset

`Sample_Data-Prog-2-csv.csv` — Pearson VUE exam results from UPHSD Molino.  
**168 data records** across columns: Candidate, Type, Exam, Language, Exam Date, Score, Result, Time Used.

> Note: The CSV contains 6 metadata/title rows before the actual column header. All programs skip these automatically.

---

## How to Run

### Java
```bash
# Compile (from the folder containing the .java file)
javac MP18.java     # or MP19.java / MP20.java

# Run
java MP18           # or MP19 / MP20

# When prompted:
Enter dataset file path: C:\path\to\Sample_Data-Prog-2-csv.csv
```

### JavaScript (Node.js)
```bash
node mp18.js        # or mp19.js / mp20.js

# When prompted:
Enter dataset file path: /path/to/Sample_Data-Prog-2-csv.csv
```

---

## MP18 — Remove Rows with Empty Fields

**Logic:** Reads every data record and checks the 8 meaningful columns (Candidate, Type, Exam, Language, Exam Date, Score, Result, Time Used) for empty values. The always-empty structural spacer column (Column1) is intentionally excluded from the check. Records with any empty meaningful field are separated into a "removed" list; the rest form the clean dataset, which is displayed in a formatted table.

**Output files:** Console table only.

**Sample Output:**
```
==========================================================================================
  MP18 — REMOVE ROWS WITH EMPTY FIELDS
  Student: MACALAM, CAMRY S.
==========================================================================================
  Total data rows scanned  : 168
  Rows removed (has empty) : 0
  Clean rows retained      : 168
==========================================================================================

  No rows were removed — all records have complete fields.

  CLEAN DATASET (168 records):
  ----------------------------------------------------------------------------------------
  Candidate                      | Type       | Exam                           | Score  | Result
  ----------------------------------------------------------------------------------------
  Nanete,Ennor                   | Student    | Python                         | 860    | PASS
  Fredelia,Macquire              | Student    | Cybersecurity                  | 937    | PASS
  ...
```

---

## MP19 — Generate Dataset Summary Report

**Logic:** Iterates through all 168 records to accumulate four groups of statistics: (1) overall pass/fail counts and pass rate, (2) score min/max/average/median, (3) candidate type frequency (Student, Faculty, NTE), and (4) a ranked list of all exams sorted by number of takers. Results are printed to the console in labelled sections and also written to `summary_report.csv`.

**Output files:** `summary_report.csv`

**Sample Output:**
```
======================================================================
  MP19 — DATASET SUMMARY REPORT
  Student: MACALAM, CAMRY S.
  Source : Sample_Data-Prog-2-csv.csv
======================================================================

  [1] GENERAL OVERVIEW
  ----------------------------------------------------------------------
  Total Records                       : 168
  PASS                                : 130
  FAIL                                : 38
  Pass Rate                           : 77.4%

  [2] SCORE STATISTICS
  ----------------------------------------------------------------------
  Minimum Score                       : 183
  Maximum Score                       : 980
  Average Score                       : 769.98
  Median Score                        : 806.0

  [3] CANDIDATE TYPE BREAKDOWN
  ----------------------------------------------------------------------
  Student                             : 161 (95.8%)
  Faculty                             : 4 (2.4%)
  NTE                                 : 3 (1.8%)

  [4] TOP EXAMS BY NUMBER OF TAKERS
  ----------------------------------------------------------------------
  RNK  Exam Title                                     Count
  1    HTML and CSS                                   54
  2    Cybersecurity                                  18
  3    Information Technology Specialist in Netwo...  16
  ...
```

---

## MP20 — Convert CSV Dataset to JSON

**Logic:** Maps each data row's columns to a set of human-readable property names (`candidate`, `type`, `exam`, `language`, `examDate`, `score`, `result`, `timeUsed`), builds a JavaScript/Java object for each record, and serialises the entire collection into a JSON array. The output is written to `dataset.json`. A preview of the first 5 JSON objects is printed to the console for quick verification.

**Output files:** `dataset.json`

**Sample Output:**
```
======================================================================
  MP20 — CSV TO JSON CONVERSION
  Student: MACALAM, CAMRY S.
======================================================================
  Records converted : 168
  Output file       : dataset.json
======================================================================

  PREVIEW — First 5 JSON Objects:
  [
    {
      "candidate": "Nanete,Ennor",
      "type": "Student",
      "exam": "Python",
      "language": "English",
      "examDate": "03/14/2026",
      "score": "860",
      "result": "PASS",
      "timeUsed": "36 min 38 sec"
    },
    ...
  ]
```

---

## Repository Structure

```
Prog2-9302-AY225-Macalam/
├── MIDTERM-LAB-3/
│   ├── MP18/
│   │   ├── MP18.java
│   │   └── mp18.js
│   ├── MP19/
│   │   ├── MP19.java
│   │   └── mp19.js
│   └── MP20/
│       ├── MP20.java
│       └── mp20.js
├── README.md
└── Sample_Data-Prog-2-csv.csv
```
