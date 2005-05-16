/* Copyright 2004, Sam Reid */
package edu.colorado.phet.timeseries.plot;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.help.HelpItem3;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphicListener;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 18, 2005
 * Time: 9:09:53 AM
 * Copyright (c) Apr 18, 2005 by Sam Reid
 */

public class CursorHelpItem extends PlotDeviceListenerAdapter {
    private boolean helpHasBeenShown = false;
    private ApparatusPanel apparatusPanel;
    private TimePlot timePlot;
    private HelpItem3 graphic;

    public CursorHelpItem( ApparatusPanel apparatusPanel, TimePlot timePlot ) {
        this.apparatusPanel = apparatusPanel;
        this.timePlot = timePlot;
//        graphic = new HelpItem3( apparatusPanel, "Drag the Cursor" );
        ///todo this is broken
//        phetShapeGraphic = new PhetShapeGraphic( apparatusPanel, new Rectangle( 0, 0, 100, 100 ), Color.red );
        graphic.setVisible( false );
        apparatusPanel.addGraphic( graphic, Double.POSITIVE_INFINITY );
        timePlot.addListener( new PlotDeviceListenerAdapter() {
            public void playbackTimeChanged() {
                graphic.setVisible( false );
            }
        } );
        timePlot.addPhetGraphicListener( new PhetGraphicListener() {
            public void phetGraphicChanged( PhetGraphic phetGraphic ) {
            }

            public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
                graphic.setVisible( phetGraphic.isVisible() );
            }
        } );
    }

    public void cursorVisibilityChanged( boolean visible ) {
//        System.out.println( "cursor bounds=" + timePlot.getCursor().getBounds() );
        if( visible ) {
            if( !helpHasBeenShown ) {
                showHelpItem();
                helpHasBeenShown = true;
            }
        }
        else {
            hideHelpItem();
        }
    }

    private void showHelpItem() {
        System.out.println( "Showing" );
        Rectangle screenBounds = timePlot.getCursor().getBounds();
        System.out.println( "CursorHelpItem: screenBounds = " + screenBounds );
//        graphic.pointRightAt( timePlot.getCursor(), 30 );//todo broken

        System.out.println( "Graphic bounds=" + graphic.getBounds() );
        graphic.setVisible( true );
    }

    private void hideHelpItem() {
        System.out.println( "Hiding" );
        graphic.setVisible( false );
    }
}
