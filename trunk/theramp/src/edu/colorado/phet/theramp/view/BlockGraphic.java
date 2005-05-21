/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.RampObject;
import edu.colorado.phet.theramp.common.LocationDebugGraphic;
import edu.colorado.phet.theramp.model.Block;
import edu.colorado.phet.theramp.model.RampModel;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 10:55:39 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class BlockGraphic extends CompositePhetGraphic {
    private RampModule module;
    private RampPanel rampPanel;
    private RampGraphic rampGraphic;
    private RampGraphic groundGraphic;
    private Block block;
    private PhetImageGraphic graphic;
    private ThresholdedDragAdapter mouseListener;
    private LocationDebugGraphic locationDebugGraphic;
    private RampObject rampObject;

    public BlockGraphic( final RampModule module, RampPanel rampPanel, RampGraphic rampGraphic, RampGraphic groundGraphic, Block block, RampObject rampObject ) {
        super( rampPanel );
        this.module = module;
        this.rampPanel = rampPanel;
        this.rampGraphic = rampGraphic;
        this.groundGraphic = groundGraphic;
        this.block = block;
        this.rampObject = rampObject;

        final RampModel model = rampPanel.getRampModule().getRampModel();

        graphic = new PhetImageGraphic( getComponent() );
        addGraphic( graphic );
        setObject( rampObject );

        locationDebugGraphic = new LocationDebugGraphic( getComponent(), 10 );
        block.addListener( new Block.Adapter() {
            public void positionChanged() {
                updateBlock();
            }

            public void massChanged() {
                updateBlock();
            }
        } );

        MouseInputAdapter mia = new MouseInputAdapter() {
            public void mouseDragged( MouseEvent e ) {
                Point ctr = getCenter();
                double dx = e.getPoint().x - ctr.x;
                double appliedForce = dx / RampModule.FORCE_LENGTH_SCALE;
                model.setAppliedForce( appliedForce );
                module.record();
            }

            // implements java.awt.event.MouseListener
            public void mouseReleased( MouseEvent e ) {
                model.setAppliedForce( 0.0 );
            }
        };

        this.mouseListener = new ThresholdedDragAdapter( mia, 10, 0, 1000 );
        addMouseInputListener( this.mouseListener );
        setCursorHand();
        rampGraphic.getSurface().addObserver( new SimpleObserver() {
            public void update() {
                updateBlock();
            }
        } );

    }

    public Point getCenter() {
        Point2D ctr = getNetTransform().transform( new Point2D.Double( graphic.getBounds().getWidth() / 2, graphic.getBounds().getHeight() / 2 ), null );
        return new Point( (int)ctr.getX(), (int)ctr.getY() );
    }

    public void updateBlock() {
        setAutorepaint( false );

        double mass = block.getMass();
        double scale = rampObject.getScale();

        double preferredMass = rampObject.getMass();
        double sy = scale * mass / preferredMass;
//        System.out.println( "sy = " + sy );
        AffineTransform transform = createTransform( scale, sy );
        transform.concatenate( AffineTransform.getScaleInstance( scale, sy ) );
        setTransform( transform );
        repaint();
    }

    private AffineTransform createTransform( double scaleX, double fracSize ) {
//        return rampGraphic.createTransform( block.getPosition(), new Dimension( (int)( graphic.getImage().getWidth() * scaleX ), (int)( graphic.getImage().getHeight() * fracSize ) ) );
        return getCurrentSurfaceGraphic().createTransform( block.getPosition(), new Dimension( (int)( graphic.getImage().getWidth() * scaleX ), (int)( graphic.getImage().getHeight() * fracSize ) ) );
    }

    private void setImage( BufferedImage image ) {
        graphic.setImage( image );
        updateBlock();
    }

    public int getObjectWidthView() {
        return (int)( graphic.getImage().getWidth() * rampObject.getScale() );//TODO scaling will hurt this.
    }

    public Block getBlock() {
        return block;
    }

    public void setObject( RampObject rampObject ) {
        this.rampObject = rampObject;
        try {
            setImage( rampObject.getImage() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        updateBlock();
    }

    public RampGraphic getCurrentSurfaceGraphic() {
        if( block.getSurface() == rampGraphic.getSurface() ) {
            return rampGraphic;
        }
        else {
            return groundGraphic;
        }
    }
}
