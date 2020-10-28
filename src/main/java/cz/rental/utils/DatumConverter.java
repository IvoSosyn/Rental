/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.utils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author ivo
 */
@FacesConverter("obdobiConverter")
public class DatumConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Date datum = null;
        if (value instanceof String) {
            try {
                Pattern pattern = Pattern.compile("([0-3]?\\d+)([/;:-])(\\d{2,4})");
                Matcher matcher = pattern.matcher(value);
                if (matcher.matches()) {
                    Calendar cal = Calendar.getInstance(Aplikace.getLocaleCZ());
                    String componentId = component.getClientId(context);
                    int rok = Integer.parseInt(matcher.group(3));
                    if (rok < 51) {
                        rok += 2000;
                    } else if (rok < 1000) {
                        rok += 1900;
                    }
                    cal.set(Calendar.YEAR, rok);

                    if (componentId.toUpperCase().endsWith("DO")) {
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.DAY_OF_MONTH, 1);
                        cal.set(Calendar.MONTH, Integer.parseInt(matcher.group(1)));
                        cal.add(Calendar.DAY_OF_MONTH, -1);
                        cal.set(Calendar.HOUR_OF_DAY, 23);
                        cal.set(Calendar.MINUTE, 59);
                    } else {
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.DAY_OF_MONTH, 1);
                        cal.set(Calendar.MONTH, Integer.parseInt(matcher.group(1)) - 1);
                    }

                    datum = cal.getTime();
                } else {
                    value = value.replaceAll(",", ".").replaceAll(";", ".").replaceAll(":", ".").replaceAll("-", ".");
                    datum = Aplikace.getSimpleDateFormat().parse(value);
                }
            } catch (ParseException ex) {
                Logger.getLogger(DatumConverter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return datum;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        } else {
            return Aplikace.getSimpleDateFormat().format(value);
        }
    }

}
