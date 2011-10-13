set dir=%CD:\=\\%
@echo mcroot=%dir%>config\build.properties
@echo mcc=${mcroot}\\bin\\mcc.exe>>config\build.properties
@echo mclib=${mcroot}\\lib\\se>>config\build.properties
@echo mcinterfaces=${mcroot}\\..\\interfaces>>config\build.properties
@echo junit=${mcroot}\\lib\\junit-4.4.jar>>config\build.properties
