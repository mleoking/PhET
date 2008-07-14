/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.TimesTenNumberFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FormattedNumberNode;
import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.PHScaleStrings;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.LiquidDescriptor;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.colorado.phet.phscale.model.LiquidDescriptor.CustomLiquidDescriptor;
import edu.colorado.phet.phscale.view.graph.ZoomControlPanel.ZoomControlPanelListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * GraphNode is the bar graph.
 * The y-axis units can be either concentration (moles/L) or moles.
 * The y-axis scale can be either log10 or linear.
 * <p>
 * NOTE: This implementation assumes that the y-axis only changes when
 * we switch between log10 and linear scales.  When we switch units,
 * the y-axis scale does not change.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GraphNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // set this to true to see how log scale ticks align with pH slider ticks
    private static final boolean DEBUG_TICKS_ALIGNMENT = false;
    
    // graph outline
    private static final Stroke OUTLINE_STROKE = new BasicStroke( 1f );
    private static final Color OUTLINE_STROKE_COLOR = Color.BLACK;
    private static final Color OUTLINE_FILL_COLOR = Color.WHITE;
    
    // bars
    private static final double BAR_WIDTH = 50;
    
    // numeric values
    private static final Font VALUE_FONT = new PhetFont( Font.BOLD, PHScaleConstants.CONTROL_FONT_SIZE );
    private static final Color VALUE_COLOR = Color.BLACK;
    private static final double VALUE_Y_MARGIN = 10;
    private static final TimesTenNumberFormat H3O_FORMAT = new TimesTenNumberFormat( "0.00" );
    private static final TimesTenNumberFormat OH_FORMAT = new TimesTenNumberFormat( "0.00" );
    private static final DecimalFormat H2O_FORMAT = new DefaultDecimalFormat( "#0" );
    
    // axis label
    private static final Font AXIS_LABEL_FONT = new PhetFont( 14 );
    private static final Color AXIS_LABEL_COLOR = Color.BLACK;
    private static final double AXIS_LABEL_X_MARGIN = 4;
    private static final String AXIS_LABEL_CONCENTRATION = PHScaleStrings.getConcentrationString();
    private static final String AXIS_LABEL_NUMBER_OF_MOLES = PHScaleStrings.getNumberOfMolesString();
    
    // ticks
    private static final double TICK_LENGTH = 6;
    private static final Stroke TICK_STROKE = new BasicStroke( 1f );
    private static final Color TICK_COLOR = Color.BLACK;
    private static final Font TICK_LABEL_FONT = new PhetFont( 14 );
    private static final Color TICK_LABEL_COLOR = Color.BLACK;
    private static final double TICKS_TOP_MARGIN = 10;
    
    // log ticks
    private static final int NUMBER_OF_LOG_TICKS = 19;
    private static final int BIGGEST_LOG_TICK_EXPONENT = 2;
    private static final int LOG_TICK_EXPONENT_SPACING = 2;
    
    // linear ticks
    private static final int NUMBER_OF_LINEAR_TICKS = 19;
    private static final double LINEAR_TICK_MANTISSA_SPACING = 0.5;
    private static final int BIGGEST_LINEAR_TICK_EXPONENT = 1;
    private static final int SMALLEST_LINEAR_TICK_EXPONENT = -15;
    
    // horizontal gridlines
    private static final Stroke GRIDLINE_STROKE = new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 ); // dashed
    private static final Color GRIDLINE_COLOR = new Color(192, 192, 192, 100 ); // translucent gray
    
    private static final CustomLiquidDescriptor CUSTOM_LIQUID = LiquidDescriptor.getCustom();
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    
    private final double _graphOutlineHeight;
    private final FormattedNumberNode _h3oNumberNode, _ohNumberNode, _h2oNumberNode;
    private final LogYAxisNode _logYAxisNode;
    private final LinearYAxisNode _linearYAxisNode;
    private final BarNode _h3oBarNode, _ohBarNode, _h2oBarNode;
    private final BarDragHandleNode _h3oDragHandleNode, _ohDragHandleNode;
    private final ZoomControlPanel _zoomControlPanel;
    private final PSwing _zoomPanelWrapper;
    private final PText _yAxisLabel;
    
    private boolean _logScale;
    private boolean _concentrationUnits;
    private int _linearTicksExponent;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GraphNode( Liquid liquid, double graphOutlineWidth, double logTicksYSpacing ) {
        
        _graphOutlineHeight = ( ( NUMBER_OF_LOG_TICKS - 1 ) * logTicksYSpacing ) + TICKS_TOP_MARGIN;
        _logScale = true;
        _concentrationUnits = true;
        _linearTicksExponent = BIGGEST_LINEAR_TICK_EXPONENT;
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                updateValues();
                updateBars();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        // graphOutlineNode is not instance data because we do NOT want to use its bounds for calculations.
        // It's stroke width will cause calculation errors.  Use _graphOutlineSize instead.
        Rectangle2D r = new Rectangle2D.Double( 0, 0, graphOutlineWidth, _graphOutlineHeight );
        PPath graphOutlineNode = new PPath( r );
        graphOutlineNode.setStroke( OUTLINE_STROKE );
        graphOutlineNode.setStrokePaint( OUTLINE_STROKE_COLOR );
        graphOutlineNode.setPaint( OUTLINE_FILL_COLOR );
        graphOutlineNode.setPickable( false );
        graphOutlineNode.setChildrenPickable( false );
        addChild( graphOutlineNode );
        
        // log y axis
        PDimension graphOutlineSize = new PDimension( graphOutlineWidth, _graphOutlineHeight );
        _logYAxisNode = new LogYAxisNode( graphOutlineSize, NUMBER_OF_LOG_TICKS, TICKS_TOP_MARGIN, 
                BIGGEST_LOG_TICK_EXPONENT,  LOG_TICK_EXPONENT_SPACING, TICK_LENGTH,
                TICK_STROKE, TICK_COLOR, TICK_LABEL_FONT, TICK_LABEL_COLOR, GRIDLINE_STROKE, GRIDLINE_COLOR );
        addChild( _logYAxisNode );
        
        // linear y axis
        _linearYAxisNode =new LinearYAxisNode( graphOutlineSize, NUMBER_OF_LINEAR_TICKS, TICKS_TOP_MARGIN, 
                BIGGEST_LINEAR_TICK_EXPONENT,  LINEAR_TICK_MANTISSA_SPACING, TICK_LENGTH,
                TICK_STROKE, TICK_COLOR, TICK_LABEL_FONT, TICK_LABEL_COLOR, GRIDLINE_STROKE, GRIDLINE_COLOR );
        addChild( _linearYAxisNode );
        
        // y-axis label
        _yAxisLabel = new PText();
        _yAxisLabel.rotate( -Math.PI / 2 );
        _yAxisLabel.setFont( AXIS_LABEL_FONT );
        _yAxisLabel.setTextPaint( AXIS_LABEL_COLOR );
        _yAxisLabel.setPickable( false );
        addChild( _yAxisLabel );
        
        // bars
        _h3oBarNode = new BarNode( BAR_WIDTH, PHScaleConstants.H3O_COLOR, _graphOutlineHeight );
        addChild( _h3oBarNode );
        _ohBarNode = new BarNode( BAR_WIDTH, PHScaleConstants.OH_COLOR, _graphOutlineHeight );
        addChild( _ohBarNode );
        _h2oBarNode = new BarNode( BAR_WIDTH, PHScaleConstants.H2O_COLOR, _graphOutlineHeight );
        addChild( _h2oBarNode );
        
        // line along the bottom of the graph, where bars overlap the outline
        PPath bottomLineNode = new PPath( new Line2D.Double( 0, _graphOutlineHeight, graphOutlineWidth, _graphOutlineHeight ) );
        bottomLineNode.setStroke( OUTLINE_STROKE );
        bottomLineNode.setStrokePaint( OUTLINE_STROKE_COLOR );
        bottomLineNode.setPickable( false );
        addChild( bottomLineNode );
        
        // numbers
        _h3oNumberNode = createNumberNode( H3O_FORMAT );
        addChild( _h3oNumberNode );
        _ohNumberNode = createNumberNode( OH_FORMAT );
        addChild( _ohNumberNode );
        _h2oNumberNode = createNumberNode( H2O_FORMAT );
        addChild( _h2oNumberNode );
        updateValues(); // do this before setting offsets so that bounds are reasonable
        
        // Zoom controls
        _zoomControlPanel = new ZoomControlPanel();
        _zoomControlPanel.addZoomControlPanelListener( new ZoomControlPanelListener() {
            public void zoomIn() {
                zoomInLinear();
            }
            public void zoomOut() {
                zoomOutLinear();
            }
        } );
        _zoomPanelWrapper = new PSwing( _zoomControlPanel );
        _zoomPanelWrapper.setVisible( !_logScale );
        addChild( _zoomPanelWrapper );
        
        // drag handles
        _h3oDragHandleNode = new BarDragHandleNode();
        _h3oDragHandleNode.addInputEventListener( new H3OBarDragHandler() );
        addChild( _h3oDragHandleNode );
        _ohDragHandleNode = new BarDragHandleNode();
        _ohDragHandleNode.addInputEventListener( new OHBarDragHandler() );
        addChild( _ohDragHandleNode );
        
        // layout
        graphOutlineNode.setOffset( 0, 0 );
        PBounds gob = graphOutlineNode.getFullBoundsReference();
        _logYAxisNode.setOffset( graphOutlineNode.getOffset() );
        _linearYAxisNode.setOffset( graphOutlineNode.getOffset() );
        final double xMargin = ( graphOutlineNode.getWidth() - ( 3 * BAR_WIDTH ) ) / 12;
        assert( xMargin > 0 );
        final double xH3O = ( 1./6.) * gob.getWidth() + xMargin;
        final double xOH = ( 3./6. ) * gob.getWidth();
        final double xH2O = ( 5./6.) * gob.getWidth() - xMargin;
        _h3oNumberNode.setOffset( xH3O - _h3oNumberNode.getFullBoundsReference().getWidth()/2, gob.getMaxY() - _h3oNumberNode.getFullBoundsReference().getHeight() - VALUE_Y_MARGIN );
        _ohNumberNode.setOffset( xOH - _ohNumberNode.getFullBoundsReference().getWidth()/2, gob.getMaxY() - _ohNumberNode.getFullBoundsReference().getHeight() - VALUE_Y_MARGIN );
        _h2oNumberNode.setOffset( xH2O - _h2oNumberNode.getFullBoundsReference().getWidth()/2, gob.getMaxY() - _h2oNumberNode.getFullBoundsReference().getHeight() - VALUE_Y_MARGIN );
        _h3oBarNode.setOffset( xH3O, graphOutlineSize.getHeight() );
        _ohBarNode.setOffset( xOH, graphOutlineSize.getHeight() );
        _h2oBarNode.setOffset( xH2O, graphOutlineSize.getHeight() );
        _zoomPanelWrapper.setOffset( gob.getCenterX() - _zoomPanelWrapper.getFullBoundsReference().getWidth()/2, 60 );
        _h3oDragHandleNode.setOffset( xH3O - BAR_WIDTH / 2 + 10, graphOutlineSize.getHeight() - _h3oDragHandleNode.getFullBoundsReference().getHeight() / 2 );
        _ohDragHandleNode.setOffset( xOH - BAR_WIDTH / 2 + 10, graphOutlineSize.getHeight() - _ohDragHandleNode.getFullBoundsReference().getHeight() / 2 );

        updateYAxis();
        updateBars();
        updateZoomControls();
        
        // Debug: extend tick marks to the left of the graph, to check alignment of pH slider and bar graph.
        if ( DEBUG_TICKS_ALIGNMENT ){
            final double lineLength = 185;
            DebugTickAlignmentNode alignmentNode = new DebugTickAlignmentNode( NUMBER_OF_LOG_TICKS, _logYAxisNode.getTickSpacing(), lineLength );
            addChild( alignmentNode );
            alignmentNode.setOffset( -lineLength, TICKS_TOP_MARGIN );
        }
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    private static FormattedNumberNode createNumberNode( NumberFormat format ) {
        FormattedNumberNode node = new FormattedNumberNode( format, 0, VALUE_FONT, VALUE_COLOR );
        node.rotate( -Math.PI / 2 );
        node.setPickable( false );
        node.setChildrenPickable( false );
        return node;
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Sets the scale to either log10 or linear.
     * 
     * @param logScale true=log10, false=linear
     */
    public void setLogScale( boolean logScale ) {
        if ( logScale != _logScale ) {
            _logScale = logScale;
            if ( !_logScale ) {
                // reset linear scale so that it's zoomed all the way out
                _linearTicksExponent = BIGGEST_LINEAR_TICK_EXPONENT;
            }
            updateYAxis();
            updateBars();
            updateZoomControls();
        }
    }
    
    /**
     * Is the scale log10?
     * 
     * @return true=log10, false=linear
     */
    public boolean isLogScale() {
        return _logScale;
    }
    
    /**
     * Sets the units to concentration or moles.
     * 
     * @param concentrationUnits true=concentration, false=moles
     */
    public void setConcentrationUnits( boolean concentrationUnits ) {
        if ( concentrationUnits != _concentrationUnits ) {
            _concentrationUnits = concentrationUnits;
            updateValues();
            updateYAxis();
            updateBars();
        }
    }

    /**
     * Are the units set to concentration?
     * 
     * @return true=concentration, false=moles
     */
    public boolean isConcentrationUnits() {
        return _concentrationUnits;
    }
    
    /**
     * Gets the offset used to vertically align the graph ticks with the pH slider ticks.
     * Offset is in global coordinates.
     * Only the y offset is meaningful.
     * 
     * @return
     */
    public Point2D getTickAlignmentGlobalOffset() {
        // 2nd tick from the top
        return localToGlobal( new Point2D.Double( 0, _logYAxisNode.getYOffset() + TICKS_TOP_MARGIN + _logYAxisNode.getTickSpacing() ) );
    }
    
    //----------------------------------------------------------------------------
    // Zoom
    //----------------------------------------------------------------------------
    
    /*
     * Zooms the y-axis in 1 power of 10 for the linear y-axis scale.
     */
    private void zoomInLinear() {
        _linearTicksExponent--;
        updateYAxis();
        updateBars();
        updateZoomControls();
    }
    
    /*
     * Zooms the y-axis out 1 power of 10 for the linear y-axis scale.
     */
    private void zoomOutLinear() {
        _linearTicksExponent++;
        updateYAxis();
        updateBars();
        updateZoomControls();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the values on the bars to match the model.
     */
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
    
    /*
     * Updates the y axis to match the model.
     */
    private void updateYAxis() {
        
        if ( !_logScale ) {
            // update linear y-axis ticks to match zoom level
            _linearYAxisNode.setTicksExponent( _linearTicksExponent );
        }
        
        // make the proper y axis (log or linear) visible
        _logYAxisNode.setVisible( _logScale );
        _linearYAxisNode.setVisible( !_logScale );
        
        // update the y-axis label and center it on the axis
        _yAxisLabel.setText( _concentrationUnits ? AXIS_LABEL_CONCENTRATION : AXIS_LABEL_NUMBER_OF_MOLES );
        if ( _logScale ) {
            double xOffset = _logYAxisNode.getFullBoundsReference().getX() - _yAxisLabel.getFullBoundsReference().getWidth() - AXIS_LABEL_X_MARGIN;
            double yOffset = _logYAxisNode.getFullBoundsReference().getCenterY() + ( _yAxisLabel.getFullBoundsReference().getHeight() / 2 );
            _yAxisLabel.setOffset( xOffset, yOffset );
        }
        else {
            double xOffset = _linearYAxisNode.getFullBoundsReference().getX() - _yAxisLabel.getFullBoundsReference().getWidth() - AXIS_LABEL_X_MARGIN;
            double yOffset = _linearYAxisNode.getFullBoundsReference().getCenterY() + ( _yAxisLabel.getFullBoundsReference().getHeight() / 2 );
            _yAxisLabel.setOffset( xOffset, yOffset );
        }
    }
    
    /*
     * Updates the bars (and their drag handles) to match the model.
     */
    private void updateBars() {
        
        final double h3oBarHeight = getH3OBarHeight();
        final double ohBarHeight = getOHBarHeight();
        final double h2oBarHeight = getH2OHeight();
        
        // bars
        _h3oBarNode.setBarHeight( h3oBarHeight );
        _ohBarNode.setBarHeight( ohBarHeight );
        _h2oBarNode.setBarHeight( h2oBarHeight );
        
        // drag handles
        updateDragHandle( _h3oDragHandleNode, h3oBarHeight, _graphOutlineHeight );
        updateDragHandle( _ohDragHandleNode, ohBarHeight, _graphOutlineHeight );
    }
    
    /*
     * Gets the height of the H3O bar, in view coordinates.
     */
    private double getH3OBarHeight() {
        double value = 0;
        if ( _concentrationUnits ) {
            value = _liquid.getConcentrationH3O();
        }
        else {
            value = _liquid.getMolesH3O();
        }
        return calculateBarHeight( value );
    }
    
    /*
     * Gets the height of the OH bar, in view coordinates.
     */
    private double getOHBarHeight() {
        double value = 0;
        if ( _concentrationUnits ) {
            value = _liquid.getConcentrationOH();
        }
        else {
            value = _liquid.getMolesOH();
        }
        return calculateBarHeight( value );
    }
    
    /*
     * Gets the height of the H2O bar, in view coordinates.
     */
    private double getH2OHeight() {
        double value = 0;
        if ( _concentrationUnits ) {
            value = _liquid.getConcentrationH2O();
        }
        else {
            value = _liquid.getMolesH2O();
        }
        return calculateBarHeight( value );
    }
    
    /*
     * Calculates a bar height in view coordinates, given a model value.
     */
    private double calculateBarHeight( final double modelValue ) {
        double viewValue = 0;
        final double maxTickHeight = _graphOutlineHeight - TICKS_TOP_MARGIN;
        if ( _logScale ) {
            // log scale
            final double maxExponent = BIGGEST_LOG_TICK_EXPONENT;
            final double minExponent = BIGGEST_LOG_TICK_EXPONENT - NUMBER_OF_LOG_TICKS + 1;
            final double modelValueExponent = MathUtil.log10( modelValue );
            viewValue = maxTickHeight * ( modelValueExponent - minExponent ) / ( maxExponent - minExponent );
        }
        else {
            // linear scale, assumes that the y-axis starts at zero!
            final double maxMantissa = ( NUMBER_OF_LINEAR_TICKS - 1 ) * LINEAR_TICK_MANTISSA_SPACING;
            final double maxTickValue = maxMantissa * Math.pow( 10, _linearTicksExponent );
            viewValue = maxTickHeight * modelValue / maxTickValue;
        }
        return viewValue;
    }
    
    /*
     * Utility that updates a drag handle.
     */
    private static void updateDragHandle( PNode dragHandleNode, final double barHeight, final double graphHeight ) {
        // handles are invisible if the bar extends above or below the graph's bounds
        dragHandleNode.setVisible( barHeight > 0 && barHeight <= graphHeight );
        if ( dragHandleNode.getVisible() ) {
            // position the handle at the top of the bar
            dragHandleNode.setOffset( dragHandleNode.getXOffset(), graphHeight - barHeight );
        }
    }
    
    /*
     * Updates the Zoom controls.
     */
    private void updateZoomControls() {
        // zoom controls are only visible for linear scale
        _zoomPanelWrapper.setVisible( !_logScale );
        // disable the "zoom in" button when we are fully zoomed in
        _zoomControlPanel.setZoomInEnabled( _linearTicksExponent != SMALLEST_LINEAR_TICK_EXPONENT );
        // disable the "zoom out" button when we are fully zoomed out
        _zoomControlPanel.setZoomOutEnabled( _linearTicksExponent != BIGGEST_LINEAR_TICK_EXPONENT );
    }
    
    //----------------------------------------------------------------------------
    // Bar drag handlers
    //----------------------------------------------------------------------------
    
    private class H3OBarDragHandler extends BarDragHandler {

        protected void setConcentration( double concentration ) {
            _liquid.setConcentrationH3O( concentration );
        }

        protected void setMoles( double moles ) {
            _liquid.setMolesH3O( moles );
        }
        
    }
    
    private class OHBarDragHandler extends BarDragHandler {

        protected void setConcentration( double concentration ) {
            _liquid.setConcentrationOH( concentration );
        }

        protected void setMoles( double moles ) {
            _liquid.setMolesOH( moles );
        }
        
    }
    
    private abstract class BarDragHandler extends PDragEventHandler {
        
        private double _globalClickYOffset; // y offset of mouse click from knob's origin, in global coordinates

        protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            // note the offset between the mouse click and the knob's origin
            Point2D pMouseLocal = event.getPositionRelativeTo( GraphNode.this );
            Point2D pMouseGlobal = GraphNode.this.localToGlobal( pMouseLocal );
            Point2D pKnobGlobal = GraphNode.this.localToGlobal( event.getPickedNode().getOffset() );
            _globalClickYOffset = pMouseGlobal.getY() - pKnobGlobal.getY();
        }
        
        protected void drag(PInputEvent event) {
            
            // determine the handle's new offset
            Point2D pMouseLocal = event.getPositionRelativeTo( GraphNode.this );
            Point2D pMouseGlobal = GraphNode.this.localToGlobal( pMouseLocal );
            Point2D pHandleGlobal = new Point2D.Double( pMouseGlobal.getX(), pMouseGlobal.getY() - _globalClickYOffset );
            Point2D pHandleLocal = GraphNode.this.globalToLocal( pHandleGlobal );
            
            // y offset, constrained to graph bounds
            double yOffset = _graphOutlineHeight - pHandleLocal.getY();
            if ( yOffset < 0 ) {
                yOffset = 0;
            }
            else if ( yOffset > _graphOutlineHeight ) {
                yOffset = _graphOutlineHeight;
            }

            // convert y offset to a model value
            double modelValue = 0;
            final double maxTickHeight = _graphOutlineHeight - TICKS_TOP_MARGIN;
            if ( _logScale ) {
                // log scale
                final double maxExponent = BIGGEST_LOG_TICK_EXPONENT;
                final double minExponent = BIGGEST_LOG_TICK_EXPONENT - NUMBER_OF_LOG_TICKS + 1;
                final double modelValueExponent = minExponent + ( ( maxExponent - minExponent ) * ( yOffset / maxTickHeight ) );
                modelValue = Math.pow( 10, modelValueExponent );
            }
            else {
                // linear scale, assumes that the y-axis starts at zero!
                final double maxMantissa = ( NUMBER_OF_LINEAR_TICKS - 1 ) * LINEAR_TICK_MANTISSA_SPACING;
                final double maxTickValue = maxMantissa * Math.pow( 10, _linearTicksExponent );
                modelValue = maxTickValue * yOffset / maxTickHeight;
            }
            
            // update the model
            if ( _concentrationUnits ) {
                setConcentration( modelValue );
            }
            else {
                setMoles( modelValue );
            }
        }

        /**
         * Updates the model's concentration.
         * @param concentration
         */
        protected abstract void setConcentration( double concentration );

        /**
         * Updates the model's number of moles.
         * @param moles
         */
        protected abstract void setMoles( double moles );
    }
}
