package useresponse.atlassian.plugins.jira.service.handler.servlet.binder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import useresponse.atlassian.plugins.jira.service.handler.Handler;
import useresponse.atlassian.plugins.jira.set.linked.LinkedSet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class IssueBinderServletHandler implements Handler<String, String> {

    private AnswerData answerData;
    private IssueBinderResponseData responseData;

    public IssueBinderResponseData handle(LinkedSet<Future<String>> futureArrayList) {
        answerData = new AnswerData();
        responseData = new IssueBinderResponseData();
        return formMessageFromFutureList(futureArrayList);
    }

    private IssueBinderResponseData formMessageFromFutureList(LinkedSet<Future<String>> futureList) {
        int initialListSize = futureList.size();
        Iterator<Future<String>> iterator = futureList.iterator();
        return handleFutureList(futureList, initialListSize);
    }

    private IssueBinderResponseData formMessageFromAnswersList() {
        for (String answer : answerData.answers) {
            responseData.data = responseData.data + answer + "\n";
        }
        responseData.message = "All items was successfully synchronized with UseResponse";
        return responseData;
    }

    private String handleFuture(Future<String> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            answerData.errorCount++;
            return e.getMessage();
        }
    }

    private void handleException(Exception e) {
        responseData.data = "Emerged exception. Details: " + e.getMessage();
    }

    private IssueBinderResponseData handleFutureList(LinkedSet<Future<String>> futureList, int initialListSize) {
        try {
            while (answerData.answers.size() < initialListSize) {
                for (Future<String> future : futureList) {
                    if (future.isDone()) {
                        answerData.answers.add(handleFuture(future));
                        futureList.remove(future);
                    }
                }
                Thread.sleep(50);
            }
            return formMessageFromAnswersList();
        } catch (InterruptedException | NoSuchElementException e) {
            e.printStackTrace();
            formMessageFromAnswersList();
            handleException(e);
            return responseData;
        }
    }

    @Override
    public String handle(String response) {

        Gson gson = new Gson();
        String responseForUser = null;

        JsonObject data = gson.fromJson(response, JsonObject.class);

        JsonObject issueData = data.getAsJsonObject("issue");
        if (issueData != null) {
            // TODO
            handleIssueData(issueData);
        }

        JsonArray commentsData = data.getAsJsonArray("comments");
        if (commentsData != null) {
            // TODO
            handleCommentsData(commentsData);
        }

        return responseForUser;
    }

    private void handleIssueData(JsonObject issueData) {

    }

    private void handleCommentsData(JsonArray commentsData) {
        Iterator<JsonElement> iterator = commentsData.iterator();
        while (iterator.hasNext()) {
            handleOneCommentData(iterator.next().getAsJsonObject());
        }
    }

    private void handleOneCommentData(JsonObject commentData) {

    }
}
