/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.bands;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.semiconductor.common.SimpleBufferedImageGraphic;
import edu.colorado.phet.semiconductor.common.TransformGraphic;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 1:54:53 PM
 * Copyright (c) Jan 18, 2004 by Sam Reid
 */
public class BandParticleGraphic extends TransformGraphic {
    BandParticle bandParticle;
    private BufferedImage image;
    SimpleBufferedImageGraphic graphic;
    private Font font = new Font( "dialog", 0, 16 );
    private static boolean showExclaim = true;
    private Font msgFont=new Font( "dialog",0,12);

    public BandParticleGraphic( BandParticle bandParticle, ModelViewTransform2D transform, BufferedImage image ) {
        super( transform );
        this.bandParticle = bandParticle;
        this.image = image;
        this.graphic = new SimpleBufferedImageGraphic( image );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D modelViewTransform2D ) {
                update();
            }
        } );
    }

    static Font exclaimFont = new Font( "Lucida Sans", Font.ITALIC, 18 );

    public void paint( Graphics2D graphics2D ) {
        PhetVector modelLoc = bandParticle.getPosition();
        Point pt = getTransform().modelToView( modelLoc );
        graphic.setPosition( pt );
        graphic.paint( graphics2D );

        if( showExclaim && bandParticle.isExcited() ) {
            graphics2D.setColor( Color.red );
            graphics2D.setFont( exclaimFont );
            graphics2D.drawString( "!", pt.x - 14, pt.y );
        }
        graphics2D.setColor( Color.black);
        graphics2D.setFont( msgFont);
        if (bandParticle.getMessage()!=null)
            graphics2D.drawString( "f="+bandParticle.getMessage(),pt.x,pt.y);
        graphics2D.setClip( null);
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
