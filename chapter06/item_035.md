# 아이템 35. ordinal 메서드 대신 인스턴스 필드를 사용하라

### `ordinal`?

```java
public enum Day {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

public static void main(String[] args) {
    System.out.println(Day.MONDAY.ordinal()); // 출력: 0
    System.out.println(Day.TUESDAY.ordinal()); // 출력: 1
}
```

각 `enum` 상수는 선언된 순서에 따라 고유한 숫자를 가지는데, 이를 `ordinal value`라고 한다. `ordinal()` 메서드를 사용하면 이 값을 얻을 수 있다.

## 왜 `ordinal`을 사용하지 말아야 할까?

1. 상수의 순서 변경 시 문제 발생 (추가 혹은 제거 시에도 문제 발생)

만약 enum 상수의 순서가 변경되면, ordinal() 값도 변경된다. 이는 프로그램의 예측 불가능한 동작을 초래할 수 있다.

```java
public enum Day {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
}
```

`SUNDAY`의 순서를 맨 뒤로 옮기면 `ordinal()` 값이 0에서 6으로 변경된다. (나머지도 마찬가지)

이러한 이유로 코드의 유지보수성이 저하된다.

## 그렇다면 어떻게 해야할까?

### 인스턴스 필드를 사용하기

각 enum 상수에 우리가 원하는 값을 직접 부여하면 된다.

```java
public enum Day {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7);

    private final int dayNumber;

    Day(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public int getDayNumber() {
        return dayNumber;
    }
}
```

이렇게 하면 순서 변경에 안전하고, 상수의 추가 및 제거에도 유연하다. 그리고 코드의 가독성도 향상된다.

## 정리

1. `ordinal` 사용하지 말자. (순서에 의존하므로 위험)
2. 인스턴스 필드를 사용하자.
3. 코드의 유지 보수성과 가독성을 높이자.
