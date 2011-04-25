// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.boundstates.control;

import JSci.maths.statistics.OutOfRangeException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.BSResources;
import edu.colorado.phet.boundstates.util.AxisSpec;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


/**
 * ZoomControl is a control for zooming in and out.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
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

        // Zoom In button
        ImageIcon zoomInIcon = new ImageIcon( BSResources.getImage( BSConstants.IMAGE_ZOOM_IN ) );
        _zoomInButton = new JButton( zoomInIcon );
        _zoomInButton.setOpaque( false );
        _zoomInButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleZoomIn();
            }
        } );

        // Zoom Out button
        ImageIcon zoomOutIcon = new ImageIcon( BSResources.getImage( BSConstants.IMAGE_ZOOM_OUT ) );
        _zoomOutButton = new JButton( zoomOutIcon );
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
        updateAxis();
        updateButtons();
    }

    /**
     * Adds a plot to the collection of plots that will be zoomed.
     *
     * @param plot
     */
    public void addPlot( XYPlot plot ) {
        if ( !_plots.contains( plot ) ) {
            _plots.add( plot );
            if ( _zoomSpec != null ) {
                updateAxis();
            }
        }
    }

    /*
    * Zooms in one level.
    */
    private void handleZoomIn() {
        _zoomSpec.zoomIn();
        updateAxis();
        updateButtons();
    }

    /*
    * Zooms out one level.
    */
    private void handleZoomOut() {
        _zoomSpec.zoomOut();
        updateAxis();
        updateButtons();
    }

    /*
    * Updates the axis' range and tick marks in each managed plot.
    */
    private void updateAxis() {

        // Range
        AxisSpec axisSpec = _zoomSpec.getAxisSpec();
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
        _zoomInButton.setEnabled( _zoomSpec != null && _zoomSpec.getCurrentIndex() > 0 );
        _zoomOutButton.setEnabled( _zoomSpec != null && _zoomSpec.getCurrentIndex() < _zoomSpec.getNumberOfZoomLevels() - 1 );
    }

    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------

    /**
     * ZoomSpec describes the number of zoom levels, how to configure the axis
     * for each zoom level, and the current zoom index.
     * The axisSpecs are assumed to be ordered from smallest to largest range.
     */
    public static class ZoomSpec {

        private final AxisSpec[] _axisSpecs;
        private final int _startIndex;
        private int _currentIndex;

        public ZoomSpec( AxisSpec[] axisSpecs, final int startIndex ) {
            assert ( axisSpecs.length > 0 );
            assert ( startIndex >= 0 && startIndex < axisSpecs.length );
            _axisSpecs = axisSpecs;
            _startIndex = _currentIndex = startIndex;
        }

        public ZoomSpec( AxisSpec[] axisSpecs ) {
            this( axisSpecs, 0 );
        }

        /**
         * Convenience constructor, for axes that have 1 zoom level, and therefore don't zoom.
         *
         * @param axisSpec
         */
        public ZoomSpec( AxisSpec axisSpec ) {
            this( new AxisSpec[] { axisSpec } );
        }

        public int getNumberOfZoomLevels() {
            return _axisSpecs.length;
        }

        public void setCurrentIndex( int index ) {
            if ( _currentIndex < 0 || _currentIndex > getNumberOfZoomLevels() - 1 ) {
                throw new OutOfRangeException( "index out of range: " + index );
            }
            _currentIndex = index;
        }

        public int getCurrentIndex() {
            return _currentIndex;
        }

        public void zoomIn() {
            setCurrentIndex( getCurrentIndex() - 1 );
        }

        public void zoomOut() {
            setCurrentIndex( getCurrentIndex() + 1 );
        }

        public AxisSpec getAxisSpec() {
            return _axisSpecs[_currentIndex];
        }

        public void reset() {
            _currentIndex = _startIndex;
        }
    }
}
