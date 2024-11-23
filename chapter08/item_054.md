# 아이템 54. null이 아닌, 빈 컬렉션이나 배열을 반환하라

## null을 반환하는 경우의 문제점

1. 호출자의 코드가 복잡해진다

```java
public List<Cheese> getCheeses() {
    if (cheeses.isEmpty()) {
        return null;
    }
    return new ArrayList<>(cheeses);
}

public void processCheeses() {
    List<Cheese> cheeses = shop.getCheeses();
    if (cheeses != null && !cheeses.isEmpty()) { // 이런 식의 예외 처리를 계속 해주어야함
        for (Cheese c : cheeses) {
            // ...
        }
    }
}
```

2. `NullPointerException`이 발생할 수 있다

## 개선된 코드

```java
public List<Cheese> getCheeses() {
    return cheeses.isEmpty() ? Collections.emptyList() : new ArrayList<>(cheeses);
}

public void processCheeses() {
    List<Cheese> cheeses = shop.getCheeses();
    for (Cheese c : cheeses) {
        // ...
    }
}
```

#### 이는 배열의 경우에도 마찬가지로 적용된다.

## 정리

**`null`이 아닌, 빈 배열이나 컬렉션을 반환하라.** `null`을 반환하는 API는 사용하기 어렵고 오류 처리 코드도 늘어난다. 그렇다고 성능이 좋은 것도 아니다. 
