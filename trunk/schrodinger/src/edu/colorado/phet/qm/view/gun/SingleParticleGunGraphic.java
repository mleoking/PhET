/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.qm.phetcommon.ImagePComboBox;
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

public class SingleParticleGunGraphic extends AbstractGunGraphic {
    private JButton fireOne;
    private GunParticle currentObject;
    private GunParticle[] gunItems;
    private AutoFire autoFire;
    private ImageIcon outIcon;
    private ImageIcon inIcon;
    private PhotonBeamParticle photonBeamParticle;
    protected final JCheckBox autoFireJCheckBox;
    private PSwing gunControlPSwing;

    public SingleParticleGunGraphic( final SchrodingerPanel schrodingerPanel ) {
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
                updateGunLocation();
                getSchrodingerPanel().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
            }
        } );
        fireOne.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                if( fireButtonEnabled() ) {
                    updateGunLocation();
                    getGunImageGraphic().translate( 0, 10 );
                }
            }

            public void mouseReleased( MouseEvent e ) {
                if( fireButtonEnabled() ) {
                    updateGunLocation();
                }
            }
        } );
//        fireJC = new PSwing( schrodingerPanel, fireOne );
//        fireJC.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
//        addChild( fireJC );


        autoFire = new AutoFire( this, schrodingerPanel.getIntensityDisplay() );
        autoFireJCheckBox = new AutoFireCheckBox( autoFire );
//        autoJC = new PSwing( schrodingerPanel, autoFireJCheckBox );
//        addChild( autoJC );
//        autoJC.setOffset( fireJC.getX(), fireJC.getY() + fireJC.getHeight() + 5 );


        JPanel gunControlPanel = createGunControlPanel();
        gunControlPSwing = new PSwing( schrodingerPanel, gunControlPanel );
        addChild( gunControlPSwing );

        setupObject( gunItems[0] );
    }

    private JPanel createGunControlPanel() {
        JPanel gunControlPanel = new VerticalLayoutPanel();
        gunControlPanel.setBorder( BorderFactory.createTitledBorder( "Gun" ) );
        gunControlPanel.add( fireOne );
        gunControlPanel.add( autoFireJCheckBox );

        return gunControlPanel;
    }

    protected void layoutChildren() {
        super.layoutChildren();
        gunControlPSwing.setOffset( getGunImageGraphic().getWidth() - 10, getControlOffsetY() );
        getComboBoxGraphic().setOffset( gunControlPSwing.getFullBounds().getMaxX(), gunControlPSwing.getFullBounds().getY() );
        if( getGunControls() != null ) {
            getGunControls().setOffset( getComboBoxGraphic().getFullBounds().getX(), getComboBoxGraphic().getFullBounds().getMaxY() );
        }
//        fireJC.setOffset( getGunImageGraphic().getWidth() + 2 + getFireButtonInsetDX(), getControlOffsetY() + 0 );
//        autoJC.setOffset( fireJC.getFullBounds().getX(), fireJC.getFullBounds().getMaxY() + 5 );
    }

    protected Point getGunLocation() {
        if( currentObject != null ) {
            return currentObject.getGunLocation();
        }
        else {
            return new Point();
        }
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
                    }
                }
                else {
                    if( fireButtonEnabled() ) {
                        fireOne.setEnabled( false );
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
        updateGunLocation();
    }

    protected ImagePComboBox initComboBox() {
        Photon photon = new Photon( this, "Photons", "images/photon-thumb.jpg" );
        PhotonBeam photonBeam = new PhotonBeam( this, photon );
        photonBeamParticle = new PhotonBeamParticle( this, "Photons", photonBeam );

        gunItems = new GunParticle[]{
                photonBeamParticle,
                DefaultGunParticle.createElectron( this ),
                DefaultGunParticle.createNeutron( this ),
                DefaultGunParticle.createHelium( this ),
                DefaultGunParticle.createCustomAtom( this )};
//                new DefaultGunParticle( this, "Electrons", "images/electron-thumb.jpg", 1.0 ),
//                new DefaultGunParticle( this, "Atoms", "images/atom-thumb.jpg" )};

        final ImagePComboBox imageComboBox = new ImagePComboBox( gunItems );
        imageComboBox.setBorder( BorderFactory.createTitledBorder( "Gun Type" ) );
        imageComboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                int index = imageComboBox.getSelectedIndex();
                setupObject( gunItems[index] );
            }
        } );
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
