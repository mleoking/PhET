/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plotdevice;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphicListener;
import edu.colorado.phet.movingman.common.HelpItem2;

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
    private PlotDevice plotDevice;
    private HelpItem2 graphic;

    public CursorHelpItem( ApparatusPanel apparatusPanel, PlotDevice plotDevice ) {
        this.apparatusPanel = apparatusPanel;
        this.plotDevice = plotDevice;
        graphic = new HelpItem2( apparatusPanel, "Drag the Cursor" );
//        phetShapeGraphic = new PhetShapeGraphic( apparatusPanel, new Rectangle( 0, 0, 100, 100 ), Color.red );
        graphic.setVisible( false );
        apparatusPanel.addGraphic( graphic, Double.POSITIVE_INFINITY );
        plotDevice.addListener( new PlotDeviceListenerAdapter() {
            public void playbackTimeChanged() {
                graphic.setVisible( false );
            }
        } );
        plotDevice.addPhetGraphicListener( new PhetGraphicListener() {
            public void phetGraphicChanged( PhetGraphic phetGraphic ) {
            }

            public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
                graphic.setVisible( phetGraphic.isVisible() );
            }
        } );
    }

    public void cursorVisibilityChanged( boolean visible ) {
//        System.out.println( "cursor bounds=" + plotDevice.getCursor().getBounds() );
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
        Rectangle screenBounds = plotDevice.getCursor().getBounds();
        System.out.println( "CursorHelpItem: screenBounds = " + screenBounds );
        graphic.pointRightAt( plotDevice.getCursor(), 30 );
        System.out.println( "Graphic bounds=" + graphic.getBounds() );
        graphic.setVisible( true );
    }

    private void hideHelpItem() {
        System.out.println( "Hiding" );
        graphic.setVisible( false );
    }
}
