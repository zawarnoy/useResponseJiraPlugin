package useresponse.atlassian.plugins.jira.service.request;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServletService {
    public static String getJsonFromRequest(HttpServletRequest request) throws IOException {
        InputStreamReader reader = new InputStreamReader(request.getInputStream());

        BufferedReader br = new BufferedReader(reader);

        String bufer;
        StringBuilder data = new StringBuilder();

        while ((bufer = br.readLine()) != null) {
            data.append(bufer);
        }

        return data.toString();
    }
}
