package ru.greshnov.pro;

import java.lang.reflect.InvocationTargetException;

public class MainAPP {
    public static void main(String[] args) throws NoSuchMethodException {

        try {
            System.out.println(TestRunner.runTests(ForTest.class));
        } catch (BadTestClassError e) {
            System.out.println(STR."ERROR BadTestClassError: \{e.s}");
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
