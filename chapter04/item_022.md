# 아이템 22. 인퍼테이스는 타입 정의 용도로만 사용

## 인터페이스의 용도

- 인터페이스를 구현한 클래스의 인스턴스를 참조할 수 있는 타입 역할
- 클래스가 인터페이스를 구현하는 것 = 인스턴스로 뭘 할 수 있는지 클라이언트에 알리는 역할
- 이거에 대한 예외로는 상수 인터페이스가 있음

## 상수 인터페이스 안티패턴

```java
public interface ObjectStreamConstants {

    static final short STREAM_MAGIC = (short)0xaced;
    static final short STREAM_VERSION = 5;
..
}
```

- 클래스 내부에서 사용하는 상수를 내부구현인데도 클래스의 API로 노출하는 행위
- 클라이언트 코드가 내부 구현에 해당하는 상수들에게 종속됨 -> 다음 릴리스에서 해당 상수들을 쓰지 않더라도 **바이너리 호환성**을 위해 상수 인터페이스를 구현하고 있어야함
- [java.io](http://java.io/).ObjectStreamConstants -> 상수 인터페이스가 있으나 인터페이스를 잘못활용한 예

## 바이너리 호환성

- 간단하게 설명하자면 이미 컴파일된 클라이언트 코드가 다시 컴파일되지 않더라도 새로운 릴리스의 라이브러리나 클래스와 함께 정상적으로 동작할 수 있어야 한다는 의미
- 상수 인터페이스 패턴에서 다음 릴리스에서 더이상 사용하지 않는 상수더라도 클라이언트 코드가 이미 상수에 의존하여 컴파일 되어있기 때문에 해당 인터페이스를 수정하거나 삭제하지 못함

## 상수를 공개해야한다면

- 오히려 특정 클래스나 인터페이스와 강하게 연관된 상수라면 거기(클래스나 인터페이스) 자체에 추가하자
    - 박싱 클래스(Integer, Double)에 선언된 MIN_VALUE, MAX_VALUE 상수가 이 예시임
- 열거타입으로 나타내기 적합하다면 열거타입으로 만들어 공개하고 (아이템34)
- 아니라면 인스턴스화할수 없도록 유틸리티 클래스(아이템4)에 담아 공개하도록

    ```java
    // 인스턴스를 만들 수 없는 유틸리티 클래스
    public class Utility{
    	private Utility(){
    	// 기본 생성자가 만들어지는 것을 막기(인스턴스 방지)
    		throw new AssertionError();
    	}
    }
    ```

- 추가적으로 숫자 리터럴에 예를 들어 int a = 1_000_000; 으로하면 읽기 편하고 좋다!

## 유틸리티 클래스

```java
import static java.lang.Math.abs;
import static java.lang.Math.acos;
    
...
    
int i = abs(-20);
double d = acos(PI) * E;
```

- 정의된 상수를 클라이언트에서 사용하려면 클래스 이름까지 명시해야하는데, 해당 상수를 자주 사용한다면 static import해서 클래스 이름을 생략하자

## 결론

인터페이스는 타입 정의용으로만 쓰고, 상수 공개용 수단으로는 적합하지 않다.

## **추가 참조**

- https://velog.io/@kasania/Java-Static-import%EC%97%90-%EB%8C%80%ED%95%9C-%EA%B4%80%EC%B0%B0
- Constant interface는 안티패턴인가? -> https://velog.io/@kasania/Java-Constant-interface
- 이진(바이너리) 호환성이란? -> https://velog.io/@kms8571/JAVA-%EB%B0%94%EC%9D%B4%EB%84%88%EB%A6%AC-%ED%98%B8%ED%99%98%EC%84%B1-%EA%B4%80%EB%A0%A8-%EC%9D%B4%EC%8A%88