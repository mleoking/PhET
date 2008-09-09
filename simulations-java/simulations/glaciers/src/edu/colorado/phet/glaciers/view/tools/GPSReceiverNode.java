/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.text.NumberFormat;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.GPSReceiver;
import edu.colorado.phet.glaciers.model.Movable.MovableAdapter;
import edu.colorado.phet.glaciers.model.Movable.MovableListener;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.colorado.phet.glaciers.view.tools.AbstractToolOriginNode.LeftToolOriginNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
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
    
    private static final NumberFormat DISTANCE_FORMAT = new DefaultDecimalFormat( "0" );
    private static final NumberFormat ELEVATION_FORMAT = new DefaultDecimalFormat( "0" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final GPSReceiver _gps;
    private final MovableListener _movableListener;
    private final ValueNode _valueNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GPSReceiverNode( GPSReceiver gps, ModelViewTransform mvt, TrashCanDelegate trashCan ) {
        super( gps, mvt, trashCan );
        
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
        
        // GPS receiver image
        PNode receiverNode = new ReceiverNode();
        addChild( receiverNode );
        receiverNode.setOffset( arrowNode.getFullBounds().getMaxX() + 2, -22 );
        
        // display to the right of arrow, vertically centered
        _valueNode = new ValueNode( getValueFont(), getValueBorder() );
        addChild( _valueNode );
        _valueNode.setOffset( receiverNode.getFullBounds().getMaxX() + 2, -arrowNode.getFullBounds().getHeight() / 2 );
        
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
     * Image of the GPS receiver.
     */
    private static class ReceiverNode extends PComposite {
        public ReceiverNode() {
            super();
            PImage imageNode = new PImage( GlaciersImages.GPS_RECEIVER );
            addChild( imageNode );
        }
    }
    
    /*
     * Displays the position coordinates.
     */
    private static class ValueNode extends PComposite {
        
        private JLabel _distanceLabel;
        private JLabel _elevationLabel;
        private PSwing _pswing;
        
        public ValueNode( Font font, Border border ) {
            super();
            
            ArrowNode xArrowNode = new ArrowNode( new Point2D.Double( 0, 0 ), new Point2D.Double( 10, 0 ), 5, 8, 2 );
            xArrowNode.setStroke( null );
            xArrowNode.setPaint( Color.BLACK );
            JLabel xArrowLabel = new JLabel( new ImageIcon( xArrowNode.toImage() ) );
            
            ArrowNode yArrowNode = new ArrowNode( new Point2D.Double( 0, 0 ), new Point2D.Double( 0, -10 ), 5, 8, 2 );
            yArrowNode.setStroke( null );
            yArrowNode.setPaint( Color.BLACK );
            JLabel yArrowLabel = new JLabel( new ImageIcon( yArrowNode.toImage() ) );
            
            _distanceLabel = new JLabel( "?" );
            _distanceLabel.setFont( font );
            
            _elevationLabel = new JLabel( "?" );
            _elevationLabel.setFont( font );
            
            JPanel panel = new JPanel();
            panel.setBackground( Color.WHITE );
            panel.setBorder( border );
            EasyGridBagLayout layout = new EasyGridBagLayout( panel );
            layout.setAnchor( GridBagConstraints.EAST );
            panel.setLayout( layout );
            layout.addComponent( xArrowLabel, 0, 0 );
            layout.addComponent( _distanceLabel, 0, 1 );
            layout.addComponent( yArrowLabel, 1, 0 );
            layout.addComponent( _elevationLabel, 1, 1 );
            
            _pswing = new PSwing( panel );
            addChild( _pswing );
        }
        
        public void setCoordinates( Point2D position ) {
            _distanceLabel.setText( GlaciersStrings.LABEL_DISTANCE + ": " +  DISTANCE_FORMAT.format( position.getX() ) + " " + GlaciersStrings.UNITS_METERS );
            _elevationLabel.setText( GlaciersStrings.LABEL_ELEVATION + ": " +  ELEVATION_FORMAT.format( position.getY() ) + " " + GlaciersStrings.UNITS_METERS );
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
        return new ReceiverNode().toImage();
    }
}
