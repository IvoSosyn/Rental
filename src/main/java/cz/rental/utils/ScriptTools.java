/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.utils;

import cz.rental.aplikace.evidence.EviForm;
import java.text.ParseException;
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

    public Throwable run(String script) {
        this.script = script;
        if (this.engine == null) {
            return null;
        }
        if (this.script == null) {
            return null;
        }
        transformScript(this.script);
        try {
            this.engine.put("js", this);
            this.engine.eval(this.script);
        } catch (ScriptException ex) {
            Logger.getLogger(ScriptTools.class.getName()).log(Level.SEVERE, null, ex);
            if (this.eviForm instanceof EviForm) {
                return new Throwable(script, ex);
            }
        }
        return null;
    }

    private boolean transformScript(String script) {
        boolean lOk = false;
        String regex = "\\{[^\\{][^\\}]{2,}\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(script);
        int i = 0;
        while (matcher.find()) {
            System.out.println((++i) + ". =" + script.substring(matcher.start(), matcher.end()));
        }
        return lOk;
    }

    /**
     * Metoda ulozi hodnotu 'value' s klicem 'klic' do matice hodnot v EviForm
     *
     * @param klic - nazev pole, do ktereho se ma ulozit hodnota
     * @param value - hodnota k ulozeni - je provedenena automaticak konverze
     * String to Date a String to Double
     * @return bylo*li ulozeni uspesne=true
     */
    public boolean set(Object klic, Object value) {
        boolean lOk = false;
        if (this.eviForm != null) {
            try {
                this.eviForm.setFromScrtipt(klic, value);
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

        st.transformScript("js.set('n.nazev',{n.prijmeni}+{n.jmeno}+{n.titul}+','+{n.titulza})");
//
//        // create a JavaScript engine
//        // evaluate JavaScript code from String
//        System.out.println("ScriptEngine: " + st.engine.toString());
//        st.engine.put("js", st);
//        try {
//            Object test1 = st.engine.eval("print('RushMoore')");
//            Object test = st.engine.eval("var x=6;var y=38; print(js.retezec); print('2. řádek');print('3.radek :'+(x+y));js.set('p.pozn',(x+y))");
//            System.out.println("test=" + test);
//        } catch (ScriptException e) {
//            System.out.println(" e.getMessage(): " + e.getMessage());
//        }
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
