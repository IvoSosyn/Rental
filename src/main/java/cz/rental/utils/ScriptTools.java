/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.utils;

import javax.script.*;

/**
 * https://docs.oracle.com/javase/7/docs/technotes/guides/scripting/programmer_guide/
 *
 * @author ivo
 */
public class ScriptTools {

    private String retezec = "Test";

    public double celkem(double... cinitel) {
        double celkem = 0d;
        for (double cinitel1 : cinitel) {
            celkem += cinitel1;
        }
        return celkem;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public static void main(String[] args) throws Exception {

        // create a script engine manager
        ScriptEngineManager factory = new ScriptEngineManager();
        for (ScriptEngineFactory sef : factory.getEngineFactories()) {
            System.out.println(" ScriptEngineFactory.getEngineName(): " + sef.getEngineName());
            System.out.println(" ScriptEngineFactory.getLanguageName: " + sef.getLanguageName());
            for (String scriptEngineName : sef.getNames()) {
                System.out.println(" ScriptEngine.getNames(): " + scriptEngineName);
            }
        }

        // create a JavaScript engine
        ScriptEngine engine = (ScriptEngine) factory.getEngineByName("JavaScript");
        // evaluate JavaScript code from String
        System.out.println("ScriptEngine: " + engine.toString());
        engine.put("scrTools", new ScriptTools());
        try {
            Object test1 = engine.eval("print('RushMoore')");
            Object test = engine.eval("print(scrTools.retezec)");
            System.out.println(" scrTools.celkem(...): " + engine.eval("scrTools.celkem(10.0,20.0,30.0)"));
        } catch (ScriptException e) {
            System.out.println(" e.getMessage(): "+e.getMessage());
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

}
