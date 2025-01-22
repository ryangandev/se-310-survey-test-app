import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Question implements Serializable {

    private static final long serialVersionUID = 1L;
    protected String questionType;
    protected String questionPrompt;
    protected List<String> questionResponse;
    protected List<String> correctAns;
    public static int promptCharLimit = 100;

    Question(String questionType, String questionPrompt){
        this.questionType = questionType;
        this.questionPrompt = questionPrompt;
        this.questionResponse = new ArrayList<>();
        this.correctAns = new ArrayList<>();
    }

    // Getter
    public String getQuestionType() {
        return this.questionType;
    }

    /*
        Returns the current question with its type as well as an answer requirement if there is one
     */
    public abstract String display();

    /*
        For Test Only, displays the correct answers of the current question
     */
    public String displayCorrectAns() {
        StringBuilder correctAnsStr = new StringBuilder();
        for (int i = 0; i < correctAns.size(); i++) {
            correctAnsStr.append("    ").append(i + 1).append("). ").append(correctAns.get(i)).append("\n");
        }
        return correctAnsStr.toString();
    }

    /*
        Display question prompt for tabulation
     */
    public abstract String displayPromptForTabulation();

    /*
        Modifies the current question including prompt, and choices contents, based on the type of questions
        Returns a boolean, true if changes made, false otherwise
     */
    public abstract boolean modifyQuestion();

    /*
        For Test Only, modifies the correct answers of the current question
        Correct answers are different depending on the type of question
        Returns a boolean, true if changes made, false otherwise
     */
    public abstract boolean modifyCorrectAns();

    /*/
        Prompt the user for a response for the current question, different based on the type of questions
     */
    public abstract List<String> takeResponse(String typeOfResponse);

    /*
        Displays the response of the current question object
     */
    public String displayResponse() {
        StringBuilder responseStr = new StringBuilder();
        for (int i = 0; i < questionResponse.size(); i++) {
            responseStr.append("    ").append(i + 1).append("). ").append(questionResponse.get(i));
        }

        return responseStr.toString();
    }

}
