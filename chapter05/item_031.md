### 제네릭이 뭐야?

제네릭은 클래스나 메서드를 정의할 때 그 안에서 사용할 타입을 미리 정하지 않고 나중에 사용할 타입을 지정할 수 있도록 만드는 기능이다.

우리가 자주 보는 이 T는 제네릭 타입 매개변수이고, 구체적인 타입을 나중에 결정할수 있도록 하는 타입변수이다.

```java
public class MyCollection<T> {
    private Collection<T> collection;

    public MyCollection(Collection<T> collection) {
        this.collection = collection;
    }

    public void add(T t) {
        collection.add(t);
    }
}
```

참고로 제네릭을 사용할때는 타입 매개변수를 반드시 <> 안에 넣어야한다.
`MyCollection<String>`은 말이 되지만 `MyCollection String`은 동작하지 않기 때문이다.

타입 매개변수도 이와 동일하게 `MyCollection<T>`로 명시해줘야한다.

위는 제네릭 클래스의 예시이고. 제너릭 메서드는 클래스 전체가 제네릭이 아니더라도 특정 메서드만 제네릭으로 정의 할 수 있다.  호출할 때 T타입이 결정된다.

```java
public <T>void print(T item) { // 이건 클래스에 <T>를 넣지 않았으니 메서드에 넣어준셈
    System.out.println(item);
}
```

제네릭을 사용하지 않을때의 코드를 보자.
String 추가 후 Integer를 추가하는 코드를 작성하더라도 컴파일 오류가 발생하지 않는다.
둘다 Object의 하위 클래스기 때문이다.

```java
public class MyCollection {
    private Collection collection;

    public void add(Object o) {
        collection.add(o);
    }
}

// API를 호출하면
MyCollection myCollection = new MyCollection(new ArrayList());
myCollection.add("Hello");  // String 타입 추가
Integer number = (Integer) myCollection.get(0);  // 런타임 오류 발생 가능
```

### 제네릭은 불공변하다.

```java
class Animal {
    ...
}

class Cat extends Animal {
    ...
}
```

이러한 상속관계가 있을때, **공변**이란 서브타입이 슈퍼타입이 될 수 있는 걸 말하고

```java
Animal animal = new Cat();
```

**불공변**이란 서브타입이 슈퍼타입이 될수 없고, 슈퍼타입도 서브타입이 될 수 없는 것을 말한다.

```java
List<Animal> animals = new ArrayList<Cat>(); // 컴파일 에러
```

아이템 28에서 설명했듯이 **제너릭은 불공변하다는 특성**을 가진다.

```java
public class MyCollection<T> {

    private Collection<T> collection;

    public MyCollection(Collection<T> collection) {
        this.collection = collection;
    }

    public void add(T t) {
        collection.add(t);
    }

    public void addAll(Collection<T> collection) {
        this.collection.addAll(collection);
    }

```

이러한 예제 코드가 있을때, `add()` 메서드는 공변이 가능하지만, `addAll()` 메서드는 불공변하다. add과 addAll은 똑같이 제너릭을 사용했는데 무슨 차이일까?

```java
// JDK 1.9 이상 기준
MyCollection<Number> myCollection = new MyCollection<>(new ArrayList<>());
myCollection.add(1); // 정상, Integer 삽입
List<Integer> ints = List.of(2, 3, 4, 5);
myCollection.addAll(ints); // 컴파일 에러
```

`add(T t)` 메서드는 **단일 요소**를 추가하는 케이스이다.
Java에서 제네릭 타입은 불공변이지만, 단일 객체에 대해서는 Java의 타입 시스템이 하위 타입을 상위 타입으로 처리할 수 있는 공변성을 허용하기 때문에 아래는 동작한다.

```java
MyCollection<Number> myCollection = new MyCollection<>(new ArrayList<>());
myCollection.add(1);  // Integer를 Number로 취급할 수 있음
```

그렇다면 왜 addAll은 불공변할까? (우리가 보기엔 둘다 제네릭인데)
addAll `(Collection<T>)` 는 여러개의 요소를 담고 있는 컬렉션 전체를 추가하는 메서드이다.
인데,  `Collection<Number>`와 `Collection<Integer>`는 상위-하위 타입이 아니라서 불공변적이며 호환이 되지 않는다.

```java
MyCollection<Number> myCollection = new MyCollection<>(new ArrayList<>());
List<Integer> ints = List.of(1, 2, 3);
myCollection.addAll(ints);  // 컴파일 에러: 불공변성 문제
```

정리하자면 단순히 T를 취급할땐 자바에서 서브타입을 추가하는 공변성이 허용되어 추가가 가능하지만, Collection을 사용하면 서로 다른 타입 취급을 하므로 불공변성 문제가 발생한다.

**제네릭 자체는 불공변적이지만**, Java의 **타입 시스템**은 공변성을 허용하기 때문에 위와 같은 코드가 동작하는 것이다.

### 불공변을 공변하도록 바꾸려면

타입 매개 변수를 한정적 와일드카드 타입을 통해 공변적으로 만들 수 있다.
단순히 T만 이용했을땐 Number만 받을 수 있지만 이젠 하위타입인 Integer도 가능하다.

```java
public void addAll(Collection<? extends T> collection) {
    this.collection.addAll(collection);
}
```

위와 같이 `extends` 키워드는 타입 매개 변수를 **공변**하게 하지만 super 키워르를 이용하면 제네릭 타입을 **반공변**하게 한다.

```java
public void addAll(Collection<? super T> collection) {
        this.collection.addAll(collection);
    }
```

반공변이란 공변의 반대로, 서브타입이 슈퍼타입의 하위 타입일때, 슈퍼티입이 서브타입이 될 수 있는 것을 말한다.

### 언제 extends를 쓰고 언제 super를 쓸까?

> 펙스(PECS): producer-extends, consumer-super
>
- **Producer-Extends** : `<? extends T>`는 **생산자**로, 값을 읽거나 가져올 때 사용됩니다.
- **Consumer-Super** : `<? super T>`는 **소비자**로, 값을 추가하거나 전달할 때 사용됩니다.

`addAllInside`같은 경우는 외부 컬렉션의 요소 -> 내부 컬렉션에 추가하기 때문에 제공(생산) 역할이며, 공변성을 지원한다. `addOutside`은 내부 컬렉션의 요소를 외부 컬렉션에 추가해주기 때문에 소비(쓰기) 작업이므로 반공변성을 지원한다.

```java

public void addAllInside(Collection<? extends T> collection) {
	this.collection.addAll(collection);  // 외부 컬렉션의 요소를 내부 컬렉션에 추가
}

public void addOutside(Collection<? super T> collection) {
    collection.addAll(this.collection);  // 내부 컬렉션의 요소를 외부 컬렉션에 추가
}
```

### 와일드 카드를 써야할 때 vs 타입 매개변수를 써야할 때

```java
// 비한정적 타입 매개변수
private static <E> void swap(List<E> list, int i, int j);

// 비한정적 와일드카드
public static void swap(List<?> list, int i, int j);
```

타입이 불분명한 상황에서 공개 API를 개발할거라면 후자가 더 낫다.

메서드 시그니처에 타입 매개변수 `<T>`가 단 한번만 사용된다면 **와일드카드(?)** 로 대체하는 것이 더 직관적이며, 와일드카드를 사용하면 해당 메서드가 **타입 매개변수를 재사용하거나 일관성을 유지할 필요가 없다는 의도**를 전달할 수 있기 떄문이다.

**결론은 메서드 선언에 타입 매개변수가 한번만 나오면 와일드 카드로 대체하자.**

```java
public <T> void printItem(T item) {
	// 이것보단
    System.out.println(item);
}

public void printItem(List<?> list) {
	// 얘가 특정 타입에 의존하지않고, 단순 읽기 전용이라는게 더 잘보이니까
    list.forEach(System.out::println);

```

그렇지만 사실 비한정적 와일드카드를 선언하면 컴파일 에러가 나기때문에 아래와같이 helper 메서드와 같이 써야한다.

```java
public static void swap(List<?> list, int i, int j) {
        list.set(i,j);  // 이렇게하면 에러남
    }

public static void swap(List<?> list, int i, int j) {
    swapHelper(list, i, j);
}

private static <E> void swapHelper(List<E> list, int i, int j) {
    list.set(i, list.set(j, list.get(i)));
}
```

### 결론.

- 제네릭에 와일드 카드 타입을 적용하면 API를 유연하게 사용할 수 있다.
- 생산자는 extends, 소비자는 super인 PECS를 기억하자.
- **타입 매개변수(T)**는 메서드,클래스 내부에서 일관된 타입을 보장해야할 때 사용한다.
- **와일드카드(?)**는 타입 불확실성을 허용할때 사용한다.