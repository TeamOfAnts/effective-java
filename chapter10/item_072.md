# 아이템 72. 표준 예외를 사용하라

## 표준 예외란?

자바에서 기본적으로 제공되는 예외 클래스

## 표준 예외를 사용했을 때의 장점

- 재사용성이 좋아진다.
- 다른 개발자들이 코드를 이해하기 쉬워진다.
- 예외 클래스가 적을수록 메모리 사용량도 줄고 클래스를 적재하는 시간도 줄어든다.

## 많이 쓰이는 표준 예외

| 예외                              | 설명 |
|---------------------------------|--|
| IllegalArgumentException        | 허용하지 않는 값이 인수로 넘어왔을 때 |
| IllegalStateException           | 객체가 메서드를 수행하기에 적절하지 않은 상태일 때 |
| NullPointerException            | null을 허용하지 않는 메서드에 null을 넘겼을 때 |
| IndexOutOfBoundsException       | 인덱스의 범위를 벗어났을 때 |
| ConcurrentModificationException | 허용되지 않는 동시 수정이 발생했을 때 |
| UnsupportedOperationException   | 호출한 메서드를 지원하지 않을 때 |
| ArithmeticException             | 산술 연산 중에 오버플로우나 0으로 나누기 등의 오류가 발생했을 때 |
| NumberFormatException | 숫자로 변환할 수 없는 문자열을 숫자로 변환하려고 했을 때 |
