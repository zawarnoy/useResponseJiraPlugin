package useresponse.atlassian.plugins.jira.service.handler.request.api;

import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.handler.Handler;

public interface RequestHandler extends Handler<Request, Request> {
    Request handle(Request request);

}
