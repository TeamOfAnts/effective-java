package org.example.item9;

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
