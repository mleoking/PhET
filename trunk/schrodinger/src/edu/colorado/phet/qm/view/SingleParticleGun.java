/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 4:03:38 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class SingleParticleGun extends GunGraphic {

    private JButton fireOne;

    public SingleParticleGun( final SchrodingerPanel schrodingerPanel ) {
        super( schrodingerPanel );

        fireOne = new JButton( "Fire!" );
        fireOne.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fireParticle();
            }
        } );
        fireOne.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                getGunImageGraphic().clearTransform();
                getGunImageGraphic().translate( 0, 10 );
            }

            public void mouseReleased( MouseEvent e ) {
                getGunImageGraphic().clearTransform();
            }
        } );
        PhetGraphic fireJC = PhetJComponent.newInstance( schrodingerPanel, fireOne );
        addGraphic( fireJC );
        fireJC.setLocation( getGunImageGraphic().getWidth() + 2, 0 );

    }

}
