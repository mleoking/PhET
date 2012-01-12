
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.view.graph;

import edu.colorado.phet.advancedacidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.advancedacidbasesolutions.model.Solute;
import edu.colorado.phet.advancedacidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Graph that depicts the concentrations related to a solution.
 * Updates itself based on changes in the solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationGraphNode extends AbstractConcentrationGraphNode {

    public ConcentrationGraphNode( PDimension outlineSize, AqueousSolution solution ) {
        super( outlineSize );
        // not interactive
        setPickable( false );
        setChildrenPickable( false );
        // listen to the model
        solution.addSolutionListener( new ModelViewController( solution, this ) );
    }
    
    /*
     * Update the view to match the model.
     */
    private static class ModelViewController implements SolutionListener {

        private final AqueousSolution solution;
        private final ConcentrationGraphNode countsNode;
        
        public ModelViewController( AqueousSolution solution, ConcentrationGraphNode countsNode ) {
            this.solution = solution;
            this.countsNode = countsNode;
            updateView();
        }
        
        public void soluteChanged() {
            updateView();
        }
        
        public void concentrationChanged() {
            updateView();
        }

        public void strengthChanged() {
            updateView();
        }
        
        private void updateView() {
            
            // hide reactant and product bars for pure water
            countsNode.setReactantVisible( !solution.isPureWater() );
            countsNode.setProductVisible( !solution.isPureWater() );
            
            // labels
            Solute solute = solution.getSolute();
            countsNode.setReactantLabel( solute.getSymbol() );
            countsNode.setProductLabel( solute.getConjugateSymbol() );
            
            // molecule representations
            countsNode.setReactantMolecule( solute.getSymbol(), solute.getIcon(), solute.getColor() );
            countsNode.setProductMolecule( solute.getConjugateSymbol(), solute.getConjugateIcon(), solute.getConjugateColor() );
            
            // "negligible" counts
            countsNode.setReactantNegligibleEnabled( solute.isZeroNegligible() );
            
            // counts
            countsNode.setReactantConcentration( solution.getReactantConcentration() );
            countsNode.setProductConcentration( solution.getProductConcentration() );
            countsNode.setH3OConcentration( solution.getH3OConcentration() );
            countsNode.setOHConcentration( solution.getOHConcentration() );
            countsNode.setH2OConcentration( solution.getH2OConcentration() );
        }
    }
}
