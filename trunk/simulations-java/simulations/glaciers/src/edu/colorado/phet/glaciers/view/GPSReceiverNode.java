package edu.colorado.phet.glaciers.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.glaciers.model.GPSReceiver;
import edu.colorado.phet.glaciers.model.Movable.MovableAdapter;
import edu.colorado.phet.glaciers.model.Movable.MovableListener;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;


public class GPSReceiverNode extends AbstractToolNode {

    private static final Font FONT = new PhetDefaultFont( 10 );
    private static final Border BORDER = BorderFactory.createLineBorder( Color.BLACK, 1 );
    private static final DecimalFormat COORDINATE_FORMAT = new DecimalFormat( "0" );
    private static final float TRIANGLE_SIZE = 10f;
    
    private GPSReceiver _gps;
    private MovableListener _movableListener;
    private JLabel _coordinatesLabel;
    
    public GPSReceiverNode( GPSReceiver gps ) {
        super( gps );
        
        _gps = gps;
        _movableListener = new MovableAdapter() {
            public void positionChanged() {
                updateCoordinates();
            }
        };
        _gps.addMovableListener( _movableListener );
        
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
        
        _coordinatesLabel = new JLabel( "(x,z)" );
        JPanel panel = new JPanel();
        panel.setBackground( Color.WHITE );
        panel.setBorder( BORDER );
        panel.add( _coordinatesLabel );
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
    
    private void updateCoordinates() {
        Point2D p = _gps.getPositionReference();
        String s = "(" + COORDINATE_FORMAT.format( p.getX() ) + "," + COORDINATE_FORMAT.format( p.getY() ) + ")";
        _coordinatesLabel.setText( s );
    }
}
