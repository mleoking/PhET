package edu.colorado.phet.reactantsproductsandleftovers.model;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.colorado.phet.reactantsproductsandleftovers.view.SandwichImageFactory;

/**
 * Model of a sandwich, the product of our "reaction" analogy.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Sandwich extends Product {
    
    private final SandwichShopModel model;
    
    public Sandwich( int coefficient, int quantity, SandwichShopModel model ) {
        super( null, null, coefficient, quantity );
        this.model = model;
    }
    
    // This must be called after the model is fully initialized.
    public void init() {
      model.getReaction().addChangeListener( new ChangeListener() {
          public void stateChanged( ChangeEvent e ) {
              updateImage();
          }
      });
      updateImage();
    }
    
    private void updateImage() {
        setImage( SandwichImageFactory.createImage( model ) );
    }
}
