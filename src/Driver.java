import utils.FileUtils;
import utils.Input;

public class Driver {
    private final String[] mainMenuOptions = {"Survey","Test","Quit"};
    private final String[] surveyMenuOptions = {"Create a new Survey","Display an existing Survey","Load an existing Survey",
            "Save the current Survey","Take the current Survey","Modify the current Survey","Tabulate a survey","Return to previous menu"};
    private final String[] testMenuOptions = {"Create a new Test","Display an existing Test without correct answers",
            "Display an existing Test with correct answers", "Load an existing Test","Save the current Test","Take the current Test",
            "Modify the current Survey","Tabulate a Test","Grade a Test","Return to previous menu"};
    private final String[] questionMenuOptions = {"Add a new T/F question","Add a new multiple-choice question","Add a new short answer question",
            "Add a new essay question", "Add a new date question","Add a new matching question","Return to previous menu"};
    private int currentMenu = 1; // MainMenu = 1; SurveyMenu = 2; TestMenu = 3; QuestionMenu = 4
    private boolean exiting = false;
    private Survey currentSurvey = null;
    private Test currentTest = null;
    private boolean modifiedWorkWasSaved = true;
    private boolean lastMenuVisitedWasSurvey;

    // Driver executes this method on loading
    public void Start() {
        System.out.println("WELCOME TO THE SURVEY SYSTEM!\n");
        do {
            displayMenu();

            // User chooses an option in main menu
            if (currentMenu == 1) {
                int userChoice = Input.readIntInRange(1,3); // Prompt for option

                // 1 is the survey menu, 2 is the test menu, 3 is exit the survey system
                if (userChoice == 1) {
                    currentMenu = 2;
                    lastMenuVisitedWasSurvey = true;
                } else if (userChoice == 2) {
                    currentMenu = 3;
                    lastMenuVisitedWasSurvey = false;
                } else if (userChoice == 3) {
                    exiting = true;
                    System.out.println("Exiting Survey System...");
                }
            }

            // User chooses an option in survey menu
            else if (currentMenu == 2) {
                int userChoice = Input.readIntInRange(1,8); // Prompt for option

                // User chooses to Exit survey menu and return to the main menu
                if(userChoice == 8) {

                    // Check if the modified work was saved, if not, ask if the user wants to save
                    if(!modifiedWorkWasSaved) {
                        notifyUnsavedWorkHelper();
                    }

                    System.out.println("Returning to previous menu...\n");
                    currentSurvey = null; // reset current loaded survey
                    currentMenu = 1; // return to main menu
                }

                // User chooses to Create a new Survey
                else if (userChoice == 1) {
                    System.out.println("Option 1 is selected from Survey Menu 2, creating a new survey...\n");
                    create();
                }

                // User chooses to Display the current survey
                else if (userChoice == 2) {
                    System.out.println("Option 2 is selected from Survey Menu 2, displaying the current survey...\n");

                    // Check following conditions before displaying
                    // 1. if there is a survey loaded
                    // 2. if the survey loaded has at least one question
                    // If both passed then display
                    if (currentSurvey == null) {
                        System.out.println("You must have a survey loaded in order to display it.\n");
                    } else if (currentSurvey.getQuestionList().size() <= 0) {
                        System.out.println("The current loaded survey is empty. Please select another survey to display.\n");
                    } else {
                        System.out.println("Displaying survey: " + currentSurvey.getFileID());
                        currentSurvey.display();
                    }
                }

                // User chooses to Load a Survey from the dir
                else if (userChoice == 3) {
                    System.out.println("Option 3 is selected from Survey Menu 2, loading a survey from the directory...\n");

                    // First check if the last loaded Survey was saved, if not, ask if user wants to save
                    // No need to check if currentSurvey = null because boolean is true when currentSurvey = null
                    if (!modifiedWorkWasSaved) {
                        notifyUnsavedWorkHelper();
                    }

                    // Secondly check if dir exists
                    // Then check if the Survey dir is not empty before loading
                    if (FileUtils.isDirNotExisted(Survey.basePath)) {
                        System.out.println("Survey directory does not exist. Please save a survey to initialize the directory.\n");
                    } else if (FileUtils.isEmptyDir(Survey.basePath)) {
                        System.out.println("Empty survey directory. Please start by creating a new survey.\n");
                    } else {
                        currentSurvey = Survey.load(Survey.basePath);
                        modifiedWorkWasSaved = true; // Will always be true after initial loading, because no changes made
                        System.out.println(currentSurvey.getFileID() + " is now loaded.\n");
                    }
                }

                // User chooses to Save the current loaded survey to dir
                else if (userChoice == 4) {
                    System.out.println("Option 4 is selected from Survey Menu 2, saving the current loaded survey...\n");

                    // First check if the there is a survey loaded before saving
                    // If yes, proceeding to save, save checker set to true
                    if (currentSurvey == null) {
                        System.out.println("You must have a survey loaded in order to save it.\n");
                    } else {
                        currentSurvey.save();
                        modifiedWorkWasSaved = true;
                    }
                }

                // User chooses to Take the current loaded survey
                else if (userChoice == 5) {
                    System.out.println("Option 5 is selected from Survey Menu 2, taking the current loaded survey...\n");

                    // First check if the last loaded Survey was saved, if not, ask if user wants to save
                    if (!modifiedWorkWasSaved) {
                        notifyUnsavedWorkHelper();
                    }

                    // Check if the there is a survey loaded before taking, if not, return to survey menu 2
                    if (currentSurvey == null) {
                        System.out.println("You must have a survey loaded in order to take it.\n");
                    }

                    // If there is a survey loaded, check the following:
                    //  1. if the dir exists
                    //  2. if any survey in the dir
                    // If both true, prompt the user to choose the survey that they want to take, then load it
                    else {
                        if (FileUtils.isDirNotExisted(Survey.basePath)) {
                            System.out.println("Survey directory does not exist. Please save a survey to initialize the directory.\n");
                        } else if (FileUtils.isEmptyDir(Survey.basePath)) {
                            System.out.println("Empty survey directory. Please save the current survey to begin.\n");
                        } else {
                            System.out.println("Enter the number of the survey you wish to take:");
                            currentSurvey = Survey.load(Survey.basePath);
                            System.out.println(currentSurvey.getFileID() + " is now loaded.\n");

                            // After the survey is loaded, check if any questions in the survey
                            // Finally, if yes, execute the take function
                            if (currentSurvey.getQuestionList().size() <= 0) {
                                System.out.println("The current loaded survey is empty. Please select another survey to take.\n");
                            } else {
                                currentSurvey.take();
                            }
                        }
                    }
                }

                // User chooses to Modify the current loaded survey
                else if (userChoice == 6) {
                    System.out.println("Option 6 is selected from Survey Menu 2, modifying the current loaded survey...\n");

                    // First check if the there is a survey loaded before modifying
                    // Then check if the loaded survey has at least 1 question before modifying
                    if (currentSurvey == null) {
                        System.out.println("You must have a survey loaded in order to modify it.\n");
                    } else if (currentSurvey.getQuestionList().size() <= 0) {
                        System.out.println("The current loaded survey is empty. Please select another survey to modify.\n");
                    }

                    // If both conditions passed, display all questions for user to choose
                    // Prompt for a choice of question and call modify method
                    // If boolean returned from modify method is true, means changes were made, set save checker to false so this will be notified
                    // If boolean returned from modify method is false, means no changes were made and should not change save checker
                    // Set save checker to false
                    else {
                        System.out.println("Below are all questions of the current loaded survey.");
                        currentSurvey.display();

                        System.out.println("What question do you wish to modify?");
                        int questionChoice = Input.readIntInRange(1,currentSurvey.getQuestionList().size());
                        if (currentSurvey.modify(questionChoice)) {
                            modifiedWorkWasSaved = false;
                        } else {
                            System.out.println("No changes were made.\n");
                        }
                    }
                }

                // User chooses to Tabulate the current loaded survey
                else if (userChoice == 7) {
                    System.out.println("Option 7 is selected from Survey Menu 2, tabulating the current loaded survey...\n");

                    // First check if the last loaded Survey was saved, if not, ask if user wants to save
                    // No need to check if currentSurvey = null because boolean is true when currentSurvey = null
                    if (!modifiedWorkWasSaved) {
                        notifyUnsavedWorkHelper();
                    }

                    // Secondly load the survey by checking if dir exists
                    // Then check if the Survey dir is not empty before loading
                    if (FileUtils.isDirNotExisted(ResponseCorrectAnswer.surveyResponseBasePath)) {
                        System.out.println("Survey response directory does not exist. Please take a survey to initialize the directory.\n");
                    } else if (FileUtils.isEmptyDir(ResponseCorrectAnswer.surveyResponseBasePath)) {
                        System.out.println("Empty survey response directory. Please start by creating a new survey.\n");
                    } else {
                        System.out.println("Select an existing survey to load for tabulating.");
                        currentSurvey = Survey.load(Survey.basePath);
                        modifiedWorkWasSaved = true; // Will always be true after initial loading, because no changes made
                        System.out.println(currentSurvey.getFileID() + " is now loaded.\n");

                        if (currentSurvey.getQuestionList().size() <= 0) {
                            System.out.println("The current loaded survey is empty. Please select another survey to tabulate.\n");
                        } else if (!FileUtils.responseSetForGivenTestExists(ResponseCorrectAnswer.surveyResponseBasePath, currentSurvey.responseBaseID)) {
                            System.out.println("There is no response set for this survey. Start by taking the survey or choose another survey to tabulate.\n");
                        } else {
                            currentSurvey.addLineSeparator();
                            System.out.println(currentSurvey.tabulate());
                            currentSurvey.addLineSeparator();
                        }
                    }
                }
            }

            // User chooses an option in the test menu
            else if (currentMenu == 3) {
                int userChoice = Input.readIntInRange(1,10); // Prompt for option

                // User chooses to Exit test menu and return to the main menu
                if(userChoice == 10) {

                    // Check if the modified work was saved, if not, ask if the user wants to save
                    if(!modifiedWorkWasSaved) {
                        notifyUnsavedWorkHelper();
                    }

                    System.out.println("Returning to previous menu...\n");
                    currentTest = null; // reset current loaded test
                    currentMenu = 1; // return to main menu
                }

                // User chooses to Create a new Test
                else if (userChoice == 1) {
                    System.out.println("Option 1 is selected from Test Menu 2, creating a new Test...\n");
                    create();
                }

                // User chooses to Display the current test without correct answers
                else if (userChoice == 2) {
                    System.out.println("Option 2 is selected from Test Menu 2, displaying the current test without correct answers...\n");

                    // Check following conditions before displaying
                    // 1. if there is a test loaded
                    // 2. if the test loaded has at least one question
                    // If both passed then display
                    if (currentTest == null) {
                        System.out.println("You must have a test loaded in order to display it.\n");
                    } else if (currentTest.getQuestionList().size() <= 0) {
                        System.out.println("The current loaded test is empty. Please select another test to display.\n");
                    } else {
                        System.out.println("Displaying test without correct answers: " + currentTest.getFileID());
                        currentTest.display();
                    }
                }

                // User chooses to Display the current test with correct answers
                else if (userChoice == 3) {
                    System.out.println("Option 3 is selected from Test Menu 2, displaying the current test with correct answers...\n");

                    // Check following conditions before displaying
                    // 1. if there is a test loaded
                    // 2. if the test loaded has at least one question
                    // If both passed then display
                    if (currentTest == null) {
                        System.out.println("You must have a test loaded in order to display it.\n");
                    } else if (currentTest.getQuestionList().size() <= 0) {
                        System.out.println("The current loaded test is empty. Please select another test to display.\n");
                    } else {
                        System.out.println("Displaying test with correct answers: " + currentTest.getFileID());
                        currentTest.displayWithAns();
                    }
                }

                // User chooses to Load a Test from the dir
                else if (userChoice == 4) {
                    System.out.println("Option 4 is selected from Test Menu 2, loading a Test from the directory...\n");

                    // First check if the last loaded Test was saved, if not, ask if user wants to save
                    // No need to check if currentTest = null because boolean is true when currentTest = null
                    if (!modifiedWorkWasSaved) {
                        notifyUnsavedWorkHelper();
                    }

                    // Secondly check if dir exists
                    // Then check if the Test dir is not empty before loading
                    if (FileUtils.isDirNotExisted(Test.basePath)) {
                        System.out.println("Test directory does not exist. Please save a test to initialize the directory.\n");
                    } else if (FileUtils.isEmptyDir(Test.basePath)) {
                        System.out.println("Empty test directory. Please start by creating a new test.\n");
                    } else {
                        currentTest = Test.load(Test.basePath);
                        modifiedWorkWasSaved = true; // Will always be true after initial loading, because no changes made
                        System.out.println(currentTest.getFileID() + " is now loaded.\n");
                    }
                }

                // User chooses to Save the current loaded test to dir
                else if (userChoice == 5) {
                    System.out.println("Option 5 is selected from Test Menu 2, saving the current loaded test...\n");

                    // First check if the there is a test loaded before saving
                    // If yes, proceeding to save, save checker set to true
                    if (currentTest == null) {
                        System.out.println("You must have a test loaded in order to save it.\n");
                    } else {
                        currentTest.save();
                        modifiedWorkWasSaved = true;
                    }
                }

                // User chooses to Take the current loaded test
                else if (userChoice == 6) {
                    System.out.println("Option 6 is selected from Test Menu 2, taking the current loaded test...\n");

                    // First check if the last loaded test was saved, if not, ask if user wants to save
                    if (!modifiedWorkWasSaved) {
                        notifyUnsavedWorkHelper();
                    }

                    // Check if the there is a test loaded before taking, if not, return to test menu 2
                    if (currentTest == null) {
                        System.out.println("You must have a test loaded in order to take it.\n");
                    }

                    // If there is a test loaded, check the following:
                    //  1. if the dir exists
                    //  2. if any test in the dir
                    // If both true, prompt the user to choose the test that they want to take, then load it
                    else {
                        if (FileUtils.isDirNotExisted(Test.basePath)) {
                            System.out.println("Test directory does not exist. Please save a test to initialize the directory.\n");
                        } else if (FileUtils.isEmptyDir(Test.basePath)) {
                            System.out.println("Empty test directory. Please save the current test to begin.\n");
                        } else {
                            System.out.println("Enter the number of the test you wish to take:");
                            currentTest = Test.load(Test.basePath);
                            System.out.println(currentTest.getFileID() + " is now loaded.\n");

                            // After the test is loaded, check if any questions in the test
                            // Finally, if yes, execute the take function
                            if (currentTest.getQuestionList().size() <= 0) {
                                System.out.println("The current loaded test is empty. Please select another test to take.\n");
                            } else {
                                currentTest.take();
                            }
                        }
                    }
                }

                // User chooses to Modify the current loaded test
                else if (userChoice == 7) {
                    System.out.println("Option 7 is selected from Test Menu 2, modifying the current loaded test...\n");

                    // First check if the there is a test loaded before modifying
                    // Then check if the loaded test has at least 1 question before modifying
                    if (currentTest == null) {
                        System.out.println("You must have a test loaded in order to modify it.\n");
                    } else if (currentTest.getQuestionList().size() <= 0) {
                        System.out.println("The current loaded test is empty. Please select another test to modify.\n");
                    }

                    // If both conditions passed, display all questions for user to choose
                    // Prompt for a choice of question and call modify method
                    // If boolean returned from modify method is true, means changes were made, set save checker to false so this will be notified
                    // If boolean returned from modify method is false, means no changes were made and should not change save checker
                    // Set save checker to false
                    else {
                        System.out.println("Below are all questions of the current loaded test.");
                        currentTest.displayWithAns();

                        System.out.println("What question do you wish to modify?");
                        int questionChoice = Input.readIntInRange(1,currentTest.getQuestionList().size());
                        if (currentTest.modify(questionChoice)) {
                            modifiedWorkWasSaved = false;
                        } else {
                            System.out.println("No changes were made.\n");
                        }
                    }
                }

                // User chooses to Tabulate the current loaded test
                else if (userChoice == 8) {
                    System.out.println("Option 8 is selected from Test Menu 2, tabulating the current loaded test...\n");

                    // First check if the last loaded Test was saved, if not, ask if user wants to save
                    // No need to check if currentTest = null because boolean is true when currentTest = null
                    if (!modifiedWorkWasSaved) {
                        notifyUnsavedWorkHelper();
                    }

                    // Secondly load the test by checking if dir exists
                    // Then check if the test dir is not empty before loading
                    if (FileUtils.isDirNotExisted(ResponseCorrectAnswer.testResponseBasePath)) {
                        System.out.println("Test response directory does not exist. Please take a test to initialize the directory.\n");
                    } else if (FileUtils.isEmptyDir(ResponseCorrectAnswer.testResponseBasePath)) {
                        System.out.println("Empty test response directory. Please start by creating a new test.\n");
                    } else {
                        System.out.println("Select an existing test to load for tabulating.");
                        currentTest = Test.load(Test.basePath);
                        modifiedWorkWasSaved = true; // Will always be true after initial loading, because no changes made
                        System.out.println(currentTest.getFileID() + " is now loaded.\n");

                        if (currentTest.getQuestionList().size() <= 0) {
                            System.out.println("The current loaded test is empty. Please select another test to tabulate.\n");
                        } else if (!FileUtils.responseSetForGivenTestExists(ResponseCorrectAnswer.testResponseBasePath, currentTest.responseBaseID)) {
                            System.out.println("There is no response set for this test. Start by taking the test or choose another test to tabulate.\n");
                        } else {
                            currentTest.addLineSeparator();
                            System.out.println(currentTest.tabulate());
                            currentTest.addLineSeparator();
                        }
                    }
                }

                // User chooses to Grade a test
                else if (userChoice == 9) {
                    System.out.println("Option 9 is selected from Test Menu 2, grading a selected test...\n");

                    // First check if the last loaded Test was saved, if not, ask if user wants to save
                    // No need to check if currentTest = null because boolean is true when currentTest = null
                    if (!modifiedWorkWasSaved) {
                        notifyUnsavedWorkHelper();
                    }

                    // Secondly load the test by checking if dir exists
                    // Then check if the Test dir is not empty before loading
                    if (FileUtils.isDirNotExisted(Test.basePath)) {
                        System.out.println("Test directory does not exist. Please save a test to initialize the directory.\n");
                    } else if (FileUtils.isEmptyDir(Test.basePath)) {
                        System.out.println("Empty test directory. Please start by creating a new test.\n");
                    } else {
                        System.out.println("Select an existing test to load for grading.");
                        currentTest = Test.load(Test.basePath);
                        modifiedWorkWasSaved = true; // Will always be true after initial loading, because no changes made
                        System.out.println(currentTest.getFileID() + " is now loaded.\n");

                        // After the test is loaded, load an existing response of the test to grade by:
                        //  1. check if the test response dir exists
                        //  2. check if the test response dir is not empty
                        //  3. check if there's a response associated to the current test
                        //  4. if all passed, prompt to pick a response to grade
                        if (FileUtils.isDirNotExisted(ResponseCorrectAnswer.testResponseBasePath)) {
                            System.out.println("Test response directory does not exist. Please take a test to initialize the directory.\n");
                        } else if (FileUtils.isEmptyDir(ResponseCorrectAnswer.testResponseBasePath)) {
                            System.out.println("Empty test response directory. Please start by taking a new test.\n");
                        } else if (!FileUtils.responseSetForGivenTestExists(ResponseCorrectAnswer.testResponseBasePath, currentTest.responseBaseID)) {
                            System.out.println("There is no response set for this test. Start by taking the test or choose another test to grade.\n");
                        } else {
                            ResponseCorrectAnswer selectedResponseSet = ResponseCorrectAnswer.loadResponseForGivenSurveyOrTest(currentTest.responseBaseID, ResponseCorrectAnswer.testResponseBasePath);
                            currentTest.addLineSeparator();
                            String gradeOutput = currentTest.grade(selectedResponseSet);
                            System.out.println(gradeOutput);
                            currentTest.addLineSeparator();
                        }
                    }
                }
            }

            // User chooses an option in Add Question menu
            else if (currentMenu == 4) {
                int userChoice = Input.readIntInRange(1,7); // Prompt for option

                // User chooses to Exit the add question menu and return to the previous menu (either survey or test menu depending on lastMenu checker)
                if(userChoice == 7) {
                    System.out.println("Returning to previous menu...\n");
                    if (lastMenuVisitedWasSurvey) {
                        currentMenu = 2;
                    } else {
                        currentMenu = 3;
                    }
                }

                // Option 1,2,3,4,5,6 are adding a different type of question, this will be handled by addQuestion method in Survey
                // Save checker set to false for later notification
                else {
                    if (lastMenuVisitedWasSurvey) {
                        currentSurvey.addQuestion(userChoice);
                    } else {
                        currentTest.addQuestion(userChoice);
                    }
                    modifiedWorkWasSaved = false;
                }
            }
        }while (!exiting);

    }

    // Display the menu base on what currentMenu is
    // currentMenu = 1 -> Survey Menu; currentMenu = 2 -> add questions menu
    public void displayMenu() {
        String[] menuOptions;
        StringBuilder menuPrompt = new StringBuilder();
        if (currentMenu == 1) {
            menuOptions = mainMenuOptions;
            menuPrompt.append("Menu 1:\n");
        } else if (currentMenu == 2){
            menuOptions = surveyMenuOptions;
            menuPrompt.append("Survey Menu 2:\n");
        } else if (currentMenu == 3) {
            menuOptions = testMenuOptions;
            menuPrompt.append("Test Menu 2:\n");
        } else{
            menuOptions = questionMenuOptions;
            menuPrompt.append("Menu 3:\n");
        }

        for(int i = 0; i< menuOptions.length; i++) {
            menuPrompt.append(i + 1).append("). ").append(menuOptions[i]).append("\n");
        }
        System.out.println(menuPrompt);
    }

    // Create a Survey or a Test method
    private void create() {

        // Pre-requisite set up:
        // 1. If the last modified survey was not saved, prompt whether to save
        // 2. Instantiate a new empty Survey or Test object to create questions with, depending on the currentMenu
        // 3. Set menu to add question menu to loop creating questions
        if (!modifiedWorkWasSaved) {
            notifyUnsavedWorkHelper();
        }

        if (currentMenu == 2) {
            currentSurvey = new Survey();
        } else if (currentMenu == 3) {
            currentTest = new Test();
        }
        modifiedWorkWasSaved = false; // Save checker set to false when new item created
        currentMenu = 4;
    }

    // Helper Method
    // Prompt the user whether to save their modified work
    private void notifyUnsavedWorkHelper() {
        System.out.println("Do you want to save the current file before proceeding? 1.Yes / 2.No");
        if (Input.readIntInRange(1,2) == 1) {
            if (currentMenu == 2) {
                currentSurvey.save();
            } else if (currentMenu == 3) {
                currentTest.save();
            }
        }
        modifiedWorkWasSaved = true;
    }
}
