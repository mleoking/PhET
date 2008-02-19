/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.glaciers.model.GPSReceiver;
import edu.colorado.phet.glaciers.model.Movable.MovableAdapter;
import edu.colorado.phet.glaciers.model.Movable.MovableListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
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
    private static final Font FONT = new PhetDefaultFont( 10 );
    private static final Border BORDER = BorderFactory.createLineBorder( Color.BLACK, 1 );
    private static final NumberFormat COORDINATE_FORMAT = new DefaultDecimalFormat( "0" );
    private static final float TRIANGLE_SIZE = 10f;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GPSReceiver _gps;
    private MovableListener _movableListener;
    private JLabel _coordinatesDisplay;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GPSReceiverNode( GPSReceiver gps, ModelViewTransform mvt ) {
        super( gps, mvt );
        
        _gps = gps;
        _movableListener = new MovableAdapter() {
            public void positionChanged() {
                updateCoordinates();
            }
        };
        _gps.addMovableListener( _movableListener );
        
        // arrow that points to the left
        GeneralPath trianglePath = new GeneralPath();
        trianglePath.moveTo( 0f, 0f );
        trianglePath.lineTo( TRIANGLE_SIZE, -TRIANGLE_SIZE / 2 );
        trianglePath.lineTo( TRIANGLE_SIZE, TRIANGLE_SIZE / 2 );
        trianglePath.closePath();
        PPath pathNode = new PPath( trianglePath );
        pathNode.setStroke( null );
        pathNode.setPaint( Color.RED );
        addChild( pathNode );
        pathNode.setOffset( 0, 0 );
        
        // display to the right of arrow, vertically centered
        _coordinatesDisplay = new JLabel( DISPLAY_FORMAT );
        _coordinatesDisplay.setFont( FONT );
        JPanel panel = new JPanel();
        panel.setBackground( Color.WHITE );
        panel.setBorder( BORDER );
        panel.add( _coordinatesDisplay );
        PSwing panelNode = new PSwing( panel );
        addChild( panelNode );
        panelNode.setOffset( pathNode.getFullBounds().getWidth(), -panelNode.getFullBounds().getHeight() / 2 );
        
        // initial state
        updateCoordinates();
    }
    
    public void cleanup() {
        _gps.removeMovableListener( _movableListener );
        super.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the displayed coordinates to match the model.
     */
    private void updateCoordinates() {
        Point2D pModel = _gps.getPositionReference();
        Object[] args = { COORDINATE_FORMAT.format( pModel.getX() ), COORDINATE_FORMAT.format( pModel.getY() ) };
        String s = MessageFormat.format( DISPLAY_FORMAT, args );
        _coordinatesDisplay.setText( s );
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
        PNode node = new GPSReceiverNode( gpsReceiver, mvt );
        return node.toImage();
    }
}
