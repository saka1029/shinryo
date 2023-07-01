package saka1029.shinryo.pdf;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * PDFファイルの指定ページを別のPDFファイルに抽出します。
 */
public class PDFSplitter implements Closeable {

    PDDocument doc = null;
    File docFile = null;

    void openDoc(String inPdfFile) throws IOException {
        File inFile = new File(inPdfFile);
        if (inFile.equals(docFile))
            return;
        if (doc != null)
            doc.close();
        docFile = inFile;
        doc = PDDocument.load(inFile);
    }

    /**
     * inPdfFileのstartPageからendPageをoutPdfFileに抽出します。
     */
    public void split(String inPdfFile, String outPdfFile, int startPage, int endPage) throws IOException {
        Files.createDirectories(Path.of(outPdfFile).getParent());
        openDoc(inPdfFile);
        Splitter splitter = new Splitter();
        splitter.setStartPage(startPage);
        splitter.setEndPage(endPage);
        splitter.setSplitAtPage(doc.getNumberOfPages());
        List<PDDocument> splitted = splitter.split(doc);
        try {
            splitted.get(0).save(outPdfFile);
        } finally {
            for (PDDocument d : splitted)
                d.close();
        }
    }

    @Override
    public void close() throws IOException {
        if (doc != null)
            doc.close();
    }
}
