/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.graphs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.TimesTenNumberFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FormattedNumberNode;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;


public class BarGraphNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Stroke OUTLINE_STROKE = new BasicStroke( 1f );
    private static final Color OUTLINE_STROKE_COLOR = Color.BLACK;
    private static final Color OUTLINE_FILL_COLOR = Color.WHITE;
    
    private static final Font VALUE_FONT = new PhetFont( 16 );
    private static final Color VALUE_COLOR = Color.BLACK;
    private static final double VALUE_Y_MARGIN = 10;
    private static final TimesTenNumberFormat H3O_FORMAT = new TimesTenNumberFormat( "0.00" );
    private static final TimesTenNumberFormat OH_FORMAT = new TimesTenNumberFormat( "0.00" );
    private static final DecimalFormat H2O_FORMAT = new DefaultDecimalFormat( "#0" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    
    private final PPath _graphOutlineNode;
    private final FormattedNumberNode _h3oNumberNode, _ohNumberNode, _h2oNumberNode;
    private PNode _ticksLogConcentrationNode, _ticksLogMolesNode, _ticksLinearConcentrationNode, _ticksLinearMolesNode;
    private PNode _h3oBarNode, _ohBarNode, _h2oBarNode;
    
    private boolean _logScale;
    private boolean _concentrationUnits;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BarGraphNode( PDimension graphOutlineSize, Liquid liquid ) {
        
        _logScale = true;
        _concentrationUnits = true;
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                updateValues();
                updateBars();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        Rectangle2D r = new Rectangle2D.Double( 0, 0, graphOutlineSize.getWidth(), graphOutlineSize.getHeight() );
        _graphOutlineNode = new PPath( r );
        _graphOutlineNode.setStroke( OUTLINE_STROKE );
        _graphOutlineNode.setStrokePaint( OUTLINE_STROKE_COLOR );
        _graphOutlineNode.setPaint( OUTLINE_FILL_COLOR );
        _graphOutlineNode.setPickable( false );
        _graphOutlineNode.setChildrenPickable( false );
        addChild( _graphOutlineNode );
        
        _h3oNumberNode = new FormattedNumberNode( H3O_FORMAT, 0, VALUE_FONT, VALUE_COLOR );
        _h3oNumberNode.rotate( -Math.PI / 2 );
        _h3oNumberNode.setPickable( false );
        _h3oNumberNode.setChildrenPickable( false );
        addChild( _h3oNumberNode );
        
        _ohNumberNode = new FormattedNumberNode( OH_FORMAT, 0, VALUE_FONT, VALUE_COLOR );
        _ohNumberNode.rotate( -Math.PI / 2 );
        _ohNumberNode.setPickable( false );
        _ohNumberNode.setChildrenPickable( false );
        addChild( _ohNumberNode );
        
        _h2oNumberNode = new FormattedNumberNode( H2O_FORMAT, 0, VALUE_FONT, VALUE_COLOR );
        _h2oNumberNode.rotate( -Math.PI / 2 );
        _h2oNumberNode.setPickable( false );
        _h2oNumberNode.setChildrenPickable( false );
        addChild( _h2oNumberNode );
        
        updateValues(); // do this before setting offsets so that bounds are reasonable
        
        _graphOutlineNode.setOffset( 0, 0 );
        PBounds gob = _graphOutlineNode.getFullBoundsReference();
        final double xH3O = 0.25 * gob.getWidth();
        final double xOH = 0.5 * gob.getWidth();
        final double xH2O = 0.75 * gob.getWidth();
        
        _h3oNumberNode.setOffset( xH3O - _h3oNumberNode.getFullBoundsReference().getWidth()/2, gob.getMaxY() - _h3oNumberNode.getFullBoundsReference().getHeight() - VALUE_Y_MARGIN );
        _ohNumberNode.setOffset( xOH - _ohNumberNode.getFullBoundsReference().getWidth()/2, gob.getMaxY() - _ohNumberNode.getFullBoundsReference().getHeight() - VALUE_Y_MARGIN );
        _h2oNumberNode.setOffset( xH2O - _h2oNumberNode.getFullBoundsReference().getWidth()/2, gob.getMaxY() - _h2oNumberNode.getFullBoundsReference().getHeight() - VALUE_Y_MARGIN );
        
        updateTicks();
        updateBars();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setLogScale( boolean logScale ) {
        if ( logScale != _logScale ) {
            _logScale = logScale;
            updateTicks();
            updateBars();
        }
    }
    
    public boolean isLogScale() {
        return _logScale;
    }
    
    public void setConcentrationUnits( boolean concentrationUnits ) {
        if ( concentrationUnits != _concentrationUnits ) {
            _concentrationUnits = concentrationUnits;
            updateValues();
            updateTicks();
            updateBars();
        }
    }
    
    public boolean isConcentrationUnits() {
        return _concentrationUnits;
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updateValues() {
        if ( _concentrationUnits ) {
            _h3oNumberNode.setValue( _liquid.getConcentrationH3O() );
            _ohNumberNode.setValue( _liquid.getConcentrationOH() );
            _h2oNumberNode.setValue( _liquid.getConcentrationH2O() );
        }
        else {
            _h3oNumberNode.setValue( _liquid.getMolesH3O() );
            _ohNumberNode.setValue( _liquid.getMolesOH() );
            _h2oNumberNode.setValue( _liquid.getMolesH2O() );
        }
    }
    
    private void updateTicks() {
        if ( _concentrationUnits ) {
            if ( _logScale ) {
                //XXX
            }
            else { /* linear */
                //XXX
            }
        }
        else { /* moles */
            if ( _logScale ) {
                //XXX
            }
            else { /* linear */
                //XXX
            }
        }
    }
    
    private void updateBars() {
        if ( _concentrationUnits ) {
            if ( _logScale ) {
                //XXX
            }
            else { /* linear */
                //XXX
            }
        }
        else { /* moles */
            if ( _logScale ) {
                //XXX
            }
            else { /* linear */
                //XXX
            }
        }
    }
}
