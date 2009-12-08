package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.reactantsproductsandleftovers.model.Substance;
import edu.colorado.phet.reactantsproductsandleftovers.model.Substance.SubstanceChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.model.Substance.SubstanceChangeListener;

/**
 * Displays the quantity value for a Substance.
 * By default, the value is not editable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class QuantityValueNode extends ValueNode {
    
    private final Substance substance;
    private final SubstanceChangeListener substanceChangeListener;
    
    public QuantityValueNode( final Substance substance, IntegerRange range, double imageScale, boolean showName ) {
        super( range, substance.getQuantity(), substance.getImage(), imageScale, substance.getName(), false /* editable */ );
        
        this.substance = substance;

        // update this control when the model changes
        substanceChangeListener = new SubstanceChangeAdapter() {
            @Override
            public void quantityChanged() {
                setValue( substance.getQuantity() );
            }
            
            @Override
            public void imageChanged() {
                setImage( substance.getImage() );
            }
        };
        substance.addSubstanceChangeListener( substanceChangeListener );

        // update the model when this control changes
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                substance.setQuantity( getValue() );
            }
        });
    }
    
    public void cleanup() {
        substance.removeSubstanceChangeListener( substanceChangeListener );
    }
}
