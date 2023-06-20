package gr8.tech.publishment.client;

import lombok.Getter;

@Getter
public enum TestResultStatus {
    PASSED(1),
    BLOCKED(2),
    UNTESTED(3),
    RETEST(4),
    FAILED(5);

    private final Integer code;

    TestResultStatus(Integer code) {
        this.code = code;
    }
}

