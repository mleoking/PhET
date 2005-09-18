/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.qm.phetcommon.ImageComboBox;
import edu.colorado.phet.qm.view.swing.SchrodingerPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

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
    private AutoFire autoFire;
    private ImageIcon outIcon;
    private ImageIcon inIcon;
    private PhotonBeamParticle photonBeamParticle;
    private PSwing fireJC;

    public SingleParticleGun( final SchrodingerPanel schrodingerPanel ) {
        super( schrodingerPanel );
        fireOne = new JButton( "Fire" );
        fireOne.setFont( new Font( "Lucida Sans", Font.BOLD, 18 ) );
        fireOne.setForeground( Color.red );
        fireOne.setMargin( new Insets( 2, 2, 2, 2 ) );

        addButtonEnableDisable();

        try {
            outIcon = new ImageIcon( ImageLoader.loadBufferedImage( "images/button-out-40.gif" ) );
            inIcon = new ImageIcon( ImageLoader.loadBufferedImage( "images/button-in-40.gif" ) );
            fireOne.setIcon( outIcon );
            fireOne.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        fireOne.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                if( fireButtonEnabled() ) {
                    fireOne.setIcon( inIcon );
                }
            }

            public void mouseReleased( MouseEvent e ) {
                if( fireButtonEnabled() ) {
                    fireOne.setIcon( outIcon );
                }
            }
        } );

        fireOne.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fireOne.setEnabled( false );
                clearAndFire();
                fireOne.setIcon( outIcon );
//                getGunImageGraphic().setTransform( new AffineTransform() );
                initGunLocation();
                getSchrodingerPanel().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
            }
        } );
        fireOne.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                if( fireButtonEnabled() ) {
                    initGunLocation();
                    getGunImageGraphic().translate( 0, 10 );
                }
            }

            public void mouseReleased( MouseEvent e ) {
                if( fireButtonEnabled() ) {
                    initGunLocation();
//                    getGunImageGraphic().setTransform( new AffineTransform() );
                }
            }
        } );
        fireJC = new PSwing( schrodingerPanel, fireOne );
//        fireJC.setCursorHand();
        addChild( fireJC );
        fireJC.setOffset( getGunImageGraphic().getWidth() + 2 + getFireButtonInsetDX(), getControlOffsetY() + 0 );

        //todo piccolo
//        final WiggleMe wiggleMe = new WiggleMe( getSchrodingerPanel(), getSchrodingerPanel().getSchrodingerModule().getModel(), "Push the Button", fireJC );
//        schrodingerPanel.addWorldChild( wiggleMe, Double.POSITIVE_INFINITY );
//        getSchrodingerPanel().addMouseListener( new MouseAdapter() {
//            public void mousePressed( MouseEvent e ) {
//                wiggleMe.setVisible( false );
//            }
//        } );

        setupObject( gunItems[0] );
        autoFire = new AutoFire( this, schrodingerPanel.getIntensityDisplay() );
        JCheckBox jcb = new AutoFireCheckBox( autoFire );
        PSwing autoJC = new PSwing( schrodingerPanel, jcb );
        addChild( autoJC );
        autoJC.setOffset( fireJC.getX(), fireJC.getY() + fireJC.getHeight() + 5 );
    }

    protected void layoutChildren() {
        super.layoutChildren();
        fireJC.setOffset( getGunImageGraphic().getWidth() + 2 + getFireButtonInsetDX(), getControlOffsetY() + 0 );
    }

    private boolean fireButtonEnabled() {
        return fireOne.isEnabled();
    }

    private void addButtonEnableDisable() {
        getSchrodingerModule().getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                double magnitude = getSchrodingerModule().getDiscreteModel().getWavefunction().getMagnitude();
                if( magnitude <= AutoFire.THRESHOLD ) {
                    if( !fireButtonEnabled() ) {
                        fireOne.setEnabled( true );
                        //todo piccolo
//                        fireJC.setCursorHand();
                    }

                }
                else {
                    if( fireButtonEnabled() ) {
                        fireOne.setEnabled( false );
                        //todo piccolo
//                        fireJC.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                    }

                }
            }
        } );
    }


    public void clearAndFire() {
        clearWavefunction();
        fireParticle();
        fireOne.setEnabled( false );
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
        photonBeamParticle = new PhotonBeamParticle( this, "Photons", photonBeam );

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

    public void reset() {
        photonBeamParticle.reset();
    }
}
