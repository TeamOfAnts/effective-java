
1. **메서드 이름을 신중히 짓자**
    - 이름만 보고도 메서드가 무슨 일을 하는지 알 수 있도록 설계하라.
    - 자바의 **표준 명명 규칙**(아이템 68)을 준수하라.
        - 일반적으로 **카멜케이스**를 사용하며, 메서드 이름은 **동사**로 시작하는 것이 좋다.
        - 예를 들어, `getName`, `setName`, `calculateTotal` 등이 적합하다.

2. **편의 메서드를 너무 많이 만들지 말자**
    - 필요 이상으로 편의 메서드를 추가하면 클래스가 비대해지고 유지보수가 어려워진다.
    - 대신, **직교성**을 높이는 설계를 고려하라.

3. **매개변수 목록을 짧게 유지하자**
    - 매개변수는 **4개 이하**로 제한하는 것이 좋다.
    - 같은 타입의 매개변수가 연달아 나올 경우, **순서를 잘못 입력해도 오류를 발견하기 어렵다.**

---

4. **매개변수 목록을 줄이는 방법**

4-1. **여러 메서드로 쪼개기**

- 메서드가 너무 많은 매개변수를 요구한다면, 관련된 동작을 **직교성이 높은 작은 메서드로 나누자.**
- 예를 들어, `List` 인터페이스는 부분 리스트에서 인덱스를 찾는 작업을 위해 두 개의 메서드를 제공한다. : `subList`와 `indexOf`.

```java
// 부분 리스트
List<String> subList = list.subList(1, 3);
// 부분 리스트에서 인덱스 찾기
int index = subList.indexOf("cherry");
System.out.println(index); // 결과: 1
```

4-2. **매개변수 여러 개를 묶는 도우미 클래스**

- **관련성 있는 매개변수**를 하나의 클래스에 묶으면 가독성과 재사용성이 좋아진다.
- 예를 들어, 카드 클래스에서 숫자(`rank`)와 무늬(`suit`)를 묶어 하나의 매개변수로 전달하라.

4-3. **빌더 패턴을 메서드 호출에 응용**

- 매개변수가 많거나 일부 매개변수를 생략해도 되는 경우, 빌더 패턴을 사용하라.

```java
// 빌더 패턴 활용
public static void main(String[] args) {
	NutritionFacts facts = new NutritionFacts.Builder(240, 8)
			.calories(100)
			.build();
}
```
---

5. **매개변수의 타입 설계**

- **인터페이스**를 매개변수 타입으로 사용하라.
    - 이를 통해, 아직 존재하지 않는 구현체도 나중에 사용할 수 있다.
- 예를 들어, `Set` 대신 `HashSet`을 직접 사용하면 새로운 구현체를 추가하기 어렵다.

```java
public void process(Set<String> data) {
    // HashSet, TreeSet 등 어떤 구현체도 받을 수 있음
}
```
---

6. **`boolean`보다는 열거 타입을 사용하라**

- `boolean`은 의미가 명확하지 않을 수 있으므로, **의미를 전달할 수 있는 열거 타입**을 사용하라.

```java
// `boolean` 대신 열거 타입 사용
public class Thermometer {
    public enum TemperatureScale {
        FAHRENHEIT, CELSIUS
    }

    public double convertTemperature(double value, TemperatureScale scale) {
        if (scale == TemperatureScale.FAHRENHEIT) {
            return (value * 9 / 5) + 32;
        } else { // CELSIUS
            return (value - 32) * 5 / 9;
        }
    }
}

```

---

### **결론**

1. 메서드 시그니처 설계 시, **직관적이고 간결**하게 작성하라.
2. **매개변수는 4개 이하**로 유지하되, 필요 시 도우미 클래스나 빌더 패턴을 사용하라.
3. **`boolean` 대신 열거 타입**, 클래스 대신 **인터페이스**를 사용해 가독성과 확장성을 높여라.
4. 설계를 단순화하면서도 직교성을 유지해 코드의 재사용성을 높이는 것이 중요하다.
