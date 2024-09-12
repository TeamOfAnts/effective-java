package org.example.item8;

public class Child extends Parent {
    static Child child;

    Child() {
        super(); // Parent의 생성자 호출 -> 이때 예외 발생
    }

    @Override
    protected void finalize() throws Throwable {
        child = this;
        System.out.println("finalize() 메서드를 통해 Child 객체 복구");
    }

    public static void main(String[] args) {
        try {
            Child child = new Child(); // Child 객체 생성 시, Parent의 생성자 호출 -> 이때 예외 발생
            System.out.println("1. Child: " + child); // 위에서 예외 발생하여 실행되지 않음
        } catch (Exception e) {
            System.out.println("예외 발생: " + e.getMessage());
        }

        System.out.println("2. Child: " + Child.child); // 생성되지 않았으므로 null 출력

        System.gc();

        try {
            Thread.sleep(1000); // 잠시 GC 완료까지 대기하여 finalize() 실행을 기다림
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("GC 후 Child 객체 상태: " + Child.child); // finalize() 메서드를 통해 Child 객체 생성
    }
}
