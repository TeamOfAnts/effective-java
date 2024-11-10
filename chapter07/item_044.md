# 아이템 44. 표준 함수형 인터페이스를 사용하라

## 표준 함수형 인터페이스란?

표준 함수형 인터페이스란 `java.util.function` 패키지에 정의된, 자주 쓰이는 함수형 인터페이스들이다. 
자바에서 기본적으로 제공하는 함수형 인터페이스는 자주 쓰이는 함수 패턴을 대표하는 일종의 템플릿으로, 메서드 시그니처가 고정되어 있다.


## 주요 표준 함수형 인터페이스

자바 8의 java.util.function 패키지에는 총 43개의 표준 함수형 인터페이스가 포함되어 있다. 
이 중 자주 쓰이는 몇 가지를 살펴보면 다음과 같다.

1. `Function<T, R>`: `T` 타입의 입력값을 받아 `R` 타입의 결과를 반환하는 인터페이스
    - `apply(T t)` 메서드 제공
    ```java
    import java.util.function.Function;
    
    public class CustomFunctionExample {
        public static void main(String[] args) {
            // 문자열을 받아 첫 글자만 대문자로 변환하는 함수 구현
            Function<String, String> capitalizeFirstLetter = str -> {
                if (str == null || str.isEmpty()) {
                    return str;
                }
                return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
            };
    
            // 함수 사용
            String result = capitalizeFirstLetter.apply("hello");
            System.out.println(result); // 출력: Hello
        }
    }
    ```

2. `Consumer<T>`: `T` 타입의 값을 받아 소비(출력 또는 저장)하고 결과를 반환하지 않는 함수형 인터페이스
    - `accept(T t)` 메서드 제공
   ```java
   import java.util.function.Consumer;

   public class CustomConsumerExample {
       public static void main(String[] args) {
           // 문자열을 꾸며서 출력하는 함수 구현
           Consumer<String> fancyPrinter = message -> System.out.println("*** " + message + " ***");

           // 함수 사용
           fancyPrinter.accept("Hello, World!"); // 출력: *** Hello, World! ***
       }
   }
   ```

3. `Supplier<T>`: 입력값 없이 특정 타입의 데이터를 반환하는 함수형 인터페이스다
    - `get()` 메서드 제공
    ```java
    import java.util.function.Supplier;
    
    public class CustomSupplierExample {
        public static void main(String[] args) {
            // 임의의 숫자를 반환하는 함수 구현
            Supplier<Double> randomSupplier = () -> Math.random();
    
            // 함수 사용
            System.out.println(randomSupplier.get()); // 출력: 0과 1 사이의 랜덤 값
        }
    }
    ```

4. `Predicate<T>`: `T` 타입의 값을 받아 `true`나 `false`를 반환하는 함수형 인터페이스
    - `test(T t)` 메서드 제공
    ```java
    import java.util.function.Predicate;
    
    public class CustomPredicateExample {
        public static void main(String[] args) {
            // 짝수인지 확인하는 함수 구현
            Predicate<Integer> isEven = number -> number % 2 == 0;
    
            // 함수 사용
            System.out.println(isEven.test(4)); // 출력: true
            System.out.println(isEven.test(5)); // 출력: false
        }
    }
    ```

5. `UnaryOperator<T>`: `T` 타입의 하나의 값을 받아 같은 타입의 결과를 반환하는 함수형 인터페이스
    - `apply(T t)` 메서드 제공
    ```java
    import java.util.function.UnaryOperator;
    
    public class UnaryOperatorExample {
        public static void main(String[] args) {
            // 숫자를 제곱하는 UnaryOperator 함수
            UnaryOperator<Integer> square = x -> x * x;
    
            // 함수 사용
            System.out.println(square.apply(5)); // 출력: 25
            System.out.println(square.apply(10)); // 출력: 100
        }
    }
    ```

6. `BinaryOperator<T>`: `T` 타입의 두 개의 값을 받아 동일한 타입의 결과를 반환하는 함수형 인터페이스
    - `apply(T t1, T t2)` 메서드 제공
    ```java
    import java.util.function.BinaryOperator;
    
    public class CustomBinaryOperatorExample {
        public static void main(String[] args) {
            // 두 숫자를 더하는 함수 구현
            BinaryOperator<Integer> add = (a, b) -> a + b;
    
            // 함수 사용
            System.out.println(add.apply(5, 3)); // 출력: 8
        }
    }
    ```


## 표준 함수형 인터페이스 사용 시 유의사항

표준 함수형 인터페이스는 자주 쓰이는 기능들을 손쉽게 처리할 수 있게 하지만, 다음과 같은 경우 주의가 필요하다.

1. 매개변수가 3개 이상일 경우: `Function`, `Predicate` 등은 기본적으로 2개의 매개변수만 처리할 수 있다. 3개 이상의 매개변수가 필요하다면 함수형 인터페이스를 새로 정의하거나 메서드 체인을 고려할 수 있다.
    ```java
   // 커스텀 함수형 인터페이스 정의
    @FunctionalInterface
    public interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);  // 세 개의 매개변수를 받는 함수
    }
    
    public class CustomFunctionExample {
        public static void main(String[] args) {
            TriFunction<Integer, Integer, Integer, Integer> sumThreeNumbers = (a, b, c) -> a + b + c;
    
            int result = sumThreeNumbers.apply(1, 2, 3);
            System.out.println(result); // 출력: 6
        }
    }
    ```
   
    ```java
    import java.util.function.Function;
    
   // 메서드 체인을 사용한 예제
    public class NumberProcessingChainExample {
        public static void main(String[] args) {
            // 첫 번째 숫자를 두 배로 만드는 함수
            Function<Integer, Function<Integer, Function<Integer, Integer>>> processNumbers =
                    x -> y -> z -> (x * 2) + y - z;
    
            // 함수 체인을 통해 매개변수를 단계별로 처리
            int result = processNumbers.apply(5) // 첫 번째 숫자: 두 배로 만든다 (5 * 2 = 10)
                                       .apply(3) // 두 번째 숫자: 더한다 (10 + 3 = 13)
                                       .apply(2); // 세 번째 숫자: 뺀다 (13 - 2 = 11)
            System.out.println(result); // 출력: 11
        }
    }
    ```

2. 명확한 의미 전달: 표준 인터페이스는 범용적이므로, 특정 의미를 전달하는 커스텀 인터페이스보다 의미 전달이 모호할 수 있다. 그럴 경우 커스텀 함수형 인터페이스를 만드는 것이 더 나을 수 있다.
    ```java
    import java.util.function.Predicate;
    
    public class PredicateExample {
        public static void main(String[] args) {
            // Predicate를 사용해 성인 여부를 판단하는 함수
            Predicate<Integer> isAdult = age -> age >= 18;
    
            System.out.println(isAdult.test(20)); // 출력: true
            System.out.println(isAdult.test(15)); // 출력: false
        }
    }
    ```

    ```java
    @FunctionalInterface
    public interface AgeValidator {
        boolean isAdult(int age);  // 나이가 성인인지 확인하는 메서드
    }
    
    // 구현 및 사용
    public class CustomPredicateExample {
        public static void main(String[] args) {
            AgeValidator ageValidator = age -> age >= 18;
    
            System.out.println(ageValidator.isAdult(20)); // 출력: true
            System.out.println(ageValidator.isAdult(15)); // 출력: false
        }
    }
    ```

## `@FunctionalInterface`에 대하여

- `@FunctionalInterface` 어노테이션은 함수형 인터페이스임을 명시하는 어노테이션이다.
- 해당 인터페이스는 메서드를 **오직 하나만** 가지고 있어야 컴파일 되게 해준다.

## 정리

커스텀 함수형 인터페이스를 사용하면 개발자간 의사 소통 시 리소스를 낭비할 수 있다.
웬만하면 표준 함수형 인터페이스를 잘 활용하면 가독성이 높고 일관된 코드를 작성할 수 있으며, 자바 API와의 호환성도 높아진다.
