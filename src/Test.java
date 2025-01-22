import utils.FSConfig;
import utils.FileUtils;
import utils.Input;
import utils.SerializationHelper;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Test extends Survey{
    private static final long serialVersionUID = 1L;
    private String fileID = "Untitled Test";
    public static String basePath = FSConfig.serialDir + "Test" + File.separator;
    private final ResponseCorrectAnswer correctAnsList;
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    Test() {
        correctAnsList = new ResponseCorrectAnswer();
    }

    // Getters
    @Override
    public String getFileID() {
        return fileID;
    }

    /*
        Display all questions in the current test with their correct answers
     */
    public void displayWithAns() {
        addLineSeparator();
        for (int i = 0; i < questionList.size(); i++) {
            System.out.println("Q" + (i+1) + "). " + questionList.get(i).display());
            System.out.println("Correct Answer for Q" + (i+1) + ": ");
            for (int j = 0; j < correctAnsList.getAnsList().get(i).size(); j++) {
                System.out.println("    " + (j+1) + "). " + correctAnsList.getAnsList().get(i).get(j));
            }
        }
        addLineSeparator();
        System.out.print("\n");
    }

    /*
        Loads a Test from the dir if exists
        Returns a deserialized serialized Test Object from the dir that user chooses
     */
    public static Test load(String fileBasePath) {
        String selectedTestPath = FileUtils.listAndPickFileFromDir(fileBasePath);
        return SerializationHelper.deserialize(Test.class, selectedTestPath);
    }

    /*
        Saves the current test by:
            1. check if current test has a name, if not, generate a unique name for it
            2. set the responseBaseID to the generated test name for take test functionality
            3. serialize the current Test object
     */
    @Override
    public void save() {
        if (fileID.equals("Untitled Test")) {
            System.out.println("The current test does not have an ID. Enter an integer ID for the name of your test.");
            String testBaseID = "Test_";
            fileID = generateFileName(basePath, testBaseID);
            responseBaseID = fileID + "_Response_";
        }
        SerializationHelper.serialize(this.getClass(), this, basePath, fileID);
        System.out.println(this.fileID + " is successfully saved!\n");
    }

    /*
        Prompt the user to take a test after they chose test to load
        Take functionality contains following procedures:
            1. prompt for answers for each question on the list
            2. after answers are filled, prompt for an integer ID as unique name for the response; then save the response with that name
            3. display the responses along with each questions at the end for user to review
            4. lastly, clear the answer list, so no duplicate answers when test is taken again
     */
    @Override
    public void take() {
        addLineSeparator();
        System.out.println("Currently taking: " + fileID + "\n");
        for (int i =0; i < questionList.size(); i++) {
            System.out.println("Q" + (i+1) + "). " + questionList.get(i).display());
            responseList.addAnswer(questionList.get(i).takeResponse("response"));
        }

        System.out.println("Congratulation! You completed the test!\n");
        addLineSeparator();
        System.out.println("Please enter an integer ID for the name of your test response.");
        responseList.saveResponse("testResponse", generateFileName(ResponseCorrectAnswer.testResponseBasePath, responseBaseID));

        System.out.println("Below is your response for '" + fileID + "'.\n" + "The response file will be saved as '" + responseList.responseID + "'.");
        addLineSeparator();
        for (int i =0; i < responseList.getAnsListSize(); i++) {
            System.out.println("Q" + (i+1) + "). " + questionList.get(i).display());
            System.out.println("Answers for Q" + (i+1) + ":");
            for (int j = 0; j < responseList.getAnsList().get(i).size(); j++) {
                System.out.println("    " + (j+1) + "). " + responseList.getAnsList().get(i).get(j));
            }
        }
        addLineSeparator();
        System.out.print("\n");

        // Last step, after all responses are saved, empty the ansList for next test taking
        responseList.getAnsList().clear();
    }

    /*
        Tabulate each question in the question list
     */
    @Override
    public String tabulate() {
        String starSeparator = "**************************************************";
        StringBuilder tabulation = new StringBuilder();

        // A list of response sets for this test
        List<File> responseSetFileList = FileUtils.getAllResponseForGivenSurveyOrTestFromDir(responseBaseID, ResponseCorrectAnswer.testResponseBasePath);

        // A list of response sets for this test
        List<ResponseCorrectAnswer> responseSetList = new ArrayList<>();

        // Add the response sets into a list for looping later
        for (File file: responseSetFileList) {
            responseSetList.add(ResponseCorrectAnswer.loadResponse(file.getAbsolutePath()));
        }


        for (int i = 0; i < questionList.size(); i++) {
            if (questionList.get(i).getQuestionType().equals(questionTypes[0]) || questionList.get(i).getQuestionType().equals(questionTypes[1])) {
                tabulation.append(starSeparator).append("\n");
                tabulation.append(tabulateTrueOrFalseAndMultipleChoice(responseSetList, i));
            } else if (questionList.get(i).getQuestionType().equals(questionTypes[2]) || questionList.get(i).getQuestionType().equals(questionTypes[4])) {
                tabulation.append(starSeparator).append("\n");
                tabulation.append(tabulateShortAnswerAndDate(responseSetList, i));
            } else if (questionList.get(i).getQuestionType().equals(questionTypes[3])) {
                tabulation.append(starSeparator).append("\n");
                tabulation.append(tabulateEssay(responseSetList, i));
            } else if (questionList.get(i).getQuestionType().equals(questionTypes[5])) {
                tabulation.append(starSeparator).append("\n");
                String str = tabulateMatching(responseSetList, i);
                tabulation.append(str);
            }
        }

        return tabulation.toString();
    }

    /*
        Grade a response of the current test
     */
    public String grade(ResponseCorrectAnswer testResponseToGrade) {
        String gradeOutput = "";
        double totalGrade = 100;
        double gradableGrade;
        double actualGrade;
        double numOfQuestion = questionList.size();
        double gradeOfOneQuestion = Double.parseDouble(decimalFormat.format(100/numOfQuestion));


        int essayNum = 0;
        for (Question question: questionList) {
            if (question.getQuestionType().equals("Essay")) {
                essayNum += 1;
            }
        }
        gradableGrade = 100 - gradeOfOneQuestion * essayNum;
        actualGrade = gradableGrade;

        for (int i = 0; i < correctAnsList.getAnsListSize();i++) {
            List<String> currentAnsList = correctAnsList.getAnsList().get(i);

            // If the question is essay, skip it
            if (questionList.get(i).getQuestionType().equals("Essay")) {
                continue;
            }
            for (int j = 0; j < correctAnsList.getAnsList().get(i).size(); j++) {
                String currentResponseAns = testResponseToGrade.getAnsList().get(i).get(j);
                if (!currentAnsList.contains(currentResponseAns)) {
                    actualGrade -= gradeOfOneQuestion;
                    break;
                }
            }
        }

        if (essayNum == 0) {
            gradeOutput += "You received an " + actualGrade + " on the test. The test was worth " + totalGrade + " points.";
        } else {
            gradeOutput += "You received an " + actualGrade + " on the test. The test was worth " + totalGrade + " points, but only " +
                    gradableGrade + " of those points coule be auto graded because there was " + essayNum + " essay question.";
        }

        return gradeOutput;
    }

    /*
        Prompt the user to modify a SINGLE question each time they select MODIFY
        Simply calls the modify method from the specific question class
     */
    @Override
    public boolean modify(int questionToModify) {
        System.out.println("You selected question " + questionToModify + "\n");
        boolean questionModified = questionList.get(questionToModify-1).modifyQuestion();
        boolean ansModified = questionList.get(questionToModify-1).modifyCorrectAns();
        return questionModified || ansModified;
    }

    @Override
    protected void addTrueOrFalse() {
        System.out.println("T/F is selected from Menu 2\n");
        System.out.println("Enter the prompt for your True/False question:");
        String questionPrompt = Input.promptForInputWithLimit(Question.promptCharLimit);

        List<String> choices = new ArrayList<>();
        choices.add("True");
        choices.add("False");

        Question question = new TrueOrFalse(questionTypes[0], questionPrompt, choices, 1);
        questionList.add(question);
        System.out.println("Follow the prompt to enter correct answer(s) for this question.");
        correctAnsList.addAnswer(question.takeResponse("answer"));
        System.out.println("A T/F question is successfully created!\n");
    }

    @Override
    protected void addMultipleChoice() {
        System.out.println("Multiple Choice is selected from Menu 2\n");
        System.out.println("Enter the prompt for your multiple-choice question:");
        String questionPrompt = Input.promptForInputWithLimit(Question.promptCharLimit);
        System.out.println("Enter the number of choices for your multiple-choice question:");
        int numOfChoices = Input.readIntInRange(2,10); // Prompt for the number of choices for multiple-choice

        List<String> choices = new ArrayList<>();

        // Prompt user for choices and as part of the question prompt
        for (int i = 1; i <= numOfChoices; i++) {
            System.out.println("Enter choice #" + i);
            choices.add(Input.promptForInputWithLimit(MultipleChoice.choiceCharLimit));
        }

        System.out.println("Enter the number of answers you wish from this multiple-choice question:");
        int numOfAns = Input.readIntInRange(1, numOfChoices); // Prompt for the number of answers user wishes for this question

        Question question = new MultipleChoice(questionTypes[1], questionPrompt, choices, numOfAns);
        questionList.add(question);
        System.out.println("Follow the prompt to enter correct answer(s) for this question.");
        correctAnsList.addAnswer(question.takeResponse("answer"));
        System.out.println("A multiple-choice question is successfully created!\n");
    }

    @Override
    protected void addShortAnswer() {
        System.out.println("Short Answer is selected from Menu 2\n");
        System.out.println("Enter the prompt for your short answer question:");
        String questionPrompt = Input.promptForInputWithLimit(Question.promptCharLimit);

        System.out.println("Enter the number of answers you wish to have for this short answer question:");
        int numOfAns = Input.readIntInRange(1,5); // Prompt for the number of answers user wishes for this question

        Question question = new ShortAnswer(questionTypes[2], questionPrompt, numOfAns);
        questionList.add(question);
        System.out.println("Follow the prompt to enter correct answer(s) for this question.");
        correctAnsList.addAnswer(question.takeResponse("answer"));
        System.out.println("A short-answer question is successfully created!\n");
    }

    @Override
    protected void addEssay() {
        System.out.println("Essay is selected from Menu 2\n");
        System.out.println("Enter the prompt for your essay question:");
        String questionPrompt = Input.promptForInputWithLimit(Question.promptCharLimit);

        System.out.println("Enter the number of answers you wish to have for this essay question:");
        int numOfAns = Input.readIntInRange(1,3); // Prompt for the number of answers user wishes for this question

        Question question = new Essay(questionTypes[3], questionPrompt, numOfAns);
        questionList.add(question);
        System.out.println("Follow the prompt to enter correct answer(s) for this question.");
        correctAnsList.addAnswer(question.takeResponse("answer"));
        System.out.println("An essay question is successfully created!\n");
    }

    @Override
    protected void addValidDate() {
        System.out.println("Valid Date is selected from Menu 2\n");
        System.out.println("Enter the prompt for your valid date question:");
        String questionPrompt = Input.promptForInputWithLimit(Question.promptCharLimit);

        System.out.println("Enter the number of answers you wish to have for this valid date question:");
        int numOfAns = Input.readIntInRange(1,5); // Prompt for the number of answers user wishes for this question

        Question question = new ValidDate(questionTypes[4], questionPrompt, numOfAns);
        questionList.add(question);
        System.out.println("Follow the prompt to enter correct answer(s) for this question.");
        correctAnsList.addAnswer(question.takeResponse("answer"));
        System.out.println("A valid-date question is successfully created!\n");
    }

    @Override
    protected void addMatching() {
        System.out.println("Matching is selected from Menu 2\n");
        System.out.println("Enter the prompt for your matching question:");
        String questionPrompt = Input.promptForInputWithLimit(Question.promptCharLimit);

        System.out.println("Enter the number of pairs you wish to have for this matching question:");
        int numOfAns = Input.readIntInRange(2, 7); // Prompt for the number of matches user wishes for this question

        List<String> leftChoices = new ArrayList<>();
        List<String> rightChoices = new ArrayList<>();

        // Prompt user for left-side choices and as part of the question prompt
        for (int i = 1; i <= numOfAns; i++) {
            System.out.println("Enter left-side choice #" + i);
            leftChoices.add(Input.promptForInputWithLimit(Matching.choiceCharLimit));
        }

        // Prompt user for right-side choices and as part of the question prompt
        for (int i = 1; i <= numOfAns; i++) {
            System.out.println("Enter right-side choice #" + i);
            rightChoices.add(Input.promptForInputWithLimit(Matching.choiceCharLimit));
        }

        Question question = new Matching(questionTypes[5], questionPrompt, leftChoices, rightChoices);
        questionList.add(question);
        System.out.println("Follow the prompt to enter correct answer(s) for this question.");
        correctAnsList.addAnswer(question.takeResponse("answer"));
        System.out.println("A matching question is successfully created!\n");
    }
}
