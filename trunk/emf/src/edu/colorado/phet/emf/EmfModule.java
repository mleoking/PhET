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
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.emf.command.DynamicFieldIsEnabledCmd;
import edu.colorado.phet.emf.command.SetMovementCmd;
import edu.colorado.phet.emf.model.Antenna;
import edu.colorado.phet.emf.model.EmfModel;
import edu.colorado.phet.emf.model.EmfSensingElectron;
import edu.colorado.phet.emf.model.PositionConstrainedElectron;
import edu.colorado.phet.emf.model.movement.ManualMovement;
import edu.colorado.phet.emf.model.movement.SinusoidalMovement;
import edu.colorado.phet.emf.model.movement.MovementType;
import edu.colorado.phet.emf.view.*;
import edu.colorado.phet.util.StripChart;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

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
    private MovementType movementStrategy;
    private boolean showWiggleMe = true;
    private boolean wiggleMeShowing = false;
    private Graphic wiggleMeGraphic;


    public EmfModule( AbstractClock clock ) {
        super( "EMF" );
        super.setModel( new EmfModel( clock ) );

        final Point origin = new Point( 125, 300 );
        Antenna transmittingAntenna = new Antenna( new Point2D.Double( origin.getX(), origin.getY() - 100 ),
                                                   new Point2D.Double( origin.getX(), origin.getY() + 250 ) );
        electronLoc = new Point2D.Double( origin.getX(), origin.getY() );
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
        Graphic electronGraphic = new TransmitterElectronGraphic( apparatusPanel, electron, this );
        this.getApparatusPanel().addGraphic( electronGraphic, 5 );

        // Set up the receiving electron and antenna
        Antenna receivingAntenna = new Antenna( new Point2D.Double( origin.x + 678, electron.getStartPosition().getY() - 50 ),
                                                new Point2D.Double( origin.x + 678, electron.getStartPosition().getY() + 75 ) );
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

        // Draw the animated "Wiggle me"
        wiggleMeGraphic = new Graphic() {
            Point2D.Double start = new Point2D.Double( 0, 0 );
            Point2D.Double stop = new Point2D.Double( origin.getX() - 100, origin.getY() - 10 );
            Point2D.Double current = new Point2D.Double( start.getX(), start.getY() );
            String family = "Sans Serif";
            int style = Font.BOLD;
            int size = 16;
            Font font = new Font( family, style, size );

            public void paint( Graphics2D g ) {
                current.setLocation( ( current.x + ( stop.x - current.x ) * .02 ),
                                     ( current.y + ( stop.y - current.y ) * .04 ) );
                g.setFont( font );
                g.setColor( new Color( 0, 100, 0 ));
                String s1 = "Wiggle the";
                String s2 = "electron!";
                g.drawString( s1, (int)current.getX(), (int)current.getY() - g.getFontMetrics( font ).getHeight() );
                g.drawString( s2, (int)current.getX(), (int)current.getY() );
                Point2D.Double arrowTail = new Point2D.Double(current.getX() + SwingUtilities.computeStringWidth( g.getFontMetrics( font ), s2 ) + 10,
                                                            (int)current.getY() - g.getFontMetrics( font ).getHeight() / 2 );
                Point2D.Double arrowTip = new Point2D.Double( arrowTail.getX() + 15, arrowTail.getY() + 12 );
                Arrow arrow = new Arrow( arrowTail, arrowTip, 6, 6, 2, 100, false );
                g.fill( arrow.getShape() );
            }
        };
        showWiggleMe();
    }

    private void showWiggleMe() {
        if( showWiggleMe && !wiggleMeShowing ) {
            this.addGraphic( wiggleMeGraphic, 5 );
            wiggleMeShowing = true;
        }
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
                                                  new JLabel( "<html>Time<br></html>" ),
                                                  0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
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
                GraphicsUtil.addGridBagComponent( stripChartDlg.getContentPane(),
                                                  new JLabel( "<html>Time<br></html>" ),
                                                  0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
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
        showWiggleMe();
        movementStrategy = sinusoidalMovement;
        this.getModel().execute( new SetMovementCmd( (EmfModel)this.getModel(),
                                                     sinusoidalMovement ) );
    }

    public void setMovementManual() {
//        showWiggleMe();
//        if( showWiggleMe && !wiggleMeShowing ) {
//            apparatusPanel.removeGraphic( wiggleMeGraphic );
//            this.addGraphic( wiggleMeGraphic, 5 );
//            wiggleMeShowing = true;
//        }
        movementStrategy = manualMovement;
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

    public void removeWiggleMeGraphic() {
        apparatusPanel.removeGraphic( wiggleMeGraphic );
        showWiggleMe = false;
    }
}
