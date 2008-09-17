package edu.colorado.phet.mazegame;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.view.util.PhetAudioClip;

public class MazeGameSimulationPanel extends JPanel {

    static int fullHeight = 500;    //height and width of applet
    static int fullWidth = 700;        //Must find way to pass in from html
    PhetAudioClip cork;
    PhetAudioClip figaro;  //sound effects
    Image ballImage, splat;  //gifs
    private JPanel topRowPanel = null;
    private JPanel bottomRowPanel = null;

    private ParticleArena pArena = null;

    public void init() {
        topRowPanel = new JPanel();
        bottomRowPanel = new JPanel();
        pArena = new ParticleArena( this );

        //Top Row Panel
        GridLayout gLayoutT = new GridLayout( 1, 1 );
        topRowPanel.setLayout( gLayoutT );
        topRowPanel.add( pArena );

        //Bottom Row Panel
        GridLayout gLayoutB = new GridLayout( 1, 2 );
        bottomRowPanel.setLayout( gLayoutB );
        bottomRowPanel.add( pArena.getScorePanel() );
        bottomRowPanel.add( pArena.getControlBoxPanel() );

        //Main Pane
        GridLayout gLayout = new GridLayout( 2, 1 );
        setLayout( gLayout );

        add( topRowPanel );
        add( bottomRowPanel );

        splat = MazeGameResources.loadBufferedImage( "RedBang.gif" );
        ballImage = MazeGameResources.loadBufferedImage( "ballsmall2.gif" );

        cork = MazeGameResources.getAudioClip( "cork.au" );
        figaro = MazeGameResources.getAudioClip( "figaro.au" );

        pArena.start();
    }
}
