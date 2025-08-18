package ru.greshnov.pro;

public class BadTestClassError extends Throwable {
    String s;

    public BadTestClassError(String s) {
        this.s = s;
    }
}
