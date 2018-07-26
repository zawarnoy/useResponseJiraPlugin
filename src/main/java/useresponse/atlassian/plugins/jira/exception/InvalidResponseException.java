package useresponse.atlassian.plugins.jira.exception;

public class InvalidResponseException extends Exception {
    public InvalidResponseException(String message) {
        super(message);
    }
}
