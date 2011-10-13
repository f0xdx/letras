@echo off
set dir=%CD:\=\\%
if not exist config mkdir config
echo mcroot=%dir%>config/build.properties
echo mcc=${mcroot}\\bin\\mcc.exe>>config/build.properties
echo mclib=${mcroot}\\lib\\se>>config/build.properties
echo mcinterfaces=${mcroot}\\interfaces>>config/build.properties
type version.txt
echo.
echo Generated config/build.properties with the following settings:
echo.
type config\build.properties
echo.
pause
