/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.data.Range;
import org.jfree.ui.RectangleInsets;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.control.CloseButtonNode;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.view.node.ModelViewTransform;

/**
 * PotentialEnergyChartNode is the Piccolo node that draws the potential energy chart.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PotentialEnergyChartNode extends PhetPNode implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final double DEFAULT_HEIGHT = 200;
    
    private static final Color BACKGROUND_COLOR = new Color( 255, 255, 255, 200 ); // translucent white
    private static final Color CHART_BORDER_COLOR = Color.BLACK;
    private static final Stroke CHART_BORDER_STROKE = new BasicStroke( 6f );
    private static final RectangleInsets CHART_INSETS = new RectangleInsets( 10, 10, 10, 10 ); // top,left,bottom,right

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Laser _laser;
    private Bead _bead;
    private ModelViewTransform _modelViewTransform;
    private double _sampleWidth;
    
    private PotentialEnergyPlot _plot;
    private JFreeChartNode _chartWrapper;
    private CloseButtonNode _closeButtonNode;
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param bead
     * @param laser
     * @param modelViewTransform
     * @param minPosition
     * @param maxPosition
     * @param sampleWidth
     */
    public PotentialEnergyChartNode( Bead bead, Laser laser, ModelViewTransform modelViewTransform, double minPosition, double maxPosition, double sampleWidth ) {
        super();
        
        _bead = bead;
        _bead.addObserver( this );
        
        _laser = laser;
        _laser.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        _sampleWidth = sampleWidth;
        
        _plot = new PotentialEnergyPlot( minPosition, maxPosition );
        
        JFreeChart chart = new JFreeChart( OTResources.getString( "title.potentialEnergyChart" ), null /* titleFont */, _plot, false /* createLegend */ );
        chart.setAntiAlias( true );
        chart.setBorderVisible( true );
        chart.setBackgroundPaint( BACKGROUND_COLOR );
        chart.setBorderPaint( CHART_BORDER_COLOR );
        chart.setBorderStroke( CHART_BORDER_STROKE );
        chart.setPadding( CHART_INSETS );
        _chartWrapper = new JFreeChartNode( chart );

        _closeButtonNode = new CloseButtonNode();
        _closeButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setVisible( false );
            }
        });
        
        addChild( _chartWrapper );
        addChild( _closeButtonNode );
        
        setPickable( false );
        _chartWrapper.setPickable( false );
        _chartWrapper.setChildrenPickable( false );
        
        updateLayout();
        updatePotentialEnergyCurve();
        updateBeadPosition();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Sets the dimensions of the chart.
     * 
     * @param w
     * @param h
     */
    public void setChartSize( double w, double h ) {
        // resize the chart
        _chartWrapper.setBounds( 0, 0, w, h );
        _chartWrapper.updateChartRenderingInfo();
        // update the layout of this node
        updateLayout();
        // update the range of the x-axis
        Rectangle2D dataArea = getPlotBounds();
        double minPosition = _modelViewTransform.viewToModel( dataArea.getMinX() );
        double maxPosition = _modelViewTransform.viewToModel( dataArea.getMaxX() );
        _plot.setPositionRange( minPosition, maxPosition );
        // update chart
        updatePotentialEnergyCurve();
        updateBeadPosition();
    }
    
    /**
     * Gets the bounds of the associated chart's plot.
     * The bounds are in the node's local coordinates.
     * 
     * @return Rectangle2D
     */
    public Rectangle2D getPlotBounds() {
        ChartRenderingInfo chartInfo = _chartWrapper.getChartRenderingInfo();
        PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();
        // Careful! getDataArea returns a direct reference!
        Rectangle2D dataAreaRef = plotInfo.getDataArea();
        Rectangle2D dataArea = new Rectangle2D.Double();
        dataArea.setRect( dataAreaRef );
        return dataArea;
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     * 
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_POSITION ) {
                updatePotentialEnergyCurve();
            }
        }
        else if ( o == _bead ) {
            if ( arg == Bead.PROPERTY_POSITION ) {
                updateBeadPosition();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the layout when the bounds change.
     */
    private void updateLayout() {
        
        final double horizMargin = 15;
        final double vertMargin = 15;
        
        final double maxWidth = _chartWrapper.getFullBounds().getWidth();
        double maxHeight = _closeButtonNode.getFullBounds().getHeight() + vertMargin;
        
        double x = 0;
        double y = 0;
        
        // chart: close button sits on chart
        x = 0;
        y = 0;
        _chartWrapper.setOffset( x, y );
        
        // close button: right edge, vertically centered
        x = maxWidth - horizMargin - _closeButtonNode.getFullBounds().getWidth();
        y = ( maxHeight - _closeButtonNode.getFullBounds().getHeight() ) / 2;
        _closeButtonNode.setOffset( x, y );
    }
    
    /*
     * Updates the potential energy curve.
     */
    private void updatePotentialEnergyCurve() {
        _plot.clear();
        Range positionRange = _plot.getPositionRange();
        final double maxPositon = positionRange.getUpperBound();
        double position = positionRange.getLowerBound();
        while ( position <= maxPositon ) {
            double potentialEnergy = _laser.getPotentialEnergy( position );
            _plot.addData( position, potentialEnergy );
            position += _sampleWidth;
        }
    }
    
    /*
     * Updates the position of the bead on the potential energy curve.
     */
    private void updateBeadPosition() {
        //XXX move the bead to a point on the potential energy curve
    }
}
