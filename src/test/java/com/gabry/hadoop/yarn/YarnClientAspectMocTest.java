package com.gabry.hadoop.yarn;

import com.gabry.hadoop.yarn.poc.YarnClientMoc;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class YarnClientAspectMocTest {
    private final PrintStream standardOut = System.out;
    ByteArrayOutputStream stdoutStream = new ByteArrayOutputStream();
    @Before
    public void beforeEveryTest(){
        //stdoutStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(stdoutStream));
    }
    @After
    public void afterEveryTest() throws IOException {
        System.setOut(standardOut);
        stdoutStream.close();
    }
    @Test
    public void testMoc(){
        YarnClientMoc moc = new YarnClientMoc();
        try {

            ApplicationSubmissionContext appContext = ApplicationSubmissionContext.newInstance(
                    ApplicationId.newInstance(System.currentTimeMillis(),1236),"appName",
                    "queue", Priority.UNDEFINED,
                    null,false,
                    false,10,null,
                    "type");
            moc.createAppId();
            ApplicationId applicationId = moc.submitApplication(appContext);
            String stdoutContent = stdoutStream.toString();
            Assert.assertTrue("trigger YarnClientAspectMoc.submitApplication failed"
                    ,stdoutContent.contains("YarnClientAspectMoc[submitApplication]"));
            Assert.assertTrue("trigger YarnClientAspectMoc.createAppId failed",
                    stdoutContent.contains("YarnClientAspectMoc[createAppId]:"));
        } catch (YarnException | IOException e) {
            Assert.fail("test YarnClientAspectMoc failed: "+e.getMessage());
            e.printStackTrace();
        }
    }
}
