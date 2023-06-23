# TestRail extension
This is a simple implementation of the TestRail integration into your test automation project.\
It works only on JUnit5
## Get started
1. Add to your test class/es, where tests you want to be integrated with TestRail, a JUnit annotation **@ExtendWith**, and pass to it parameter the **TestRailExtension.class**
2. Create a file `resources/META-INF/services/org.junit.platform.launcher.TestExecutionListener` and add in it a single line: `gr8.tech.publishment.TestRailExtension`
3. To match your tests with the test cases in the TestRail those tests must be annotated with the **@TestRailId** annotation or with the **@TestCaseId** annotation (from ReportPortal extension) with a single String parameter **value**. The value must match the case id from the TestRail. Example:\
   `@TestCaseId("37973446")`\
   `@Test`\
   `void someTest() {}`\
   If the test method is parametrized, then write all case ids in the **@TestCaseId** annotation by a comma in the same order as it will be invoked in the test method. Example:\
   `@TestCaseId("37973446", "37973447", "37973448")`
4. For configuring the connection to the TestRail, the project id, and the test run create in the resources folder the file `testrail.properties`

   | Parameter | Required | Description                                                                                                                 |
   |-----------| --- |-----------------------------------------------------------------------------------------------------------------------------|
   | tr.enable |optional| *true* - if you want to enable TestRail integration. By default it set to *false*                                           |
   | tr.url    |yes| base url to your TestRail. Example: *http://00.00.00.00/testrail*                                                           |                                                        |
   | tr.domain |yes| your organisation TestRail start page. Example: *https://pass.to.your.organisation/index.php?*                              |
   |tr.apikey|yes| authentification token to get access with API for your project. Example: *Basic kjhdad7Gj7HGHG698sdjhkGFDHvs7sdkhgj6hfhhY/* |
   |tr.project.id|yes| the id of your project in the TestRail                                                                                      |
   |tr.run.name|optional| the name of created test run. By default its name contains only the date and time of its creation                           |