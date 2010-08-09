/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.graph;

import java.text.DecimalFormat;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.acidbasesolutions.constants.ABSImages;
import edu.colorado.phet.acidbasesolutions.constants.ABSSymbols;
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
    
    private void updateMolecules() {
        AqueousSolution solution = graph.getSolution();
        if ( solution instanceof PureWaterSolution ) {
            updateWaterMolecules();
        }
        else if ( solution instanceof AcidSolution ) {
            updateAcidMolecules( solution instanceof StrongAcidSolution );
        }
        else if ( solution instanceof StrongBaseSolution ) {
            updateStrongBaseMolecules();
        }
        else if ( solution instanceof WeakBaseSolution ) {
            updateWeakBaseMolecules();
        }
        else {
            throw new IllegalStateException( "unsupported solution type: " + solution.getClass().getName() );
        }
    }
    
    // 2H2O <-> H3O+ + OH-
    private void updateWaterMolecules() {
        setAllVisible( true );
        setMolecule( 0, ABSSymbols.H2O, ABSImages.H2O_MOLECULE, ABSColors.H2O, H2O_FORMAT );
        setMolecule( 1, ABSSymbols.H3O_PLUS, ABSImages.H3O_PLUS_MOLECULE, ABSColors.H3O_PLUS, FORMAT );
        setMolecule( 2, ABSSymbols.OH_MINUS, ABSImages.OH_MINUS_MOLECULE, ABSColors.OH_MINUS, FORMAT );
        setVisible( 3, false );
    }
    
    // HA + H2O <-> A- + H3O+  (strong)
    // HA + H2O -> A- + H3O+   (weak)
    private void updateAcidMolecules( boolean isStrong ) {
        setAllVisible( true );
        setMolecule( 0, ABSSymbols.HA, ABSImages.HA_MOLECULE, ABSColors.HA, FORMAT, isStrong /* negligibleEnabled */ );
        setMolecule( 1, ABSSymbols.H2O, ABSImages.H2O_MOLECULE, ABSColors.H2O, H2O_FORMAT );
        setMolecule( 2, ABSSymbols.A_MINUS, ABSImages.A_MINUS_MOLECULE, ABSColors.A_MINUS, FORMAT );
        setMolecule( 3, ABSSymbols.H3O_PLUS, ABSImages.H3O_PLUS_MOLECULE, ABSColors.H3O_PLUS, FORMAT );
    }
    
    // MOH -> M+ + OH-
    private void updateStrongBaseMolecules() {
        setAllVisible( true );
        setMolecule( 0, ABSSymbols.MOH, ABSImages.MOH_MOLECULE, ABSColors.MOH, FORMAT, true /* negligibleEnabled */ );
        setMolecule( 1, ABSSymbols.M_PLUS, ABSImages.M_PLUS_MOLECULE, ABSColors.M_PLUS, FORMAT );
        setMolecule( 2, ABSSymbols.OH_MINUS, ABSImages.OH_MINUS_MOLECULE, ABSColors.OH_MINUS, FORMAT );
        setVisible( 3, false );
    }
    
    // B + H2O <-> BH+ + OH-
    private void updateWeakBaseMolecules() {
        setAllVisible( true );
        setMolecule( 0, ABSSymbols.B, ABSImages.B_MOLECULE, ABSColors.B, FORMAT );
        setMolecule( 1, ABSSymbols.H2O, ABSImages.H2O_MOLECULE, ABSColors.H2O, H2O_FORMAT );
        setMolecule( 2, ABSSymbols.BH_PLUS, ABSImages.BH_PLUS_MOLECULE, ABSColors.BH_PLUS, FORMAT );
        setMolecule( 3, ABSSymbols.OH_MINUS, ABSImages.OH_MINUS_MOLECULE, ABSColors.OH_MINUS, FORMAT );
    }
    
    private void updateValues() {
        AqueousSolution solution = graph.getSolution();
        if ( solution instanceof PureWaterSolution ) {
            updateWaterValues();
        }
        else if ( solution instanceof AcidSolution ) {
            updateAcidValues();
        }
        else if ( solution instanceof StrongBaseSolution ) {
            updateStrongBaseValues();
        }
        else if ( solution instanceof WeakBaseSolution ) {
            updateWeakBaseValues();
        }
        else {
            throw new IllegalStateException( "unsupported solution type: " + solution.getClass().getName() );
        }
    }
    
    // 2H2O <-> H3O+ + OH-
    private void updateWaterValues() {
        AqueousSolution solution = graph.getSolution();
        setConcentration( 0, solution.getH2OConcentration() );
        setConcentration( 1, solution.getH3OConcentration() );
        setConcentration( 2, solution.getOHConcentration() );
    }
    
    // HA + H2O <-> A- + H3O+  (strong)
    // HA + H2O -> A- + H3O+   (weak)
    private void updateAcidValues() {
        AqueousSolution solution = graph.getSolution();
        setConcentration( 0, solution.getSoluteConcentration() );
        setConcentration( 1, solution.getH2OConcentration() );
        setConcentration( 2, solution.getProductConcentration() ); 
        setConcentration( 3, solution.getH3OConcentration() ); 
    }
    
    // MOH -> M+ + OH-
    private void updateStrongBaseValues() {
        AqueousSolution solution = graph.getSolution();
        setConcentration( 0, solution.getSoluteConcentration() );
        setConcentration( 1, solution.getProductConcentration() );
        setConcentration( 2, solution.getOHConcentration() );
    }
    
    // B + H2O <-> BH+ + OH-
    private void updateWeakBaseValues() {
        AqueousSolution solution = graph.getSolution();
        setConcentration( 0, solution.getSoluteConcentration() );
        setConcentration( 1, solution.getH2OConcentration() );
        setConcentration( 2, solution.getProductConcentration() ); 
        setConcentration( 3, solution.getOHConcentration() );
    }
}
