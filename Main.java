import java.util.*;

public class Main { // Entry point & overall game controller
    static final Scanner SC = new Scanner(System.in);
    static int totalScore = 0;
    static int totalTime = 0; // seconds
    static String userName;
    static List<ScoreEntry> scoreboard = new ArrayList<>();
    static final Random RAND = new Random();

    public static void main(String[] args) { // Main loop: players & replay menu
        System.out.println("Welcome to the Math Quiz!\n" +
                "You will answer 5 randomly generated two-step multiplication/division questions with integer answers.\n" +
                "You have 5 seconds for each question.");

        boolean continuePlaying = true;

        while (continuePlaying) {
            System.out.print("Enter your name: ");
            userName = SC.nextLine().trim();

            boolean playAgain = true;
            while (playAgain) {
                resetGame();
                playGame();       // generate 5 Qs, time responses, update score
                displayResults(); // show score, avg time, update scoreboard

                boolean validInput = false;
                while (!validInput) {
                    System.out.println("\nOptions: \n(y) Play again with the same player\n(c) Change player\n(q) Quit");
                    System.out.print("Choose an option: ");
                    String choice = SC.nextLine().trim().toLowerCase();

                    switch (choice) {
                        case "y":
                            playAgain = true;
                            validInput = true;
                            break;
                        case "c":
                            playAgain = false; // exits inner loop; asks name again
                            validInput = true;
                            break;
                        case "q":
                            continuePlaying = false;
                            playAgain = false;
                            validInput = true;
                            break;
                        default:
                            System.out.println("Invalid choice, please choose again.");
                    }
                }
            }
        }

        System.out.println("Thank you for playing!");
    }

    public static void resetGame() { // zero score/time for a new run
        totalScore = 0;
        totalTime = 0;
    }

    public static void playGame() { // ask 5 questions; track correctness & time
        int questionCount = 0;
        while (questionCount < 5) {
            Question q = generateTwoStepIntegerQuestion();
            System.out.println("Question " + (questionCount + 1) + ": " + q.questionText);
            long start = System.currentTimeMillis();

            Integer userAnswer = readIntOrNull(); // non-blocking validation
            long end = System.currentTimeMillis();
            int timeTaken = (int) ((end - start) / 1000);

            // Cap time at 5 if exceeded; treat >5s as "Time's up!"
            if (timeTaken > 5) {
                System.out.println("Time's up!");
                totalTime += 5;
            } else if (userAnswer != null && userAnswer == q.correctAnswer) {
                System.out.println("Correct!");
                totalScore++;
                totalTime += timeTaken;
            } else {
                System.out.println("Incorrect!");
                totalTime += timeTaken;
            }
            questionCount++;
        }
    }

    // Safely read an integer; if not an int, consume the line and return null
    private static Integer readIntOrNull() {
        String line = SC.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Question generateTwoStepIntegerQuestion() { // ensure integer-only answers
        int A, B, C;
        String question;
        int correctAnswer;

        while (true) {
            A = RAND.nextInt(9) + 2; // 2..10
            B = RAND.nextInt(9) + 2;
            C = RAND.nextInt(9) + 2;

            if (RAND.nextBoolean()) { // (A / B) * C
                if (A % B == 0) {
                    question = A + " / " + B + " * " + C;
                    correctAnswer = (A / B) * C;
                    break;
                }
            } else { // (A * B) / C
                int prod = A * B;
                if (prod % C == 0) {
                    question = A + " * " + B + " / " + C;
                    correctAnswer = prod / C;
                    break;
                }
            }
        }
        return new Question(question, correctAnswer);
    }

    public static void displayResults() { // show results & update scoreboard
        double averageTime = totalTime / 5.0;
        System.out.println("\nQuiz Over!");
        System.out.println("Final Score: " + totalScore + "/5");
        System.out.printf("Average Time: %.1f seconds per question%n", averageTime);
        updateScoreboard();
    }

    public static void updateScoreboard() { // sort by score desc, time asc
        double avg = totalTime / 5.0;
        scoreboard.add(new ScoreEntry(userName, totalScore, avg));
        scoreboard.sort((a, b) -> {
            if (b.score != a.score) return b.score - a.score;
            return Double.compare(a.averageTime, b.averageTime);
        });

        System.out.println("\nTop Scores:");
        for (ScoreEntry entry : scoreboard) {
            System.out.println(entry);
        }
    }
}

class Question { // single math question
    String questionText;
    int correctAnswer;
    public Question(String questionText, int correctAnswer) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
    }
}

class ScoreEntry { // scoreboard row
    String userName;
    int score;
    double averageTime;
    public ScoreEntry(String userName, int score, double averageTime) {
        this.userName = userName;
        this.score = score;
        this.averageTime = averageTime;
    }
    @Override
    public String toString() {
        return userName + " - Score: " + score + ", Avg Time: " + String.format("%.1f", averageTime) + " seconds";
    }
}
