# 아이템 53. 가변인수는 신중히 사용하라

## 가변인수 메서드란?

전달할 인수의 개수를 유연하게 조정할 수 있는 메서드

```java
static int sum(int... args) {
    int sum = 0;
    for (int arg : args) {
        sum += arg;
    }
    return sum;
}
```

## 가변인수의 문제점

1. 가변인수 메서드는 성능 비용이 있다.
- 가변인수를 사용할 때, *내부적으로 배열이 생성된다*.
- 따라서 호출마다 배열 할당과 초기화 비용이 발생한다.
- 인수 개수에 따라 안전성을 보장하기 어렵다.

2. 잘못된 인수 개수로 인해 런타임 예외가 발생할 가능성이 있다.
- 예를 들어, 최소한 하나의 인수를 요구하는 경우가 있다면, 호출자가 실수로 아무것도 전달하지 않을 위험이 있다.

## 가변인수 잘못된 예

인수를 0개만 넣어 호출하면 (컴파일 타임이 아닌) 런타임에 예외가 발생한다.

```java
static int min(int... args) {
    int min = args[0];
    for (int i = 1; i < args.length; i++) {
        if (args[i] < min) {
            min = args[i];
        }
    }
    return min;
}
```

## 가변인수 올바른 사용법 1

인수가 *반드시* 1개 이상이어야한다면, 명시적으로 첫 번째 인수를 받고 나머지 인수를 가변인수로 받는다.

```java
static int min(int firstArg, int... remainingArgs) {
    int min = firstArg;
    for (int arg : remainingArgs) {
        if (arg < min) {
            min = arg;
        }
    }
    return min;
}
```

## 가변인수 올바른 사용법 2

성능이 중요한 상황에서는 가변인수를 사용하기 전에 오버로드된 메서드를 제공하여 성능을 개선할 수 있다.

```java
static int sum(int a, int b) {
    return a + b;
}

static int sum(int a, int b, int c) {
    return a + b + c;
}

static int sum(int a, int b, int c, int d) {
    return a + b + c + d;
}

static int sum(int... args) {
    int sum = 0;
    for (int arg : args) {
        sum += arg;
    }
    return sum;
}
```

## 정리

1. 인수 개수가 일정하지 않은 메서드를 정의해야한다면 가변인수를 사용하되, 에러가 날 수 있는 상황을 인지하고 있자.
2. 성능에 민감한 상황이라면 가변인수를 사용하기 전에 오버로드된 메서드를 고려해보자.
