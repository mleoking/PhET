/**
 * Class: WaterMoleculeGraphic
 * Package: edu.colorado.phet.microwave.view
 * Author: Another Guy
 * Date: Aug 19, 2003
 */
package edu.colorado.phet.microwave.view;

import edu.colorado.phet.common.view.graphics.ModelViewTransform2D;
import edu.colorado.phet.coreadditions.TxObservingGraphic;
import edu.colorado.phet.microwave.model.WaterMolecule;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.Observable;

/**
 * ObservingGraphic for WaterMolecules.
 * <p>
 * The class uses a the Flyweight pattern to achieve acceptable performance.
 * The class keeps a static cache of images at different orientations so that
 * new BufferedImages don't have to be allocated for each WaterMolecule in the
 * model, and they do not have to be rotated for each time a WaterMolecule turns.
 */
public class WaterMoleculeGraphic extends TxObservingGraphic implements ImageObserver {

    private WaterMolecule molecule;
    private Point oxygenCenter = new Point();
    private Point hydrogenCenter1 = new Point();
    private Point hydrogenCenter2 = new Point();
    private int viewOxygenRad;
    private int viewHydrogenRad;

    public WaterMoleculeGraphic( WaterMolecule molecule, ModelViewTransform2D tx ) {
        super( tx );
        this.molecule = molecule;
        molecule.addObserver( this );
        update( null, null );
    }

    public void paint( Graphics2D g ) {
        if( hydrogenCenter1 != null ) {
            g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g.setStroke( moleculeStroke );
            g.setColor( Color.RED );
            g.fillOval( hydrogenCenter1.x - (int)WaterMolecule.s_hydrogenRadius,
                        hydrogenCenter1.y - (int)WaterMolecule.s_hydrogenRadius,
                        (int)WaterMolecule.s_hydrogenRadius * 2,
                        (int)WaterMolecule.s_hydrogenRadius * 2 );
            g.setColor( Color.BLACK );
            g.drawOval( hydrogenCenter1.x - (int)WaterMolecule.s_hydrogenRadius,
                        hydrogenCenter1.y - (int)WaterMolecule.s_hydrogenRadius,
                        (int)WaterMolecule.s_hydrogenRadius * 2,
                        (int)WaterMolecule.s_hydrogenRadius * 2 );
            g.setColor( Color.RED );
            g.fillOval( hydrogenCenter2.x - (int)WaterMolecule.s_hydrogenRadius,
                        hydrogenCenter2.y - (int)WaterMolecule.s_hydrogenRadius,
                        (int)WaterMolecule.s_hydrogenRadius * 2,
                        (int)WaterMolecule.s_hydrogenRadius * 2 );
            g.setColor( Color.BLACK );
            g.drawOval( hydrogenCenter2.x - (int)WaterMolecule.s_hydrogenRadius,
                        hydrogenCenter2.y - (int)WaterMolecule.s_hydrogenRadius,
                        (int)WaterMolecule.s_hydrogenRadius * 2,
                        (int)WaterMolecule.s_hydrogenRadius * 2 );
            g.setColor( Color.BLUE );
            g.fillOval( oxygenCenter.x - (int)WaterMolecule.s_oxygenRadius,
                        oxygenCenter.y - (int)WaterMolecule.s_oxygenRadius,
                        (int)WaterMolecule.s_oxygenRadius * 2,
                        (int)WaterMolecule.s_oxygenRadius * 2 );
            g.setColor( Color.BLACK );
            g.drawOval( oxygenCenter.x - (int)WaterMolecule.s_oxygenRadius,
                        oxygenCenter.y - (int)WaterMolecule.s_oxygenRadius,
                        (int)WaterMolecule.s_oxygenRadius * 2,
                        (int)WaterMolecule.s_oxygenRadius * 2 );
        }
    }

    public void update( Observable o, Object arg ) {
        if( o instanceof WaterMolecule ) {
            oxygenCenter.setLocation( molecule.getLobes()[0].getCenter() );
            hydrogenCenter1.setLocation( molecule.getLobes()[1].getCenter() );
            hydrogenCenter2.setLocation( molecule.getLobes()[2].getCenter() );
        }
    }

    public boolean imageUpdate( Image img, int infoflags,
                                int x, int y, int width, int height ) {
        return false;
    }

    //
    // Statics
    //
    private Stroke moleculeStroke = new BasicStroke( 1.0f );
}
