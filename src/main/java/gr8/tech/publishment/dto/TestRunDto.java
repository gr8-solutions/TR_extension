package gr8.tech.publishment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestRunDto {
    @JsonProperty("suite_id")
    String suiteId;
    String name;
    String description;
    @JsonProperty("case_ids")
    int[] caseIds;
    @JsonProperty("include_all")
    boolean includeAll;
}

