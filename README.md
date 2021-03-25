- 👋 Hi, I’m @IvoSosyn

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
- málo paměti, v souboru "C:\Program Files\wildfly-20.0.0.Final\bin\standalone.conf.bat" nutné přidat paměť "-Xms1024m -Xmx2048m -XX:MetaspaceSize=128M -XX:MaxMetaspaceSize=512m" 
  nebo v nastavení NetBeans>Services>WildFly Application Server>Properties>Platform>VM Options

DB PostgreSQL
============
Datumové položky jsou uloženy s TimeZone, pak se musí u každého JSF "f:convertDateTime" nastavit parametr timeZone="Europe/Prague":
    <f:convertDateTime locale="cs_CZ" type="date" dateStyle="medium" timeZone="Europe/Prague" />

PrimeFaces
==========
   Je nutní založit faces-config.xml a do něj vložit     
    <application>
        <action-listener>
        <navigation-handler>
        </view-handler>
        jinak nefunguje(nezobrazí se)  PrimeFaces.current().dialog().openDynamic(...);
    !!! Stejně to nfunguje !!!

Keyword             Type	Description
@this               Standard	Current component.
@all                Standard	Whole view.
@form               Standard	Closest ancestor form of current component.
@none               Standard	No component.
@namingcontainer    PrimeFaces	Closest ancestor naming container of current component.
@parent             PrimeFaces	Parent of the current component.
@composite          PrimeFaces	Closest composite component ancestor.
@child(n)           PrimeFaces	nth child.
@row(n)             PrimeFaces	nth row.
@previous           PrimeFaces	Previous sibling.
@next               PrimeFaces	Next sibling.
@widgetVar(name)    PrimeFaces	Component with given widgetVar.
@root               PrimeFaces	UIViewRoot instance of the view, can be used to start searching from the root instead the current component.
@id(id              PrimeFaces	Used to search components by their id ignoring the component tree structure and naming containers.


Name              |Default| Type    | Description  
---               | ---   | ---     | ---  
widgetVar         | null  | String  | Custom widgetVar of the dialog, if not declared it will be automatically created as "id+dlgWidget". 
modal             | false | Boolean | Controls modality of the dialog. 
resizable         | true  | Boolean | When enabled, makes dialog resizable. 
draggable         | true  | Boolean | When enabled, makes dialog draggable. 
width             | auto  | String  | Width of the dialog. 
height            | auto  | String  | Height of the dialog. 
contentWidth      | 640   | String  | Width of the dialog content. 
contentHeight     | auto  | String  | Height of the dialog content. 
closable          | true  | Boolean | Whether the dialog can be closed or not. 
includeViewParams | false | Boolean | When enabled, includes the view parameters. 
headerElement     | null  | String  | Client id of the element to display inside header. 
minimizable       | false | Boolean | Makes dialog minimizable. 
maximizable       | false | Boolean | Makes dialog maximizable. 
closeOnEscape     | false | Boolean | Whether the dialog can be closed with escape key. 
minWidth          | 150   | Integer | Minimum width of a resizable dialog. 
minHeight         | 0     | Integer | Minimum height of a resizable dialog. 
appendTo          | null  | String  | Appends the dialog to the element defined by the given search expression. 
dynamic           | false | Boolean | Enables lazy loading of the content with ajax. 
showEffect        | null  | String  | Effect to use when showing the dialog 
hideEffect        | null  | String  | Effect to use when hiding the dialog 
position          | null  | String  | Defines where the dialog should be displayed 
fitViewport       | false | Boolean | Dialog size might exceeed viewport if content is bigger than viewport in terms of height.fitViewport option automatically adjusts height to fit dialog within the viewport. 
responsive        | false | Boolean | In responsive mode, dialog adjusts itself based on screen width. 
focus             | null  | String  | Defines which component to apply focus by search expression. 
onShow            | null  | String  | Client side callback to execute when dialog is displayed. 
onHide            | null  | String  | Client side callback to execute when dialog is hidden.

Vzory RTF pro tisky
===================
http://rtftemplate.sourceforge.net/
https://github.com/ullenboom/jrtf

Vzory DOCX pro tisky
===================
https://poi.apache.org/index.html
https://www.docx4java.org/trac/docx4j