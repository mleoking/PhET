package edu.colorado.phet.lasers;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.ColorChooserFactory;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.quantum.model.StimulatedPhoton;
import edu.colorado.phet.lasers.view.PhotonGraphic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Author: Sam Reid
 * Aug 29, 2007, 7:21:38 PM
 */
public class OptionsDialog extends JDialog {
    private LaserApplication laserApplication;

    public OptionsDialog( final LaserApplication laserApplication ) {
        super( laserApplication.getPhetFrame(), "View Options", false );
        VerticalLayoutPanel pane = new VerticalLayoutPanel();
        pane.setFillNone();
        setContentPane( pane );
        this.laserApplication = laserApplication;
        JButton backgroundColor = new JButton( "Background Color" );
        backgroundColor.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final Color origColor = laserApplication.getBackgroundColor();
                ColorChooserFactory.showDialog( "Background color", laserApplication.getPhetFrame(), laserApplication.getBackgroundColor(), new ColorChooserFactory.Listener() {
                    public void colorChanged( Color color ) {
                        laserApplication.setBackgroundColor( color );
                    }

                    public void ok( Color color ) {
                    }

                    public void cancelled( Color originalColor ) {
                        laserApplication.setBackgroundColor( origColor );
                    }
                } );
            }
        } );
        getContentPane().add( backgroundColor );

        final LinearValueControl photonSize = new LinearValueControl( 1, 50, "Photon Diameter", "000", "pixels" );
        photonSize.setValue( laserApplication.getPhotonSize() );
        photonSize.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                laserApplication.setPhotonSize( (int)photonSize.getValue() );
            }
        } );
        getContentPane().add( photonSize );

        final LinearValueControl photonSeparation = new LinearValueControl( 1, 100, "Pair Separation", "000", "" );
        photonSeparation.setValue( StimulatedPhoton.getSeparation() );
        photonSeparation.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                StimulatedPhoton.setSeparation( photonSeparation.getValue() );
            }
        } );
        getContentPane().add( photonSeparation );

        final JCheckBox jcb = new JCheckBox( "Comet", PhotonGraphic.isCometGraphic() );
        jcb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhotonGraphic.setCometGraphic( jcb.isSelected() );
            }
        } );
        getContentPane().add( jcb );

        pack();
        SwingUtils.centerDialogInParent( this );
    }
}
