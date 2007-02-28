/** Sam Reid*/
package edu.colorado.phet.cck.phetgraphics_cck.circuit.particles;

import edu.colorado.phet.cck.common.AffineTransformUtil;
import edu.colorado.phet.cck.model.Electron;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.Bulb;
import edu.colorado.phet.cck.model.components.PathBranch;
import edu.colorado.phet.cck.phetgraphics_cck.CCKPhetgraphicsModule;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.components.BulbComponentGraphic;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.components.CircuitComponentInteractiveGraphic;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.common_cck.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetImageGraphic;

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
public class ElectronGraphic extends PhetImageGraphic {
    private Electron electron;
    private ModelViewTransform2D transform;
    private CCKPhetgraphicsModule module;
    private TransformListener transformListener;
    private SimpleObserver electronObserver;

    public ElectronGraphic( final Electron electron, ModelViewTransform2D transform, BufferedImage image, Component parent, CCKPhetgraphicsModule module ) {
        super( parent, image, createTransformStatic( electron, transform, image ) );
        this.electron = electron;
        this.transform = transform;
        this.module = module;
        electronObserver = new SimpleObserver() {
            public void update() {
                doUpdate();
            }
        };
        electron.addObserver( electronObserver );
        transformListener = new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                doUpdate();
            }
        };
        transform.addTransformListener( transformListener );
        doUpdate();
        setVisible( true );
    }

    public void delete() {
        transform.removeTransformListener( transformListener );
        electron.removeObserver( electronObserver );
    }

    private static AffineTransform createTransformStatic( Electron electron, ModelViewTransform2D transform, BufferedImage image ) {
        double radius = electron.getRadius();
        int imWidth = transform.modelToViewDifferentialX( radius ) * 2;
        int imHeight = transform.modelToViewDifferentialY( radius ) * 2;
        Point2D at = electron.getPosition();
        at = transform.modelToView( at );
        if( at.getX() < 1 ) {
//            System.out.println( "view coordinate was Less than 1" );
        }
        Rectangle2D src = new Rectangle2D.Double( 0, 0, image.getWidth(), image.getHeight() );
        Rectangle2D dst = new Rectangle2D.Double( at.getX() - imWidth / 2, at.getY() - imHeight / 2, imWidth, imHeight );
        AffineTransform tx = AffineTransformUtil.getTransform( src, dst, Math.PI / 2 );//the pi/2 is a hack because AffineTransformUtil turns upside down.
        return tx;
    }

    public void paint( Graphics2D graphics2D ) {

        Branch branch = electron.getBranch();
        if( branch instanceof Bulb ) {
            Bulb bulb = (Bulb)branch;
            PathBranch.Location loc = bulb.getFilament().getLocation( electron.getDistAlongWire() );
            if( loc == null ) {
                throw new RuntimeException( "Null electron location." );
            }
            if( bulb.getFilament().isHiddenBranch( loc ) ) {
                Shape origClip = graphics2D.getClip();
                Area area = new Area( origClip );
                InteractiveGraphic thegraphic = module.getCircuitGraphic().getGraphic( bulb );
                if( thegraphic instanceof CircuitComponentInteractiveGraphic ) { //check to see if it's a lifelike bulb.
                    CircuitComponentInteractiveGraphic ccig = (CircuitComponentInteractiveGraphic)thegraphic;
                    if( ccig.getCircuitComponentGraphic() instanceof BulbComponentGraphic ) {
                        BulbComponentGraphic bcg = (BulbComponentGraphic)ccig.getCircuitComponentGraphic();
                        Shape cover = bcg.getCoverShape();
                        area.subtract( new Area( cover ) );
                        graphics2D.clip( area );
                        super.paint( graphics2D );
                        graphics2D.setClip( origClip );
                        return;
                    }
                }
            }
        }
        Shape origClip = graphics2D.getClip();

        Shape electronClip = module.getElectronClip();
//        System.out.println( "electronClip.getBounds() = " + electronClip.getBounds() );
        if( !electronClip.getBounds().equals( new Rectangle( 0, 0, 0, 0 ) ) ) {
            graphics2D.setClip( electronClip );
        }

        super.paint( graphics2D );
        graphics2D.setClip( origClip );
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
        if( at.getX() < 1 ) {
//            System.out.println( "view coordinate was Less than 1" );
        }
        Rectangle2D src = new Rectangle2D.Double( 0, 0, getImage().getWidth(), getImage().getHeight() );
        Rectangle2D dst = new Rectangle2D.Double( at.getX() - imWidth / 2, at.getY() - imHeight / 2, imWidth, imHeight );
        return AffineTransformUtil.getTransform( src, dst, Math.PI / 2 );
    }

    public Electron getElectron() {
        return electron;
    }

}
