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

    public interface SolutionFactory {
        AqueousSolution createSolution();
    }
    
    private final SolutionFactory defaultSolutionFactory;
    private AqueousSolution solution;
    private final Beaker beaker;
    private final PHMeter pHMeter;
    private final MagnifyingGlass magnifyingGlass;
    private final ConcentrationGraph concentrationGraph;
    
    private EventListenerList listeners;
    
    public ABSModel( SolutionFactory defaultSolutionFactory ) {
        this.defaultSolutionFactory = defaultSolutionFactory;
        this.solution = defaultSolutionFactory.createSolution();
        beaker = new Beaker( solution, ABSConstants.BEAKER_LOCATION, ABSConstants.BEAKER_VISIBLE, ABSConstants.BEAKER_SIZE );
        pHMeter = new PHMeter( solution, ABSConstants.PH_METER_LOCATION, ABSConstants.PH_METER_VISIBLE, ABSConstants.PH_METER_SHAFT_SIZE, ABSConstants.PH_METER_TIP_SIZE, beaker );
        magnifyingGlass = new MagnifyingGlass( solution, ABSConstants.MAGNIFYING_GLASS_LOCATION, ABSConstants.MAGNIFYING_GLASS_VISIBLE, ABSConstants.MAGNIFYING_GLASS_DIAMETER, ABSConstants.WATER_VISIBLE );
        concentrationGraph = new ConcentrationGraph( solution, ABSConstants.CONCENTRATION_GRAPH_LOCATION, ABSConstants.CONCENTRATION_GRAPH_VISIBLE, ABSConstants.CONCENTRATION_GRAPH_SIZE );
        listeners = new EventListenerList();
        reset();
    }
    
    public void reset() {
        setSolution( defaultSolutionFactory.createSolution() );
        magnifyingGlass.setVisible( ABSConstants.MAGNIFYING_GLASS_VISIBLE );
        magnifyingGlass.setWaterVisible( ABSConstants.WATER_VISIBLE );
        concentrationGraph.setVisible( ABSConstants.CONCENTRATION_GRAPH_VISIBLE );
        pHMeter.setLocation( ABSConstants.PH_METER_LOCATION );
        pHMeter.setVisible( ABSConstants.PH_METER_VISIBLE );
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
            beaker.setSolution( solution );
            pHMeter.setSolution( solution );
            magnifyingGlass.setSolution( solution );
            concentrationGraph.setSolution( solution );
            fireSolutionChanged();
        }
    }
    
    public AqueousSolution getSolution() {
        return solution;
    }
    
    public interface ModelChangeListener extends EventListener {
        public void solutionChanged();
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
}
