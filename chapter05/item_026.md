# 아이템 26. 로 타입은 사용하지 말라

## 로(raw) 타입이란?
로 타입은 제네릭(Generic) 타입에서 타입 매개변수를 전혀 사용하지 않은 때를 말합니다. 클래스와 인터페이스 선언에 타입 매개변수가 쓰인 경우를 제네릭 클래스, 제네릭 인터페이스라고 합니다. 예를 들어서 List<E>와 같이 말이지요. 앞서 말한 제네릭 타입은 제네릭 클래스와 제네릭 인터페이스를 일컫습니다.

## 로 타입을 사용하면 어떻게 될까?
제네릭을 지원하기 Java 1.5 이전에는 컬렉션을 아래와 같이 사용했습니다.

```java
private final Collection stamps = ...;
stamps.add(new Coin(...));
// unchecked call "경고"를 호출하지만 컴파일도 되고 실행도 됩니다.
```
위와 같은 코드가 컴파일 오류를 발생시키지 않으므로 실행중에 오류(Runtime Exception)가 발생할 수 있습니다. 예를 들어, add한 `Coin` 객체를 꺼내서 `Stamp` 변수에 할당하는 순간 `ClassCastException`이 발생합니다.

## 제네릭 지원 이후에는?
제네릭을 지원한 이후에는 아래와 같은 코드로 변경해서 사용합니다.

```java
private final Collection<Stamp> stamps = ...;
stamps.add(new Coin()); // 컴파일 오류 발생
```

컴파일 오류가 바로 발생합니다. 그렇다면 사용하지 않는 것을 권장하는데 왜 그대로 남겨두었을까요? 바로 제네릭이 등장하기 이전의 코드와의 호환성을 위해서 남겨졌습니다.

## 로 타입은 권장되지 않는다.
`List`와 같은 로 타입은 권장하지 않지만 `List<Object>`는 괜찮습니다. 모든 타입을 허용한다는 의사를 컴파일러에게 명확하게 전달한 것이기 때문입니다. 그렇다면 `List`와 `List<Object>`의 차이는 무엇일까요?
`List`는 제네릭 타입과 무관한 것이고, `List<Object>`는 모든 타입을 허용한다는 것입니다. 다시 말해서 매개변수로 `List`를 받는 메서드에 `List<String>`을 넘길 수 있지만, 제네릭의 하위 규칙 때문에 `List<Object>`를 받는 메서드에는 매개변수로 넘길 수 없습니다.
`List<String>`은 로 타입인 `List`의 하위 타입이지만 `List<Object>`의 하위 타입은 아니기 때문이지요. 그래서 `List<Object>`와 같은 매개변수화 타입을 사용할 때와 달리 `List`같은 로 타입을 사용하면 타입 안전성을 잃게 됩니다.

```java
// 컴파일 성공
public static void main(String[] args) {
    List<String> strings = new ArrayList<>();

    unsafeAdd(strings, Integer.valueOf(42));
    String s = strings.get(0);
}

// 로 타입
private static void unsafeAdd(List list, Object o) {
    list.add(o);
}
```

위의 코드는 컴파일은 성공하지만 로 타입인 `List`를 사용하여 `unchecked call to add(E) as a member of raw type List...` 라는 경고 메시지가 발생됩니다. 그런데 실행을 하게 되면 `strings.get(0)`의 결과를 형변환하려 할 때 `ClassCastException`이 발생합니다. `Integer`를 `String`으로 변환하려고 시도했으니까요.

```java
// 컴파일 실패 -> 더 안전하다!
public static void main(String[] args) {
List<String> strings = new ArrayList<>();

    unsafeAdd(strings, Integer.valueOf(42));
    String s = strings.get(0);
}

// List<Object>
private static void unsafeAdd(List<Object> list, Object o) {
    list.add(o);
}
```

컴파일 오류가 발생하며 `incompatible types: List<String> cannot be converted to List<Object>...` 라는 메시지가 출력됩니다. 실행 시점이 아닌 컴파일 시점에 오류를 확인할 수 있어 보다 안전합니다.

## Element의 타입을 모른채 쓰고 싶다면?
비한정적 와일드카드 타입을 사용하면 됩니다. 제네릭 타입인 `Set<E>`의 비한정적 와일드카드 타입은 `Set<?>` 입니다. 제네릭 타입을 쓰고 싶긴하지만 실제 타입 매개변수가 무엇인지 신경쓰고 싶지 않을 때 적절합니다.

그렇다면 `Set<?>`과 로 타입인 `Set`의 차이는 무엇일까요? 아래의 코드를 통해 조금 더 빠르게 비교해봅시다.

```java
public class TypeTest {
    private static void addToObjList(final List<Object> list, final Object o) {
        list.add(o);
    }

    private static void addToWildList(final List<?> list, final Object o) {
        // null 외에 허용되지 않는다
        list.add(o);
    }

    private static <T> void addToGenericList(final List<T> list, final T o) {
        list.add(o);
    }


    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        String s = "hello";

        // List<Object> 이므로 incompatible types 오류
        addToObjList(list, s);
        
        // 아무 타입이나 가능하지만, 하지만 메서드에서 오류
        addToWildList(list, s);
        
        // 가능
        addToGenericList(list, s);
    }
}

public class TypeTest2 {
    public static void main(String[] args) {
        List raw = new ArrayList<String>(); // 가능
        List<?> wildcard = new ArrayList<String>(); // 가능
        List<Object> generic = new ArrayList<String>(); // 컴파일 오류
        
        raw.add("Hello"); // 가능. 하지만 raw 타입 사용하지 말 것
        wildcard.add("Hello"); // 컴파일 오류
        wildcard.size(); // 가능. 왜냐하면 size()는 제네릭 타입에 의존성 없음.
        wildcard.clear(); // 가능. 왜냐하면 clear()는 제네릭 타입에 의존성 없음.
    }
}
```

간단하게 얘기하면 역시나 로타입은 안전하지 않습니다. 와일드카드 타입은 안전하고요. 로 타입 컬렉션에는 앞서 살펴본 예제처럼 아무 원소나 넣을 수 있어서 타입 불변식을 훼손하기 쉽습니다. 하지만 Collection<?>에는 null 외에는 어떤 원소도 넣을 수 없습니다. 제네릭 타입에 의존성이 보인다면 컴파일 오류가 발생합니다.

## 로 타입을 사용하는 예외

1. `List.class`, `String[].class`

2. `instanceof` 연산자 
    
    ```java
    if (o instanceof Set) {
        Set<?> s = (Set<?>) o;
    }
    ```

## 정리
   
정리하자면, 로 타입은 되도록 쓰지 않는 것이 좋으며 아직 남아 있는 이유는 하위 버전과의 호환때문입니다. (예외 제외)
