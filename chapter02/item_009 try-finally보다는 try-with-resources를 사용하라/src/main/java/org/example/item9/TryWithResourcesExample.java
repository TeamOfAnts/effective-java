package org.example.item9;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
