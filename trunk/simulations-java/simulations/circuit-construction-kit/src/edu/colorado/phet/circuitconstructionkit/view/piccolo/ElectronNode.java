// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.circuitconstructionkit.model.Electron;
import edu.colorado.phet.circuitconstructionkit.view.CCKImageSuite;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * User: Sam Reid
 * Date: Sep 18, 2006
 * Time: 11:24:35 AM
 */

public class ElectronNode extends PhetPNode {
    private Electron electron;
    private ClipFactory clipFactory;
    private PImage pImage;
    private static BufferedImage image = null;
    private Shape clip;
    private double SCALE = 1.0 / 160.0;

    static {
        try {
            image = CCKImageSuite.getInstance().getParticleImage();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public ElectronNode( Electron electron, ClipFactory clipFactory ) {
        this.electron = electron;
        this.clipFactory = clipFactory;
        pImage = new PImage( image );
        addChild( pImage );
        electron.addObserver( new SimpleObserver() {
            public void update() {
                ElectronNode.this.update();
            }
        } );

        pImage.scale( SCALE );
        setPickable( false );
        setChildrenPickable( false );
        update();
    }

    public void update() {
        pImage.setOffset( electron.getPosition() );
        pImage.translate( -pImage.getFullBounds().getWidth() / 2.0 / SCALE, -pImage.getFullBounds().getHeight() / 2.0 / SCALE );
        this.clip = clipFactory.getClip( this );
    }

    protected void paint( PPaintContext paintContext ) {
        if ( clip != null ) {
            Shape origClip = paintContext.getGraphics().getClip();
            Area area = new Area( origClip );
            area.subtract( new Area( clip ) );
            paintContext.pushClip( area );
        }
    }

    protected void paintAfterChildren( PPaintContext paintContext ) {
        if ( clip != null ) {
            paintContext.popClip( null );
        }
    }

    public Electron getElectron() {
        return electron;
    }
}
