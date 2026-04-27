package saka1029.shinryo.main;

import java.io.IOException;

import org.junit.Test;

public class TestMain {

    public static String OUT_DIR = "../s";
    public static String BASE_URL = "https://saka1029.github.io/s";

    /**
     * 以下のコマンドのデバッグ実行
     * ./run 08 k1
     */
    @Test
    public void test08K1() throws IOException {
        Main.main(new String[] {
            "-i", "in", "-o", OUT_DIR, "-b", BASE_URL, "08", "k1"});
    }
}
