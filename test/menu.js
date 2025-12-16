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
    if (年度 != 最新年度) {
        links += ` <a href='../../${最新年度}/${点数表}/${種類}.html'><font color='red'>最新版</font></a>`;
        window.onload = function() { alert("このページは旧版です。「最新版」をクリックしてください。"); };
    }
    if (点数表 == "k") {
        links += " <a href='index.html'>告示</a>";
        links += " <a href='tuti.html'>通知</a>";
    } else if (種類 == "index-single" || 種類 == "kubun-single" || 種類 == "yoshiki-single") {
        links += " <a href='index-single.html'>本文</a>";
        links += " <a href='kubun-single.html'>区分</a>";
    } else {
        links += " <a href='index.html'>本文</a>";
        links += " <a href='kubun.html'>区分</a>";
    }
    if (種類 == "index-single" || 種類 == "kubun-single" || 種類 == "yoshiki-single")
        links += " <a href='yoshiki-single.html'>様式</a>";
    else
        links += " <a href='yoshiki.html'>様式</a>";
    if (前年度 != null && 種類 != "kubun" && 種類 != "index-single" && 種類 != "kubun-single" && 種類 != "yoshiki-single") {
        links += ` <a href='../../hikaku.html?l=${前年度}/${点数表}/${種類}.html`
            + `&r=${年度}/${点数表}/${種類}.html' target='_top'>旧版と比較</a>`;
    }
    links += " <span id='search-word-box'>"
            + "<input id='search-word-input' type='text' placeholder='ページ内検索'/>"
            + "<span id='search-clear-button'><img src='../../clear-button-in-textbox.svg'/></span>"
            + "</span>";
    menu.innerHTML = links;

    // ページ内検索機能
    // see https://mo2nabe.com/js-search-in-page/
    const searchParas = document.getElementsByTagName('p');
    const searchHighs = document.getElementsByClassName('s-highlight');
    const searchHides = document.getElementsByClassName('s-hide');
    const searchWordInput = document.getElementById('search-word-input');
    const searchClearButton = document.getElementById('search-clear-button');

    searchWordInput.addEventListener('input', searchHighlight);
    searchClearButton.addEventListener('click', function() {
        searchWordInput.value = "";
        searchWordInput.focus();
        searchHighlight();
    });

    function shiftChar(c, offset) {
        return String.fromCharCode(c.charCodeAt(0) + offset);
    }

    function normalizeSearchWord(rawWord) {
        const searchWord = rawWord
            .replace(/([\u002d\u30fc\uff0d])|([\u0021-\u007E])|([\uFF01-\uFF5E])|([\u3041-\u3096])|([\u30a1-\u30f6])/g,
                (match, hyphen, hankaku, zenkaku, hiragana, katakana) => {
                    if (hyphen != undefined)    // \u002d: 半角ハイフン、\u30fc: 長音記号、\uff0d: 全角ハイフン
                        return `[\u002d\u30fc\uff0d]`;
                    else if (hankaku != undefined) // 半角英数字の場合は全角も含める
                        return `[\\${match}${shiftChar(match, 0xFEE0)}]`;
                    else if (zenkaku != undefined) // 全角英数字の場合は半角も含める
                        return `[${match}\\${shiftChar(match, -0xFEE0)}]`;
                    else if (hiragana != undefined) // ひらがなの場合はカタカナも含める
                        return `[${match}${shiftChar(match, 0x60)}]`;
                    else                         // カタカナの場合はひらがなも含める
                        return `[${match}${shiftChar(match, -0x60)}]`;
            });
        return searchWord;
    }

    function searchHighlight() {
        //リセット処理
        for (const el of [...searchHighs])  // ハイライト削除
            el.outerHTML = el.textContent;
        for (const el of [...searchHides])  // 除外した行を復活
            el.classList.remove('s-hide');
        //本処理
        const rawSearchWord = searchWordInput.value;
        if (rawSearchWord === '') return;
        // 検索文字列を正規化する
        const searchWord = normalizeSearchWord(rawSearchWord);
        // textContent検索用の正規表現
        const rawRegexp = new RegExp(searchWord, "gi");
        // innerHTMLのテキスト部分だけにマッチするように調整する
        // ex) '(?<=AA)BB' : 直前にAAがあるBBのみにマッチする。
        //     ただしAAはマッチする対象に含まれない。
        const contentRegexp = new RegExp(`(?<=>[^<]*)(${searchWord})`, "gi");
        for (const partEl of [...searchParas]) {
            // RegExpの状態をリセットする
            rawRegexp.lastIndex = 0;
            if (rawRegexp.test(partEl.textContent))
                partEl.outerHTML = partEl.outerHTML.replace(
                    contentRegexp,
                    (partMatch) => `<span class="s-highlight">${partMatch}</span>`
                );
            else
                partEl.classList.add('s-hide');
        }
    }
})();
