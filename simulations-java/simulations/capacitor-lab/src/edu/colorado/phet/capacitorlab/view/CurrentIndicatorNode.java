/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.Battery.BatteryChangeAdapter;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.colorado.phet.capacitorlab.util.FadeOutActivity;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Arrow and electron that indicates the direction of current flow.
 * Transparency is modulated proportional to dV/dt (change in voltage over change in time).
 * When voltage goes to zero, this node fades out over a period of time.
 * <p>
 * Origin is at the geometric center, so that this node can be easily 
 * flipped (rotated) when current changes direction.
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
    private static final Font ELECTRON_MINUS_FONT = new PhetFont( Font.BOLD, 24 );
    
    // transparency
    private static final float MIN_TRANSPARENCY = 0.25f; // range is 0-1f
    private static final float MAX_TRANSPARENCY = 1.0f; // range is 0-1f
    private static final long FADEOUT_DURATION = 1500; // ms
    private static final long FADEOUT_STEP_RATE = 100; // ms
    
    private final BatteryCapacitorCircuit circuit;
    private PActivity fadeOutActivity;

    public CurrentIndicatorNode( BatteryCapacitorCircuit circuit ) {
        
        this.circuit = circuit;
        
        ArrowNode arrowNode = new ArrowNode( ARROW_TAIL_LOCATION, ARROW_TIP_LOCATION, ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
        arrowNode.setPaint( ARROW_COLOR );
        addChild( arrowNode );
        
        SphericalNode electronNode = new SphericalNode( ELECTRON_DIAMETER, ELECTRON_FILL_COLOR, false /* convertToImage */ );
        electronNode.setStroke( ELECTRON_STROKE );
        electronNode.setStrokePaint( ELECTRON_STROKE_COLOR );
        addChild( electronNode );
        
        PText minusNode = new PText( "-" );
        minusNode.setFont( ELECTRON_MINUS_FONT );
        addChild( minusNode );
        
        // layout
        double x = -arrowNode.getFullBoundsReference().getWidth() / 2;
        double y = 0;
        arrowNode.setOffset( x, y );
        x = arrowNode.getFullBoundsReference().getMaxX() - ( 0.6 * ( arrowNode.getFullBoundsReference().getWidth() - ARROW_HEAD_HEIGHT ) );
        y = arrowNode.getFullBoundsReference().getCenterY();
        electronNode.setOffset( x, y );
        x = electronNode.getFullBoundsReference().getCenterX() - ( minusNode.getFullBoundsReference().getWidth() / 2 );
        y = electronNode.getFullBoundsReference().getCenterY() - ( minusNode.getFullBoundsReference().getHeight() / 2 ) - 1; 
        minusNode.setOffset( x, y );
        
        // listeners
        circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeAdapter() {
            @Override
            public void currentChanged() {
                updateTransparency();
            }
        });
        circuit.getBattery().addBatteryChangeListener( new BatteryChangeAdapter() {
            @Override
            public void polarityChanged() {
                flipOrientation();
            }
        });
        
        updateTransparency();
    }
    
    private void updateTransparency() {
        
        // if a fade out is in progress, stop it without fully fading out
        if ( fadeOutActivity != null ) {
            fadeOutActivity.terminate( PActivity.TERMINATE_WITHOUT_FINISHING );
            fadeOutActivity = null;
        }
        
        double currentAmplitude = circuit.getCurrentAmplitude();
        if ( currentAmplitude == 0 ) {
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
        else {
            // modulate the alpha channel
            float transparency = (float) ( MIN_TRANSPARENCY + ( currentAmplitude * ( MAX_TRANSPARENCY - MIN_TRANSPARENCY ) ) );
            setTransparency( transparency );
        }
    }
    
    private void flipOrientation() {
        rotate( Math.PI );
    }
}
