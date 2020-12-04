/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.utils;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author ivo
 */
@FacesConverter("booleanConverter")
@SessionScoped
public class BooleanConverter implements Converter, Serializable {

    static final long serialVersionUID = 42L;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return (value != null);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return value == null ? "" : value.toString();
    }
}
