/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.core;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author elahi
 */
public class KB_TYPE {

    private String type_name = null;

    private List<GP> gps = new ArrayList<GP>();

    public KB_TYPE(String type_name, List<GP> gps) {
        this.type_name = type_name;
        this.gps = gps;
    }

    public List<GP> getGps() {
        return gps;
    }

    public String getType_name() {
        return type_name;
    }

    
    public void display() {
        for(GP gp:gps){
             System.out.println(gp);
            }
        
    }

}
