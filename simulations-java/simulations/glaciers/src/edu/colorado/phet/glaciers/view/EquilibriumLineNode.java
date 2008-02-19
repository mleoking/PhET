/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.glaciers.model.EquilibriumLine;
import edu.colorado.phet.glaciers.model.EquilibriumLine.EquilibriumLineListener;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * EquilibriumLineNode is the visual representation of the equilibrium line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EquilibriumLineNode extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String DISPLAY_FORMAT = "(x,z)=({0},{1})";
    private static final Font FONT = new PhetDefaultFont( 10 );
    private static final Border BORDER = BorderFactory.createLineBorder( Color.BLACK, 1 );
    private static final NumberFormat COORDINATE_FORMAT = new DefaultDecimalFormat( "0" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private EquilibriumLine _equilibriumLine;
    private EquilibriumLineListener _equilibriumLineListener;
    private ModelViewTransform _mvt;
    private JLabel _coordinatesDisplay;
    private Point2D _pView; // reusable point
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public EquilibriumLineNode( EquilibriumLine equilibriumLine, ModelViewTransform mvt ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        _equilibriumLine = equilibriumLine;
        _equilibriumLineListener = new EquilibriumLineListener() {
            public void positionChanged() {
                updatePosition();
            }
        };
        _equilibriumLine.addEquilibriumLineListener( _equilibriumLineListener );
        
        _mvt = mvt;
        _pView = new Point2D.Double();
        
        // vertical line
        Line2D path = new Line2D.Double( 0, 0, 0, -50 );
        PPath pathNode = new PPath( path );
        pathNode.setStroke( new BasicStroke( 4f ) );
        pathNode.setStrokePaint( Color.RED );
        addChild( pathNode );
        
        // (x,z) display above the line
        _coordinatesDisplay = new JLabel( DISPLAY_FORMAT );
        _coordinatesDisplay.setFont( FONT );
        JPanel panel = new JPanel();
        panel.setBackground( Color.WHITE );
        panel.setBorder( BORDER );
        panel.add( _coordinatesDisplay );
        PSwing panelNode = new PSwing( panel );
        addChild( panelNode );
        panelNode.setOffset( 0, pathNode.getFullBounds().getMinY() - panelNode.getFullBoundsReference().getHeight() );
        
        // intialize
        updatePosition();
    }
    
    public void cleanup() {
        _equilibriumLine.removeEquilibriumLineListener( _equilibriumLineListener );
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updatePosition() {
        
        Point2D pModel = _equilibriumLine.getPositionReference();
        
        // update position of this node
        _mvt.modelToView( pModel, _pView );
        setOffset( _pView );
        
        // update the coordinates display
        Object[] args = { COORDINATE_FORMAT.format( pModel.getX() ), COORDINATE_FORMAT.format( pModel.getY() ) };
        String s = MessageFormat.format( DISPLAY_FORMAT, args );
        _coordinatesDisplay.setText( s );
    }
}
