/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.utils;

import cz.rental.aplikace.evidence.EviForm;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public static void main(String[] args) throws Exception {
        ScriptTools st = new ScriptTools();

        // create a script engine manager
        for (ScriptEngineFactory sef : st.factory.getEngineFactories()) {
            System.out.println(" ScriptEngineFactory.getEngineName(): " + sef.getEngineName());
            System.out.println(" ScriptEngineFactory.getLanguageName: " + sef.getLanguageName());
            for (String scriptEngineName : sef.getNames()) {
                System.out.println(" ScriptEngine.getNames(): " + scriptEngineName);
            }
        }

        // create a JavaScript engine
        // evaluate JavaScript code from String
        System.out.println("ScriptEngine: " + st.engine.toString());
        st.engine.put("js", st);
        try {
            Object test1 = st.engine.eval("print('RushMoore')");
            Object test = st.engine.eval("var x=6;var y=38; print(js.retezec); print('2. řádek');print('3.radek :'+(x+y));js.set('p.pozn',(x+y))");
            System.out.println("test=" + test);
        } catch (ScriptException e) {
            System.out.println(" e.getMessage(): " + e.getMessage());
        }
    }

    public boolean set(Object klic, Object value) {
        if (this.eviForm != null) {
            this.eviForm.setFromScrtipt(klic, value);
            return true;
        } else {
            System.out.println(" this.set#klic:" + klic);
            System.out.println(" this.set#value: " + value);
            return false;
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
