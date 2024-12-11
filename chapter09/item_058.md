### **for문의 단점**

- 전통적인 for문은 반복자(iterator)나 인덱스를 직접 다뤄야 하므로 **실수할 가능성이 크다**.
    - 예를 들어, 잘못된 인덱스를 사용하거나, 반복자 객체를 잊고 관리하면 오류가 발생할 수 있다.
- 코드가 장황해져서 **가독성이 떨어지고** 유지보수가 어려워진다.

```java
// 실수하기 쉬운 전통적인 for문
List<String> list = List.of("apple", "banana", "cherry");
for (int i = 0; i <= list.size(); i++) { // 경계 오류: <= 대신 <여야 함
    System.out.println(list.get(i));    // 실행 시 IndexOutOfBoundsException 발생
}
```

### **for-each를 사용하는 이유**

1. **반복자와 인덱스를 다루지 않아도 된다.**
    - for-each는 컬렉션의 내부 구조를 추상화하여 반복자의 존재를 신경 쓰지 않아도 된다.
2. **어떤 컨테이너를 다루는지 신경 쓰지 않아도 된다.**
    - 배열, `List`, `Set`, `Map` 등 **`Iterable` 인터페이스를 구현한 객체**는 모두 동일한 방식으로 순회 가능하다.
3. **가독성과 유지보수성이 뛰어나다.**
    - 코드가 간결해지고 오류 가능성을 줄인다.

```java
// 다양한 컨테이너에서 for-each 사용
// 배열
int[] numbers = {1, 2, 3, 4};
for (int number : numbers) {
    System.out.println(number);
}

// 리스트
List<String> list = List.of("apple", "banana", "cherry");
for (String item : list) {
    System.out.println(item);
}

// 집합
Set<Integer> set = Set.of(10, 20, 30);
for (int num : set) {
    System.out.println(num);
}
```

---

### **for-each를 쓸 수 없는 상황**

1. **파괴적인 필터링 (Destructive Filtering)**:
    - 컬렉션을 순회하면서 원소를 제거해야 하는 경우, `Iterator`의 `remove` 메서드를 사용해야 한다.

```java
List<String> list = new ArrayList<>(List.of("apple", "banana", "cherry"));
Iterator<String> iterator = list.iterator();
while (iterator.hasNext()) {
    if (iterator.next().startsWith("b")) {
        iterator.remove(); // for-each로는 불가능
    }
}
System.out.println(list); // 출력: [apple, cherry]
```

1. **원소 변형 (Transforming)**:
    - 컬렉션의 원소를 수정해야 하는 경우.

```java
List<String> list = new ArrayList<>(List.of("apple", "banana", "cherry"));
for (int i = 0; i < list.size(); i++) {
    list.set(i, list.get(i).toUpperCase()); // for-each로는 불가능
}
System.out.println(list); // 출력: [APPLE, BANANA, CHERRY]
```

1. **병렬 반복 (Parallel Iteration)**:
    - 두 개 이상의 컬렉션을 동시에 순회해야 하는 경우.

```java
List<String> names = List.of("apple", "banana", "cherry");
List<Integer> quantities = List.of(10, 20, 30);

for (int i = 0; i < names.size(); i++) {
    System.out.println(names.get(i) + ": " + quantities.get(i));
}
```

---

### **결론**

1. for-each 문을 사용하면 코드가 간결하고, 오류 가능성이 줄며 가독성이 좋아진다.
2. 원소의 묶음을 표현하는 타입을 작성할 때는 **`Iterable` 인터페이스**를 구현하여 for-each 문에서 사용할 수 있게 하라.
3. 다만, 컬렉션의 원소를 제거하거나 수정해야 하거나, 병렬로 순회해야 하는 경우는 전통적인 for문을 사용하라.