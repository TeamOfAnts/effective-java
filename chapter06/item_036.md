# 아이템 36. 비트 필드 대신 EnumSet을 사용하라

## 비트 필드란 무엇일까?

비트 필드는 이진수의 각 비트를 사용하여 여러 상태나 옵션을 하나의 숫자로 표현하는 방법이다.

```java
public class Pizza {
    public static final int CHEESE      = 1 << 0; // 1
    public static final int PEPPERONI   = 1 << 1; // 2
    public static final int MUSHROOM    = 1 << 2; // 4
    public static final int ONION       = 1 << 3; // 8

    private int toppings;

    public void addToppings(int toppings) {
        this.toppings |= toppings;
    }

    public boolean hasTopping(int topping) {
        return (this.toppings & topping) != 0;
    }
}
```

여기서 각 피자 토핑은 고유한 비트 위치를 가지고 있다. 이렇게 하면 여러 토핑을 하나의 `int` 값으로 관리할 수 있다.

### 비트 필드의 문제점

1. 가독성이 떨어진다.

비트 연산을 사용하기 때문에 코드가 복잡하고 이해하기 어려워진다.

```java
pizza.addToppings(Pizza.CHEESE | Pizza.PEPPERONI);
```

이 코드(`Pizza.CHEESE | Pizza.PEPPERONI`)가 무엇을 의미하는지 직관적으로 바로 알기 어렵다.

2. 타입 안정성이 없다.

비트 필드는 정수형을 사용하므로, 잘못된 값이 들어와도 컴파일러가 잡아주지 않는다.

```java
pizza.addToppings(42);
```

여기서 `42`는 존재하지 않는 토핑이지만, 컴파일러는 오류를 내지 않는다.

3. 확장성이 부족하다.

새로운 토핑을 추가하려면 비트 위치를 할당해야 하고, `int`의 비트 수는 제한되어 있다. (최대 32개)

## EnumSet이란 무엇인가?

EnumSet은 자바에서 enum 타입을 위한 전용 집합 클래스이다. 이것은 비트 필드의 단점을 해결해줄 수 있다.

```java
public enum Topping {
    CHEESE,
    PEPPERONI,
    MUSHROOM,
    ONION
}

public class Pizza {
    private EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);

    public void addTopping(Topping topping) {
        toppings.add(topping);
    }

    public boolean hasTopping(Topping topping) {
        return toppings.contains(topping);
    }
}
```

### EnumSet의 장점

1. 가독성이 높다

```java
pizza.addTopping(Topping.CHEESE);
pizza.addTopping(Topping.PEPPERONI);
```

이렇게 하면 어떤 토핑을 추가하는지 명확하게 알 수 있다.

2. 타입 안정성

EnumSet은 enum 타입을 사용하므로, 잘못된 값이 들어오면 컴파일러가 잡아준다.

```java
pizza.addTopping(Topping.valueOf("PINEAPPLE")); // enum에 정의되어있지 않아서 컴파일 에러
```

3. 확장성

쉽게 새로운 enum 타입을 추가할 수 있다.

## 정리

1. 비트 필드는 가독성, 타입 안전성, 확장성에서 문제가 있다.
2. `EnumSet`은 이러한 문제를 해결하고 더 편리하게 사용할 수 있다.
3. 코드의 명확성과 유지 보수성을 높이기 위해 `EnumSet`을 사용하자.
