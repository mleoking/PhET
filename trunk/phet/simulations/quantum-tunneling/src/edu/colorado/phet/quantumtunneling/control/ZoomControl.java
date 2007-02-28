/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.control;

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

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.quantumtunneling.QTConstants;


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
    
    private static final Insets MARGIN = new Insets( 0, 0, 0, 0 ); // top,left,bottom,right
    private static final int SPACING_BETWEEN_BUTTONS = 2; // pixels
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _orientation;
    private ArrayList _plots; // array of XYPlot
    private ZoomSpec[] _specs;
    private int _zoomIndex;
    JButton _zoomInButton, _zoomOutButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param orientation
     * @param specs the specification of each zoom level
     * @param startingIndex
     */
    public ZoomControl( int orientation, ZoomSpec[] specs, int startingIndex ) {
        super();

        if ( orientation != HORIZONTAL && orientation != VERTICAL ) {
            throw new IllegalArgumentException( "invalid orientation: " + orientation );
        }
        if ( startingIndex < 0 || startingIndex > specs.length - 1 ) {
            throw new IndexOutOfBoundsException( "startingIndex out of bounds: " + startingIndex );
        }
        
        this.setOpaque( false );
        
        _orientation = orientation;
        _plots = new ArrayList();
        _specs = specs;
        _zoomIndex = startingIndex;
        
        try {
            // Icons on buttons
            ImageIcon zoomInIcon = new ImageIcon( ImageLoader.loadBufferedImage( QTConstants.IMAGE_ZOOM_IN ) );
            _zoomInButton = new JButton( zoomInIcon );
            ImageIcon zoomOutIcon = new ImageIcon( ImageLoader.loadBufferedImage( QTConstants.IMAGE_ZOOM_OUT ) );
            _zoomOutButton = new JButton( zoomOutIcon );
        }
        catch ( IOException ioe ) {
            // Fall back to text on buttons
            _zoomInButton = new JButton( "+" );
            _zoomOutButton = new JButton( "-" );
        }
        
        // Zoom In button
        _zoomInButton.setOpaque( true );
        _zoomInButton.setMargin( MARGIN );
        _zoomInButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleZoomIn();
            }
        } );
        
        // Zoom Out button
        _zoomOutButton.setOpaque( true );
        _zoomOutButton.setMargin( MARGIN );
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
        
        // make sure we're starting where we said we're starting
        setRange( _zoomIndex );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Adds a plot to the collection of plots that will be zoomed.
     * 
     * @param plot
     */
    public void addPlot( XYPlot plot ) {
        if ( ! _plots.contains( plot ) ) {
            _plots.add( plot );
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
     * @return
     */
    public int getZoomIndex() {
        return _zoomIndex;
    }
    
    /**
     * Sets the zoom index.
     * 
     * @param zoomIndex
     */
    public void setZoomIndex( int zoomIndex ) {
        if ( zoomIndex < 0 || zoomIndex > _specs.length - 1 ) {
            _zoomIndex = 0;
            System.err.println( "WARNING: zoom index out of range: " + zoomIndex );
        }
        else {
            _zoomIndex = zoomIndex;
        }
        setRange( _zoomIndex );
    }

    /*
     * Zooms in one level.
     */
    private void handleZoomIn() {
        _zoomIndex--;
        setRange( _zoomIndex );
    }
    
    /*
     * Zooms out one level.
     */
    private void handleZoomOut() {
        _zoomIndex++;
        setRange( _zoomIndex );
    }
    
    /*
     * Sets the range and tick marks.
     * The index indentifies the zoom level specification that we should use.
     * @param index
     */
    private void setRange( int index ) {
        
        ZoomSpec spec = _specs[ index ];
        Range range = spec.getRange();
        double tickSpacing = spec.getTickSpacing();
        String tickPattern = spec.getTickPattern();
        TickUnits tickUnits = new TickUnits();
        tickUnits.add( new NumberTickUnit( tickSpacing, new DecimalFormat( tickPattern ) ) );
        
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
        
        _zoomInButton.setEnabled( index > 0 );
        _zoomOutButton.setEnabled( index < _specs.length - 1 );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * ZoomSpec describes a zoom level.
     * A zoom level has a range, tick spacing, and tick pattern (format of the tick labels).
     */
    public static class ZoomSpec {
        
        private Range _range;
        private double _tickSpacing;
        private String _tickPattern;
        
        public ZoomSpec( Range range, double tickSpacing, String tickPattern ) {
            _range = new Range( range.getLowerBound(), range.getUpperBound() );
            _tickSpacing = tickSpacing;
            _tickPattern = tickPattern;
        }
        
        public Range getRange() {
            return _range;
        }
        
        public double getTickSpacing() {
            return _tickSpacing;
        }
        
        public String getTickPattern() {
            return _tickPattern;
        }
    }
}
