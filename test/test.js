const contentRegexp = /(?<=>[^<]*)([-ー－])/gi;
const html = "<p style='margin-left:5.5em;text-indent:-5.5em'>－ － －－ Ａ００３ 削除</p>";
const result = html.replace(
        contentRegexp,
        (match) => {
            return `<span class='hide'>${match}</span>`;
        });
console.log(`result=${result}`);