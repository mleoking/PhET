package edu.colorado.phet.electrichockey;

//Mediator applet for Electric edu.colorado.phet.ehockey.HockeyModule

import java.applet.AudioClip;
import java.awt.*;
import java.util.Locale;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.DummyConstantStringTester;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.electrichockey.common.SwingUtils;

//Need File class

public class ElectricHockeyApplication extends JApplet implements Runnable {
    private int width;
    private int height;
    private PlayingField playingField;
    private Model model;
    private FieldGrid fieldGrid;
    private ControlPanel controlPanel;
    private BarrierList barrierList;
    AudioClip tada;
    AudioClip cork;
    Image plusDisk, minusDisk, plusBag, minusBag, positivePuckImage;

    Container pane;
    MyClipLoader mcl;
    public Image negativePuckImage;

    public void init() {
        width = 700;
        height = 600;

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
//        return new PhetAudioClip( name);
        return mcl.loadAudioClip( name );
    }

    public Image getImage( ResourceLoader4 ralf, String name ) {
        return ralf.loadBufferedImage( "electric-hockey/images/" + name );
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

    public void run() {
        tada = getAudioClip( mcl, "electric-hockey/audio/tada.WAV" );
        cork = getAudioClip( mcl, "electric-hockey/audio/cork.au" );
    }

    public boolean isAntialias() {
        return fieldGrid.isAntialias();
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
//                DummyConstantStringTester.setTestScenario( new Locale( "ja" ), "\u30A8\u30CD\u30EB\u30AE\u30FC\u306E\u6642\u9593\u5909\u5316" );
                SimStrings.getInstance().init( args, HockeyConfig.localizedStringPath );
                new PhetLookAndFeel().initLookAndFeel();



                JFrame frame = new JFrame( SimStrings.getInstance().getString( "HockeyApplication.Title" ) + " (" + PhetApplicationConfig.getVersion( "electric-hockey" ).formatForTitleBar() + ")" );
                ElectricHockeyApplication electricHockeyApplication = new ElectricHockeyApplication();
                frame.setContentPane( electricHockeyApplication );
                frame.setSize( 800, 750 );

                electricHockeyApplication.init();

                frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

                SwingUtils.centerWindowOnScreen( frame );
                frame.setVisible( true );

                frame.invalidate();
                frame.repaint();
                frame.validate();
                frame.repaint();
                new PhetLookAndFeel().initLookAndFeel();
            }
        } );
    }

}
