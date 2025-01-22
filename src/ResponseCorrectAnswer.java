import utils.FSConfig;
import utils.FileUtils;
import utils.SerializationHelper;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResponseCorrectAnswer implements Serializable {

    private static final long serialVersionUID = 1L;
    public static String surveyResponseBasePath = FSConfig.serialDir + "SurveyResponse" + File.separator;
    public static String testResponseBasePath = FSConfig.serialDir + "TestResponse" + File.separator;
    public String responseID; // Will be initialized when a response is being saved
    private final List<List<String>> ansList;

    ResponseCorrectAnswer() {
        this.ansList = new ArrayList<>();
    }
    // Getters
    public int getAnsListSize() {
        return ansList.size();
    }

    public List<List<String>> getAnsList() {
        return ansList;
    }

    /*
        Adds a list of answers of a question to the response object
     */
    public void addAnswer(List<String> answer) {
        ansList.add(answer);
    }

    /*
        Load a response given path
     */
    public static ResponseCorrectAnswer loadResponse(String responsePath) {
        return SerializationHelper.deserialize(ResponseCorrectAnswer.class, responsePath);
    }

    /*
        Loads a response set for a given test
     */
    public static ResponseCorrectAnswer loadResponseForGivenSurveyOrTest(String responseBaseID, String fileBasePath) {
        String selectedResponsePath = FileUtils.listAndPickResponseFromDir(responseBaseID, fileBasePath);
        return SerializationHelper.deserialize(ResponseCorrectAnswer.class, selectedResponsePath);
    }

    /*/
        Saves the current response by serializing depending on the type of test
     */
    public void saveResponse(String responseType, String responseID) {
        this.responseID = responseID;
        if (responseType.equals("surveyResponse")) {
            SerializationHelper.serialize(this.getClass(), this, surveyResponseBasePath, responseID);
        } else if (responseType.equals("testResponse")) {
            SerializationHelper.serialize(this.getClass(), this, testResponseBasePath, responseID);
        }
    }
}
