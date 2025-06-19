package dev.tom.sentinels.utils;

public class TextUtil {


    public static String upperCaseFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Makes fields and attributes pretty
     * e.g searchRadius -> Search Radius
     * @param str
     * @return
     */
    public static String prettyFieldName(String str){
        String result = str.replaceAll("([A-Z])", " $1");
        return upperCaseFirstLetter(result).strip();
    }
}
