package edu.colorado.phet.lasers.controller.module;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.view.PhotonGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class: WaveViewControlPanel
 * Package: edu.colorado.phet.lasers.controller.module
 * Author: Another Guy
 * Date: Nov 22, 2004
 * <p/>
 * CVS Info:
 * Current revision:   $Revision$
 * On branch:          $Name$
 * Latest change by:   $Author$
 * On date:            $Date$
 */
public class WaveViewControlPanel extends JPanel {
    private BaseLaserModule module;
    private ButtonGroup buttonGrp;
    private JRadioButton photonViewRB;
    private JRadioButton waveViewRB;

    public WaveViewControlPanel( BaseLaserModule module ) {
        this.module = module;
        setLayout( new GridBagLayout() );
        buttonGrp = new ButtonGroup();
        photonViewRB = new JRadioButton( SimStrings.get( "WaveViewControlPanel.photonView" ) );
        waveViewRB = new JRadioButton( SimStrings.get( "WaveViewControlPanel.waveView" ) );
        buttonGrp.add( photonViewRB );
        buttonGrp.add( waveViewRB );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.WEST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 5, 5, 5, 5 ), 0, 0 );
        this.add( photonViewRB, gbc );
        gbc.gridy++;
        this.add( waveViewRB, gbc );

        photonViewRB.addActionListener( new RadioButtonListener() );
        waveViewRB.addActionListener( new RadioButtonListener() );
    }

    private class RadioButtonListener implements ActionListener {
        public void actionPerformed( ActionEvent e ) {
            JRadioButton selection = GraphicsUtil.getSelection( buttonGrp );
            if( selection == photonViewRB ) {
                module.setPhotonView();
            }
            if( selection == waveViewRB ) {
                PhotonGraphic.removeAll( module.getApparatusPanel() );
                module.setWaveView();
            }
        }
    }
}
