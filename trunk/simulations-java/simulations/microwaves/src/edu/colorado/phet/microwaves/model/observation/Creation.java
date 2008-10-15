/*, 2003.*/
package edu.colorado.phet.microwaves.model.observation;

import edu.colorado.phet.common_microwaves.model.ModelElement;


/**
 * User: Sam Reid
 * Date: May 21, 2003
 * Time: 6:34:20 PM
 */
public class Creation implements ObservationType {
    ModelElement target;

    public Creation( ModelElement target ) {
        this.target = target;
    }

    public ModelElement getTarget() {
        return target;
    }

}
