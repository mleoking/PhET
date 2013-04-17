// Copyright 2002-2011, University of Colorado

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
import org.jfree.ui.RectangleInsets;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.control.CloseButtonNode;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.model.OTModelViewTransform;
import edu.colorado.phet.opticaltweezers.view.BeadNode;
import edu.umd.cs.piccolo.util.PBounds;

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
    private OTModelViewTransform _modelViewTransform;
    private double _sampleWidth;
    
    private PotentialEnergyPlot _plot;
    private JFreeChartNode _chartWrapper;
    private CloseButtonNode _closeButtonNode;
    private BeadNode _beadNode;
    
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
    public PotentialEnergyChartNode( Bead bead, Laser laser, OTModelViewTransform modelViewTransform, double sampleWidth ) {
        super();
        
        _bead = bead;
        _bead.addObserver( this );
        
        _laser = laser;
        _laser.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        _sampleWidth = sampleWidth;
        
        _plot = new PotentialEnergyPlot();
        
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
        
        _beadNode = new BeadNode( 20 );
        
        addChild( _chartWrapper );
        addChild( _closeButtonNode );
        addChild( _beadNode );
        
        setPickable( false );
        _chartWrapper.setPickable( false );
        _chartWrapper.setChildrenPickable( false );
        
        // y axis range
        double minPotentialEnergy = _laser.getMinPotentialEnergy();
        double maxPotentialEnergy = _laser.getMaxPotentialEnergy();
        _plot.setPotentialEnergyRange( minPotentialEnergy * 1.02, maxPotentialEnergy );
    
        updateLayout();
        updateCurve();
        updateBeadPosition();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Update the chart when it becomes visible.
     * 
     * @param visible true or false
     */
    public void setVisible( boolean visible ) {
        if ( visible != isVisible() ) {
            if ( visible ) {
                updateCurve();
                updateBeadPosition();
            }
            super.setVisible( visible );
        }
    }

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
        // x axis
        Rectangle2D dataArea = getPlotBounds();
        double minPosition = _modelViewTransform.viewToModel( dataArea.getMinX() );
        double maxPosition = _modelViewTransform.viewToModel( dataArea.getMaxX() );
        _plot.setPositionRange( minPosition, maxPosition );
        // update chart
        updateCurve();
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
        if ( isVisible() ) {
            if ( o == _laser ) {
                if ( arg == Laser.PROPERTY_POSITION || arg == Laser.PROPERTY_POWER || arg == Laser.PROPERTY_RUNNING ) {
                    updateCurve();
                }
            }
            else if ( o == _bead ) {
                if ( arg == Bead.PROPERTY_POSITION ) {
                    updateBeadPosition();
                }
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
        
        PBounds chartBounds = _chartWrapper.getFullBoundsReference();
        PBounds closeButtonBounds = _closeButtonNode.getFullBoundsReference();
        
        final double maxWidth = chartBounds.getWidth();
        double maxHeight = closeButtonBounds.getHeight() + vertMargin;
        
        double x = 0;
        double y = 0;
        
        // chart: close button sits on chart
        x = 0;
        y = 0;
        _chartWrapper.setOffset( x, y );
        
        // close button: right edge, vertically centered
        x = maxWidth - horizMargin - closeButtonBounds.getWidth();
        y = ( maxHeight - closeButtonBounds.getHeight() ) / 2;
        _closeButtonNode.setOffset( x, y );
    }
    
    /*
     * Updates the potential energy curve.
     */
    private void updateCurve() {
        
        //  remove existing data
        _plot.clear();
        
        // compute the new potential energy curve
        double x = _plot.getPositionRange().getLowerBound();
        final double y = _bead.getY();
        final double maxX = _plot.getPositionRange().getUpperBound();
        while ( x <= maxX ) {
            double potentialEnergy = _laser.getPotentialEnergy( x, y );
            _plot.addData( x, potentialEnergy );
            x += _sampleWidth;
        }
    }
    
    /*
     * Updates the position of the bead on the potential energy curve.
     */
    private void updateBeadPosition() {
        // model values
        double beadX = _bead.getX();
        double potentialEnergy = _bead.getPotentialEnergy();
        
        // plot bounds (view and model coordinates)
        Rectangle2D plotBounds = getPlotBounds();
        double minPosition = _plot.getPositionRange().getLowerBound();
        double maxPosition = _plot.getPositionRange().getUpperBound();
        double minEnergy = _plot.getPotentialEnergyRange().getLowerBound();
        double maxEnergy = _plot.getPotentialEnergyRange().getUpperBound();
        
        if ( beadX < minPosition || beadX > maxPosition ) {
            _beadNode.setVisible( false );
        }
        else {
            _beadNode.setVisible( true );
            double nodeX = _chartWrapper.getXOffset() + plotBounds.getMinX() + ( plotBounds.getWidth() * ( beadX - minPosition ) / ( maxPosition - minPosition ) );
            double nodeY = _chartWrapper.getYOffset() + plotBounds.getMaxY() - ( plotBounds.getHeight() * ( potentialEnergy - minEnergy ) / ( maxEnergy - minEnergy ) );
            _beadNode.setOffset( nodeX, nodeY );
        }
    }
}
