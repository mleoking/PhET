/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.qm.phetcommon.ImageComboBox;
import edu.colorado.phet.qm.view.SchrodingerPanel;

import javax.swing.*;
import java.awt.event.*;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 4:03:38 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class SingleParticleGun extends AbstractGun {
    private JButton fireOne;
    private GunParticle currentObject;
    private GunParticle[] gunItems;

    public SingleParticleGun( final SchrodingerPanel schrodingerPanel ) {
        super( schrodingerPanel );
        fireOne = new JButton( "Fire!" );
        fireOne.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clearWavefunction();
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

        setupObject( gunItems[0] );
    }

    private void clearWavefunction() {
        getDiscreteModel().clearWavefunction();
    }

    public void fireParticle() {
        currentObject.fireParticle();
    }

    public GunParticle getCurrentObject() {
        return currentObject;
    }


    public void addMomentumChangeListener( MomentumChangeListener momentumChangeListener ) {
        for( int i = 0; i < gunItems.length; i++ ) {
            gunItems[i].addMomentumChangeListerner( momentumChangeListener );
        }
    }

    protected void setupObject( GunParticle particle ) {
        if( particle != currentObject ) {
            getDiscreteModel().clearWavefunction();
            if( currentObject != null ) {
                currentObject.deactivate( this );
            }
            particle.setup( this );
            currentObject = particle;
        }
    }

    protected JComboBox initComboBox() {
        Photon photon = new Photon( this, "Photons", "images/photon-thumb.jpg" );
        PhotonBeam photonBeam = new PhotonBeam( this, photon );
        PhotonBeamParticle photonBeamParticle = new PhotonBeamParticle( this, "Photons", photonBeam );

        gunItems = new GunParticle[]{
//            new Photon( this, "Photons", "images/photon-thumb.jpg" ),
            photonBeamParticle,
            new Electron( this, "Electrons", "images/electron-thumb.jpg" ),
            new Atom( this, "Atoms", "images/atom-thumb.jpg" )};

//        for( int i = 0; i < gunItems.length; i++ ) {
//            GunParticle gunItem = gunItems[i];
//            gunItem.addMomentumChangeListerner( new MomentumChangeListener() {
//                public void momentumChanged( double val ) {
//                    double lambda = Math.abs( 2 * Math.PI / val );
//                    double meters = getDiscreteModel().getMeasurementScale().modelLengthToMeters( lambda );
//                    System.out.println( "Wavelength: meters = " + meters );
//                }
//            } );
//        }

        final ImageComboBox imageComboBox = new ImageComboBox( gunItems );
        imageComboBox.setBorder( BorderFactory.createTitledBorder( "Gun Type" ) );
        imageComboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                int index = imageComboBox.getSelectedIndex();
                setupObject( gunItems[index] );
            }
        } );

//        setupObject( gunItems[0] );
        return imageComboBox;
    }

    public GunParticle[] getGunItems() {
        return gunItems;
    }

    public void setLocation( int x, int y ) {
        super.setLocation( x, y );
        setupObject( currentObject );
    }
}
