## Comparable은 정렬할때 사용하는 인터페이스다.

- 인터페이스 안에는 comaperTo 구현메서드가 있고
- comaperTo는 동치성 비교, 순서 비교, 제너릭을 지원한다. (equals는 동치만 비교하지만 comaperTo는 순서도 비교한다.)
- 순서가 명확한 값 클래스를 작성한다면 반드시 Comparable 인터페이스를 구현하는게 좋다!

## Comparable의 규약

a.comaperTo(b) 라고 주어졌을때

- 객체간의 주어진 순서를 비교한다.
- a를 기준으로 b보다 작으면 음의 정수, 같으면 0, 크면 양의 정수이다.

구체적인 규약(sgn은 부호함수이고, 음일때 -1, 양일때 1을 나타내준다)

```java
모든 x, y에 대해 sgn(x.compareTo(y)) == -sgn(y.compareTo(x))여야 한다. 
예외도 x.compareTo(y)와 y.compareTo(x)가 동일하게 터져야 한다.
```

두객체 참조의 순서를 바꿔도 예상가능해야한다는 의미. 두개가 같다면 식의 순서가 바뀌어도 같아야 한다.

```java
추이성(transitivity)을 보장해야 한다. (x.compareTo(y) > 0 && y.compareTo(z) > 0)이면, 
x.compareTo(z) > 0이여야 한다.
0보다 크다는 것은 비교 대상보다 크다는 것이다. x > y > z 인 경우에 x > z여야 한다는 뜻이다.
```

추이관계라는게 있는데, 추이 관계 = 수학에서 집합 상의 임의의 세 원소 a, b, c에 대하여 정의된 이항관계 이 추이적 관계라 함은 이고 이면 를 만족한다는 뜻이다.

그러니까 a,b,c에 대하여 중간에 a가 빠지나, b가 빠지나 남은 원소끼리의 관계는 같아야한다는 의미다.

```java
x.compareTo(y) == 0이면, sgn(x.compareTo(z)) == sgn(y.compareTo(z))여야 한다.
x == y일 때, x == z && y == z여야 한다는 뜻이다.
```

x와 y가 순서상 동일하다면, 어떤 다른 객체 z와 비교했을 때 x와 y의 비교 결과의 부호가 동일해야 한다는 의미다.

```java
(x.compareTo(y) == 0) == (x.equals(y))
```

꼭 지켜야하는 것은 아니지만 권고사항이다. equals()와 논리적 동치를 판단하는 기준이 같다는 뜻이다.

equals()와 달리 타입이 다른 객체에 대해서는 신경 안 써도 된다. equals()와 같이 상속으로는 이러한 일반규약을 다 지킬 방법이 없고, 사용형태로 객체 안에 사용할 필드를 두는 것이 낫다.

하지만 마지막은 지키는 것이 좋다고 한다. compareTo 메서드로 수행한 동치성 결과가 equals와 같으면, compareTo로 줄지은 순서와 equals의 결과가 일관되기 때문이다.

일관되지 않아도 동작은 하겠지만 일관되지 않은 클래스 객체를 컬렉션 인터페이스에 넣으면,

컬렉션 인터페이스(Collection, Set, Map)에서 정의된 동작과 엇박자를 낼 수 있다. 정렬된 컬렉션은 동치를 비교할 때 equals()대신 compareTo()를 사용한다.

위의 예시중 하나가 BigDecimal이다.

BigDecimal의 compareTo는 순수히 숫자값만 비교하기때문에 compareTo로 “1.0”과 “1.00”을 비교하면 정렬되지 않는다.(스케일을 비교하지 않는다.)

```java
@Override
    public int compareTo(BigDecimal val) {
        // Quick path for equal scale and non-inflated case.
        if (scale == val.scale) {
            long xs = intCompact;
            long ys = val.intCompact;
            if (xs != INFLATED && ys != INFLATED)
                return xs != ys ? ((xs > ys) ? 1 : -1) : 0;
        }
        int xsign = this.signum();
        int ysign = val.signum();
        if (xsign != ysign)
            return (xsign > ysign) ? 1 : -1;
        if (xsign == 0)
            return 0;
        int cmp = compareMagnitude(val);
        return (xsign > 0) ? cmp : -cmp;
    }
```

equals에서는 스케일까지 비교하기 때문에 두 메서드가 논리적 동치를 판단하는 기준이 달라져 버렸다.

이렇게되면 HashSet을 사용할땐 원소 2개를 갖게되지만, TreeSet을 사용하면 원소를 하나만 갖게된다.

```java
@Override
    public boolean equals(Object x) {
        if (!(x instanceof BigDecimal xDec))
            return false;
        if (x == this)
            return true;
        if (scale != xDec.scale)
            return false;
        long s = this.intCompact;
        long xs = xDec.intCompact;
        if (s != INFLATED) {
            if (xs == INFLATED)
                xs = compactValFor(xDec.intVal);
            return xs == s;
        } else if (xs != INFLATED)
            return xs == compactValFor(this.intVal);

        return this.inflated().equals(xDec.inflated());
    }
```

## CompareTo는 어떻게 작성해야할까

- Comparable은 타입을 인수로 받는 제너릭 인터페이스라 compareTo 메서드의 인수타입은 컴파일 타임에 정해진다. → 형변환하거나, 인수의 타입을 확인하지 말자. 인수의 타입이 잘못됐다면 컴파일 자체가 안된다.
- compareTo는 각 필드에 대한 동치는 검사하지 않는다. 순서를 비교한다. 객체 참조 필드를 비교하려면 compareTo를 재귀적으로 호출한다. 만약 Comparable을 구현하지 않은 필드에서 비교해야한다면 Comparator를 대신 사용하자.
- Comparator는 직접만들거나, 자바가 기본적으로 제공하는 것 중 골라쓰면 된다. (아래는 자바가 제공하는걸 사용하는 예시)

    ```java
     public final class CaseInsensitiveString 
    implements Comparable<CaseInsensitiveString>{
    	public int compareTo(CaseInsensitiveString o) {
            return String.CASE_INSENSITIVE_ORDER.compare(s, o.s);
        }
    ...
    ```

    - CaseInsensitiveString가 Comparable<CaseInsensitiveString>을 구현했는데, 이는 CaseInsensitiveString의 참조는 CaseInsensitiveString만 비교할 수 있다는 뜻이다.
    - compareTo에서 java의 기본 래퍼 클래스가 제공하는 정적메서드 compare()를 이용했다.
- 클래스에 비교 대상이 되는 핵심필드가 여러개라면 어느것을 먼저 비교하느냐도 중요해진다.
    - 가장 핵심이 되는 필드가 똑같다면, 똑같지 않은 필드를 찾을때까지 비교한다. (중요한 순서대로 비교)
    - 자바 7 이전까지는 기본 타입 필드를 비교할때 >, < 같은 연산자를 사용했지만 박싱된 기본 타입 클래스들에 정적 메서드인 compare가 추가되었으니 이를 활용하자.

    ```java
    // 기본 타입 필드가 여러개일때의 비교자
    public int compareTo(PhoneNumber phonNumber) {
    				// 박싱된 기본 타입 클래스의 compare을 사용
            int result = Short.compare(areaCode, phonNumber.areaCode);
            if(result == 0){
    	        result = Short.compare(prefix, phonNumber.prefix);
    	        if(result == 0){
    			        result = Short.compare(lineNum, phonNumber.lineNum);
    		        }
            }
            return result;
        }
    ```

    - 이 예시는 자바8 이전의 예시고 자바8부터는 Comparator 인터페이스를 활용해 메서드 연쇄 방식으로 비교자를 생성할 수 있게 됐다. (가독성은 좋지만 약간의 성능저하가 있다)

    ```java
    private static final Comparator<PhoneNumber> COMPARATOR =
            comparingInt((PhoneNumber pn) -> pn.areaCode)
                    .thenComparingInt(pn -> pn.prefix)
                    .thenComparingInt(pn -> pn.lineNum);
    
    @Override
    public int compareTo(PhoneNumber pn) {
        return COMPARATOR.compare(this, pn);
    }
    ```

    - 비교자 생성 메서드 comparingInt와 thenComparingInt가 있다.
    - comparingInt는 키를 기준으로 순서를 정하는 비교자 반환 정적 메서드다. 람다 입력 인수타입을 PhoneNumber pn로 명시해주었는데, 자바의 타입 추론 능력이 타입을 알아내지 못하기때문에 컴파일 타입을 명시해준것이다.
    - thenComparingInt는 지역번호(areaCode)가 같을 수 있으니 comparingInt로 첫번째 비교자가 적용한 다음 추출된 새 키로 비교를 진행한다. 여기서는 타입을 명시해주지 않아도 된다.
    - 이와같이 Comparator는 보조 생성 메서드를 많이 지원한다.
- 참고로 Comparator는 객체 참조용 비교자 생성 메서드도 있다.
    - comparing이라는 정적 메서드가 2개 있다.
    - 첫번째는 키 추출자를 받아서 키의 자연적 순서를 이용하고, 두번째는 키 추출자 하나와 추출된 키를 비교하는 비교자까지 2개를 받는다.

        ```java
        // 자연적 순서 이용
        public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
                    Function<? super T, ? extends U> keyExtractor)
            {
                Objects.requireNonNull(keyExtractor);
                return (Comparator<T> & Serializable)
                    (c1, c2) -> keyExtractor.apply(c1).compareTo(keyExtractor.apply(c2));
            }
        
        // 키 추출자와 비교자까지 2개
        public static <T, U> Comparator<T> comparing(
                    Function<? super T, ? extends U> keyExtractor,
                    Comparator<? super U> keyComparator)
            {
                Objects.requireNonNull(keyExtractor);
                Objects.requireNonNull(keyComparator);
                return (Comparator<T> & Serializable)
                    (c1, c2) -> keyComparator.compare(keyExtractor.apply(c1),
                                                      keyExtractor.apply(c2));
            }
        ```


## 결론

- 순서를 고려해야한다면 Comparable 인터페이스를 구현하자.
- 하지만 비교기능을 제공하는 컬렉션과 어우러지게 해야한다.
- compareTo 메서드에서 필드 값을 비교할때 <와 > 같은 연산자를 쓰지 말자.
- 박싱된 기본 타입 클래스가 제공하는 정적 compare 메서드나 Comparator 인터페이스가 제공하는 비교자 생성 메서드를 활용하자.