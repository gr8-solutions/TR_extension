package gr8.tech.publishment.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.aeonbits.owner.ConfigFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectConfig {
    public static IProjectConfig config = ConfigFactory.create(IProjectConfig.class);
}
