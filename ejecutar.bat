@echo off
cd /d "%~dp0"
javac -encoding UTF-8 -cp "lib/*;src" -d bin src/paquelibre/excepciones/*.java src/paquelibre/modelo/*.java src/paquelibre/patron/*.java src/paquelibre/persistencia/*.java src/paquelibre/vista/*.java src/paquelibre/Main.java src/paquelibre/CasosPrueba.java
java -cp "lib/*;bin" paquelibre.Main
pause
