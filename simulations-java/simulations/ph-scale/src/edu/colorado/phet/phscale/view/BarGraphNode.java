/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.phscale.PHScaleStrings;
import edu.colorado.phet.phscale.control.GraphScaleControlPanel;
import edu.colorado.phet.phscale.control.GraphUnitsControlPanel;
import edu.colorado.phet.phscale.control.GraphUnitsControlPanel.GraphUnitsControlPanelListener;
import edu.colorado.phet.phscale.graphs.FormattedNumberNode;
import edu.colorado.phet.phscale.graphs.LegendNode;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.colorado.phet.phscale.util.TimesTenNumberFormat;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwing;


public class BarGraphNode extends PNode {

    private static final Font TITLE_FONT = new PhetFont( Font.BOLD, 24 );
    
    private static final Font VALUE_FONT = new PhetFont( 16 );
    private static final double VALUE_SPACING = 50;
    private static final TimesTenNumberFormat H3O_FORMAT = new TimesTenNumberFormat( "0.00" );
    private static final TimesTenNumberFormat OH_FORMAT = new TimesTenNumberFormat( "0.00" );
    private static final DecimalFormat H2O_FORMAT = new DecimalFormat( "#0" );
    
    private static final Stroke OUTLINE_STROKE = new BasicStroke( 1f );
    private static final Color OUTLINE_STROKE_COLOR = Color.BLACK;
    private static final Color OUTLINE_FILL_COLOR = Color.WHITE;

    private static final double LEGEND_Y_SPACING = 5;
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    
    private final PPath _graphOutlineNode;
    private final GraphUnitsControlPanel _graphUnitsControlPanel;
    private final GraphScaleControlPanel _graphScaleControlPanel;
    private final ValuesNode _concentrationsNode, _molesNode;
    
    public BarGraphNode( PDimension graphOutlineSize, Liquid liquid ) {
        super();
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                updateValues();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        PText titleNode = new PText( PHScaleStrings.TITLE_WATER_COMPONENTS );
        titleNode.setFont( TITLE_FONT );
        addChild( titleNode );
        
        _graphUnitsControlPanel = new GraphUnitsControlPanel();
        _graphUnitsControlPanel.setConcentrationSelected( true );
        PSwing graphUnitsControlPanelWrapper = new PSwing( _graphUnitsControlPanel );
        addChild( graphUnitsControlPanelWrapper );
        _graphUnitsControlPanel.addGraphUnitsControlPanelListener( new GraphUnitsControlPanelListener() {
            public void selectionChanged() {
                updateUnits();
            }
        });
        
        Rectangle2D r = new Rectangle2D.Double( 0, 0, graphOutlineSize.getWidth(), graphOutlineSize.getHeight() );
        _graphOutlineNode = new PPath( r );
        _graphOutlineNode.setStroke( OUTLINE_STROKE );
        _graphOutlineNode.setStrokePaint( OUTLINE_STROKE_COLOR );
        _graphOutlineNode.setPaint( OUTLINE_FILL_COLOR );
        addChild( _graphOutlineNode );
        
        _concentrationsNode = new ValuesNode();
        _concentrationsNode.rotate( -Math.PI/2 );
        addChild( _concentrationsNode );
        
        _molesNode = new ValuesNode();
        _molesNode.rotate( -Math.PI/2 );
        addChild( _molesNode );

        LegendNode legendNode = new LegendNode();
        addChild( legendNode );
        
        _graphScaleControlPanel = new GraphScaleControlPanel();
        PSwing graphScaleControlPanelWrapper = new PSwing( _graphScaleControlPanel );
        addChild( graphScaleControlPanelWrapper );
        
        titleNode.setOffset( 0, 0 );
        PBounds tb = titleNode.getFullBoundsReference();
        graphUnitsControlPanelWrapper.setOffset( tb.getX(), tb.getMaxY() + 10 );
        PBounds ub = graphUnitsControlPanelWrapper.getFullBoundsReference();
        _graphOutlineNode.setOffset( ub.getX(), ub.getMaxY() + 10 );
        PBounds ob = _graphOutlineNode.getFullBoundsReference();
        PBounds lb = legendNode.getFullBoundsReference();
        legendNode.setOffset( ( ob.getWidth() - lb.getWidth() ) / 2, ob.getMaxY() + LEGEND_Y_SPACING );
        lb = legendNode.getFullBoundsReference();
        graphScaleControlPanelWrapper.setOffset( ob.getX(), lb.getMaxY() + 10 );
        PBounds cb = _concentrationsNode.getFullBoundsReference();
        double xo = ob.getMinX() + ( ob.getWidth() - cb.getWidth() ) / 2;
        double yo = ob.getMaxY() - cb.getMaxY() - 20;
        _concentrationsNode.setOffset( xo, yo );
        PBounds mb = _molesNode.getFullBoundsReference();
        xo = ob.getMinX() + ( ob.getWidth() - mb.getWidth() ) / 2;
        yo = ob.getMaxY() - mb.getMaxY() - 20;
        _molesNode.setOffset( xo, yo );
        
        updateUnits();
        updateValues();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    private void updateValues() {
        _concentrationsNode.setValues( _liquid.getConcentrationH3O(), _liquid.getConcentrationOH(), _liquid.getConcentrationH2O() );
        _molesNode.setValues( _liquid.getMolesH3O(), _liquid.getMolesOH(), _liquid.getMolesH2O() );
    }
    
    private void updateUnits() {
        _concentrationsNode.setVisible( _graphUnitsControlPanel.isConcentrationSelected() );
        _molesNode.setVisible( _graphUnitsControlPanel.isMolesSelected() );
    }

    private static class ValuesNode extends PComposite {
        
        private final FormattedNumberNode _h3oNode, _ohNode, _h2oNode;
        
        public ValuesNode() {
            this( Color.BLACK );
        }
        
        public ValuesNode( Color textColor ) {
            
            _h3oNode = new FormattedNumberNode( H3O_FORMAT, 0, VALUE_FONT, textColor );
            _ohNode = new FormattedNumberNode( OH_FORMAT, 0, VALUE_FONT, textColor );
            _h2oNode = new FormattedNumberNode( H2O_FORMAT, 0, VALUE_FONT, textColor );
            addChild( _h3oNode );
            addChild( _ohNode );
            addChild( _h2oNode );
            
            _h3oNode.setOffset( 0, 0 );
            _ohNode.setOffset( _h3oNode.getFullBoundsReference().getX(), _h3oNode.getFullBoundsReference().getMaxY() + VALUE_SPACING );
            _h2oNode.setOffset( _ohNode.getFullBoundsReference().getX(), _ohNode.getFullBoundsReference().getMaxY() + VALUE_SPACING );
        }
        
        public void setValues( double h3o, double oh, double h2o ) {
            _h3oNode.setValue( h3o );
            _ohNode.setValue( oh );
            _h2oNode.setValue( h2o );
        }
    }
}
