/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

/**
 * Model for the "Acid-Base Solutions" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSModel  {
    
    public interface SolutionFactory {
        AqueousSolution createSolution();
    }
    
    private final SolutionFactory defaultSolutionFactory;
    private AqueousSolution solution;
    private final Beaker beaker;
    private final PHMeter pHMeter;
    private final MagnifyingGlass magnifyingGlass;
    private final ConcentrationGraph concentrationGraph;
    private final ReactionEquation reactionEquation;
    private final PHPaper pHPaper;
    private final ConductivityTester conductivityTester;
    
    private EventListenerList listeners;
    
    public ABSModel( IClock clock, SolutionFactory defaultSolutionFactory ) {
        
        clock.addClockListener( new ClockAdapter() {
            @Override
            public void clockTicked( ClockEvent clockEvent ) {
                pHPaper.clockTicked( clockEvent.getSimulationTimeChange() );
            }
        } );
        
        this.defaultSolutionFactory = defaultSolutionFactory;
        this.solution = defaultSolutionFactory.createSolution();
        
        beaker = new Beaker( solution, ABSConstants.BEAKER_LOCATION, true, ABSConstants.BEAKER_SIZE );
        pHMeter = new PHMeter( solution, ABSConstants.PH_METER_LOCATION, ABSConstants.PH_METER_VISIBLE, ABSConstants.PH_METER_SHAFT_SIZE, ABSConstants.PH_METER_TIP_SIZE, beaker );
        magnifyingGlass = new MagnifyingGlass( solution, ABSConstants.MAGNIFYING_GLASS_LOCATION, ABSConstants.MAGNIFYING_GLASS_VISIBLE, ABSConstants.MAGNIFYING_GLASS_DIAMETER, ABSConstants.WATER_VISIBLE );
        concentrationGraph = new ConcentrationGraph( solution, ABSConstants.CONCENTRATION_GRAPH_LOCATION, ABSConstants.CONCENTRATION_GRAPH_VISIBLE, ABSConstants.CONCENTRATION_GRAPH_SIZE );
        reactionEquation = new ReactionEquation( solution, ABSConstants.REACTION_EQUATION_LOCATION, true );
        pHPaper = new PHPaper( solution, ABSConstants.PH_PAPER_LOCATION, ABSConstants.PH_PAPER_VISIBLE, ABSConstants.PH_PAPER_SIZE, beaker );
        conductivityTester = new ConductivityTester( solution, ABSConstants.CONDUCTIVITY_TESTER_LOCATION, ABSConstants.CONDUCTIVITY_TESTER_VISIBLE, 
                ABSConstants.CONDUCTIVITY_TESTER_PROBE_SIZE, ABSConstants.CONDUCTIVITY_TESTER_POSITIVE_PROBE_LOCATION, ABSConstants.CONDUCTIVITY_TESTER_NEGATIVE_PROBE_LOCATION, beaker );
        
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
    
    public ReactionEquation getReactionEquation() {
        return reactionEquation;
    }
    
    public PHPaper getPHPaper() {
        return pHPaper;
    }
    
    public ConductivityTester getConductivityTester() {
        return conductivityTester;
    }
    
    public void setSolution( AqueousSolution solution ) {
        if ( solution != this.solution ) {  /* yes, referential equality */
            this.solution = solution;
            beaker.setSolution( solution );
            pHMeter.setSolution( solution );
            magnifyingGlass.setSolution( solution );
            concentrationGraph.setSolution( solution );
            reactionEquation.setSolution( solution );
            pHPaper.setSolution( solution );
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
