
package edu.colorado.phet.acidbasesolutions.view.moleculecounts;

import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.model.equilibrium.EquilibriumModel;


public class MoleculeCountsNode extends AbstractMoleculeCountsNode {

    public MoleculeCountsNode( AqueousSolution solution ) {
        this();
        solution.addSolutionListener( new ModelViewController( solution, this ) );
    }
    
    public MoleculeCountsNode() {
        super();
        // not interactive
        setPickable( false );
        setChildrenPickable( false );
    }
    
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
            EquilibriumModel equilibriumModel = solution.getEquilibriumModel();
            countsNode.setReactantCount( equilibriumModel.getReactantMoleculeCount() );
            countsNode.setProductCount( equilibriumModel.getProductMoleculeCount() );
            countsNode.setH3OCount( equilibriumModel.getH3OMoleculeCount() );
            countsNode.setOHCount( equilibriumModel.getOHMoleculeCount() );
            countsNode.setH2OCount( equilibriumModel.getH2OMoleculeCount() );
        }
    }
}
