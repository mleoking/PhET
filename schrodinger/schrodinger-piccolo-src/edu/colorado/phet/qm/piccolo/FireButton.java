/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.piccolo;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.qm.model.DiscreteModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jul 28, 2005
 * Time: 10:18:31 AM
 * Copyright (c) Jul 28, 2005 by Sam Reid
 */

public class FireButton extends JButton {
    private ImageIcon outIcon;
    private ImageIcon inIcon;
//    private SchrodingerCanvas schrodingerCanvas;
    private GunPGraphic gunPGraphic;

    public FireButton( GunPGraphic gunPGraphic ) {
        super( "Fire" );
        this.gunPGraphic = gunPGraphic;

        setFont( new Font( "Lucida Sans", Font.BOLD, 18 ) );
        setForeground( Color.red );
        setMargin( new Insets( 2, 2, 2, 2 ) );

        try {
            outIcon = new ImageIcon( ImageLoader.loadBufferedImage( "images/button-out-40.gif" ) );
            inIcon = new ImageIcon( ImageLoader.loadBufferedImage( "images/button-in-40.gif" ) );
            setIcon( outIcon );
            setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                if( fireButtonEnabled() ) {
                    setIcon( inIcon );
                }
            }

            public void mouseReleased( MouseEvent e ) {
                if( fireButtonEnabled() ) {
                    setIcon( outIcon );
                }
            }
        } );

        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setEnabled( false );
                clearAndFire();
                setIcon( outIcon );
//                getGunImageGraphic().clearTransform();
//                getSchrodingerPanel().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
            }
        } );
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                if( fireButtonEnabled() ) {
//                    getGunImageGraphic().clearTransform();
//                    getGunImageGraphic().translate( 0, 10 );
                }
            }

            public void mouseReleased( MouseEvent e ) {
                if( fireButtonEnabled() ) {
//                    getGunImageGraphic().clearTransform();
                }
            }
        } );
    }

    private void clearAndFire() {
        clearWavefunction();
        fireParticle();
        setEnabled( false );
    }

    private void clearWavefunction() {
        getDiscreteModel().clearWavefunction();
    }

    private DiscreteModel getDiscreteModel() {
        return gunPGraphic.getDiscreteModel();
    }

    public void fireParticle() {
        gunPGraphic.fireParticle();
    }

    private boolean fireButtonEnabled() {
        return isEnabled();
    }
}
