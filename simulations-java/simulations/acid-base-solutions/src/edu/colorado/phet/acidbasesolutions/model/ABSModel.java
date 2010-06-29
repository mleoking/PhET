/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;

/**
 * Model for the "Acid-Base Solutions" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSModel {

    /**
     * Factory for creating a default solution, used by modules when instantiating the model.
     */
    public interface DefaultSolutionFactory {
        AqueousSolution getDefaultSolution();
    }
    
    private final DefaultSolutionFactory defaultSolutionFactory;
    private AqueousSolution solution;
    private final Beaker beaker;
    private final PHMeter pHMeter;
    private final MagnifyingGlass magnifyingGlass;
    private final ConcentrationGraph concentrationGraph;
    private boolean waterVisible; // water visibility, a global property, included in model for convenience
    
    private EventListenerList listeners;
    
    public ABSModel( DefaultSolutionFactory defaultSolutionFactory ) {
        this.defaultSolutionFactory = defaultSolutionFactory;
        beaker = new Beaker( ABSConstants.BEAKER_LOCATION, ABSConstants.BEAKER_VISIBLE, ABSConstants.BEAKER_SIZE );
        magnifyingGlass = new MagnifyingGlass( ABSConstants.MAGNIFYING_GLASS_LOCATION, ABSConstants.MAGNIFYING_GLASS_VISIBLE, ABSConstants.MAGNIFYING_GLASS_DIAMETER );
        pHMeter = new PHMeter( ABSConstants.PH_METER_LOCATION, ABSConstants.PH_METER_VISIBLE, ABSConstants.PH_METER_SHAFT_LENGTH );
        concentrationGraph = new ConcentrationGraph( ABSConstants.CONCENTRATION_GRAPH_LOCATION, ABSConstants.CONCENTRATION_GRAPH_VISIBLE, ABSConstants.CONCENTRATION_GRAPH_SIZE );
        waterVisible = ABSConstants.WATER_VISIBLE;
        listeners = new EventListenerList();
        reset();
    }
    
    public void reset() {
        setSolution( defaultSolutionFactory.getDefaultSolution() );
        getMagnifyingGlass().setVisible( ABSConstants.MAGNIFYING_GLASS_VISIBLE );
        getConcentrationGraph().setVisible( ABSConstants.CONCENTRATION_GRAPH_VISIBLE );
        getPHMeter().setLocation( ABSConstants.PH_METER_LOCATION );
        getPHMeter().setVisible( ABSConstants.PH_METER_VISIBLE );
        setWaterVisible( ABSConstants.WATER_VISIBLE );
    }
    
    public Beaker getBeaker() {
        return beaker;
    }
    
    public MagnifyingGlass getMagnifyingGlass() {
        return magnifyingGlass;
    }
    
    public PHMeter getPHMeter() {
        return pHMeter;
    }
    
    public ConcentrationGraph getConcentrationGraph() {
        return concentrationGraph;
    }
    
    public void setSolution( AqueousSolution solution ) {
        if ( solution != this.solution ) {  /* yes, referential equality */
            this.solution = solution;
            fireSolutionChanged();
        }
    }
    
    public AqueousSolution getSolution() {
        return solution;
    }
    
    public void setWaterVisible( boolean waterVisible ) {
        if ( waterVisible != this.waterVisible ) {
            this.waterVisible = waterVisible;
            fireWaterVisibleChanged();
        }
    }
    
    public boolean isWaterVisible() {
        return waterVisible;
    }
    
    public interface ModelChangeListener extends EventListener {
        public void solutionChanged();
        public void waterVisibleChanged();
    }
    
    public static class ModelChangeAdapter implements ModelChangeListener {
        public void solutionChanged() {}
        public void waterVisibleChanged() {}
    }
    
    public void addModelChangeListener( ModelChangeListener listener ) {
        listeners.add( ModelChangeListener.class, listener );
    }
    
    public void removeModelChangeListener( ModelChangeListener listener ) {
        listeners.remove( ModelChangeListener.class, listener );
    }
    
    private void fireSolutionChanged() {
        for ( ModelChangeListener listener : listeners.getListeners( ModelChangeListener.class ) ) {
            listener.solutionChanged();
        }
    }
    
    private void fireWaterVisibleChanged() {
        for ( ModelChangeListener listener : listeners.getListeners( ModelChangeListener.class ) ) {
            listener.waterVisibleChanged();
        }
    }
}
