/**
 * Class: EmfModule Package: edu.colorado.phet.emf Author: Another Guy Date: Jun
 * 25, 2003
 */

package edu.colorado.phet.radiowaves;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.Command;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.radiowaves.command.AddTransmittingElectronCmd;
import edu.colorado.phet.radiowaves.command.DynamicFieldIsEnabledCmd;
import edu.colorado.phet.radiowaves.command.SetMovementCmd;
import edu.colorado.phet.radiowaves.common_1200.graphics.Graphic;
import edu.colorado.phet.radiowaves.common_1200.graphics.HelpItem;
import edu.colorado.phet.radiowaves.coreadditions.PhetControlPanel;
import edu.colorado.phet.radiowaves.model.Antenna;
import edu.colorado.phet.radiowaves.model.EmfModel;
import edu.colorado.phet.radiowaves.model.EmfSensingElectron;
import edu.colorado.phet.radiowaves.model.PositionConstrainedElectron;
import edu.colorado.phet.radiowaves.model.movement.ManualMovement;
import edu.colorado.phet.radiowaves.model.movement.MovementType;
import edu.colorado.phet.radiowaves.model.movement.SinusoidalMovement;
import edu.colorado.phet.radiowaves.util.StripChart;
import edu.colorado.phet.radiowaves.view.*;

public class EmfModule extends PiccoloModule {
    
    final double HELP_LAYER_NUMBER = Double.POSITIVE_INFINITY;
    final double WIGGLE_ME_LAYER_NUMBER = 5;

    private PositionConstrainedElectron electron;
    int fieldWidth = 1000;
    int fieldHeight = 700;
    private EmfSensingElectron receivingElectron;
    private Point2D.Double receivingElectronLoc;
    private Point2D.Double electronLoc;
    private StripChart receiverStripChart;
    private JDialog stripChartDlg;
    private StripChart senderStripChart;
    protected SinusoidalMovement sinusoidalMovement = new SinusoidalMovement( 0.02f, 50f );
    private ManualMovement manualMovement = new ManualMovement();
    private EmfPanel apparatusPanel;
    private MovementType movementStrategy;
    private Graphic wiggleMeGraphic;
    private boolean beenWiggled;
    private final EmfModel model;
    
    private ModelViewTransform2D mvTx;
    private WaveMediumGraphic waveMediumGraphicB;
    private WaveMediumGraphic waveMediumGraphicA;
    private int fieldSense = EmfConfig.SHOW_FORCE_ON_ELECTRON;
    
    private final HelpItem helpItem;


    public EmfModule( IClock clock ) {
        super( RadioWavesResources.getString( "ModuleTitle.EmfModule" ), clock );
        model = new EmfModel( clock );
        super.setModel( model );

        final Point origin = new Point( 125, 300 );

        // Initialize the ModelViewTransform2D
        mvTx = new ModelViewTransform2D( new Point2D.Double( -origin.getX(), -origin.getY() ), new Point2D.Double( fieldWidth - origin.getX(), fieldHeight - origin.getY() ), new Point( 0, 0 ), new Point( fieldWidth, fieldHeight ) );


        Antenna transmittingAntenna = new Antenna( new Point2D.Double( origin.getX(), origin.getY() - 100 ), new Point2D.Double( origin.getX(), origin.getY() + 250 ) );
        electronLoc = new Point2D.Double( origin.getX(), origin.getY() );
        electron = new PositionConstrainedElectron( model, electronLoc, transmittingAntenna );
        new AddTransmittingElectronCmd( model, electron ).doIt();
        new DynamicFieldIsEnabledCmd( model, true ).doIt();

        apparatusPanel = new EmfPanel( electron, origin, fieldWidth, fieldHeight );
        mvTx.addTransformListener( apparatusPanel );
        apparatusPanel.addComponentListener( new ComponentAdapter() {

            boolean init = false;

            public void componentResized( ComponentEvent e ) {
                // The model bounds are the same as the view bounds when the
                // apparatus panel is first displayed
                if ( !init ) {
                    init = true;
                    double aspectRatio = ( (double) fieldHeight ) / ( (double) fieldWidth );
                    mvTx.setModelBounds( new Rectangle2D.Double( 0, 0, apparatusPanel.getWidth(), apparatusPanel.getHeight() ) );
                }
                mvTx.setViewBounds( apparatusPanel.getBounds() );
            }
        } );
        super.setSimulationPanel( apparatusPanel );

        // Set up the electron graphic
        TransmitterElectronGraphic electronGraphic = new TransmitterElectronGraphic( apparatusPanel, electron, this );
        apparatusPanel.addGraphic( electronGraphic, 5 );
        mvTx.addTransformListener( electronGraphic );

        // Set up the receiving electron and antenna
        Antenna receivingAntenna = new Antenna( new Point2D.Double( origin.x + 679, electron.getStartPosition().getY() - 50 ), new Point2D.Double( origin.x + 679, electron.getStartPosition().getY() + 75 ) );
        receivingElectronLoc = new Point2D.Double( origin.x + 680, electron.getStartPosition().getY() );
        receivingElectron = new EmfSensingElectron( model, receivingElectronLoc, electron, receivingAntenna );
        model.execute( new Command() {
            public void doIt() {
                model.addModelElement( receivingElectron );
            }
        } );

        // Load image for small electron
        ElectronGraphic receivingElectronGraphic = new ReceivingElectronGraphic( apparatusPanel, receivingElectron );
        receivingElectron.addObserver( receivingElectronGraphic );
        apparatusPanel.addGraphic( receivingElectronGraphic, 5 );
        mvTx.addTransformListener( receivingElectronGraphic );

        // Create the strip chart
        double dy = ( transmittingAntenna.getMaxY() - transmittingAntenna.getMinY() ) / 2;
        receiverStripChart = new StripChart( 200, 80, 0, 500, electron.getPositionAt( 0 ) + dy, electron.getPositionAt( 0 ) - dy, 1 );
        senderStripChart = new StripChart( 200, 80, 0, 500, electron.getPositionAt( 0 ) + dy, electron.getPositionAt( 0 ) - dy, 1 );

        // Set the control panel
        EmfControlPanel emfControlsPanel = new EmfControlPanel( model, this );
        PhetControlPanel controlPanel = new PhetControlPanel( this, emfControlsPanel );
        setControlPanel( controlPanel );

        // Draw the animated "Wiggle me"
        createWiggleMeGraphic( origin, mvTx );

        // Create the wave medium graphics
        createScalarRepresentations();

        // Create some help items
        helpItem = new HelpItem( RadioWavesResources.getString( "EmfModule.help1" ), origin.getX() + 15, origin.getY() + 10, HelpItem.RIGHT, HelpItem.BELOW );
        helpItem.setForegroundColor( Color.black );
        helpItem.setShadowColor( Color.gray );
        if ( isHelpEnabled() ) {
            apparatusPanel.addGraphic( helpItem, HELP_LAYER_NUMBER );
        }
    }
    
    public boolean hasHelp() {
        return true;
    }

    public void setHelpEnabled( boolean enabled ) {
        super.setHelpEnabled( enabled );
        if ( enabled ) {
            apparatusPanel.addGraphic( helpItem, HELP_LAYER_NUMBER );
        }
        else {
            apparatusPanel.removeGraphic( helpItem );
        }
    }
    private void createScalarRepresentations() {
        waveMediumGraphicA = new WaveMediumGraphic( electron, apparatusPanel, electronLoc, 800, WaveMediumGraphic.TO_RIGHT );
        waveMediumGraphicB = new WaveMediumGraphic( electron, apparatusPanel, electronLoc, 200, WaveMediumGraphic.TO_LEFT );
    }

    public ModelViewTransform2D getMvTx() {
        return mvTx;
    }

    private void createWiggleMeGraphic( final Point origin, final ModelViewTransform2D mvTx ) {
        wiggleMeGraphic = new Graphic() {

            Point2D.Double start = new Point2D.Double( 0, 0 );
            Point2D.Double stop = new Point2D.Double( origin.getX() - 100, origin.getY() - 10 );
            Point2D.Double current = new Point2D.Double( start.getX(), start.getY() );
            Font font = new PhetFont( 16, true );

            public void paint( Graphics2D g ) {
                AffineTransform orgTx = g.getTransform();
                g.transform( mvTx.getAffineTransform() );
                current.setLocation( ( current.x + ( stop.x - current.x ) * .02 ), ( current.y + ( stop.y - current.y ) * .04 ) );
                g.setFont( font );
                g.setColor( new Color( 0, 100, 0 ) );
                String s1 = RadioWavesResources.getString( "EmfModule.Wiggle" );
                String s2 = RadioWavesResources.getString( "EmfModule.Electron" );
                g.drawString( s1, (int) current.getX(), (int) current.getY() - g.getFontMetrics( font ).getHeight() );
                g.drawString( s2, (int) current.getX(), (int) current.getY() );
                Point2D.Double arrowTail = new Point2D.Double( current.getX() + SwingUtilities.computeStringWidth( g.getFontMetrics( font ), s2 ) + 10, (int) current.getY() - g.getFontMetrics( font ).getHeight() / 2 );
                Point2D.Double arrowTip = new Point2D.Double( arrowTail.getX() + 15, arrowTail.getY() + 12 );
                Arrow arrow = new Arrow( arrowTail, arrowTip, 6, 6, 2, 100, false );
                g.fill( arrow.getShape() );
                g.setTransform( orgTx );
            }
        };
        setWiggleMeGraphicState();
    }

    private Timer wiggleMeTimer = new Timer( 10, new ActionListener() {

        public void actionPerformed( ActionEvent e ) {
            apparatusPanel.repaint();
        }
    } );

    private void setWiggleMeGraphicState() {
        if ( wiggleMeGraphic != null ) {
            if ( movementStrategy == manualMovement && !beenWiggled ) {
                apparatusPanel.addGraphic( wiggleMeGraphic, WIGGLE_ME_LAYER_NUMBER );
                wiggleMeTimer.start();
            }
            else {
                apparatusPanel.removeGraphic( wiggleMeGraphic );
                wiggleMeTimer.stop();
            }
        }
    }

    public void setAutoscaleEnabled( boolean enabled ) {
        apparatusPanel.setAutoscaleEnabled( enabled );
    }

    public void recenterElectrons() {
        receivingElectron.recenter();
        electron.moveToNewPosition( new Point( (int) electronLoc.getX(), (int) electronLoc.getY() ) );
    }

    public JDialog setStripChartEnabled( boolean isEnabled ) {
        if ( isEnabled && stripChartDlg == null ) {
            JFrame frame = PhetApplication.instance().getPhetFrame();
            stripChartDlg = new JDialog( frame, false );
            //            stripChartDlg.setUndecorated( true );
            //            stripChartDlg.getRootPane().setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );
            stripChartDlg.getContentPane().setLayout( new GridBagLayout() );
            new StripChartDelegate( receivingElectron, receiverStripChart );
            new StripChartDelegate( electron, senderStripChart );
            stripChartDlg.setTitle( RadioWavesResources.getString( "EmfModule.ChartTitle" ) );

            try {
                int rowIdx = 0;
                SwingUtils.addGridBagComponent( stripChartDlg.getContentPane(), new JLabel( RadioWavesResources.getString( "EmfModule.Transmitter" ) ), 0, rowIdx++, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST );
                SwingUtils.addGridBagComponent( stripChartDlg.getContentPane(), senderStripChart, 0, rowIdx++, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST );
                SwingUtils.addGridBagComponent( stripChartDlg.getContentPane(), new JLabel( RadioWavesResources.getString( "EmfModule.TimeLabel" ) ), 0, rowIdx++, 1, 1, GridBagConstraints.NONE, GridBagConstraints.CENTER );
                SwingUtils.addGridBagComponent( stripChartDlg.getContentPane(), new JLabel( RadioWavesResources.getString( "EmfModule.Receiver" ) ), 0, rowIdx++, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST );
                SwingUtils.addGridBagComponent( stripChartDlg.getContentPane(), receiverStripChart, 0, rowIdx++, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST );
                SwingUtils.addGridBagComponent( stripChartDlg.getContentPane(), new JLabel( RadioWavesResources.getString( "EmfModule.TimeLabel" ) ), 0, rowIdx++, 1, 1, GridBagConstraints.NONE, GridBagConstraints.CENTER );
            }
            catch ( AWTException e ) {
                e.printStackTrace();
            }

            //            stripChartDlg.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
            stripChartDlg.pack();
            SwingUtils.centerDialogInParent( stripChartDlg );

        }

        if ( stripChartDlg != null ) {
            stripChartDlg.setVisible( isEnabled );
        }
        return stripChartDlg;
    }

    public void setMovementSinusoidal() {
        setWiggleMeGraphicState();
        movementStrategy = sinusoidalMovement;
        model.execute( new SetMovementCmd( model, sinusoidalMovement ) );
        setWiggleMeGraphicState();
    }

    public void setMovementManual() {
        movementStrategy = manualMovement;
        model.execute( new SetMovementCmd( model, manualMovement ) );
        setWiggleMeGraphicState();
    }

    public void displayStaticField( boolean display ) {
        apparatusPanel.displayStaticField( display );
    }

    public void displayDynamicField( boolean display ) {
        apparatusPanel.displayDynamicField( display );
    }

    public void setUseBufferedImage( boolean selected ) {
        apparatusPanel.setUseBufferedImage( selected );
    }

    public void setFieldSense( int fieldSense ) {
        this.fieldSense = fieldSense;
        apparatusPanel.setFieldSense( fieldSense );
        Color color = null;
        if ( fieldSense == EmfConfig.SHOW_ELECTRIC_FIELD ) {
            color = EmfConfig.FIELD_COLOR;
        }
        if ( fieldSense == EmfConfig.SHOW_FORCE_ON_ELECTRON ) {
            color = EmfConfig.FORCE_COLOR;
        }
        if ( color != null ) {
            waveMediumGraphicA.setMaxAmplitudeColor( color );
            waveMediumGraphicB.setMaxAmplitudeColor( color );
        }
        else {
            throw new RuntimeException( "invalid fieldSense" );
        }

        // Causes screen to repaint if the clock is paused
        model.updateWhileClockIsPaused();
    }

    public int getFieldSense() {
        return fieldSense;
    }

    public void setFieldDisplay( int display ) {
        apparatusPanel.setFieldDisplay( display );
        // This makes the display refresh if the clock is paused
        model.updateWhileClockIsPaused();
    }

    public void setCurveVisible( boolean isVisible ) {
        apparatusPanel.setCurveVisible( isVisible );
    }

    public void setSingleVectorRowRepresentation( Object representation ) {
        if ( representation == EmfConfig.SINGLE_VECTOR_ROW_CENTERED ) {
            EmfConfig.SINGLE_VECTOR_ROW_OFFSET = 0.5;
        }
        else if ( representation == EmfConfig.SINGLE_VECTOR_ROW_PINNED ) {
            EmfConfig.SINGLE_VECTOR_ROW_OFFSET = 0;
        }
        else {
            throw new RuntimeException( "invalid representation" );
        }
    }

    public void removeWiggleMeGraphic() {
        apparatusPanel.removeGraphic( wiggleMeGraphic );
        beenWiggled = true;
    }

    public void showMegaHelp() {
        final JDialog imageFrame = new JDialog( PhetApplication.instance().getPhetFrame(), false );
        try {
            final BufferedImage image = ImageLoader.loadBufferedImage( "radio-waves/images/emf.gif" );
            final JPanel panel = new JPanel() {

                public void paint( Graphics g ) {
                    g.setColor( Color.white );
                    g.fillRect( 0, 0, image.getWidth(), image.getHeight() );
                    g.drawImage( image, 0, 0, null );
                }
            };
            panel.setPreferredSize( new Dimension( image.getWidth(), image.getHeight() ) );

            JScrollPane scrollPane = new JScrollPane( panel );
            AdjustmentListener adjustmentListener = new AdjustmentListener() {

                public void adjustmentValueChanged( AdjustmentEvent e ) {
                    panel.repaint();
                }
            };
            scrollPane.getHorizontalScrollBar().addAdjustmentListener( adjustmentListener );
            scrollPane.getVerticalScrollBar().addAdjustmentListener( adjustmentListener );
            scrollPane.setPreferredSize( new Dimension( image.getWidth(), 500 ) );
            imageFrame.setContentPane( scrollPane );
            imageFrame.pack();
            imageFrame.setVisible( true );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }


    static ModelViewTransform2D create( Point2D.Double mp1, Point2D.Double mp2, Point vp1, Point vp2 ) {
        Rectangle2D.Double mr = new Rectangle2D.Double( mp1.getX(), mp1.getY(), mp2.getX() - mp1.getX(), mp2.getY() - mp1.getY() );
        Rectangle vr = new Rectangle( (int) vp1.getX(), (int) vp1.getY(), (int) ( vp2.getX() - vp1.getX() ), (int) ( vp2.getY() - vp1.getY() ) );
        return new ModelViewTransform2D( mr, vr );
    }

    public void setScalarRepEnabled( boolean enabled ) {
        if ( enabled ) {
            apparatusPanel.addGraphic( waveMediumGraphicA, 1E5 );
            apparatusPanel.addGraphic( waveMediumGraphicB, 1E5 );
        }
        else if ( waveMediumGraphicA != null ) {
            apparatusPanel.removeGraphic( waveMediumGraphicA );
            apparatusPanel.removeGraphic( waveMediumGraphicB );
            waveMediumGraphicA.setVisible( false );
            waveMediumGraphicB.setVisible( false );
        }
    }
}
