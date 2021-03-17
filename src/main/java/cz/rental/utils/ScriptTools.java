/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.utils;

import cz.rental.aplikace.evidence.EviAttrValue;
import cz.rental.aplikace.evidence.EviForm;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.*;

/**
 * https://docs.oracle.com/javase/7/docs/technotes/guides/scripting/programmer_guide/
 *
 * @author ivo
 */
public class ScriptTools {

    private ScriptEngineManager factory = null;
    private ScriptEngine engine = null;
    private String script = null;
    private String retezec = "1. radek scriptu";
    EviForm eviForm = null;
    ArrayList<EviAttrValue> values = new ArrayList<>();

    public ScriptTools() {
        this(null);
    }

    public ScriptTools(EviForm eviForm) {
        factory = new ScriptEngineManager();
        engine = factory.getEngineByName("Nashorn");
        if (engine == null) {
            engine = factory.getEngineByName("JavaScript");
        }
        this.eviForm = eviForm;
    }

    public Throwable validate(String attrName, Object currentValue, ArrayList<EviAttrValue> values) {
        String transScript;
        Pattern pattern;
        Matcher matcher;
        String message;
        ArrayList<String[]> parsers = new ArrayList<>();
        if (!(values instanceof ArrayList) || values.isEmpty()) {
            return null;
        }
        this.values = values;
        for (EviAttrValue value : values) {
            if (value.getAttribute().getAttrname().equalsIgnoreCase(attrName)) {
                if (value.getAttribute().getAttrparser() != null && !value.getAttribute().getAttrparser().isEmpty()) {
                    String[] rows = value.getAttribute().getAttrparser().split("[\n;]");
                    for (String row : rows) {
                        if (!row.trim().isEmpty() && !row.trim().startsWith("/*")&& !row.trim().startsWith("//")) {
                            parsers.add(row.split("[|]"));
                        }
                    }
                }
                break;
            }
        }

        for (String[] parser : parsers) {
            // Neni podminka k validaci
            if (parser.length == 0 || parser[0].trim().isEmpty()) {
                continue;
            }
            // Ostrit, zda-li je zadana hlaska v prpade neuspesne validace
            if (parser.length > 1) {
                message = parser[1];
            } else {
                message = "'Nesprávná hodnota: {0}'";
            }
            // Vytvorim a transformuji oznameni pro pripad neuspesneho testu
            String transMessage = this.transformScript(message, attrName, currentValue);
            System.out.println(" Message: " + parser[1] + " TransMessage: " + transMessage);
            this.engine.put("js", this);
            try {
                // Normalizovat oznameni pred provedenim
                transMessage=(!(transMessage.startsWith("\"") || transMessage.startsWith("'"))?"\"":"")+transMessage+(!(transMessage.endsWith("\"") || transMessage.endsWith("'"))?"\"":"");
                transMessage = (String) this.engine.eval(transMessage);
            } catch (ScriptException ex) {
                Logger.getLogger(ScriptTools.class.getName()).log(Level.SEVERE, null, ex);
                transMessage = ex.getMessage();
            }
            message = transMessage;
            // Vlastni validace podminky - parseru
            System.out.println(" Parser: " + parser[0]);
            if (parser[0].matches(".*[js\\.][a-zA-Z]+[(].*")) {
                transScript = this.transformScript(parser[0], attrName, currentValue);
                System.out.println(" parser[0]: " + parser[0] + " TarnsParser: " + transScript);
                this.engine.put("js", this);
                try {
                    boolean isValid = (boolean) this.engine.eval(transScript);
                    if (!isValid) {
                        return new Throwable(message);
                    }
                } catch (ScriptException ex) {
                    Logger.getLogger(ScriptTools.class.getName()).log(Level.SEVERE, null, ex);
                    return new Throwable(ex);
                }

            } else {
                if (currentValue==null) {
                    currentValue="";
                }
                pattern = Pattern.compile(parser[0].trim());
                matcher = pattern.matcher(currentValue.toString());
                if (!matcher.matches()) {
                    return new Throwable(message);
                }
            }
        }
        return null;
    }

    /**
     * Metoda provede volani JavScript(Nashorn)engine nad Attribute.script
     * 1.nejdrive se provede transformace parametru '{parametr}' na hodnotu
     * vyuzitim matice hodnot 'values' 2. nasledne se vola
     * JavScript(Nashorn)engine.eval k provedeni transformovaneho scriptu
     *
     * @param attrName - jmeno aktualne editovaneho Attribute
     * @param currentValue - hodnota aktualne editovaneho Attribute
     * @param values - matice hodnot, ve ktere se maji hledat hodnoty parametru
     * '{parametr}'
     *
     * @return hlaseni, pokud se neco nepovede
     */
    public Throwable run(String attrName, Object currentValue, ArrayList<EviAttrValue> values) {
        String transScript;
        if (this.engine == null) {
            return null;
        }
        if (!(values instanceof ArrayList) || values.isEmpty()) {
            return null;
        }
        this.values = values;
        for (EviAttrValue value : values) {
            if (value.getAttribute().getAttrname().equalsIgnoreCase(attrName)) {
                this.script = value.getAttribute().getScript();
                break;
            }
        }
        if (this.script == null || this.script.trim().isEmpty()) {
            return null;
        }
        transScript = transformScript(script, attrName, currentValue);
        try {
            this.engine.put("js", this);
            this.engine.eval(transScript);
        } catch (ScriptException ex) {
            Logger.getLogger(ScriptTools.class.getName()).log(Level.SEVERE, null, ex);
            return new Throwable(transScript, ex);
        }
        return null;
    }

    /**
     * Metoda provede transformaci parametru na hodnoty z matice 'this.values'
     * (zohledni hodnotu 'currentValue' pro parametr jmenem 'attrName') -
     * zohlednuje se String, Double a Date hodnota
     *
     * @param script - originalni 'script' k transformaci
     * @param attrName - jmeno Attribute jehoz 'currentValue' se ma dosdit misto
     * vyhledani v matici 'this.values'
     * @param currentValue - hodnota pro Attribute.attrname, ktera se ma dosadit
     * misto hodnoty z matice 'this.values'
     * @return - transformovany script vhodny k provedeni v JavaScript engine
     */
    private String transformScript(String script, String attrName, Object currentValue) {
        Object value;
        String transScript = script;
        String localAtrrName;
        String regex = "[\\{][^\\}]*[\\}]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(script);
        int i = 0;
        while (matcher.find()) {
            String field = script.substring(matcher.start(), matcher.end());
            // System.out.println((i++) + ". =" + field);
            String[] attrNames = field.replace("{", "").replace("}", "").trim().split("\\.");
            localAtrrName = attrNames[attrNames.length - 1];
            if (localAtrrName == null || localAtrrName.isEmpty() || localAtrrName.equals("0") || localAtrrName.equalsIgnoreCase(attrName)) {
                value = currentValue;
            } else {
                value = this.getValue(localAtrrName);
            }
            if (value == null) {

            } else if (value instanceof String) {
                transScript = transScript.replace(field, "'" + value + "'");
            } else if (value instanceof Date) {
                transScript = transScript.replace(field, "'" + value + "'");
            } else {
                transScript = transScript.replace(field, "" + value);
            }
        }
        return transScript;
    }

    /**
     * Metoda vrati hodnotu z matice hodnot 'this.values' pro
     * Attribute.attrname==attrName
     *
     * @param attrName nazev klice Attribute.attrname, pro ktery hledam
     * odpovidajici hodnotu
     * @return vracena hodnota proklic 'attrName' z matice hodnot 'this.values'
     */
    private Object getValue(String attrName) {
        Object value = null;
        for (EviAttrValue eviAttrValue : this.values) {
            if (eviAttrValue.getAttribute().getAttrname().equalsIgnoreCase(attrName)) {
                value = eviAttrValue.getValue();
                if (value!=null) {
                    
                }else if(eviAttrValue.getAttribute().getAttrtype()=='C'){
                    value="";
                }else if(eviAttrValue.getAttribute().getAttrtype()=='I'){
                    value=0;
                }if(eviAttrValue.getAttribute().getAttrtype()=='D'){
                    value=0.0d;
                }                    
                break;
            }
        }
        return value;
    }

    /**
     * Metoda poskytne prevodnik String to Date pro funkce JavaScriptu
     *
     * @param dateAsString - vstupní datum jako retezec
     * @return vystupni hodnota jako třída 'java.util.Date'
     *
     * @throws ParseException chyba, pokud se změna nepovede
     */
    public Date getDateValue(String dateAsString) throws ParseException {
        Date dateAsDate = null;
        if (dateAsString instanceof String && !dateAsString.trim().isEmpty()) {
            Aplikace.getSimpleDateFormat().parse(dateAsString);
        }
        return dateAsDate;
    }

    public boolean cmp(String string1, String string2) {
        if (string1 instanceof String && string2 instanceof String) {
            return string1.trim().compareTo(string2.trim()) == 0;
        } else {
            return false;
        }
    }

    public boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        } else if (object instanceof String) {
            return ((String) object).trim().isEmpty();
        }
        if (object instanceof Double) {
            return ((Double) object) == 0.0d;
        }
        if (object instanceof Integer) {
            return ((Integer) object) == 0.0d;
        }
        return false;
    }

    /**
     * Metoda ulozi hodnotu 'value' s klicem 'klic' do matice hodnot v EviForm
     *
     * @param klic - nazev pole, do ktereho se ma ulozit hodnota - muze byt i ve
     * tvaru 'Typentity#typentity.Attribute#attrname' napr. 'n.prijmeni'
     * @param value - hodnota k ulozeni - je provedenena automaticak konverze
     * String to Date a String to Double
     * @return bylo-li ulozeni uspesne=true
     */
    public boolean set(String klic, Object value) {
        boolean lOk = false;
        String[] fields;
        String field;
        if (this.eviForm != null && klic instanceof String) {
            try {
                fields = klic.split("\\.");
                field = fields[fields.length - 1];
                this.setFromScrtipt(field, value);
                lOk = true;
            } catch (ParseException ex) {
                Logger.getLogger(ScriptTools.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (!lOk) {
            System.out.println(" this.set#klic:" + klic);
            System.out.println(" this.set#value: " + value);

        }
        return lOk;
    }

    /**
     * Metoda dosadi hodnotu 'value' do formularove promenne definovane hodnotou
     * 'klic' Metoda provadi automatickou konverzi z retezce 'String' na cislo
     * 'Double'
     *
     * @param attrName jmeno Attribute definujiciho konkrétní člena matice
     * 'this.values' , kam dosadit hodnotu 'value'
     * @param value hodonta k dosazeni
     * @throws java.text.ParseException - chyba v pripaded konverze retecove
     * hodnoty 'value' na datum
     */
    private void setFromScrtipt(String attrName, Object value) throws ParseException {
        if (attrName instanceof String) {
            for (EviAttrValue eviAttrValue : this.values) {
                if (eviAttrValue.getAttribute().getAttrname().equalsIgnoreCase(attrName)) {
                    if ((eviAttrValue.getAttribute().getAttrtype() == 'I' || eviAttrValue.getAttribute().getAttrtype() == 'N') && value instanceof String) {
                        eviAttrValue.setValue(Double.valueOf((String) value));
                    } else if (eviAttrValue.getAttribute().getAttrtype() == 'D' && value instanceof String) {
                        eviAttrValue.setValue(Aplikace.getSimpleDateFormat().parse((String) value));
                    } else {
                        eviAttrValue.setValue(value);
                    }
                    break;
                }
            }
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public static void main(String[] args) throws Exception {
        ScriptTools st = new ScriptTools();
//
//        // create a script engine manager
//        for (ScriptEngineFactory sef : st.factory.getEngineFactories()) {
//            System.out.println(" ScriptEngineFactory.getEngineName(): " + sef.getEngineName());
//            System.out.println(" ScriptEngineFactory.getLanguageName: " + sef.getLanguageName());
//            for (String scriptEngineName : sef.getNames()) {
//                System.out.println(" ScriptEngine.getNames(): " + scriptEngineName);
//            }
//        }

//        st.transformScript("js.set('n.nazev',{n.prijmeni}+{n.jmeno}+{n.titul}+','+{n.titulza})", null, null);
//
        // create a JavaScript engine
        // evaluate JavaScript code from String
        System.out.println("ScriptEngine: " + st.engine.toString());
        st.engine.put("js", st);
        try {
            Object test = st.engine.eval("js.cmp('ěščřžýáíé','ěščřžýáíé')");
            test = st.engine.eval("!js.isEmpty(' ')");
            System.out.println("test=" + test);
        } catch (ScriptException e) {
            System.out.println(" e.getMessage(): " + e.getMessage());
        }
    }

    /**
     * @return the retezec
     */
    public String getRetezec() {
        return retezec;
    }

    /**
     * @param retezec the retezec to set
     */
    public void setRetezec(String retezec) {
        this.retezec = retezec;
    }

    /**
     * @return the script
     */
    public String getScript() {
        return script;
    }

    /**
     * @param script the script to set
     */
    public void setScript(String script) {
        this.script = script;
    }
}
