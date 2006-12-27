package edu.colorado.phet.ec2.common.view.creation;

import edu.colorado.phet.common.view.graphics.InteractiveGraphic;

/**
 * User: Sam Reid
 * Date: Jul 28, 2003
 * Time: 12:32:29 AM
 * Copyright (c) Jul 28, 2003 by Sam Reid
 */
public interface CreationEvent {
    /*Creates and adds to the model and view, and returns the interactive graphic for event forwarding purposes.*/
    InteractiveGraphic createElement();
}
