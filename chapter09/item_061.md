# 아이템 61. 박싱된 기본 타입보다는 기본 타입을 사용하라

## 기본 타입과 박싱된 기본 타입의 주된 차이 3가지

1. 기본 타입은 값만 가지고 있으나, 박싱된 기본 타입은 값에 더해 식별성이(identity)란 속성을 가진다.
   - 박싱된 기본 타입의 두 인스턴스는 값이 같아도 서로 다르다고 식별될 수 있다는 뜻!
2. 기본 타입의 값은 언제나 유효하나, 박싱된 기본 타입은 유효하지 않은 값, 즉 `null`을 가질 수 있다.
3. 기본 타입이 박싱된 기본 타입보다 시간과 메모리 사용면에서 더 효율적이다.

## 실수할 수 있는 예

```java
Integer a = new Integer(1000);
Integer b = new Integer(1000);

System.out.println(a == b);        // false
System.out.println(a.equals(b));   // true
```

`==` 연산자는 참조 비교를 하기 때문에 `false`가 출력된다.
`equals` 메서드는 값 비교를 하기 때문에 `true`가 출력된다.

## 특별한 상황: `Integer` 캐싱

```java
Integer a = 100;    // Integer.valueOf(100)와 동일
Integer b = 100;    // Integer.valueOf(100)와 동일
System.out.println(a == b);    // true

Integer c = 1000;   // 범위를 벗어난 값
Integer d = 1000;
System.out.println(c == d);    // false
```

-128에서 127 사이의 정수값들은 캐싱되기 때문에 `==` 연산자로 비교해도 `true`가 출력된다.
이에 대한 내용은 앞서 다룬 적 있다. (책에는 나오지 않음)

## 기이하게 동작하는 프로그램

```java
public class Unbelievable {
    static Integer i;

    public static void main(String[] args) {
        if (i == 42) {
            System.out.println("믿을 수 없군!");
        }
    }
}
```

`믿을 수 없군!`이 출력될 것 같지만, `NullPointerException`이 발생한다.
기본 타입과 박싱된 기본 타입을 혼용한 연산에서는 박싱된 기본 타입의 박싱이 자동으로 풀리게 된다. 이 때, 박싱된 기본 타입이 `null`이면 연산에서 `NullPointerException`이 발생한다.

## 그럼 언제 박싱된 기본 타입을 사용해야 할까?

- 컬렉션의 원소, 키, 값으로 사용할 때(=매개변수화 타입이나 매개변수화 메서드를 사용할 때)
  - 매개변수화 타입은 제네릭이라고 생각하면 됨

## 정리

- 기본적으로는 기본 타입을 사용
- 컬렉션의 원소, 키, 값으로 써야할 때만 박싱된 기본 타입을 사용
- 두 타입을 섞어 쓸 때는 자동 박싱/언박싱이 발생!
- `==` 연산자로 박싱된 기본 타입을 비교하지 말 것
