public class Util {
    public static String getIdFromMention(String mention) {
        return mention.substring(3, mention.length() - 1);
    }
}
