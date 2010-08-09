/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.graph;

import java.text.DecimalFormat;

import edu.colorado.phet.acidbasesolutions.model.*;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.TimesTenNumberFormat;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;

/**
 * Graph that depicts the concentrations related to a solution.
 * Updates itself based on changes in the solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationGraphNode extends AbstractConcentrationGraphNode {

    private static final boolean X_AXIS_LABELED = false; // puts symbols and icons under bars
    private static final TimesTenNumberFormat FORMAT = new TimesTenNumberFormat( "0.00" );
    private static final DecimalFormat H2O_FORMAT = new DefaultDecimalFormat( "#0.0" );
    
    private final ConcentrationGraph graph;
    
    public ConcentrationGraphNode( final ConcentrationGraph graph ) {
        super( graph.getSizeReference(), X_AXIS_LABELED );
        
        // not interactive
        setPickable( false );
        setChildrenPickable( false );
        
        // model changes
        this.graph = graph;
        graph.addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
            @Override
            public void solutionChanged() {
                updateMolecules();
                updateValues();
            }
            
            @Override
            public void concentrationChanged() {
                updateValues();
            }

            @Override
            public void strengthChanged() {
                updateValues();
            }
        });
        
        // graph listener
        graph.addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
            @Override
            public void visibilityChanged() {
                setVisible( graph.isVisible() );
            }
        });
        
        double x = graph.getLocationReference().getX() - ( getFullBoundsReference().getWidth() / 2 ) - PNodeLayoutUtils.getOriginXOffset( this );
        double y = graph.getLocationReference().getY() - ( getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( this );
        setOffset( x, y );
        setVisible( graph.isVisible() );
        updateMolecules();
        updateValues();
    }
    
    /*
     * Updates the molecules (symbol, icon, color) for all bars.
     */
    private void updateMolecules() {
        AqueousSolution solution = graph.getSolution();
        if ( solution instanceof PureWaterSolution ) {
            updateWaterMolecules( solution );
        }
        else if ( solution instanceof AcidSolution ) {
            updateAcidMolecules( solution, solution instanceof StrongAcidSolution );
        }
        else if ( solution instanceof StrongBaseSolution ) {
            updateStrongBaseMolecules( solution );
        }
        else if ( solution instanceof WeakBaseSolution ) {
            updateWeakBaseMolecules( solution );
        }
        else {
            throw new IllegalStateException( "unsupported solution type: " + solution.getClass().getName() );
        }
    }
    
    // 2H2O <-> H3O+ + OH-
    private void updateWaterMolecules( AqueousSolution solution ) {
        setAllVisible( true );
        setMolecule( 0, solution.getWaterMolecule(), H2O_FORMAT );
        setMolecule( 1, solution.getH3OMolecule(), FORMAT );
        setMolecule( 2, solution.getOHMolecule(), FORMAT );
        setVisible( 3, false );
    }
    
    // HA + H2O <-> A- + H3O+  (strong)
    // HA + H2O -> A- + H3O+   (weak)
    private void updateAcidMolecules( AqueousSolution solution, boolean isStrong ) {
        setAllVisible( true );
        setMolecule( 0, solution.getSolute(), FORMAT, isStrong /* negligibleEnabled */ );
        setMolecule( 1, solution.getWaterMolecule(), H2O_FORMAT );
        setMolecule( 2, solution.getProduct(), FORMAT );
        setMolecule( 3, solution.getH3OMolecule(), FORMAT );
    }
    
    // MOH -> M+ + OH-
    private void updateStrongBaseMolecules( AqueousSolution solution ) {
        setAllVisible( true );
        setMolecule( 0, solution.getSolute(), FORMAT, true /* negligibleEnabled */ );
        setMolecule( 1, solution.getProduct(), FORMAT );
        setMolecule( 2, solution.getOHMolecule(), FORMAT );
        setVisible( 3, false );
    }
    
    // B + H2O <-> BH+ + OH-
    private void updateWeakBaseMolecules( AqueousSolution solution ) {
        setAllVisible( true );
        setMolecule( 0, solution.getSolute(), FORMAT );
        setMolecule( 1, solution.getWaterMolecule(), H2O_FORMAT );
        setMolecule( 2, solution.getProduct(), FORMAT );
        setMolecule( 3, solution.getOHMolecule(), FORMAT );
    }
    
    /*
     * Updates the concentration values for all bars.
     */
    private void updateValues() {
        AqueousSolution solution = graph.getSolution();
        if ( solution instanceof PureWaterSolution ) {
            updateWaterValues( solution );
        }
        else if ( solution instanceof AcidSolution ) {
            updateAcidValues( solution );
        }
        else if ( solution instanceof StrongBaseSolution ) {
            updateStrongBaseValues( solution );
        }
        else if ( solution instanceof WeakBaseSolution ) {
            updateWeakBaseValues( solution );
        }
        else {
            throw new IllegalStateException( "unsupported solution type: " + solution.getClass().getName() );
        }
    }
    
    // 2H2O <-> H3O+ + OH-
    private void updateWaterValues( AqueousSolution solution ) {
        setConcentration( 0, solution.getH2OConcentration() );
        setConcentration( 1, solution.getH3OConcentration() );
        setConcentration( 2, solution.getOHConcentration() );
    }
    
    // HA + H2O <-> A- + H3O+  (strong)
    // HA + H2O -> A- + H3O+   (weak)
    private void updateAcidValues( AqueousSolution solution ) {
        setConcentration( 0, solution.getSoluteConcentration() );
        setConcentration( 1, solution.getH2OConcentration() );
        setConcentration( 2, solution.getProductConcentration() ); 
        setConcentration( 3, solution.getH3OConcentration() ); 
    }
    
    // MOH -> M+ + OH-
    private void updateStrongBaseValues( AqueousSolution solution ) {
        setConcentration( 0, solution.getSoluteConcentration() );
        setConcentration( 1, solution.getProductConcentration() );
        setConcentration( 2, solution.getOHConcentration() );
    }
    
    // B + H2O <-> BH+ + OH-
    private void updateWeakBaseValues( AqueousSolution solution ) {
        setConcentration( 0, solution.getSoluteConcentration() );
        setConcentration( 1, solution.getH2OConcentration() );
        setConcentration( 2, solution.getProductConcentration() ); 
        setConcentration( 3, solution.getOHConcentration() );
    }
}
