# Item8. finalizer와 cleaner 사용을 피하라

## finalizer

finalize 메서드는 최상위 Object 클래스에 포함된 메서드이다. 그래서 어느 클래스던지 finalize 메서드를 재정의 할 수 있다. finalize 메서드를 재정의(Overriding)하면 해당 객체가 가비지 컬렉션 대상이 되었을 때 finalize메서드가 호출된다. 단, 즉시 호출을 보장받을 수는 없다. 즉시 호출이 보장되지 않기 때문에 한정적 자원 해제시 해제하는 작업을 finalizer로 구현하면 안된다. 이런 경우 try-with-resource 또는 try-finally를 이용해 구현해야 한다. finalize 메서드는 자바9부터 deprecated 되었고 이에 대한 대안으로 자바에서는 cleaner를 지원하게 되었다.

### finalize가 deprecated 된 이유

finalize 메서드는 즉시 호출이 보장되지 않기 때문에 즉시 호출이 필요한 자원 해제에는 적합하지 않다. 또한 finalize 메서드는 예외를 던지면서 종료될 수 있기 때문에 finalize 메서드 내부에서 예외를 처리해야 한다. 이로 인해 finalize 메서드를 사용하면 코드가 복잡해지고 예외 처리가 어려워진다. 또한 finalize 메서드는 성능 저하를 일으킬 수 있다. finalize 메서드는 객체가 가비지 컬렉션 대상이 되었을 때 호출되는데 이 때 객체가 가비지 컬렉션 대상이 되었을 때 finalize 메서드를 호출하는데 시간이 걸릴 수 있다. 이로 인해 finalize 메서드를 사용하면 성능이 저하될 수 있다.
또한 GC 전에 호출되기 때문에 GC의 성능 저하를 일으키고, 언제 호출되는지 또한 알 수 없다.

<br>

## Cleaner

자바 9부터 finalizer 대신 새로운 소멸자인 java.lang.ref 패키지에 포함된 cleaner를 대안으로 제공한다.
cleaner는 API로 제공했던 finalizer처럼 재정의(Overriding)하는 것과 달리 구성을 통해 cleaner를 사용해야 한다.
하지만, cleaner 역시 예측할 수 없고, 느리고, 일반적으로 불필요하다.

<br>

## 마치며

쓰지 말자. (정도만 가져가도 될 듯)

<br>

## 그럼에도 불구하고 item8 코드를 한 번 보자

```java
public class Parent {
    Parent() {
        throw new RuntimeException("Parent 생성 불가");
    }
}
```
Parent 클래스는 생성자에서 예외를 던지기 때문에 Parent 객체를 생성할 수 없다.

```java
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
```

finalize를 사용하지 말라는 이유는 바로 이러한 부분 때문이다. 실제 의도와 다른 사이드 이펙트를 발생시킬 수 있다.

## 그럼 어떻게 이를 해결할 수 있을까?

```java
public class Parent {
    Parent() {
        throw new RuntimeException("Parent 생성 불가");
    }

    @Override
    protected final void finalize() {
        System.out.println("Parent 객체 소멸");
    }
}
```

부모 클래스에서 finalize를 `final`로 만들어 상속하지 못하게 하면 된다.
