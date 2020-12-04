/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.registrace;

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
@FacesConverter("typentityConverter")
@SessionScoped
public class TypentityConverter implements Converter, Serializable {

    static final long serialVersionUID = 42L;

    private ArrayList<Typentity> typEntities = new ArrayList<>();

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value instanceof String) {

            ValueExpression vex
                    = context.getApplication().getExpressionFactory()
                            .createValueExpression(context.getELContext(),
                                    "#{registrace.models}", ArrayList.class);
            this.typEntities = (ArrayList<Typentity>) vex.getValue(context.getELContext());
            if (!this.typEntities.isEmpty()) {
                for (Typentity typEntity : typEntities) {
                    if (typEntity.getTypentity().contentEquals(value)) {
                        return typEntity;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        } else {
            return ((Typentity) value).getTypentity();
        }
    }
}
