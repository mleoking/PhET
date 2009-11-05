package edu.colorado.phet.reactantsproductsandleftovers.controls;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeListener;


public class LeftoversDisplayNode extends SubstanceValueDisplayNode {
    
    private final Reactant reactant;
    private final ReactantChangeListener reactantChangeListener;
    
    public LeftoversDisplayNode( final Reactant reactant, IntegerRange range, double imageScale, boolean showName ) {
        super( reactant, range, imageScale, showName );
        
        this.reactant = reactant;
        
        reactantChangeListener = new ReactantChangeAdapter() {
            @Override
            public void leftoversChanged() {
                setValue( reactant.getLeftovers() );
            }
        };
        reactant.addReactantChangeListener( reactantChangeListener );
        
        setValue( reactant.getLeftovers() );
    }
    
    public void cleanup() {
        super.cleanup();
        reactant.removeReactantChangeListener( reactantChangeListener );
    }
}
