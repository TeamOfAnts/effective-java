
### `@Override`의 역할
- 메서드에 달아서 상위 클래스/인터페이스의 메서드를 재정의 했음을 알려준다.

-> 사실 `@Override`를 안써도 동작은 한다.

###  `@Override`를 쓰면 방지할 수 있는 문제들

```java
public boolean equals(Object obj) {  
    return (this == obj);  
}
```

메서드 시그니처는 **타입 파라미터**와 **메서드 명**이다.
위와 같이 Object 타입의 파라미터를 받는것까지가 시그니처인데
아래처럼 시그니처중 하나를 잘못 작성하게 될 수도 있다.

```java
static class Bigram {
        private final char first;
        private final char second;

        public Bigram(char first, char second) {
            this.first = first;
            this.second = second;
        }

        public boolean equals(Bigram b) {
            return b.first == first && b.second == second;
        }

        public int hashCode() {
            return 31 * first + second;
        }
    }
```

오버라이딩을 한게 아니라 오버로딩한셈이다..

```java
@Override
public boolean equals(Bigram b) {
            return b.first == first && b.second == second;
        }
```

이럴때 `@Override` 애너테이션을 달면 실제로 상속받은 메서드가 아니라고 에러를 내준다.
`@Override` 를 통해 제대로 상속 받은건지 알 수 있다.

### 결론
- 재정의할땐 `@Override`를 달면 실수하지 않을 수 있다.
- 다만 구체 클래스에서 상위 클래스의 추상메서드를 재정의할때는 굳이 @Override를 달지 않아도 된다. (애초에 구현하지 않으면 컴파일 자체가 안되니까)