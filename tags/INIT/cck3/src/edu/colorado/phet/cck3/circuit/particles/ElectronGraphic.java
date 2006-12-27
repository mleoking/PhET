/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.particles;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.PathBranch;
import edu.colorado.phet.cck3.circuit.components.Bulb;
import edu.colorado.phet.cck3.circuit.components.BulbComponentGraphic;
import edu.colorado.phet.cck3.circuit.components.CircuitComponentInteractiveGraphic;
import edu.colorado.phet.cck3.common.AffineTransformUtil;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.fastpaint.FastPaintImageGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: May 29, 2004
 * Time: 12:20:04 AM
 * Copyright (c) May 29, 2004 by Sam Reid
 */
public class ElectronGraphic extends FastPaintImageGraphic {
    Electron electron;
    ModelViewTransform2D transform;
    private CCK3Module module;

    public ElectronGraphic( final Electron electron, ModelViewTransform2D transform, BufferedImage image, Component parent, CCK3Module module ) {
        super( image, parent );
        this.electron = electron;
        this.transform = transform;
        this.module = module;
        electron.addObserver( new SimpleObserver() {
            public void update() {
                doUpdate();
            }
        } );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                doUpdate();
            }
        } );
        doUpdate();
    }

    public void paint( Graphics2D graphics2D ) {
        Branch branch = electron.getBranch();
        if( branch instanceof Bulb ) {
            Bulb bulb = (Bulb)branch;
            PathBranch.Location loc = bulb.getFilament().getLocation( electron.getDistAlongWire() );
            if( bulb.getFilament().isHiddenBranch( loc ) ) {
                Shape origClip = graphics2D.getClip();
                Area area = new Area( origClip );
                module.getCircuitGraphic().getGraphic( bulb );
                CircuitComponentInteractiveGraphic ccig = (CircuitComponentInteractiveGraphic)module.getCircuitGraphic().getGraphic( bulb );
                BulbComponentGraphic bcg = (BulbComponentGraphic)ccig.getCircuitComponentGraphic();
                Shape cover = bcg.getCoverShape();
                area.subtract( new Area( cover ) );
                graphics2D.clip( area );
                super.paint( graphics2D );
                graphics2D.setClip( origClip );
                return;
            }
        }
        super.paint( graphics2D );
    }

    private void doUpdate() {
        setTransform( createTransform() );
    }

    private AffineTransform createTransform() {
        double radius = electron.getRadius();
        int imWidth = transform.modelToViewDifferentialX( radius ) * 2;
        int imHeight = transform.modelToViewDifferentialY( radius ) * 2;
        Point2D at = electron.getPosition();
        at = transform.modelToView( at );
        Rectangle2D src = new Rectangle2D.Double( 0, 0, getBufferedImage().getWidth(), getBufferedImage().getHeight() );
        Rectangle2D dst = new Rectangle2D.Double( at.getX() - imWidth / 2, at.getY() - imHeight / 2, imWidth, imHeight );
        AffineTransform tx = AffineTransformUtil.getTransform( src, dst, Math.PI / 2 );//the pi/2 is a hack because AffineTransformUtil turns upside down.
        return tx;
    }

    public Electron getElectron() {
        return electron;
    }

}
