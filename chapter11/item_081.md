# 아이템 81. wait와 notify보다는 동시성 유틸리티를 애용하라

지금은 `wait`와 `notify`를 직접 쓸 일 거의 없다. 쓰지 마라. 
대신 `java.util.concurrent` 패키지에 있는 동시성 유틸리티를 사용하라.

**책에서 한 말: `wait`와 `notify`는 올바르게 사용하기가 아주 까다로우니 고수준 동시성 유틸리티를 사용하자.**

## 과거의 유산 예시: Collections.synchronizedMap

```java
public static void main(String[] args) {
    Map<String, String> synchronizedMap = Collections.synchronizedMap(new HashMap<>());

    synchronizedMap.put("key1", "value1");
    synchronizedMap.put("key2", "value2");

    // 동기화 블록 필요
    synchronized (synchronizedMap) {
        for (Map.Entry<String, String> entry : synchronizedMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
```

## 최신 1

<details>
<summary>`String.intern`을 직접 구현해보자</summary>

```java
public class StringInternExample {
    public static void main(String[] args) {
        String str1 = "hello";
        String str2 = new String("hello");

        System.out.println(str1 == str2); // false (다른 객체)

        String str3 = str2.intern(); // 문자열 풀에서 "hello"의 참조를 가져옴
        System.out.println(str1 == str3); // true (같은 참조)
    }
}
```
</details>

```java
private static final ConcurrentMap<String, String> map = new ConcurrentHashMap<>();

public static String intern(String s) {
    String result = map.putIfAbsent(s, s);
    return result == null ? s : result;
}
```

`ConcurentHashMap`은 `get`에 최적화되어있으므로, `get`을 먼저 호출하여 필요할 때만 `putIfAbsent`를 호출하면 더 빠르다.

## 최신 2

`ConcurrentHashMap`을 사용하면 `synchronized` 블록을 사용하지 않아도 된다.

```java
public static String intern(String s) {
    String result = map.get(s);
    if (result == null) {
        result = map.putIfAbsent(s, s);
        if (result == null) {
            result = s;
        }
    }
    return result;
}
```

동시성 컬렉션(ex. `ConcurrentHashMap`)은 동기화한 컬렉션(ex. `Collections.synchronizedMap`)을 낡은 유산으로 만들어버렸다.
동기화된 맵을 동시성 맵으로 교체하는 것만으로 동시성 애플리케이션의 성능은 극적으로 개선된다.

## 자바에서 제공해주는 동시성 유틸리티를 사용하자!

아래에 있는 예시를 이 자료에서 다루지는 않을 것. 이러한 프로그래밍을 할 경우는 많지 않으며 필요할 때 찾아서 사용하면 된다*(고 생각)*.

1. `CountDownLatch`:
   - 스레드가 특정 조건(카운트가 0)에 도달할 때까지 대기.
   - 대체: 여러 스레드의 작업 완료 대기(`notify` 대신 사용).
2. `Semaphore`:
   - 리소스 접근을 제어하는 동시성 유틸리티.
   - 대체: 제한된 리소스에 대한 스레드 간 동기화(`wait` 대신 사용).
3. `CyclicBarrier`:
   - 여러 스레드가 모두 도달했을 때 실행을 재개.
   - 대체: 스레드 간의 협력을 간단하게 구현.
4. `BlockingQueue`:
   - 스레드 안전한 생산자-소비자 패턴 구현.
   - 대체: 생산자-소비자 문제에서 `wait`와 `notify`를 사용한 복잡한 코드.
5. `ReentrantLock`, `Condition`:
   - `wait`와 `notify`의 고급 대체품.

## 그 외 내용

책에서는 `wait`와 `notify`를 사용하지 말라고 하고 있지만, 그래도 레거시 코드를 위해 무엇을 조심해야하는지 설명해두었다. 하지만 이 내용은 불필요해보이므로 정리하지 않았다.
