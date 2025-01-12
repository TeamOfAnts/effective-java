
### 직렬화란 뭘까요?

자바가 객체를 바이트스크림으로 인코딩하고, 
바이트 스트림으로부터 다시 객체를 재구성하는 메커니즘을 직렬화 -> 역직렬화라고한다. 
직렬화된 객체는 다른 VM에 전송하거나 디스크에 저장할 수 있고, 그래서 저장한 객체를 나중에 다시 JVM으로 불러와 역직렬화할 수 있다.  

자바에 직렬화가 처음 도입된 것은 1997년. 당시에는 범용 데이터 포맷(JSON, Protobuf 등)이 보편화되지 않았음. 그때 나온 방법이 직렬화/역직렬화를 통해 분산객체(RMI등 바이트스트림으로 직접 자바 객체를 주고 받는것)를 주고받는 것이었다. 하지만 이 방식은 문제가 많았다.  

### 직렬화의 문제점

1. **너무 넓은 공격 범위**
    - 역직렬화(`ObjectInputStream.readObject()`) 과정에서 **해당 객체 그래프에 속한 모든 코드**(생성자, 메서드)가 잠재적으로 호출될 수 있음.
    - 자바 표준 라이브러리, 서드파티 라이브러리, 애플리케이션 내부 클래스 등 **모든 직렬화 가능 클래스**가 공격 범위 안에 들어온다.
2. **가젯(Gadget)**
    - 역직렬화 시 자동으로 호출될 수 있는 **위험한 메서드**를 “가젯”이라고 부름.
    - 공격자는 직렬화된 바이트 스트림에 특정 객체 그래프를 심어, 가젯을 체인처럼 이어붙여 **임의 코드 실행** 또는 **디도스**(DOS) 공격을 가능하게 함.
3. **역직렬화 폭탄(Deserialization Bomb)**
    - 짧은 바이트 스트림이지만, 역직렬화하는 데 기하급수적 시간이 드는 **HashSet 중첩 구조** 같은 사례.
    - `HashSet`이 해시코드를 계산하기 위해 내부적으로 계속 서로를 참조하게 되어, **사실상 무한**에 가깝게 시간이 소요됨.

```java

Set<Object> root = new HashSet<>();
        Set<Object> s1 = root;
        Set<Object> s2 = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            Set<Object> t1 = new HashSet<>();
            Set<Object> t2 = new HashSet<>();
            t1.add("foo"); // t1을 t2와 다르게 만든다.
            s1.add(t1);
            s1.add(t2);
            s2.add(t1);
            s2.add(t2);
            s1 = t1;
            s2 = t2;
        }
        return serialize(root); // 이 메서드는 effectivejava.chapter12.Util 클래스에 정의되어 있다.
```
        
### 그러니까 자바 직렬화를 최대한 피하라

1. **신뢰할 수 없는 바이트 스트림은 역직렬화하지 말 것**
2. **크로스-플랫폼 구조화된 데이터** 사용
    - **JSON**, **프로토콜 버퍼(Protocol Buffers)**, **XML** 등
    - 이들은 기본적으로 **타입/필드** 단위로만 역직렬화하고, **“객체 전체”**(임의 그래프) 복원은 하지 않는다.
    - 언어 간 호환이 쉽고, 보안 측면에서도 **직렬화만큼 위험**하지 않음.
3. **그래도 직렬화를 써야 한다면 (레거시 시스템에서)**
    - **역직렬화 필터링**(`java.io.ObjectInputFilter`, 자바 9+) 사용
    - **화이트리스트 방식**으로 허용할 클래스만 지정(블랙리스트는 지속적으로 관리가 어려움).
    - 그래도 **폭탄** 등 모든 공격을 완벽 방어하긴 어렵다.


### 직렬화를 피할 수 없는 레거시에선? 객체 역직렬화 필터링

레거시 시스템의 경우를 알려준다고해놓고선 감히 자바9부터 지원한다고한다.
자바6 맛 좀 보고 싶나? 가랏 레거시빔

```java
	ObjectInputFilter filter = info -> {
	    Class<?> serialClass = info.serialClass();
	    // 화이트리스트 방식: 특정 클래스만 허용
	    if (serialClass != null && serialClass.getName().equals("java.util.HashSet")) {
	        return ObjectInputFilter.Status.ALLOWED;
	    }
	    return ObjectInputFilter.Status.REJECTED;
	};
	
	try (ObjectInputStream ois = new ObjectInputStream(inputStream)) {
	    ObjectInputFilter.Config.setObjectInputFilter(ois, filter);
	    Object obj = ois.readObject();
	    // ... 안전성이 어느정도 보장된 obj 처리 ...
	} catch (IOException | ClassNotFoundException e) {
	    // handle exception
	}
```

- 화이트리스트로 제한된 클래스만 역직렬화 허용.
- 역직렬화 필터링은 블랙리스트(거부할 클래스 지정)와 화이트리스트(받아들일 클래스 지정) 방식을 지원하는데 화이트리스트 방식을 추천한다. 
- 그래도 직렬화 폭탄은 걸러내지 못한다.  

---

###  결론

1. **자바 직렬화를 되도록 쓰지 말자**
    - 보안 위험이 크고, 유지보수·성능도 좋지 않음.
2. **JSON·프로토콜 버퍼 같은 대안** 사용
    - 데이터 구조가 명확하고, 이식성이 높으며, 역직렬화 공격 위험도 상대적으로 낮음.
3. **레거시 코드는 역직렬화 필터링(ObjectInputFilter)** 적용
    - 신뢰할 수 없는 소스의 바이트 스트림에는 절대 `readObject()`를 직접 쓰지 말 것.
4. **직렬화 폭탄** 문제 해결은 쉽지 않음
    - 필터링으로 클래스 목록은 제한 가능하나, 해시 중첩 구조 같은 폭탄 공격을 완전히 막기는 어렵다.