package useresponse.atlassian.plugins.jira.service.handler.request.api;

import useresponse.atlassian.plugins.jira.request.Request;

public abstract class AbstractRequestHandler implements RequestHandler {

    protected RequestHandler requestHandler;

    @Override
    public Request handle(Request request) {
        request = requestHandler.handle(request);
        return currentAddition(request);
    }

    protected abstract Request currentAddition(Request request);
}
