/*  */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.model.Block;
import edu.colorado.phet.theramp.model.Ramp;
import edu.colorado.phet.theramp.model.RampObject;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
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
 */

public class BlockGraphic extends PNode {
    private RampModule module;
    private RampPanel rampPanel;
    private SurfaceGraphic rampGraphic;
    private SurfaceGraphic groundGraphic;
    private Block block;
    private PImage imageGraphic;
    private ThresholdedDragAdapter mouseListener;
    private RampObject rampObject;
    private PImage wheelGraphic;
    private double viewScale = 0.5;
    private double scaleDownInputForces = 0.1;

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
            wheelGraphic = new PImage( ImageLoader.loadBufferedImage( "the-ramp/images/skateboard.png" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        imageGraphic = new PImage();
        addChild( imageGraphic );
        setObject( rampObject );

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

        PBasicInputEventHandler dragHandler = new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent e ) {
                super.mouseDragged( e );
                double x = e.getPositionRelativeTo( imageGraphic ).getX();
                double ctrX = imageGraphic.getBounds().getCenterX();
                double dx = x - ctrX;

                double appliedForce = dx / RampModule.FORCE_LENGTH_SCALE * viewScale * scaleDownInputForces;
                physicalModel.setAppliedForce( appliedForce );
                module.record();
            }

            public void mouseReleased( PInputEvent event ) {
                super.mouseReleased( event );
                physicalModel.setAppliedForce( 0.0 );
            }
        };

        ThresholdedPDragAdapter thresholdedPDragAdapter = new ThresholdedPDragAdapter( dragHandler, 10, 0, 1000 );

//        addInputEventListener( dragHandler );
        addInputEventListener( thresholdedPDragAdapter );
        addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );

        rampGraphic.getSurface().addObserver( new SimpleObserver() {
            public void update() {
                updateBlock();
            }
        } );
        imageGraphic.setPaint( Color.black );
    }

    public void updateBlock() {
        double mass = block.getMass();
        double scale = rampObject.getScale();

        double preferredMass = rampObject.getMass();
        double sy = scale * mass / preferredMass;
        AffineTransform transform = createTransform( scale, sy );
        transform.concatenate( AffineTransform.getScaleInstance( scale, sy ) );
        transform.concatenate( AffineTransform.getTranslateInstance( 0, getOffsetYPlease() ) );
        imageGraphic.setTransform( transform );
        if( isFrictionless() ) {
            if( !isAncestorOf( wheelGraphic ) ) {
                addChild( 0, wheelGraphic );
            }
            wheelGraphic.setVisible( true );
            AffineTransform wheelTx = createTransform( block.getPositionInSurface(), 1.0, 1.0, wheelGraphic.getImage().getWidth( null ), wheelGraphic.getImage().getHeight( null ) );
//            wheelTx.concatenate( AffineTransform.getScaleInstance( scale, sy ) );
//            wheelTx.concatenate( AffineTransform.getTranslateInstance( 0, getYOffset() ) );
            wheelGraphic.setTransform( wheelTx );
//            this.graphic.setVisible( false );
        }
        else {
            if( isAncestorOf( wheelGraphic ) ) {
                removeChild( wheelGraphic );
            }
//            wheelGraphic.setVisible( false );
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
        return getCurrentSurfaceGraphic().createTransform( block.getPositionInSurface(), new Dimension( (int)( imageGraphic.getImage().getWidth( null ) * scaleX ), (int)( imageGraphic.getImage().getHeight( null ) * fracSize ) ) );
    }

    private void setImage( BufferedImage image ) {
        imageGraphic.setImage( image );
        updateBlock();
    }

    public int getObjectWidthView() {
        return (int)( imageGraphic.getImage().getWidth( null ) * rampObject.getScale() );//TODO scaling will hurt this.
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
        return imageGraphic;
    }

    public PBounds getBlockBounds() {
        return imageGraphic.getFullBounds();
    }

    public void paintRed() {
        System.out.println( "BlockGraphic.paintRed" );
        MakeDuotoneImageOp duotone = new MakeDuotoneImageOp( Color.red );
        BufferedImage dest = duotone.filter( (BufferedImage)imageGraphic.getImage(), null );
        imageGraphic.setImage( dest );
    }

    public void restoreOriginalImage() {
        System.out.println( "BlockGraphic.restoreOriginalImage" );
        try {
            setImage( rampObject.getImage() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}