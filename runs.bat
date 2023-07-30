set OUT_DIR=../s
set BASE_URL=https://saka1029.github.io/s
call run -o %OUT_DIR% 02 i1 i2 s1 s2 t1 t2 k1 k2
call run -o %OUT_DIR% -b %BASE_URL% 04 i1 i2 s1 s2 t1 t2 k1 k2
