package useresponse.atlassian.plugins.jira.service.handler;

public interface Handler<T, V> {
    V  handle(T t);
}
