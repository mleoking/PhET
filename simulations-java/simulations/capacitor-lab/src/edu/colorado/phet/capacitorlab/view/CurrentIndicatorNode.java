// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeListener;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.activities.FadeOutActivity;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;

/**
 * Arrow and electron that indicates the direction of current flow.
 * Visibility of this node is handled via its transparency.
 * The node appears while current is flowing.
 * When current stops flowing, the node fades out over a period of time.
 * <p>
 * By default, the arrow points to the left.
 * Origin is at the geometric center, so that this node can be easily 
 * rotated when current changes direction.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CurrentIndicatorNode extends PhetPNode {
    
    // arrow properties
    private static final double ARROW_LENGTH = 175;
    private static final double ARROW_HEAD_WIDTH = 60;
    private static final double ARROW_HEAD_HEIGHT = 50;
    private static final double ARROW_TAIL_WIDTH = 0.4 * ARROW_HEAD_WIDTH;
    private static final Point2D ARROW_TIP_LOCATION = new Point2D.Double( 0, 0 ); // origin at the tip
    private static final Point2D ARROW_TAIL_LOCATION = new Point2D.Double( ARROW_LENGTH, 0 );
    private static final Color ARROW_COLOR = new Color( 83, 200, 236 );
    
    // electron properties
    private static final double ELECTRON_DIAMETER = 0.8 * ARROW_TAIL_WIDTH;
    private static final Paint ELECTRON_FILL_COLOR = new RoundGradientPaint( 0, 0, Color.WHITE, new Point2D.Double( ELECTRON_DIAMETER / 4, ELECTRON_DIAMETER / 4 ), ARROW_COLOR );
    private static final Stroke ELECTRON_STROKE = new BasicStroke( 1f );
    private static final Color ELECTRON_STROKE_COLOR = Color.BLACK;
    private static final Color ELECTRON_MINUS_COLOR = Color.BLACK;
    private static final double ELECTRON_MINUS_WIDTH = 0.6 * ELECTRON_DIAMETER;
    private static final double ELECTRON_MINUS_HEIGHT = 0.1 * ELECTRON_DIAMETER;
    
    // transparency
    private static final float TRANSPARENCY = 0.75f; // range is 0-1f
    private static final long FADEOUT_DURATION = 500; // ms
    private static final long FADEOUT_STEP_RATE = 10; // ms
    
    private final BatteryCapacitorCircuit circuit;
    private final double positiveOrientation;
    private PActivity fadeOutActivity;

    /**
     * Constructor.
     * Rotation angles should be set such that +dV/dt indicates 
     * current flow towards the positive terminal of the battery.
     * 
     * @param circuit circuit model
     * @param positiveOrientation rotation angle for +dV/dt (radians)
     */
    public CurrentIndicatorNode( BatteryCapacitorCircuit circuit, double positiveOrientation ) {
        
        this.circuit = circuit;
        this.positiveOrientation = positiveOrientation;
        
        ArrowNode arrowNode = new ArrowNode( ARROW_TAIL_LOCATION, ARROW_TIP_LOCATION, ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
        arrowNode.setPaint( ARROW_COLOR );
        addChild( arrowNode );
        
        SphericalNode electronNode = new SphericalNode( ELECTRON_DIAMETER, ELECTRON_FILL_COLOR, false /* convertToImage */ );
        electronNode.setStroke( ELECTRON_STROKE );
        electronNode.setStrokePaint( ELECTRON_STROKE_COLOR );
        addChild( electronNode );
        
        // Use a PPath, because PText("-") can't be accurately centered.
        MinusNode minusNode = new MinusNode( ELECTRON_MINUS_WIDTH, ELECTRON_MINUS_HEIGHT, ELECTRON_MINUS_COLOR );
        addChild( minusNode );
        
        // layout
        double x = -arrowNode.getFullBoundsReference().getWidth() / 2;
        double y = 0;
        arrowNode.setOffset( x, y );
        x = arrowNode.getFullBoundsReference().getMaxX() - ( 0.6 * ( arrowNode.getFullBoundsReference().getWidth() - ARROW_HEAD_HEIGHT ) );
        y = arrowNode.getFullBoundsReference().getCenterY();
        electronNode.setOffset( x, y );
        x = electronNode.getFullBoundsReference().getCenterX();
        y = electronNode.getFullBoundsReference().getCenterY(); 
        minusNode.setOffset( x, y );
        
        // listeners
        circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeListener() {
            public void circuitChanged() {
                if ( isVisible() ) {
                    updateTransparency();
                    updateOrientation();
                }
            }
        } );
        
        updateTransparency();
    }
    
    /*
     * Updates the transparency of this node based on the current amplitude.
     * Any non-zero current amplitude results in a constant transparency.
     * When current amplitude goes to zero, a Piccolo activity is scheduled
     * which gradually fades this node to fully transparent, making it 
     * effectively invisible.
     */
    private void updateTransparency() {
        
        // if a fade out is in progress, stop it without fully fading out
        if ( fadeOutActivity != null ) {
            fadeOutActivity.terminate( PActivity.TERMINATE_WITHOUT_FINISHING );
            fadeOutActivity = null;
        }
        
        double currentAmplitude = circuit.getCurrentAmplitude();
        if ( currentAmplitude != 0 ) {
            // constant transparency for non-zero current amplitude
            setTransparency( TRANSPARENCY );
        }
        else {
            if ( getRoot() == null ) {
                // node is not in the scenegraph, make it invisible immediately
                setTransparency( 0f );
            }
            else {
                // gradually fade out
                fadeOutActivity = new FadeOutActivity( this, FADEOUT_DURATION, FADEOUT_STEP_RATE );
                fadeOutActivity.setDelegate( new PActivityDelegate() {

                    public void activityFinished( PActivity activity ) {
                        fadeOutActivity = null;
                    }
                    
                    public void activityStarted( PActivity activity ) {}

                    public void activityStepped( PActivity activity ) {}
                });
                getRoot().addActivity( fadeOutActivity ); // schedule the activity
            }
        }
    }

    /*
     * Updates the orientation of the current indicator, based on the sign of the current amplitude.
     */
    private void updateOrientation() {
        final double currentAmplitude = circuit.getCurrentAmplitude();
        if ( currentAmplitude != 0 ) {
            setRotation( ( circuit.getCurrentAmplitude() > 0 ) ? positiveOrientation : positiveOrientation + Math.PI );
        }
    }
}
