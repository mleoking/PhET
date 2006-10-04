package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.CCKImageSuite;
import edu.colorado.phet.cck.model.Electron;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Sep 18, 2006
 * Time: 11:24:35 AM
 * Copyright (c) Sep 18, 2006 by Sam Reid
 */

public class ElectronNode extends PhetPNode {
    private Electron electron;
    private PImage pImage;
    private static BufferedImage image = null;

    static {
        try {
            image = CCKImageSuite.getInstance().getParticleImage();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public ElectronNode( Electron electron ) {
        this.electron = electron;
        pImage = new PImage( image );
        addChild( pImage );
        electron.addObserver( new SimpleObserver() {
            public void update() {
                ElectronNode.this.update();
            }
        } );
        scale( 1.0 / 160.0 );
        setPickable( false );
        setChildrenPickable( false );
        update();
    }

    private void update() {
        setOffset( electron.getPosition() );
        translate( -pImage.getFullBounds().getWidth() / 2.0, -pImage.getFullBounds().getHeight() / 2.0 );
    }

    public Electron getElectron() {
        return electron;
    }
}
