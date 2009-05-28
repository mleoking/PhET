
package edu.colorado.phet.acidbasesolutions.view.graph;

import edu.colorado.phet.acidbasesolutions.model.Acid;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.model.Base.CustomBase;
import edu.colorado.phet.acidbasesolutions.model.Base.StrongBase;
import edu.colorado.phet.acidbasesolutions.model.Base.WeakBase;
import edu.colorado.phet.acidbasesolutions.model.equilibrium.*;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;


public class ConcentrationGraphNode extends PComposite {

    private final NoSoluteConcentrationGraphNode waterNode;
    private final AcidConcentrationGraphNode acidNode;
    private final WeakBaseConcentrationGraphNode weakBaseNode;
    private final StrongBaseConcentrationGraphNode strongBaseNode;
    
    public ConcentrationGraphNode( PDimension outlineSize, AqueousSolution solution ) {
        this( outlineSize );
        solution.addSolutionListener( new ModelViewController( solution, this ) );
    }
    
    public ConcentrationGraphNode( PDimension outlineSize ) {
        super();
        
        // not interactive
        setPickable( false );
        setChildrenPickable( false );
        
        waterNode = new NoSoluteConcentrationGraphNode( outlineSize );
        addChild( waterNode );
        
        acidNode = new AcidConcentrationGraphNode( outlineSize );
        addChild( acidNode );
        
        weakBaseNode = new WeakBaseConcentrationGraphNode( outlineSize );
        addChild( weakBaseNode );
        
        strongBaseNode = new StrongBaseConcentrationGraphNode( outlineSize );
        addChild( strongBaseNode );
    }
    
    protected NoSoluteConcentrationGraphNode getWaterNode() {
        return waterNode;
    }
    
    protected AcidConcentrationGraphNode getAcidNode() {
        return acidNode;
    }
    
    protected WeakBaseConcentrationGraphNode getWeakBaseNode() {
        return weakBaseNode;
    }
    
    protected StrongBaseConcentrationGraphNode getStrongBaseNode() {
        return strongBaseNode;
    }
    
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
            
            EquilibriumModel equilibriumModel = solution.getEquilibriumModel();
            
            // visibility
            countsNode.getWaterNode().setVisible( equilibriumModel instanceof PureWaterEquilibriumModel );
            countsNode.getAcidNode().setVisible( equilibriumModel instanceof WeakAcidEquilibriumModel || equilibriumModel instanceof IntermediateAcidEquilibriumModel || equilibriumModel instanceof StrongAcidEquilibriumModel );
            countsNode.getWeakBaseNode().setVisible( equilibriumModel instanceof WeakBaseEquilibriumModel || equilibriumModel instanceof IntermediateBaseEquilibriumModel );
            countsNode.getStrongBaseNode().setVisible( equilibriumModel instanceof StrongBaseEquilibriumModel );
            
            // counts & labels
            if ( equilibriumModel instanceof PureWaterEquilibriumModel ) {
                NoSoluteConcentrationGraphNode node = countsNode.getWaterNode();
                node.setH3OConcentration( equilibriumModel.getH3OConcentration() );
                node.setOHConcentration( equilibriumModel.getOHConcentration() );
                node.setH2OConcentration( equilibriumModel.getH2OConcentration() );
            }
            else if ( equilibriumModel instanceof WeakAcidEquilibriumModel || equilibriumModel instanceof IntermediateAcidEquilibriumModel || equilibriumModel instanceof StrongAcidEquilibriumModel ) {
                AcidConcentrationGraphNode node = countsNode.getAcidNode();
                // counts
                node.setReactantConcentration( equilibriumModel.getReactantConcentration() );
                node.setProductConcentration( equilibriumModel.getProductConcentration() );
                node.setH3OConcentration( equilibriumModel.getH3OConcentration() );
                node.setOHConcentration( equilibriumModel.getOHConcentration() );
                node.setH2OConcentration( equilibriumModel.getH2OConcentration() );
                // labels
                node.setReactantLabel( solution.getSolute().getSymbol() );
                Solute solute = solution.getSolute();
                if ( solute instanceof Acid ) {
                    node.setProductLabel( ((Acid)solution.getSolute()).getConjugateSymbol() );
                }
                else {
                    throw new IllegalStateException( "unexpected solute type: " + solute.getClass().getName() );
                }
            }
            else if ( equilibriumModel instanceof WeakBaseEquilibriumModel || equilibriumModel instanceof IntermediateBaseEquilibriumModel ) {
                WeakBaseConcentrationGraphNode node = countsNode.getWeakBaseNode();
                // counts
                node.setReactantConcentration( equilibriumModel.getReactantConcentration() );
                node.setProductConcentration( equilibriumModel.getProductConcentration() );
                node.setH3OConcentration( equilibriumModel.getH3OConcentration() );
                node.setOHConcentration( equilibriumModel.getOHConcentration() );
                node.setH2OConcentration( equilibriumModel.getH2OConcentration() );
                // labels
                node.setReactantLabel( solution.getSolute().getSymbol() );
                Solute solute = solution.getSolute();
                if ( solute instanceof WeakBase ) {
                    node.setProductLabel( ((WeakBase)solution.getSolute()).getConjugateSymbol() );
                }
                else if ( solute instanceof CustomBase ) {
                    node.setProductLabel( ((CustomBase)solution.getSolute()).getConjugateSymbol() );
                }
                else {
                    throw new IllegalStateException( "unexpected solute type: " + solute.getClass().getName() );
                }
            }
            else if ( equilibriumModel instanceof StrongBaseEquilibriumModel ) {
                StrongBaseConcentrationGraphNode node = countsNode.getStrongBaseNode();
                // counts
                node.setReactantConcentration( equilibriumModel.getReactantConcentration() );
                node.setProductConcentration( equilibriumModel.getProductConcentration() );
                node.setH3OConcentration( equilibriumModel.getH3OConcentration() );
                node.setOHConcentration( equilibriumModel.getOHConcentration() );
                node.setH2OConcentration( equilibriumModel.getH2OConcentration() );
                // labels
                node.setReactantLabel( solution.getSolute().getSymbol() );
                Solute solute = solution.getSolute();
                if ( solute instanceof StrongBase ) {
                    node.setProductLabel( ((StrongBase)solution.getSolute()).getConjugateSymbol() );
                }
                else if ( solute instanceof CustomBase ) {
                    node.setProductLabel( ((CustomBase)solution.getSolute()).getConjugateSymbol() );
                }
                else {
                    throw new IllegalStateException( "unexpected solute type: " + solute.getClass().getName() );
                }
            }
            else { 
                throw new UnsupportedOperationException( "unsupported concentration model type: " + equilibriumModel.getClass().getName() );
            }
        }
    }
}
