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

## 추가 내용 - by 홍성혁

때에 따라 `String`을 단순하게 `+` 연산을 하는 것이 좋을 수도 있다.

## 추가 정보

### `StringBuffer`와 `StringBuilder`의 차이점

- `StringBuffer`와 `StringBuilder`는 둘 다 문자열을 수정할 수 있는 클래스이다. 하지만 이 두 클래스에는 중요한 차이가 있다.

  - `StringBuffer`는 스레드 안전(Thread-safe)하다. 여러 스레드가 동시에 접근할 수 있도록 설계되어 있어 멀티스레딩 환경에서 안전하게 사용할 수 있다. 하지만 그로 인해 성능은 다소 떨어질 수 있다.
  - `StringBuilder`는 스레드 안전하지 않다. 즉, 멀티스레딩 환경에서는 여러 스레드가 동시에 접근하면 문제가 생길 수 있다. 그러나 일반적인 상황에서는 성능이 더 우수하다.

**결론**: 멀티스레드 환경이 아니고 성능이 중요한 경우에는 `StringBuilder`를 사용하는 것이 좋다!
