3. Zahájeny práce na stromu šablon
2. Hlavní menu - vyladit
1. Pokus o hlavní menu

WildFly - nutné změny
=======
- persistence doc: https://docs.jboss.org/author/display/WFLY10/JPA%20Reference%20Guide.html#91947210_JPAReferenceGuide-Determinethepersistenceprovidermodule
- module eclipse-persistence doplnen o "eclipselink-2.7.7.jar" - podle návodu z dokumentace výše "Using EclipseLink"
- v konfiguraci "standalone.xml" zrušen řádek s poolem 
- POZOR nelze uzavírat EntityManager - to si vyhrazuje WildFly persistent manager

# Rental
Webová aplikace k evideci a výpočtu nájemného.

