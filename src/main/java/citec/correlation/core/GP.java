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
public class GP {

    private List<ObjPredSubj> objPredSubj = new ArrayList<ObjPredSubj>();
    
    public GP(List<ObjPredSubj> objPredSubjs) {
        this.objPredSubj=objPredSubjs;
    }

    public List<ObjPredSubj> getObjPredSubj() {
        return objPredSubj;
    }

    @Override
    public String toString() {
        return "GP{"+ objPredSubj + '}';
    }
    

}
