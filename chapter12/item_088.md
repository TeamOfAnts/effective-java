# 아이템 88. readObject 메서드는 방어적으로 작성하라

## readObject 메서드란?

`readObject` 메서드는 자바의 직렬화(Serialization) 과정에서 역직렬화(Deserialization)를 커스터마이징 하기 위해 사용하는 특별한 메서드다.
`readObject` 메서드는 `Serializable` 인터페이스를 구현한 클래스 내에 필요에 따라 직접 작성한다.
`readObject` 메서드는 실질적으로 또 다른 `public` 생성자라고 할 수 있다. 따라서 생성자가 수행하는 조건들을 `readObject`에도 똑같이 수행해야 한다.

```java
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
    }
```

위의 코드는 가장 기본적인 `readObject` 메서드의 형태다. 즉, 위와 같이 코드를 작성할 경우, `readObject`를 구현하지 않은 것과 동일하다.

## readObject 메서드의 방어적 작성

1. 역직렬화된 데이터가 객체의 상태를 일관되게 유지하도록 필드를 검증

```java
import java.io.*;

public class Person implements Serializable {
    private String name;
    private int age;

    // 생성자
    public Person(String name, int age) {
        if (name == null) throw new IllegalArgumentException("name은 null일 수 없습니다.");
        if (age < 0) throw new IllegalArgumentException("age는 0 이상이어야 합니다.");
        this.name = name;
        this.age = age;
    }

    // readObject 메서드 방어적으로 작성
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();

        // 필드 검증
        if (name == null) {
            throw new InvalidObjectException("name 필드는 null일 수 없습니다.");
        }
        if (age < 0) {
            throw new InvalidObjectException("age 필드는 0 이상이어야 합니다.");
        }
    }
}

```

2. 객체의 불변성을 유지하기 위해 필요한 조건 확인 + 참조 제거

```java
private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();

    // 가변 요소들을 방어적으로 복사한다. (깊은 복사)
    start = new Date(start.getTime());
    end = new Date(end.getTime());

    // 불변식을 만족하는지 검사한다.
    if(start.compareTo(end) > 0) {
        throw new InvalidObjectException(start + "가 " + end + "보다 늦다.");
    }
}
```

## 실질적으로 readObject 메서드에서 참조를 통해 객체가 수정되는 경우는 거의 없다

이 장을 읽으면서 계속 드는 의문은 "직렬화, 역직렬화 과정에서 어차피 원본과의 참조가 깨지기 때문에 상관없지 않은가?"였다.

`readObject` 메서드에서 참조를 통해 버그가 발생할 확률은 낮다. 왜냐하면 바이트 코드를 직접 수정할 일은 거의 없기 때문이다.  
그래도 방어적 프로그래밍 원칙을 준수함으로써, 예상치 못한 보안 취약점과 데이터 무결성 문제를 사전에 방지하는 것에 의의가 있다.

## 추가 정보

`Date` 클래스는 자바 초기에 만들어진 클래스로, `java.time` 패키지의 `LocalDateTime` 클래스를 사용하는 것이 좋다.

### `Date` 클래스 사용의 좋지 않은 예시

```java
Date date = new Date();
date.setTime(System.currentTimeMillis()); // 객체의 내부 상태를 변경
```

### `LocalDateTime` 클래스 사용의 좋은 예시

```java
LocalDate date = LocalDate.now();
LocalDate newDate = date.plusDays(5); // 새로운 객체 반환
```
