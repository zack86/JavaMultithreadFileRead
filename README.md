===== 編譯 =====
javac -encoding utf-8 *.java

===== 執行 =====
java SingleThread <輸入log檔案位置> <輸出檔案位置>
java MultiThread1 <輸入log檔案位置> <輸出檔案位置> <執行緒數量(建議跟core數量一樣)>
java MultiThread2 <輸入log檔案位置> <輸出檔案位置> <執行緒數量(建議跟core數量一樣)>

Ex. java SingleThread ./log20170630.csv ./iplist.csv