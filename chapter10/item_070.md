# 아이템 70. 복구할 수 있는 상황에는 검사 예외를, 프로그래밍 오류에는 런타임 예외를 사용하라

## 예외의 종류

### Checked Exception

컴파일 시점에 예외 처리를 강제하는 예외. 메서드가 검사 예외를 던질 수 있음을 선언해야 하며, 호출하는 측에서도 예외를 처리하거나 다시 던져야 한다.

- `java.lang.Exception`을 상속받지만, `java.lang.RuntimeException`을 상속받지 않는 예외
- 예) `IOException`, `SQLException`

### Unchecked Exception

컴파일 시점에 예외 처리를 강제하지 않는 예외.

- `java.lang.RuntimeException`을 상속받는 예외.
- 예) `NullPointerException`, `IllegalArgumentException`

## 언제 무슨 에러를 쓰는가

### 호출하는 쪽에서 복구하리라 여겨지는 상황: Checked Exception 사용

- 예외 상황이 복구 가능할 때: 호출자가 예외를 인지하고 적절히 대처할 수 있는 상황
- 예외가 정상적인 프로그램 흐름의 일부일 때: 예를 들어, 사용자가 잘못된 입력을 제공했을 때 다시 입력을 받는 경우
- 외부 자원과의 상호작용 시: 파일, 네트워크, 데이터베이스 등 외부 자원과의 상호작용에서 발생할 수 있는 예외

### 프로그래밍 오류 상황: Unchecked Exception 사용

- 프로그래밍 오류를 나타낼 때: 잘못된 API 사용, 논리적 오류 등 개발자가 수정해야 할 문제
- 예외를 복구할 수 없을 때: 예외가 발생하더라도 프로그램을 정상 상태로 복구하기 어려운 경우
- API 사용의 편의성을 높이고자 할 때: 불필요한 예외 선언을 줄여 코드의 가독성을 높이고자 할 때

## 어떤 에러를 쓸 지 판단하기 애매한 상황이라면?

확신하기 어렵다면 Unchecked Exception을 사용하는 것이 더 나은 선택일 수 있다.

## 주의해야할 점

- Unchecked Exception은 반드시 `RuntimeException`을 상속받아야 한다.
- `Error`는 상속하지 않아야하고, throw 문으로 직접 던지는 일은 없어야한다. (단, `AssertionError`는 예외)
  - `Error`는 시스템 레벨에서 발생하는 문제를 나타내는 클래스이며, 프로그래머가 직접 처리할 수 없는 문제를 나타내기 때문이다.
  - `AssertionError`는 일반적으로 개발 단계에서 코드의 논리적 오류를 검증하기 위해 사용되므로 예외이다.
- `Exception`, `RuntimeException`, `Error`를 상속하지 않는 `Throwable`을 만들어서는 안 된다.
  - 참고: 자바의 예외 계층 구조는 `Throwable`을 최상위 클래스로 하고, 그 아래에 `Exception`과 `Error`가 있다.

## 정리

- 복구 가능한 상황에는 호출자가 처리할 수 있는 **검사 예외(Checked Exception)**를 사용하고, 프로그래밍 오류에는 **런타임 예외(Unchecked Exception)**를 사용
- 자바 예외 계층 구조를 준수
