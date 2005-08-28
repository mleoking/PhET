/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo.experimental;

import edu.umd.cs.piccolo.PNode;
import edu.colorado.phet.piccolo.WiggleMe;
import edu.colorado.phet.piccolo.ConnectorGraphic;

/**
 * User: Sam Reid
 * Date: Jul 28, 2005
 * Time: 12:30:23 PM
 * Copyright (c) Jul 28, 2005 by Sam Reid
 */

public class TargetedWiggleMe extends WiggleMe {

    public TargetedWiggleMe( String message, int x, int y, PNode dest ) {
        super( message, x, y );

        ConnectorGraphic arrowConnectorGraphic = new ConnectorGraphic( this, dest );
        addChild( arrowConnectorGraphic );
    }
}
