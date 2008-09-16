// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.electron.gui.media;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.efield.electron.core.SystemFactory;
import edu.colorado.phet.efield.electron.gui.ParticlePainter;
import edu.colorado.phet.efield.electron.gui.ParticlePanel;
import edu.colorado.phet.efield.electron.phys2d_efield.SystemRunner;

// Referenced classes of package edu.colorado.phet.efield.electron.gui.media:
//            SelectableJButton, Resettable

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


    public MediaControl( SystemRunner systemrunner, SystemFactory systemfactory, ParticlePanel particlepanel, double d, int i, ParticlePainter particlepainter,
                         BufferedImage bufferedimage, BufferedImage bufferedimage1, BufferedImage bufferedimage2 ) {
        playIcon = bufferedimage;
        pauseIcon = bufferedimage1;
        resetIcon = bufferedimage2;
        painter = particlepainter;
        system = systemrunner;
        resetter = systemfactory;
        pp = particlepanel;
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
        playButton = new SelectableJButton( SimStrings.get( "MediaControl.PlayButton" ) + " ", icon, imageicon, true );
        playButton.addActionListener( new UnPauseListener() );
        ImageIcon imageicon1 = new ImageIcon( pauseIcon );
        pauseButton = new SelectableJButton( SimStrings.get( "MediaControl.PauseButton" ) + " ", ( new JLabel( imageicon1 ) ).getDisabledIcon(), imageicon1, false );
        pauseButton.addActionListener( new PauseListener() );
        ImageIcon imageicon2 = new ImageIcon( resetIcon );
        resetButton = new SelectableJButton( SimStrings.get( "MediaControl.ResetButton" ) + " ", ( new JLabel( imageicon2 ) ).getDisabledIcon(), imageicon2, false );
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
        pp.reset();
        system.setRunning( false );
        edu.colorado.phet.efield.electron.phys2d_efield.System2D system2d = resetter.newSystem();
        system.setSystem( system2d );
        resetter.updatePanel( pp, system2d, painter );
        pp.repaint();
        for ( int i = 0; i < resettables.size(); i++ ) {
            EFieldResettable EFieldResettable = (EFieldResettable) resettables.get( i );
            EFieldResettable.fireResetAction( system2d, pp );
        }

    }

    public void pause() {
        system.setRunning( false );
    }

    public void unpause() {
        system.setRunning( true );
    }

    SystemRunner system;
    SystemFactory resetter;
    ParticlePanel pp;
    ParticlePainter painter;
    Vector resettables;
    SelectableJButton playButton;
    SelectableJButton resetButton;
    SelectableJButton pauseButton;
    BufferedImage playIcon;
    BufferedImage pauseIcon;
    BufferedImage resetIcon;
}
