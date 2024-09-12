# Item9. try-finally 보다는 try-with-resources를 사용하라

- `finalizer`와 `cleaner` 사용 대신 `AutoCloseable`를 구현하라

## try-with-resources란?

- 자바 7부터 추가된 기능으로, 자원을 회수하는 최선의 방법이다.

try 블록이 종료되었을 때, close 메서드를 실행하여 사용중인 자원을 해제하거나 반환.

`AutoClosable` 인터페이스를 상속 받고, `close` 메서드를 구현한 클래스만 사용 가능하다.

```java
public class AutoCloseFile implements AutoCloseable {
    @Override
    public void close() throws Exception {
        System.out.println("close");
    }
}
```

<br>

## try-with-resources를 사용하기 좋은 예시

1. 파일 처리
2. 네트워크 연결
3. 데이터베이스 연결

<br>

## try-with-resources 예제

```java
public class AutoCloseFile implements AutoCloseable {
    @Override
    public void close() {
        System.out.println("close");
    }

    public static void main(String[] args) {
        try (AutoCloseFile autoCloseFile = new AutoCloseFile()) {
            System.out.println("try");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

```
// 결과
try
close
```

<br>

## 꼭 회수해야하는 자원을 다룰 때는 try-finally는 사용하지 마라

코드가 복잡해져서 가독성이 떨어진다.

```java
public class Main {
    public static void main(String[] args) {
        AutoCloseFile autoCloseFile = new AutoCloseFile();
        try {
            System.out.println("try");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                autoCloseFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
```

<br>

## try-finally와 try-with-resorces 비교 코드

### TryFinallyExample

```java
public class TryFinallyExample {

    public static void copyFile(String sourceFile, String destinationFile) {
        InputStream fis = null;
        FileOutputStream fos = null;

        try {
            System.out.println("Try-Finally: 파일 복사 시작");
            fis = TryFinallyExample.class.getClassLoader().getResourceAsStream(sourceFile);
            if (fis == null) {
                System.out.println("파일을 찾을 수 없습니다: " + sourceFile);
                return;
            }

            fos = new FileOutputStream(destinationFile);
            System.out.println("Try-Finally: 파일 스트림 열기 완료");

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            System.out.println("Try-Finally: 파일 복사 완료");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 각 자원을 명시적으로 하나하나 닫아주어야 함
            if (fis != null) {
                try {
                    fis.close();
                    System.out.println("Try-Finally: InputStream 닫기 완료");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                    System.out.println("Try-Finally: FileOutputStream 닫기 완료");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        String sourceFile = "source.txt";
        String destinationFile = "destination.txt";

        copyFile(sourceFile, destinationFile);
    }
}
```

```
// 결과
Try-Finally: 파일 복사 시작
Try-Finally: 파일 스트림 열기 완료
Try-Finally: 파일 복사 완료
Try-Finally: InputStream 닫기 완료
Try-Finally: FileOutputStream 닫기 완료
```

<br>

### TryWithResourcesExample

```java
public class TryWithResourcesExample {

    public static void copyFile(String sourceFile, String destinationFile) {
        System.out.println("Try-With-Resources: 파일 복사 시작");

        try (InputStream fis = TryWithResourcesExample.class.getClassLoader().getResourceAsStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(destinationFile)) {

            if (fis == null) {
                System.out.println("파일을 찾을 수 없습니다: " + sourceFile);
                return;
            }

            System.out.println("Try-With-Resources: 파일 스트림 열기 완료");

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            System.out.println("Try-With-Resources: 파일 복사 완료");

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 여러 자원이 한 번에 해제됨
        System.out.println("Try-With-Resources: InputStream 및 FileOutputStream 해제 완료");
    }

    public static void main(String[] args) {
        String sourceFile = "source.txt";
        String destinationFile = "destination.txt";

        copyFile(sourceFile, destinationFile);
    }
}
```

```
// 결과
Try-With-Resources: 파일 복사 시작
Try-With-Resources: 파일 스트림 열기 완료
Try-With-Resources: 파일 복사 완료
Try-With-Resources: InputStream 및 FileOutputStream 해제 완료
```

- `InputStream`과 `FileOutputStream`는 `AutoCloseable` 인터페이스를 구현하고 있기 때문에, try-with-resources를 사용할 수 있다.
