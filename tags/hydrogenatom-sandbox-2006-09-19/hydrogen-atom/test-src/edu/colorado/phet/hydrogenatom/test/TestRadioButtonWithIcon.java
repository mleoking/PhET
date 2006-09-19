/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.test;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.control.RadioButtonWithIcon;


public class TestRadioButtonWithIcon extends JFrame {

    public TestRadioButtonWithIcon() {
        
        // Images
        Icon photonIcon = null;
        Icon alphaParticleIcon = null;
        try {
            BufferedImage photonImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_PHOTON );
            photonIcon = new ImageIcon( photonImage );
            BufferedImage alphaParticleImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_ALPHA_PARTICLE );
            alphaParticleIcon = new ImageIcon( alphaParticleImage );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        
        RadioButtonWithIcon r1 = new RadioButtonWithIcon( "photons", photonIcon );
        RadioButtonWithIcon r2 = new RadioButtonWithIcon( "alphaParticles", alphaParticleIcon );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( r1.getRadioButton() );
        buttonGroup.add( r2.getRadioButton() );
        r1.getRadioButton().setSelected( true );
        
        JPanel panel = new JPanel();
        panel.add( r1 );
        panel.add( r2 );

        getContentPane().add( panel );
        pack();
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    }
    public static void main( String[] args ) {
        JFrame frame = new TestRadioButtonWithIcon();
        frame.show();
    }
    
}
