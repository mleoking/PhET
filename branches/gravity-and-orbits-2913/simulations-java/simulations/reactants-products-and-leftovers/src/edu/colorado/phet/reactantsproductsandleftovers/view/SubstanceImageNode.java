// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view;

import edu.colorado.phet.reactantsproductsandleftovers.model.Substance;
import edu.colorado.phet.reactantsproductsandleftovers.model.Substance.SubstanceChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.model.Substance.SubstanceChangeListener;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Displays the image that corresponds to a substance.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SubstanceImageNode extends PImage implements IDynamicNode {

    private final Substance substance;
    private final SubstanceChangeListener listener;
    
    public SubstanceImageNode( Substance substance ) {
        super( substance.getImage() );
        this.substance = substance;
        listener = new SubstanceChangeAdapter() {
            @Override
            public void imageChanged() {
                update();
            }
        };
        substance.addSubstanceChangeListener( listener );
    }
    
    public Substance getSubstance() {
        return substance;
    }
    
    private void update() {
        setImage( substance.getImage() );
    }
    
    /**
     * Cleans up all listeners that could cause memory leaks.
     */
    public void cleanup() {
        substance.removeSubstanceChangeListener( listener );
    }
}
