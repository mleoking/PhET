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
import java.awt.geom.AffineTransform;
import java.awt.event.ContainerAdapter;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;

/**
 *
 */
public abstract class GasMoleculeGraphic extends PhetImageGraphic implements GasMolecule.Observer {
    private GasMolecule molecule;
    private ApparatusPanel apparatusPanel;
    private static Color s_color;
    private AffineTransform scaleAtx = new AffineTransform();
    private BufferedImage baseImage;
                                  private static Dimension dim=null;
    public GasMoleculeGraphic( final ApparatusPanel apparatusPanel, BufferedImage image, GasMolecule molecule ) {
        super( apparatusPanel, image );
        this.apparatusPanel = apparatusPanel;
        this.molecule = molecule;
        this.baseImage = image;
        molecule.addObserver( this );
        super.setIgnoreMouse( true );
        update();

//        final Dimension startingSize = apparatusPanel.getSize( );
        if (dim==null){
            dim=apparatusPanel.getSize( );
        }
        apparatusPanel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                handleResize( apparatusPanel, dim);
            }
        } );
        handleResize( apparatusPanel, dim );
    }

    private void handleResize( ApparatusPanel apparatusPanel, Dimension startingSize ) {
        Dimension newSize = apparatusPanel.getSize( );
        double scale = newSize.getWidth() / startingSize.getWidth();
        scaleAtx = AffineTransform.getScaleInstance( scale, scale );
        AffineTransformOp atxOp = new AffineTransformOp( scaleAtx, new RenderingHints( RenderingHints.KEY_INTERPOLATION,
                                                                                       RenderingHints.VALUE_INTERPOLATION_BICUBIC ));
        setImage( atxOp.filter( baseImage, null ));
    }


    public void paint( Graphics2D g2 ) {
        AffineTransform orgTx = getNetTransform();
//        System.out.println( "orgTx = " + orgTx );
//        g2.setTransform( new AffineTransform( ) );
//        super.paint( g2 );
//        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
        g2.drawImage( getImage(),(int)orgTx.getTranslateX(),(int)orgTx.getTranslateY(),null);
//        g2.setTransform( orgTx );
    }

    public void update() {
        super.setLocation( (int)( molecule.getCM().getX() - molecule.getRadius() ),
                           (int)( molecule.getCM().getY() - molecule.getRadius() ) );
    }

    public void removedFromSystem() {
        apparatusPanel.removeGraphic( this );
        update();
    }

    public static Color getColor() {
        return s_color;
    }

    public static void setColor( Color color ) {
        s_color = color;
    }
}
