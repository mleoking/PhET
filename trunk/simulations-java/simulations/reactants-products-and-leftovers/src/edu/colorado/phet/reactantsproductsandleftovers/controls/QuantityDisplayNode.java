package edu.colorado.phet.reactantsproductsandleftovers.controls;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.reactantsproductsandleftovers.model.Substance;
import edu.colorado.phet.reactantsproductsandleftovers.model.Substance.SubstanceChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.model.Substance.SubstanceChangeListener;


public class QuantityDisplayNode extends IntegerDisplayNode {
    
    private final Substance substance;
    private final SubstanceChangeListener substanceChangeListener;
    
    public QuantityDisplayNode( final Substance substance, IntegerRange range, double imageScale ) {
        super( substance, range, imageScale );
        
        this.substance = substance;
        
        substanceChangeListener = new SubstanceChangeAdapter() {
            @Override
            public void quantityChanged() {
                setValue( substance.getQuantity() );
            }
        };
        substance.addSubstanceChangeListener( substanceChangeListener );
    }
    
    public void cleanup() {
        super.cleanup();
        substance.removeSubstanceChangeListener( substanceChangeListener );
    }
}
