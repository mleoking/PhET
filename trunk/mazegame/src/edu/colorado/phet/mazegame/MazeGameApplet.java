package edu.colorado.phet.mazegame;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;

import javax.swing.*;
import javax.swing.border.Border;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

public class MazeGameApplet extends JApplet {
    static int fullHeight = 500;	//height and width of applet
    static int fullWidth = 700;		//Must find way to pass in from html
    AudioClip tada, bangouch, cork, figaro, twiddle;  //sound effects
    Image ballImage, splat;  //gifs
    JPanel topRowPanel = new JPanel();
    JPanel bottomRowPanel = new JPanel();
    JPanel bottomLeftPanel = new JPanel();

    public static Border raisedBevel, loweredBevel, compound1, compound2;
    ParticleArena pArena = new ParticleArena( this );

    public void init() {
//new JFrame("Init").setVisible(true);
        raisedBevel = BorderFactory.createRaisedBevelBorder();  //Why won't this go in the preamble?
        loweredBevel = BorderFactory.createLoweredBevelBorder();
        compound1 = BorderFactory.createCompoundBorder( raisedBevel, loweredBevel );

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
            splat = ImageLoader.loadBufferedImage( "RedBang.gif" );//getImage(getDocumentBase(), "RedBang.gif");
            ballImage = ImageLoader.loadBufferedImage( "ballsmall2.gif" );//getImage(getDocumentBase(), "ballsmall2.gif");
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

//new JFrame("Starting audio load").setVisible(true);
        MyClipLoader audioLoader = new MyClipLoader( getClass().getClassLoader(), this );//AudioLoaderFactory().getAudioLoader(this);//.new AudioLoader(getClass().getClassLoader());
        tada = audioLoader.loadAudioClip( "tada.WAV" );//getAudioClip(getDocumentBase(), "tada.WAV");
        cork = audioLoader.loadAudioClip( "cork.au" );//getAudioClip(getDocumentBase(), "cork.au");
        figaro = audioLoader.loadAudioClip( "figaro.au" );//getAudioClip(getDocumentBase(), "figaro.au");
//        figaro.play() ;
//new JFrame("Playing tada").setVisible(true);
//tada.play();

        pArena.start();
    }//end of constructor

    public static void main( String[] args ) {
        JFrame f = new JFrame();
        MazeGameApplet mg = new MazeGameApplet();
        f.setContentPane( mg );
        mg.init();
        f.setSize( 700, 500 );
        GraphicsUtil.centerFrameOnScreen( f );
        f.setVisible( true );
        f.addWindowListener( new Exit() );


    }

    public static class Exit implements WindowListener {
        public void windowOpened( WindowEvent e ) {
        }

        public void windowClosing( WindowEvent e ) {
            System.exit( 0 );
        }

        public void windowClosed( WindowEvent e ) {
        }

        public void windowIconified( WindowEvent e ) {
        }

        public void windowDeiconified( WindowEvent e ) {
        }

        public void windowActivated( WindowEvent e ) {
        }

        public void windowDeactivated( WindowEvent e ) {
        }

    }
}//end of public class
