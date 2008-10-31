/**
 * Class: WaterMoleculeGraphic
 * Package: edu.colorado.phet.microwave.view
 * Author: Another Guy
 * Date: Aug 19, 2003
 */
package edu.colorado.phet.microwaves.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.ImageObserver;
import java.util.Observable;

import edu.colorado.phet.coreadditions_microwaves.graphics.ModelViewTransform2D;
import edu.colorado.phet.microwaves.coreadditions.TxObservingGraphic;
import edu.colorado.phet.microwaves.model.WaterMolecule;

/**
 * ObservingGraphic for WaterMolecules.
 * <p/>
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
    private Ellipse2D atomGraphic = new Ellipse2D.Double();

    public WaterMoleculeGraphic( WaterMolecule molecule, ModelViewTransform2D tx ) {
        super( tx );
        this.molecule = molecule;
        molecule.addObserver( this );
        update( null, null );
    }

    public void paint( Graphics2D g ) {
        if ( hydrogenCenter1 != null ) {
            drawAtom( g, hydrogenCenter1.x, hydrogenCenter1.y, WaterMolecule.s_hydrogenRadius, Color.red );
            drawAtom( g, hydrogenCenter2.x, hydrogenCenter2.y, WaterMolecule.s_hydrogenRadius, Color.red );
            drawAtom( g, oxygenCenter.x, oxygenCenter.y, WaterMolecule.s_oxygenRadius, Color.blue );
        }
    }

    private void drawAtom( Graphics2D g, double x, double y, double radius, Color color ) {
        RenderingHints orgRH = g.getRenderingHints();
        atomGraphic.setFrameFromCenter( x, y,
                                        x + radius,
                                        y + radius );
        g.setColor( color );
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.fill( atomGraphic );
        g.setStroke( moleculeStroke );
        g.setColor( Color.BLACK );
        g.draw( atomGraphic );
        g.setRenderingHints( orgRH );
//        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
    }

    public void update( Observable o, Object arg ) {
        if ( o instanceof WaterMolecule ) {
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
