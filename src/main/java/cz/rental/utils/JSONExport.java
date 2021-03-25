/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.utils;

import cz.rental.entity.Attribute;
import cz.rental.entity.EntitySuperClassNajem;
import cz.rental.entity.Typentity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.persistence.Query;

/**
 *
 * @author sosyn
 */
@Stateless
public class JSONExport {

    @EJB
    cz.rental.entity.TypentityController typEntitycontroller;
    @EJB
    cz.rental.entity.AttrController attrController;

    Query query = null;
    Date platiOd = Aplikace.getPlatiOd();
    Date platiDo = Aplikace.getPlatiDo();
    int randomInt = (int) (Math.random() * 1000);
    File exportFile = new File(System.getenv("TEMP"), String.format("ModelNajem%1$04d", randomInt) + ".JSON");
    OutputStream os = null;
    Object value = null;
    HashSet<Field> hashSetSuperFields = new HashSet<>(Arrays.asList(EntitySuperClassNajem.class.getDeclaredFields()));
    HashSet<Method> hashSetSuperMethods = new HashSet<>(Arrays.asList(EntitySuperClassNajem.class.getDeclaredMethods()));
    HashSet<Field> hashSetTypentityFields = new HashSet<>(Arrays.asList(Typentity.class.getDeclaredFields()));
    HashSet<Method> hashSetTypentityMethods = new HashSet<>(Arrays.asList(Typentity.class.getDeclaredMethods()));
    HashSet<Field> hashSetAttributeFields = new HashSet<>(Arrays.asList(Attribute.class.getDeclaredFields()));
    HashSet<Method> hashSetAttributeMethods = new HashSet<>(Arrays.asList(Attribute.class.getDeclaredMethods()));

    public String exportModel(Typentity typentityRoot) throws FileNotFoundException, IOException {
        if (platiOd == null) {
            Aplikace.getCalendar().set(1970, Calendar.JANUARY, 1, 0, 0, 0);
            platiOd = Aplikace.getCalendar().getTime();
        }
        if (platiDo == null) {
            Aplikace.getCalendar().set(2101, Calendar.JANUARY, 1, 0, 0, 0);
            Aplikace.getCalendar().add(Calendar.HOUR_OF_DAY, -1);
            platiDo = Aplikace.getCalendar().getTime();
        }
        try {

            JsonObjectBuilder json = Json.createObjectBuilder().add("MODEL", createJsonObjectModel(typentityRoot));

            //write to file
            os = new FileOutputStream(exportFile);
            try (JsonWriter jsonWriter = Json.createWriter(os)) {
                jsonWriter.writeObject(json.build());
            }
        } catch (FileNotFoundException ex) {
            throw ex;
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (os instanceof FileOutputStream) {
                    os.close();
                }
            } catch (IOException ex) {
                throw ex;
            }
        }
        return exportFile.getAbsolutePath();
    }

    private JsonObjectBuilder createJsonObjectModel(Typentity typEntity) {
        // Typentity - popis
        if (typEntity == null) {
            query = typEntitycontroller.getEm().createQuery("SELECT t FROM Typentity t WHERE t.idparent IS NULL AND (t.platiod IS NULL OR t.platiod <= :PlatiDO) AND (t.platido IS NULL OR t.platido >= :PlatiOD)");
            query.setParameter("PlatiOD", this.platiOd);
            query.setParameter("PlatiDO", this.platiDo);
            typEntity = (Typentity) query.getSingleResult();
        }
        if (typEntity == null) {
            return null;
        }
        System.out.println(" typEntity:=" + typEntity.getTypentity() + " " + typEntity.getPopis());
        JsonObjectBuilder jsonObject = Json.createObjectBuilder();

        // Nacist pole a getMetody pro Typentity
        getObjectFields(Typentity.class, typEntity, jsonObject);

        // Nacist pole a getMetody pro Attribute s klíčem Typentity.id
        jsonObject.add("ATTRIBUTE", this.getAttributes(typEntity));

        // Pole s Child - Typentity
        JsonArrayBuilder jsonChild = Json.createArrayBuilder();
        query = typEntitycontroller.getEm().createQuery("SELECT t FROM Typentity t WHERE t.idparent= :idParentEntity AND (t.platiod IS NULL OR t.platiod <= :PlatiDO) AND (t.platido IS NULL OR t.platido >= :PlatiOD)");
        query.setParameter("idParentEntity", typEntity.getId());
        query.setParameter("PlatiOD", platiOd);
        query.setParameter("PlatiDO", platiDo);
        ArrayList<Typentity> listChild = new ArrayList<>(query.getResultList());
        for (Typentity typentityChild : listChild) {
            JsonObjectBuilder jsonObjectChild = createJsonObjectModel(typentityChild);
            if (jsonObjectChild != null) {
                jsonChild.add(jsonObjectChild);
            }
        }
        jsonObject.add("TYPENTITIES", jsonChild);

        return jsonObject;
    }

    /**
     * Rekursivni volani metody nad DB - nalezeni dane Entita (samostatny
     * objekt) a jejich Attribute (samostatny objekt)
     */
    private JsonObjectBuilder createJSON_OLD(Typentity typEntity) {

        // Typentity - popis
        JsonObjectBuilder jobTypEntity = Json.createObjectBuilder();
        if (typEntity instanceof Typentity) {
            query = typEntitycontroller.getEm().createQuery("SELECT t FROM Typentity t WHERE t.id= :idTypEntity AND (t.platiod IS NULL OR t.platiod <= :PlatiDO) AND (t.platido IS NULL OR t.platido >= :PlatiOD)");
            query.setParameter("idTypEntity", typEntity.getId());
        } else {
            query = typEntitycontroller.getEm().createQuery("SELECT t FROM Typentity t WHERE t.idparent IS NULL AND (t.platiod IS NULL OR t.platiod <= :PlatiDO) AND (t.platido IS NULL OR t.platido >= :PlatiOD)");
        }
        query.setParameter("PlatiOD", platiOd);
        query.setParameter("PlatiDO", platiDo);
        List<Typentity> list = query.getResultList();
        for (Typentity typEntityJob : list) {
            // EntitySuperClassNajem.class
            for (Field field : hashSetSuperFields) {
                for (Method method : hashSetSuperMethods) {
                    if (method.getName().equalsIgnoreCase("get" + field.getName())) {
                        try {
                            value = method.invoke(typEntityJob);
                            if (value == null) {
                                jobTypEntity.addNull(field.getName());
                            } else if (value instanceof String) {
                                jobTypEntity.add(field.getName(), (String) value);
                            } else if (value instanceof Integer) {
                                jobTypEntity.add(field.getName(), (Integer) value);
                            } else if (value instanceof Double) {
                                jobTypEntity.add(field.getName(), (Double) value);
                            } else if (value instanceof Date) {
                                jobTypEntity.add(field.getName(), Aplikace.getSimpleDateFormat().format((Date) value));
                            } else if (value instanceof Boolean) {
                                jobTypEntity.add(field.getName(), (Boolean) value);
                            } else {
                                jobTypEntity.add(field.getName(), value.toString());
                            }
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                            Logger.getLogger(JSONExport.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    }
                }
            }
            // Typentity.class
            for (Field field : hashSetTypentityFields) {
                for (Method method : hashSetTypentityMethods) {
                    if (method.getName().equalsIgnoreCase("get" + field.getName())) {
                        try {
                            value = method.invoke(typEntityJob);
                            if (value == null) {
                                jobTypEntity.addNull(field.getName());
                            } else if (value instanceof String) {
                                jobTypEntity.add(field.getName(), (String) value);
                            } else if (value instanceof Integer) {
                                jobTypEntity.add(field.getName(), (Integer) value);
                            } else if (value instanceof Double) {
                                jobTypEntity.add(field.getName(), (Double) value);
                            } else if (value instanceof Date) {
                                jobTypEntity.add(field.getName(), Aplikace.getSimpleDateFormat().format((Date) value));
                            } else if (value instanceof Boolean) {
                                jobTypEntity.add(field.getName(), (Boolean) value);
                            } else {
                                jobTypEntity.add(field.getName(), value.toString());
                            }
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                            Logger.getLogger(JSONExport.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    }
                }
            }
            // Attribute
            JsonArrayBuilder jobAttrs = Json.createArrayBuilder();
            query = attrController.getEm().createQuery("SELECT a FROM Attribute a WHERE a.idtypentity= :idTypEntity AND a.identita IS NULL AND (a.platiod IS NULL OR a.platiod <= :PlatiDO) AND (a.platido IS NULL OR a.platido >= :PlatiOD) ORDER BY a.poradi");
            query.setParameter("idTypEntity", typEntityJob.getId());
            query.setParameter("PlatiOD", platiOd);
            query.setParameter("PlatiDO", platiDo);
            List<Attribute> listAttr = query.getResultList();
            for (Attribute attribute : listAttr) {
                JsonObjectBuilder jobAttr = Json.createObjectBuilder();
                // EntitySuperClassNajem.class
                for (Field field : hashSetSuperFields) {
                    for (Method method : hashSetSuperMethods) {
                        if (method.getName().equalsIgnoreCase("get" + field.getName())) {
                            try {
                                value = method.invoke(attribute);
                                if (value == null) {
                                    jobAttr.addNull(field.getName());
                                } else if (value instanceof String) {
                                    jobAttr.add(field.getName(), (String) value);
                                } else if (value instanceof Integer) {
                                    jobAttr.add(field.getName(), (Integer) value);
                                } else if (value instanceof Double) {
                                    jobAttr.add(field.getName(), (Double) value);
                                } else if (value instanceof Date) {
                                    jobAttr.add(field.getName(), Aplikace.getSimpleDateFormat().format((Date) value));
                                } else if (value instanceof Boolean) {
                                    jobAttr.add(field.getName(), (Boolean) value);
                                } else {
                                    jobAttr.add(field.getName(), value.toString());
                                }
                            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                                Logger.getLogger(JSONExport.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                        }
                    }
                }
                // Attribute.class
                for (Field field : hashSetAttributeFields) {
                    for (Method method : hashSetAttributeMethods) {
                        if (method.getName().equalsIgnoreCase("get" + field.getName())) {
                            try {
                                value = method.invoke(attribute);
                                if (value == null) {
                                    jobAttr.addNull(field.getName());
                                } else if (value instanceof String) {
                                    jobAttr.add(field.getName(), (String) value);
                                } else if (value instanceof Integer) {
                                    jobAttr.add(field.getName(), (Integer) value);
                                } else if (value instanceof Double) {
                                    jobAttr.add(field.getName(), (Double) value);
                                } else if (value instanceof Date) {
                                    jobAttr.add(field.getName(), Aplikace.getSimpleDateFormat().format((Date) value));
                                } else if (value instanceof Boolean) {
                                    jobAttr.add(field.getName(), (Boolean) value);
                                } else {
                                    jobAttr.add(field.getName(), value.toString());
                                }
                            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                                Logger.getLogger(JSONExport.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                        }
                    }
                }
                jobAttrs.add(jobAttr);
            }
            jobTypEntity.add("ATTRIBUTE", jobAttrs);

            // Pole s Child - Typentity
            JsonArrayBuilder jobEntyties = Json.createArrayBuilder();
            query = typEntitycontroller.getEm().createQuery("SELECT t FROM Typentity t WHERE t.idparent= :idParentEntity AND (t.platiod IS NULL OR t.platiod <= :PlatiDO) AND (t.platido IS NULL OR t.platido >= :PlatiOD)");
            query.setParameter("idParentEntity", typEntityJob.getId());
            query.setParameter("PlatiOD", platiOd);
            query.setParameter("PlatiDO", platiDo);
            List<Typentity> listChild = query.getResultList();
            for (Typentity typentityChild : listChild) {
                jobEntyties.add(createJsonObjectModel(typentityChild));
            }
            jobTypEntity.add("TYPENTITY", jobEntyties);
        }
        return jobTypEntity;
    }

    private JsonArrayBuilder getAttributes(Typentity typEntity) {
        JsonArrayBuilder jobAttrs = Json.createArrayBuilder();
        query = attrController.getEm().createQuery("SELECT a FROM Attribute a WHERE a.idtypentity= :idTypEntity AND a.identita IS NULL AND (a.platiod IS NULL OR a.platiod <= :PlatiDO) AND (a.platido IS NULL OR a.platido >= :PlatiOD) ORDER BY a.poradi");
        query.setParameter("idTypEntity", typEntity.getId());
        query.setParameter("PlatiOD", platiOd);
        query.setParameter("PlatiDO", platiDo);
        List<Attribute> listAttr = query.getResultList();
        for (Attribute attr : listAttr) {
            JsonObjectBuilder jobAttr = Json.createObjectBuilder();
            getObjectFields(Attribute.class, attr, jobAttr);
            jobAttrs.add(jobAttr);
        }
        return jobAttrs;
    }

    private void getObjectFields(java.lang.Class objectClass, Object object, JsonObjectBuilder jsonObjectTypEntityFields) {
        // System.out.println(" object.getClass().getName(): " + object.getClass().getName());
        if (object != null && !objectClass.getName().contains("EntitySuperClassNajem")) {
            //System.out.println(" object.getClass().getSuperclass().getName(): " + object.getClass().getSuperclass().getName());
            getObjectFields(EntitySuperClassNajem.class, object, jsonObjectTypEntityFields);
        }
        for (Field field : objectClass.getDeclaredFields()) {
            for (Method method : objectClass.getDeclaredMethods()) {
                if (method.getName().equalsIgnoreCase("get" + field.getName())) {
                    try {
                        value = method.invoke(object);
                        if (value == null) {
                            jsonObjectTypEntityFields.addNull(field.getName());
                        } else if (value instanceof String) {
                            jsonObjectTypEntityFields.add(field.getName(), (String) value);
                        } else if (value instanceof Integer) {
                            jsonObjectTypEntityFields.add(field.getName(), (Integer) value);
                        } else if (value instanceof Double) {
                            jsonObjectTypEntityFields.add(field.getName(), (Double) value);
                        } else if (value instanceof Date) {
                            jsonObjectTypEntityFields.add(field.getName(), Aplikace.getSimpleDateFormat().format((Date) value));
                        } else if (value instanceof Boolean) {
                            jsonObjectTypEntityFields.add(field.getName(), (Boolean) value);
                        } else {
                            jsonObjectTypEntityFields.add(field.getName(), value.toString());
                        }
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        Logger.getLogger(JSONExport.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }
            }
        }
    }

}
