## 우선 Clone이 뭔데? 어떻게 쓰는건데? 얘만 있으면 쓸수 있나?

- 얘는 Object 클래스에 있는 clone이라는 메서드고 객체간의 값을 복사를 해줄 때 사용하는 메서드야. Object가 모든 객체의 모체인만큼 사실 어떤 클래스든 clone 메서드를 클래스내에 오버라이드하면 호출할 수 있어
- 호출할 수 있어야하지만 clone을 오버라이드하는 것 만으로는 CloneNotSupportedException이 발생해
- 그럼 뭘 더 해줘야해? Cloneable이라는 인터페이스가 있는데 clone 메서드를 쓸거면 얘를 구현 받아야함 (그리고 얘를 믹스인 인터페이스라고함 명세만을 위한 인터페이스, 상속받을 수 있는 클래스인데 개념적으로 부모클래스의 개념이 아닌 다른 클래스에서 사용할 수 있는 메서드를 포함하는 클래스를 말함. 정확히는 상속이 아니라 포함이라고함, 근데 정확히는 Cloneable는 믹스인보다는 마커인터페이스라고함)
- Cloneable 인터페이스는 어떤 메서드도 포함하지 않음.(마커 인터페이스) JVM에 이 객체를 복제할 수 있다고 알릴 뿐임..

## 엥 그러면 클래스가 clone이라는 메서드를 사용하려면 Cloneable를 상속받고 클래스내에 clone 메서드를 재정의해야하는구나.. 뭔가 이상하다..

- 이게 정석적으로 사용되는 clone의 구현방법이야

```java
public class PhoneNumber implements Cloneable {
    private int areaCode;
    private int prefix;
    private int lineNumber;

    // 생성자와 기타 메서드 생략

    @Override
    public PhoneNumber clone() {
        try {
            return (PhoneNumber) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // 발생할 수 없는 예외
        }
    }
}

```

- 막상 Object.clone 구현체도 비어있고.. Cloneable도 별다른 메서드는 없어보이는데 어디서 해당 예외를 발생시키는거래?

```java
// object
@HotSpotIntrinsicCandidate
    protected native Object clone() throws CloneNotSupportedException;

// cloneable인터페이스

public interface Cloneable {
}
```

- Object.clone 메서드를 보면 native라는 키워드가 붙여져있는데 clone 메서드는 cpp 코드에선 clone을 호출한 클래스가 Cloneable를 상속받았는지 검사하고 런타임 타입에 맞는 복제본을 생성해주는거야 (여기서는 return 타입이 Object 선언되어있으니 Object를 return해주고 이를 상속받는 클래스들에서는 공변 반환 타입을 거쳐줘야함)
- 궁금해서 더 찾아본 clone의 native 코드 (src/hotspot/share/prims/object.cpp 참조, https://github.com/openjdk/jdk/blob/jdk-11%2B28/src/java.base/share/native/libjava/Object.c)
+) 참고로 자바 버전에 따라 어디에 있는지랑 코드가 조금씩 달라서 이해하기 쉽게 간략화된 코드

```java
// src/hotspot/share/prims/object.cpp

JNIEXPORT jobject JNICALL
Java_java_lang_Object_clone(JNIEnv* env, jobject obj) {
    // 객체의 클래스 정보를 가져옴
    Klass* klass = obj->klass();

    // Cloneable 인터페이스 구현 여부 확인
    if (!klass->is_cloneable()) {
        // CloneNotSupportedException 발생
        THROW_MSG_0(vmSymbols::java_lang_CloneNotSupportedException(), "Object is not cloneable");
    }

    // 새로운 객체 메모리 할당
    oop new_obj = obj->clone();

    // 복제된 객체 반환
    return JNIHandles::make_local(env, new_obj);
}

```

## 근데 잠깐..  clone은 그러면 자바의 기본 의도와 다르게 생성자를 호출하지않고도 객체를 생성할 수 있겠네?

- 맞아!!! 그래서 우리의 의도와는 다르게 얕은 복사만 진행하게돼
- 그래서 정석적으로 사용하는 것만으론 얕은복사밖에 못해! 깊은 복사가 필요하다면 clone을 오버라이드할 때 코딩해서 구현해야돼

그러면 super.clone말고 구현내용을 바꾸면 안되나? clone 메서드내에서 super.clone()말고 this.(필드내용)으로 return하면 안돼?

```java
@Override
    protected Resource clone() throws CloneNotSupportedException {
        Resource newIns = new Resource(this.name);
        return newIns;
    }
```

- 물론 그생각을 할수도있음! 그런데 사실.. clone 메서드는 super.clone을 사용하는게 베스트야.
- super.clone외에 다른 내용이 들어가면 상속받는 하위클래스들에서 예상치 못한 동작을 하게되거든! 예를 들어서 clone메서드내에서 생성자를 호출해서 return하면 얘를 상속받는 하위클래스를 호출했을때 A를 기대하고 clone했는데 B가 return되어 예상치 못하게 동작할 수 있는거지 (그리고 이건 컴파일러가 캐치를 못해..)

## 그렇다면 clone은 깊은 복사도 못하는데.. 주소값만 따서 같은애라고 주장할거면 도대체 뭘 위해 존재한거야?

처음 clone이 나왔을 당시에는 어떻게 동작할 걸 예상하고 만들어진걸까.. Clone 메서드의 일반 규약을 아라보자.

- x.clone() != x 식은 참이어야 한다.
복사된 객체가 원본이랑 같은 주소를 가지면 안된다는 뜻
- x.clone().getClass() == x.clone().getClass() 식도 참이어야 한다.
복사된 객체가 같은 클래스여야 한다는 뜻이다.
- x.clone().equals()는 참이어야 하지만, 필수는 아니다.
복사된 객체가 논리적 동치는 일치해야 한다는 뜻이다. (필수는 아니다.)

요약하자면 clone 메서드를 사용하면 **새로운 인스턴스를 생성하여 객체 자체는 다른 주소값**을 가지지만 **내부의 참조 필드들은 원본과 동일한 객체를 참조하게 되는 것!** = 객체 자체의 주소값은 다르고 내부의 참조 필드들은 원본과 같은 얕은 복사라는거지!

## 그러면 clone메서드를 사용하는 클래스를 final클래스로 만들면안되나?

- final이라면 걱정해야할 하위 클래스가 없으니 그래도되지~
- 그런데 final 클래스를 보통 언제쓰나?
    - Java에서는 이론상으로는 중요한 class의 sub class를 만들어 sub class로 하여금 시스템을 파괴하도록 할 수 있기 때문에 Java 시스템은 중요한 class에 대해서는 final class로 선언하고 있다. 대표적인 것이 String class이다.
- 이럴때 쓰는데 final 클래스인데.. 얘를 clone하는건 이상하지?

## 가변객체에서 clone을 사용하는 법

- 우리가 Stack.clone() 했을때 기대하는건 stack 인스턴스가 생기고 내부의 elements도 가지고오는건데, 알다시피 clone은 내부 필드들은 원본과 동일한 객체를 참조하게 됨

    ```java
    public class Stack implements Cloneable {

        private Object[] elements;
        private int size = 0;
        private static final int DEFAULT_INITIAL_CAPACITY = 16;

        public Stack() {
            this.elements = new Object[DEFAULT_INITIAL_CAPACITY];
        }

        public void push(Object e) {
            ensureCapacity();
            elements[size++] = e;
        }

        public Object pop() {
            if(size == 0) {
                throw new EmptyStackException();
            }

            Object result = elements[--size];
            elements[size] = null;
            return result;
        }

        private void ensureCapacity() {
            if (elements.length == size) {
                elements = Arrays.copyOf(elements, 2 * size + 1);
            }
        }
    }
    ```

- 그러면 위와 같은 stack은 어떻게 clone 메서드를 구성해야할까? 가장 쉬운 방법은 Stack에 있는 배열객체의 clone을 이용해 복사해주는거임

    ```java
    @Override
        protected Object clone() throws CloneNotSupportedException {
            Stack cloneStack = (Stack) super.clone();
            cloneStack.elements = elements.clone();

            return cloneStack;
        }
    ```

- 여기서 elements는 배열이기 때문에 clone메서드를 따로 구현하지 않아도 쓸 수 있다. 배열 내부 메소드 clone()은 손쉽게 배열을 복사할 수 있다는 점에서 배열 복제에 권장된다. (elements 각 내부의 필드 값은 원본객체와 같은 값을 공유하겠지만 stack에서 동작할때는 문제가 되지 않는다)

결론은 배열의 clone을 사용하면, 서로 같은 elements 배열을 참조하지 않는다. (정확히는 elements는 다르고 elements 내부의 필드값들은 같은 주소를 가리키는 셈)

    ```java
    @Test
    public void stackClone() throws CloneNotSupportedException {
        Stack stack1 = new Stack();
        stack1.push("value1");
        stack1.push("value2");

        Stack stack2 = stack1.clone();
        stack2.push("value3");

        System.out.println("stack1 = " + stack1);
        System.out.println("stack2 = " + stack2);
    }
    ```


## 가변객체 내부에 가변객체가 있을때의 clone 사용법
위에서 말했던 문제가 이거다..

buckets을 복제하면 buckets자체는 다른 주소값(새로운)을 가지겠지만 buckets 내부의 key, value, next는 값은 주소를 가리키고 있을거임

```java
class HashTable implements Cloneable {
    private Entry[] buckets;

    private static class Entry {
        final Object key;
        Object value;
        Entry next;

        public Entry(Object key, Object value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    @Override
    public HashTable clone() throws CloneNotSupportedException {
        HashTable result = (HashTable) super.clone();
        result.buckets = buckets.clone();
        return result;
    }
}
```

- 원본 객체가 value를 바꾸면 복사된 객체도 똑같이 value가 지워지는거다. 이를 막기위해 재귀적으로 깊은 복사를 진행하게되면 스택오버플로우가 날수도있으니, 재귀 말고 반복자를 써서 순회하는 걸 추천한다.

```java
// 재귀
Entry deepCopy() {
            Entry result = new Entry(key, value
	            , next == null? null : next.deepCopy());
            return result;
        }

// 반복자
Entry deepCopy() {
            Entry result = new Entry(key, value, next);
            for(Entry p = result; p.next != null; p = p.next) {
                p.next = new Entry(p.next.key, p.next.value, p.next.next);
            }
            return result;
        }
```

## 더 신경써야할 것

- Object의 clone 메서드는 동기화를 고려하지않았다. (동시성 문제가 발생할 수 있다.)
- 만일 하위 클래스의 clone()을 막고 싶다면 clone() 메서드를 재정의하여, CloneNotSupportedException()을 던지도록 하자.
- 기본 타입이나 불변 객체 참조만 가지면 아무것도 수정할 필요 없으나 일련번호 혹은 고유 ID와 같은 값을 가지고 있다면, 비록 불변일지라도 새롭게 수정해주어야 할 것이다.
- 사실 위와 같은 상황까지 고려해서 clone을 구현할일은 드물다.
    - Cloneable을 이미 구현한 클래스를 확장한다면 어쩔 수 없이 저런 엣지케이스까지 고려해보고.. 잘 작동하도록 짜야하는데
    - **그게 아니라면 복사 생성자와 복사 팩터리라는 방식을 쓰자.**

## clone 대신 복사 생성자와 복사 팩터리

- 복사 생성자란 **복사 생성자**는 동일한 클래스의 다른 인스턴스를 매개변수로 받아, 그 값을 사용하여 새로운 객체를 생성하는 생성자를 말함

    ```java
    // 복사 생성자
    public Person(Person other) {
        this.name = other.name;
        this.age = other.age;
        // 깊은 복사를 위해 새로운 Address 객체 생성
        this.address = new Address(other.address);
    }
    ```

- **복사 팩토리 메서드**는 클래스 내에 정의된 정적 메서드로, 객체를 매개변수로 받아 새로운 객체를 반환함

    ```java
    // 복사 팩토리 메서드
    public static Person newInstance(Person other) {
        Address newAddress = new Address(other.address);
        return new Person(other.name, other.age, newAddress);
    }
    ```


이렇게하면

- clone() 메서드와 달리 CloneNotSupportedException을 처리할 필요가 없고 형 변환(casting) 없이도 정확한 타입을 반환함
- 상속 관계에서 복사 생성자를 구현할때 상위 클래스의 복사 생성자를 호출하여 상위 클래스의 필드도 제대로 복제할 수 있음(하위 클래스에서는 자신의 필드만 추가로 복제해주면 됨)
- '인터페이스' 타입의 인스턴스도 인수로 받을 수 있다.

```java
public HashSet(Collection<? extends E> c) {
        map = new HashMap<>(Math.max((int) (c.size()/.75f) + 1, 16));
        addAll(c);
    }
```

## 결론. 이번 챕터는 복제기능을 이용할때 clone 메서드를 이용하지말고 복사 생성자와 팩터리를 이용하자가 주제입니다.

- final클래스라면 Cloneable을 구현해도 위험은 크지 않지만, 성능 최적화 관점에서 검토 후 드물게 허용해야함
- 다만 배열을 복제할땐 쓰는게 좋음