
WildFly - nutné změny
=======
- persistence doc: https://docs.jboss.org/author/display/WFLY10/JPA%20Reference%20Guide.html#91947210_JPAReferenceGuide-Determinethepersistenceprovidermodule
- module eclipse-persistence doplnen o "eclipselink-2.7.7.jar" - podle návodu z dokumentace výše "Using EclipseLink"
- module eclipse-persistence doplnen o <dependencies>...<module name="javax.xml.bind.api"/>...</dependencies> jinak chyba NoClaasDefinitionFound
- v konfiguraci "standalone.xml" zrušen řádek s poolem 
- POZOR nelze uzavírat EntityManager.close() - to si vyhrazuje WildFly persistent manager

# Rental
Webová aplikace k evideci a výpočtu nájemného.

