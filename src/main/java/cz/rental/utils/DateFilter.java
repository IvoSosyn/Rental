/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.utils;

import cz.rental.admin.users.Users;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Named;

/**
 *
 * @author sosyn
 */
@Named(value = "dateFilter")
@Stateless
public class DateFilter {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    /**
     * Metoda vyhodnoti, zada-li <code>value</code> odpovida podmince dane v
     * <code>filter</code> metoda prijma parametry typu
     *
     *
     * @param value kontrolovana hodnota
     * @param filter podminka filtru, kterou musi <code>value</code> splnovat.
     * <bold>Filtr muze byt typu:</bold>
     * <code>java.util.Date</code><br>
     * <code>java.util.ArrayList</code> - filtr je mnozina o 2 prvcich a je to
     * implicitni tvar pri fitrovani pomoci vestaveneho filtru pro
     * <p:datePicker> a filterMatchMode="range"<br>
     * <code>java.lang.String</code><br>
     * <i><u>Filtr je definovany bud jako:</u></i><br>
     * explicitne zadane datum 1.12.2021<br>
     * jako rozsah 1.12.2021 - 31.12.2021<br>
     * jako mesic 12/2021<br>
     * jako rozsah mesicu 1/2021 - 12/2021
     *
     * @param locale narodni prostredi
     * @return
     */
    public boolean dateFilter(Object value, Object filter, Locale locale) {
        System.out.println(" dateFilter(Object value, Object filter, Locale locale):" + value + "," + filter + "," + locale);
        boolean lOk = true;
        Date odData = null;
        Date doData = null;

        // Nejsou zadany spravne parametry
        if (value == null || filter == null) {
            return true;
        }
        // Filtr je datum - otestovat na shodu
        if (filter instanceof Date) {
            return ((Date) value).equals(filter);
        }
        // Filtr je mnozina o 2 prvcich - implicitni tvar pri fitrovani pomoci vestaveneho filtru pro  <p:datePicker> a filterMatchMode="range"
        if (filter instanceof ArrayList) {
            try {
                odData = Aplikace.getSimpleDateFormat().parse(((LocalDate) ((ArrayList) filter).get(0)).format(DateTimeFormatter.ofPattern("dd.MM.uuuu", Aplikace.getLocaleCZ())));
                doData = Aplikace.getSimpleDateFormat().parse(((LocalDate) ((ArrayList) filter).get(1)).format(DateTimeFormatter.ofPattern("dd.MM.uuuu", Aplikace.getLocaleCZ())));

                return (((Date) value).equals(odData) || ((Date) value).after(odData)) && (((Date) value).equals(doData) || ((Date) value).before(doData));

            } catch (ParseException ex) {
                Logger.getLogger(Users.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        // Filtr je definovany bud jako:
        // explicitne zadane datum 1.12.2021
        // jako rozsah 1.12.2021 - 31.12.2021
        // jako mesic 12/2021
        // jako rozsah mesicu  1/2021 - 12/2021
        if (!(filter instanceof String)) {
            return true;
        }
        String datums = ((String) filter).replaceAll(" ", "").replaceAll(",", ".");
        if (datums.length() == 0) {
            return true;
        }
        String[] splitOdDo = datums.split("[-_>;]");
        if (splitOdDo.length == 1 && splitOdDo[0].matches("\\d{1,2}/\\d{0,1}")) {
            return true;

        }
        if (splitOdDo.length == 1 && splitOdDo[0].matches("\\d{1,2}/\\d{2,4}")) {
            odData = filterToDate(splitOdDo[0], 0);
            doData = filterToDate(splitOdDo[0], 1);
            return (odData == null || ((Date) value).equals(odData) || ((Date) value).after(odData)) && (doData == null || ((Date) value).equals(doData) || ((Date) value).before(doData));

        }
        if (splitOdDo.length == 1 && splitOdDo[0].matches("\\d{1,2}\\.{0,1}\\d{0,2}\\.{0,1}\\d{0,4}")) {
            String[] datumA = splitOdDo[0].split("\\.");
            int[] datumI = new int[]{0, 0, 0};
            for (int i = 0; i < Math.min(3, datumA.length); i++) {
                datumI[i] = Integer.parseInt(datumA[i]);
            }
            Calendar cal = Calendar.getInstance(Aplikace.getLocaleCZ());
            cal.setTime(((Date) value));
            if (datumI[0] > 0) {
                lOk &= cal.get(Calendar.DAY_OF_MONTH) == datumI[0];
            }
            if (datumI[1] > 0) {
                lOk &= cal.get(Calendar.MONTH) == datumI[1] - 1;
            }
            if (datumI[2] > 0) {
                lOk &= cal.get(Calendar.YEAR) == datumI[2];
            }
            return lOk;
        }
        if (splitOdDo.length > 1) {
            odData = filterToDate(splitOdDo[0], 0);
            doData = filterToDate(splitOdDo[1], 1);
            return (odData == null || ((Date) value).equals(odData) || ((Date) value).after(odData)) && (doData == null || ((Date) value).equals(doData) || ((Date) value).before(doData));
        }

        return lOk;
    }

    private Date filterToDate(String strDatum, int mode) {
        Date datum = null;
        Calendar cal = Calendar.getInstance(Aplikace.getLocaleCZ());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (strDatum.matches("\\d{1,2}\\.\\d{1,2}\\.\\d{2,4}")) {
            cal.set(Calendar.YEAR, Integer.parseInt(strDatum.split("\\.")[2]));
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(strDatum.split("\\.")[0]));
            cal.set(Calendar.MONTH, Integer.parseInt(strDatum.split("\\.")[1]) - 1);
            if (mode == 1) {
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
            }
            datum = cal.getTime();
        }
        if (strDatum.matches("\\d{1,2}/\\d{2,4}")) {
            cal.set(Calendar.YEAR, Integer.parseInt(strDatum.split("/")[1]));
            if (mode == 0) {
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.MONTH, Integer.parseInt(strDatum.split("/")[0]) - 1);
            } else {
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.MONTH, Integer.parseInt(strDatum.split("/")[0]));
                cal.add(Calendar.MINUTE, -1);
            }
            datum = cal.getTime();
        }
        return datum;
    }

}
