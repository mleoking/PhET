// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.reactantsproductsandleftovers.model.Substance;
import edu.colorado.phet.reactantsproductsandleftovers.model.Substance.SubstanceChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.model.Substance.SubstanceChangeListener;
import edu.colorado.phet.reactantsproductsandleftovers.view.IDynamicNode;

/**
 * Displays the quantity value for a Substance.
 * By default, the value is not editable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class QuantityValueNode extends ValueNode implements IDynamicNode {
    
    private Substance substance;
    private final SubstanceChangeListener substanceChangeListener;
    
    public QuantityValueNode( Substance substance, IntegerRange range, double imageScale, boolean showName ) {
        super( range, substance.getQuantity(), substance.getImage(), imageScale, substance.getName(), showName, false /* editable */ );
        
        this.substance = substance;

        // update this control when the model changes
        substanceChangeListener = new SubstanceChangeAdapter() {
            @Override
            public void quantityChanged() {
                updateQuantity();
            }
            
            @Override
            public void imageChanged() {
                updateImage();
            }
        };
        substance.addSubstanceChangeListener( substanceChangeListener );

        // update the model when this control changes
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateModel();
            }
        });
    }
    
    public void setSubstance( Substance substance ) {
        if ( substance != this.substance ) {
            this.substance.removeSubstanceChangeListener( substanceChangeListener );
            this.substance = substance;
            updateQuantity();
            updateImage();
            this.substance.addSubstanceChangeListener( substanceChangeListener );
        }
    }
    
    public void cleanup() {
        substance.removeSubstanceChangeListener( substanceChangeListener );
    }
    
    private void updateQuantity() {
        setValue( substance.getQuantity() );
    }
    
    private void updateImage() {
        setImage( substance.getImage() );
    }
    
    private void updateModel() {
        substance.setQuantity( getValue() );
    }
}
