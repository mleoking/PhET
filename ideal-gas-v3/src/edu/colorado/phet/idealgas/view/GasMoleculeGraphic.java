/*
 * Class: GasMoleculeGraphic
 * Package: edu.colorado.phet.graphics.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Nov 4, 2002
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.idealgas.model.GasMolecule;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 */
public abstract class GasMoleculeGraphic extends PhetImageGraphic implements GasMolecule.Observer {
    private GasMolecule molecule;
    private ApparatusPanel apparatusPanel;
    private static Color s_color;

    public GasMoleculeGraphic( ApparatusPanel apparatusPanel, BufferedImage image, GasMolecule molecule ) {
        super( apparatusPanel, image );
        this.apparatusPanel = apparatusPanel;
        this.molecule = molecule;
        molecule.addObserver( this );
        update();

        super.setIgnoreMouse( true );
    }

    public void update() {
        super.setLocation( (int)( molecule.getCM().getX() - molecule.getRadius() ),
                           (int)( molecule.getCM().getY() - molecule.getRadius() ) );
    }

    public void removedFromSystem() {
        apparatusPanel.removeGraphic( this );
    }

    public static Color getColor() {
        return s_color;
    }

    public static void setColor( Color color ) {
        s_color = color;
    }
}
