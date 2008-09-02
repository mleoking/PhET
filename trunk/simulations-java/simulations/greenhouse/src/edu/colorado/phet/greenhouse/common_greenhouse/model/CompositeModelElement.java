package edu.colorado.phet.greenhouse.common_greenhouse.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Mar 1, 2003
 * Time: 5:38:59 PM
 * To change this template use Options | File Templates.
 */
public class CompositeModelElement extends ModelElement {
    ArrayList modelElements = new ArrayList();

    public void addModelElement(ModelElement aps) {
        modelElements.add(aps);
    }

    public ModelElement modelElementAt(int i) {
        return (ModelElement) modelElements.get(i);
    }

    public int numModelElements() {
        return modelElements.size();
    }

    public void stepInTime(double dt) {
        for (int i = 0; i < numModelElements(); i++) {
            modelElementAt(i).stepInTime(dt);
        }
        updateObservers();
    }

    public void removeModelElement(ModelElement m) {
        modelElements.remove(m);
    }

    public void removeAllModelElements() {
        modelElements.clear();
    }

    protected List getModelElements() {
        return modelElements;
    }
}
