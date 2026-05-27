package saka1029.shinryo.pdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;

import saka1029.shinryo.common.Common;

public class 様式分割 {

    static final Logger logger = Common.logger(様式分割.class);

    record FilePage(File file, int page) {
        FilePage(String file, int page) {
            this(new File(file), page);
        }
        FilePage(String file, String page) {
            this(new File(file), Integer.parseInt(page));
        }
    }

    // final String yeTxt, ysTxt;
    final Map<FilePage, FilePage> map = new HashMap<>();

    public 様式分割(String yeTxt, String ysTxt) throws IOException {
        // this.yeTxt = yeTxt;
        // this.ysTxt = ysTxt;
        Map<FilePage, FilePage> subst = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(ysTxt), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) continue;
                String[] f = line.split(",");
                subst.put(new FilePage(f[0], f[1]), new FilePage(f[2], f[3]));
            }
        }
        try (BufferedReader reader = Files.newBufferedReader(Path.of(yeTxt), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) continue;
                String[] f = line.split(",");
                int start = Integer.parseInt(f[3]);
                int end = Integer.parseInt(f[4]);
                for (int i = start; i <= end; ++i) {
                    FilePage org = new FilePage(f[0], i);
                    FilePage sub = subst.get(org);
                    if (sub != null)
                        map.put(org, sub);
                }
            }
        }
    }

    public void split(String file, Path outFile, int startPage, int endPage) throws IOException {
        Map<File, PDDocument> opened = new HashMap<>();
        try {
            logger.info("outFile=" + outFile);
            try (PDDocument doc = new PDDocument()) {
                for (int i = startPage; i <= endPage; ++i) {
                    FilePage filePage = new FilePage(file, i);
                    if (map.containsKey(filePage))
                        filePage = map.get(filePage);
                    logger.info("addPage=" + filePage);
                    PDDocument source = opened.get(filePage.file);
                    if (source == null) {
                        source = PDDocument.load(filePage.file);
                        opened.put(filePage.file, source);
                    }
                    doc.addPage(source.getPage(filePage.page - 1));
                }
                doc.save(outFile.toFile());
            }
        } finally {
            for (PDDocument d : opened.values())
                d.close();
        }
    }

}
