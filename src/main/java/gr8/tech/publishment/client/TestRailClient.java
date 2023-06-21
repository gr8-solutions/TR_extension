package gr8.tech.publishment.client;

import gr8.tech.publishment.dto.TestResultDto;
import gr8.tech.publishment.dto.TestRunDto;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static gr8.tech.publishment.config.ProjectConfig.config;
import static io.restassured.RestAssured.given;
import static java.lang.String.format;

@Log4j2
public class TestRailClient {
    private static final String TEST_RUN_LINK = config.testRailDomain() + "/runs/view/%s";
    private static String suitId;

    @SneakyThrows
    public void createTestRun() {
        log.info("Create test run");
        Response response = setUpRequestWithHeaderAuthorization().body(getTestRunDto())
                .post("/index.php?/api/v2/add_run/" + config.projectId())
                .then()
                .statusCode(200)
                .extract()
                .response();
        suitId = response.jsonPath().getString("id");
    }

    public void addTestCaseToTestRail(List<Integer> caseIds) {
        log.info("Add Test Case To Test Rail: " + caseIds);
        String body = "{\"case_ids\":" + caseIds + ",\"include_all\":false}";
        setUpRequestWithHeaderAuthorization().body(body)
                .post(format("/index.php?/api/v2/update_run/%s", suitId))
                .then()
                .statusCode(200);
    }

    public int addTestResultToCase(int caseId, TestResultStatus status) {
        log.info(format("Add Test Case %s with result %s", caseId, status.name()));
        return setUpRequestWithHeaderAuthorization().body(format("{\"status_id\":%s}", status.getCode()))
                .post(format("/index.php?/api/v2/add_result_for_case/%s/%s", suitId, caseId))
                .then()
                .statusCode(200)
                .extract().jsonPath().get("id");
    }

    public List<Integer> getTestCasesFromRun() {
        log.info("Get test cases from run");
        return setUpRequestWithHeaderAuthorization().param("/api/v2/get_tests/" + suitId)
                .contentType(ContentType.JSON)
                .get("/index.php")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("case_id");
    }

    public void addResultsToTestCases(TestResultDto resultDto) {
        log.info("Adding the test results to the TestRail test cases");
        setUpRequestWithHeaderAuthorization()
                .body(resultDto)
                .post(format("index.php?/api/v2/add_results_for_cases/%s", suitId))
                .then()
                .statusCode(200);
    }

    public static String getTestRailLink() {
        return String.format(TEST_RUN_LINK, suitId);
    }

    public static boolean isTestRailEnabled() {
        return config.isTestRailEnabled();
    }

    private RequestSpecification setUpRequestWithHeaderAuthorization() {
        return given().spec(new RequestSpecBuilder()
                .setBaseUri(config.testRailUrl())
                .setUrlEncodingEnabled(false)
                .setContentType(ContentType.JSON)
                .build()).header("Authorization", config.apikey());
    }

    private static TestRunDto getTestRunDto() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMMM yyyy, HH:mm");
        Date date = new Date();
        return TestRunDto.builder()
                .suiteId("25088")
                .name(format("%s - %s", config.testRunName(), formatter.format(date)))
                .includeAll(false)
                .caseIds(new int[0])
                .build();
    }
}
