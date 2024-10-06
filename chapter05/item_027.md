# 아이템 27. 비검사 경고를 제거하라

## 코드로 이해하는 비검사 경고

간단한 내용이므로 예시 위주로 설명하겠습니다.

```java
public class NonCheckedWarningExample {
    public static void main(String[] args) {
        List rawList = new ArrayList(); // Raw use of parameterized class 'ArrayList' 경고 발생
        rawList.add("Hello");
        rawList.add(10); // 에러가 나지 않고, 컴파일은 되지만 실행 시 ClassCastException 발생
    }
}
```

```java
public class NonCheckedMethodCallExample {

    public static void main(String[] args) {
        List<String> stringList = new ArrayList<>();
        stringList.add("Hello");

        addToList(stringList);
    }

    private static void addToList(List list) { // Raw use of parameterized class 'List' 경고 발생
        list.add(10);
    }
}
```

## 경고를 제거하는 방법

`@SuppressWarnings("unchecked")`를 사용하면 경고를 없앨 수 있지만, 이 방법을 사용하기 전에 리팩토링을 통해 경고를 없애는 방법을 찾아보는 것이 좋습니다.

### 그래도 예상컨데, 아마 유일하게 `@SuppressWarnings("unchecked")`를 사용할 만한 부분

```java
// Unchecked cast: 'java.lang.Object' to 'java.util.List<java.lang.String>' 경고 발생
List<String> list = (List<String>) Class.forName("com.example.MyClass").getMethod("getList").invoke(instance);
```

리플렉션 같은 경우 컴파일러가 타입을 검사할 수 없기 때문에, 이런 경우에는 사용해야할 수도 있습니다.
