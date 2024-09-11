package org.example.item7;

import java.util.EmptyStackException;

public class Stack1 {

    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack1() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        return elements[--size];
    }

    public Object get(int i) {
        return elements[i];
    }

    /**
     * pop을 했지만 실제로 메모리에서 제거되지 않아서 메모리 누수가 발생한다.
     */
    public static void main(String[] args) {
        Stack1 stack1 = new Stack1();
        stack1.push("1");
        stack1.push("2");
        stack1.push("3");

        for (int i = 0; i < 3; i++) {
            System.out.println(stack1.pop());
        }

        for (int i = 0; i < 3; i++) {
            System.out.println(stack1.get(i).toString());
        }
    }
}
