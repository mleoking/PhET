package edu.colorado.phet.mazegame;

//import edu.colorado.phet.common.view.util.GraphicsUtil;
//import edu.colorado.phet.common.view.util.graphics.ImageLoader;

import edu.colorado.phet.common.view.util.ImageLoader;

import javax.swing.*;
import javax.swing.border.Border;
import java.applet.AudioClip;
import java.awt.*;
import java.io.IOException;

public class MazeGameApplet extends JApplet {
    // Localization

    static int fullHeight = 500;    //height and width of applet
    static int fullWidth = 700;        //Must find way to pass in from html
    AudioClip cork, figaro;  //sound effects
    Image ballImage, splat;  //gifs
    JPanel topRowPanel = null;
    JPanel bottomRowPanel = null;

    public static Border raisedBevel, loweredBevel;
    ParticleArena pArena = null;

    public void init() {
        topRowPanel = new JPanel();
        bottomRowPanel = new JPanel();
        pArena = new ParticleArena( this );

        raisedBevel = BorderFactory.createRaisedBevelBorder();  //Why won't this go in the preamble?
        loweredBevel = BorderFactory.createLoweredBevelBorder();

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
        Container mainPane = this.getContentPane();
        mainPane.setLayout( gLayout );

        mainPane.add( topRowPanel );
        mainPane.add( bottomRowPanel );

//new JFrame("Starting load").setVisible(true);
//        ResourceLoader4 loader = new ResourceLoader4(getClass().getClassLoader(), this);
//AudioLoader audioLoader=new AudioLoader(getClass().getClassLoader());

        //splat = getImage(getDocumentBase(), "sunburst.gif");
        try {

            splat = ImageLoader.loadBufferedImage( "RedBang.gif" );
            ballImage = ImageLoader.loadBufferedImage( "ballsmall2-orig.gif" );
//            splat = ImageLoader.loadBufferedImage( "RedBang.gif" );//getImage(getDocumentBase(), "RedBang.gif");
//            ballImage = ImageLoader.loadBufferedImage( "ballsmall2.gif" );//getImage(getDocumentBase(), "ballsmall2.gif");
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

//new JFrame("Starting audio load").setVisible(true);
        MyClipLoader audioLoader = new MyClipLoader( getClass().getClassLoader(), this );//AudioLoaderFactory().getAudioLoader(this);//.new AudioLoader(getClass().getClassLoader());
        cork = audioLoader.loadAudioClip( "cork.au" );//getAudioClip(getDocumentBase(), "cork.au");
        figaro = audioLoader.loadAudioClip( "figaro.au" );//getAudioClip(getDocumentBase(), "figaro.au");
//        figaro.play() ;
//new JFrame("Playing tada").setVisible(true);
//tada.play();

        pArena.start();
    }//end of constructor


}
