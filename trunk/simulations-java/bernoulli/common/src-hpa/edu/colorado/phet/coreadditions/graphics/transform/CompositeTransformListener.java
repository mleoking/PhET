/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.graphics.transform;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Aug 19, 2003
 * Time: 10:26:01 AM
 * Copyright (c) Aug 19, 2003 by Sam Reid
 */
public class CompositeTransformListener implements TransformListener {
    ArrayList listeners = new ArrayList();

    public void transformChanged(ModelViewTransform2d mvt) {
        for (int i = 0; i < listeners.size(); i++) {
            TransformListener o = (TransformListener) listeners.get(i);
            o.transformChanged(mvt);
        }
    }

    public TransformListener transformListenerAt(int i) {
        return (TransformListener) listeners.get(i);
    }

    public void removeTransformListener(TransformListener tl) {
        listeners.remove(tl);
    }

    public int numTransformListeners(TransformListener tl) {
        return listeners.size();
    }

    public void addTransformListener(TransformListener tl) {
        listeners.add(tl);
    }
}
