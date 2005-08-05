/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.CursorHandler;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.RampObject;
import edu.colorado.phet.theramp.model.Block;
import edu.colorado.phet.theramp.model.Ramp;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 10:55:39 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class BlockGraphic extends PNode {
    private RampModule module;
    private RampPanel rampPanel;
    private SurfaceGraphic rampGraphic;
    private SurfaceGraphic groundGraphic;
    private Block block;
    private PImage graphic;
    private ThresholdedDragAdapter mouseListener;
//    private LocationDebugGraphic locationDebugGraphic;
    private RampObject rampObject;
    private PImage wheelGraphic;

    public BlockGraphic( final RampModule module, RampPanel rampPanel, SurfaceGraphic rampGraphic, SurfaceGraphic groundGraphic, Block block, RampObject rampObject ) {
        super();
        this.module = module;
        this.rampPanel = rampPanel;
        this.rampGraphic = rampGraphic;
        this.groundGraphic = groundGraphic;
        this.block = block;
        this.rampObject = rampObject;

        final RampPhysicalModel physicalModel = module.getRampPhysicalModel();

        try {
            wheelGraphic = new PImage( ImageLoader.loadBufferedImage( "images/skateboard.png" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        wheelGraphic.setVisible( false );
        addChild( wheelGraphic );

        graphic = new PImage();
        //graphic.setCursorHand();
        addChild( graphic );
        setObject( rampObject );

//        locationDebugGraphic = new LocationDebugGraphic( getComponent(), 10 );
        block.addListener( new Block.Adapter() {
            public void positionChanged() {
                updateBlock();
            }

            public void massChanged() {
                updateBlock();
            }

            public void staticFrictionChanged() {
                updateBlock();
            }

            public void kineticFrictionChanged() {
                updateBlock();
            }
        } );

        PInputEventListener dragHandler = new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent e ) {
                super.mouseDragged( e );
//                Point2D ctr = getCenter();
                double x = e.getPositionRelativeTo( graphic ).getX();
//                double x = e.getPositionRelativeTo( BlockGraphic.this ).getX();
                double ctrX = graphic.getBounds().getCenterX();
                double dx = x - ctrX;

//                System.out.println( "x=" + x + ", ctrX=" + ctrX + ", dx = " + dx );
                double appliedForce = dx / RampModule.FORCE_LENGTH_SCALE;
                physicalModel.setAppliedForce( appliedForce );
                module.record();
            }

            public void mouseReleased( PInputEvent event ) {
                super.mouseReleased( event );
                physicalModel.setAppliedForce( 0.0 );
            }
        };
        addInputEventListener( dragHandler );
        addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );

        //todo piccolo add thresholded drag adapter.
//        this.mouseListener = new ThresholdedDragAdapter( mia, 10, 0, 1000 );

        rampGraphic.getSurface().addObserver( new SimpleObserver() {
            public void update() {
                updateBlock();
            }
        } );

    }

    public void updateBlock() {
        double mass = block.getMass();
        double scale = rampObject.getScale();

        double preferredMass = rampObject.getMass();
        double sy = scale * mass / preferredMass;
        AffineTransform transform = createTransform( scale, sy );
        transform.concatenate( AffineTransform.getScaleInstance( scale, sy ) );
        transform.concatenate( AffineTransform.getTranslateInstance( 0, getOffsetYPlease() ) );
        graphic.setTransform( transform );
        if( isFrictionless() ) {
            wheelGraphic.setVisible( true );
            AffineTransform wheelTx = createTransform( block.getPositionInSurface(), 1.0, 1.0, wheelGraphic.getImage().getWidth( null ), wheelGraphic.getImage().getHeight( null ) );
//            wheelTx.concatenate( AffineTransform.getScaleInstance( scale, sy ) );
//            wheelTx.concatenate( AffineTransform.getTranslateInstance( 0, getYOffset() ) );
            wheelGraphic.setTransform( wheelTx );
//            this.graphic.setVisible( false );
        }
        else {
            wheelGraphic.setVisible( false );
//            this.graphic.setVisible( true );
        }
        repaint();
    }

    private boolean isFrictionless() {
        return block.getStaticFriction() == 0 || block.getKineticFriction() == 0;
    }

    private double getOffsetYPlease() {
        if( isFrictionless() ) {
            return rampObject.getYOffset() - getSkateboardHeight();
        }
        else {
            return rampObject.getYOffset();
        }
    }

    private double getSkateboardHeight() {
        return wheelGraphic.getImage().getHeight( null );
    }

    private AffineTransform createTransform( double x, double scaleX, double fracSize, int imageWidth, int imageHeight ) {
        return getCurrentSurfaceGraphic().createTransform( x, new Dimension( (int)( imageWidth * scaleX ), (int)( imageHeight * fracSize ) ) );
    }

    private AffineTransform createTransform( double scaleX, double fracSize ) {
//        return rampGraphic.createTransform( block.getPosition(), new Dimension( (int)( graphic.getImage().getWidth() * scaleX ), (int)( graphic.getImage().getHeight() * fracSize ) ) );
        return getCurrentSurfaceGraphic().createTransform( block.getPositionInSurface(), new Dimension( (int)( graphic.getImage().getWidth( null ) * scaleX ), (int)( graphic.getImage().getHeight( null ) * fracSize ) ) );
    }

    private void setImage( BufferedImage image ) {
        graphic.setImage( image );
        updateBlock();
    }

    public int getObjectWidthView() {
        return (int)( graphic.getImage().getWidth( null ) * rampObject.getScale() );//TODO scaling will hurt this.
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

    public SurfaceGraphic getCurrentSurfaceGraphic() {
        if( block.getSurface() instanceof Ramp ) {
            return rampGraphic;
        }
        else {
            return groundGraphic;
        }
    }

    public PImage getObjectGraphic() {
        return graphic;
    }

    public PBounds getBlockBounds() {
        return graphic.getFullBounds();
    }
}