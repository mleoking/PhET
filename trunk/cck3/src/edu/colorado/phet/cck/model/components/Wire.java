package edu.colorado.phet.cck.model.components;

import edu.colorado.phet.cck.CCKLookAndFeel;
import edu.colorado.phet.cck.model.CircuitChangeListener;
import edu.colorado.phet.cck.model.Junction;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 5:17:00 PM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class Wire extends Branch {
    protected Wire( CircuitChangeListener listener ) {
        super( listener );
    }

    public Wire( CircuitChangeListener listener, Junction startJunction, Junction endJunction ) {
        super( listener, startJunction, endJunction );
    }

    public Shape getShape() {
        return new BasicStroke( (float)( CCKLookAndFeel.WIRE_THICKNESS * CCKLookAndFeel.DEFAULT_SCALE ), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ).createStrokedShape( getLine() );
    }

    public Line2D.Double getLine() {
        return new Line2D.Double( getStartPoint(), getEndPoint() );
    }
}
