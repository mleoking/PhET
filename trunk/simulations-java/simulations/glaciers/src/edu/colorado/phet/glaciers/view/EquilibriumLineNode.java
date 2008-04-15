/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
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
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.Climate;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;
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
    
    private static final String DISPLAY_FORMAT = "{0} " + GlaciersStrings.UNITS_ELEVATION;
    private static final Font FONT = new PhetDefaultFont( 10 );
    private static final Border BORDER = BorderFactory.createLineBorder( Color.BLACK, 1 );
    private static final NumberFormat COORDINATE_FORMAT = new DefaultDecimalFormat( "0" );
    private static final Stroke STROKE = 
        new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 );
    private static final Color STROKE_COLOR = Color.RED;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Climate _climate;
    private ClimateListener _climateListener;
    private ModelViewTransform _mvt;
    private JLabel _coordinatesDisplay;
    private Point2D _pModel, _pView; // reusable points
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public EquilibriumLineNode( Climate climate, ModelViewTransform mvt ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        _climate = climate;
        _climateListener = new ClimateListener() {

            public void snowfallChanged() {
                update();
            }

            public void snowfallReferenceElevationChanged() {
                update();
            }

            public void temperatureChanged() {
                update();
            }
        };
        _climate.addClimateListener( _climateListener );
        
        _mvt = mvt;
        _pModel = new Point2D.Double();
        _pView = new Point2D.Double();
        
        // horizontal line
        _pModel.setLocation( 80E3, 0 );
        mvt.modelToView( _pModel, _pView );
        Line2D path = new Line2D.Double( 0, 0, _pView.getX(), 0 );
        PPath pathNode = new PPath( path );
        pathNode.setStroke( STROKE );
        pathNode.setStrokePaint( STROKE_COLOR );
        addChild( pathNode );
        
        // display altitude above the line
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
        update();
    }
    
    public void cleanup() {
        _climate.removeClimateListener( _climateListener );
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void update() {
        
        double ela = _climate.getELA();
        _pModel.setLocation( 0, ela );
        _mvt.modelToView( _pModel, _pView );
        
        // update position of this node
        setOffset( _pView );
        
        // update the coordinates display
        Object[] args = { COORDINATE_FORMAT.format( _pModel.getY() ) };
        String s = MessageFormat.format( DISPLAY_FORMAT, args );
        _coordinatesDisplay.setText( s );
    }
}
