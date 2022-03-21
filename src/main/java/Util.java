public class Util {
    public static String getIdFromMention(String mention) {
        return mention.substring(3, mention.length() - 1);
    }

    public static boolean isValidMention(String mention) {
        return mention.length() > 3 && mention.charAt(0) == '<';
    }
}
