/**
 * Class: IdealGasModule
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.collision.SphereBoxExpert;
import edu.colorado.phet.collision.SphereSphereExpert;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.util.EventChannelProxy;
import edu.colorado.phet.common.util.EventRegistry;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.PhetControlPanel;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.help.HelpItem;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.PressureSlice;
import edu.colorado.phet.idealgas.controller.command.RemoveMoleculeCmd;
import edu.colorado.phet.idealgas.model.Gravity;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.PressureSensingBox;
import edu.colorado.phet.idealgas.model.Pump;
import edu.colorado.phet.idealgas.view.*;
import edu.colorado.phet.idealgas.view.monitors.*;
import edu.colorado.phet.instrumentation.Thermometer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;

public class IdealGasModule extends Module implements EventChannel {

    private IdealGasModel idealGasModel;
    private PressureSensingBox box;
    private Gravity gravity;
    private Pump pump;
    private CmLines cmLines;
    private static WiggleMeGraphic wiggleMeGraphic;

    private PressureSlice pressureSlice;
    private AbstractClock clock;
    private PressureSliceGraphic pressureSliceGraphic;
    private DefaultInteractiveGraphic rulerGraphic;
    private EnergyHistogramDialog histogramDlg;
    private ArrayList visibleInstruments = new ArrayList();
    private JDialog measurementDlg;
    private JDialog speciesMonitorDlg;
    private IdealGasControlPanel idealGasControlPanel;
    private Thermometer thermometer;
    private EventRegistry eventRegistry = new EventRegistry();
    private BufferedImage basePumpImg;
    private BufferedImage currentPumpImg;
    private BufferedImage bluePumpImg;
    private BufferedImage redPumpImg;
    private PhetImageGraphic pumpGraphic;
    private JDialog averagingControlDlg;
    private Box2DGraphic boxGraphic;


    public IdealGasModule( AbstractClock clock ) {
        this( clock, SimStrings.get( "ModuleTitle.IdealGas" ) );
        this.clock = clock;
    }

    protected IdealGasModel getIdealGasModel() {
        return (IdealGasModel)getModel();
    }

    public IdealGasModule( AbstractClock clock, String name ) {
        super( name );

        // Create the model
        idealGasModel = new IdealGasModel( clock.getDt() );
        setModel( idealGasModel );

        gravity = new Gravity( idealGasModel );
        idealGasModel.addModelElement( gravity );

        // Add collision experts
        idealGasModel.addCollisionExpert( new SphereSphereExpert( idealGasModel, clock.getDt() ) );
        idealGasModel.addCollisionExpert( new SphereBoxExpert( idealGasModel ) );

        // Create the box
        double xOrigin = 132 + IdealGasConfig.X_BASE_OFFSET;
        double yOrigin = 252 + IdealGasConfig.Y_BASE_OFFSET;
        double xDiag = 434 + IdealGasConfig.X_BASE_OFFSET;
        double yDiag = 497 + IdealGasConfig.Y_BASE_OFFSET;
        box = new PressureSensingBox( new Point2D.Double( xOrigin, yOrigin ),
                                      new Point2D.Double( xDiag, yDiag ), idealGasModel, clock );
        idealGasModel.addBox( box );
        setApparatusPanel( new BaseIdealGasApparatusPanel( this, box ) );

        // Add the pressure gauge
        PressureSlice gaugeSlice = new PressureSlice( box, idealGasModel, clock );
        gaugeSlice.setTimeAveragingWindow( 2500 );
        gaugeSlice.setUpdateContinuously( true );
        gaugeSlice.setY( box.getMinY() + 50 );
        idealGasModel.addModelElement( gaugeSlice );
        PressureDialGauge pressureGauge = new PressureDialGauge( box, getApparatusPanel(), gaugeSlice );
        addGraphic( pressureGauge, 20 );

        // Add the thermometer
        double thermometerHeight = 100;
        Point2D.Double thermometerLoc = new Point2D.Double( box.getMaxX() - 30, box.getMinY() - thermometerHeight );
        thermometer = new IdealGasThermometer( getApparatusPanel(),
                                               idealGasModel, thermometerLoc,
                                               thermometerHeight, 10, true, 0, 1000E3 );
        addGraphic( thermometer, 8 );

        // Create the pump
        pump = new Pump( this, box );

        // Set up the graphics for the pump
        try {
            basePumpImg = ImageLoader.loadBufferedImage( IdealGasConfig.PUMP_IMAGE_FILE );
            BufferedImage handleImg = ImageLoader.loadBufferedImage( IdealGasConfig.HANDLE_IMAGE_FILE );
            PhetImageGraphic handleGraphic = new PhetImageGraphic( getApparatusPanel(), handleImg );

            PumpHandleGraphic handleGraphicImage = new PumpHandleGraphic( pump, handleGraphic,
                                                                          IdealGasConfig.X_BASE_OFFSET + 578, IdealGasConfig.Y_BASE_OFFSET + 238,
                                                                          IdealGasConfig.X_BASE_OFFSET + 578, IdealGasConfig.Y_BASE_OFFSET + 100,
                                                                          IdealGasConfig.X_BASE_OFFSET + 578, IdealGasConfig.Y_BASE_OFFSET + 238 );

            bluePumpImg = new BufferedImage( basePumpImg.getWidth(), basePumpImg.getHeight(), BufferedImage.TYPE_INT_ARGB );
            redPumpImg = new BufferedImage( basePumpImg.getWidth(), basePumpImg.getHeight(), BufferedImage.TYPE_INT_ARGB );
            BufferedImageOp blueOp = new MakeDuotoneImageOp( Color.blue );
            blueOp.filter( basePumpImg, bluePumpImg );
            BufferedImageOp redOp = new MakeDuotoneImageOp( Color.red );
            redOp.filter( basePumpImg, redPumpImg );
            currentPumpImg = bluePumpImg;

            this.addGraphic( handleGraphicImage, -6 );
            pumpGraphic = new PhetImageGraphic( getApparatusPanel(), currentPumpImg, IdealGasConfig.X_BASE_OFFSET + 436, IdealGasConfig.Y_BASE_OFFSET + 253 );
//            PhetImageGraphic pumpGraphic = new PhetImageGraphic( getApparatusPanel(), basePumpImg, IdealGasConfig.X_BASE_OFFSET + 436, IdealGasConfig.Y_BASE_OFFSET + 253 );
            this.addGraphic( pumpGraphic, -4 );

            if( wiggleMeGraphic == null ) {
                wiggleMeGraphic = new WiggleMeGraphic( getApparatusPanel(),
                                                       new Point2D.Double( IdealGasConfig.X_BASE_OFFSET + 470, IdealGasConfig.Y_BASE_OFFSET + 200 ),
                                                       getModel() );
                addGraphic( wiggleMeGraphic, 40 );
                wiggleMeGraphic.start();
            }
            pump.addObserver( new SimpleObserver() {
                public void update() {
                    if( wiggleMeGraphic != null ) {
                        wiggleMeGraphic.kill();
                        getApparatusPanel().removeGraphic( wiggleMeGraphic );
                        wiggleMeGraphic = null;
                        pump.removeObserver( this );
                    }
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }


        // Set up the box
        boxGraphic = new Box2DGraphic( getApparatusPanel(), box );
        addGraphic( boxGraphic, 10 );

        // Add the animated mannequin
        Mannequin pusher = new Mannequin( getApparatusPanel(), idealGasModel, box, boxGraphic );
        addGraphic( pusher, 10 );

        // Set up the control panel
        idealGasControlPanel = new IdealGasControlPanel( this );
        PhetControlPanel controlPanel = new PhetControlPanel( this, idealGasControlPanel );
        setControlPanel( controlPanel );

        // Place a slider to control the stove
        StoveControlPanel stoveControlPanel = new StoveControlPanel( this );
        stoveControlPanel.setBounds( IdealGasConfig.X_BASE_OFFSET + IdealGasConfig.X_STOVE_OFFSET + 80,
                                     IdealGasConfig.Y_BASE_OFFSET + IdealGasConfig.Y_STOVE_OFFSET - 30, 300, 120 );
        getApparatusPanel().add( stoveControlPanel );

        // Add buttons for selecting the species that the pump will produce
        PumpSpeciesSelectorPanel pumpSelectorPanel = new PumpSpeciesSelectorPanel( this );
        pumpSelectorPanel.setBounds( IdealGasConfig.X_BASE_OFFSET + 590, IdealGasConfig.Y_BASE_OFFSET + 300,
                                     200, 150 );
        getApparatusPanel().add( pumpSelectorPanel );

        // Add help items
        addHelp();
    }

    private void addHelp() {
        HelpItem helpItem1 = new HelpItem( "Wall can be moved\nleft and right",
                                           box.getPosition().getX(), box.getPosition().getY(),
                                           HelpItem.BELOW, HelpItem.LEFT );
        helpItem1.setForegroundColor( IdealGasConfig.helpColor );
        addHelpItem( helpItem1 );
        HelpItem helpItem2 = new HelpItem( "Door can be slid\nleft and right",
                                           box.getPosition().getX() + 100, box.getPosition().getY() - 50 );
        helpItem2.setForegroundColor( IdealGasConfig.helpColor );
        addHelpItem( helpItem2 );
        HelpItem helpItem3 = new HelpItem( "Heat can be removed or added\nby adjusting stove",
                                           box.getPosition().getX() + 50, box.getMaxY() + 50 );
        helpItem3.setForegroundColor( IdealGasConfig.helpColor );
        addHelpItem( helpItem3 );
    }

    public JDialog setMeasurementDlgVisible( boolean isVisible ) {
        if( measurementDlg == null ) {
            measurementDlg = new MeasurementDialog( PhetApplication.instance().getApplicationView().getPhetFrame(), this );
            JFrame frame = PhetApplication.instance().getApplicationView().getPhetFrame();
            measurementDlg.setLocationRelativeTo( frame );
            measurementDlg.setLocation( (int)( frame.getLocation().getX() + frame.getWidth() * 3 / 5 ),
                                        (int)frame.getLocation().getY() + 20 );
        }
        measurementDlg.setVisible( isVisible );
        if( isVisible ) {
            visibleInstruments.add( measurementDlg );
        }
        else {
            visibleInstruments.remove( measurementDlg );
        }
        return measurementDlg;
    }

    public JDialog setSpeciesMonitorDlgEnabled( boolean isEnabled ) {
        if( speciesMonitorDlg == null ) {
            speciesMonitorDlg = new SpeciesMonitorDialog( PhetApplication.instance().getApplicationView().getPhetFrame(),
                                                          idealGasModel );
        }
        speciesMonitorDlg.setVisible( isEnabled );
        if( isEnabled ) {
            visibleInstruments.add( speciesMonitorDlg );
        }
        else {
            visibleInstruments.remove( speciesMonitorDlg );
        }
        return speciesMonitorDlg;
    }

    public void setCurrentSpecies( Class moleculeClass ) {
        pump.setCurrentGasSpecies( moleculeClass );
    }

    public void setCmLinesOn( boolean cmLinesOn ) {
        if( cmLines == null ) {
            cmLines = new CmLines( getApparatusPanel(), idealGasModel );
        }
        if( cmLinesOn ) {
            addGraphic( cmLines, 20 );
        }
        else {
            getApparatusPanel().removeGraphic( cmLines );
        }

    }

    public void setStove( int value ) {
        idealGasModel.setHeatSource( (double)value );
        ( (BaseIdealGasApparatusPanel)getApparatusPanel() ).setStove( value );
    }

    public void setGravity( double value ) {
        gravity.setAmt( value );
    }

    public void pumpGasMolecules( int numMolecules ) {
        pump.pump( numMolecules );
    }

    public void removeGasMolecule() {
        Command cmd = new RemoveMoleculeCmd( idealGasModel, pump.getCurrentGasSpecies() );
        cmd.doIt();
    }

    public void removeGasMolecule( Class species ) {
        Command cmd = new RemoveMoleculeCmd( idealGasModel, species );
        cmd.doIt();
    }

    protected PressureSensingBox getBox() {
        return box;
    }

    public Pump getPump() {
        return pump;
    }

    public Thermometer getThermomenter() {
        return thermometer;
    }

    /**
     * @param pressureSliceEnabled
     */
    public void setPressureSliceEnabled( boolean pressureSliceEnabled ) {
        if( pressureSlice == null ) {
            pressureSlice = new PressureSlice( getBox(), (IdealGasModel)getModel(), clock );
            pressureSlice.setUpdateContinuously( false );
            pressureSliceGraphic = new PressureSliceGraphic( getApparatusPanel(),
                                                             pressureSlice,
                                                             getBox() );

            // Create a nonmodal dialog with controls for the averaging times of the pressure slice
            final JTextField aveTimeTF = new JTextField( 3 );
            aveTimeTF.setHorizontalAlignment( JTextField.RIGHT );
//            aveTimeTF.addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent e ) {
//                    pressureSlice.setTimeAveragingWindow( Double.parseDouble( aveTimeTF.getText() ) * 1000 );
//                }
//            } );
            aveTimeTF.setText( Double.toString( pressureSlice.getTimeAveragingWindow() / 1000 ));

            JButton setTimeBtn = new JButton( "Set");
            setTimeBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    pressureSlice.setTimeAveragingWindow( Double.parseDouble( aveTimeTF.getText() ) * 1000 );
                }
            } );

            averagingControlDlg = new JDialog( PhetApplication.instance().getApplicationView().getPhetFrame(),
                                               "Averaging time", false );
            averagingControlDlg.setUndecorated( true );
            averagingControlDlg.getRootPane().setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );
            averagingControlDlg.set
            Container ctrlPane = averagingControlDlg.getContentPane();
            ctrlPane.setLayout( new GridBagLayout() );
            GridBagConstraints gbc = new GridBagConstraints( GridBagConstraints.RELATIVE, 0,
                                                             1, 1, 1, 1,
                                                             GridBagConstraints.CENTER,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );
            ctrlPane.add( aveTimeTF, gbc );
            ctrlPane.add( new JLabel( "sec" ), gbc );
            gbc.insets = new Insets( 0, 10, 0, 0);
            ctrlPane.add( setTimeBtn, gbc );

            averagingControlDlg.pack();
            final Rectangle graphicBounds = ((PhetShapeGraphic)pressureSliceGraphic.getGraphic()).getBounds();
            Rectangle frameBounds = PhetApplication.instance().getApplicationView().getPhetFrame().getBounds();
            Rectangle bounds = averagingControlDlg.getBounds();
            averagingControlDlg.setBounds( frameBounds.x + (int)graphicBounds.getMinX() +10,
                                                   (int)((PhetGraphic)boxGraphic.getGraphic()).getY() + frameBounds.y,
                                                   (int)bounds.getWidth(), (int)bounds.getHeight() );

        }
        if( pressureSliceEnabled ) {
            getModel().addModelElement( pressureSlice );
            addGraphic( pressureSliceGraphic, 20 );
            averagingControlDlg.setVisible( true );

            JPanel placementPane = new JPanel( null );
            placementPane.setOpaque( false );
            placementPane.add(new JLabel("FFFFF") );
            Rectangle bounds = ((PhetShapeGraphic)pressureSliceGraphic.getGraphic()).getBounds();
            placementPane.setBounds( bounds.x-100, bounds.y, 100, 50);
            getApparatusPanel().add( placementPane );

        }
        else {
            getApparatusPanel().removeGraphic( pressureSliceGraphic );
            getModel().removeModelElement( pressureSlice );
            averagingControlDlg.setVisible( false );
        }
    }

    /**
     * @param rulerEnabled
     */
    public void setRulerEnabed( boolean rulerEnabled ) {
        if( rulerGraphic == null ) {
            rulerGraphic = new RulerGraphic( getApparatusPanel() );
        }
        if( rulerEnabled ) {
            getApparatusPanel().addGraphic( rulerGraphic, Integer.MAX_VALUE );
        }
        else {
            getApparatusPanel().removeGraphic( rulerGraphic );
        }
        getApparatusPanel().revalidate();
        getApparatusPanel().paintImmediately( 0, 0,
                                              (int)getApparatusPanel().getBounds().getWidth(),
                                              (int)getApparatusPanel().getBounds().getHeight() );
    }

    public JDialog setHistogramDlgEnabled( boolean histogramDlgEnabled ) {
        if( histogramDlg == null ) {
            histogramDlg = new EnergyHistogramDialog( PhetApplication.instance().getApplicationView().getPhetFrame(),
                                                      (IdealGasModel)getModel() );
        }
        histogramDlg.setVisible( histogramDlgEnabled );
        if( histogramDlgEnabled ) {
            visibleInstruments.add( histogramDlg );
        }
        else {
            visibleInstruments.remove( histogramDlg );
        }
        return histogramDlg;
    }

    public void activate( PhetApplication app ) {
        super.activate( app );
        for( int i = 0; i < visibleInstruments.size(); i++ ) {
            Component component = (Component)visibleInstruments.get( i );
            component.setVisible( true );
        }


        // FOR DEBUG. displays the total energy in the system
//        TotalEnergyMonitor tem = new TotalEnergyMonitor( null, idealGasModel );
//        tem.setVisible( true );
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        for( int i = 0; i < visibleInstruments.size(); i++ ) {
            Component component = (Component)visibleInstruments.get( i );
            component.setVisible( false );
        }
    }

    protected IdealGasControlPanel getIdealGasControlPanel() {
        return idealGasControlPanel;
    }

    //-----------------------------------------------------
    // Event handling
    //-----------------------------------------------------
    EventChannelProxy resetEventChannel = new EventChannelProxy( ResetListener.class );
    ResetListener resetListenersProxy = (ResetListener)resetEventChannel.getProxy();

    public void reset() {
        getIdealGasModel().removeAllMolecules();
        resetListenersProxy.resetOccurred( new ResetEvent( this ) );
//        eventRegistry.fireEvent( new ResetEvent( this ) );
    }

    public void addListener( EventListener listener ) {
        resetEventChannel.addListener( listener );
//        eventRegistry.addListener( listener );
    }

    public void removeListener( EventListener listener ) {
        resetEventChannel.removeListener( listener );
//        eventRegistry.removeListener( listener );
    }

    public void removeAllListeners() {
        resetEventChannel.removeAllListeners();
//        eventRegistry.removeAllListeners();
    }

//    public void fireEvent( EventObject event ) {
//        eventRegistry.fireEvent( event );
//    }

//    public int getNumListeners() {
//        return eventRegistry.getNumListeners();
//    }

    public void setCurrentPumpImage( Color color ) {
        if( color.equals( Color.blue ) ) {
            pumpGraphic.setImage( bluePumpImg );
        }
        if( color.equals( Color.red ) ) {
            pumpGraphic.setImage( redPumpImg );
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Inner classes
    //

    public interface ResetListener extends EventListener {
        void resetOccurred( ResetEvent event );
    }

    public class ResetEvent extends EventObject {
        public ResetEvent( Object source ) {
            super( source );
        }
    }
}
