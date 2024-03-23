package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.logging.Logger;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;

public class TestSitemap {

    static final Logger logger = Common.logger(TestSitemap.class);
    
    static final Param param = Param.of("in", "debug/html", "04");

//    @Test
    public void testRender() throws IOException {
        String outDir = param.outHomeDir();
        String baseUrl = "http://tensuhyo.test.server.com";
        Sitemap.render(outDir, baseUrl);
    }

}
