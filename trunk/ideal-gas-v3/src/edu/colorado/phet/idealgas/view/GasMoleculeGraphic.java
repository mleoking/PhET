/*
 * Class: GasMoleculeGraphic
 * Package: edu.colorado.phet.graphics.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Nov 4, 2002
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.idealgas.model.GasMolecule;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 */
public abstract class GasMoleculeGraphic extends PhetImageGraphic implements SimpleObserver {
    private GasMolecule molecule;

    public GasMoleculeGraphic( Component component, BufferedImage image, GasMolecule molecule ) {
        super( component, image );
        this.molecule = molecule;
        molecule.addObserver( this );
        update();
    }

    //    public GasMoleculeGraphic( Component component, BufferedImage image, int x, int y ) {
    //        super( component, image, x, y );
    //    }
    //
    //    public GasMoleculeGraphic( Component component, BufferedImage image, AffineTransform transform ) {
    //        super( component, image, transform );
    //    }
    //

    public void update() {
        super.setPositionCentered( (int)molecule.getCM().getX(), (int)molecule.getCM().getY() );
    }
}
