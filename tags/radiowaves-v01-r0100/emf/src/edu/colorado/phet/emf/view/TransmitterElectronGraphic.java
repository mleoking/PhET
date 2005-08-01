/**
 * Class: TransmitterElectronGraphic
 * Package: edu.colorado.phet.emf.view
 * Author: Another Guy
 * Date: Dec 4, 2003
 */
package edu.colorado.phet.emf.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.BoundedGraphic;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.emf.Config;
import edu.colorado.phet.emf.EmfModule;
import edu.colorado.phet.emf.model.Electron;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TransmitterElectronGraphic extends DefaultInteractiveGraphic
        implements BoundedGraphic, Translatable, TransformListener {

    private static BufferedImage image;
    static {
        try {
            image = ImageLoader.loadBufferedImage( Config.bigElectronImg );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private ApparatusPanel apparatusPanel;
    private Graphic wiggleMeGraphic;
    private ElectronGraphic electronGraphic;
    private Electron electron;
    private EmfModule module;
    private Point dragPt;
    private AffineTransform atx;


    public TransmitterElectronGraphic( ApparatusPanel apparatusPanel, Electron electron, /*Point origin,*/ EmfModule module ) {
        super( null );
        this.electron = electron;
        this.module = module;
        init( apparatusPanel, electron );
    }

    public void transformChanged( ModelViewTransform2D mvt ) {
        electronGraphic.transformChanged( mvt );
    }


    public void mouseDragged( MouseEvent e ) {
        dragPt = e.getPoint();
        super.mouseDragged( e );
    }

    public void translate( double dx, double dy ) {
        ModelViewTransform2D mvTx = module.getMvTx();
        Point2D newPt = mvTx.viewToModel( dragPt.x, dragPt.y );
        electron.moveToNewPosition( newPt );
    }

    public boolean contains( int x, int y ) {
        boolean b = electronGraphic.contains( x, y );
        return b;
    }

    private void init( ApparatusPanel apparatusPanel, /*final Point origin,*/ Electron electron ) {
        this.apparatusPanel = apparatusPanel;
        electronGraphic = new ElectronGraphic( apparatusPanel, image, electron );
        super.setBoundary( electronGraphic);
        super.setGraphic( electronGraphic);
        electron.addObserver( electronGraphic );
        this.addCursorHandBehavior();
        this.addTranslationBehavior( this );
    }

    public void mouseReleased( MouseEvent event ) {
        module.removeWiggleMeGraphic();
    }
}
