const rexs = "[0０]";
const rex = new RegExp(rexs, "gi");
const input = ["Ａ０００", "旧 新 比較 Ａ００２ 外来診療料"];
for (const s of input)
    console.log(s.replace(rex, (x) => `[${x}]`));