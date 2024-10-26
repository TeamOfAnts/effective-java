# 32. 제네릭과 가변인수를 함께 쓸땐 신중히!

### 가변인수란?

가변인수는 메서드의 매개변수를 동적으로 처리하도록 도와주는 문법이다.

```java
public void printArgs(String... args) { // 가변인수를 사용할때
    for (String arg : args) {
        System.out.println(arg);
    }
}

public void printArgs(List<String> args) {
    for (String arg : args) {
        System.out.println(arg);
    }
}

```

위의 두 메서드는 동일하게 동작한다. 그러나 메서드를 호출할때 코드 가독성에서 차이가 난다.

```java
// 가변인수 사용 시
printArgs("Hello", "World");

// List 사용 시
List<String> args = Arrays.asList("Hello", "World");
printArgs(args);

```

### 가변인수를 쓸때 주의해야할 점

![Pasted image 20241012194137.png](32%20%E1%84%8C%E1%85%A6%E1%84%82%E1%85%A6%E1%84%85%E1%85%B5%E1%86%A8%E1%84%80%E1%85%AA%20%E1%84%80%E1%85%A1%E1%84%87%E1%85%A7%E1%86%AB%E1%84%8B%E1%85%B5%E1%86%AB%E1%84%89%E1%85%AE%E1%84%85%E1%85%B3%E1%86%AF%20%E1%84%92%E1%85%A1%E1%86%B7%E1%84%81%E1%85%A6%20%E1%84%8A%E1%85%B3%E1%86%AF%E1%84%84%E1%85%A2%E1%86%AB%20%E1%84%89%E1%85%B5%E1%86%AB%E1%84%8C%E1%85%AE%E1%86%BC%E1%84%92%E1%85%B5!%2011d55986da10801e8a22ced35d20275f/Pasted_image_20241012194137.png)

가변인수 메서드를 호출하면 Java는 자동으로 가변인수를 배열로 감싸서 메서드 내부로 전달한다.

아래처럼 호출할때

```java
printItems("A", "B", "C");
```

위의 코드는 그러니까 이거랑 똑같은거다.

```java
printItems(new String[]{"A", "B", "C"});
```

기억해야할점은 가변인수는 들어오는 값을 배열로 만든다는 점이다.

### 제네릭을 가변인수로 이용했을때

이러한 가변인수를 제네릭과 함께 사용하면 어떨까?

```java
public static <T> T[] toArray(T... args) {
    return args;
}
```

당연히.. 위와 같은 예시는 해당 배열을 외부 메서드에 반환하기때문에도 문제지만
가변인수&제네릭 조합은 이것보다 더욱 크리티컬한 문제가 있다.

제네릭 타입을 가변인수로 사용한 메서드를 작성하면 아래와 같은 경고가 발생한다.

**Possible heap pollution from parameterized vararg type**

매개변수화 타입의 변수가 타입이 다른 객체를 참조하면 힙 오염이 발생한다.라는 건데
제네릭 타입 정보가 런타임에 소거되기 때문에 타입 안정성이 깨지는 상황을 고려해서 경고를 띄우는 것이다.

힙오염의 예시는 아래와 같다.

```java
static void dangerous(List<String>... stringLists) {
    List<Integer> intList = List.of(42);
    Object[] objects = stringLists;
    objects[0] = intList; // 힙 오염 발생
    String s = stringLists[0].get(0); // ClassCastException
}

public static void main(String[] args) {
    dangerous(List.of("dangerous"));
}
```

왜 형변환하는 곳이 보이지 않음에도 ClassCastException 오류가 날까?

마지막 줄에 실제로 컴파일러가 알수 없는 형변환이 있기 때문이다. → 런타임에서 잘못된것이다 라는 말이 정확함(중의적이니까 문장 수정) 어떻게 혼동??????????

![image.png](32%20%E1%84%8C%E1%85%A6%E1%84%82%E1%85%A6%E1%84%85%E1%85%B5%E1%86%A8%E1%84%80%E1%85%AA%20%E1%84%80%E1%85%A1%E1%84%87%E1%85%A7%E1%86%AB%E1%84%8B%E1%85%B5%E1%86%AB%E1%84%89%E1%85%AE%E1%84%85%E1%85%B3%E1%86%AF%20%E1%84%92%E1%85%A1%E1%86%B7%E1%84%81%E1%85%A6%20%E1%84%8A%E1%85%B3%E1%86%AF%E1%84%84%E1%85%A2%E1%86%AB%20%E1%84%89%E1%85%B5%E1%86%AB%E1%84%8C%E1%85%AE%E1%86%BC%E1%84%92%E1%85%B5!%2011d55986da10801e8a22ced35d20275f/image.png)

우리는 stringLists가 List<String>이기 때문에 직관적으로 stringLists에는 string만 들어가야한다는 걸 안다. 

하지만 가변 인수는 List<String>을(제네릭을) List[]로 만들기때문에 타입 소거가 돼 stringLists에는 Integer도 들어갈 수 있고 Double도 들어갈 수 있게 되는 셈이다.

### 대신 리스트나 컬렉션을 매개변수로 사용하자

그렇다면 List[]로 들어오지 못하도록 수정하면 되지 않을까?

```java
@SafeVarargs
static <T> List<T> flatten(List<? extends T>... lists) {
    List<T> result = new ArrayList<>();
    for (List<? extends T> list : lists)
        result.addAll(list);
    return result;
}

@Test
public void flatTest() {
    List<Integer> flatList = flatten(
            List.of(1, 2), List.of(3, 4, 5), List.of(6,7));
    System.out.println(flatList);
}

```

다음 두가지 조건을 만족하는 varargs를 사용하는 제네릭 메소드는 안전하다.

1. varargs 매개변수 배열에 아무것도 저장하지 않는다.
2. varargs 매개변수 배열(혹은 복제본)을 신뢰할 수 없는 코드에 노출하지 않는다.

→ 캡슐화를 잘 지키면 써도된다.

이러한 가변인수의 조건을 지켰다면, @SafeVarargs를 써라

@SafeVarargs는 재정의할 수 없는 메서드에만 달아야 한다. 상속할 때도 애노테이션이 이어지기 때문에 하위 타입의 구현이 정말로 안전한 가변인수인지는 알기 힘들다.

### @SafeVarargs를 안쓰고 싶다면?

사실 가변인수가 무조건 정답은 아니다.

```java
static <T> List<T> flatten(List<List<? extends T>> lists) {
        List<T> result = new ArrayList<>();
        for (List<? extends T> list : lists)
            result.addAll(list);
        return result;
    }

    public static void main(String[] args) {
        List<Integer> flatList = flatten(List.of(
                List.of(1, 2), List.of(3, 4, 5), List.of(6,7)));
        System.out.println(flatList);
    }
}

```

### 결론

- 가변인수와 제네릭은 같이 안쓰는게 좋다.
- 배열과 제네릭은 규칙이 서로 다르다.
- 제네릭과 가변인수는 타입 safe하지 않지만, 허용은 된다.
    - 단, 외부에 가변인수 배열을 노출하거나 내부적으로 다른 배열에 저장후 바꾸지 않도록 주의하자.
    - 안전히 보장 되었다면 @SafeVarargs 으로 안전함을 알리자!

---

[https://inpa.tistory.com/entry/JAVA-☕-제네릭-와일드-카드-extends-super-T-완벽-이해](https://inpa.tistory.com/entry/JAVA-%E2%98%95-%EC%A0%9C%EB%84%A4%EB%A6%AD-%EC%99%80%EC%9D%BC%EB%93%9C-%EC%B9%B4%EB%93%9C-extends-super-T-%EC%99%84%EB%B2%BD-%EC%9D%B4%ED%95%B4)

[https://sejoung.github.io/2019/01/2019-01-07-Combine_generics_and_varargs_judiciously/#아이템-32-제네릭과-가변인수를-함께-쓸-때는-신중하라](https://sejoung.github.io/2019/01/2019-01-07-Combine_generics_and_varargs_judiciously/#%EC%95%84%EC%9D%B4%ED%85%9C-32-%EC%A0%9C%EB%84%A4%EB%A6%AD%EA%B3%BC-%EA%B0%80%EB%B3%80%EC%9D%B8%EC%88%98%EB%A5%BC-%ED%95%A8%EA%BB%98-%EC%93%B8-%EB%95%8C%EB%8A%94-%EC%8B%A0%EC%A4%91%ED%95%98%EB%9D%BC)

[이펙티브 자바, 쉽게 정리하기 - item 32. 제네릭과 가변인수를 함께 쓸 때는 신중하라](https://jake-seo-dev.tistory.com/52)
