

1. **메서드 몸체가 시작되기 전에 매개변수의 유효성 검사를 완료하자**
    - 그렇지 않으면 **모호한 예외**가 발생하거나, 잘못된 입력으로 인해 **예상치 못한 동작**을 할 수 있다.
    - 특히, 유효성 검사를 수행하지 않아 메서드가 잘못된 상태에서 성공할 경우, 실패 원자성(failure atomicity)을 어길 수 있다.

```java
// 유효성 검사를 통해 명확한 예외를 던지기
public static int divide(int numerator, int denominator) {
    if (denominator == 0) {
        throw new IllegalArgumentException("Denominator must not be zero.");
    }
    return numerator / denominator;
}
```

---

2. **`public`과 `protected` 메서드는 매개변수 값이 잘못됐을 때 던지는 예외를 문서화하자**
    - 메서드의 제약 조건을 Javadoc에 명확히 기재하고, 제약을 어겼을 때 발생하는 예외도 포함하라.

```java
// Javadoc에 예외를 문서화하기
/**
 * Divides one integer by another.
 *
 * @param numerator the number to be divided
 * @param denominator the number to divide by (must not be zero)
 * @return the result of the division
 * @throws IllegalArgumentException if the denominator is zero
 */
public static int divide(int numerator, int denominator) {
    if (denominator == 0) {
        throw new IllegalArgumentException("Denominator must not be zero.");
    }
    return numerator / denominator;
}

```

---

3. **`NullPointerException`에 대한 문서화**
    - 일반적으로 메서드 수준 주석보다는 **클래스 수준 주석**에서 `null` 처리 방침을 설명한다.
    - 특정 매개변수가 `null`이 될 수 있음을 명시하려면 `@Nullable` 주석을 활용하라.
    - 유효성 검사를 위해 자바 7에 추가된 `Objects.requireNonNull` 메서드를 사용하라.

```java
public void setName(String name) {
    this.name = Objects.requireNonNull(name, "Name must not be null");
}
```

---

4. **`private` 메서드의 경우, 호출 환경을 통제하자**
    - `private` 메서드는 호출되는 상황을 완전히 제어할 수 있다면, 유효성 검사가 생략될 수 있다.
    - 하지만 테스트나 디버깅 목적으로 `assert` 문을 사용해 매개변수 유효성을 검증할 수 있다.
    - 단, `assert`는 기본적으로 런타임에서 비활성화될 수 있으므로 필수적인 유효성 검사로는 적합하지 않다.

```java
// `assert`를 활용한 유효성 검사
private void sort(int[] array, int start, int end) {
    assert array != null : "Array must not be null";
    assert start >= 0 && start <= array.length : "Invalid start index";
    assert end >= 0 && end <= array.length : "Invalid end index";
    assert start <= end : "Start index must not be greater than end index";

    // Sorting logic...
}
```

---

5. **나중에 사용하기 위해 저장하는 매개변수도 유효성 검사를 진행하자**
    - 변수를 저장한 후 문제가 발생하면 **출처를 추적하기 어렵다.**
    - 초기 저장 시점에 반드시 검사를 수행하자

6. **유효성 검사가 지나치게 비싸다면 생략하라.**
    - 유효성 검사의 비용이 높거나, 검사로 얻는 이득이 없다면 생략할 수 있다.
    - 예를 들어, `sort` 메서드는 리스트의 객체들이 비교 가능(comparable)한지 검사하지 않아도 된다.
        - 비교할 수 없는 객체가 들어오면 결국 `ClassCastException`이 발생하기 때문이다.

---

### **결론**

1. 메서드나 생성자를 작성할 때 매개변수의 제약 조건을 반드시 고려하라.
2. 제약 조건과 위반 시 발생하는 예외를 **문서화**하라.
3. 유효성 검사는 가능한 **메서드 초입**에서 수행하라.
4. 비용이 지나치게 크거나 소득이 없는 유효성 검사는 생략할 수 있다.
