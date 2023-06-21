package gr8.tech.publishment;

import com.epam.reportportal.annotations.TestCaseId;
import gr8.tech.publishment.client.TestRailClient;
import gr8.tech.publishment.client.TestResultStatus;
import gr8.tech.publishment.dto.TestResultDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Log4j2
public class TestRailExtension extends SummaryGeneratingListener implements Extension, TestWatcher, BeforeAllCallback {
    private static final Lock LOCK = new ReentrantLock();
    protected static final Map<String, Object[]> testResults = new HashMap<>();
    private final TestRailClient testRailClient = new TestRailClient();
    private static boolean isTestRunCreated = false;

    @Override
    public void beforeAll(ExtensionContext context) {
        LOCK.lock();
        try {
            if (!isTestRunCreated && TestRailClient.isTestRailEnabled()) {
                isTestRunCreated = true;
                testRailClient.createTestRun();
            }
        } finally {
            LOCK.unlock();
        }
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        context.getElement().ifPresent(element -> {
            if ((element.isAnnotationPresent(TestCaseId.class) || element.isAnnotationPresent(TestRailId.class))
                    && TestRailClient.isTestRailEnabled()) {
                saveResult(context, TestResultStatus.PASSED, EMPTY);
            }
        });
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        context.getElement().ifPresent(element -> {
            if ((element.isAnnotationPresent(TestCaseId.class) || element.isAnnotationPresent(TestRailId.class))
                    && TestRailClient.isTestRailEnabled()) {
                saveResult(context, TestResultStatus.FAILED, cause.getMessage());
            }
        });
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        context.getElement().ifPresent(element -> {
            if ((element.isAnnotationPresent(TestCaseId.class) || element.isAnnotationPresent(TestRailId.class))
                    && TestRailClient.isTestRailEnabled()) {
                for (String caseId : getTestRailId(context).split(", ")) {
                    testResults.put(caseId, new Object[]{TestResultStatus.RETEST, reason.orElse("Unexpected reason")});
                }
            }
        });
    }

    private String getTestRailId(ExtensionContext context) {
        final String[] id = new String[1];
        context.getElement().ifPresent(element -> id[0] = element.isAnnotationPresent(TestCaseId.class)
                ? element.getAnnotation(TestCaseId.class).value()
                : element.getAnnotation(TestRailId.class).value());
        return id[0];
    }

    private int getInvocationNumber(ExtensionContext context) {
        int numOfElements = context.getUniqueId().split("/").length;
        String lastElement = context.getUniqueId().split("/")[numOfElements - 1];
        return Integer.parseInt(lastElement.substring(lastElement.indexOf("#") + 1, lastElement.length() - 1));
    }

    private void saveResult(ExtensionContext context, TestResultStatus status, String message) {
        context.getTestMethod().ifPresent(method -> {
            if (method.getParameterCount() == 0) {
                testResults.put(getTestRailId(context), new Object[]{status, message});
            } else {
                int invocationNumber = getInvocationNumber(context);
                testResults.put(getTestRailId(context).split(", ")[invocationNumber - 1], new Object[]{status, message});
            }
        });
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        super.testPlanExecutionFinished(testPlan);
        if (TestRailClient.isTestRailEnabled()) {
            testRailClient.addTestCaseToTestRail(testResults.keySet().stream().map(Integer::parseInt).toList());
            testRailClient.addResultsToTestCases(getTestResultDto());
        }
    }

    private TestResultDto getTestResultDto() {
        Set<TestResultDto.TestCases> tests = new HashSet<>();
        TestResultDto resultDto = new TestResultDto();
        for (Map.Entry<String, Object[]> entrySet : testResults.entrySet()) {
            tests.add(new TestResultDto.TestCases(Integer.parseInt(entrySet.getKey()),
                    ((TestResultStatus) entrySet.getValue()[0]).getCode(),
                    (String) entrySet.getValue()[1]));
        }
        resultDto.setResults(tests);
        return resultDto;
    }
}
