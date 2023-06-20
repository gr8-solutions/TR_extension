package gr8.tech.publishment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class TestResultDto {
    Set<TestCases> results = new HashSet<>();
    @Data
    @AllArgsConstructor
    public static class TestCases {
        @JsonProperty("case_id")
        int caseId;
        @JsonProperty("status_id")
        int statusId;
        String comment;
    }
}
