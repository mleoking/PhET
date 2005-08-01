/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.piccolo;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.WiggleMe;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jul 28, 2005
 * Time: 9:19:30 AM
 * Copyright (c) Jul 28, 2005 by Sam Reid
 */

public class GunPGraphic extends PNode {
    private SchrodingerCanvas schrodingerCanvas;
    private PImage gunImage;
    private double gunInsetY = 8;

    public GunPGraphic( SchrodingerCanvas schrodingerCanvas ) {
        this.schrodingerCanvas = schrodingerCanvas;
        String imageResourceName = "images/raygun3-200x160-scaled-matt.gif";
        try {
            BufferedImage im = ImageLoader.loadBufferedImage( imageResourceName );
            gunImage = new PImage( im );
            addChild( gunImage );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        WavefunctionPGraphic wavefunctionPGraphic = schrodingerCanvas.getWavefunctionPGraphic();
        PBounds fullBounds = wavefunctionPGraphic.getFullBounds();
        setOffset( fullBounds.getWidth() / 2 - gunImage.getFullBounds().getWidth() / 2, fullBounds.getMaxY() - gunInsetY );

        FireButton fireButton = new FireButton( this );
        PSwing fireButtonGraphic = new PSwing( schrodingerCanvas, fireButton );
        addChild( fireButtonGraphic );
        fireButtonGraphic.setOffset( gunImage.getFullBounds().getWidth(), 5 );

        JCheckBox autoRepeat = new JCheckBox( "Auto-Repeat" );
        PSwing autoRepeatGraphic = new PSwing( schrodingerCanvas, autoRepeat );
        addChild( autoRepeatGraphic );
        autoRepeatGraphic.setOffset( fireButtonGraphic.getFullBounds().getX(), fireButtonGraphic.getFullBounds().getMaxY() );

        WiggleMe wiggleMe = new WiggleMe( "Push the Button" );
        addChild( wiggleMe );
        wiggleMe.setOscillating( true );

//        PGunParticle gunParticle=new PhotonBeamParticle( );
    }

    public DiscreteModel getDiscreteModel() {
        return schrodingerCanvas.getDiscreteModel();
    }

    public void fireParticle() {
    }

    public SchrodingerModule getSchrodingerModule() {
        return null;
    }
}
