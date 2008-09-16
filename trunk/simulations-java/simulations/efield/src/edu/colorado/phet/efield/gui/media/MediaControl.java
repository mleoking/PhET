// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.gui.media;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.efield.EFieldResources;
import edu.colorado.phet.efield.core.SystemFactory;
import edu.colorado.phet.efield.gui.ParticlePainter;
import edu.colorado.phet.efield.gui.ParticlePanel;
import edu.colorado.phet.efield.phys2d_efield.SystemRunner;

public class MediaControl {
    public class UnPauseListener
            implements ActionListener {

        public void actionPerformed( ActionEvent actionevent ) {
            unpause();
        }

        public UnPauseListener() {
        }
    }

    public class PauseListener
            implements ActionListener {

        public void actionPerformed( ActionEvent actionevent ) {
            pause();
        }

        public PauseListener() {
        }
    }

    public class ResetListener
            implements ActionListener {

        public void actionPerformed( ActionEvent actionevent ) {
            reset();
        }

        public ResetListener() {
        }
    }


    public MediaControl( IClock clock, SystemRunner systemrunner, SystemFactory systemfactory, ParticlePanel particlepanel, ParticlePainter particlepainter,
                         BufferedImage bufferedimage, BufferedImage bufferedimage1, BufferedImage bufferedimage2 ) {
        this.clock = clock;
        playIcon = bufferedimage;
        pauseIcon = bufferedimage1;
        resetIcon = bufferedimage2;
        particlePainter = particlepainter;
        systemRunner = systemrunner;
        systemFactory = systemfactory;
        particlePanel = particlepanel;
        resettables = new Vector();
//        display = new JTextArea("My text area.");
    }

    public void add( EFieldResettable EFieldResettable ) {
        resettables.add( EFieldResettable );
    }

    public JPanel getPanel() {
        JPanel jpanel = new JPanel();
        jpanel.setLayout( new BoxLayout( jpanel, 0 ) );
        ImageIcon imageicon = new ImageIcon( playIcon );
        javax.swing.Icon icon = ( new JLabel( imageicon ) ).getDisabledIcon();
        playButton = new SelectableJButton( EFieldResources.getString( "MediaControl.PlayButton" ) + " ", imageicon, true );
        playButton.addActionListener( new UnPauseListener() );
        ImageIcon imageicon1 = new ImageIcon( pauseIcon );
        pauseButton = new SelectableJButton( EFieldResources.getString( "MediaControl.PauseButton" ) + " ", imageicon1, false );
        pauseButton.addActionListener( new PauseListener() );
        ImageIcon imageicon2 = new ImageIcon( resetIcon );
        resetButton = new SelectableJButton( EFieldResources.getString( "MediaControl.ResetButton" ) + " ", imageicon2, false );
        resetButton.addActionListener( new ResetListener() );
        jpanel.add( pauseButton );
        jpanel.add( playButton );
        jpanel.add( resetButton );
        pauseButton.addGroupElement( resetButton );
        pauseButton.addGroupElement( playButton );
        resetButton.addGroupElement( playButton );
        resetButton.addGroupElement( pauseButton );
        playButton.addGroupElement( resetButton );
        playButton.addGroupElement( pauseButton );
        return jpanel;
    }

    public void reset() {
        particlePanel.reset();
        clock.start();
        edu.colorado.phet.efield.phys2d_efield.System2D system2d = systemFactory.newSystem();
        systemRunner.setSystem( system2d );
        systemFactory.updatePanel( particlePanel, system2d, particlePainter );
        particlePanel.repaint();
        for ( int i = 0; i < resettables.size(); i++ ) {
            EFieldResettable EFieldResettable = (EFieldResettable) resettables.get( i );
            EFieldResettable.fireResetAction( system2d, particlePanel );
        }

    }

    public void pause() {
        clock.pause();
    }

    public void unpause() {
        clock.start();
    }

    SystemRunner systemRunner;
    SystemFactory systemFactory;
    ParticlePanel particlePanel;
    ParticlePainter particlePainter;
    Vector resettables;
    SelectableJButton playButton;
    SelectableJButton resetButton;
    SelectableJButton pauseButton;
    private IClock clock;
    BufferedImage playIcon;
    BufferedImage pauseIcon;
    BufferedImage resetIcon;
}
