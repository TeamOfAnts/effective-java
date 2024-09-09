클래스라면 자원에 의존하기 마련이다. 그러나 의존을 하더라도 유연하고 테스트하기 쉽도록 설계하는 것이 중요하다.

우선 예시를 보자

```java
// 정적 유틸리티를 잘못 사용한 예시
public class SpellChecker{
	private static final Lexcion dictionary = ..;
	
	private SpellChecker(){} // 객체 생성 방지
	
	public static boolean isValid(String word){...}
	public static List<String> suggestions(String typo){...}
}

// 싱글턴을 잘못 사용한 예시
public class SpellChecker{
	private static final Lexcion dictionary = ..;
	
	private SpellChecker(..){}
	public static SpellChecker INSTANCE = new SpellCheck(..);
		
	public static boolean isValid(String word){...}
	public static List<String> suggestions(String typo){...}
}

```

두 방식 모두 사전을 단 하나만 사용한다고 가정하고 있으나, 실제로는 사전이 언어별로 다르거나 특수 어휘용 사전이 있을수도 있다. 그럼에도 dictionary를 final로 선언해 처음 생성하면 다른걸로 교체할 수 없도록 설계했다.

그러면 SpellChecker가 여러 사전을 사용할 수 있도록 수정하려면 final 키워드를 삭제하면 되는 일일까? 그러면 간단하겠지만 그렇게되면 멀티스레드 환경에선 어떻게 동작할지 보장할 수 없게 된다.

**그러니까 애당초 사용하는 자원에 따라 달라지는 클래스를 정적 유틸리티 클래스나 싱글턴 방식으로 설계하는 건 적합치 않은거다!**

그렇다면 클래스가 여러 자원 인스턴스를 지원해야 할때는 어떤 패턴을 사용해야할까? **인스턴스를 생성할때 생성자에 필요한 자원을 넘겨주는 방식**을 고려해볼 수 있다. 이런걸 **의존 객체 주입**이라고 부른다.

개선된 예시를 보자.

```java
public class SpellChecker{
	private final Lexcion dictionary;
	
	public SpellChecker(Lexcion dictionary){
		this.dictionary = Objects.requireNonNull(dictionary);
	}
	
	..
}
```

사실 굉장히 익숙한 코드다. 하지만 이렇게 수정함으로써 dictionary라는 하나의 자원을 사용하면서 의존관계가 어떻든 상관없이 잘 작동하게 된다. 또한 final 키워드로 불변을 보장하여 여러 클라이언트가 의존 객체를 안심하고 공유할 수 있다.

Lexcion1을 써야하는 SpellChecker객체와 Lexcion2를 써야하는 SpellChecker가 공존할 수 있게된 셈이다.

이러한 의존 객체 주입은 생성자, 정적 팩터리, 빌더에 똑같이 응용할 수 있다. 또 다른 변형을 설명하자면 생**성자에 자원 팩터리를 넘겨주는 방식**이 있다.

팩터리란 호출할 때마다 특정 타입의 인스턴스를 반복해서 만들어주는 객체를 말한다.

+) 여기서 잠깐 참조 : [[아이템 4] 인스턴스화를 막으려든 private 생성자를 사용하라](https://www.notion.so/4-private-5c73359479e54c848bfd9744c9e46393?pvs=21)

// todo → supplier 보완 예정

이러한 팩터리를 잘 표현한 예시는 자바8에서 도입된 Supplier<T>인터페이스이다.

Supplier<T>를 입력으로 받는 메서드는 일반적으로 한정적 와일드 카드 타입을 사용해 팩터리 타입 매개 변수를 제한해야한다. 이 방식을 사용해 클라이언트코드에선 자신이 명시한 타입의 하위타입이라면 무엇이든 생성할 수 있는 팩터리를 넘길 수 있다.

```java
Mosaic create(Supplier<? extends Tile> tileRactory){...}
```

물론 의존 객체 주입은 유연성과 테스트 용이성을 개선해주지만 의존성이 수천개나 된다면 알아보기 힘든 코드 설계가 될 수도 있다.

요약하자면 클래스가 내부적으로 하나 이상의 자원에 의존하고(여러가지의 사전 타입같은) 그 자원이 클래스 동작에 영향을 준다면 싱글턴과 정적 유틸리티 클래스는 지양하자. 클래스가 직접 자원을 만드는 방식은 좋지 않다. 그 대신 의존 객체 주입을 사용하자!