
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.view.moleculecounts;

import edu.colorado.phet.advancedacidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.advancedacidbasesolutions.model.Solute;
import edu.colorado.phet.advancedacidbasesolutions.model.AqueousSolution.SolutionListener;

/**
 * 
 * MoleculeCountsNode depicts the molecule counts in a solution.
 * Updates itself based on changes in the solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MoleculeCountsNode extends AbstractMoleculeCountsNode {

    public MoleculeCountsNode( AqueousSolution solution ) {
        super();
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
        private final MoleculeCountsNode countsNode;
        
        public ModelViewController( AqueousSolution solution, MoleculeCountsNode countsNode ) {
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
            
            // hide reactant and product counts for pure water
            countsNode.setReactantVisible( !solution.isPureWater() );
            countsNode.setProductVisible( !solution.isPureWater() );
            
            // labels
            Solute solute = solution.getSolute();
            countsNode.setReactantLabel( solute.getSymbol() );
            countsNode.setProductLabel( solute.getConjugateSymbol() );
            
            // icons
            countsNode.setReactantIcon( solute.getIcon() );
            countsNode.setProductIcon( solute.getConjugateIcon() );
            
            // "negligible" counts
            countsNode.setReactantNegligibleEnabled( solute.isZeroNegligible() );
            
            // counts
            countsNode.setReactantCount( solution.getReactantMoleculeCount() );
            countsNode.setProductCount( solution.getProductMoleculeCount() );
            countsNode.setH3OCount( solution.getH3OMoleculeCount() );
            countsNode.setOHCount( solution.getOHMoleculeCount() );
            countsNode.setH2OCount( solution.getH2OMoleculeCount() );
        }
    }
}
