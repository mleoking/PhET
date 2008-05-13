/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.glaciers.model.GPSReceiver;
import edu.colorado.phet.glaciers.model.Movable.MovableAdapter;
import edu.colorado.phet.glaciers.model.Movable.MovableListener;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.colorado.phet.glaciers.view.tools.AbstractToolOriginNode.LeftToolOriginNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * GPSReceiverNode is the visual representation of a GPS receiver.
 * This node is primarily for use during development as a debugging tool.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GPSReceiverNode extends AbstractToolNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String DISPLAY_FORMAT = "(x,z)=({0},{1})";
    private static final Font FONT = new PhetFont( 10 );
    private static final Border BORDER = BorderFactory.createLineBorder( Color.BLACK, 1 );
    private static final NumberFormat COORDINATE_FORMAT = new DefaultDecimalFormat( "0" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GPSReceiver _gps;
    private MovableListener _movableListener;
    private ValueNode _valueNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GPSReceiverNode( GPSReceiver gps, ModelViewTransform mvt, TrashCanIconNode trashCanIconNode ) {
        super( gps, mvt, trashCanIconNode );
        
        _gps = gps;
        _movableListener = new MovableAdapter() {
            public void positionChanged() {
                updateCoordinates();
            }
        };
        _gps.addMovableListener( _movableListener );
        
        // arrow that points to the left
        PNode arrowNode = new LeftToolOriginNode();
        addChild( arrowNode );
        arrowNode.setOffset( 0, 0 ); // this node identifies the origin
        
        // display to the right of arrow, vertically centered
        _valueNode = new ValueNode();
        addChild( _valueNode );
        _valueNode.setOffset( arrowNode.getFullBounds().getWidth() + 1, -_valueNode.getFullBounds().getHeight() / 2 );
        
        // initial state
        updateCoordinates();
    }
    
    public void cleanup() {
        _gps.removeMovableListener( _movableListener );
        super.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * Displays the position coordinates.
     */
    private static class ValueNode extends PComposite {
        
        private JLabel _coordinatesLabel;
        private PSwing _pswing;
        
        public ValueNode() {
            super();
            
            _coordinatesLabel = new JLabel( DISPLAY_FORMAT );
            _coordinatesLabel.setFont( FONT );
            
            JPanel panel = new JPanel();
            panel.setBackground( Color.WHITE );
            panel.setBorder( BORDER );
            panel.add( _coordinatesLabel );
            
            _pswing = new PSwing( panel );
            addChild( _pswing );
        }
        
        public void setCoordinates( Point2D position ) {
            Object[] args = { COORDINATE_FORMAT.format( position.getX() ), COORDINATE_FORMAT.format( position.getY() ) };
            String s = MessageFormat.format( DISPLAY_FORMAT, args );
            _coordinatesLabel.setText( s );
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the displayed coordinates to match the model.
     */
    private void updateCoordinates() {
        _valueNode.setCoordinates( _gps.getPositionReference() );
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /**
     * Creates a sample image of this node type, for use as an icon.
     */
    public static Image createImage() {
        GPSReceiver gpsReceiver = new GPSReceiver( new Point2D.Double( 0, 0 ) );
        ModelViewTransform mvt = new ModelViewTransform(); // identity transform
        PNode node = new GPSReceiverNode( gpsReceiver, mvt, null /* trashCanIconNode */ );
        return node.toImage();
    }
}
