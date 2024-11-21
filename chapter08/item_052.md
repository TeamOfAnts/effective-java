# 아이템 52. 다중정의는 신중히 사용하라

## 다중정의란?

오버로딩이다.

```java
public class OverloadingExample {

    public int add(int a, int b) {
        return a + b;
    }

    public int add(int a, int b, int c) {
        return a + b + c;
    }

    public static void main(String[] args) {
        OverloadingExample example = new OverloadingExample();

        System.out.println(example.add(1, 2));          // 출력: 3
        System.out.println(example.add(1, 2, 3));       // 출력: 6
    }
}
```

## 다중정의의 문제점

1. 다중정의된 메서드는 컴파일타임에 정해지므로, 호출자가 생각하는 메서드와 다른 메서드가 호출될 가능성이 있다.
    
```java
public class CollectionClassifier {
    public static String classify(Set<?> s) {
        return "집합";
    }

    public static String classify(List<?> l) {
        return "리스트";
    }

    public static String classify(Collection<?> c) {
        return "그 외";
    }

    public static void main(String[] args) {
        Collection<?>[] collections = {
            new HashSet<String>(),
            new ArrayList<String>(),
            new HashMap<String, String>().values()
        };

        for (Collection<?> c : collections) {
            System.out.println(classify(c)); // 예상과 다를 수 있음
        }
    }
}

// # 출력 결과
// 그 외
// 그 외
// 그 외
```

이걸 해결하려면 `classify()`를 하나로 합친 후 `intanceof`로 명시적으로 검사해야한다.

```java
public static String classify(Collection<?> c) {
    return c instanceof Set ? "집합" : c instanceof List ? "리스트" : "그 외";
}
```

---

## 어쨌든...

헷갈릴 수 있는 코드는 작성하지 않는 게 좋다. 다중정의가 혼동을 일으키는 상황을 피해야 한다.

- 안전하고 보수적으로 가려면 **매개변수 수가 같은** 다중정의는 만들지 말자.
- 다중정의하는 대신 메서드 이름을 다르게 지어주서 만들어라.


## 생성자

생성자는 이름을 다르게 지을 수 없으니, 두 번째 생성자부터는 무조건 다중정의가 된다.  
하지만 정적 팩터리라는 대안을 활용할 수 있는 경우가 많이 있다.

## 해결 방법

정말 어쩔 수 없이 매개변수 수가 같은 다중정의 메서드를 만들더라도, 매개변수 중 하나 이상을 **'근본적으로 다르게'** 만들자.  
근본적으로 다르다는 건, 두 타입이 `null`이 아닌 값으로 서로 어느 쪽으로든 형변환할 수 없다는 이야기다.

예를 들어, `int`와 `Collection`는 근본적으로 다르다.

## 정리

- 다중정의는 필요할 때도 있지만, 혼란을 초래할 가능성이 크다.
- 컴파일 타임에 호출될 메서드가 결정되므로 의도와 다른 결과를 낼 수 있다.
- 가능하면 다른 이름의 메서드를 사용하는 방식으로 문제를 피하고, 명확한 문서화를 통해 사용자가 의도를 정확히 이해할 수 있도록 해야 한다.

**일반적으로 매개변수가 같을 때는 다중 정의를 피하라**
