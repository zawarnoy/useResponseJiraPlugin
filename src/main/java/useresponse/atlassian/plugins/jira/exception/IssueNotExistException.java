package useresponse.atlassian.plugins.jira.exception;

public class IssueNotExistException extends Exception{

    public IssueNotExistException(String message) {
        super(message);
    }
}
