/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.nodes.ShadowPText;
import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.davissongermer.QWIStrings;
import edu.colorado.phet.qm.view.QWIPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Feb 6, 2006
 * Time: 10:32:23 PM
 * Copyright (c) Feb 6, 2006 by Sam Reid
 */

public class PlainFireButton extends PNode {
    private BufferedImage outIcon;
    private BufferedImage inIcon;
    private PImage icon;
    private ShadowPText text;
    private SingleParticleGunNode gun;
    private FireParticle fireParticle;
    private boolean enabled = true;
    private BufferedImage grayIcon;

    public PlainFireButton( SingleParticleGunNode gun, FireParticle fireParticle ) {
        this.gun = gun;
        this.fireParticle = fireParticle;
        icon = new PImage();
        text = new ShadowPText( QWIStrings.getString( "fire1" ) );
        text.setShadowOffset( 1, 1 );
        text.setShadowColor( Color.black );
        text.setFont( new Font( "Lucida Sans", Font.BOLD, 12 ) );
        text.setTextPaint( Color.red );
        try {
            outIcon = ImageLoader.loadBufferedImage( "images/button-out-40.gif" );
            inIcon = ImageLoader.loadBufferedImage( "images/button-in-40.gif" );
            grayIcon = ImageLoader.loadBufferedImage( "images/button-out-40-gray.gif" );
            icon = new PImage( outIcon );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        setIcon( outIcon );
        addChild( icon );
        addChild( text );
        text.setOffset( icon.getFullBounds().getWidth() / 2 - text.getFullBounds().getWidth() / 2, icon.getFullBounds().getHeight() );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                if( fireButtonEnabled() ) {
                    setIcon( inIcon );
                    pullback();
                }
            }

            public void mouseReleased( PInputEvent event ) {
//                if( fireButtonEnabled() ) {
//                    setIcon( outIcon );
//                }
                releasePullback();
                fireParticle();
            }
        } );
        addInputEventListener( new CursorHandler() );
    }

    private void setIcon( BufferedImage image ) {
        icon.setImage( image );
    }

    private void releasePullback() {
        if( fireButtonEnabled() ) {
            updateGunLocation();
        }
    }

    private void pullback() {
        if( fireButtonEnabled() ) {
            updateGunLocation();
            getGunImageGraphic().translate( 0, 10 );
        }
    }

    private void fireParticle() {
        clearAndFire();
//        setIcon( outIcon );
        setIcon( inIcon );
        updateGunLocation();
        getSchrodingerPanel().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public void addButtonEnableDisable() {
        getSchrodingerModule().getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                double magnitude = getSchrodingerModule().getQWIModel().getWavefunction().getMagnitude();
                if( ( magnitude <= AutoFire.THRESHOLD || Double.isNaN( magnitude ) ) && !gun.isFiring() ) {
                    if( !fireButtonEnabled() ) {
                        setEnabled( true );
                    }
                }
                else {
                    if( fireButtonEnabled() ) {
                        setEnabled( false );
                    }
                }
            }
        } );
    }

    public void setEnabled( boolean b ) {
        this.enabled = b;
        setIcon( enabled ? outIcon : inIcon );
    }

    private boolean fireButtonEnabled() {
        return enabled;
    }

    private QWIModule getSchrodingerModule() {
        return fireParticle.getSchrodingerModule();
    }

    private QWIPanel getSchrodingerPanel() {
        return getSchrodingerModule().getSchrodingerPanel();
    }

    private void updateGunLocation() {
        fireParticle.updateGunLocation();
    }

    private void clearAndFire() {
        fireParticle.clearAndFire();
    }

    private PNode getGunImageGraphic() {
        return fireParticle.getGunImageGraphic();
    }
}
