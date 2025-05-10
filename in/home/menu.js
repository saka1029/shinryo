(function() {
    const menu = document.getElementById("menu");
    if (menu == null) {
        console.log("no menu");
        return;
    }
    // console.log("location.href=" + location.href);
    // console.log("location.pathname=" + location.pathname);
    const inFrame = window != window.parent;
    // console.log(`frame=${inFrame}`);
    // iframe内の場合はページ先頭にメニューを表示しない。
    if (inFrame) {
        menu.hidden = true;
        return;
    }
    const 最新年度 = "06";
    const PREV = {"06": "04", "04": "02", "02": null};
    const path = location.pathname.replace(/^.*\/(\d\d\/.\/.*)\.html$/, "$1").split("/");
    const 年度 = path[0];
    const 前年度 = PREV[年度];
    const 点数表 = path[1];
    const 種類 = path[2];
    // console.log(`年度=${年度} 点数表=${点数表} 種類=${種類} 最新版=${年度 == 最新年度} 前年度=${PREV[年度]} frame=${inFrame}`);
    var links = "";
//    links += ""
//        + "<form id='cse-search-box' action='http://google.com/cse'>\n"
//        + "    <input type='hidden' name='cx' value='a6474cacc7567404b' />\n"
//        + "    <input type='hidden' name='ie' value='UTF-8' />\n"
//        + "    <input type='text' name='q' size='50' placeholder='サイト内検索' />\n"
//        + "    <input type='submit' name='sa' value='検索' />\n"
//        + "    <img src='http://www.google.com/cse/images/google_custom_search_smwide.gif' align='middle'>\n"
//        + "</form>\n";
    links += " <a href='../../index.html' target='_top'>ホーム</a>";
    if (点数表 == "k") {
        links += " <a href='index.html'>告示</a>";
        links += " <a href='tuti.html'>通知</a>";
    } else {
        links += " <a href='index.html'>本文</a>";
        links += " <a href='kubun.html'>区分</a>";
    }
    links += " <a href='yoshiki.html'>様式</a>";
    if (年度 != 最新年度) {
        links += " <a href='../../" + 最新年度 + "/" + 点数表 + "/" + 種類 + ".html'>"
            + "<font color='red'>最新版</font></a>";
        window.onload = function() {
            alert("このページは旧版です。「最新版」をクリックしてください。");
        };
    }
    if (前年度 != null && 種類 != "kubun") {
        links += " <a href='../../hikaku.html"
            + "?l=" + 前年度 + "/" + 点数表 + "/" + 種類 + ".html"
            + "&r=" + 年度 + "/" + 点数表 + "/" + 種類 + ".html' target='_top'>旧版と比較</a>";
    }
    menu.innerHTML = links;
})();