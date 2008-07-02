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
    
    private static final Stroke OUTLINE_STROKE = new BasicStroke( 1f );
    private static final Color OUTLINE_STROKE_COLOR = Color.BLACK;
    private static final Color OUTLINE_FILL_COLOR = Color.WHITE;
    
    private static final Font VALUE_FONT = new PhetFont( 16 );
    private static final Color VALUE_COLOR = Color.BLACK;
    private static final double VALUE_Y_MARGIN = 10;
    private static final TimesTenNumberFormat H3O_FORMAT = new TimesTenNumberFormat( "0.00" );
    private static final TimesTenNumberFormat OH_FORMAT = new TimesTenNumberFormat( "0.00" );
    private static final DecimalFormat H2O_FORMAT = new DefaultDecimalFormat( "#0" );
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    
    private final PPath _graphOutlineNode;
    private final FormattedNumberNode _h3oNode, _ohNode, _h2oNode;
    
    private boolean _logScale;
    private boolean _concentrationUnits;
    

    public BarGraphNode( PDimension graphOutlineSize, Liquid liquid ) {
        
        _logScale = true;
        _concentrationUnits = true;
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                update();
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
        
        _h3oNode = new FormattedNumberNode( H3O_FORMAT, 0, VALUE_FONT, VALUE_COLOR );
        _h3oNode.rotate( -Math.PI / 2 );
        _h3oNode.setPickable( false );
        _h3oNode.setChildrenPickable( false );
        addChild( _h3oNode );
        
        _ohNode = new FormattedNumberNode( OH_FORMAT, 0, VALUE_FONT, VALUE_COLOR );
        _ohNode.rotate( -Math.PI / 2 );
        _ohNode.setPickable( false );
        _ohNode.setChildrenPickable( false );
        addChild( _ohNode );
        
        _h2oNode = new FormattedNumberNode( H2O_FORMAT, 0, VALUE_FONT, VALUE_COLOR );
        _h2oNode.rotate( -Math.PI / 2 );
        _h2oNode.setPickable( false );
        _h2oNode.setChildrenPickable( false );
        addChild( _h2oNode );
        
        updateValues(); // do this before setting offsets so that bounds are reasonable
        
        _graphOutlineNode.setOffset( 0, 0 );
        PBounds gob = _graphOutlineNode.getFullBoundsReference();
        final double xH3O = 0.25 * gob.getWidth();
        final double xOH = 0.5 * gob.getWidth();
        final double xH2O = 0.75 * gob.getWidth();
        
        _h3oNode.setOffset( xH3O - _h3oNode.getFullBoundsReference().getWidth()/2, gob.getMaxY() - _h3oNode.getFullBoundsReference().getHeight() - VALUE_Y_MARGIN );
        _ohNode.setOffset( xOH - _ohNode.getFullBoundsReference().getWidth()/2, gob.getMaxY() - _ohNode.getFullBoundsReference().getHeight() - VALUE_Y_MARGIN );
        _h2oNode.setOffset( xH2O - _h2oNode.getFullBoundsReference().getWidth()/2, gob.getMaxY() - _h2oNode.getFullBoundsReference().getHeight() - VALUE_Y_MARGIN );
        
        updateBars();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    public void setLogScale( boolean logScale ) {
        if ( logScale != _logScale ) {
            _logScale = logScale;
            update();
        }
    }
    
    public boolean isLogScale() {
        return _logScale;
    }
    
    public void setConcentrationUnits( boolean concentrationUnits ) {
        if ( concentrationUnits != _concentrationUnits ) {
            _concentrationUnits = concentrationUnits;
            update();
        }
    }
    
    public boolean isConcentrationUnits() {
        return _concentrationUnits;
    }
    
    private void update() {
       updateValues();        
       updateBars();
    }
    
    private void updateValues() {
        if ( _concentrationUnits ) {
            _h3oNode.setValue( _liquid.getConcentrationH3O() );
            _ohNode.setValue( _liquid.getConcentrationOH() );
            _h2oNode.setValue( _liquid.getConcentrationH2O() );
        }
        else {
            _h3oNode.setValue( _liquid.getMolesH3O() );
            _ohNode.setValue( _liquid.getMolesOH() );
            _h2oNode.setValue( _liquid.getMolesH2O() );
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
