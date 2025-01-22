import utils.Input;

import java.util.List;

public class TrueOrFalse extends MultipleChoice{

    TrueOrFalse(String questionType, String questionPrompt, List<String> choices, int numOfAns) {
        super(questionType, questionPrompt, choices, numOfAns);
    }

    @Override
    public String display() {
        // The prompt that will be displayed (with type of question added before the actual prompt while displaying)
        String displayPrompt = "(" + questionType + ") " + questionPrompt;
        StringBuilder questionPromptBuilder = new StringBuilder(displayPrompt);
        for (int i = 1; i <= choices.size(); i++) {
            questionPromptBuilder.append("\n   ").append(i).append("). ").append(choices.get(i-1));
        }
        return questionPromptBuilder.toString();
    }

    /**
     * For T/F question, only prompt the user if they want to modify the question prompt
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
}
