const rexs = "[0０][0０][0０]";
// const raw = new RegExp(rexs, "gi");
// console.log(`raw=${raw}`);
const input = [
    "Ａ０００",
    "旧 新 比較 Ａ００２ 外来診療料"
];
for (const s of input) {
    const flag = new RegExp(rexs).test(s);
    console.log(`flag=${flag}, ${s}`);
}