IF NOT EXIST "config\build.properties" (
  CALL config\mkconfig.bat
)
IF NOT EXIST "tools\configure.jar" (
  CD tools\config
  CALL ant
  CD ..\..
)
java -jar tools\configure.jar -lib
