# 아이템 80. 스레드보다는 실행자, 태스크, 스트림을 애용하라

## 제목의 뜻

직접 스레드 관리를 피하고, 실행자(Executor), 태스크(Future), 스트림(Stream)을 사용하라!

## BAD! 직접 스레드를 관리하여 병렬 처리

나쁜 방법임!

```java
public class BadThreadManagementExample {
    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        int numberOfTasks = 1000; // 실행할 태스크 수

        for (int i = 0; i < numberOfTasks; i++) {
            Thread thread = new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " is executing the task.");
                try {
                    // 태스크 실행 시간 시뮬레이션
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            threads.add(thread);
            thread.start(); // 각 태스크마다 새로운 스레드 생성
        }

        // 모든 스레드가 종료될 때까지 대기
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("All tasks are completed.");
    }
}
```

직접 스레드를 생성하고 관리하는 방식은 자원 낭비, 복잡성 증가, 동기화 문제, 메모리 누수 등 다양한 문제를 일으킬 수 있다.

## GOOD! 실행자를 활용한 병렬 처리

좋은 방법임!

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BetterThreadManagementExample {
    public static void main(String[] args) {
        int numberOfTasks = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(10); // 고정된 스레드 풀 생성

        for (int i = 0; i < numberOfTasks; i++) {
            executor.submit(() -> {
                System.out.println(Thread.currentThread().getName() + " is executing the task.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown(); // 태스크 실행 완료 후 스레드 풀 종료
        System.out.println("All tasks are submitted.");
    }
}
```

## GOOD! Runnable과 Callable을 활용한 태스크 처리

### Runnable

```java
public class RunnableExample {
    public static void main(String[] args) {
        int numberOfTasks = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(10); // 고정된 스레드 풀 생성

        // Runnable 태스크 정의
        Runnable task = () -> {
            System.out.println(Thread.currentThread().getName() + " is executing the task.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // 태스크 제출
        for (int i = 0; i < numberOfTasks; i++) {
            executor.submit(task);
        }

        executor.shutdown(); // 태스크 실행 완료 후 스레드 풀 종료
        System.out.println("All tasks are submitted.");
    }
}
```

### Callable

```java
public class CallableExample {
    public static void main(String[] args) {
        int numberOfTasks = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(10); // 고정된 스레드 풀 생성

        // Callable 태스크 정의
        Callable<String> task = () -> {
            System.out.println(Thread.currentThread().getName() + " is executing the task.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return Thread.currentThread().getName() + " completed the task.";
        };

        // 태스크 제출
        for (int i = 0; i < numberOfTasks; i++) {
            Future<String> future = executor.submit(task); // Callable 반환값을 받을 수 있음
            try {
                // 결과 출력
                System.out.println(future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executor.shutdown(); // 태스크 실행 완료 후 스레드 풀 종료
        System.out.println("All tasks are submitted.");
    }
}
```

## GOOD! 스트림을 활용한 병렬 처리

`parallelStream()`을 사용하여 스트림을 병렬 처리

## Bonus: Runnable과 Callable의 차이

- Runnable: 반환값이 없는 태스크
- Callable: 반환값이 있는 태스크 + 예외를 처리할 수 있음

## 정리

책에서는 `ExecutorService`를 잘 쓰기를 원함.
