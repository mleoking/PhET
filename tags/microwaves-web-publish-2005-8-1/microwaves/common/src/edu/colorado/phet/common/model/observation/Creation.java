/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.model.observation;

import edu.colorado.phet.common.model.ModelElement;


/**
 * User: Sam Reid
 * Date: May 21, 2003
 * Time: 6:34:20 PM
 * Copyright (c) May 21, 2003 by Sam Reid
 */
public class Creation implements ObservationType {
    ModelElement target;

    public Creation(ModelElement target) {
        this.target = target;
    }

    public ModelElement getTarget() {
        return target;
    }

}
