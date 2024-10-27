# 33. 타입 안전 이종 컨테이너를 고려하라

### 기존의 컨테이너

컨테이너 = 데이터를 저장하고 관리하는 구조
우리가 아는 리스트, 셋, 맵등을 얘기한다.

알다시피 이러한 컨테이너들은 제네릭 클래스이다.

![image.png](33%20%E1%84%90%E1%85%A1%E1%84%8B%E1%85%B5%E1%86%B8%20%E1%84%8B%E1%85%A1%E1%86%AB%E1%84%8C%E1%85%A5%E1%86%AB%20%E1%84%8B%E1%85%B5%E1%84%8C%E1%85%A9%E1%86%BC%20%E1%84%8F%E1%85%A5%E1%86%AB%E1%84%90%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%82%E1%85%A5%E1%84%85%E1%85%B3%E1%86%AF%20%E1%84%80%E1%85%A9%E1%84%85%E1%85%A7%E1%84%92%E1%85%A1%E1%84%85%E1%85%A1%2012155986da1080958763d4a355007935/image.png)

(그리고 보통은 아래와같이 쓴다.)

```java
List<String> stringList = new ArrayList<>();
stringList.add("Hello"); // String 타입만 추가 가능
stringList.add(123);     // 컴파일 에러: Integer는 추가 불가능
```

이러한 컨테이너는 단일 타입의 객체만 처리할 수 있기 때문에, 서로 다른 타입의 객체를 함께 담으려고하면 문제가 발생한다.

아래와 같이 Object로 List를 만들면 이종 컨테이너처럼 사용가능하지만..
제네릭 컨테이너는 하나의 타입 파라미터만 사용하기 때문에 이종 타입을 함께 저장하면 타입 안정성이 깨진다는 문제에서 자유로울 수 없다.

```java
List<Object> objectList = new ArrayList<>();
objectList.add("Hello");  // String 추가
objectList.add(123);      // Integer 추가

// 데이터를 꺼낼 때 명시적 타입 캐스팅 필요
String s = (String) objectList.get(0); // 타입 캐스팅: 정상 동작
Integer i = (Integer) objectList.get(1); // 타입 캐스팅: 정상 동작

// 잘못된 캐스팅으로 런타임 오류 발생 가능
Integer wrong = (Integer) objectList.get(0); // ClassCastException 발생
```

### 클래스 리터럴

제네릭은 컴파일 타임에 타입 정보를 유지하지만 런타임에서 타입 소거가 된다.

그래서 이종 컨테이너에 적합하지 않으니 꺼내려는 객체의 타입정보가 뭔지 직접 알 수 있는 방법을 쓰면 된다.

이럴때 추천되는 게 클래스 리터럴이다. 클래스 리터럴은 자바 리플렉션 API에서 사용하는 메타 데이터 객체다. 클래스의 타입정보를 런타임에 참조할 수 있게 해준다.

여튼, 이런식으로 런타임에 특정 객체가 어떤 타입인지 알 수 있는걸 **타입토큰**이라고 한다. (클래스 리터럴보다 더 넓은 개념)

```java
Class<String> stringClass = String.class;
Class<Integer> intClass = Integer.class;
```

### 타입이 안전한 이종 컨테이너

이전에 설명한 클래스 리터럴을 키로 사용하면 객체를 저장하고 꺼낼때 **타입 안정성**을 유지하면서 다양한 타입을 처리할 수 있다.

```java
public class Favorites {
    private Map<Class<?>, Object> favorites = new HashMap<>();

    // 객체를 저장할 때, 클래스 리터럴을 함께 사용하여 타입을 안전하게 구분
    public <T> void putFavorite(Class<T> type, T instance) {
        favorites.put(type, instance);
    }

    // 객체를 꺼낼 때, 클래스 리터럴을 사용해 타입 캐스팅을 안전하게 수행
    public <T> T getFavorite(Class<T> type) {
        return type.cast(favorites.get(type));  // 타입 안전한 캐스팅
    }

    public static void main(String[] args) {
        Favorites f = new Favorites();
        f.putFavorite(String.class, "Hello");
        f.putFavorite(Integer.class, 123);

        // 타입 안전하게 객체를 꺼낼 수 있음
        String favoriteString = f.getFavorite(String.class);  // Hello
        Integer favoriteInteger = f.getFavorite(Integer.class);  // 123

        System.out.println(favoriteString);  // "Hello"
        System.out.println(favoriteInteger);  // 123
    }
}

```

이러한 타입이 안전한 이종 컨테이너도 문제가 있는데,

**클라이언트가 로타입을 사용하면 안정성이 쉽게 깨짐**

사실.. 이종 컨테이너를 사용한다면 로타입을 넘기면 안된다는 걸 안다.

그러나 이종 컨테이너는 뭐든 받아주는 것이 전제이기 때문에 이러한 로타입을 넘겨주는 것조차 막을 수 없다.

```java
// 로타입 사용으로 타입 불일치 문제
Class rawType = String.class;
f.putFavorite(rawType, 123);  // Integer를 String 타입으로 잘못 저장
String wrong = f.getFavorite(rawType);  // 런타임 오류 발생 가능
```

**제네릭(실체화 불가하니까..)에는 사용할 수 없다.**

제네릭은 타입 소거 메커니즘을 이용하기 때문에 런타임에 타입 정보를 유지 하지 않는다.

그렇기에 T.class를 사용하려고 할때 컴파일 오류가 발생한다. 

T는 타임 소거로 인해 런타임에 구체적인 타입 정보를 못주니까 Class<T>를 사용할 수 없기 때문에 타입 안전 이종 컨테이너의 클래스 리터럴을 사용할 수 없는 셈이다.

```java
public class Container<T> {
    private Favorites favorites = new Favorites();

    // 제네릭 타입 매개변수 T에 대해서는 Class<T> 사용 불가능
    public void put(T item) {
        favorites.putFavorite(T.class, item); // 컴파일 오류 발생: T.class는 사용할 수 없음
    }
}
```

그러니까 제네릭은 런타임에 타입정보가 없고! 타입 안전 이종컨테이너는 런타임에 타입정보가 필요하고! 그래서 같이 못씀!

물론 **슈퍼 타입 토큰**이라고 이걸 해결 할 수 있긴하다. (제네릭이지만 타입 이종 컨테이너를 쓸 수 있게 해줌)

![image.png](33%20%E1%84%90%E1%85%A1%E1%84%8B%E1%85%B5%E1%86%B8%20%E1%84%8B%E1%85%A1%E1%86%AB%E1%84%8C%E1%85%A5%E1%86%AB%20%E1%84%8B%E1%85%B5%E1%84%8C%E1%85%A9%E1%86%BC%20%E1%84%8F%E1%85%A5%E1%86%AB%E1%84%90%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%82%E1%85%A5%E1%84%85%E1%85%B3%E1%86%AF%20%E1%84%80%E1%85%A9%E1%84%85%E1%85%A7%E1%84%92%E1%85%A1%E1%84%85%E1%85%A1%2012155986da1080958763d4a355007935/image%201.png)

### 실제로 쓰이는 예시

사실 실제로 우리가 코딩하면서 타입 안전 이종 컨테이너를 쓸일은 크게 없어 보인다.

그러나 애너테이션 API에선 **한정적 타입 토큰**을 잘 사용중이다.

`AnnotatedElement` 인터페이스에 선언된 메서드중 하나이다. 대상 요소에 달려있으면 애너테이션을 런타임에 읽어오도록 동작한다. 

```java
<T extends Annotation> T getAnnotation(Class<T> annotationType);
```

여기서 annotationType인수는 애너테이션 타입을 뜻하는 타입 토큰이다. 

이 메서드는 토큰으로 명시한 타입의 애너테이션이 대상 요소(**클래스, 메서드, 필드** 등)에 달려있다면 해당 애너테이션을 반환하고 없다면 null을 반환한다.

여기선 애너테이션된 요소가 타입이종컨테이너인 셈이다.

여기서 Class<?>와 같이 비한정적 와일드카드 타입을 한정적 타입 토큰을 받는 메서드에 전달할 때,객체를 Class<? extends Annotation>으로 형변환할 수는 있으나, 비검사 경고 문구가 뜰 것이다.

(왜냐? 애너테이션 타입만을 대상으로하려했지만 제네릭이라 타입안정성이 보장되지 않기때문에)

Class에서는 이러한 동적 형변환을 안전하게 수행해주는 asSubclass 메서드를 제공한다. 이거 쓰면 된다.

```java
public <U> Class<? extends U> asSubclass(Class<U> clazz) {
    if (clazz.isAssignableFrom(this))
        return (Class<? extends U>) this;
    elsethrow new ClassCastException(this.toString());
}
```

### 요약

- 어렵다……………………
- 뭔소리야…………………………..
- 타입토큰을 이용해 타입 안전 이종 컨테이너를 만들 수 있고.
- 특히 애노테이션에서 타입 안전 이종 컨테이너를 쓰고 있다.

---

[[Effective Java] 아이템 33 : 타입 안전 이종 컨테이너를 고려하라](https://velog.io/@semi-cloud/Effective-Java-아이템-33-타입-안전-이종-컨테이너를-고려하라)

[이펙티브 자바, 쉽게 정리하기 - item 33. 타입 안전 이종 컨테이너를 고려하라](https://jake-seo-dev.tistory.com/53#%ED%25--%25--%EC%25-E%25--%25--%EC%25--%25--%EC%25A-%25--%25--%EC%25-D%25B-%EC%25A-%25--%25--%EC%BB%25A-%ED%25--%25-C%EC%25-D%25B-%EB%25--%25--%EB%25-E%25--%25-F)

[https://velog.io/@dailylifecoding/Java-Using-ParameterizedTypeReferenceType-At-Runtime-Using-Spring-Parameterized](https://velog.io/@dailylifecoding/Java-Using-ParameterizedTypeReferenceType-At-Runtime-Using-Spring-Parameterized)

[https://yangbongsoo.gitbook.io/study/super_type_token](https://yangbongsoo.gitbook.io/study/super_type_token)

[Effective Java : 아이템33. 한정적 타입 토큰](https://ojt90902.tistory.com/1418)