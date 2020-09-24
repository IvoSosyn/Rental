/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.prefs.Preferences;
import javax.swing.JFrame;

/**
 *
 * @author ivo
 */
public class Aplikace {

    private static Locale localeCZ = new Locale("cs", "CZ");
    private static Calendar calendar = Calendar.getInstance(localeCZ);
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", localeCZ);
    private static Date platiOd = null;
    private static Date platiDo = null;
    private static Date zmenaOd = null;
    private static boolean updateLast = false;
    private static JFrame jFrameMain = null;
    private static Properties properties = new Properties();
    private static Preferences preferencesSys = Preferences.systemRoot();
    private static Preferences preferencesUser = Preferences.userRoot();

    static {
        calendar.setTime(new Date());
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        platiOd = new Date(preferencesUser.getLong("najem.platiOd", calendar.getTimeInMillis()));
        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.MINUTE, -1);
        platiDo = new Date(preferencesUser.getLong("najem.platiDo", calendar.getTimeInMillis()));
        zmenaOd = new Date();
    }

    /**
     * @return the localeCZ
     */
    public static Locale getLocaleCZ() {
        return localeCZ;
    }

    /**
     * @param aLocaleCZ the localeCZ to set
     */
    public static void setLocaleCZ(Locale aLocaleCZ) {
        localeCZ = aLocaleCZ;
    }

    /**
     * @return the calendar
     */
    public static Calendar getCalendar() {
        return calendar;
    }

    /**
     * @param aCalendar the calendar to set
     */
    public static void setCalendar(Calendar aCalendar) {
        calendar = aCalendar;
    }

    /**
     * @return the simpleDateFormat
     */
    public static SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }

    /**
     * @param aSimpleDateFormat the simpleDateFormat to set
     */
    public static void setSimpleDateFormat(SimpleDateFormat aSimpleDateFormat) {
        simpleDateFormat = aSimpleDateFormat;
    }

    /**
     * @return the platiOd
     */
    public static Date getPlatiOd() {
        return platiOd;
    }

    /**
     * @param aPlatiOd the platiOd to set
     */
    public static void setPlatiOd(Date aPlatiOd) {
        platiOd = aPlatiOd;
        preferencesUser.putLong("najem.platiOd", platiOd.getTime());
    }

    /**
     * @return the platiDo
     */
    public static Date getPlatiDo() {
        return platiDo;
    }

    /**
     * @param aPlatiDo the platiDo to set
     */
    public static void setPlatiDo(Date aPlatiDo) {
        platiDo = aPlatiDo;
        preferencesUser.putLong("najem.platiDo", platiDo.getTime());
    }

    /**
     * @return the properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(Properties properties) {
        Aplikace.properties = properties;
    }

    /**
     * @return the preferencesSys
     */
    public Preferences getPreferencesSys() {
        return preferencesSys;
    }

    /**
     * @param preferencesSys the preferencesSys to set
     */
    public void setPreferencesSys(Preferences preferencesSys) {
        Aplikace.preferencesSys = preferencesSys;
    }

    /**
     * @return the preferencesUser
     */
    public Preferences getPreferencesUser() {
        return preferencesUser;
    }

    /**
     * @param preferencesUser the preferencesUser to set
     */
    public void setPreferencesUser(Preferences preferencesUser) {
        Aplikace.preferencesUser = preferencesUser;
    }

    /**
     * @return the jFrameMain - hlavni okno(ramec) aplikace
     */
    public static JFrame getJFrameMain() {
        return jFrameMain;
    }

    /**
     * @param aJFrameMain - ulozeni hlavniho okna aplikace k pouziti v dialozich
     */
    public static void setJFrameMain(JFrame aJFrameMain) {
        jFrameMain = aJFrameMain;
    }

    /**
     * @return the zmenaOd
     */
    public static Date getZmenaOd() {
        return zmenaOd;
    }

    /**
     * @param aZmenaOd the zmenaOd to set
     */
    public static void setZmenaOd(Date aZmenaOd) {
        zmenaOd = aZmenaOd;
    }

    /**
     * @return the updateLast
     */
    public static boolean isUpdateLast() {
        return updateLast;
    }

    /**
     * @param aUpdateLast the updateLast to set
     */
    public static void setUpdateLast(boolean aUpdateLast) {
        updateLast = aUpdateLast;
    }

}
