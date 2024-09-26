# 아이템 18. 상속보다는 컴포지션을 고려하라

## 요점

- 클래스 설계 시 상속보다는 컴포지션을 우선적으로 고려해야한다.
- 상속과 컴포지션은 코드 재사용을 위한 두 가지 주요 방법이다.
- 올바른 선택을 통해 유지보수성과 확장성을 높이자.

## 상속의 문제점

- 상속은 부모 클래스의 구현에 의존하므로, 부모 클래스의 변경이 자식 클래스에 영향을 줄 수 있음
- 부모 클래스의 메서드를 잘못 오버라이딩하면 오류 발생할 수 있음
- 상속 구조가 복잡해질수록 유연성이 떨어짐

## 컴포지션

### 컴포지션은 다른 객체를 자신의 필드로 포함하여 활용하는 방법

- 캡슐화 유지
  - 내부 구현이 외부에 노출되지 않음 
- 유연성 향상
  - 런타임에 포함된 객체를 변경하여 동적으로 동작을 변경 가능
- 재사용성 증가
  - 필요한 기능만 선택적으로 사용하고, 불필요한 부모 클래스의 기능을 상속받지 않음
- 의존성 감소
  - 클래스 간 결합도 낮아짐

## 예제 코드 비교

#### 문제 있는 상속의 예

```java
public class InstrumentedHashSet<E> extends HashSet<E> {
    private int addCount = 0;

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }
}
```

- 문제점:
    HashSet의 addAll 메서드가 add 메서드를 호출하면 addCount가 이중으로 증가합니다.
    부모 클래스의 구현 세부 사항에 의존하여 오류가 발생합니다.

```bash
addAll() 메서드는 내부적으로 add() 메서드를 호출한다.
따라서, addAll()을 호출하면 addCount가 두 번 증가하게 된다.
이 문제는 HashSet이 내부적으로 add()를 사용하는지 알 수 없기 때문에 발생한다.
즉, 부모 클래스의 구현 세부 사항에 의존하고 있기 때문에 발생하는 문제이다.
```


#### 컴포지션을 활용한 예

```java
public class InstrumentedSet<E> extends ForwardingSet<E> {
    private int addCount = 0;

    public InstrumentedSet(Set<E> s) {
        super(s);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }
}

public class ForwardingSet<E> implements Set<E> {
    private final Set<E> s;

    public ForwardingSet(Set<E> s) {
        this.s = s;
    }
    
    public boolean add(E e) { return s.add(e); }
    public boolean addAll(Collection<? extends E> c) { return s.addAll(c); }
}
```

- 이점:
    InstrumentedSet은 Set 인터페이스를 구현하고, 내부에 다른 Set의 구현체를 포함합니다.
    내부 Set의 구현 변경에 영향을 받지 않습니다.
