# 아이템 89. 인스턴스 수를 통제해야 한다면 `readResolve`보다는 `Enum`을 사용하라

## 직렬화의 문제점

자바에서 객체를 직렬화 한 후 역직렬화 하면 두 개 이상의 인스턴스가 생성된다.  
문제는 싱글톤 패턴을 구현한 클래스에서도 인스턴스의 고유성을 해친다는 것이다.

#### 즉, 클래스에 `implements Serializable`을 추가하는 순간 더 이상 싱글턴이 아니게 된다.

## BAD: `readResolve`를 이용한 해결책

- 싱글톤 패턴을 유지하기 위해, `readResolve` 메서드를 통해 기존의 인스턴스를 반환하도록 구현할 수 있다.

```java
import java.io.Serializable;

public class Singleton implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Singleton INSTANCE = new Singleton();

    private Singleton() {
        // 생성자
    }

    public static Singleton getInstance() {
        return INSTANCE;
    }

    // readResolve 메서드
    private Object readResolve() {
        return INSTANCE;
    }
}
```

그러나, 이는 `readResolve`를 구현해야하므로 이를 개발자가 잊어버릴 수도 있고, 귀찮다.  
그리고 `Reflection` API를 사용하여 `private constructor`를 호출하여 새로운 인스턴스를 생성할 수도 있다.
그 외에 도둑 클래스나, 바이트 스트림을 조작하여 새로운 인스턴스를 생성할 수도 있다.

## GOOD: `Enum`을 사용하라!

- `Enum`은 기본적으로 싱글톤을 보장!
- `Enum`은 컴파일러에 의해 자동으로 직렬화되며, `Reflection` 공격에 안전!
- 코드가 간결하고 명확하며, 추가적인 메서드 구현이 필요 없기 때문에 유지보수가 용이!

## 인스턴스 수를 통제해야 하는데, `Enum`을 사용하지 못한다면 그 때 `readResolve`와 `transient`를 사용

ex) 상속을 받아야 하는 경우 *(`Enum`은 상속이 불가능)*
