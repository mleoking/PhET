/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.piccolo.nodes.MeasuringTape;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 8:24:31 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class MeasurementToolSet extends PNode {
    private MeasuringTape measuringTape;
    private PNode stopwatchGraphic;

    public MeasurementToolSet() {
        measuringTape = new MeasuringTape( new ModelViewTransform2D( new Rectangle2D.Double( 0, 0, 100, 100 ), new Rectangle2D.Double( 0, 0, 100, 100 ) ), new Point2D.Double( 0, 0 ) );
        measuringTape.setVisible( false );
        measuringTape.setOffset( 100, 100 );
        addChild( measuringTape );

        stopwatchGraphic = new PText( "Stopwatch goes here." );
        stopwatchGraphic.setVisible( false );
        addChild( stopwatchGraphic );
    }

    public boolean isMeasuringTapeVisible() {
        return measuringTape.getVisible();
    }

    public void setMeasuringTapeVisible( boolean selected ) {
        measuringTape.setVisible( selected );
    }

    public boolean isStopwatchVisible() {
        return stopwatchGraphic.getVisible();
    }

    public void setStopwatchVisible( boolean selected ) {
        stopwatchGraphic.setVisible( selected );
    }
}
