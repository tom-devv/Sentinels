package dev.tom.sentinels.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.List;

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

    public static List<Component> asComponent(String... minimessage){
        MiniMessage mm = MiniMessage.miniMessage();
        List<Component> components = new ArrayList<>();
        for(String s : minimessage){
            components.add(mm.deserialize(s));
        }
        return components;
    }

    public static List<Component> asComponent(List<String> minimessage){
        return asComponent(minimessage.toArray(new String[0]));
    }

}
