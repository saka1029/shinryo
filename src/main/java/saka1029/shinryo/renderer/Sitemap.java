package saka1029.shinryo.renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import saka1029.shinryo.common.TextWriter;

public class Sitemap {

	private static void render(Path file, String url, TextWriter w) throws IOException {
	    String name = file.getFileName().toString();
		if (Files.isDirectory(file)) {
			// WindowsとUnixで同じ順番にするため、ファイル名でソートする。
			Path[] children = Files.list(file)
			    .sorted(Comparator.comparing(p -> p.getFileName().toString()))
			    .toArray(Path[]::new);
			for (Path child : children)
				render(child, url + "/" + child.getFileName(), w);
		} else if (!name.equals("sitemap.xml")) {
			w.println("<url><loc>%s</loc></url>", url);
		}
	}
	
	public static void render(String outDir, String baseUrl) throws IOException {
	    baseUrl = baseUrl.replaceFirst("/$", "");
	    Path dir = Path.of(outDir);
		try (TextWriter writer = new TextWriter(dir.resolve("sitemap.xml"))) {
			writer.println("<?xml version='1.0' encoding='UTF-8'?>");
			writer.println("<urlset xmlns='http://www.sitemaps.org/schemas/sitemap/0.9'>");
			render(dir, baseUrl, writer);
			writer.println("</urlset>");
		}
	}
	
}
