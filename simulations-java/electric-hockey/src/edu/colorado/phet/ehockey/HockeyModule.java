package edu.colorado.phet.ehockey;

//Mediator applet for Electric edu.colorado.phet.ehockey.HockeyModule

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.ehockey.common.SwingUtils;

import javax.swing.*;
import java.applet.AudioClip;
import java.awt.*;
import java.util.Locale;

//Need File class

public class HockeyModule extends JApplet implements Runnable {
    static boolean isApplet = true;
    private int width;
    private int height;
    private PlayingField playingField;
    private Model model;
    private FieldGrid fieldGrid;
    private ControlPanel controlPanel;
    private BarrierList barrierList;
    AudioClip tada, cork;
    Image plusDisk, minusDisk, plusBag, minusBag, positivePuckImage;

    Container pane;
    MyClipLoader mcl;
    public Image negativePuckImage;

    public void init() {
        width = 700;
        height = 600;

        if( isApplet ) {
            String applicationLocale = Toolkit.getProperty( "javaws.locale", null );
            if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
                SimStrings.setLocale( new Locale( applicationLocale ) );
            }
            SimStrings.setStrings( HockeyConfig.localizedStringPath );
        }

        barrierList = new BarrierList( this );
        model = new Model( width, height, this );
        fieldGrid = new FieldGrid( width, height, this );
        controlPanel = new ControlPanel( this );
        playingField = new PlayingField( width, height, this );


        ClassLoader cl = getClass().getClassLoader();
        this.mcl = new MyClipLoader( cl, this );
        ResourceLoader4 ralf = new ResourceLoader4( cl, this );
        new Thread( this ).start();
        plusDisk = getImage( ralf, "plusDisk.gif" );
        minusDisk = getImage( ralf, "minusDisk.gif" );
        plusBag = getImage( ralf, "plusBag.gif" );
        minusBag = getImage( ralf, "minusBag.gif" );
        positivePuckImage = getImage( ralf, "puckPositive.gif" );
        negativePuckImage = getImage( ralf, "puckNegative.gif" );
        pane = getContentPane();
        pane.setLayout( new BorderLayout() );
        pane.add( playingField, BorderLayout.CENTER );

        pane.add( controlPanel, BorderLayout.SOUTH );
    }

    public AudioClip getAudioClip( MyClipLoader mcl, String name ) {
        return mcl.loadAudioClip( name );
    }

    public Image getImage( ResourceLoader4 ralf, String name ) {
        return ralf.loadBufferedImage( name );
    }

    public void paintComponent( Graphics g ) {

    }

    public PlayingField getPlayingField() {
        return playingField;
    }

    public FieldGrid getFieldGrid() {
        return fieldGrid;
    }

    public Model getModel() {
        return model;
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    public static void main( String[] args ) {
        SimStrings.init( args, HockeyConfig.localizedStringPath );

        isApplet = false;

        JFrame frame = new JFrame( SimStrings.get( "HockeyApplication.Title" ) );
        HockeyModule hockeyModule = new HockeyModule();
        frame.setContentPane( hockeyModule );
        frame.setSize( 800, 750 );

        hockeyModule.init();

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
//        frame.addWindowListener( new Exit() );

        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );

        frame.invalidate();
        frame.repaint();
        frame.validate();
        //frame.update();
        frame.repaint();
    }

    public void run() {
        tada = getAudioClip( mcl, "tada.WAV" );
        cork = getAudioClip( mcl, "cork.au" );
    }

    public boolean isAntialias() {
        return fieldGrid.isAntialias();
    }
}
