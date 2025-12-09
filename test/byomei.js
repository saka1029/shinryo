const dictionary = {
    a: "A",
    b: "B",
    c: "C",
    ab: "AB",
    bc: "BC",
    abc: "ABC"
}

function encode(text, filter = null) {
    const length = text.length;
    const result = [];
    const sequence = [];

    function search(start) {
        if (start >= length) {
            if (filter == null || filter(sequence))
                result.push(sequence.slice());
        } else
            for (let i = start + 1; i <= length; ++i) {
                const key = text.substring(start, i);
                const data = dictionary[key];
                // console.log(`start=${start}, i=${i}, key=${key}, data=${data}`)
                if (data === undefined)
                    continue;
                sequence.push({start: start, end: i, key: key, data: data});
                search(i);
                sequence.pop();
            }
    }
    search(0);
    return result;
}

function run() {
    const input = document.getElementById("input").value;
    // alert(input);
    const result = encode(input);
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