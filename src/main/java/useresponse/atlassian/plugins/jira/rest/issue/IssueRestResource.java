package useresponse.atlassian.plugins.jira.rest.issue;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class IssueRestResource {

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getMessage()
    {



       return Response.ok(new IssueRestResourceModel()).build();
    }
}