package edu.colorado.phet.electrichockey;

//Mediator applet for Electric edu.colorado.phet.ehockey.HockeyModule

import java.applet.AudioClip;
import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetAudioClip;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.electrichockey.common.SwingUtils;

//Need File class

public class ElectricHockeyApplication extends JPanel implements Runnable {
    private int width;
    private int height;
    private PlayingField playingField;
    private Model model;
    private FieldGrid fieldGrid;
    private ControlPanel controlPanel;
    private BarrierList barrierList;
    private PhetAudioClip tada;
    private PhetAudioClip cork;
    private Image plusDisk, minusDisk, plusBag, minusBag, positivePuckImage;

    private Container pane;
    public Image negativePuckImage;

    public void init() {
        width = 700;
        height = 600;

        barrierList = new BarrierList( this );
        model = new Model( width, height, this );
        fieldGrid = new FieldGrid( width, height, this );
        controlPanel = new ControlPanel( this );
        playingField = new PlayingField( width, height, this );

        new Thread( this ).start();
        plusDisk = getImage( "plusDisk.gif" );
        minusDisk = getImage( "minusDisk.gif" );
        plusBag = getImage( "plusBag.gif" );
        minusBag = getImage( "minusBag.gif" );
        positivePuckImage = getImage( "puckPositive.gif" );
        negativePuckImage = getImage( "puckNegative.gif" );
        pane = this;
        pane.setLayout( new BorderLayout() );
        pane.add( playingField, BorderLayout.CENTER );

        pane.add( controlPanel, BorderLayout.SOUTH );
    }

    public Image getImage( String name ) {
        return new PhetResources( "electric-hockey" ).getImage( name );
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
        tada = new PhetAudioClip( "electric-hockey/audio/tada.WAV" );
        cork = new PhetAudioClip( "electric-hockey/audio/cork.au" );
    }

    public boolean isAntialias() {
        return fieldGrid.isAntialias();
    }

    public Image getPlusBag() {
        return plusBag;
    }

    public Image getMinusBag() {
        return minusBag;
    }

    public Image getMinusDisk() {
        return minusDisk;
    }

    public Image getPlusDisk() {
        return plusDisk;
    }

    public Image getPositivePuckImage() {
        return positivePuckImage;
    }

    public Image getNegativePuckImage() {
        return negativePuckImage;
    }

    public PhetAudioClip getCork() {
        return cork;
    }

    public PhetAudioClip getTada() {
        return tada;
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                SimStrings.getInstance().init( args, HockeyConfig.localizedStringPath );
                new PhetLookAndFeel().initLookAndFeel();


                JFrame frame = new JFrame( ElectricHockeyStrings.getString( "HockeyApplication.Title" ) + " (" + PhetApplicationConfig.getVersion( "electric-hockey" ).formatForTitleBar() + ")" );
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
