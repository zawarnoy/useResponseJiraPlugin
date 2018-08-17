package useresponse.atlassian.plugins.jira.service.handler;

import org.json.simple.parser.ParseException;

import java.io.IOException;

public interface Handler<T, V> {
    V  handle(T t) throws IOException, ParseException;
}
