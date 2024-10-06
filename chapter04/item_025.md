# 아이템 25. 톱레벨 클래스는 한 파일에 하나만 담자

톱레벨 클래스를 여러 개 선언하면 이득은 없고 심각한 위험만 발생할 수 있다.

한 클래스를 여러 개로 정의할 수 있으며, 그 중 어느 것을 사용할지는 어느 소스 파일을 먼저 컴파일하냐에 따라 달라지기 때문이다.

## 톱레벨 클래스 중복 정의

[Utensil과 Dessert를 참조하는 Main 클래스]

```java
public class Main {
    public static void main(String[] args) {
        System.out.println(Utensil.NAME + Dessert.NAME);
    }
}

```

이제 아래 파일을 만들어보자.

[Utensil 파일에 함께 선언된 Utensil 클래스와 Dessert 클래스]

```java
class Utensil {
    static final String NAME = "pan";
}

class Dessert {
    static final String NAME = "cake";
}
```

Main을 실행하면 `pancake`를 출력한다.

이제 우연히 똑같은 두 클래스를 담은 Dessert.java 파일을 만들어보자.

[Dessert 파일에 함께 선언된 Utensil 클래스와 Dessert 클래스]

```java
class Utensil {
    static final String NAME = "pot";
}

class Dessert {
    static final String NAME = "pie";
}
```

이렇게 Utensil 파일에 있는 똑같은 클래스를 중복 정의했을 때 컴파일러의 동작 결과가 달라지므로 문제가 발생한다.

### 컴파일 오류 발생 명령어

```java
javac Main.java Dessert.java // 컴파일 오류 발생
```

컴파일러는 가장 먼저 Main.java를 컴파일하고, 그 안에서 Utensil.java 파일을 살펴 Utensil 클래스와 Dessert 클래스를 찾아낼 것이다.

그리고 Dessert.java를 처리할 때 같은 클래스들의 정의가 이미 있음을 알게 된다.

### 정상 동작 명령어

```java
javac Main.java // pancake 출력
javac Main.java Utensil.java // pancake 출력
javac Dessert.java Main.java // potpie 출력
```

이렇게 컴파일러에 어느 소스 파일을 먼저 건네느냐에 따라서 정상 동작할 수도 또는 컴파일 오류가 발생할 수도 있다. 따라서 이런 문제가 발생하지 않게 바로잡아야 한다.

## 톱레벨 클래스 중복 정의 해결 방법

단순히 톱레벨 클래스들을 서로 다른 소스 파일로 분리하면 된다. 만약, 굳이 서로 다른 여러 톱레벨 클래스를 한 파일에 담고 싶다면 정적 멤버 클래스를 사용하는 방법을 고민해보자.

### 톱레벨 클래스를 정적 멤버 클래스로 변경

```java
public class Test {
    public static void main(String[] args) {
        System.out.println(Utensil.NAME + Dessert.NAME);
    }

    private static class Utensil {
        static final String NAME = "pan";
    }
    
    private static class Dessert {
        static final String NAME = "cake";
    }
}
```

## 정리

소스 파일 하나에는 반드시 톱레벨 클래스(혹은 톱레벨 인터페이스)를 하나만 작성하자.
