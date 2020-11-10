/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.core;

/**
 *
 * @author elahi
 */
public class ObjPredSubj {

    private TripleElement object;
    private TripleElement predicate;
    private TripleElement subject;

    public ObjPredSubj(TripleElement object, TripleElement predicate, TripleElement subject) {
       this.object=object;
       this.predicate=predicate;
       this.subject=subject;
    }

    public TripleElement getObject() {
        return object;
    }

    public TripleElement getPredicate() {
        return predicate;
    }

    public TripleElement getSubject() {
        return subject;
    }

    @Override
    public String toString() {
        return  "\n"+  object + "\n"+predicate + "\n"+subject + "\n";
    }

}
