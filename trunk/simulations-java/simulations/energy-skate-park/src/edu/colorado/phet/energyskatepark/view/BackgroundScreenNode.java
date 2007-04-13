/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.PhetRootPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: Jun 1, 2006
 * Time: 12:25:26 PM
 * Copyright (c) Jun 1, 2006 by Sam Reid
 */

public class BackgroundScreenNode extends PhetPNode {
    private EnergySkateParkSimulationPanel canvas;
    private Image backgroundImage;
    private PNode floorGraphic;

    public BackgroundScreenNode( EnergySkateParkSimulationPanel simulationPanel, Image backgroundImage, PNode floorGraphic, PhetRootPNode rootNode ) {
        this.canvas = simulationPanel;
        this.backgroundImage = backgroundImage;
        this.floorGraphic = floorGraphic;

        floorGraphic.addPropertyChangeListener( PNode.PROPERTY_VISIBLE, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                update();
            }
        } );
        simulationPanel.addListener( new EnergySkateParkSimulationPanel.Listener() {
            public void zoomChanged() {
                update();
            }
        } );
        rootNode.addWorldTransformListener( new PropertyChangeListener() {

            public void propertyChange( PropertyChangeEvent evt ) {
//                System.out.println( "evt = " + evt );
                update();
            }
        } );
    }

    public void update() {
        if( backgroundImage == null ) {
            removeAllChildren();
        }
        else {
            BufferedImage image = BufferedImageUtils.toBufferedImage( backgroundImage );
            double yRatio = ( (double)canvas.getHeight() ) / image.getHeight();
            double xRatio = ( (double)canvas.getWidth() ) / image.getWidth();
            if( canvas.getHeight() > 0 && canvas.getWidth() > 0 ) {
                //choose smaller
                if( xRatio > yRatio ) {
                    image = BufferedImageUtils.rescaleXMaintainAspectRatio( image, canvas.getWidth() );
                }
                else {
                    image = BufferedImageUtils.rescaleYMaintainAspectRatio( image, canvas.getHeight() );
                }
            }
            removeAllChildren();
            PImage child = new PImage( image );
            double maxY = floorGraphic.getGlobalFullBounds().getMinY();
            if( floorGraphic.getVisible() ) {
                Point2D.Double loc = new Point2D.Double( 0, maxY );
                globalToLocal( loc );
                double dy = child.getFullBounds().getHeight() - maxY;
                child.translate( 0, -dy );
            }
            addChild( child );
        }
    }

    public void setBackground( Image image ) {
        if( image != null ) {
            BufferedImage im = BufferedImageUtils.toBufferedImage( image );//todo this is a bit of a hack for background color.
            canvas.setBackground( new Color( im.getRGB( im.getWidth() / 2, 30 ) ) );
        }
        else {
            canvas.setBackground( EnergySkateParkRootNode.SKY_COLOR );
        }
        if( this.backgroundImage != image ) {
            this.backgroundImage = image;
            update();
        }
    }
}
