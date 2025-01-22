package utils;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author Sean Grimes, sean@seanpgrimes.com
 *
 * Validate that values are what they're supposed to be. Very helpful when getting user
 * input.
 */
@SuppressWarnings({"WeakerAccess", "unused", "BooleanMethodIsAlwaysInverted"})
public class Validation {
    /**
     * Validate that a string is a number
     * @param num The string to be validated
     * @return True if the String can be parsed to an int, else false
     */
    public static boolean isInt(String num){
        try{
            Integer.parseInt(num);
            return true;
        }
        // Not an int
        catch(Exception e){
            return false;
        }
    }

    /**
     * Validate that a string is a double
     * @param num The string to be validated
     * @return True if it's a double, else false
     */
    public static boolean isDouble(String num){
        try{
            Double.parseDouble(num);
            return true;
        }
        // Not a double
        catch(Exception e){
            return false;
        }
    }

    /**
     * Validate that a String is a valid integer between an inclusive range
     * @param start The start of the inclusive range
     * @param end The end of the inclusive range
     * @param num The String to be validated
     * @return True if it's a valid int between the inclusive range, else false
     */
    public static boolean isIntBetweenInclusive(int start, int end, String num){
        if(!isInt(num)) {
            return false;
        }
        int val = Integer.parseInt(num);
        return val >= start && val <= end;
    }

    /**
     * @author Ryan Gan(zg88)
     * Validate if a string is in a range of characters
     * @param wordLimit The character limit in a word
     * @param input The user input
     * @return A boolean true if input is in char limit and false otherwise
     */
    public static boolean isInWordLimit(int wordLimit, String input) {
        return input.length() <= wordLimit;
    }

    /**
     * @author Ryan Gan(zg88)
     * Check if user input id is existed, if not, then it could be used to name the survey
     * @param dirPath path to a directory
     * @param uniqueID an ID of user's choice
     * @return a boolean, false if it doesn't exist - can be used, true if it already existed - cannot be used
     */
    public static boolean isUniqueIdExisted(String dirPath, int uniqueID, String baseID) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();

        if(files != null && files.length !=0) {
            for (File file : files) {
                if (file.isFile()) {
                    String currentFileID = file.getName().replace(baseID, "");
                    if (Integer.toString(uniqueID).equals(currentFileID)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @author Ryan Gan
     * Validate if a date input is valid
     * @param dateInput A date input
     * @return a boolean true if the date is valid, false otherwise
     */
    public static boolean isDateValid(String dateInput) {
        // Validate if the input is in the exact length of the format: yyyy-mm-dd
        if (dateInput.length() != 10) {
            return false;
        }

        // Validate if the input contains 3 parts: years, months, and days
        String[] dateInArray = dateInput.split("-");
        if (dateInArray.length != 3) {
            return false;
        }

        // Validate if the length of each element is correct: years.length = 4; months.length = 2; days.length = 2
        String years = dateInArray[0];
        String months = dateInArray[1];
        String days = dateInArray[2];

        if (years.length() != 4 || months.length() != 2 || days.length() != 2) {
            return false;
        }

        /*
            The part below till the end of this method of date validation is inspired from the source Stack Overflow
            Written by: Aravind Yarram;
            Source link: https://stackoverflow.com/questions/226910/how-to-sanity-check-a-date-in-java
         */
        String DATE_FORMAT = "yyyy-MM-dd";
        try {
            DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            dateFormat.setLenient(false);
            dateFormat.parse(dateInput);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
