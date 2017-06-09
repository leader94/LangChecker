package bluetowel.com.langchecker.utils;

/**
 * Created by Pawan on 5/14/2017.
 */

public class Utilities {


    public static enum CallbackResultCode {
        FAIL, SUCCESS
    }


    public static void getScreenSize() {

    }


    static String getStringBefore(String text) {
        int i;
        String before = "";
        int count = 0, reverseStartIndex = 0, reverseEndIndex = text.length() - 1;
        for (i = text.length() - 1; i >= 0; i--) {
            if (text.charAt(i) == ' ') {
                count++;
                if (count == 3) {
                    reverseStartIndex = i;
                    break;
                }
            }
        }
        for (i = reverseStartIndex; i <= reverseEndIndex; i++) {
            before = before + text.charAt(i);
        }
        return before;
    }

    static String getStringAfter(String text)

    {
        String after = "";
        int i, count = 0, forwardStartIndex = 0, forwardEndIndex = text.length() - 1;
        for (i = 0; i < text.length() - 1; i++) {
            if (text.charAt(i) == ' ') {
                count++;
                if (count == 3) {
                    forwardEndIndex = i;
                    break;
                }
            }
        }
        System.out.println(forwardStartIndex);
        System.out.println(forwardEndIndex);

        for (i = forwardStartIndex; i <= forwardEndIndex; i++) {
            after = after + text.charAt(i);
        }
        return after;
    }

}
