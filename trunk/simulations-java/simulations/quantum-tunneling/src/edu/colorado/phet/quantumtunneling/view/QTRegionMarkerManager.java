/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;

import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.model.AbstractPotential;


/**
 * QTRegionMarkerManager manages the vertical region markers for a set of XYPlots.
 * We're using JFreeChart's domain markers are used to indicate the potential energy's
 * region boundaries.  This manager class updates the region boundaries whenever
 * the potential energy changes.  
 * <p>
 * Note that we could have done this by having the plots or chart observe
 * the potential energy and handle the updates.  But we're doing this using a
 * "manager" pattern so that we easily switch between drawing the markers via 
 * JFreeChart or via a custom Piccolo node (for improved performance).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTRegionMarkerManager implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ArrayList _plots;  // array of XYPlot
    private AbstractPotential _potentialEnergy;
    private Color _markerColor;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public QTRegionMarkerManager() {
        _plots = new ArrayList();
        _potentialEnergy = null;
        _markerColor = QTConstants.COLOR_SCHEME.getRegionMarkerColor();
    }

    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        if ( _potentialEnergy != null ) {
            _potentialEnergy.deleteObserver( this );
            _potentialEnergy = null;
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the color used for region markers.
     * 
     * @param color
     */
    public void setMarkerColor( Color color ) {
        _markerColor = color;
        updateRegionMarkers();
    }
    
    /**
     * Sets the potential energy model that is used to set region markers.
     * 
     * @param potentialEnergy
     */
    public void setPotentialEnergy( AbstractPotential potentialEnergy ) {
        if ( _potentialEnergy != null ) {
            _potentialEnergy.deleteObserver( this );
        }
        _potentialEnergy = potentialEnergy;
        _potentialEnergy.addObserver( this );
        updateRegionMarkers();
    }
    
    /**
     * Adds a plot.
     * @param plot
     */
    public void addPlot( XYPlot plot ) {
        _plots.add( plot );
        updateRegionMarkers();
    }
    
    /**
     * Removes a plot.
     * @param plot
     */
    public void removePlot( XYPlot plot ) {
        _plots.remove( plot );
        updateRegionMarkers();
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates region markers when the potential energy changes.
     * 
     * @param observable
     * @param arg
     */
    public void update( Observable observable, Object arg ) {
        if ( observable == _potentialEnergy ) {
            updateRegionMarkers();
        }
    }
    
    //----------------------------------------------------------------------------
    // Markers
    //----------------------------------------------------------------------------
    
    /*
     * Updates the region markers to match the the model.
     */
    private void updateRegionMarkers() {
        clearRegionMarkers();
        if ( _potentialEnergy != null ) {
            int numberOfRegions = _potentialEnergy.getNumberOfRegions();
            for ( int i = 1; i < numberOfRegions; i++ ) {
                double start = _potentialEnergy.getStart( i );
                addRegionMarker( start );
            }
        }
    }
    
    /*
     * Adds a region marker at the specified position.
     * A region marker is a vertical line that denotes the 
     * boundary between two regions.
     * 
     * @param x
     */
    private void addRegionMarker( double x ) {

        Marker marker = new ValueMarker( x );
        marker.setPaint( _markerColor );
        marker.setStroke( QTConstants.REGION_MARKER_STROKE );
        
        Iterator i = _plots.iterator();
        while ( i.hasNext() ) {
            XYPlot plot = (XYPlot) i.next();
            plot.addDomainMarker( marker );
        }
    }
    
    /*
     * Clears all region markers.
     */
    private void clearRegionMarkers() {
        Iterator i = _plots.iterator();
        while ( i.hasNext() ) {
            XYPlot plot = (XYPlot) i.next();
            plot.clearDomainMarkers();
        }
    }
}
