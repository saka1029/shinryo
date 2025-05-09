@echo off
setlocal

set OUT_DIR=../s
set BASE_URL=https://saka1029.github.io/s

java -Djava.util.logging.config.file=logging.properties ^
    -cp target/classes;target/dependency/* ^
    saka1029.shinryo.main.Main ^
    -o %OUT_DIR% ^
    -b %BASE_URL% ^
    %*

endlocal

