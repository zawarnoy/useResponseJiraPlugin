package useresponse.atlassian.plugins.jira.service;

import java.util.HashMap;
import java.util.Map;

public class IconsService {
    public static Map<String, String> getIconsMap() {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("smile", ":-)");
        resultMap.put("tongue", ":P");
        resultMap.put("wink", ";)");
        resultMap.put("biggrin", ":D");
        resultMap.put("sad", ":(");
        resultMap.put("thumbs_up", "(y)");
        resultMap.put("thumbs_down", "(n)");
        resultMap.put("information", "(i)");
        resultMap.put("check", "(/)");
        resultMap.put("error", "(x)");
        resultMap.put("add", "(+)");
        resultMap.put("forbidden", "(-)");
        resultMap.put("warning", "(!)");
        resultMap.put("help_16", "(?)");
        resultMap.put("lightbulb_on", "(on)");
        resultMap.put("lightbulb", "(off)");
        resultMap.put("star_yellow", "(*)");
        resultMap.put("star_blue", "(*b)");
        resultMap.put("star_green", "(*g)");
        resultMap.put("star_red", "(*r)");
        resultMap.put("flag", "(flag)");
        resultMap.put("flag_grey", "(flagoff)");
        return resultMap;
    }
}
