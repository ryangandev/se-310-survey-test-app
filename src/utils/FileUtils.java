package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Sean Grimes, sean@seanpgrimes.com
 *
 * File utility class.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class FileUtils {
    /**
     * Append data to an existing file
     * @param filename Path to the file
     * @param str The string to write to the new file
     */
    public static boolean appendToFile(String filename, String str){
        BufferedWriter writer;
        try{
            writer = Files.newBufferedWriter(Paths.get(filename),
                    StandardOpenOption.APPEND);
            writer.write(str);
            writer.close();
        }
        catch(IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Write a new file to disk. Will overwrite existing file
     * @param fileName Path to the file
     * @param str The string to write to the file
     */
    public static boolean writeNewFile(String fileName, String str){
        BufferedWriter writer;
        try {
            writer = Files.newBufferedWriter(Paths.get(fileName));
            writer.write(str);
            writer.close();
        }
        catch(IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Read a file line by line
     * @param filePath The path to the file
     * @return A list of lines of the file (as strings) if file is found and readable
     */
    public static List<String> readLineByLine(String filePath) {
        List<String> lines = new ArrayList<>();
        BufferedReader br = null;
        try{
            br = Files.newBufferedReader(Paths.get(filePath));
            String line = br.readLine();
            while(line != null){
                lines.add(line);
                line = br.readLine();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            if(br != null){
                try{
                    br.close();
                }
                catch(IOException e){
                    System.err.println("Failed to finalize readLineByLine");
                }
            }
        }
        return lines;
    }

    /**
     * Create a directory if one does not exist
     * @param directoryPath The path to the directory to be created
     * @return True if success, else false
     */
    public static boolean createDirectory(String directoryPath){
        File dir = new File(directoryPath);
        // Nothing exists here, create the directory and all parent directories
        if(!dir.exists()) {
            return dir.mkdirs();
        }

        // Something exists at the supplied path, see if it's a directory. If it is,
        // return true. If it's not, it's something else so return false.
        return dir.isDirectory();
    }

    /**
     * Get a sorted list of all files in a directory
     * @param path The path to the directory
     * @return The sorted list of paths
     */
    public static List<String> getAllFilePathsInDir(String path){
        List<String> paths = new ArrayList<>();
        File[] files = new File(path).listFiles();
        if(files == null || files.length == 0) {
            throw new IllegalStateException(path + " is empty");
        }
        for(File f : files){
            if(f.isFile()) {
                paths.add(f.getPath());
            }
        }
        return sortPaths(paths);
    }

    /**
     * Sort strings *in place* using a custom comparator 
     * @param paths The list of strings to be sorted
     * @return The sorted list of strings
     */
    private static List<String> sortPaths(List<String> paths){
        if(paths.isEmpty()) {
            return Collections.emptyList();
        }
        if(paths.size() == 1) {
            return paths;
        }
        Comparator<String> comp = Comparator.comparing((String x) -> x);
        paths.sort(comp);
        return paths;
    }

    /**
     * Displays a user a list of files available in a given directory. Allows user to
     * select a single file from this list of files
     * @param dirPath The path to the directory that holds the files
     * @return The absolute path to the selected file
     */
    public static String listAndPickFileFromDir(String dirPath){
        File dir = new File(dirPath);
        if(!dir.exists() || !dir.isDirectory()) {
            throw new IllegalStateException(dirPath + " is invalid");
        }

        File[] files = dir.listFiles();
        if(files == null || files.length == 0) {
            throw new IllegalStateException(dirPath + " is empty");
        }

        System.out.println("Please enter number of file to load: ");
        for(int i = 0; i < files.length; ++i){
            if(files[i].isFile())
                // Adding 1 to avoid 0-indexed UI. getName to chop off full path.
            {
                System.out.println((i + 1) + ") " + files[i].getName());
            }
        }

        // Get a valid integer between 1 and number of files
        int fileSelection = Input.readIntInRange(1, files.length);

        // Remember to subtract 1 to get back to 0-indexed value
        return files[fileSelection - 1].getAbsolutePath();
    }

    /**
     * @author Ryan Gan(zg88)
     * Purpose is to prevent throwing exceptions
     * Check if given path exists and if it is a directory
     * @param dirPath path to a directory
     * @return a boolean, true if exists, false otherwise
     */
    public static boolean isDirNotExisted(String dirPath) {
        File dir = new File(dirPath);
        return !dir.exists() || !dir.isDirectory();
    }

    /**
     * @author Ryan Gan(zg88)
     * Check if the directory is empty
     * Purpose is to prevent throwing exceptions
     * @param dirPath path to a directory
     * @return a boolean, true if empty, false otherwise
     */
    public static boolean isEmptyDir(String dirPath) {
        File[] files = new File(dirPath).listFiles();
        return files == null || files.length == 0;
    }

    /**
     * @author Ryan Gan(zg88)
     * Get a list of file names given dirPath
     * @param dirPath path to a dir that has files
     * @return a list of file names
     */
    public static List<String> getListOfFileNames(String dirPath){
        List<String> fileNameList = new ArrayList<>();

        File dir = new File(dirPath);
        if(!dir.exists() || !dir.isDirectory()) {
            throw new IllegalStateException(dirPath + " is invalid");
        }

        File[] files = dir.listFiles();
        if(files == null || files.length == 0) {
            throw new IllegalStateException(dirPath + " is empty");
        }

        for (File file : files) {
            if (file.isFile()) {
                fileNameList.add(file.getName());
            }
        }

        return fileNameList;
    }

    /**
     * Check if there is a response set for given test
     * @param dirPath path to dir that stores response
     * @param responseBaseID an ID that file name starts with for given test
     * @return a boolean that returns true if a response set exist
     */
    public static boolean responseSetForGivenTestExists(String dirPath, String responseBaseID) {
        List<String> fileNameList = getListOfFileNames(dirPath);

        for (String fileName: fileNameList) {
            if (fileName.startsWith(responseBaseID)) {
                return true;
            }
        }
        return false;
    }

    public static List<File> getAllResponseForGivenSurveyOrTestFromDir(String responseBaseID, String dirPath) {
        File dir = new File(dirPath);
        if(!dir.exists() || !dir.isDirectory()) {
            throw new IllegalStateException(dirPath + " is invalid");
        }

        File[] files = dir.listFiles();
        List<File> associatedFiles = new ArrayList<>();
        if(files == null || files.length == 0) {
            throw new IllegalStateException(dirPath + " is empty");
        }

        // Store response associated to the test in the list
        for (File file : files) {
            if (file.isFile())
            // Adding 1 to avoid 0-indexed UI. getName to chop off full path.
            {
                if (file.getName().startsWith(responseBaseID)) {
                    associatedFiles.add(file);
                }
            }
        }

        return associatedFiles;
    }

    /**
     * Display the response files that are associated to a given test and let user pick one
     * @param dirPath The path to the directory that holds the files
     * @return The absolute path to the selected file
     */
    public static String listAndPickResponseFromDir(String responseBaseID, String dirPath){
        List<File> associatedFiles = getAllResponseForGivenSurveyOrTestFromDir(responseBaseID,dirPath);

        System.out.println("Select an existing response set: ");
        for(int i = 0; i < associatedFiles.size(); ++i){
            // Adding 1 to avoid 0-indexed UI. getName to chop off full path.
            System.out.println((i + 1) + ") " + associatedFiles.get(i).getName());
        }

        // Get a valid integer between 1 and number of files
        int fileSelection = Input.readIntInRange(1, associatedFiles.size());

        // Remember to subtract 1 to get back to 0-indexed value
        return associatedFiles.get(fileSelection-1).getAbsolutePath();
    }
}
