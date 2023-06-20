package gr8.tech.publishment.config;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:testrail.properties"})
public interface IProjectConfig extends Config {
    @DefaultValue("false")
    @Key("tr.enable")
    boolean isTestRailEnabled();

    @Key("tr.url")
    String testRailUrl();

    @Key("tr.apikey")
    String apikey();

    @Key("tr.domain")
    String testRailDomain();

    @Key("tr.project.id")
    String projectId();

    @Key("tr.run.name")
    String testRunName();
}
