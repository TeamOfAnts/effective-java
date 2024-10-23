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
