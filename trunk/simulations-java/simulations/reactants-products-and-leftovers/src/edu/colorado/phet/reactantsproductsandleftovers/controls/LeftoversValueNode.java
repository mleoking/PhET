package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeListener;

/**
 * Displays the leftovers value for a Reactant.
 * By default, the value is not editable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LeftoversValueNode extends ValueNode {
    
    private final Reactant reactant;
    private final ReactantChangeListener reactantChangeListener;
    
    public LeftoversValueNode( final Reactant reactant, IntegerRange range, double imageScale, boolean showName ) {
        super( range, reactant.getLeftovers(), reactant.getImage(), imageScale, reactant.getName(), false /* editable */ );
        
        this.reactant = reactant;
        
        // update this control when the model changes
        reactantChangeListener = new ReactantChangeAdapter() {
            @Override
            public void leftoversChanged() {
                setValue( reactant.getLeftovers() );
            }
            
            @Override
            public void imageChanged() {
                setImage( reactant.getImage() );
            }
        };
        reactant.addReactantChangeListener( reactantChangeListener );
        
        // update the model when this control changes
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                reactant.setLeftovers( getValue() );
            }
        });
    }
    
    public void cleanup() {
        reactant.removeReactantChangeListener( reactantChangeListener );
    }
}
