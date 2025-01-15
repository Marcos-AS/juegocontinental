@echo off
echo Compilando archivos Java...
dir /s /B *.java > sources.txt
javac @sources.txt -d out/production/juego-continental
if %ERRORLEVEL% neq 0 (
    echo Error durante la compilación.
    pause
    exit /b
)

echo Iniciando el servidor...
start java -cp out/production/juego-continental main.AppServidor

echo Iniciando instancias del cliente...
start java -cp out/production/juego-continental main.AppClienteGUI
start java -cp out/production/juego-continental main.AppClienteConsola
pause