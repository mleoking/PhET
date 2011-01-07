// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeListener;
import edu.colorado.phet.reactantsproductsandleftovers.view.IDynamicNode;

/**
 * Displays the leftovers value for a Reactant.
 * By default, the value is not editable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LeftoversValueNode extends ValueNode implements IDynamicNode {
    
    private Reactant reactant;
    private final ReactantChangeListener reactantChangeListener;
    
    public LeftoversValueNode( Reactant reactant, IntegerRange range, double imageScale, boolean showName ) {
        super( range, reactant.getLeftovers(), reactant.getImage(), imageScale, reactant.getName(), showName, false /* editable */ );
        
        this.reactant = reactant;
        
        // update this control when the model changes
        reactantChangeListener = new ReactantChangeAdapter() {
            @Override
            public void leftoversChanged() {
                updateLeftovers();
            }
            
            @Override
            public void imageChanged() {
                updateImage();
            }
        };
        reactant.addReactantChangeListener( reactantChangeListener );
        
        // update the model when this control changes
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateModel();
            }
        });
    }
    
    public void setReactant( Reactant reactant ) {
        if ( reactant != this.reactant ) {
            this.reactant.removeSubstanceChangeListener( reactantChangeListener );
            this.reactant = reactant;
            updateLeftovers();
            updateImage();
            this.reactant.addSubstanceChangeListener( reactantChangeListener );
        }
    }
    
    public void cleanup() {
        reactant.removeReactantChangeListener( reactantChangeListener );
    }
    
    private void updateLeftovers() {
        setValue( reactant.getLeftovers() );
    }
    
    private void updateImage() {
        setImage( reactant.getImage() );
    }
    
    private void updateModel() {
        reactant.setLeftovers( getValue() );
    }
}
