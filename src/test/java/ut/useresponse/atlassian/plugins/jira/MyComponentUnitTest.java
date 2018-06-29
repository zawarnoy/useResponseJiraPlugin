package ut.useresponse.atlassian.plugins.jira;

import org.junit.Test;
import useresponse.atlassian.plugins.jira.api.MyPluginComponent;
import useresponse.atlassian.plugins.jira.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}