import utils.Input;

import java.util.List;

public class Essay extends Question {

    protected int numOfAns;
    protected final int ansCharLimit = 300;
    protected int numOfAnsLimit = 3;

    Essay(String questionType, String questionPrompt, int numOfAns) {
        super(questionType, questionPrompt);
        this.numOfAns = numOfAns;
    }

    @Override
    public String display() {
        // The prompt that will be displayed (with type of question added before the actual prompt while displaying)
        String displayPrompt = "(" + questionType + ") " + questionPrompt;
        return displayPrompt + " --- " + numOfAns + " answers required, word limit " + this.ansCharLimit;
    }

    @Override
    public String displayPromptForTabulation() {
        return questionPrompt + "\n";
    }

    /**
     * For essay question, prompt the user if they want to modify the following:
     *      1. modify the question prompt
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
        int userChoice = Input.readIntInRange(1,2);

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

                System.out.println("Enter a new value for that answer:");
                String newValue = Input.promptForInputWithLimit(ansCharLimit);

                // Check if the new choice was already an answer, if not, proceed
                if (!correctAns.contains(newValue)) {
                    // -1 to get the correct index of the answer in the list
                    correctAns.set(userChoice-1, newValue);
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
        System.out.println("Enter your answer(s) below:");
        int ansCount = 0;

        if (typeOfResponse.equals("response")) {
            do {
                System.out.println("Answer #" + (ansCount+1));
                String ans = Input.promptForInputWithLimit(ansCharLimit);
                if (questionResponse.size() == 0) {
                    questionResponse.add(ans);
                    ansCount += 1;
                } else if (questionResponse.contains(ans)) {
                    System.out.println("You already chose this answer before. Please choose another one.");
                } else {
                    questionResponse.add(ans);
                    ansCount += 1;
                }
            } while (ansCount != numOfAns);
            return questionResponse;
        } else {
            do {
                System.out.println("Answer #" + (ansCount+1));
                String ans = Input.promptForInputWithLimit(ansCharLimit);
                if (correctAns.size() == 0) {
                    correctAns.add(ans);
                    ansCount += 1;
                } else if (correctAns.contains(ans)) {
                    System.out.println("You already chose this answer before. Please choose another one.");
                } else {
                    correctAns.add(ans);
                    ansCount += 1;
                }
            } while (ansCount != numOfAns);
            return correctAns;
        }

    }

}
