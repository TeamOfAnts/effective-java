
### **원자성과 가시성에 대해 짚고 넘어가자**

1. **원자성**
    - 데이터를 읽거나 쓸 때, 중간에 끼어드는 일이 없어야 한다.
    - 이를 **배타적 수행**이라 한다.
    - 예: `i++`는 내부적으로 `read -> increment -> write` 3단계로 이뤄지므로 **원자** 연산이 아니다.
2. **가시성**
    - 한 스레드에서 변경한 값이 **즉시 다른 스레드**에게도 보이는가?
    - 이를 **스레드간 통신**이라 한다.
    - 자바 메모리 모델에서는 동기화가 없으면 다른 스레드가 **변경 사항을 읽지 못할 수 있음**.
    - `volatile` 키워드는 **가시성**을 보장하지만, **원자성**은 보장하지 않는다.

---

본론으로 들어가봅시다.

우리는 보통 동기화를 떠올릴때 특정 스레드에 의해 객체가 변하는 순간, 다른 스레드가 변하는 걸 막게하는 용도도 있지만(원자성), 실제로는 스레드간의 접근 결과를 온전히 보여주는 역할도 한다.(가시성)

그러니까 `synchronized` 블럭은 여러 스레드가 동기화가 필요한 필드에 접근하기전, **너 동기화 됐니?** 라고 스레드 간 물어보는 역할도 하는 셈이다.

### **동기화 없이는 가시성 문제가 발생**

```java
public class StopThread {
    private static boolean stopRequested;

    public static void main(String[] args) throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            while (!stopRequested) {
                i++;
            }
        });

        backgroundThread.start();
        TimeUnit.SECONDS.sleep(1);
        stopRequested = true; // 다른 스레드에서 볼 수 없을 수도 있음
    }
}

```

- 기대대로라면 1초 후 `stopRequested`가 `true`가 되어 `while` 문이 종료되어야 한다.
- 실제로는 `backgroundThread`는 `stopRequested`가 변경되지 않았다고 가정하고 무한 루프 돈다.
- 왜냐? 컴파일러 최적화로 인해 `stopRequested`를 확인하는 과정에서 동기화가 됐는지를 확인 안하도록 수정했을수도 있기때문이다. 
  
실제론 조건을 확인하는 부분이 이렇게 돌고 있단 얘기다.

```java
if (!stopRequested)
	while (true)
		i++;
```

이전에 말했듯이, 동기화에선 원자성과 가시성을 둘다 보장해야한다. 이를 보장하는 데엔 여러가지 방법이 있다.

### **`synchronized` 사용**

```java
public class StopThread {
    private static boolean stopRequested;

    private static synchronized void requestStop() {
        stopRequested = true;
    }

    private static synchronized boolean isStopRequested() {
        return stopRequested;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            while (!isStopRequested()) {
                i++;
            }
        });

        backgroundThread.start();
        TimeUnit.SECONDS.sleep(1);
        requestStop();
    }
}
```

- 첫째로는 synchronized를 사용해서 해당 필드에 접근하는 메서드를 만드는 방식이고
- 이때는 **쓰기도 동기화**, **읽기도 동기화**해야 한다.
- 동기화 블록에 들어갈 때와 나올 때, **해당 스레드는 메인 메모리와 동기화**되므로, 가시성이 보장된다.

### **`volatile` 사용**

```java
public class StopThread {
    private static volatile boolean stopRequested;

    public static void main(String[] args) throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            while (!stopRequested) {
                i++;
            }
        });

        backgroundThread.start();
        TimeUnit.SECONDS.sleep(1);
        stopRequested = true; // 이 값은 다른 스레드에서도 즉시 보임
    }
}

```

- 둘째로는 `synchronized`보다 조금 더 빠른 방법인데 동기화가 필요한 필드를 `volatile`로 선언하는 거다. 
- `volatile` 한정자는 배타적 수행을 보장하는 키워드는 아니지만 항상 최근에 기록된 값을 읽게해주는 건 보장하니, 가시성을 보장해주는 키워드인셈이다.

### **`volatile`은 원자성 보장 X**

```java
public class SerialNumberGenerator {
    private static volatile int nextSerialNumber = 0;

    public static int generateSerialNumber() {
        return nextSerialNumber++;
        // 실제로는 (read -> increment -> write) 세 단계
        // 둘 이상의 스레드가 동시에 접근하면 충돌 가능
    }
}

```

단, `volatile`은 **원자성**을 보장하지 않는다. `++` 같은 복합 연산에는 부적합하다.
nextSerialNumber++;라는건 코드자체는 하나지만, 실제로는 nextSerialNumber필드에 1. 값을 읽고 2. 증가된 새로운 값을 저장하기 때문에 두번 접근한다. 이때 A스레드에서 값을 읽고 > B에서도 값을 읽고 > A가 증가한다면? +2가 되어야하지만 결과적으론 +1의 결론이 난다.

즉, `volatile`만으로는 **원자적 증가 연산**을 보장할 수 없다.

### **해결책**

1. `synchronized` 사용
    ```java
    public class SerialNumberGenerator {
        private static int nextSerialNumber = 0;
    
        public static synchronized int generateSerialNumber() {
            return nextSerialNumber++;
        }
    }
    
    ```
    
2. **`AtomicLong` 또는 `AtomicInteger`** 사용
    ```java
    private static final AtomicLong nextSerialNumber = new AtomicLong(0);
    
    public static long generateSerialNumber() {
        return nextSerialNumber.incrementAndGet();
    }
    
    ```
    

### **가변 데이터를 공유하지 않는 것이 최선**

장황하게 설명했지만 이러한 동기화 문제들을 피하고 싶으면
- 불변 데이터만 공유하거나 아무것도 공유하지말고
- 가변 데이터는 단일 스레드에서만 쓰자.
- 가변 객체를 다른 스레드와 공유하려면, **안전 발행**(safe publication) 과정을 거쳐야 한다.
    - 예: **동기화된 접근** 후에만 공유, 불변 래퍼로 감싸서 공유 등등.. (다음 아이템에 자세히 나온다)

---

### **결론**

1. **동시성**에서는 **원자성**과 **가시성** 모두를 고려해야 한다.
2. `volatile`은 가시성은 보장하지만, 원자성은 보장하지 않는다.
3. **증가 연산**처럼 복합적 연산을 안전하게 처리하려면 `synchronized`나 `AtomicXXX` 클래스를 사용한다.
4. 가능하면 **가변 데이터를 공유하지 않도록** 설계하라.
    - 불변 객체만 공유하거나, 공유가 필요한 경우 **동기화**를 철저히 적용하자.
