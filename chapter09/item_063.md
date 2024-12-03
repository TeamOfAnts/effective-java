# 아이템 63. 문자열 연결은 느리니 주의하라

문자열 연결은 `StringBuilder`를 사용하라.

## 예시

```java
public String statement() {
    String result = "";
    for (int i = 0; i < numItems(); i++)
        result += lineForItem(i);
    return result;
}
```

```java
public String statement() {
    StringBuilder b = new StringBuilder(numItems() * LINE_WIDTH);
    for (int i = 0; i < numItems(); i++)
        b.append(lineForItem(i));
    return b.toString();
}
```

`numItems`를 100, `lineForItem`을 80자로 테스트했을 시, 후자가 6.5배나 빨랐다.

## 정리

성능에 신경 써야한다면, `StringBuilder`의 `append`메서드를 사용하라.

## 추가내용 - by 홍성혁

때에 따라 `String`을 단순하게 `+` 연산을 하는 것이 좋을 수도 있다.
