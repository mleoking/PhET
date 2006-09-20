package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.CCKImageSuite;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.CircuitComponent;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 20, 2006
 * Time: 10:49:26 AM
 * Copyright (c) Sep 20, 2006 by Sam Reid
 */

public class InductorNode extends ComponentImageNode {
    public InductorNode( CCKModel model, CircuitComponent circuitComponent, Component component ) {
        super( model, circuitComponent, CCKImageSuite.getInstance().getInductorImage(), component );
    }
}
