package saka1029.shinryo.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;

public class Param {
    public String 元号;
    public String 年度;
    public String 旧元号;
    public String 旧年度;
    
    public static Param 年度(String nendo) throws IOException {
        return read("in/%s/param.json".formatted(nendo));
    }

    public static Param read(String inJsonFile) throws IOException {
        return new Gson().fromJson(Files.readString(Path.of(inJsonFile)), Param.class);
    }
}
