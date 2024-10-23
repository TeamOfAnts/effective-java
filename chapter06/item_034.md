# 아이템 34. int 상수 대신 열거 타입을 사용하라

### 열거 타입 = `enum`

## 숫자 상수를 사용하던 시절

```java
public static final int MERCURY = 1;
public static final int VENUS = 2;
public static final int EARTH = 3;

public static double calculateGravity(int planet, double mass) {
    switch (planet) {
        case MERCURY:
            return mass * 3.7;
        case VENUS:
            return mass * 8.87;
        case EARTH:
            return mass * 9.81;
        default:
            throw new IllegalArgumentException("알 수 없는 행성입니다.");
    }
}
```

만약, 행성의 중력을 계산한다면 위와 같은 코드를 쓸 수 있다.

그러나 이 코드에는 문제가 있다.

1. **타입 안전성 부족:** int 타입(`int planet`)은 아무 숫자나 넣을 수 있으니, 잘못된 값이 들어와도 컴파일러가 잡아주지 못한다.
2. **가독성 문제:** 숫자 1, 2, 3이 어떤 행성을 의미하는지 코드만 봐서는 알기 어렵다.
3. **네임스페이스 오염:** 상수들이 전역 공간에 흩어져 있어서 관리하기 어렵다.

이러한 문제를 해결하기 위해 **열거 타입**을 사용할 수 있다. 열거 타입을 사용하면 정해진 값들만 가질 수 있는 새로운 타입을 만들 수 있다.

## 열거 타입 사용

```java
public enum Planet {
    MERCURY(3.7),
    VENUS(8.87),
    EARTH(9.81);

    private final double gravity;

    Planet(double gravity) {
        this.gravity = gravity;
    }

    public double getGravity() {
        return gravity;
    }
}

public static double calculateGravity(Planet planet, double mass) {
    return mass * planet.getGravity();
}
```

1. **타입 안전성:** `Planet` 타입만 받을 수 있으니, 잘못된 값이 들어오는 것을 컴파일 단계에서 막을 수 있다.
2. **가독성 향상:** 코드만 봐도 어떤 행성인지 알 수 있다.
3. **부가 기능 추가 가능:** 열거 타입은 클래스처럼 동작하니, 필드나 메서드를 추가할 수 있다.
4. **네임스페이스 관리:** 관련된 상수들이 한 곳에 모여 있으니 관리하기 편하다.

### 열거 타입은 밖에서 접근할 수 있는 생성자를 제공하지 않으므로 사실상 final 클래스이다. 따라서 클라이언트가 인스턴스를 직접 생성하거나 확장할 수 없으니 열거 타입 선언으로 만들어진 인스턴스들은 딱 하나씩만 존재함이 보장된다.
