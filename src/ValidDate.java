import utils.Input;

import java.util.List;

public class ValidDate extends ShortAnswer{

    private static final long serialVersionUID = -3717348474319842383L;
    protected final int ansCharLimit = 10;
    protected int numOfAnsLimit = 5;

    ValidDate(String questionType, String questionPrompt, int numOfAns) {
        super(questionType, questionPrompt, numOfAns);
    }

    @Override
    public String display() {
        // The prompt that will be displayed (with type of question added before the actual prompt while displaying)
        String displayPrompt = "(" + questionType + ") " + questionPrompt;
        return displayPrompt + " --- " + numOfAns + " answers required, answer format: YYYY-MM-DD";
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

                correctAns.set(userChoice-1, newValue);
                System.out.println("The answer was successfully modified!\n");

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
                String ans = Input.promptForValidDate();
                questionResponse.add(ans);
                ansCount += 1;
            } while (ansCount != numOfAns);
            return questionResponse;
        } else {
            do {
                System.out.println("Answer #" + (ansCount+1));
                String ans = Input.promptForValidDate();
                correctAns.add(ans);
                ansCount += 1;
            } while (ansCount != numOfAns);
            return correctAns;
        }
    }
}
