function normalizeSearchWord(rawWord) {
    const searchWord = rawWord
        .replace(/([\u002d\u30fc\uff0d])|([A-Za-z0-9])|([\u3041-\u3096])|([\u30a1-\u30f6])/g,
            (match, hyphen, hankaku, hiragana, katakana) => {
                if (hyphen != undefined)    // \u002d: 半角ハイフン、\u30fc: 長音記号、\uff0d: 全角ハイフン
                    return "[\u002d\u30fc\uff0d]";
                else if (hankaku != undefined) // 半角英数字の場合は全角も含める
                    return `[${match}${String.fromCharCode(match.charCodeAt(0) + 0xFEE0)}]`;
                else if (hiragana != undefined) // ひらがなの場合はカタカナも含める
                    return `[${match}${String.fromCharCode(match.charCodeAt(0) + 0x60)}]`;
                else                         // カタカナの場合はひらがなも含める
                    return `[${match}${String.fromCharCode(match.charCodeAt(0) - 0x60)}]`;
        });
    return searchWord;
}

const rawSearchWord = "0";
const searchWord = normalizeSearchWord(rawSearchWord);
const contentRegexp = new RegExp(`(?<=>[^<]*)(${searchWord})`, "gi");
// const rawRegexp = new RegExp(searchWord, "ig");
console.log(`rawSearchWord=${rawSearchWord}`);
console.log(`searchWord=\"${searchWord}\"`);
console.log(`contentRegexp=${contentRegexp}`);
// console.log(`rawRegexp=${rawRegexp}`);
const searchParas = [
"<p style='margin-left:5.5em;text-indent:-5.5em'><!-- 000907834.pdf:3 ke.txt:118 --><a href='../../04/i/A000.html'>旧</a> <!-- 001218731.pdf:3 ke.txt:119 --><a href='A000.html'>新</a> <a href='../../hikaku.html?l=04/i/A000.html&r=06/i/A000.html'>比較</a> Ａ０００ 初診料</p>",
"<p style='margin-left:5.5em;text-indent:-5.5em'><!-- 000907834.pdf:5 ke.txt:215 --><a href='../../04/i/A001.html'>旧</a> <!-- 001218731.pdf:5 ke.txt:222 --><a href='A001.html'>新</a> <a href='../../hikaku.html?l=04/i/A001.html&r=06/i/A001.html'>比較</a> Ａ００１ 再診料</p>",
"<p style='margin-left:5.5em;text-indent:-5.5em'><!-- 000907834.pdf:6 ke.txt:312 --><a href='../../04/i/A002.html'>旧</a> <!-- 001218731.pdf:7 ke.txt:333 --><a href='A002.html'>新</a> <a href='../../hikaku.html?l=04/i/A002.html&r=06/i/A002.html'>比較</a> Ａ００２ 外来診療料</p>",
"<p style='margin-left:5.5em;text-indent:-5.5em'>－ － －－ Ａ００３ 削除</p>",
];
for (const partEl of searchParas) { 
    const textContent = partEl.replace(/<[^>]*>/g, "");
    console.log(`text:${textContent}`);
    if (new RegExp(searchWord, "gi")) {
        const result = partEl.replace(
            contentRegexp,
            (partMatch) => `[${partMatch}]`
        );
        console.log(`show: ${result}`);
    } else {
        console.log(`hide: ${partEl}`);
    }
}