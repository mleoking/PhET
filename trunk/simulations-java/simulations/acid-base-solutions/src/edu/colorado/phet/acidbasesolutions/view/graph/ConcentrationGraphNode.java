/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.graph;

import edu.colorado.phet.acidbasesolutions.model.*;
import edu.colorado.phet.acidbasesolutions.model.ABSModel.ModelChangeAdapter;
import edu.colorado.phet.acidbasesolutions.model.ABSModelElement.ModelElementChangeAdapter;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.AqueousSolutionChangeListener;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;

/**
 * Graph that depicts the concentrations related to a solution.
 * Updates itself based on changes in the solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationGraphNode extends AbstractConcentrationGraphNode {

    private final AqueousSolutionChangeListener solutionChangeListener;
    private AqueousSolution solution;
    
    public ConcentrationGraphNode( final ABSModel model ) {
        super( model.getConcentrationGraph().getSizeReference() );
        
        // not interactive
        setPickable( false );
        
        // model changes
        model.addModelChangeListener( new ModelChangeAdapter() {
            @Override
            public void solutionChanged() {
                setSolution( model.getSolution() );
            }
        });
        
        // solution changes
        solutionChangeListener = new AqueousSolutionChangeListener() {

            public void concentrationChanged() {
                update();
            }

            public void strengthChanged() {
                update();
            }
        };
        solution = model.getSolution();
        solution.addAqueousSolutionChangeListener( solutionChangeListener );
        
        // graph listener
        model.getConcentrationGraph().addModelElementChangeListener( new ModelElementChangeAdapter() {
            @Override
            public void visibilityChanged() {
                setVisible( model.getConcentrationGraph().isVisible() );
            }
        });
        
        double x = model.getConcentrationGraph().getLocationReference().getX() - ( getFullBoundsReference().getWidth() / 2 ) - PNodeLayoutUtils.getOriginXOffset( this );
        double y = model.getConcentrationGraph().getLocationReference().getY() - ( getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( this );
        setOffset( x, y );
        setVisible( model.getConcentrationGraph().isVisible() );
        update();
    }
    
    private void setSolution( AqueousSolution solution ) {
        if ( solution != this.solution ) {
            this.solution.removeAqueousSolutionChangeListener( solutionChangeListener );
            this.solution = solution;
            this.solution.addAqueousSolutionChangeListener( solutionChangeListener );
            update();
        }
    }
    
    private void update() {
        
        boolean isPureWater = ( solution instanceof PureWaterSolution );
        boolean isAcid = ( solution instanceof StrongAcidSolution || solution instanceof WeakAcidSolution );
        boolean isStrong = ( solution instanceof StrongAcidSolution || solution instanceof StrongBaseSolution );
        
        // hide solute and product bars for pure water
        setSoluteVisible( !isPureWater );
        setProductVisible( !isPureWater );
        
        // labels
        if ( !isPureWater ) {
            setSoluteLabel( solution.getSolute().getSymbol() );
            setProductLabel( solution.getProduct().getSymbol() );

            // molecule representations
            setSoluteMolecule( solution.getSolute().getSymbol(), solution.getSolute().getIcon(), solution.getSolute().getColor() );
            setProductMolecule( solution.getProduct().getSymbol(), solution.getProduct().getIcon(), solution.getProduct().getColor() );

            // "negligible" counts
            setSoluteNegligibleEnabled( isStrong );
        }
        
        // counts
        setSoluteConcentration( solution.getSoluteConcentration() );
        setProductConcentration( solution.getProductConcentration() );
        setH3OConcentration( solution.getH3OConcentration() );
        setOHConcentration( solution.getOHConcentration() );
        setH2OConcentration( solution.getH2OConcentration() );
    }
}
