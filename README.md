# Rental #
==========
Webová aplikace k evideci a výpočtu nájemného.

Transakce - tansakce je typu CONTAINER - řídí je WildFly, takže nelze použít lokální uživatelské transakce (BEAN)  trn.begin(), trn.commit(), trn.rollback()
=========     WildFly sám vyhodí exception, pokud se transakce nepovede.  

WildFly - nutné změny
=======
- persistence doc: https://docs.jboss.org/author/display/WFLY10/JPA%20Reference%20Guide.html#91947210_JPAReferenceGuide-Determinethepersistenceprovidermodule
- module eclipse-persistence doplnen o "eclipselink-2.7.7.jar" - podle návodu z dokumentace výše "Using EclipseLink"
- module eclipse-persistence doplnen o <dependencies>...<module name="javax.ws.rs.api"/>...</dependencies> jinak chyba NoClaasDefinitionFound
- v konfiguraci "standalone.xml" zrušen řádek s poolem <datasource-class>org.postgresql.ds.PGSimpleDataSource</datasource-class> 
- POZOR nelze uzavírat EntityManager.close() - to si vyhrazuje WildFly persistent manager

DB PostgreSQL
============
Datumové položky jsou uloženy s TimeZone, pak se musí u každého JSF "f:convertDateTime" nastavit parametr timeZone="Europe/Prague":
    <f:convertDateTime locale="cs_CZ" type="date" dateStyle="medium" timeZone="Europe/Prague" />
