/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.waveinterference.phetcommon.VerticalConnector;
import edu.umd.cs.piccolo.PNode;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Apr 13, 2006
 * Time: 12:02:41 AM
 * Copyright (c) Apr 13, 2006 by Sam Reid
 */

public class FaucetConnector extends VerticalConnector {
    private FaucetControlPanelPNode faucetControlPanelPNode;
    private FaucetGraphic faucetGraphic;

    public FaucetConnector( FaucetControlPanelPNode faucetControlPanelPNode, FaucetGraphic faucetGraphic ) {
        super( faucetGraphic.getImagePNode(), faucetControlPanelPNode );
        this.faucetControlPanelPNode = faucetControlPanelPNode;
        this.faucetGraphic = faucetGraphic;

        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                update();
            }
        };
        faucetGraphic.addPropertyChangeListener( PNode.PROPERTY_BOUNDS, listener );
        faucetGraphic.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, listener );
        faucetControlPanelPNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, listener );
        faucetControlPanelPNode.addPropertyChangeListener( PNode.PROPERTY_BOUNDS, listener );
        try {
            setTexture( ImageLoader.loadBufferedImage( "images/silverwire.png" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        update();
    }

    protected void updateShape( Point2D r1c, Point2D r2c ) {

        if( faucetControlPanelPNode != null ) {
//            System.out.println( "faucetControlPanelPNode.getFullBounds() = " + faucetGraphic.getFullBounds() );
            double yMin = Math.min( r1c.getY(), r2c.getY() );
            double yMax = Math.max( r1c.getY(), r2c.getY() );
            double height = yMax - yMin;
//            Rectangle2D.Double rect = new Rectangle2D.Double( faucetGraphic.getFullBounds().getX(), yMin, 20, height );
            Rectangle2D.Double rect = new Rectangle2D.Double( faucetGraphic.getFullBounds().getX() + faucetGraphic.getFullBounds().getWidth() * 0.33,
                                                              yMin, 20, height );
            super.setPathTo( rect );
        }
    }
}
