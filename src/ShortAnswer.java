public class ShortAnswer extends Essay {

    protected final int ansCharLimit = 50;

    ShortAnswer(String questionType, String questionPrompt, int numOfAns) {
        super(questionType, questionPrompt, numOfAns);
    }

    @Override
    public String display() {
        // The prompt that will be displayed (with type of question added before the actual prompt while displaying)
        String displayPrompt = "(" + questionType + ") " + questionPrompt;
        return displayPrompt + " --- " + numOfAns + " answers required, word limit " + this.ansCharLimit;
    }

}
