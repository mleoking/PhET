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
import java.text.NumberFormat;

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
 * ZoomControl
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ZoomControl extends JPanel {
    
    public static final int HORIZONTAL = SwingConstants.HORIZONTAL;
    public static final int VERTICAL = SwingConstants.VERTICAL;
    
    private static final Insets MARGIN = new Insets( 0, 0, 0, 0 ); // top,left,bottom,right
    private static final int SPACING_BETWEEN_BUTTONS = 2; // pixels
    
    private int _orientation;
    private XYPlot _plot;
    private ZoomSpec[] _specs;
    private int _zoomIndex;
    JButton _zoomInButton, _zoomOutButton;
    
    public ZoomControl( int orientation, XYPlot plot, ZoomSpec[] specs, int startingIndex ) {
        super();

        if ( orientation != HORIZONTAL && orientation != VERTICAL ) {
            throw new IllegalArgumentException( "invalid orientation: " + orientation );
        }
        if ( startingIndex < 0 || startingIndex > specs.length - 1 ) {
            throw new IndexOutOfBoundsException( "startingIndex out of bounds: " + startingIndex );
        }
        
        this.setOpaque( false );
        
        _orientation = orientation;
        _plot = plot;
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
        _zoomInButton.setOpaque( false );
        _zoomInButton.setMargin( MARGIN );
        _zoomInButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleZoomIn();
            }
        } );
        
        // Zoom Out button
        _zoomOutButton.setOpaque( false );
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
    
    public int getZoomIndex() {
        return _zoomIndex;
    }
    
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

    private void handleZoomIn() {
        _zoomIndex--;
        setRange( _zoomIndex );
    }
    
    private void handleZoomOut() {
        _zoomIndex++;
        setRange( _zoomIndex );
    }
    
    private void setRange( int index ) {
        ZoomSpec spec = _specs[ index ];
        Range range = spec.getRange();
        double tickSpacing = spec.getTickSpacing();
        String tickPattern = spec.getTickPattern();
        TickUnits tickUnits = new TickUnits();
        tickUnits.add( new NumberTickUnit( tickSpacing, new DecimalFormat( tickPattern ) ) );
        if ( _orientation == HORIZONTAL ) {
            _plot.getDomainAxis().setRange( range );
            _plot.getDomainAxis().setStandardTickUnits( tickUnits );
        }
        else {
            _plot.getRangeAxis().setRange( range );
            _plot.getRangeAxis().setStandardTickUnits( tickUnits );
        }
        _zoomInButton.setEnabled( index > 0 );
        _zoomOutButton.setEnabled( index < _specs.length - 1 );
    }
    
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
