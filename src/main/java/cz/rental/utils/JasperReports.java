/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 *
 * @author ivo
 */
public class JasperReports {

    public void runJasperReports(File jrxmlFile, File jsonFile, String pdfFileName) throws FileNotFoundException, JRException {

//    JsonDataSource datasource = new JsonDataSource(file);
        Map params = new HashMap();
        params.put(net.sf.jasperreports.engine.query.JsonQueryExecuterFactory.JSON_INPUT_STREAM, new FileInputStream(jsonFile));
        params.put(net.sf.jasperreports.engine.query.JsonQueryExecuterFactory.JSON_DATE_PATTERN, "dd.MM.yyyy");
        params.put(net.sf.jasperreports.engine.query.JsonQueryExecuterFactory.JSON_NUMBER_PATTERN, "#.##0,##");
        params.put(net.sf.jasperreports.engine.query.JsonQueryExecuterFactory.JSON_LOCALE, Aplikace.getLocaleCZ());
//        params.put(JRParameter.REPORT_LOCALE, Aplikace.getLocaleCZ());

        JasperDesign jasperDesign = JRXmlLoader.load(jrxmlFile);

        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint jasperPrint;
        jasperPrint = JasperFillManager.fillReport(jasperReport, params);

        JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFileName);
        // JasperViewer.viewReport(jasperPrint);
    }
}
