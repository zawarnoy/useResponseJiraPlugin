package useresponse.atlassian.plugins.jira.service.handler.servlet.binder;

import java.util.ArrayList;
import java.util.concurrent.Future;

public class AnswerData {
    public ArrayList<String> answers;
    public int errorCount;

    public AnswerData() {
        errorCount = 0;
        answers = new ArrayList<>();
    }
}
