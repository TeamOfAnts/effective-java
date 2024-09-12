package org.example.item7;

import java.util.EmptyStackException;

public class Stack2 {

    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack2() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }

        Object result = elements[--size];
        elements[size] = null;

        return result;
    }

    public Object get(int i) {
        return elements[i];
    }

    /**
     * 정상적으로 메모리에서 제거된다.
     */
    public static void main(String[] args) {
        Stack2 stack = new Stack2();
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
