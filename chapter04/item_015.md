### 컴포넌트를 잘 설계하려면

- 내부 데이터와 내부 구현 정보를 외부 컴포넌트로부터 잘 숨겨야한다.
- 구현과 API를 깔끔히 분리하자.
- 오직 API를 통해서만 다른 컴포넌트와 소통하며 서로의 내부 동작 방식은 관여하지 않는다.
- 정보 은닉, 캡술화가 핵심이다.

### 다시 짚고 넘어가는 API란

- 프로그래머가 클래스, 인터페이스, 패키지를 통해 접근할 수 있는 모든 클래스, 인터페이스, 생성자, 멤버, 직렬화된 상태를 말한다.
- API를 사용하는 프로그램 작성사를 = 사용자, API를 사용하는 클래스(코드) = 클라이언트 라고 한다.
- 공개 API는 해당 API를 정의한 패키지 밖에서 접근할 수 있는 API 요소로 이루어진다.

### 정보은닉은 왜 해야할까

- 시스템 개발 속도를 높인다. 컴포넌트간 의존성이 없어 병렬로 개발 가능하기 때문이다.
- 시스템 관리 비용을 낮춘다. 각 컴포넌트별로 디버깅이 가능하기 때문
- 컴포넌트간 의존성이 없어 특정 컴포넌트만 최적화할 수 있기 때문에 성능 최적화가 쉽다.
- 컴포넌트 재사용 하기 좋다

요지는 접근 제한자를 제대로 활용하면 컴포넌트간의 의존성을 줄일 수 있고 결과적으로는 시스템 개발 속도도 올라간다.

### 접근 제한자 사용 원칙

접근 제한자 사용의 기본 원칙은 모든 클래스와 멤버의 접근성을 가능한 한 좁혀야 한다.

public으로 선언하면 공개 API가 되니 패키지 외부에서 쓸 일이 없다면 package-private으로 선언하자. package-private은 내부 구현이 되어 언제든 수정할 수 있지만 public으로 선언한다면 API가 되므로 하위 호환을 영원히 관리해줘야만 한다.

만약 특정 클래스에서만 사용되는 package-private 클래스가 있다면 이를 사용하는 클래스 안에 private static으로 중첩 시켜보자. 톱레벨로 두면 같은 패키지의 모든 클래스가 접근할 수 있지만, private static으로 중첩시키면 바깥 클래스 하나에서만 접근할 수 있다.

```java
// Library.java
public class Library {
    public void addBook(String isbn) {
        if (BookValidator.isValidISBN(isbn)) {
            // 도서 컬렉션에 도서 추가
            System.out.println("도서가 추가되었습니다. ISBN: " + isbn);
        } else {
            System.out.println("유효하지 않은 ISBN: " + isbn);
        }
    }

    // private static으로 중첩된 도우미 클래스
    private static class BookValidator {
        public static boolean isValidISBN(String isbn) {
            // 간단한 ISBN 검증 로직
            return isbn != null && isbn.matches("\\d{13}");
        }
    }
}
```

public일 필요가 없는 클래스들의 접근 수준을 package-private으로 좁히자.

- private: 멤버를 선언한 톱레벨 클래스에서만 접근 가능
- package-private: 멤버가 소속된 패키지 안의 모든 클래스에서 접근 가능
- protected: package-private이 가지는 접근 범위 + 상속받은 하위 클래스에서도 접근 가능
- public: 모든 곳에서 접근 가능

어떤걸 클래스 공개 API로 둘건지 잘 설계한 후, 나머지는 private으로 만들자.

같은 패키지의 다른 클래스가 접근해야하는 멤버에 한해 package-private 풀어주되, 권한을 풀어주는 일이 자주 있다면 컴포넌트를 분해하는 쪽으로 리팩토링을 고려해보자.

Serialize 인터페이스를 구현하게 되면, 필드들이 의도치 않게 공개 API가 될 수 있으므로 주의해야 한다. (아이템 86,87에 등장)

자바에서는 상위 클래스의 메서드를 재정의할 때 상위 클래스보다 접근 제어자를 좁게 설정할 수 없다.

상위 클래스의 인스턴스는 하위 클래스의 인스턴스로 대체할 수 있어야하기 때문이다.(리스코프 치환 원칙)

인터페이스의 멤버는 기본적으로 public(abstract)이 적용되고, 클래스는 인터페이스가 정의한 모든 메서드를 public으로 선언해야하는 것이 이 예시중 하나이다.

### 테스트를 위해 접근 범위를 넓히지 말자

- 코드를 테스트하려는 목적으로 접근 범위를 넓힐때 private멤버를 package-private까지 풀어주는 것은 괜찮지만, 공개 API로 만들면 안된다.
- 차라리 테스트 코드를 테스트 대상과 같은 패키지에 두면 package-private요소에 접근할 수 있다.

### 클래스 설계시 주의해야할 점

public 클래스의 가변 필드는 되도록이면 private이어야한다.

필드가 가변 객체를 참조하거나, final이 아닌 인스턴스 필드를 public으로 선언하면 필드에 들어가는 값을 제한하지 못하게 된다. 어디서든 접근해 수정할 수 있기때문이다.

또한 필드가 수정될때 락 획득 같은 병렬적인 작업을 할 수 없게 되므로 public 가변 필드를 갖는 클래스는 스레드 안전하지 않다.

필드는 private, 동기화된 메서드로 스레드 안정성을 보장하자.

```java
// Counter.java
public class Counter {
    private int count = 0;

    // 동기화된 메서드로 스레드 안전성 보장
    public synchronized void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }
}
```

final에 불변객체 참조라고 문제가 해결되진 않는다.
public 접근자라면 필드가 final이고 불변 객체를 참조하더라도 외부에서 이미 호출중일때 리팩터링에 제약이 생긴다는 문제가 있다.

상수라면 public static final 필드로 공개해도된다. 다만 기본 타입이나 불변 객체를 참조해야한다. 재할당을 못하더라도 참조된 객체 자체가 수정될 수 있기 때문이다.

특히 길이가 0이 아닌 배열은 모두 변경 가능하니 주의하자.(0은 변경이고 자시고 값 자체가 없으니)
아래와 같이 작성한다면 클라이언트가 VALUES를 자유롭게 수정할 수 있다.

```java
public static final Thing[] VALUES = { ... };
```

여튼 저러한 변경가능성을 해결하려면 두가지 방법이 있는데

첫번째론 코드의 public배열을 private로 만들고 public 불변 리스트를 추가하는 것이다.

```java
private static final Thing[] PRIVATE_VALUES = { ... };
public static final List<Thing> VALUES =
	Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));
```

두번째는 배열을 private으로 만들고 복사본을 반환하는 것이다.

아이템 14에서 배열의 clone은 사용을 추천했다. 주의해야할 점은 Thing의 필드값은 private이거나 불변이어야한다는 점이다. **얕은 복사**의 경우, **객체의 상태 변경 가능성이 존재하기때문이당.**

```java
private static final Thing[] VALUES = { ... };
public static final Thing[] values() {
  return PRIVATE_VALUES.clone(); 
}
```

### 자바9에서 도입된 모듈

패키지란 클래스의 묶음이다. 모듈은 패키지의 묶음이다.

자바9에서는 모듈 시스템이라는 개념이 도입되면서 패키지 중 공개할 것을 module-info.java에 선언한다.

```java
module my.module {
	export module.name;
}
```

위와 같이 public 멤버라도 해당 패키지에서 공개하지 않았다면 모듈 외부에서 접근할 수 없다.

모듈의 JAR 파일을 자신의 모듈 경로가 아닌 애플리케이션의 클래스패스(class-path)에 두면 그 모듈 안의 모든 패키지는 마치 모듈이 없는 것처럼 행동한다.

모듈이 공개했는지 여부와 상관없이 public, protected 멤버를 모듈 밖에서도 접근 가능하게된다.

### 결론

- 접근제한자는 가능한 최소로 주자.
- 꼭 필요한 것만 골라 최소한의 public API를 설계하자
- 멤버가 의도치 않게 API로 공개되는 일이 없도록 하자.
- public 클래스는 상수용 public static final 필드 외에 어떠한 public 필드도 가져선 안된다.
- public static final 필드가 참조하는 객체가 불변인지 확인하자