/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.evidence.eviTable;

import cz.rental.entity.Attribute;
import cz.rental.entity.Entita;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 *
 * @author ivo
 */
public class EviHeader {

    private String header = "*";
    private String column = "Entita.popis";
    private String table = "Entita";
    private String field = "popis";
    private Attribute attribute = null;
    private HashMap<Entita, Object> entitaValues = new HashMap<>(10);
    Method method;

    public EviHeader(String column, String header, Attribute attribute) {
        String[] tableField;
        if (column != null) {
            this.column = column;
        }
        if (header != null) {
            this.header = header;
        }
        this.attribute = attribute;
        tableField = column.split("\\.");
        switch (tableField.length) {
            case 0: {
                break;
            }
            case 1: {
                field = tableField[0];
                break;
            }
            case 2: {
                table = tableField[0];
                field = tableField[1];
                break;
            }
            default:
        }
    }

    /**
     * Metoda vraci hodnotu pro zadanou Entita
     *
     * @param entita
     * @return hodnota pro pozadovanou polozku Entita nebo NULL
     */
    public Object getValue(Entita entita) {
        if (entita != null && this.getEntitaValues() != null) {
            return this.getEntitaValues().get(entita);
        } else {
            return null;
        }
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return the type
     */
    public char getType() {
        if (getAttribute() instanceof Attribute) {
            return this.attribute.getAttrtype();
        } else {
            return 'C';
        }
    }

    /**
     * @return the column
     */
    public String getColumn() {
        return column;
    }

    /**
     * @param column the column to set
     */
    public void setColumn(String column) {
        this.column = column;
    }

    /**
     * @return the entitaValues
     */
    public HashMap<Entita, Object> getEntitaValues() {
        return entitaValues;
    }

    /**
     * @param entitaValues the entitaValues to set
     */
    public void setEntitaValues(HashMap<Entita, Object> entitaValues) {
        this.entitaValues = entitaValues;
    }

    /**
     * @return the table
     */
    public String getTable() {
        return table;
    }

    /**
     * @param table the table to set
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * @param field the field to set
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * @return the attribute
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * @param attribute the attribute to set
     */
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

}
