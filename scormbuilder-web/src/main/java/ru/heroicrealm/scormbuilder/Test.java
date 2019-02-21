package ru.heroicrealm.scormbuilder;

import java.util.UUID;

/**
 * Created by kuran on 20.01.2019.
 */
public class Test {
    public static void main(String[] args) {
      String str = "<div class=\"adssad\" sdaf=\"123\">";
      String s=str.replaceAll("\"","\\\\\"");
        System.out.println(s);


    }
}
