# 아이템 71. 필요 없는 검사 예외 사용은 피하라

Checked Exception은 잘 쓰면 프로그램의 안정성을 높인다.  
하지만 Checked Exception은 호출하는 코드에서 catch 블록을 사용하거나 더 바깥으로 던져 문제를 전파해야만 한다. 그리고 추가적으로 스트림 안에서 Checked Exception을 던지는 메서드를 사용할 수 없기도 하다.    
이러한 이유로 아이템 70에서 애매한 경우에는 Unchecked Exception을 사용하는 것이 더 나은 선택이라고 언급했다.

## Checked Exception을 회피하는 방법

### 1. Optional을 반환

예외를 던지는 대신, Optional을 반환하여 호출자가 값의 존재 여부를 명확히 알 수 있도록 한다.

- 잘못된 예시
    ```java
    public User findUser(String username) throws UserNotFoundException {
        User user = database.findUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + username);
        }
        return user;
    }
    ```

- 올바른 예시
    ```java
    public Optional<User> findUser(String username) {
        User user = database.findUserByUsername(username);
        return Optional.ofNullable(user);
    }
    ```

### 2. 메서드를 두 개로 분리하여 Unchecked Exception 사용

- 바꾸기 전
    ```java
    try {
        obj.action(args);
    } catch (TheCheckedException e) {
        ...
    }
    ```

- 바꾼 후
    ```java
    if (obj.actionPermitted(args)) {
        obj.action(args);
    } else {
        ...
    }
  
    // 실패 시 스레드를 중단하기 원한다면 다음처럼 한 줄로 작성해도 무방
    obj.action(args);
    ```

여기에서의 `actionPermitted`는 상태 검사 메서드에 해당하므로, 아이템 69에서 언급했던 것처럼 만약 외부 동기화 없이 여러 스레드가 동시에 접근할 수 있거나 외부 요인에 의해 상태가 변할 수 있다면 이 리팩터링은 적절하지 않다. `actionPermitted`와 `action` 호출 사이에 객체의 상태가 변할 수 있기 때문이다.

## 정리

- Checked Exception은 프로그램 안정성을 높이지만, 너무 많이 사용하면 오히려 프로그램을 더 어렵게 만드므로 필요한 곳에 잘 쓰자.
- 혹은 `Optional`을 사용하도록 해보고, 그래도 안된다면 그 때 Checked Exception을 던지자.
