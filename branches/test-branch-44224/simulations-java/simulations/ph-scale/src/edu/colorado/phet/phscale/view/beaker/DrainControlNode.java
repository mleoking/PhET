/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view.beaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.LiquidDescriptor;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.colorado.phet.phscale.model.LiquidDescriptor.LiquidDescriptorAdapter;
import edu.colorado.phet.phscale.view.beaker.FaucetControlNode.FaucetControlListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * DrainControlNode is the faucet used to drain the beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DrainControlNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final PDimension LIQUID_COLUMN_SIZE = PHScaleConstants.LIQUID_COLUMN_SIZE;
    private static final double MIN_LIQUID_COLUMN_WIDTH = PHScaleConstants.MIN_LIQUID_COLUMN_WIDTH;
    
    private static final Color PIPE_FILL_COLOR = new Color( 233, 184, 0 ); // mustard yellow
    private static final Color PIPE_STROKE_COLOR = Color.BLACK;
    private static final Stroke PIPE_STROKE = new BasicStroke( 2f );
    
    private static final LiquidDescriptor WATER = LiquidDescriptor.getWater();

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final FaucetControlNode _faucetControlNode;
    private final Rectangle2D _liquidColumnShape;
    private final PPath _liquidColumnNode;
    private final PPath _waterColumnNode; // put water behind liquid so that it looks the same as in beaker
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    private boolean _notifyEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DrainControlNode( Liquid liquid, double maxDrainRate ) {
        super();
        
        _notifyEnabled = true;
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                update();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        WATER.addLiquidDescriptorListener( new LiquidDescriptorAdapter() {
            public void colorChanged( Color color ) {
                _waterColumnNode.setPaint( WATER.getColor() );
            }
        } );
        
        _faucetControlNode = new FaucetControlNode( FaucetControlNode.ORIENTATION_LEFT, maxDrainRate);
        _faucetControlNode.addFaucetControlListener( new FaucetControlListener() {
            public void valueChanged() {
                if ( _notifyEnabled ) {
                    _liquid.setDrainRate( _faucetControlNode.getRate() );
                }
            }
        } );

        _liquidColumnShape = new Rectangle2D.Double();
        _liquidColumnNode = new PPath( _liquidColumnShape );
        _liquidColumnNode.setStroke( null );
        _liquidColumnNode.setVisible( _faucetControlNode.isOn() );
        _liquidColumnNode.setPickable( false );
        _liquidColumnNode.setChildrenPickable( false );
        
        _waterColumnNode = new PPath( _liquidColumnShape );
        _waterColumnNode.setPaint( WATER.getColor() );
        _waterColumnNode.setStroke( null );
        _waterColumnNode.setVisible( _faucetControlNode.isOn() );
        _waterColumnNode.setPickable( false );
        _waterColumnNode.setChildrenPickable( false );
        
        Shape pipeShape = createPipeShape();
        PPath pipeNode = new PPath( pipeShape );
        pipeNode.setPaint( PIPE_FILL_COLOR );
        pipeNode.setStrokePaint( PIPE_STROKE_COLOR );
        pipeNode.setStroke( PIPE_STROKE );
        pipeNode.setPickable( false );
        pipeNode.setChildrenPickable( false );
        
        addChild( pipeNode );
        addChild( _waterColumnNode );
        addChild( _liquidColumnNode );
        addChild( _faucetControlNode );
        
        _faucetControlNode.setOffset( 0, 0 );
        _liquidColumnNode.setOffset( _faucetControlNode.getFullBoundsReference().getMinX() + 18, _faucetControlNode.getFullBoundsReference().getMaxY() - 2 );
        _waterColumnNode.setOffset( _liquidColumnNode.getOffset() );
        pipeNode.setOffset( _faucetControlNode.getFullBoundsReference().getMaxX() - 2, 37 );

        update();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public boolean isOn() {
        return _faucetControlNode.isOn();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void update() {
        
        _notifyEnabled = false;
        _faucetControlNode.setRate( _liquid.getDrainRate() );
        _faucetControlNode.setEnabled( !_liquid.isEmpty() );
        _notifyEnabled = true;
        
        _liquidColumnNode.setPaint( _liquid.getColor() );
        _liquidColumnNode.setVisible( _liquid.isDraining() );
        _waterColumnNode.setVisible( _liquid.isDraining() );
        
        // shape of the water column
        final double percentOn = _faucetControlNode.getPercentOn();
        final double columnWidth = MIN_LIQUID_COLUMN_WIDTH + ( percentOn * ( LIQUID_COLUMN_SIZE.getWidth() - MIN_LIQUID_COLUMN_WIDTH ) );
        _liquidColumnShape.setRect( -columnWidth/2, 0, columnWidth, LIQUID_COLUMN_SIZE.getHeight() );
        _liquidColumnNode.setPathTo( _liquidColumnShape );
        _waterColumnNode.setPathTo( _liquidColumnShape );
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /*
     * Creates the shape of the pipe, using constructive area geometry.
     * This shape is specific to the faucet image.
     */
    private Shape createPipeShape() {
        
        final double pipeWidth = 31;
        final double horizontalLength = 20; // horizontal section of pipe
        final double verticalLength = 50; // vertical section of pipe
        final double elbowRadius = 20; // elbow where the horizontal and vertical sections are joined
        assert( elbowRadius < pipeWidth );
        
        Shape horizontalPipe = new Rectangle2D.Double( 0, 0, horizontalLength + elbowRadius, pipeWidth );
        Shape verticalPipe = new Rectangle2D.Double( horizontalLength, -verticalLength, pipeWidth, verticalLength + elbowRadius );
        Shape elbow = new RoundRectangle2D.Double( horizontalLength, 0, pipeWidth, pipeWidth, elbowRadius, elbowRadius );
        
        Area area = new Area( horizontalPipe );
        area.add( new Area( verticalPipe ) );
        area.add( new Area( elbow ) );
        return area;
    }
}
