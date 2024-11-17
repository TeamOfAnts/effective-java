

1. **누군가가 내가 만든 불변식을 깨뜨리려 혈안이 되어 있다고 가정하자**
   - 클래스를 설계할 때 **방어적 프로그래밍(defensive programming)**을 통해 외부로부터의 공격을 방지하라.
2. **`private final`만으로 값을 지킬 수 없다.**
   - 클래스가 불변으로 선언되더라도 내부 값이 가변 객체라면 불변식을 깨뜨릴 수 있다.
   - 내부까지 불변인 클래스를 사용하면 간단하지만, 많은 API와 구현이 가변 객체를 사용하기 때문에 방어적 조치를 고려해야 한다.
3. **생성자에서 방어적 복사본을 만들어라.**
   - **생성자로 전달받은 가변 매개변수는 방어적 복사(defensive copy)를 통해 보호하라.**
   - 방어적 복사본을 만든 후 유효성 검사를 진행하면, 원본 객체가 멀티스레딩 환경에서 변경될 위험을 방지할 수 있다.
      - 검사시점과 사용시점의 차이로 발생하는 취약점을 **TOCTOU(검사시점/사용시점 공격)**이라고 한다.

```java
import java.util.Date;

public final class Period {
    private final Date start;
    private final Date end;

    public Period(Date start, Date end) {
        // 방어적 복사본 생성
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());

        // 유효성 검사
        if (this.start.after(this.end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }
}

```
---

4. **접근자도 방어적 복사를 적용하라.**
   - 생성자에서 방어적 복사를 적용했더라도, 내부 가변 정보를 반환하는 접근자 메서드가 있다면 여전히 문제가 생길 수 있다.
   - **클래스 외부에서 내부 필드에 접근할 때마다 새로운 인스턴스를 반환**하여 캡슐화를 강화하라.

```java
public Date getStart() {
    return new Date(start.getTime()); // 방어적 복사
}

public Date getEnd() {
    return new Date(end.getTime()); // 방어적 복사
}
```
---

5. **`clone` 사용 시 주의하라.**
   - **생성자에서는 `clone`을 사용하지 마라.**
      - 예를 들어, 생성자가 `Date` 타입을 받고, `Date`를 확장한 악의적인 하위 클래스가 들어올 경우, 해당 클래스의 `clone`이 예상치 못한 동작을 할 수 있다.
   - **접근자에서 `clone`은 사용 가능하다.**
      - 클래스 내부에서 사용하는 객체가 신뢰 가능한 불변 클래스라면 `clone`을 안전하게 사용할 수 있다.

```java
// 생성자와 접근자에서 `clone` 비교

// 생성자에서 clone 사용은 권장되지 않음
public Period(Date start, Date end) {
    this.start = start.clone(); // 비권장
    this.end = end.clone();     // 비권장
}

// 접근자에서 clone 사용은 안전
public Date getStartClone() {
    return (Date) start.clone(); // 신뢰 가능한 클래스이므로 안전
}

```

---

6. **클라이언트 제공 객체를 내부 자료구조에 저장할 때 주의하라.**
   - 클라이언트가 제공한 객체가 변경 가능하다면, 방어적 복사를 수행해 내부 자료구조에 저장하라.
   - 내부 객체를 클라이언트에 반환하기 전에도 방어적 복사를 적용하라.

```java
// 방어적 복사 후 저장 및 반환
public class Person {
   private final List<String> favoriteBooks;

   public Person(List<String> favoriteBooks) {
      // 방어적 복사 후 저장
      this.favoriteBooks = new ArrayList<>(favoriteBooks);
   }

   public List<String> getFavoriteBooks() {
      // 방어적 복사 후 반환
      return Collections.unmodifiableList(new ArrayList<>(favoriteBooks));
   }
}
```
---

### **결론**

1. 클라이언트로부터 받은 객체나 반환할 객체가 가변이라면 반드시 방어적 복사를 수행하라.
2. 방어적 복사가 필요 없다고 확신할 때는, 문서화하여 책임이 클라이언트에 있음을 명시하라.
3. 복사 비용이 지나치게 크거나 객체가 불변임을 보장할 수 있다면, 방어적 복사를 생략할 수 있다.