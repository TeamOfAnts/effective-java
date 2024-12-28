# 아이템 79. 과도한 동기화는 피하라

- 응답 불가와 안전 실패를 피하려면 동기화 메서드나 동기화 블록 안에서는 제어를 절대로 클라이언트에 양도하면 안 된다.

- 외계인 메서드(alien method, *외부에서 제어 가능한 메서드*) 주의
  1. 동기화된 영역 안에서 재정의할 수 있는 메서드
  2. 클라이어트가 넘겨준 함수 객체

## 예시

### 1. 동기화된 영역 안에서 재정의할 수 있는 메서드

```java
// 기본 클래스
public class Base {
    public synchronized void methodA() {
        System.out.println("Base: methodA 시작");
        methodB();
        System.out.println("Base: methodA 끝");
    }

    public synchronized void methodB() {
        System.out.println("Base: methodB 실행");
    }
}

// 서브클래스
public class Derived extends Base {
    @Override
    public synchronized void methodB() {
        System.out.println("Derived: methodB 실행");
        // methodA를 다시 호출하여 교착 상태 유발
        methodA();
    }
}

public class Main {
    public static void main(String[] args) {
        Base obj = new Derived();
        obj.methodA();
    }
}
```

#### 예상 실행 결과

아래처럼 무한히 반복되면서 **교착 상태**를 유발한다.

```Base: methodA 시작
Derived: methodB 실행
Base: methodA 시작
Derived: methodB 실행
Base: methodA 시작
Derived: methodB 실행
...
```

#### 해결 방법

- `final`을 사용하여 재정의를 막는다.
- `private`을 사용하여 재정의를 막는다.
- 애초에 `abstract`로 선언하여 하위 클래스에서 구현하도록 한다.


### 2. 클라이어트가 넘겨준 함수 객체

```java
public class ObservableSet<E> extends ForwardingSet<E> {
   public ObservableSet(Set<E> set) { super(set); }
   private final List<SetObserver<E>> observers = new ArrayList<>();

   public void addObserver(SetObserver<E> observer) {
       synchronized(observers) {
           observers.add(observer);
       }
   }

   public boolean removeObserver(SetObserver<E> observer) {
       synchronized(observers) {
           return observers.remove(observer);
       }
   }

   private void notifyElementAdded(E element) {
       synchronized(observers) {
           for (SetObserver<E> observer : observers)
               observer.added(this, element);
       }
   }

    @Override public boolean add(E element) {
        boolean added = super.add(element);
        if (added)
            notifyElementAdded(element);
        return added;
    }

    @Override public boolean addAll(Collection<? extends E> c) {
        boolean result = false;
        for (E element : c)
            result |= add(element);
        return result;
    }
}
```

위의 코드가 있다.
이 코드는 아래처럼 하면 잘 동작한다.

```java
ObservableSet<Integer> set = new ObservableSet<>(new HashSet<>());
set.addObserver((s, e) -> System.out.println(e));
// 원소가 추가될 때마다 알림이 가서 0~99 출력
for (int i = 0; i < 100; i++) {
    set.add(i);
}
```

하지만 아래처럼 하면, 0부터 23까진 정상적으로 출력하지만, e가 23일 때 `ConcurrentModificationException` 에러가 발생한다.

```java
set.addObserver(new SetObserver<>() {
    public void added(ObservableSet<Integer> s, Integer e) {
        System.out.println(e);
        if (e == 23) {
            // 값이 23이면 자신을 구독해지한다.
            s.removeObserver(this);
        }
    }
});
```

`synchronized`를 사용했다 하더라도, 인자로 넣은 콜백 함수를 통해 `s.removeObserver(this)`를 통해 리스트가 수정되는 것을 막을 수 없다.
이 때 향상된 for문의 Iterator가 리스트 변경을 감지하고 `ConcurrentModificationException`을 던진다.

#### 해결 방법

- 리스트를 복사한 후 순회한다.
- `CopyOnWriteArrayList`을 사용한다. (내부적으로 복사한 후 순회)
- 동기화 블록 밖으로 클라이언트 코드를 이동시킨다. (사실상 같은 방법임)
    ```java
    private void notifyElementAdded(E element) {
        List<SetObserver<E>> snapshot = null;
        synchronized(observers) {
            snapshot = new ArrayList<>(observers);
        }
        for (SetObserver<E> observer : snapshot)
            observer.added(this, element); // (동기화 블록 외부)
    }
    ```

## 문제를 피할 수 있는 기본 규칙

- 동기화 영역에서는 가능한 한 일을 적게 하자.

## 성능 측면

- 동기화를 하면 성능이 좋지 않다.
  - 병렬로 실행할 기회를 잃는다.
  - 가상머신의 코드 최적화를 제한한다.

## 가변 클래스를 작성해야한다면

- 동기화를 하지 말고, 그 클래스를 사용해야하는 클래스가 외부에서 알아서 동기화하게 하자.
- 동기화를 내부에서 수행해 스레드 안전한 클래스로 만들자.
    -
    ```java
    public class Counter {
        private int count = 0;

        // synchronized 키워드로 동기화
        public synchronized void increment() {
            count++;
        }

        public synchronized int getCount() {
            return count;
        }
    }
    // 혹은 `Atomic` 사용
    ```
  
## 핵심 정리

- 동기화 영역 안에서의 작업은 최소화(외계인 메서드 절대 호출 금지)
- 합당한 이유가 있을 때만 내부에서 동기화(+문서화)
