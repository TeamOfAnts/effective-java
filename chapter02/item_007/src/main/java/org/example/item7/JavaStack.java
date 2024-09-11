package org.example.item7;

import java.util.Stack;

public class JavaStack {

    /**
     * 당연히 자바에서 제공하는 Stack은 메모리 누수가 발생하지 않는다.
     */
    public static void main(String[] args) {
        Stack<String> stack = new Stack<>();
        stack.push("1");
        stack.push("2");
        stack.push("3");

        for (int i = 0; i < 3; i++) {
            System.out.println(stack.pop());
        }

        for (int i = 0; i < 3; i++) {
            System.out.println(stack.get(i).toString());
        }
    }
}
