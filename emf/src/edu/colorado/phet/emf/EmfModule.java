/**
 * Class: EmfModule
 * Package: edu.colorado.phet.emf
 * Author: Another Guy
 * Date: Jun 25, 2003
 */
package edu.colorado.phet.emf;

import edu.colorado.phet.command.AddTransmittingElectronCmd;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.ShapeGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.emf.command.DynamicFieldIsEnabledCmd;
import edu.colorado.phet.emf.command.SetMovementCmd;
import edu.colorado.phet.emf.model.Antenna;
import edu.colorado.phet.emf.model.EmfModel;
import edu.colorado.phet.emf.model.EmfSensingElectron;
import edu.colorado.phet.emf.model.PositionConstrainedElectron;
import edu.colorado.phet.emf.model.movement.ManualMovement;
import edu.colorado.phet.emf.model.movement.SinusoidalMovement;
import edu.colorado.phet.emf.view.*;
import edu.colorado.phet.util.StripChart;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;
import java.io.IOException;

public class EmfModule extends Module {

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


    public EmfModule( AbstractClock clock ) {
        super( "EMF" );

        super.setModel( new EmfModel( clock ) );

//        final Point origin = new Point( 129, 250 );
        final Point origin = new Point( 125, 250 );

        Antenna transmittingAntenna = new Antenna( new Point2D.Double( origin.getX(), origin.getY() - 100 ),
                                                   new Point2D.Double( origin.getX(), origin.getY() + 250 ) );
        electronLoc = new Point2D.Double( origin.getX(), origin.getY() + 50 );
        electron = new PositionConstrainedElectron( (EmfModel)this.getModel(),
                                                    electronLoc,
                                                    transmittingAntenna );
        new AddTransmittingElectronCmd( (EmfModel)this.getModel(), electron ).doIt();
        new DynamicFieldIsEnabledCmd( (EmfModel)getModel(), true ).doIt();

        apparatusPanel = new EmfPanel( (EmfModel)this.getModel(),
                                       electron,
                                       origin,
                                       fieldWidth,
                                       fieldHeight );
        super.setApparatusPanel( apparatusPanel );

        // Set up the electron graphic
        Graphic electronGraphic = new TransmitterElectronGraphic( apparatusPanel, electron, origin );
        this.getApparatusPanel().addGraphic( electronGraphic, 5 );

        // Set up the receiving electron and antenna
        Antenna receivingAntenna = new Antenna( new Point2D.Double( origin.x + 680, electron.getStartPosition().getY() - 50 ),
                                                new Point2D.Double( origin.x + 680, electron.getStartPosition().getY() + 75 ) );
        receivingElectronLoc = new Point2D.Double( origin.x + 680, electron.getStartPosition().getY() );
        receivingElectron = new EmfSensingElectron(
                (EmfModel)this.getModel(), receivingElectronLoc, electron,
                receivingAntenna );
        getModel().execute( new Command() {
            public void doIt() {
                getModel().addModelElement( receivingElectron );
            }
        } );

        // Load image for small electron
        Image image = null;
        try {
            image = ImageLoader.loadBufferedImage( Config.smallElectronImg );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        ElectronGraphic receivingElectronGraphic = new ReceivingElectronGraphic( apparatusPanel, receivingElectron );
        receivingElectron.addObserver( receivingElectronGraphic );
        this.getApparatusPanel().addGraphic( receivingElectronGraphic, 5 );

        // Create the strip chart
        double dy = ( transmittingAntenna.getMaxY() - transmittingAntenna.getMinY() ) / 2;
        receiverStripChart = new StripChart( 200, 80,
                                             0, 500,
                                             electron.getPositionAt( 0 ) + dy,
                                             electron.getPositionAt( 0 ) - dy,
                                             1 );
        senderStripChart = new StripChart( 200, 80,
                                           0, 500,
                                           electron.getPositionAt( 0 ) + dy,
                                           electron.getPositionAt( 0 ) - dy,
                                           1 );

        // Set the control panel
        setControlPanel( new EmfControlPanel( (EmfModel)this.getModel(),
                                              this ) );


//        Ellipse2D.Double s = new Ellipse2D.Double();
//        s.setFrameFromCenter( origin.getX(), origin.getY(), origin.getX() + 5, origin.getY() + 5);
//        getApparatusPanel().addGraphic( new ShapeGraphic( s, Color.yellow ));
//

    }

    public void setAutoscaleEnabled( boolean enabled ) {
        ( (EmfPanel)this.getApparatusPanel() ).setAutoscaleEnabled( enabled );
    }

    public void recenterElectrons() {
        receivingElectron.recenter();
        electron.moveToNewPosition( new Point( (int)electronLoc.getX(),
                                               (int)electronLoc.getY() ) );
    }

    public void setStripChartEnabled( boolean isEnabled ) {
        if( isEnabled && stripChartDlg == null ) {
            JFrame frame = PhetApplication.instance().getApplicationView().getPhetFrame();
            stripChartDlg = new JDialog( frame );
            stripChartDlg.setUndecorated( true );
            stripChartDlg.getRootPane().setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );
            stripChartDlg.getContentPane().setLayout( new GridBagLayout() );
            new StripChartDelegate( receivingElectron, receiverStripChart );
            new StripChartDelegate( electron, senderStripChart );
            stripChartDlg.setTitle( "Electron Positions" );

            try {
                int rowIdx = 0;
                GraphicsUtil.addGridBagComponent( stripChartDlg.getContentPane(),
                                                  new JLabel( "Transmitter" ),
                                                  0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.WEST );
                GraphicsUtil.addGridBagComponent( stripChartDlg.getContentPane(),
                                                  senderStripChart,
                                                  0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.WEST );
                GraphicsUtil.addGridBagComponent( stripChartDlg.getContentPane(),
                                                  new JLabel( "Receiver" ),
                                                  0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.WEST );
                GraphicsUtil.addGridBagComponent( stripChartDlg.getContentPane(),
                                                  receiverStripChart,
                                                  0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.WEST );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }

            stripChartDlg.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
            stripChartDlg.pack();
            GraphicsUtil.centerDialogInParent( stripChartDlg );

        }

        if( stripChartDlg != null ) {
            stripChartDlg.setVisible( isEnabled );
        }
    }

    public void setMovementSinusoidal() {
        this.getModel().execute( new SetMovementCmd( (EmfModel)this.getModel(),
                                                     sinusoidalMovement ) );
    }

    public void setMovementManual() {
        this.getModel().execute( new SetMovementCmd( (EmfModel)this.getModel(),
                                                     manualMovement ) );
    }

    public void displayStaticField( boolean display ) {
        ( (EmfPanel)getApparatusPanel() ).displayStaticField( display );
    }

    public void displayDynamicField( boolean display ) {
        ( (EmfPanel)getApparatusPanel() ).displayDynamicField( display );
    }

    public void setUseBufferedImage( boolean selected ) {
        ( (EmfPanel)getApparatusPanel() ).setUseBufferedImage( selected );
    }

    public void setFieldSense( int fieldSense ) {
        apparatusPanel.setFieldSense( fieldSense );
    }

    public void setFieldDisplay( int display ) {
        apparatusPanel.setFieldDisplay( display );
    }
}
