package edu.colorado.phet.mazegame;

//import edu.colorado.phet.common.view.util.GraphicsUtil;
//import edu.colorado.phet.common.view.util.graphics.ImageLoader;

import edu.colorado.phet.common.view.util.SimStrings;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Locale;

public class MazeGameApplet extends JApplet {
    public static boolean applet = true;

    // Localization
    public static final String localizedStringsPath = "localization/MazeGameStrings";

    static int fullHeight = 500;	//height and width of applet
    static int fullWidth = 700;		//Must find way to pass in from html
    AudioClip cork, figaro;  //sound effects
    Image ballImage, splat;  //gifs
    JPanel topRowPanel = null;
    JPanel bottomRowPanel = null;

    public static Border raisedBevel, loweredBevel;
    ParticleArena pArena = null;
    
    public void init() {
        if ( applet ) {
            String applicationLocale = Toolkit.getDefaultToolkit().getProperty( "javaws.locale", null );
            if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
                Locale.setDefault( new Locale( applicationLocale ) );
            }
            SimStrings.setStrings( localizedStringsPath );
        }
        
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

            splat = ImageIO.read( getClass().getClassLoader().getResource( "RedBang.gif" ) );
            ballImage = ImageIO.read( getClass().getClassLoader().getResource( "ballsmall2.gif" ) );
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

    public static void main( String[] args ) {
        String applicationLocale = System.getProperty( "javaws.locale" );
        if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
            Locale.setDefault( new Locale( applicationLocale ) );
        }
        String argsKey = "user.language=";
        if( args.length > 0 && args[0].startsWith( argsKey )) {
            String locale = args[0].substring( argsKey.length(), args[0].length() );
            Locale.setDefault( new Locale( locale ));
        }

        SimStrings.setStrings( localizedStringsPath );
        
        MazeGameApplet.applet = false;
        
        JFrame f = new JFrame();
        MazeGameApplet mg = new MazeGameApplet();
        f.setContentPane( mg );
        mg.init();
        f.setSize( 700, 500 );
        centerFrameOnScreen( f );
        f.setVisible( true );
        f.addWindowListener( new Exit() );
    }

    private static void centerFrameOnScreen( JFrame f ) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        int dw = size.width - f.getWidth();
        int dh = size.height - f.getHeight();

        f.setBounds( dw / 2, dh / 2, f.getWidth(), f.getHeight() );
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
