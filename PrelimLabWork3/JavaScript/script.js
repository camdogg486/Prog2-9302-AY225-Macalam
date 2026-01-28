function computeGrades() {

    let attendance = parseFloat(document.getElementById("attendance").value);
    let lab1 = parseFloat(document.getElementById("lab1").value);
    let lab2 = parseFloat(document.getElementById("lab2").value);
    let lab3 = parseFloat(document.getElementById("lab3").value);

    // If any field is empty, simply clear output and stop
    if (isNaN(attendance) || isNaN(lab1) || isNaN(lab2) || isNaN(lab3)) {
        document.getElementById("output").innerHTML = "";
        return;
    }

    let labAverage = (lab1 + lab2 + lab3) / 3;
    let classStanding = (attendance * 0.40) + (labAverage * 0.60);

    let requiredPass = (75 - (classStanding * 0.70)) / 0.30;
    let requiredExcellent = (100 - (classStanding * 0.70)) / 0.30;

    let output = `
        <strong>Results</strong><br><br>
        Lab Work Average: ${labAverage.toFixed(2)}<br>
        Class Standing: ${classStanding.toFixed(2)}<br><br>

        <strong>Required Prelim Exam Scores</strong><br>
        Passing (75): ${
            requiredPass <= 0 ? "Already guaranteed" :
            requiredPass > 100 ? "Not achievable" :
            requiredPass.toFixed(2)
        }<br>
        Excellent (100): ${
            requiredExcellent <= 0 ? "Already guaranteed" :
            requiredExcellent > 100 ? "Not achievable" :
            requiredExcellent.toFixed(2)
        }
    `;

    document.getElementById("output").innerHTML = output;
}

function clearForm() {
    document.getElementById("attendance").value = "";
    document.getElementById("lab1").value = "";
    document.getElementById("lab2").value = "";
    document.getElementById("lab3").value = "";
    document.getElementById("output").innerHTML = "";

    // Move cursor back to first input (good UX)
    document.getElementById("attendance").focus();
}

