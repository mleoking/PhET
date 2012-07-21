// Copyright 2002-2012, University of Colorado

/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.bands;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.semiconductor.SemiconductorResources;
import edu.colorado.phet.semiconductor.common.SimpleBufferedImageGraphic;
import edu.colorado.phet.semiconductor.common.TransformGraphic;


/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 1:54:53 PM
 */
public class BandParticleGraphic extends TransformGraphic {
    BandParticle bandParticle;
    SimpleBufferedImageGraphic graphic;
    private static boolean showExclaim = true;
    private Font msgFont = new PhetFont( 12 );

    public BandParticleGraphic( BandParticle bandParticle, ModelViewTransform2D transform, BufferedImage image ) {
        super( transform );
        this.bandParticle = bandParticle;
        this.graphic = new SimpleBufferedImageGraphic( image );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D modelViewTransform2D ) {
                update();
            }
        } );
    }

    static Font exclaimFont = new PhetFont( Font.ITALIC, 18 );

    public void paint( Graphics2D graphics2D ) {
        MutableVector2D modelLoc = bandParticle.getPosition();
        Point pt = getTransform().modelToView( modelLoc );
        graphic.setPosition( pt );
        graphic.paint( graphics2D );

        if ( showExclaim && bandParticle.isExcited() ) {
            graphics2D.setColor( Color.red );
            graphics2D.setFont( exclaimFont );
            graphics2D.drawString( SemiconductorResources.getString( "BandParticleGraphic.ExclaimText" ), pt.x - 14, pt.y );
        }
        graphics2D.setColor( Color.black );
        graphics2D.setFont( msgFont );
//        if( bandParticle.getMessage() != null ) {
//            graphics2D.drawString( "f=" + bandParticle.getMessage(), pt.x, pt.y );
//        }
//        graphics2D.setClip( null );

//        graphics2D.setColor(Color.black);
//        graphics2D.setClip(0,0,1000,1000);
//        if (bandParticle.isMoving()){
//            graphics2D.setFont(font);
//            graphics2D.drawString(bandParticle.getState().getClass().getName(),pt.x,pt.energy);
////            graphics2D.fillRect(pt.x,pt.energy,5,5);
//        }
    }

    public void update() {
    }

    public BandParticle getBandParticle() {
        return bandParticle;
    }
}
