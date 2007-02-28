/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.model.observation;

import edu.colorado.phet.common.model.ModelElement;


/**
 * User: Sam Reid
 * Date: May 21, 2003
 * Time: 6:33:57 PM
 * Copyright (c) May 21, 2003 by Sam Reid
 */
public class Deletion implements ObservationType {
    ModelElement target;

    public Deletion(ModelElement target) {
        this.target = target;
    }

    public ModelElement getTarget() {
        return target;
    }
}
