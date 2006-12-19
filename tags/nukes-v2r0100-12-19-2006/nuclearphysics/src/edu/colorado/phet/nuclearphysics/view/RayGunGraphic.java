/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.nuclearphysics.controller.FireButton;
import edu.colorado.phet.nuclearphysics.controller.ChainReactionModule;
import edu.colorado.phet.nuclearphysics.controller.NeutronGun;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * RayGunGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RayGunGraphic extends GraphicLayerSet {

    public RayGunGraphic( Component component, final NeutronGun gun ) {
        super( component );

        PhetImageGraphic gunGraphic;
        gunGraphic = new PhetImageGraphic( component, "images/gun-8A.png" );
        gunGraphic.setRegistrationPoint( gunGraphic.getWidth() - 15, 25 );
        gunGraphic.setRenderingHints( new RenderingHints( RenderingHints.KEY_ALPHA_INTERPOLATION,
                                                          RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY ) );
        gunGraphic.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING,
                                                          RenderingHints.VALUE_ANTIALIAS_ON ) );
        addGraphic( gunGraphic );

        final FireButton fireButton = new FireButton( component );
        fireButton.setLocation( -150, -15 );
        addGraphic( fireButton );
        fireButton.addActionListener( new FireButton.ActionListener() {
            public void actionPerformed( FireButton.ActionEvent event ) {
                gun.fireNeutron();
            }
        } );

        component.addMouseMotionListener( new MouseInputAdapter() {
            // implements java.awt.event.MouseMotionListener
            public void mouseMoved( MouseEvent e ) {
                if( fireButton.contains( e.getPoint() ) ) {
                    System.out.println( "e = " + e );
                    System.out.println( "e = " + e );
                }
                super.mouseMoved( e );
            }
        } );
//        addMouseInputListener( new MouseInputAdapter() {
//            // implements java.awt.event.MouseListener
//            public void mousePressed( MouseEvent e ) {
//                if( fireButton.contains(  e.getPoint() )) {
//                    fireBut
//                }
//            }
//
//            // implements java.awt.event.MouseListener
//            public void mouseReleased( MouseEvent e ) {
//                buttonUpIG.setVisible( true );
//            }
//        } );
        setCursorHand();
        setIgnoreMouse( false );
    }
}
