/*  */
package edu.colorado.phet.waveinterference.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 12:00:58 AM
 */

public class FaucetGraphic extends PhetPNode {
    private PImage image;
    private ArrayList drops = new ArrayList();
    private FaucetData faucetData;
    private PNode waterChild;
    private WaveModel waveModel;
    private Oscillator oscillator;
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private double dropHeight = 100;
    private double dropSpeed = 100;
    private double lastTime;
    private boolean enabled = true;
    private boolean clipDrops = true;
    private ArrayList listeners = new ArrayList();
    private HorizontalFaucetDragHandler horizontalDragHandler;
    private VerticalFaucetDragHandler verticalDragHandler;
    private CursorHandler cursorHandler;

    public FaucetGraphic( WaveModel waveModel, Oscillator oscillator, LatticeScreenCoordinates latticeScreenCoordinates ) {
        this( waveModel, oscillator, latticeScreenCoordinates, new MSFaucetData2() );
    }

    public FaucetGraphic( WaveModel waveModel, final Oscillator oscillator, final LatticeScreenCoordinates latticeScreenCoordinates, FaucetData faucetData ) {
        this.waveModel = waveModel;
        this.oscillator = oscillator;
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        this.faucetData = faucetData;
        image = PImageFactory.create( faucetData.getFaucetImageName() );

        waterChild = new PNode();
        addChild( waterChild );//so they appear behind
        addChild( image );

        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                updateLocation();
            }
        } );
        updateLocation();
        oscillator.addListener( new Oscillator.Adapter() {
            public void locationChanged() {
                updateLocation();
            }
        } );
        FaucetOnOffControl faucetOnOffButton = new FaucetOnOffControl( this );
        faucetOnOffButton.setOffset( 0,-faucetOnOffButton.getFullBounds().getHeight() );
        addChild( faucetOnOffButton );

        horizontalDragHandler = new HorizontalFaucetDragHandler( this );
        verticalDragHandler = new VerticalFaucetDragHandler( this );
        cursorHandler = new CursorHandler( CursorHandler.HAND );
//        addInputEventListener( horizontalDragHandler );
//        addInputEventListener( verticalDragHandler );
    }

    public void setHorizontalDrag() {
        removeVerticicalDrag();
        addHorizontalDrag();

    }

    public void setVerticalDrag() {
        removeHorizontalDrag();
        addVerticalDrag();

    }

    public void setDragDisabled() {
        removeHorizontalDrag();
        removeVerticicalDrag();
    }

    private void addVerticalDrag() {
        addInputEventListener( verticalDragHandler );
        addInputEventListener( cursorHandler );
    }

    private void removeHorizontalDrag() {
        removeInputEventListener( horizontalDragHandler );
        removeInputEventListener( cursorHandler );
    }

    private void addHorizontalDrag() {
        addInputEventListener( horizontalDragHandler );
        addInputEventListener( cursorHandler );
    }

    private void removeVerticicalDrag() {
        removeInputEventListener( verticalDragHandler );
        removeInputEventListener( cursorHandler );
    }

    public PImage getImagePNode() {
        return image;
    }

    private Point2D.Double getDistToOpeningOffset() {
        return new Point2D.Double( faucetData.getDistToOpeningX( image.getImage() ), faucetData.getDistToOpeningY( image.getImage() ) );
    }

    private void updateLocation() {
        Point2D.Double pt = getDistToOpeningOffset();
        Point2D screenLocationForOscillator = getOscillatorScreenCoordinates();
        setOffset( screenLocationForOscillator.getX() - pt.getX(), screenLocationForOscillator.getY() - pt.getY() - dropHeight );
    }

    private Point2D getOscillatorScreenCoordinates() {
        return latticeScreenCoordinates.toScreenCoordinates( oscillator.getCenterX(), oscillator.getCenterY() );
    }

    public void step() {
        if ( isEnabled() ) {
            if ( isRightBeforeReleaseTime( lastTime ) && isRightAfterReleaseTime( oscillator.getTime() ) ) {
                addDrop();
            }
        }
        updateDrops();
        lastTime = oscillator.getTime();
    }

    private boolean isRightAfterReleaseTime( double time ) {
        double releaseTime = getNearestReleaseTime( time );
        if ( time >= releaseTime && Math.abs( releaseTime - time ) < oscillator.getPeriod() / 4 ) {
            return true;
        }
        return false;
    }

    private boolean isRightBeforeReleaseTime( double time ) {
        double releaseTime = getNearestReleaseTime( time );
        if ( time < releaseTime && Math.abs( releaseTime - time ) < oscillator.getPeriod() / 4 ) {
            return true;
        }
        return false;
    }

    private double getNearestReleaseTime( double time ) {
        double nReal = time / oscillator.getPeriod() - 0.25 + getTimeToHitTarget() / oscillator.getPeriod();
        int n = (int) Math.round( nReal );
        return oscillator.getPeriod() / 4 - getTimeToHitTarget() + n * oscillator.getPeriod();
    }

    private void updateDrops() {
        for ( int i = 0; i < drops.size(); i++ ) {
            WaterDropGraphic waterDropGraphic = (WaterDropGraphic) drops.get( i );
            waterDropGraphic.update( oscillator.getTime() - lastTime );
            if ( waterDropGraphic.readyToRemove() ) {
//                System.out.println( "FaucetGraphic.updateDrops" );
                removeDrop( (WaterDropGraphic) drops.get( i ) );
                i--;

                //consider this a collision for purposes of starting waves.
                oscillator.setEnabled( this.isEnabled() );
            }
        }
    }

    private void removeDrop( WaterDropGraphic waterDropGraphic ) {
        drops.remove( waterDropGraphic );
        waterChild.removeChild( waterDropGraphic );
    }

    private void addDrop() {
        WaterDropGraphic waterDropGraphic = new WaterDropGraphic( dropSpeed, clipDrops, oscillator.getAmplitude() );
        double x = faucetData.getDistToOpeningX( image.getImage() ) - waterDropGraphic.getFullBounds().getWidth() / 2.0;
        double y = faucetData.getDistToOpeningY( image.getImage() ) - waterDropGraphic.getFullBounds().getHeight() / 2.0;
        waterDropGraphic.setOffset( x, y );
//        waterDropGraphic.setScale( );
        addDrop( waterDropGraphic );
    }

    private double getTimeToHitTarget() {
        return dropHeight / dropSpeed;
    }

    private void removeAllDrops() {
        drops.clear();
        waterChild.removeAllChildren();
    }

    private void addDrop( WaterDropGraphic waterDropGraphic ) {
        drops.add( waterDropGraphic );
        waterChild.addChild( waterDropGraphic );
    }

    private double getDistanceFromParentOriginToOscillatorY() {
        return -( getOffset().getY() - getOscillatorScreenCoordinates().getY() );
    }

    public double getDropHeight() {
        return dropHeight;
    }

    public void setDropHeight( double value ) {
        this.dropHeight = value;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled( boolean selected ) {
        this.enabled = selected;
        if ( !enabled && drops.size() == 0 ) {
            oscillator.setEnabled( false );
        }
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.enabledStateChanged();
        }
    }

    public Oscillator getOscillator() {
        return oscillator;
    }

    public void setClipDrops( boolean clipDrops ) {
        this.clipDrops = clipDrops;
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public LatticeScreenCoordinates getLatticeScreenCoordinates() {
        return latticeScreenCoordinates;
    }

    public void reset() {
        while ( drops.size() > 0 ) {
            removeDrop( (WaterDropGraphic) drops.get( 0 ) );
        }
    }

    public static interface Listener {
        void enabledStateChanged();
    }

    class WaterDropGraphic extends PNode {
        private PImage image;
        private double speed;
        private double amplitude;
        private boolean clip = false;

        public WaterDropGraphic( double speed, boolean clip, double amplitude ) {
            this.clip = clip;
            this.speed = speed;
            this.amplitude = amplitude;
//            image = PImageFactory.create( "wave-interference/images/raindrop1.png" );
            double scale = amplitude;
            try {
                BufferedImage origImage = ImageLoader.loadBufferedImage( "wave-interference/images/raindrop1.png" );
                image = new PImage( BufferedImageUtils.rescaleXMaintainAspectRatio( origImage, (int) Math.max( scale * origImage.getWidth(), 1 ) ) );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
//            image.setImage( BufferedImageUtils.rescaleFractional( image.get) );
            addChild( image );
        }

        public void update( double dt ) {
//            System.out.println( "FaucetGraphic$WaterDropGraphic.update" );
            offset( 0, speed * dt );
        }

        public void fullPaint( PPaintContext paintContext ) {
            Shape origClip = paintContext.getLocalClip();
            //todo: works under a variety of conditions, not fully tested
            Rectangle rect = new Rectangle( 0, 0, (int) getFullBounds().getWidth(), (int) ( getFullBounds().getHeight() / 2 - getOffset().getY() + getDistanceFromParentOriginToOscillatorY() - image.getFullBounds().getHeight() / 2 ) );
            localToParent( rect );
            if ( clip ) {
                paintContext.pushClip( rect );
            }
            super.fullPaint( paintContext );
            if ( clip ) {
                paintContext.popClip( origClip );
            }
        }

        //todo works under a variety of conditions, not fully tested.
        public boolean readyToRemove() {
            Rectangle rect = new Rectangle( 0, 0, (int) getFullBounds().getWidth(), (int) ( getFullBounds().getHeight() / 2 - getOffset().getY() + getDistanceFromParentOriginToOscillatorY() - image.getFullBounds().getHeight() / 2 ) );
//            Rectangle rect = new Rectangle( 0, 0, (int)getFullBounds().getWidth(), (int)( getFullBounds().getHeight() / 2 - getOffset().getY() + getDistanceFromParentOriginToOscillatorY() - image.getFullBounds().getHeight() ) );
            localToParent( rect );
            PBounds bounds = getFullBounds();
            return !bounds.intersects( rect );
        }
    }

    public static void debugNearestTime( FaucetGraphic faucetGraphic, double t ) {
        double nearest = faucetGraphic.getNearestReleaseTime( t );
        System.out.println( "t = " + t + ", nearest release = " + nearest );
    }

    public double getDropSpeed() {
        return dropSpeed;
    }

    public void setDropSpeed( double dropSpeed ) {
        this.dropSpeed = dropSpeed;
    }
}
