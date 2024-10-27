# 아이템 23. 태그 달린 클래스보다 클래스 계층구조를 활용하자

## 태그 달린 클래스

- 두가지 이상의 의미를 표현
- 현재 표현하는 의미를 태그값으로 알려줌

```java
class Figure{
	enum Shape { RECTANGLE, CIRCLE };
	
	final Shape shape;
	
	// 얘는 RECTANGLE 일때만 쓰이고
	double length;
	double width;
	
	// 얘는 CIRECLE 일때만 쓰임
	double radius;
}	
```

## 태그 달린 클래스 단점 개많음

- Enum으로 타입 선언을 해줘야하고
- 태그 필드나 switch문 등 쓸데없는 코드가 많다
- 여러 구현이 한 클래스에 다 있어서 가독성도 나쁨
- 예를 들어 원에 의한 기능만 쓸건데 사각형을 위한 코드도 언제나 함께하니 메모리 효율적이지도 않음
- 필드를 final로 선언하는 경우엔 쓰지않는 필드를 초기화하는 불필요한 코드도 늘어감

암튼 태그 달린 클래스는 장황하고, 오류를 내기 쉽고, 비효율적임

## 태그 달린 클래스 말고 서브타이핑을 활용하자

- 태그 달린 클래스는 클래스 계층 구조를 어설프게 흉내낸 것일 뿐임
- 태그 달린 클래스 -> 계층구조로 리팩토링하는 방법
    - 계층구조의 루트(root)가 될 추상 클래스를 정의

    ```java
    abstract class Figure {
    }
    ```

    - 태그 값에 따라 동작이 달라지는 메서드들을 추상 메서드로 선언 → 여기서는 area

    ```java
    abstract class Figure {
      abstract dobule area(); 
    }
    ```

    - 태그 값과 상관없이 동작이 일정한 메서드들을 루트 클래스에 일반 메서드로 추가
    - 하위 클래스에서 공통으로 사용하는 데이터 필드도 전부 루트 클래스로 올리자
      → 지금 예시에서는 없음

```java
abstract class Figure {
  abstract dobule area(); 
}

class Circle extends Figure {
  final double radius;
  
  Circle(double radius) {this.radius = radius;}
  
  @Override double area() {return Math.PI * (radius * radius);}
}

class Rectangle extends Figure {
  final double length;
  final double width;
  
  Rectangle(double length, double width) {
    this.length = length;
    this.width = width;
  }
  
  @Override double area() {return length * width;}
}

```

## 서브타이핑을 이용할때의 장점

- 태그 달린 클래스는 switch문 내에서 엉뚱한 필드를 초기화하면 런타임에서야 문제가 드러난다

```java
// 안티패턴
private double area() {
        switch (shape) {
            case RECTANGLE:
                return length + width;
            case CIRCLE:
                return Math.PI * (radius * radius);
            case SQUARE:
                return side * side; // -- 추가
            default:
                throw new AssertionError(shape);
        }
    }
```

- 서브타이핑을 이용하면 생성자가 필드를 남김없이 초기화하고 추상 메서드를 구현했는지 컴파일러가 확인해주기 때문에 런타임 오류를 미연에 방지할 수 있다.

```java
class Circle extends Figure {
  final double radius;
  
  Circle(double radius) {this.radius = radius;}
  
  // 여기서 area를 구현하지 않으면 컴파일 에러가 나니까
  @Override double area() {return Math.PI * (radius * radius);}
}
```

- 루트 클래스의 코드를 건드리지 않아도 다른 프로그래머들이 독립적으로 계층구조를 확장해 사용할 수 있다.
- 타입 사이의 자연스러운 계층 관계를 반영할 수 있어서 컴파일타임 타입 검사 능력을 높여준다.

## 결론

- 태그 달린 클래스를 써야하는 필수적인 상황은 거의 없다.
- 만약있다면 계층구조로의 리팩터링을 고민하자.