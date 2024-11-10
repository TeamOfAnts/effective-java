# 아이템 43. 람다보다는 메서드 참조를 사용하라

요약) 람다보다 더 간결하게 만드는 방법: 메서드 참조

## 메서드 참조의 유형

자바에서는 메서드 참조를 몇 가지 유형으로 구분한다.

1. 정적 메서드 참조 - `ClassName::staticMethod`
2. 한정적(bound) 인스턴스 메서드 참조 - `instance::instanceMethod`
3. 비한정적(unbound) 인스턴스 메서드 참조 - `ClassName::instanceMethod`
4. 생성자 참조 - `ClassName::new`

## 예제 코드

### 예제 1. 정적 메서드 참조

이 예제에서는 `BinaryOperator<T>` 함수형 인터페이스가 사용된다.

```java
Map<String, Integer> map = new HashMap<>();
map.put("a", 2);
map.put("b", 3);

map.merge("a", 1, (count, increment) -> Integer.sum(count, increment));
map.merge("b", 1, Integer::sum);
```

### 예제 2. 한정적 인스턴스 메서드 참조

이 예제에서는 `BiFunction<T, U, R>` 함수형 인터페이스가 사용된다.

```java
BiFunction<String, String, Boolean> lambdaExpression = (s, prefix) -> str.startsWith(prefix);
BiFunction<String, String, Boolean> methodReference = str::startsWith;
```

### 예제 3: 비한정적 인스턴스 메서드 참조

이 예제에서는 `Function<T, R>` 함수형 인터페이스가 사용된다.

```java
Function<String, Integer> lambdaExpression = s -> s.length();
Function<String, Integer> methodReference = String::length;
```

### 예제 4: 생성자 참조

이 예제에서는 `Supplier<T>` 함수형 인터페이스가 사용된다.

```java
Supplier<List<String>> lambdaExpression = () -> new ArrayList<>();
Supplier<List<String>> methodReference = ArrayList::new;
```

# 결론

메서드 참조를 사용할 수 있을 땐 사용하자. 단, 오해의 소지가 있다면 람다를 쓰자.
