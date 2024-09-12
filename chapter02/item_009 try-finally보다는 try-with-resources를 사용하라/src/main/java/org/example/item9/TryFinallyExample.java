package org.example.item9;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
