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
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.emf.command.DynamicFieldIsEnabledCmd;
import edu.colorado.phet.emf.command.SetMovementCmd;
import edu.colorado.phet.emf.model.*;
import edu.colorado.phet.emf.model.movement.ManualMovement;
import edu.colorado.phet.emf.model.movement.SinusoidalMovement;
import edu.colorado.phet.emf.view.*;
import edu.colorado.phet.util.StripChart;
import edu.colorado.phet.coreadditions.graphics.DefaultInteractiveGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
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


    public EmfModule() {
        super( "EMF" );

        super.setModel( new EmfModel() );

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
        Image multiElectron = new ImageLoader().loadBufferedImage( "images/blue-sml.gif" );
//        Image multiElectron = new ImageLoader().loadBufferedImage( "images/yellow-electron.gif" );
        ElectronGraphic electronGraphic = new TransmitterElectronGraphic( apparatusPanel, electron, multiElectron, origin );
        electron.addObserver( electronGraphic );
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
        Image image = ImageLoader.fetchBufferedImage( Config.smallElectronImg );
        ElectronGraphic receivingElectronGraphic = new ReceivingElectronGraphic( apparatusPanel, receivingElectron, image );
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

        // todo: HACK!!!
        if( getApparatusPanel() instanceof
                edu.colorado.phet.coreadditions.ApparatusPanel ) {
            this.getModel().addModelElement( new ModelElement() {
                public void stepInTime( double dt ) {
                    ( (edu.colorado.phet.coreadditions.ApparatusPanel)getApparatusPanel() ).updateBufferedGraphic();
                }
            } );
        }

//        getApparatusPanel().addGraphic( new InteractiveGraphic() {
//            Point2D.Double start = new Point2D.Double( 0, 0 );
//            Point2D.Double stop = new Point2D.Double( origin.getX() - 90, origin.getY() + 50 );
//            Point2D.Double current = new Point2D.Double( start.getX(), start.getY() );
//            String family = "Sans Serif";
//            int style = Font.BOLD;
//            int size = 16;
//            Font font = new Font( family, style, size );
//
//            public void paint( Graphics2D g ) {
//                current.setLocation( ( current.x + ( stop.x - current.x ) * .02 ),
//                                     ( current.y + ( stop.y - current.y ) * .04 ) );
//                g.setFont( font );
//                g.drawString( "Wiggle me!", (int)current.getX(), (int)current.getY() );
//            }
//
//            public boolean canHandleMousePress( MouseEvent event, Point2D.Double modelLoc ) {
//                return false;
//            }
//
//            public void mouseDragged( MouseEvent event, Point2D.Double modelLoc ) {
//            }
//
//            public void mousePressed( MouseEvent event, Point2D.Double modelLoc ) {
//            }
//
//            public void mouseReleased( MouseEvent event, Point2D.Double modelLoc ) {
//            }
//
//            public void mouseEntered( MouseEvent event, Point2D.Double modelLoc ) {
//            }
//
//            public void mouseExited( MouseEvent event, Point2D.Double modelLoc ) {
//            }
//        }, 5 );
    }

    public void setAutoscaleEnabled( boolean enabled ) {
        ( (EmfPanel)this.getApparatusPanel() ).setAutoscaleEnabled( enabled );
    }

    public void recenterElectrons() {
//        receivingElectron.setCurrentPosition( new Point( (int)receivingElectronLoc.getX(),
//                                                        (int)receivingElectronLoc.getY() ));
        receivingElectron.recenter();
//        receivingElectron.moveToNewPosition( new Point( (int)receivingElectronLoc.getX(),
//                                                        (int)receivingElectronLoc.getY() ));
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

    public void activate( PhetApplication app ) {
        setControlPanel( new EmfControlPanel( (EmfModel)this.getModel(),
                                              this ) );
//        this.getModel().execute( new SetMovementCmd( (EmfModel)this.getModel(),
//                                                     sinusoidalMovement ) );
    }

    public void deactivate( PhetApplication app ) {
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
