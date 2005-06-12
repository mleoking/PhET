/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.qm.model.Detector;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:54:38 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class DetectorGraphic extends RectangleGraphic {
    private Detector detector;
    private DecimalFormat format = new DecimalFormat( "0.00" );
    private PhetTextGraphic probDisplay;

    public DetectorGraphic( SchrodingerPanel component, final Detector detector ) {
        super( component, detector, new Color( 0, 255, 0, 30 ) );
        this.detector = detector;

        probDisplay = new PhetTextGraphic( component, new Font( "Lucida Sans", Font.BOLD, 14 ), "", Color.red );
        probDisplay.setIgnoreMouse( true );
        addGraphic( probDisplay );
        detector.addObserver( new SimpleObserver() {
            public void update() {
                DetectorGraphic.this.update();
            }
        } );
        update();
    }

    private void update() {
        Rectangle modelRect = detector.getBounds();
        ColorGrid grid = super.getColorGrid();
        Rectangle viewRect = grid.getViewRectangle( modelRect );

        double probPercent = detector.getProbability() * 100;
        String formatted = format.format( probPercent );
        probDisplay.setText( formatted + " %" );
        probDisplay.setLocation( (int)viewRect.getX(), (int)viewRect.getY() );
    }

}
