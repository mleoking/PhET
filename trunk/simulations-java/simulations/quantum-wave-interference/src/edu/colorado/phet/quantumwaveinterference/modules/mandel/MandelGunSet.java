/*  */
package edu.colorado.phet.quantumwaveinterference.modules.mandel;

import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.phetcommon.ImagePComboBox;
import edu.colorado.phet.quantumwaveinterference.view.gun.IntensityBeam;
import edu.colorado.phet.quantumwaveinterference.view.gun.IntensityGunNode;
import edu.colorado.phet.quantumwaveinterference.view.gun.Photon;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.BlueGunDetails;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.PinkGunDetails;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 8:35:37 AM
 */

public class MandelGunSet extends IntensityGunNode {
    private MandelGun leftGun;
    private MandelGun rightGun;
    private Photon photon;
    private PhotonMandelBeam photonMandelBeam;

    public MandelGunSet( MandelSchrodingerPanel mandelSchrodingerPanel ) {
        super( mandelSchrodingerPanel );
        leftGun = new MandelGun( "quantum-wave-interference/images/mandel-gun.gif", mandelSchrodingerPanel );
        leftGun.setControlsOffset( -leftGun.getGunControlPanelPSwing().getFullBounds().getWidth() + 50, 0 );
        leftGun.setControlBackgroundColor( BlueGunDetails.gunBackgroundColor );
        leftGun.translateOnGunControls( 0, 5 );
        rightGun = new MandelGun( "quantum-wave-interference/images/gun2-ii.gif", mandelSchrodingerPanel );
        rightGun.setControlsOffset( rightGun.getGunImageGraphic().getFullBounds().getWidth(), 0 );
        rightGun.setControlBackgroundColor( PinkGunDetails.backgroundColor );
        rightGun.translateOnGunControls( 10, 5 );

        leftGun.addListener( new MandelGun.Listener() {
            public void wavelengthChanged() {
                updateWavelengths();
            }

            public void intensityChanged() {
            }
        } );
        rightGun.addListener( new MandelGun.Listener() {
            public void wavelengthChanged() {
                updateWavelengths();
            }

            public void intensityChanged() {
            }
        } );
        getOnGunGraphic().setVisible( false );
        getOnGunGraphic().setPickable( false );
        getOnGunGraphic().setChildrenPickable( false );

        getGunImageGraphic().setVisible( false );
        getGunImageGraphic().setPickable( false );
        getGunImageGraphic().setChildrenPickable( false );

        addChild( leftGun );
        addChild( rightGun );
        layoutChildren();
        setOnOffTextVisible( false );
    }

    private void updateWavelengths() {
//        leftGun.getWavelength();
        photonMandelBeam.setLeftMomentum( leftGun.getMomentum() );
        photonMandelBeam.setRightMomentum( rightGun.getMomentum() );
    }

    protected void layoutChildren() {
        super.layoutChildren();
        Point2D origGunLoc = getGunLocation();
        double dx = getGunOffsetDx();
        if( leftGun != null && rightGun != null ) {
            double dy = 5;
            leftGun.setOffset( new Point2D.Double( origGunLoc.getX() - dx, origGunLoc.getY() - dy ) );
            rightGun.setOffset( new Point2D.Double( origGunLoc.getX() + dx, origGunLoc.getY() - dy ) );
        }
    }

//    protected double getControlOffsetX() {
//        if( rightGun != null ) {
//            return rightGun.getFullBounds().getMaxX();
//        }
//        else {
//            return 0;
//        }
//    }

    private double getGunOffsetDx() {
        double width = getSchrodingerPanel().getWavefunctionGraphic().getGlobalFullBounds().getWidth();
        return width / 2 - width * DoublePhotonWave.getFractionalInset();
    }

    public void setBeamParameters( MandelModule.BeamParam leftParam, MandelModule.BeamParam rightParam ) {
        photonMandelBeam.setBeamParameters( leftParam, rightParam );
    }

    protected ImagePComboBox initComboBox() {
        photon = new Photon( this, QWIResources.getString( "particles.photons" ), "quantum-wave-interference/images/photon-thumb.jpg" );
        photonMandelBeam = new PhotonMandelBeam( this, photon );

        final IntensityBeam[] beams = new IntensityBeam[]{photonMandelBeam};
        setBeams( beams );

        final ImagePComboBox imageComboBox = new ImagePComboBox( beams );
//        imageComboBox.setBorder( BorderFactory.createTitledBorder( "Gun Type" ) );
        imageComboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                int index = imageComboBox.getSelectedIndex();
                setupObject( beams[index] );
            }
        } );
        imageComboBox.setVisible( false );
        return imageComboBox;
    }

    public MandelGun getLeftGun() {
        return leftGun;
    }

    public MandelGun getRightGun() {
        return rightGun;
    }

}
