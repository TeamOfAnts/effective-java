
Seriazliable의 기본 직렬화 형태인 상태로 세상에 내놓게되면 다음 릴리스때 기존의 구현을 변경하지 못한다. 
그러니 고민해보고 괜찮다고 판단될 때만 기본 직렬화 형태를 사용하자.

### 기본 직렬화 형태는 되도록이면 사용하지 말자

객체의 물리적 표현과 논리적 내용이 같으면 기본 직렬화 형태도 괜찮다.

**물리적 표현과 논리적 내용이 같은 경우**는 사용해도 괜찮다.
이 경우 내용이 직렬화 대상인 Name 클래스가 논리적으로 이름, 성, 중간이름이라는 3개의 문자열이고
물리적 표현 또한 이와 관련된 변수 세개가 끝이니 논리적 구성요소와 같다고 본다.
```java
public class Name implements Serializable {
    /**
     * 성. null이 아니어야 함.
     * @serial
     */
    private final Stirng lastName;

    /**
     * 이름. null이 아니어야 함.
     * @serial
     */
    private final String firstName;

    /**
     * 중간이름. 중간이름이 없다면 null.
     * @serial
     */
    private final String middleName;

    ... // 나머지 코드는 생략
}
```

이런식으로 기본 직렬화가 적합하다 판단했으면 불변식 보장과 보안을 위해 readObject 메서드를 제공하고 끝내면 된다.

하지만 **물리적 표현과 논리적 내용이 다른 경우**도 있다.
이 클래스에 기본 직렬화 형태를 사용하면 양방향 연결 정보를 포함해 각 노드에 연결된 노드들까지 모두 표현할 것이다. 논리적으론 문자열을 표현하고자 했는데 연결정보나 노드 연결정보까지 전부 직렬화되기 때문에 문제가 발생한다.
```java
public final class StringList implements Serializable {
    private int size = 0;
    private Entry head = null;

    private static class Entry implements Serializable {
        String data;
        Entry next;
        Entry previous;
    }
    
    ... // 나머지 코드는 생략
}
```

### 물리적 표현과 논리적 표현의 차이가 클때 기본 직렬화 형태를 사용하면 생기는 문제

1. 공개 API가 현재 내부 표현에 영구히 묶인다
	- 데이터 표현 방식을 바꾸더라도 기존 코드를 제거할 수 없다.
2. 너무 많은 공간을 차지한다.
	- 내부 구현에 해당해 딱히 직렬화 형태에 필요없는 내용도 전부 직렬화 된다. 
3. 시간이 오래 걸린다
	- 객체 그래프 위상에 관한 정보가 없으니 일일히 순회하기때문에
4. 스택 오버플로를 일으킬수있다
	- 객체 그래프를 재귀 순회하기 때문에

### 그렇다면 적절한 직렬화 방식은 무엇일까?

위의 케이스에서는 두가지를 지키면 된다.

1. 단순히 리스트가 포함한 문자열의 개수, 문자열들만 있으면 된다.
2. 물리적인 상세 표현은 배제하고 논리적인 구성을 담으면 된다.

```java
public final class StringList implements Serializable {
    private transient int size = 0;
    private transient Entry head = null;

    // 이번에는 직렬화 하지 않는다.
    private static class Entry {
        String data;
        Entry next;
        Entry previous;
    }

    // 지정한 문자열을 리스트에 추가한다.
    public final void add(String s) { ... }

    /**
     * StringList 인스턴스를 직렬화한다.
     */
    private void writeObject(ObjectOutputStream stream)
            throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(size);

        // 모든 원소를 순서대로 기록한다.
        for (Entry e = head; e != null; e = e.next) {
            s.writeObject(e.data);
        }
    }

    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        int numElements = stream.readInt();

        for (int i = 0; i < numElements; i++) {
            add((String) stream.readObject());
        }
    }
    ... // 나머지 코드는 생략
}
```

1. `transient` 키워드가 붙은 필드는 기본 직렬화 형태에 포함되지 않는다.
2. 클래스의 모든 필드가 `transient`로 선언되었더라도 `writeObject`와 `readObject` 메서드는 각각 `defaultWriteObject`와 `defaultReadObject` 메서드를 호출한다. 이렇게 해야 이후 `transient` 가 아닌 인스턴스 필드가 추가되더라도 상호호환되기 때문이다.
3. **논리적 상태와 무관한 필드라고 판단될 때만 `transient` 한정자를 생략해야 한다.**
4. 기본 직렬화를 사용한다면 역직렬화할 때 `transient` 필드는 기본값으로 초기화된다.
5. 기본값을 변경해야 하는 경우에는 `readObject` 메서드에서 `defaultReadObject` 메서드를 호출한 다음 원하는 값으로 지정하면 된다.

### 기본 직렬화 사용 여부와 상관없이 직렬화에도 동기화 규칙을 적용해야 한다.

모든 메서드를 `synchronized`로 선언하여 스레드 안전하게 만든 객체에서 기본 직렬화를 사용하려면 `writeObject`도 `synchronized`로 선언해야 한다.

```java
private synchronized void writeObject(ObjectOutputStream stream)
        throws IOException {
    stream.defaultWriteObject();
}
```

### SerialVersionUID

어느 직렬화 형태를 택하든 직렬화 가능 클래스 모두에 직렬 버전 UID를 명시적으로 부여해야 한다.

```java
private static final long serialVersionUID = <무작위로 고른 long 값>;
```

- 직렬 버전 UID가 일으키는 잠재적인 호환성 문제를 피할 수 있고,
- 런타임에 생성하는 연산을 skip할 수 있어서 성능 측면에서 약간 도움이 된다.
- 새로 작성하는 경우 어떤 long 값을 선택하든 상관 없으며, 반드시 고유한 값일 필요도 없다.
- 구버전으로 직렬화된 인스턴스들과의 호환성을 끊는 경우가 아니라면 SUID 값을 절대 수정해서는 안 된다.

### 결론

- 클래스를 직렬화하기로 했다면 어떤 직렬화 형태를 사용할지 고민해보자.
- 객체의 논리적 표현과 물리적 표현이 같을때만 기본 직렬화를 사용하자. 아니라면 커스텀 직렬화 형태를 사용하자.
