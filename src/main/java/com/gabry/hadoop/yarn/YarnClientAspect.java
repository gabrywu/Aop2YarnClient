package com.gabry.hadoop.yarn;

import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

@Aspect
public class YarnClientAspect {
    /**
     * file path to store application information, including application id & tracking url
     */
    private final String PARA_NAME_ASPECTJ_FILE_PATH = "YARN_CLIENT_ASPECTJ_FILE_PATH";
    /**
     * flag to indicate whether print debug logs
     */
    private final String PARA_NAME_ASPECTJ_DEBUG = "PARA_NAME_ASPECTJ_DEBUG";

    /**
     * The current application report when application submitted successfully
     */
    private ApplicationReport currentApplicationReport = null;

    private String appInfoFilePath;
    private boolean debug;

    public YarnClientAspect() {
        appInfoFilePath = System.getenv(PARA_NAME_ASPECTJ_FILE_PATH);
        debug = Boolean.parseBoolean(System.getenv(PARA_NAME_ASPECTJ_DEBUG));
        if (appInfoFilePath != null) {
            new File(appInfoFilePath).delete();
        }
    }

    private void registerAppInfo(final ApplicationSubmissionContext appContext,
                                 final ApplicationId submittedAppId,
                                 final ApplicationReport currentApplicationReport) {
        if (appInfoFilePath != null) {
            try {
                Files.write(Paths.get(appInfoFilePath),
                        Collections.singletonList(submittedAppId + ";" + currentApplicationReport.getTrackingUrl()),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE,
                        StandardOpenOption.APPEND);
            } catch (IOException ioException) {
                System.out.println("YarnClientAspect[registerAppInfo]: can't output current application information, because "
                        + ioException.getMessage());
            }

        }
    }

    /**
     * Trigger submitApplication when invoking YarnClientImpl.submitApplication
     *
     * @param appContext     application context when invoking YarnClientImpl.submitApplication
     * @param submittedAppId the submitted application id returned by YarnClientImpl.submitApplication
     * @throws Throwable exceptions
     */
    @AfterReturning(pointcut = "execution(ApplicationId org.apache.hadoop.yarn.client.api.impl.YarnClientImpl.submitApplication(ApplicationSubmissionContext)) && args(appContext)",
            returning = "submittedAppId", argNames = "appContext")
    public void registerApplicationInfo(ApplicationSubmissionContext appContext, ApplicationId submittedAppId) throws Throwable {
        // do anything you want, you can output the application id & tracking URL to a local file, or System environments
        if (debug) {
            System.out.println("YarnClientAspect[submitApplication]: current application context " + appContext);
            System.out.println("YarnClientAspect[submitApplication]: submitted application id " + submittedAppId);
            System.out.println("YarnClientAspect[submitApplication]: current application report  " + currentApplicationReport);
        }
        registerAppInfo(appContext, submittedAppId, currentApplicationReport);
    }

    /**
     * Trigger getAppReport only when invoking getApplicationReport within submitApplication
     * This method will invoke many times, however, the last ApplicationReport instance assigned to currentApplicationReport
     *
     * @param appReport current application report when invoking getApplicationReport within submitApplication
     * @param appId     current application id, which is the parameter of getApplicationReport
     * @throws Throwable exceptions
     */
    @AfterReturning(pointcut = "cflow(execution(ApplicationId org.apache.hadoop.yarn.client.api.impl.YarnClientImpl.submitApplication(ApplicationSubmissionContext))) " +
            "&& !within(CfowAspect) && execution(ApplicationReport org.apache.hadoop.yarn.client.api.impl.YarnClientImpl.getApplicationReport(ApplicationId)) && args(appId)",
            returning = "appReport", argNames = "appId")
    public void registerApplicationReport(ApplicationReport appReport, ApplicationId appId) throws Throwable {
        currentApplicationReport = appReport;
    }
}
