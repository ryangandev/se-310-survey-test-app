import utils.Input;

import java.util.List;

public class Matching extends Question {

    private final List<String> leftChoice;
    private final List<String> rightChoice;
    public static int choiceCharLimit = 20;

    Matching(String questionType, String questionPrompt, List<String> leftChoice, List<String> rightChoice) {
        super(questionType, questionPrompt);
        this.leftChoice = leftChoice;
        this.rightChoice = rightChoice;
    }

    @Override
    public String display() {
        // The prompt that will be displayed (with type of question added before the actual prompt while displaying)
        String displayPrompt = "(" + questionType + ") " + questionPrompt;
        StringBuilder choiceStr = new StringBuilder();
        for (int i = 0; i < leftChoice.size(); i++) {
            choiceStr.append("\n    ").append(i+1).append("). ").append(leftChoice.get(i)).append(" ".repeat(choiceCharLimit - leftChoice.get(i).length()))
                    .append("   ").append(i+1).append("). ").append(rightChoice.get(i));
        }
        return displayPrompt + choiceStr;
    }

    public String displayRightColumnChoices() {
        StringBuilder rightChoicesStr = new StringBuilder();
        for (int i = 0; i < rightChoice.size(); i++) {
            rightChoicesStr.append("  ").append(i + 1).append("). ").append(rightChoice.get(i)).append("\n");
        }
        return rightChoicesStr.toString();
    }

    @Override
    public String displayPromptForTabulation() {
        // The prompt that will be displayed (with type of question added before the actual prompt while displaying)
        String displayPrompt = questionPrompt;
        StringBuilder choiceStr = new StringBuilder();
        for (int i = 0; i < leftChoice.size(); i++) {
            choiceStr.append("\n    ").append(i+1).append("). ").append(leftChoice.get(i)).append(" ".repeat(choiceCharLimit - leftChoice.get(i).length()))
                    .append("   ").append(i+1).append("). ").append(rightChoice.get(i));
        }
        return displayPrompt + choiceStr + "\n";
    }

    public String displayChoiceStr() {
        StringBuilder choiceStr = new StringBuilder();
        for (int i = 0; i < leftChoice.size(); i++) {
            choiceStr.append("\n    ").append(i+1).append("). ").append(leftChoice.get(i)).append(" ".repeat(choiceCharLimit - leftChoice.get(i).length()))
                    .append("   ").append(i+1).append("). ").append(rightChoice.get(i));
        }
        return choiceStr.toString();
    }

    /**
     * For matching question, prompt the user if they want to modify the following:
     *      1. modify the question prompt
     *      2. modify choices from the left column
     *      3. modify choices from the right column
     * @return a boolean, true if changes made, false otherwise
     */
    @Override
    public boolean modifyQuestion() {

        // A checker to return, true if changes are made, false otherwise
        boolean changesMade = false;

        // First display the displayPrompt
        // Then prompt for whether to modify the question prompt
        System.out.println(this.display());
        System.out.println("Do you wish to modify the prompt? 1.Yes / 2.No");
        int userChoice = Input.readIntInRange(1, 2);

        // User chooses to modify the prompt
        // First display the actual prompt (without the question type showing)
        // Then prompt for the new question prompt and set it to the prompt of current question
        if (userChoice == 1) {
            System.out.println("The current prompt is: \n" + this.questionPrompt + "\n");
            System.out.println("Enter a new prompt:");
            this.questionPrompt = Input.promptForInputWithLimit(promptCharLimit);
            System.out.println("The prompt was successfully modified!\n");
            changesMade = true;
        }

        // Regardless if user modified the prompt, prompt whether to modify choices from the left column
        System.out.println("Do you wish to modify a choice from the left column? 1.Yes / 2.No");
        userChoice = Input.readIntInRange(1, 2);

        // User chooses to modify choices from the left column
        // First display the choices from the left column
        // Then prompt for which choice to modify
        // Lastly based on user choice, prompt for a new value for that choice and set the choice to that value
        // Looping last 3 steps until user don't want to modify left choices anymore
        if (userChoice == 1) {

            // A local boolean to stop the while loop
            boolean repeat = true;
            do {
                System.out.println("Which choice do you want to modify?");
                for (int i = 0; i < leftChoice.size(); i++) {
                    System.out.println("  " + (i + 1) + "). " + leftChoice.get(i));
                }

                userChoice = Input.readIntInRange(1, leftChoice.size());
                System.out.println("Enter the new value for that choice: ");

                // -1 to get the correct index of the choice in the list
                leftChoice.set(userChoice - 1, Input.promptForInputWithLimit(choiceCharLimit));
                System.out.println("The choice was successfully modified!\n");

                System.out.println("Do you want to modify another choice from the left column? 1.Yes / 2.No");
                if (Input.readIntInRange(1, 2) == 2) {
                    repeat = false;
                }

            } while (repeat);

            changesMade = true;
        }

        // Regardless if user modified the prompt or choices from the left column, prompt whether to modify choices from the right column
        System.out.println("Do you wish to modify a choice from the right column? 1.Yes / 2.No");
        userChoice = Input.readIntInRange(1, 2);

        // User chooses to modify choices from the right column
        // First display the choices from the right column
        // Then prompt for which choice to modify
        // Lastly based on user choice, prompt for a new value for that choice and set the choice to that value
        // Looping last 3 steps until user don't want to modify right choices anymore
        if (userChoice == 1) {

            // A local boolean to stop the while loop
            boolean repeat = true;
            do {
                System.out.println("Which choice do you want to modify?");
                for (int i = 0; i < rightChoice.size(); i++) {
                    System.out.println("  " + (i + 1) + "). " + rightChoice.get(i));
                }

                userChoice = Input.readIntInRange(1, rightChoice.size());
                System.out.println("Enter the new value for that choice: ");

                // -1 to get the correct index of the choice in the list
                rightChoice.set(userChoice - 1, Input.promptForInputWithLimit(choiceCharLimit));
                System.out.println("The choice was successfully modified!\n");

                System.out.println("Do you want to modify another choice from the right column? 1.Yes / 2.No");
                if (Input.readIntInRange(1, 2) == 2) {
                    repeat = false;
                }

            } while (repeat);

            changesMade = true;
        }

        return changesMade;
    }

    @Override
    public boolean modifyCorrectAns() {

        // A checker to return, true if changes are made, false otherwise
        boolean changesMade = false;

        System.out.println("Do you wish to modify the correct answer(s)? 1.Yes / 2.No");
        int userChoice = Input.readIntInRange(1,2);

        // User chooses to modify the correct answers
        // First display all correct answers for users to choose
        // Then prompt for which answer to modify
        // Prompt for a new value
        // Looping last 3 steps until user don't want to modify correct answers anymore
        if (userChoice == 1) {

            // A local boolean to stop the while loop
            boolean repeat = true;
            do {
                System.out.println("Which answer do you want to modify?");
                System.out.println(displayCorrectAns());
                userChoice = Input.readIntInRange(1,correctAns.size());

                int ansCorrespondingChoice = Integer.parseInt(String.valueOf(correctAns.get(userChoice-1).charAt(0)));
                System.out.println("Enter a new match for choice " + (ansCorrespondingChoice) + " on the left column: ");
                System.out.println("Available right column choices:");
                System.out.println(displayRightColumnChoices());
                String newAns = ansCorrespondingChoice + "->" + Input.readIntInRange(1, rightChoice.size());

                // Check if the new choice was already an answer, if not, proceed
                if (!correctAns.contains(newAns)) {
                    // -1 to get the correct index of the answer in the list
                    correctAns.set(userChoice-1, newAns);
                    System.out.println("The answer was successfully modified!\n");
                } else {
                    System.out.println("The choice was already in the correct answers.");
                }

                System.out.println("Do you want to modify another correct answer? 1.Yes / 2.No");
                if (Input.readIntInRange(1,2) == 2) {
                    repeat = false;
                }
            } while (repeat);

            changesMade = true;
        }

        return changesMade;
    }

    @Override
    public List<String> takeResponse(String typeOfResponse) {
        System.out.println("For each of the choice in the left column, match with a choice from the right column.");

        if (typeOfResponse.equals("response")) {
            for (int i = 0; i < leftChoice.size(); i++) {
                System.out.println("Enter a match for: " + (i+1) + "). " + leftChoice.get(i));
                String ans = (i+1) + "->" + Input.readIntInRange(1, rightChoice.size());
                questionResponse.add(ans);
            }
            return questionResponse;
        } else {
            for (int i = 0; i < leftChoice.size(); i++) {
                System.out.println("Enter a match for: " + (i+1) + "). " + leftChoice.get(i));
                System.out.println("Available right column choices:");
                System.out.println(displayRightColumnChoices());
                String ans = (i+1) + "->" + Input.readIntInRange(1, rightChoice.size());
                correctAns.add(ans);
            }
            return correctAns;
        }

    }
}
