/**
 * Class: HelloPhetModel
 * Package: edu.colorado.phet.common.examples.hellophet.model
 * Author: Another Guy
 * Date: May 20, 2003
 */
package edu.colorado.phet.common.examples.hellophet.model;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.view.components.media.Resettable;

public class HelloPhetModel extends BaseModel implements Resettable {

    public HelloPhetModel() {
    }

    public void reset() {
        removeAllModelElements();
    }

    public void removeMessage( Message m ) {
        super.removeModelElement( m );
    }

}
