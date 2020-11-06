package com.gabry.hadoop.yarn.poc;

import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class YarnClientAspectMoc {
    private ApplicationId privateId = null;
    @AfterReturning(pointcut="execution(ApplicationId com.gabry.hadoop.yarn.poc.YarnClientMoc.submitApplication(ApplicationSubmissionContext)) && args(appContext)",
    returning = "submittedAppId",argNames = "appContext")
    public void submitApplication(ApplicationSubmissionContext appContext,ApplicationId submittedAppId) throws Throwable {
        System.out.println("YarnClientAspectMoc[submitApplication]: app context "+appContext+", submittedAppId "+submittedAppId+" privateId "+privateId);
    }
    // 只有在submitApplication内部调用createAppId时拦截，获取createAppId的返回值
    @AfterReturning(pointcut="cflow(execution(ApplicationId com.gabry.hadoop.yarn.poc.YarnClientMoc.submitApplication(ApplicationSubmissionContext))) " +
            "&& !within(CfowAspect) && execution(ApplicationId com.gabry.hadoop.yarn.poc.YarnClientMoc.createAppId())",
            returning = "submittedAppId")
    public void createAppId(ApplicationId submittedAppId) throws Throwable {
        privateId = submittedAppId;
        System.out.println("YarnClientAspectMoc[createAppId]: created submittedAppId "+submittedAppId);
    }
}
