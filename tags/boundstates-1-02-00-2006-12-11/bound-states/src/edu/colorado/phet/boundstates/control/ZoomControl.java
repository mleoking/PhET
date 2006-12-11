/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.control;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.util.AxisSpec;
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.ImageLoader;


/**
 * ZoomControl is a control for zooming in and out.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ZoomControl extends JPanel {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final int HORIZONTAL = SwingConstants.HORIZONTAL;
    public static final int VERTICAL = SwingConstants.VERTICAL;
    
    private static final int SPACING_BETWEEN_BUTTONS = 2; // pixels
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _orientation;
    private ArrayList _plots; // array of XYPlot
    private ZoomSpec _zoomSpec;
    private int _zoomIndex;
    private JButton _zoomInButton, _zoomOutButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param orientation
     */
    public ZoomControl( int orientation ) {
        super();

        if ( orientation != HORIZONTAL && orientation != VERTICAL ) {
            throw new IllegalArgumentException( "invalid orientation: " + orientation );
        }
        
        if ( PhetUtilities.isMacintosh() ) {
            this.setOpaque( false );
        }
        
        _orientation = orientation;
        _plots = new ArrayList();
        _zoomSpec = null;
        
        try {
            // Icons on buttons
            ImageIcon zoomInIcon = new ImageIcon( ImageLoader.loadBufferedImage( BSConstants.IMAGE_ZOOM_IN ) );
            _zoomInButton = new JButton( zoomInIcon );
            ImageIcon zoomOutIcon = new ImageIcon( ImageLoader.loadBufferedImage( BSConstants.IMAGE_ZOOM_OUT ) );
            _zoomOutButton = new JButton( zoomOutIcon );
        }
        catch ( IOException ioe ) {
            // Fall back to text on buttons
            _zoomInButton = new JButton( "+" );
            _zoomOutButton = new JButton( "-" );
        }
        
        // Zoom In button
        _zoomInButton.setOpaque( false );
        _zoomInButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleZoomIn();
            }
        } );
        
        // Zoom Out button
        _zoomOutButton.setOpaque( false );
        _zoomOutButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleZoomOut();
            }
        } );
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        if ( orientation == HORIZONTAL ) {
            layout.setInsets( new Insets( 0, 0, 0, SPACING_BETWEEN_BUTTONS ) ); // top,left,bottom,right
            layout.addComponent( _zoomInButton, 0, 0 );
            layout.addComponent( _zoomOutButton, 0, 1 );
        }
        else {
            layout.setInsets( new Insets( 0, 0, SPACING_BETWEEN_BUTTONS, 0 ) ); // top,left,bottom,right
            layout.addComponent( _zoomInButton, 0, 0 );
            layout.addComponent( _zoomOutButton, 1, 0 );
        }
        
        updateButtons();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the zoom specification.
     * 
     * @param zoomSpec
     */
    public void setZoomSpec( ZoomSpec zoomSpec ) {
        _zoomSpec = zoomSpec;
        _zoomIndex = zoomSpec.getDefaultIndex();
        updateAxis();
        updateButtons();
    }
    
    /**
     * Adds a plot to the collection of plots that will be zoomed.
     * 
     * @param plot
     */
    public void addPlot( XYPlot plot ) {
        if ( ! _plots.contains( plot ) ) {
            _plots.add( plot );
            if ( _zoomSpec != null ) {
                updateAxis();
            }
        }
    }
    
    /**
     * Removes a plot from the collection of plots that will be zoomed.
     * 
     * @param plot
     */
    public void removePlot( XYPlot plot ) {
        _plots.remove( plot );
    }
    
    /**
     * Gets the current zoom index.
     * The zoom index is an index into the constructor's specs argument.
     * @return int
     */
    public int getZoomIndex() {
        return _zoomIndex;
    }
    
    /**
     * Gets the AxisSpec that corresponds to the current zoom level.
     * @return AxisSpec
     */
    public AxisSpec getAxisSpec() {
        return _zoomSpec.getAxisSpec( _zoomIndex );
    }
    
    /**
     * Sets the zoom index.
     * 
     * @param zoomIndex
     */
    public void setZoomIndex( int zoomIndex ) {
        _zoomIndex = zoomIndex;
        updateAxis();
        updateButtons();
    }

    /**
     * Resets to the default zoom level.
     */
    public void resetZoom() {
        _zoomIndex = _zoomSpec.getDefaultIndex();
        updateAxis();
        updateButtons();
    }
    
    /*
     * Zooms in one level.
     */
    private void handleZoomIn() {
        _zoomIndex--;
        updateAxis();
        updateButtons();
    }
    
    /*
     * Zooms out one level.
     */
    private void handleZoomOut() {
        _zoomIndex++;
        updateAxis();
        updateButtons();
    }
    
    /*
     * Updates the axis' range and tick marks in each managed plot.
     */
    private void updateAxis() {
        
        // Range
        AxisSpec axisSpec = _zoomSpec.getAxisSpec( _zoomIndex );
        Range range = axisSpec.getRange();
        
        // Ticks
        double tickSpacing = axisSpec.getTickSpacing();
        DecimalFormat tickFormat = axisSpec.getTickFormat();
        TickUnits tickUnits = new TickUnits();
        tickUnits.add( new NumberTickUnit( tickSpacing, tickFormat ) );
        
        // Adjust the proper axis in each plot
        Iterator i = _plots.iterator();
        while ( i.hasNext() ) {
            XYPlot plot = (XYPlot) i.next();
            if ( _orientation == HORIZONTAL ) {
                plot.getDomainAxis().setRange( range );
                plot.getDomainAxis().setStandardTickUnits( tickUnits );
            }
            else {
                plot.getRangeAxis().setRange( range );
                plot.getRangeAxis().setStandardTickUnits( tickUnits );
            }
        }
    }
    
    /*
     * Updates the state of the zoom buttons.
     */
    private void updateButtons() {
        _zoomInButton.setEnabled( _zoomSpec != null && _zoomIndex > 0 );
        _zoomOutButton.setEnabled( _zoomSpec != null && _zoomIndex < _zoomSpec.getNumberOfZoomLevels() - 1 );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * ZoomSpec describes the number of zoom levels, how to configure the axis
     * for each zoom level, and which zoom level is the default.
     * The axisSpecs are assumed to be ordered from smallest to largest range.
     * Objects of this type are immutable.
     */
    public static class ZoomSpec {
        
        private AxisSpec[] _axisSpecs;
        private int _defaultIndex;
        
        public ZoomSpec( AxisSpec[] axisSpecs, final int defaultIndex ) {
            assert( axisSpecs.length > 0 );
            assert( defaultIndex < axisSpecs.length );
            _axisSpecs = axisSpecs;
            _defaultIndex = defaultIndex;
        }
        
        public ZoomSpec( AxisSpec[] axisSpecs ) {
            this( axisSpecs, 0 );
        }
        
        /**
         * Convenience constructor, for axes that don't really zoom.
         * @param axisSpec
         */
        public ZoomSpec( AxisSpec axisSpec ) {
            _axisSpecs = new AxisSpec[1];
            _axisSpecs[0] = axisSpec;
            _defaultIndex = 0;
        }
        
        public int getNumberOfZoomLevels() {
            return _axisSpecs.length;
        }  
        
        public int getDefaultIndex() {
            return _defaultIndex;
        }
        
        public AxisSpec[] getAxisSpecs() {
            return _axisSpecs;
        }
        
        public AxisSpec getAxisSpec( int index ) {
            return _axisSpecs[ index ];
        }
        
        public AxisSpec getDefaultAxisSpec() {
            return _axisSpecs[ _defaultIndex ];
        }
    }
}
