package org.example.item8;

public class Parent {
    Parent() {
        throw new RuntimeException("Parent 생성 불가");
    }

    @Override
    protected final void finalize() {
        System.out.println("Parent 객체 소멸");
    }
}
