package ru.greshnov.pro;

public class Test {
    public Test(TestResult testResult, String nameTest, Throwable exception) {
        this.testResult = testResult;
        this.nameTest = nameTest;
        this.exception = exception;
    }

    private TestResult testResult;
    private String nameTest;
    private Throwable exception;
}
