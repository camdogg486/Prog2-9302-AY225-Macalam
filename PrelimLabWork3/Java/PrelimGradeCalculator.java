import java.util.Scanner;

public class PrelimGradeCalculator {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        // Input Section
        System.out.print("Enter Attendance Grade (0-100): ");
        double attendance = input.nextDouble();

        System.out.print("Enter Lab Work 1 Grade: ");
        double lab1 = input.nextDouble();

        System.out.print("Enter Lab Work 2 Grade: ");
        double lab2 = input.nextDouble();

        System.out.print("Enter Lab Work 3 Grade: ");
        double lab3 = input.nextDouble();

        // Computations
        double labAverage = (lab1 + lab2 + lab3) / 3;
        double classStanding = (attendance * 0.40) + (labAverage * 0.60);

        double requiredPass = (75 - (classStanding * 0.70)) / 0.30;
        double requiredExcellent = (100 - (classStanding * 0.70)) / 0.30;

        // Output Section
        System.out.println("\n--- PRELIM GRADE COMPUTATION ---");
        System.out.println("Attendance Score: " + attendance);
        System.out.println("Lab Work 1: " + lab1);
        System.out.println("Lab Work 2: " + lab2);
        System.out.println("Lab Work 3: " + lab3);
        System.out.println("Lab Work Average: " + labAverage);
        System.out.println("Class Standing: " + classStanding);

        // Remarks
        System.out.println("\n--- REQUIRED PRELIM EXAM SCORES ---");

        if (requiredPass <= 0) {
            System.out.println("Passing (75): Already guaranteed");
        } else if (requiredPass > 100) {
            System.out.println("Passing (75): Not achievable");
        } else {
            System.out.println("Passing (75): " + requiredPass);
        }

        if (requiredExcellent <= 0) {
            System.out.println("Excellent (100): Already guaranteed");
        } else if (requiredExcellent > 100) {
            System.out.println("Excellent (100): Not achievable");
        } else {
            System.out.println("Excellent (100): " + requiredExcellent);
        }

        input.close();
    }
}
