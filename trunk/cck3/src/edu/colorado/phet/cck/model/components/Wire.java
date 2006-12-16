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
    public static final double LIFELIKE_THICKNESS = CCKLookAndFeel.WIRE_THICKNESS * CCKLookAndFeel.DEFAULT_SCALE;
    public static final double SCHEMATIC_THICKNESS = CCKLookAndFeel.WIRE_THICKNESS * CCKLookAndFeel.DEFAULT_SCALE * 0.6;
    private double thickness = LIFELIKE_THICKNESS;

    public Wire( CircuitChangeListener listener, Junction startJunction, Junction endJunction ) {
        super( listener, startJunction, endJunction );
    }

    public Shape getShape() {
        return new BasicStroke( (float)( thickness ), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ).createStrokedShape( getLine() );
    }

    public void setThickness( double thickness ) {
        this.thickness = thickness;
        super.notifyObservers();
    }

    public Line2D.Double getLine() {
        return new Line2D.Double( getStartPoint(), getEndPoint() );
    }

    public boolean isEditing() {
        return false;
    }

    public void setEditing( boolean editing ) {
        //no-op for wire
    }

    public boolean isEditable() {
        return false;
    }
}
