package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Sean Grimes, sean@seanpgrimes.com
 * 
 * Utility class for getting user input
 */
@SuppressWarnings("WeakerAccess")
public class Input {

    // Validate input as an int between inclusive range
    public static int readIntInRange(int start, int end){
        String failSpeech = "Please enter a valid number between " + start + " - " + end;
        // BufferedReader is better than Scanner. Prove me wrong.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = "z";  // Some non-integer value to fail parsing if checks are missed
        try{
            line = br.readLine();
            while(
                       line == null
                    || line.length() <= 0
                    || !Validation.isIntBetweenInclusive(start, end, line)
            ){
                System.out.println(failSpeech);
                line = br.readLine();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        //noinspection ConstantConditions
        return Integer.parseInt(line);
    }

    // @author Ryan Gan(zg88)
    // Prompt for matching choice with a char limit
    public static String promptForInputWithLimit(int charLimit){
        String failSpeech = "Character range is from 1 to " + charLimit + ". Please Try again";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = "a".repeat(charLimit + 1);  // Some input value to fail parsing if checks are missed
        try{
            line = br.readLine();
            while(
                    line == null
                            || line.length() <= 0
                            || !Validation.isInWordLimit(charLimit, line)
            ){
                System.out.println(failSpeech);
                line = br.readLine();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return line;
    }

    // @author Ryan Gan(zg88)
    // Prompt for an integer to be used
    public static int promptForAnInt(){
        String failSpeech = "Please enter a valid integer.";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = "a";  // Some non-integer value to fail parsing if checks are missed
        try{
            line = br.readLine();
            while(
                    line == null
                            || line.length() <= 0
                            || !Validation.isInt(line)
            ){
                System.out.println(failSpeech);
                line = br.readLine();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }

        //noinspection ConstantConditions
        return Integer.parseInt(line);
    }

    // @author Ryan Gan(zg88)
    // Prompt for a valid date input
    public static String promptForValidDate(){
        String failSpeech = "Invalid date input. Correct date format: YYYY-MM-DD";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = "a";  // Some input value to fail parsing if checks are missed
        try{
            line = br.readLine();
            while(
                    line == null
                            || line.length() <= 0
                            || !Validation.isDateValid(line)
            ){
                System.out.println(failSpeech);
                line = br.readLine();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return line;
    }
}
