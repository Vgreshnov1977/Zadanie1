package ru.greshnov.pro;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

public class TestRunner{

    static String path = "ru.greshnov.pro.MyAnnotations$";

    public static Map<TestResult, List<Test>> runTests(Class c) throws BadTestClassError, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Map<TestResult, List<Test>> mapTest = new HashMap<>();
        mapTest.put(TestResult.Skipped, new ArrayList<>());
        mapTest.put(TestResult.Error, new ArrayList<>());
        mapTest.put(TestResult.Failed, new ArrayList<>());
        mapTest.put(TestResult.Success, new ArrayList<>());


        getChecks(c);

        List<Method> lstMeth = getOrder(c);

        System.out.println(lstMeth);

        for (int i = 0; i < lstMeth.size(); i++) {
            try {
                lstMeth.get(i).invoke(c.newInstance());
                if (lstMeth.get(i).getAnnotation(MyAnnotations.Disabled.class) != null) {
                    mapTest.get(TestResult.Skipped).add(new Test(TestResult.Skipped,
                            getName(lstMeth.get(i)), null));
                } else if (lstMeth.get(i).getAnnotation(MyAnnotations.Test.class) != null) {
                    mapTest.get(TestResult.Success).add(new Test(TestResult.Success,
                            getName(lstMeth.get(i)), null));
                }
            } catch (Exception e) {
                if (e.getCause() instanceof TestAssertionError) {
                    mapTest.get(TestResult.Failed).add(new Test(TestResult.Failed,
                            lstMeth.get(i).getName(), e));
                } else {
                    mapTest.get(TestResult.Error).add(new Test(TestResult.Error,
                            lstMeth.get(i).getName(), e));
                }

            }
        }

        return  mapTest;
    }

    public static void getChecks (Class c) throws BadTestClassError {

        try {
            Object fff = c.newInstance();
        } catch (Exception e) {
            throw new BadTestClassError("Object don't create");
        }

        for (Method method : c.getDeclaredMethods()) {
            List<String> annotations = Stream.of(method.getDeclaredAnnotations())
                    .map(a -> a.annotationType().getName()).toList();
            if ((annotations.contains(path + "Test") || annotations.contains(path + "BeforeEach")
                    || annotations.contains(path + "AfterEach")) & Modifier.isStatic(method.getModifiers())
            ) {
                throw new BadTestClassError(
                        "method " + method.getName() + " - Test, BeforeEach, AfterEach don't use for static methods");
            }
            if ((annotations.contains(path + "BeforeSuite") || annotations.contains(path + "AfterSuite"))
                    & !Modifier.isStatic(method.getModifiers())
            ) {
                throw new BadTestClassError(
                        "method " + method.getName() + " BeforeSuite, AfterSuite don't use for object methods");
            }
            if (annotations.contains(path + "Disabled")) {
                if (!annotations.contains(path + "Test")) {
                    {
                        throw new BadTestClassError(
                                "method " + method.getName() + " Disabled, Test should be together");
                    }
                }
            }
            if (annotations.contains(path + "Order")) {
                if (!annotations.contains(path + "Test")) {
                    {
                        throw new BadTestClassError(
                                "method " + method.getName() + " Order, Test should be together");
                    }
                }
            }
            if (annotations.contains(path + "Test")) {
                int value = method.getAnnotation(MyAnnotations.Test.class).range();
                if (value < 0 || value > 10) {
                    throw new BadTestClassError(
                            "method " + method.getName() + " - Test out of Range");
                }
            }
            if (annotations.contains(path + "Order")) {
                int value = method.getAnnotation(MyAnnotations.Order.class).range();
                if (value < 1 || value > 10) {
                    throw new BadTestClassError(
                            "method " + method.getName() + " - Test out of Range");
                }
            }
        }
    }

    public static List<Method> getOrder (Class c) throws NoSuchMethodException {
        int i = 0;
        List<Method> lstBeforeSuite = new ArrayList<>();
        List<Method> lstAfterSuite = new ArrayList<>();
        List<Method> lstBeforeEach = new ArrayList<>();
        List<Method> lstAfterEach = new ArrayList<>();

        String[][] array = new String[c.getDeclaredMethods().length][3];
        for (Method method : c.getDeclaredMethods()) {
            if (method.getAnnotation(MyAnnotations.Test.class) != null) {
                array[i][0] = String.valueOf(method.getAnnotation(MyAnnotations.Test.class).range());
                if (method.getAnnotation(MyAnnotations.Test.class).name() != null
                        && !method.getAnnotation(MyAnnotations.Test.class).name().isBlank()) {
                    array[i][1] = String.valueOf(method.getAnnotation(MyAnnotations.Test.class).name());
                }
                else {
                    array[i][1] = method.getName();
                }
                array[i][2] = method.getName();
                i++;
            } else if (method.getAnnotation(MyAnnotations.BeforeSuite.class) != null) {
                lstBeforeSuite.add(method);
            } else if (method.getAnnotation(MyAnnotations.AfterSuite.class) != null) {
                lstAfterSuite.add(method);
            } else if (method.getAnnotation(MyAnnotations.BeforeEach.class) != null) {
                lstBeforeEach.add(method);
            } else if (method.getAnnotation(MyAnnotations.AfterEach.class) != null) {
                lstAfterEach.add(method);
            }
        }

        array = Arrays.copyOf(array,i);

        Arrays.sort(array, new Comparator<String[]>() {
            @Override
            public int compare(String[] o1, String[] o2) {
                int intComparison = Integer.compare(Integer.parseInt(o1[0]), Integer.parseInt(o2[0]));
                if (intComparison != 0) {
                    return intComparison;
                } else {
                    return o1[1].compareTo(o2[1]);
                }
            }
        });

        List<Method> lstMethFinal = new ArrayList<>(lstBeforeSuite);
        for (int j = 0; j < i; j++) {
            lstMethFinal.addAll(lstBeforeEach);
            lstMethFinal.add(c.getMethod(array[j][2]));
            lstMethFinal.addAll(lstAfterEach);
        }
        lstMethFinal.addAll(lstAfterSuite);

        return lstMethFinal;
    }

    public static String getName (Method method) {
        MyAnnotations.Test annotation = method.getAnnotation(MyAnnotations.Test.class);
        if (annotation.name() == null) {
            return method.getName();
        } else {
            return annotation.name();
        }
    }
}
