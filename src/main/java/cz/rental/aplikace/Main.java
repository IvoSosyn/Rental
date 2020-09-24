/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace;

import javax.ejb.Stateless;
import javax.inject.Named;

/**
 *
 * @author ivo
 */
@Named(value = "main")
@Stateless
public class Main {
    private String selectedMainPage="mainPage2";
            
    public String mainPage() {
        return "/aplikace/"+selectedMainPage+".xhtml";
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    /**
     * @return the selectedMainPage
     */
    public String getSelectedMainPage() {
        return selectedMainPage;
    }

    /**
     * @param selectedMainPage the selectedMainPage to set
     */
    public void setSelectedMainPage(String selectedMainPage) {
        this.selectedMainPage = selectedMainPage;
    }
}
