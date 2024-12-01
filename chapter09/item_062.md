# 아이템 62. 다른 타입이 적절하다면 문자열 사용을 피하라

## 들어가기에 앞서

입력받을 데이터가 진짜 문자열일 때만 문자열을 사용하자.  
받은 데이터가 수치형이란 `int`, `float`, `BigInteger` 등을 사용하자.  
'예/아니오' 질문의 답이라면 적절한 `enum`이나 `boolean`을 사용하자.

## 문자열을 잘못 사용하는 사례들

1. 문자열로 데이터 타입을 대체하는 경우

```java
// 나쁜 예
String compoundKey = className + "#" + i.next();
```

두 요소를 구분해주는 문자 `#`이 쓰이고 있다.

이런 방식보다는 전용 클래스를 만드는 것이 더 낫다.

```java
// 좋은 예
class CompoundKey {
    private final String className;
    private final int i;
    
    public CompoundKey(String className, int i) {
        this.className = className;
        this.i = i;
    }
    // ...
}
```

2. 열거 타입을 문자열로 표현하는 경우

```java
// 나쁜 예
public static final String APPLE = "apple";
public static final String ORANGE = "orange";
```

```java
// 좋은 예
public enum Fruit {
    APPLE, ORANGE
}
```

3. 권한을 문자열로 표현하는 경우

```java
// 나쁜 예
void doSomething(String permission) {
    if (permission.equals("admin")) {
        // ...
    }
}
```

```java
// 좋은 예
public class Permission {
    public static final Permission ADMIN = new Permission("admin");
    private final String name;
    
    private Permission(String name) {
        this.name = name;
    }
}
```

## 정리

- 문자열을 막 쓰지 말고, 적절한 타입과 전용 클래스를 사용하자. 

#### 추가 의견: 문자열보다 적절한 타입이나 클래스를 사용해야 IDE의 자동완성 기능을 활용 가능!
