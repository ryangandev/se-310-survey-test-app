import utils.*;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Survey implements Serializable {
    private static final long serialVersionUID = 1L;
    private String fileID = "Untitled Survey";
    protected String responseBaseID; //Will be the same for each response according to the current survey loaded
    public static String basePath = FSConfig.serialDir + "Survey" + File.separator;
    protected final String[] questionTypes = {"T/F", "Multiple Choice", "Short Answer", "Essay", "Valid Date", "Matching"};
    protected final List<Question> questionList;
    protected final ResponseCorrectAnswer responseList;

    Survey(){
        questionList = new ArrayList<>();
        responseList = new ResponseCorrectAnswer();
    }

    // Getters
    public List<Question> getQuestionList() {
        return questionList;
    }

    public String getFileID() {
        return fileID;
    }

    /*
        Displays all questions in the current survey
     */
    public void display(){
        addLineSeparator();
        for (int i = 1; i <= questionList.size(); i++) {
            System.out.println("Q" + i + "). " + questionList.get(i-1).display());
        }
        addLineSeparator();
        System.out.print("\n");
    }

    /*
        Loads a survey from the dir if exists
        Returns a deserialized serialized Survey Object from the dir that user chooses
     */
    public static Survey load(String fileBasePath) {
        String selectedSurveyPath = FileUtils.listAndPickFileFromDir(fileBasePath);
        return SerializationHelper.deserialize(Survey.class, selectedSurveyPath);
    }

    /*
        Saves the current survey by:
            1. check if current survey has a name, if not, generate a unique name for it
            2. set the responseBaseID to the generated survey name for take survey functionality
            3. serialize the current Survey object
     */
    public void save() {
        if (fileID.equals("Untitled Survey")) {
            System.out.println("The current survey does not have an ID. Enter an integer ID for the name of your survey.");
            String surveyBaseID = "Survey_";
            fileID = generateFileName(basePath, surveyBaseID);
            responseBaseID = fileID + "_Response_";
        }
        SerializationHelper.serialize(this.getClass(), this, basePath, fileID);
        System.out.println(this.fileID + " is successfully saved!\n");
    }

    /*
        Helper method to generate a file name
        @param1 directory path that files are located
        @param2 baseID of the type of files
     */
    protected String generateFileName(String dirPath, String baseID) {
        return baseID + generateUniqueID(dirPath, baseID);
    }

    /*
        Helper method for generateFileName()
        Same params, calls a validation method from Validation utils
     */
    protected int generateUniqueID(String dirPath, String baseID) {
        do {
            int uniqueID = Input.promptForAnInt();
            if (!Validation.isUniqueIdExisted(dirPath, uniqueID, baseID)) {
                return uniqueID;
            }
            System.out.println("This id is used. Please enter another one.");
        } while (true);
    }

    /*
        Prompt the user to take a survey after they chose survey to load
        Take functionality contains following procedures:
            1. prompt for answers for each question on the list
            2. after answers are filled, prompt for an integer ID as unique name for the response; then save the response with that name
            3. display the responses along with each questions at the end for user's review
            4. lastly, clear the answer list, so no duplicate answers when survey is taken again
     */
    public void take() {
        addLineSeparator();
        System.out.println("Currently taking: " + fileID + "\n");
        for (int i =0; i < questionList.size(); i++) {
            System.out.println("Q" + (i+1) + "). " + questionList.get(i).display());
            responseList.addAnswer(questionList.get(i).takeResponse("response"));
        }

        System.out.println("Congratulation! You completed the survey!\n");
        addLineSeparator();
        System.out.println("Please enter an integer ID for the name of your survey response.");
        responseList.saveResponse("surveyResponse", generateFileName(ResponseCorrectAnswer.surveyResponseBasePath, responseBaseID));

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

        // Last step, after all responses are saved, empty the ansList for next survey taking
        responseList.getAnsList().clear();
    }

    /*
        Prompt the user to modify a SINGLE question each time they select MODIFY
        Simply calls the modify method from the specific question class
     */
    public boolean modify(int questionToModify) {
        System.out.println("You selected question " + questionToModify + "\n");
        return questionList.get(questionToModify-1).modifyQuestion();
    }

    /*
        Tabulate each question in the question list
     */
    public String tabulate() {
        String starSeparator = "**************************************************";
        StringBuilder tabulation = new StringBuilder();

        // A list of response sets for this survey
        List<File> responseSetFileList = FileUtils.getAllResponseForGivenSurveyOrTestFromDir(responseBaseID, ResponseCorrectAnswer.surveyResponseBasePath);

        // A list of response sets for this survey
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

    public void addQuestion(int questionChoice) {
        if (questionChoice == 1) {
            addTrueOrFalse();
        } else if (questionChoice == 2) {
            addMultipleChoice();
        } else if (questionChoice == 3) {
            addShortAnswer();
        } else if (questionChoice == 4) {
            addEssay();
        } else if (questionChoice == 5) {
            addValidDate();
        } else if (questionChoice == 6) {
            addMatching();
        }
    }

    protected void addTrueOrFalse() {
        System.out.println("T/F is selected from Menu 2\n");
        System.out.println("Enter the prompt for your True/False question:");
        String questionPrompt = Input.promptForInputWithLimit(Question.promptCharLimit);

        List<String> choices = new ArrayList<>();
        choices.add("True");
        choices.add("False");

        Question question = new TrueOrFalse(questionTypes[0], questionPrompt, choices, 1);
        questionList.add(question);
        System.out.println("A T/F question is successfully created!\n");
    }

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
        System.out.println("A multiple-choice question is successfully created!\n");
    }

    protected void addShortAnswer() {
        System.out.println("Short Answer is selected from Menu 2\n");
        System.out.println("Enter the prompt for your short answer question:");
        String questionPrompt = Input.promptForInputWithLimit(Question.promptCharLimit);

        System.out.println("Enter the number of answers you wish to have for this short answer question:");
        int numOfAns = Input.readIntInRange(1,5); // Prompt for the number of answers user wishes for this question

        Question question = new ShortAnswer(questionTypes[2], questionPrompt, numOfAns);
        questionList.add(question);
        System.out.println("A short-answer question is successfully created!\n");
    }

    protected void addEssay() {
        System.out.println("Essay is selected from Menu 2\n");
        System.out.println("Enter the prompt for your essay question:");
        String questionPrompt = Input.promptForInputWithLimit(Question.promptCharLimit);

        System.out.println("Enter the number of answers you wish to have for this essay question:");
        int numOfAns = Input.readIntInRange(1,3); // Prompt for the number of answers user wishes for this question

        Question question = new Essay(questionTypes[3], questionPrompt, numOfAns);
        questionList.add(question);
        System.out.println("An essay question is successfully created!\n");
    }

    protected void addValidDate() {
        System.out.println("Valid Date is selected from Menu 2\n");
        System.out.println("Enter the prompt for your valid date question:");
        String questionPrompt = Input.promptForInputWithLimit(Question.promptCharLimit);

        System.out.println("Enter the number of answers you wish to have for this valid date question:");
        int numOfAns = Input.readIntInRange(1,5); // Prompt for the number of answers user wishes for this question

        Question question = new ValidDate(questionTypes[4], questionPrompt, numOfAns);
        questionList.add(question);
        System.out.println("A valid-date question is successfully created!\n");
    }

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
        System.out.println("A matching question is successfully created!\n");
    }

    protected void addLineSeparator() {
        System.out.println("----------------------------------------------------------------------");
    }

    protected String tabulateTrueOrFalseAndMultipleChoice(List<ResponseCorrectAnswer> responseSetList, int questionIndex) {
        // 1. Build the part for displaying question -- "Question"
        StringBuilder tfOrMcTabulation = new StringBuilder("Question " + (questionIndex + 1) + ":\n" + questionList.get(questionIndex).displayPromptForTabulation());

        // 2. Build the part for displaying each response of the question from each response set -- "Responses"
        tfOrMcTabulation.append("Responses:\n");

        List<String> multipleChoiceList = ((MultipleChoice)questionList.get(questionIndex)).choices;
        int multipleChoiceSize = ((MultipleChoice)questionList.get(questionIndex)).choices.size();

        // an array of 0s for each answer count, the array size equals to choice list size
        int[] ansCounts = new int[multipleChoiceSize];

        // a list to store every response of the question from each response set
        List<List<String>> AllResponseOfAQuestion = new ArrayList<>();
        for (ResponseCorrectAnswer responseSet: responseSetList) {
            AllResponseOfAQuestion.add(responseSet.getAnsList().get(questionIndex));
        }

        // loop the list created above and add each response into the tabulationStr
        for (List<String> singleResponse: AllResponseOfAQuestion) {
            tfOrMcTabulation.append(String.join(" & ", singleResponse)).append("\n");
        }

        // 3. Build the part for displaying counts to each response -- "Tabulation"
        tfOrMcTabulation.append("\nTabulation:\n").append(questionList.get(questionIndex).questionPrompt).append("\n");

        // loop through each response from the list created before and add 1 to each answer count for each occurrence
        for (int i = 0; i < multipleChoiceSize; i++) {
            for (List<String> strings : AllResponseOfAQuestion) {
                for (String string : strings) {
                    if (multipleChoiceList.get(i).equals(string)) {
                        ansCounts[i] += 1;
                    }
                }
            }
        }

        // append ansCount to each choice
        for (int i = 0; i < multipleChoiceSize; i++) {
            tfOrMcTabulation.append(multipleChoiceList.get(i)).append(" -- ").append(ansCounts[i]).append("\n");
        }

        return tfOrMcTabulation.toString();
    }

    protected String tabulateShortAnswerAndDate(List<ResponseCorrectAnswer> responseSetList, int questionIndex) {
        // 1. Build the part for displaying question -- "Question"
        StringBuilder saAndDateTabulation = new StringBuilder("Question " + (questionIndex + 1) + ":\n" + questionList.get(questionIndex).displayPromptForTabulation() + "\n");

        // 2. Build the part for displaying each response of the question from each response set -- "Responses"
        saAndDateTabulation.append("Responses:\n");

        // a list to store every response of the question from each response set
        List<List<String>> AllResponseOfAQuestion = new ArrayList<>();
        for (ResponseCorrectAnswer responseSet: responseSetList) {
            AllResponseOfAQuestion.add(responseSet.getAnsList().get(questionIndex));
        }

        // loop the list created above and add each response into the tabulationStr
        for (List<String> singleResponse: AllResponseOfAQuestion) {
            saAndDateTabulation.append(String.join(" & ", singleResponse)).append("\n");
        }

        // 3. Build the part for displaying counts to each response -- "Tabulation"
        saAndDateTabulation.append("\nTabulation:\n").append(questionList.get(questionIndex).questionPrompt).append("\n");

        // Stores unique ans and their counts
        List<String> uniqueAnswers = new ArrayList<>();
        int uniqueAnsCount = 0;

        for (List<String> response : AllResponseOfAQuestion) {
            for (String singleAns: response) {
                if (!uniqueAnswers.contains(singleAns)) {
                    uniqueAnswers.add(singleAns);
                    uniqueAnsCount += 1;
                }
            }
        }

        // an array of 0s for each unique answer count
        int[] ansCounts = new int[uniqueAnsCount];

        // update the array with each ans count
        for (List<String> response : AllResponseOfAQuestion) {
            for (String singleAns: response) {
                if (uniqueAnswers.contains(singleAns)) {
                    int ansIndexInUniqueAns = uniqueAnswers.indexOf(singleAns);
                    ansCounts[ansIndexInUniqueAns] += 1;
                }
            }
        }

        for (int i = 0; i < uniqueAnsCount; i++) {
            saAndDateTabulation.append(uniqueAnswers.get(i)).append(" -- ").append(ansCounts[i]).append("\n");
        }

        return saAndDateTabulation.toString();
    }

    protected String tabulateEssay(List<ResponseCorrectAnswer> responseSetList, int questionIndex) {
        // 1. Build the part for displaying question -- "Question"
        StringBuilder essayTabulation = new StringBuilder("Question " + (questionIndex + 1) + ":\n" + questionList.get(questionIndex).displayPromptForTabulation() + "\n");

        // 2. Build the part for displaying each response of the question from each response set -- "Responses"
        essayTabulation.append("Responses:\n");

        // a list to store every response of the question from each response set
        List<List<String>> AllResponseOfAQuestion = new ArrayList<>();
        for (ResponseCorrectAnswer responseSet: responseSetList) {
            AllResponseOfAQuestion.add(responseSet.getAnsList().get(questionIndex));
        }

        // loop the list created above and add each response into the tabulationStr
        for (List<String> singleResponse: AllResponseOfAQuestion) {
            essayTabulation.append(String.join(" & ", singleResponse)).append("\n");
        }

        // 3. Build the part for displaying each response -- "Tabulation"
        essayTabulation.append("\nTabulation:\n").append(questionList.get(questionIndex).questionPrompt).append("\n");

        // Stores unique ans
        List<String> uniqueAnswers = new ArrayList<>();
        for (List<String> response : AllResponseOfAQuestion) {
            for (String singleAns: response) {
                if (!uniqueAnswers.contains(singleAns)) {
                    uniqueAnswers.add(singleAns);
                }
            }
        }

        for (String uniqueAnswer : uniqueAnswers) {
            essayTabulation.append(uniqueAnswer).append("\n");
        }

        return essayTabulation.toString();
    }

    protected String tabulateMatching(List<ResponseCorrectAnswer> responseSetList, int questionIndex) {
        // 1. Build the part for displaying question -- "Question"
        StringBuilder matchingTabulation = new StringBuilder("Question " + (questionIndex + 1) + ":\n" + questionList.get(questionIndex).displayPromptForTabulation() + "\n");

        // 2. Build the part for displaying each response of the question from each response set -- "Responses"
        matchingTabulation.append("Responses:\n");

        // a list to store every response of the question from each response set
        List<List<String>> AllResponseOfAQuestion = new ArrayList<>();
        for (ResponseCorrectAnswer responseSet: responseSetList) {
            AllResponseOfAQuestion.add(responseSet.getAnsList().get(questionIndex));
        }

        // loop the list created above and add each response permutation into the tabulationStr
        // store each unique permutation
        List<String> uniquePermutation = new ArrayList<>();
        for (List<String> singleResponse: AllResponseOfAQuestion) {
            String matchingPermutation = String.join("\n", singleResponse);
            matchingTabulation.append(matchingPermutation).append("\n\n");
            if (!uniquePermutation.contains(matchingPermutation)) {
                uniquePermutation.add(matchingPermutation);
            }
        }

        // 3. Build the part for displaying each response -- "Tabulation"
        matchingTabulation.append("Tabulation:\n").append(questionList.get(questionIndex).questionPrompt).
                append(((Matching)questionList.get(questionIndex)).displayChoiceStr()).append("\n\n");

        // an array of 0s for each answer count, the array size equals to choice list size
        int[] permutationCounts = new int[uniquePermutation.size()];
        // test array to get permutation counts
        List<String> permutationForChecking = new ArrayList<>();
        // update the permutation count
        for (List<String> strings : AllResponseOfAQuestion) {
            String matchingPermutation = String.join("\n", strings);
            if (!permutationForChecking.contains(matchingPermutation)) {
                permutationForChecking.add(matchingPermutation);
            }

            // permutations are at the same index for uniquePermutation and permutationForTest, so use the correct index to give increment to count
            permutationCounts[permutationForChecking.indexOf(matchingPermutation)] += 1;
        }

        for (int i = 0; i < uniquePermutation.size(); i++) {
            matchingTabulation.append("Count: ").append(permutationCounts[i]).append("\n").append(uniquePermutation.get(i)).append("\n\n");
        }

        return matchingTabulation.toString();
    }

}
