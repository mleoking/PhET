/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 24, 2005
 * Time: 11:25:35 PM
 * Copyright (c) Apr 24, 2005 by Sam Reid
 */

public class ShapeDebugGraphic extends CompositePhetGraphic implements ClockTickListener {
    private ShapeGetter shapeGetter;

    private PhetShapeGraphic shapeGraphic;
    static Color[] colors = new Color[]{Color.red, Color.green, Color.blue};
    static int colorIndex = 0;

    public ShapeDebugGraphic( Component component, AbstractClock clock, ShapeGetter shapeGetter ) {
        super( component );
        this.shapeGetter = shapeGetter;
        clock.addClockTickListener( this );
        shapeGraphic = new PhetShapeGraphic( component, shapeGetter.getShape(), transparify( colors[colorIndex % colors.length] ), new BasicStroke( 1 ), Color.black );
        colorIndex++;
        addGraphic( shapeGraphic );
        setIgnoreMouse( true );
    }

    private Paint transparify( Color color ) {
        return new Color( color.getRed(), color.getGreen(), color.getBlue(), 80 );
    }

    public void clockTicked( ClockTickEvent event ) {
        setShape( shapeGetter.getShape() );
    }

    private void setShape( Shape shape ) {
        shapeGraphic.setShape( shape );
    }
}
