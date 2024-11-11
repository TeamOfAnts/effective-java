# 아이템 45. 스트림은 주의해서 사용하라

## 스트림 특징

```java
public class StreamExample {
    public static void main(String[] args) {
        List<StreamExampleDto> dtoList = someObjects.stream() // 스트림
                .map(StreamExampleDto::new) // 스트림 파이프라인 시작
                .filter(dto -> dto.getSomeValue() > 0)
                .limit(10)
                .map(dto -> {
                    dto.setSomeValue(dto.getSomeValue() * 2);
                    return dto;
                }) // 스트림 파이프라인 끝
                .collect(Collectors.toList()); // 종단 연산
    }
}
```

- 플루언트 API(fluent API) : 메서드 체인을 이용해 연산을 수행하는 API
- 순차적 진행이 default지만 병렬 진행도 가능

## 적절한 스트림 사용

```java
// 스트림 사용 전
public class Anagrams {
    public static void main(String[] args) throws IOException {
        File dictionary = new File(args[0]);
        int minGroupSize = Integer.parseInt(args[1]);
        
        Map<String, Set<String>> groups = new HashMap<>();
        try (Scanner s = new Scanner(dictionary)) {
            while (s.hasNext()) {
                String word = s.next();
                groups.computeIfAbsent(alphabetize(word), (unused) -> new TreeSet<>()).add(word);
            }
        }
        
        for (Set<String> group : groups.values()) {
            if (group.size() >= minGroupSize) {
                System.out.println(group.size() + ": " + group);
            }
        }
    }
    
    private static String alphabetize(String s) {
        char[] a = s.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }
}
```

```java
// 잘못된 스트림 사용
public class Anagrams {
    public static void main(String[] args) throws IOException {
        Path dictionary = Paths.get(args[0]);
        int minGroupSize = Integer.parseInt(args[1]);
        
        try (Stream<String> words = Files.lines(dictionary)) {
            words.collect(groupingBy(word -> word.chars().sorted()
                    .collect(StringBuilder::new, // 책에서는
                            (sb, c) -> sb.append((char) c), // 이 부분을 
                            StringBuilder::append).toString())) // 과하다고 말하고 있다.
                    .values().stream()
                    .filter(group -> group.size() >= minGroupSize)
                    .forEach(g -> System.out.println(g.size() + ": " + g));
        }
    }
}
```

```java
// 적절한 스트림 사용
public class Anagrams {
    public static void main(String[] args) throws IOException {
        Path dictionary = Paths.get(args[0]);
        int minGroupSize = Integer.parseInt(args[1]);
        
        try (Stream<String> words = Files.lines(dictionary)) {
            words.collect(groupingBy(word -> alphabetize(word))) // 이와 같이 도우미 메서드를 사용하여 리팩토링!
                    .values().stream()
                    .filter(group -> group.size() >= minGroupSize)
                    .forEach(g -> System.out.println(g.size() + ": " + g));
        }
    }
    
    private static String alphabetize(String s) {
        char[] a = s.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }
}
```

## 반복문 vs 스트림

### 반복문이 더 유리한 경우

1. 지역 변수 사용 및 수정이 잦을 때 & `return` / `break` / `continue` / `throw` 할 때
```java
int[] numbers = {1, 3, 5, 6, 7, 9};
int result = 0;

for (int number : numbers) {
    if (number % 2 == 0) {
        result = number;
        break;
    }
    result += number;
}
```

2. 성능이 중요할 때
for문이 stream보다 빠르다. (벤치마크 찾아보면 진짜 그냥 훨씬 빠르다. 단, 안의 로직 자체의 오버헤드가 크다면 for, stream의 성능 차이가 큰 의미가 없어질 수는 있다. 그래도 성능차이는 무조건 존재하긴 한다.)

### 스트림이 더 유리한 경우

1. 데이터의 변환과 필터링이 필요할 때 (가독성과 유지보수성을 중요시할 때)

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
List<Integer> evenSquares = numbers.stream()
                                   .filter(n -> n % 2 == 0)
                                   .map(n -> n * n)
                                   .collect(Collectors.toList());
```

2. 병렬 처리할 때 (단, 병렬 처리하는 데이터의 양이 충분히 크거나 로직의 오버헤드가 커야 의미가 있음. 비교해보고 쓰자.)

```java
List<Integer> largeNumbers = IntStream.range(1, 1000000).boxed().collect(Collectors.toList());
int sum = largeNumbers.parallelStream()
                      .mapToInt(Integer::intValue)
                      .sum();
```

## 정리

반복문이 좋을 때도 있고, 스트림이 좋은 경우가 있다.
**스트림과 반복문 중 어느 쪽이 나은지 확신하기 어렵다면 둘 다 해보고 더 나은 쪽을 택하라.**
