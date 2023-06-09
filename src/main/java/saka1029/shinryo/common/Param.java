package saka1029.shinryo.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;

public class Param {

    record Nendo(String 元号, String 年度, String 旧元号, String 旧年度) {
    }

    public static Map<String, Nendo> ALL = Map.of(
        "30", new Nendo("平成", "30", "平成", "28"),
        "01", new Nendo("令和", "01", "平成", "30"),
        "02", new Nendo("令和", "02", "令和", "01"),
        "04", new Nendo("令和", "04", "令和", "02"),
        "06", new Nendo("令和", "06", "令和", "04"),
        "08", new Nendo("令和", "08", "令和", "06"));
    
    public static Map<String, String> TITLES = Map.of(
        "i", "医科診療報酬点数表",
        "s", "歯科診療報酬点数表",
        "t", "調剤診療報酬点数表",
        "k", "施設基準");

    public final String inDir, outDir;
    public final String 元号;
    public final String 年度;
    public final String 旧元号;
    public final String 旧年度;
    
    Param(String inDir, String outDir, Nendo n) {
        this.inDir = inDir;
        this.outDir = outDir;
        this.元号 = n.元号;
        this.年度 = n.年度;
        this.旧元号 = n.旧元号;
        this.旧年度 = n.旧年度;
    }
    
    public static Param of(String inDir, String outDir, String 年度) {
        return new Param(inDir, outDir, ALL.get(年度));
    }
    
    public static Param of(String 年度) {
        return of("in", "out", 年度);
    }
    
    public static String[] files(Path dir, String ext) throws IOException {
        return Files.walk(dir, 1)
            .filter(p -> p.getFileName().toString().toLowerCase().endsWith(ext))
            .sorted(Comparator.comparing(Path::getFileName))
            .map(p -> p.toString())
            .toArray(String[]::new);
    }

    public String[] pdf(String 点数表, String type) throws IOException {
        return files(Path.of(inDir, 年度, 点数表, "pdf", type), ".pdf");
    }

    public String txt(String 点数表, String name) {
        return Path.of(inDir, 年度, 点数表, "txt", name + ".txt").toString();
    }

    public String inHomeDir() {
        return Path.of(inDir, "home").toString();
    }

    public String outHomeDir() {
        return outDir;
    }

    public String outDir() {
        return Path.of(outDir, 年度).toString();
    }

    public String outFile(String fileName) {
        return Path.of(outDir(), fileName).toString();
    }

    public String outDir(String 点数表) {
        return Path.of(outDir, 年度, 点数表).toString();
    }

    public String outFile(String 点数表, String fileName) {
        return Path.of(outDir(点数表), fileName).toString();
    }
    
    public String title(String 点数表) {
        return "%s%s年%s".formatted(元号, 年度, TITLES.get(点数表));
    }
}
