package utils;

/**
 * @author Sean Grimes, sean@seanpgrimes.com
 *
 * Helper class for accessing System time
 */
public class TimeHelper {
    public static String getUniqueTimeStamp(){
        long seconds = System.currentTimeMillis() / 1000L;
        return Long.toString(seconds);
    }
}
