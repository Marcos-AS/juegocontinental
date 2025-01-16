@echo off
dir /s /B *.java > sources.txt
javac @sources.txt -d out
if %ERRORLEVEL% neq 0 (
	echo Error durante la compilación.
	pause
	exit /b
)

start java -cp out main.AppClienteConsola
pause