/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.admin.model;

import cz.rental.entity.Typentity;
import java.io.Serializable;
import java.util.ArrayList;
import javax.el.ValueExpression;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author ivo
 */
@FacesConverter(value = "typentityConvertet")
@SessionScoped
public class TypentityConvertet implements Converter, Serializable {

    static final long serialVersionUID = 42L;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ValueExpression vex
                = context.getApplication().getExpressionFactory()
                        .createValueExpression(context.getELContext(),
                                "#{modelTree.models}", ArrayList.class);

        ArrayList<Typentity> models = (ArrayList<Typentity>) vex.getValue(context.getELContext());
        for (Typentity model : models) {
            if (model.getId().toString().equals(value)) {
                return (Typentity) model;
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((Typentity) value).getId().toString();
    }
}
