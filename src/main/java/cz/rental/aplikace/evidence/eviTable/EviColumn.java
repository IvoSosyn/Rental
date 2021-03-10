/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.evidence.eviTable;

import cz.rental.aplikace.evidence.EviEntita;
import cz.rental.entity.Attribute;
import cz.rental.entity.Entita;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ivo
 */
public class EviColumn {

        Entita entita = null;
        String column = null;
        String table = "Entita";
        String field = "popis";

        private String header = "";
        private char type = 'C';

        private Object value = null;
        Method method;

        public EviColumn() {
        }

        public EviColumn(Entita entita, String column ) {
            this.entita = entita;
            this.column = column;
            loadColumn();
        }

        private void loadColumn() {
            String[] tableField;
            if (column == null || column.isEmpty()) {
                column = "Entita.popis";
            }
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
//            // Pokud se jedna o hodnotu Attribute je potreba dohledaat v DB
//            if (table.equalsIgnoreCase("Attribute")) {
//                setValue(column);
//                if (getAttributes() != null) {
//                    for (Attribute attr : getAttributes()) {
//                        if (attr.getAttrname().trim().equalsIgnoreCase(field.trim())) {
//                            this.setHeader(attr.getPopis());
//                            this.setType((char) attr.getAttrtype());
//                            setValue(attrController.getAttrValue(entita, attr, (Date) null, (Date) null));
//                            break;
//                        }
//                    }
//                }
//            }
//            // Pokud se jedna o tabulku "Typentity" metoda vrati hodnotu pole z  instance EviTable.typentity
//            if (table.equalsIgnoreCase("Typentity")) {
//                this.setHeader(getTypentity().getPopis());
//                try {
//                    this.method = getTypentity().getClass().getMethod("get" + field, new Class[]{});
//                    this.setValue(method.invoke(getTypentity(), new Object[]{}));
//                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
//                    Logger.getLogger(EviEntita.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//            // Pokud se jedna o tabulku "Entita" metoda vrati hodnotu pole z  instance EviColumn.entita
//            if (table.equalsIgnoreCase("Entita")) {
//                this.setHeader(entita.getPopis());
//                try {
//                    this.method = entita.getClass().getMethod("get" + field, new Class[]{});
//                    this.setValue(method.invoke(entita, new Object[]{}));
//                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
//                    Logger.getLogger(EviEntita.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
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
            return type;
        }

        /**
         * @param type the type to set
         */
        public void setType(char type) {
            this.type = type;
        }

        /**
         * @return the value
         */
        public Object getValue() {
            return value;
        }

        /**
         * @param value the value to set
         */
        public void setValue(Object value) {
            this.value = value;
        }    
}
