/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.evidence;

import cz.rental.utils.Aplikace;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author ivo
 */
@FacesConverter("obdobiConverter")
public class ObdobiConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Date datum = null;
        if (value instanceof String) {
            try {
                value=value.replaceAll(",", ".").replaceAll(";", ".").replaceAll(":", ".").replaceAll("-", ".");
                datum = Aplikace.getSimpleDateFormat().parse(value);
            } catch (ParseException ex) {
                Logger.getLogger(ObdobiConverter.class.getName()).log(Level.SEVERE, null, ex);
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
