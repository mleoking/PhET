
package edu.colorado.phet.acidbasesolutions.view.moleculecounts;

import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.model.Acid;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.NoSolute;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.Acid.CustomAcid;
import edu.colorado.phet.acidbasesolutions.model.Acid.StrongAcid;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.model.Base.CustomBase;
import edu.colorado.phet.acidbasesolutions.model.Base.StrongBase;
import edu.colorado.phet.acidbasesolutions.model.Base.WeakBase;
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
            updateIconsAndLabels();
            updateCounts();
        }
        
        public void soluteChanged() {
            updateIconsAndLabels();
            updateCounts();
        }
        
        public void concentrationChanged() {
            updateCounts();
        }

        public void strengthChanged() {
            updateCounts();
        }
        
        private void updateCounts() {
            EquilibriumModel equilibriumModel = solution.getEquilibriumModel();
            countsNode.setReactantCount( equilibriumModel.getReactantMoleculeCount() );
            countsNode.setProductCount( equilibriumModel.getProductMoleculeCount() );
            countsNode.setH3OCount( equilibriumModel.getH3OMoleculeCount() );
            countsNode.setOHCount( equilibriumModel.getOHMoleculeCount() );
            countsNode.setH2OCount( equilibriumModel.getH2OMoleculeCount() );
        }
        
        private void updateIconsAndLabels() {
            
            Solute solute = solution.getSolute();
            
            // hide reactant and product counts for pure water
            boolean pureWater = solute instanceof NoSolute; //XXX get rid of this
            countsNode.setReactantVisible( !pureWater );
            countsNode.setProductVisible( !pureWater );
            
            // icons
            //XXX get rid of this block
            if ( solute instanceof NoSolute ) {
                // do nothing, icons not visible
            }
            else if ( solute instanceof Acid ) {
                countsNode.setReactantIcon( ABSImages.HA_MOLECULE );
                countsNode.setProductIcon( ABSImages.A_MINUS_MOLECULE );
            }
            else if ( solute instanceof WeakBase ) {
                countsNode.setReactantIcon( ABSImages.B_MOLECULE );
                countsNode.setProductIcon( ABSImages.BH_PLUS_MOLECULE );
            }
            else if ( solute instanceof StrongBase ) {
                countsNode.setReactantIcon( ABSImages.MOH_MOLECULE );
                countsNode.setProductIcon( ABSImages.M_PLUS_MOLECULE );
            }
            else if ( solute instanceof CustomBase ) {
                CustomBase customBase = (CustomBase) solute;
                if ( customBase.isStrong() ) {
                    countsNode.setReactantIcon( ABSImages.MOH_MOLECULE );
                    countsNode.setProductIcon( ABSImages.M_PLUS_MOLECULE );
                }
                else {
                    countsNode.setReactantIcon( ABSImages.B_MOLECULE );
                    countsNode.setProductIcon( ABSImages.BH_PLUS_MOLECULE );
                }
            }
            else {
                throw new IllegalStateException( "unexpected solute type: " + solute.getClass().getName() );
            }
            
            // "negligible" counts
            boolean negligibleEnabled = ( solute instanceof StrongAcid || solute instanceof CustomAcid ); //XXX get rid of this
            countsNode.setReactantNegligibleEnabled( negligibleEnabled );
            
            // labels
            countsNode.setReactantLabel( solution.getSolute().getSymbol() );
            //XXX get rid of this block
            if ( solute instanceof NoSolute ) {
                // do nothing, product not visible
            }
            else if ( solute instanceof Acid ) {
                countsNode.setProductLabel( ((Acid)solution.getSolute()).getConjugateSymbol() );
            }
            else if ( solute instanceof WeakBase ) {
                countsNode.setProductLabel( ((WeakBase)solution.getSolute()).getConjugateSymbol() );
            }
            else if ( solute instanceof StrongBase ) {
                countsNode.setProductLabel( ((StrongBase)solution.getSolute()).getMetalSymbol() );
            }
            else if ( solute instanceof CustomBase ) {
                CustomBase customBase = (CustomBase) solute;
                if ( customBase.isStrong() ) {
                    countsNode.setProductLabel( customBase.getMetalSymbol() );
                }
                else {
                    countsNode.setProductLabel( customBase.getConjugateSymbol() );
                }
            }
            else {
                throw new IllegalStateException( "unexpected solute type: " + solute.getClass().getName() );
            }
        }
    }
}
