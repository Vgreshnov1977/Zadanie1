package ru.greshnov.pro;

public class ForTest {

    @MyAnnotations.Test (name = "first", range = 0)
    public void first (){
        System.out.println("first");
    }
    @MyAnnotations.Test (range = 7)
    public void second () throws TestAssertionError {
        System.out.println("second");
        throw new RuntimeException("Error of Assertion");
    }
    @MyAnnotations.Test (name = "third", range = 1)
    @MyAnnotations.Disabled
    public void third (){
        System.out.println("third");
    }
    @MyAnnotations.BeforeEach
    public void four (){
        System.out.println("four");
    }
    @MyAnnotations.AfterEach
    public void five (){
        System.out.println("five");
    }
    @MyAnnotations.BeforeSuite
    public static void six (){
        System.out.println("six");
    }
    @MyAnnotations.AfterSuite
    public static void seven (){
        System.out.println("seven");
    }
    @MyAnnotations.Test (range = 7)
    public void eight () throws TestAssertionError {
        System.out.println("eight");
        throw new TestAssertionError("Error of Assertion");
    }
}
