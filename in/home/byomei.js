function encode(text) {
    const length = text.length;
    const result = [];
    const sequence = [];

    function search(start) {
        if (start >= length) {
            if (sequence.filter(e => e.data.length == 7).length == 1)
                result.push(sequence.slice());
        } else
            for (let i = start + 1; i <= length; ++i) {
                const key = text.substring(start, i);
                const data = BYOMEI[key];
                // console.log(`start=${start}, i=${i}, key=${key}, data=${data}`)
                if (data === undefined)
                    continue;
                sequence.push({key: key, data: data});
                search(i);
                sequence.pop();
            }
    }
    search(0);
    return result;
}

function clearInput() {
    const input = document.getElementById("byomei-word-input");
    input.value = "";
    input.focus();
    run();
}

function run() {
    const input = document.getElementById("byomei-word-input").value;
    // alert(input);
    const result = encode(input);
    // console.log(`input=${input} result=${result}`);
    // let str = JSON.stringify(result);
    let str = "";
    for (const line of result) {
        for (const e of line)
            str += ` ${e.data}:${e.key}`
        str += "\n";
    }
    // alert(str);
    document.getElementById("result").innerText = str;
}