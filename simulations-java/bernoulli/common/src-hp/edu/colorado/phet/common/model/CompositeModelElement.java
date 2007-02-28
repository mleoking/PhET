package edu.colorado.phet.common.model;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Mar 1, 2003
 * Time: 5:38:59 PM
 * To change this template use Options | File Templates.
 */
public class CompositeModelElement extends ModelElement {
    ArrayList al = new ArrayList();

    public void addModelElement(ModelElement aps) {
        al.add(aps);
    }

    public ModelElement modelElementAt(int i) {
        return (ModelElement) al.get(i);
    }

    public int numModelElements() {
        return al.size();
    }

    public void stepInTime(double dt) {
        for (int i = 0; i < numModelElements(); i++) {
            modelElementAt(i).stepInTime(dt);
        }
        updateObservers();
    }

    public void removeModelElement(ModelElement m) {
        al.remove(m);
    }

    protected void removeAllModelElements() {
        al.clear();
    }
}
