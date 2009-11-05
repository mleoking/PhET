package edu.colorado.phet.reactantsproductsandleftovers.view;

import edu.colorado.phet.reactantsproductsandleftovers.model.Substance;
import edu.colorado.phet.reactantsproductsandleftovers.model.Substance.SubstanceChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.model.Substance.SubstanceChangeListener;
import edu.umd.cs.piccolo.nodes.PImage;


public class SubstanceImageNode extends PImage {

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
