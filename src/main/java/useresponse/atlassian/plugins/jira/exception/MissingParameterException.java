package useresponse.atlassian.plugins.jira.exception;

public class MissingParameterException extends Exception{
    public MissingParameterException(String parameter) {
        super("Required parameter " + parameter + "is missing.");
    }
}
