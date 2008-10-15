/*, 2003.*/
package edu.colorado.phet.microwaves.model.observation;

import edu.colorado.phet.common_microwaves.model.ModelElement;


/**
 * User: Sam Reid
 * Date: May 21, 2003
 * Time: 6:33:57 PM
 */
public class Deletion implements ObservationType {
    ModelElement target;

    public Deletion( ModelElement target ) {
        this.target = target;
    }

    public ModelElement getTarget() {
        return target;
    }
}
