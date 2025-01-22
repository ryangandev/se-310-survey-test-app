import utils.Input;

import java.util.List;

public class MultipleChoice extends Question {

    protected int numOfAns; // number of answers expected
    protected List<String> choices; // choices user entered
    public static int choiceCharLimit = 30; // char limit for each choice entered

    MultipleChoice(String questionType, String questionPrompt, List<String> choices, int numOfAns) {
        super(questionType, questionPrompt);
        this.numOfAns = numOfAns;
        this.choices = choices;
    }

    @Override
    public String display() {
        // The prompt that will be displayed (with type of question added before the actual prompt while displaying)
        String displayPrompt = "(" + questionType + ") " + questionPrompt;

        StringBuilder questionPromptBuilder = new StringBuilder(displayPrompt);
        questionPromptBuilder.append(" --- ").append(numOfAns).append(" answers required");

        for (int i = 1; i <= choices.size(); i++) {
            questionPromptBuilder.append("\n   ").append(i).append("). ").append(choices.get(i-1));
        }
        return questionPromptBuilder.toString();
    }

    public String displayAvailableChoices() {
        StringBuilder choicesStr = new StringBuilder();
        for (int i = 0; i < choices.size(); i++) {
            choicesStr.append("  ").append(i + 1).append("). ").append(choices.get(i)).append("\n");
        }
        return choicesStr.toString();
    }

    @Override
    public String displayPromptForTabulation() {
        return questionPrompt + "\n" + displayAvailableChoices() + "\n";
    }

    /**
     * For multiple-choice question, prompt the user if they want to modify the following:
     *      1. modify the question prompt
     *      2. modify choices
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

        // Regardless if user modified the prompt, prompt whether to modify the choices
        System.out.println("Do you wish to modify the choices? 1.Yes / 2.No");
        userChoice = Input.readIntInRange(1,2);

        // User chooses to modify the choices
        // First display all choices for users to choose
        // Then prompt for which choice to modify
        // Lastly based on user choice, prompt for a new value for that choice and set the choice to that value
        // Looping last 3 steps until user don't want to modify choices anymore
        if (userChoice == 1) {

            // A local boolean to stop the while loop
            boolean repeat = true;
            do {
                System.out.println("Which choice do you want to modify?");
                System.out.println(displayAvailableChoices());

                userChoice = Input.readIntInRange(1,choices.size());
                System.out.println("Enter the new value for that choice: ");

                // -1 to get the correct index of the choice in the list
                choices.set(userChoice-1, Input.promptForInputWithLimit(choiceCharLimit));
                System.out.println("The choice was successfully modified!\n");

                System.out.println("Do you want to modify another choice? 1.Yes / 2.No");
                if (Input.readIntInRange(1,2) == 2) {
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

                System.out.println("Below are the available choices. Enter the new choice for that answer: ");
                System.out.println(displayAvailableChoices());

                int newChoice = Input.readIntInRange(1, choices.size());

                // Check if the new choice was already an answer, if not, proceed
                if (!correctAns.contains(choices.get(newChoice-1))) {
                    // -1 to get the correct index of the answer in the list
                    correctAns.set(userChoice-1, choices.get(newChoice-1));
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
                String ans = choices.get(Input.readIntInRange(1,choices.size())-1);
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
                System.out.println("Available choices:");
                System.out.println(displayAvailableChoices());
                System.out.println("Answer #" + (ansCount+1));
                String ans = choices.get(Input.readIntInRange(1,choices.size())-1);
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
