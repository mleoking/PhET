// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.util.EventChannel;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.application.PhetGraphicsModule;
import edu.colorado.phet.common.phetgraphics.view.help.HelpItem;
import edu.colorado.phet.common.phetgraphics.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.IdealGasResources;
import edu.colorado.phet.idealgas.collision.SphereBoxExpert;
import edu.colorado.phet.idealgas.collision.SphereSphereExpert;
import edu.colorado.phet.idealgas.coreadditions.StopwatchPanel;
import edu.colorado.phet.idealgas.instrumentation.Thermometer;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.Gravity;
import edu.colorado.phet.idealgas.model.IdealGasClock;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.PressureSensingBox;
import edu.colorado.phet.idealgas.model.PressureSlice;
import edu.colorado.phet.idealgas.model.Pump;
import edu.colorado.phet.idealgas.view.BaseIdealGasApparatusPanel;
import edu.colorado.phet.idealgas.view.Box2DGraphic;
import edu.colorado.phet.idealgas.view.BoxDoorGraphic;
import edu.colorado.phet.idealgas.view.Mannequin;
import edu.colorado.phet.idealgas.view.PumpHandleGraphic;
import edu.colorado.phet.idealgas.view.RulerGraphic;
import edu.colorado.phet.idealgas.view.WiggleMeGraphic;
import edu.colorado.phet.idealgas.view.monitors.CmLines;
import edu.colorado.phet.idealgas.view.monitors.EnergyHistogramDialog;
import edu.colorado.phet.idealgas.view.monitors.PressureDialGauge;
import edu.colorado.phet.idealgas.view.monitors.PressureSliceGraphic;
import edu.colorado.phet.idealgas.view.monitors.SpeciesMonitorDialog;

/**
 *
 */
public class IdealGasModule extends PhetGraphicsModule {

    //----------------------------------------------------------------
    // Class data, initializers and methods
    //----------------------------------------------------------------

    static private BufferedImage basePumpImg;
    static private BufferedImage bluePumpImg;
    static private BufferedImage redPumpImg;
    static protected WiggleMeGraphic wiggleMeGraphic;
    static private BufferedImage pumpBaseAndHoseImg;
    static private BufferedImage handleImg;

    static {
        basePumpImg = IdealGasResources.getImage( IdealGasConfig.PUMP_IMAGE_FILE );
        pumpBaseAndHoseImg = IdealGasResources.getImage( IdealGasConfig.PUMP_BASE_IMAGE_FILE );
        handleImg = IdealGasResources.getImage( IdealGasConfig.HANDLE_IMAGE_FILE );
        bluePumpImg = new BufferedImage( basePumpImg.getWidth(), basePumpImg.getHeight(), BufferedImage.TYPE_INT_ARGB );
        redPumpImg = new BufferedImage( basePumpImg.getWidth(), basePumpImg.getHeight(), BufferedImage.TYPE_INT_ARGB );
        BufferedImageOp blueOp = new MakeDuotoneImageOp( Color.blue );
        blueOp.filter( basePumpImg, bluePumpImg );
        BufferedImageOp redOp = new MakeDuotoneImageOp( Color.red );
        redOp.filter( basePumpImg, redPumpImg );
    }

    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    private IdealGasModel idealGasModel;
    private PressureSensingBox box;
    private Gravity gravity;
    private Pump pump;
    private CmLines cmLines;

    private PressureSlice pressureSlice;
    private IdealGasClock clock;
    private PressureSliceGraphic pressureSliceGraphic;
    private PhetGraphic rulerGraphic;
    private EnergyHistogramDialog histogramDlg;
    private ArrayList visibleInstruments = new ArrayList();
    private JDialog measurementDlg;
    private JDialog speciesMonitorDlg;
    private IdealGasControlPanel idealGasControlPanel;
    private Thermometer thermometer;
    private PhetImageGraphic pumpGraphic;
    private Box2DGraphic boxGraphic;
    private JPanel pressureSlideTimeAveCtrlPane;
    private StopwatchPanel stopwatchPanel;
    private Mannequin pusher;
    private BoxDoorGraphic boxDoorGraphic;
    private PumpHandleGraphic pumpHandleGraphic;
    private PumpSpeciesSelectorPanel pumpSelectorPanel;
    private PhetImageGraphic pumpBaseAndHoseGraphic;
    // Coordinates of origin and opposite corner of box
    private double xOrigin = 132 + IdealGasConfig.X_BASE_OFFSET;
    private double yOrigin = 252 + IdealGasConfig.Y_BASE_OFFSET;
    private double xDiag = 434 + IdealGasConfig.X_BASE_OFFSET;
    private double yDiag = 497 + IdealGasConfig.Y_BASE_OFFSET;
    private Color boxColor = new Color( 180, 180, 180 );
    private Random random = new Random();
    private PressureDialGauge pressureGauge;
    private PhetGraphic returnLidGraphic;

    //-----------------------------------------------------------------
    // Constructors and initialization
    //-----------------------------------------------------------------

    /**
     * @param clock
     */
    public IdealGasModule( IdealGasClock clock ) {
        this( clock, IdealGasResources.getString( "ModuleTitle.IdealGas" ) );
        this.clock = clock;
    }

    /**
     * @param clock
     * @param name
     */
    public IdealGasModule( IdealGasClock clock, String name ) {
        this( clock, name, new IdealGasModel( clock.getDt() ) );
    }

    /**
     * @param clock
     * @param name
     */
    protected IdealGasModule( final IdealGasClock clock, String name, final IdealGasModel model ) {
        super( name, clock );
        this.clock = clock;

        // Create the model
        idealGasModel = model;
        setModel( idealGasModel );

        // Add collision experts
        idealGasModel.addCollisionExpert( new SphereSphereExpert( idealGasModel, clock.getDt() ) );
        idealGasModel.addCollisionExpert( new SphereBoxExpert( idealGasModel ) );

        // Create the box and its graphic
        createBoxAndGraphic( clock );

        // Create a listener that will keep the model bounds set correctly
        getApparatusPanel().addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                idealGasModel.setModelBounds();
            }
        } );

        // Make sure the apparatus panel has a border
        getApparatusPanel().setDisplayBorder( true );

        // Create the pressure gauge and thermometer
        createGauges( clock );

        // Create the pump
        createPumpAndGraphic();

        // Set up gravity. Note that this must be done after the box and pump are created
        gravity = new Gravity( idealGasModel );
        setGravity( 0 );
        idealGasModel.addModelElement( gravity );

        // Add the animated mannequin
        pusher = new Mannequin( getApparatusPanel(), idealGasModel, box, boxGraphic );
        addGraphic( pusher, 10 );

        // Set up the control panel
        idealGasControlPanel = new IdealGasControlPanel( this );
        ControlPanel controlPanel = new ControlPanel( this );
        controlPanel.addControl( idealGasControlPanel );
        setControlPanel( controlPanel );

        // Place a slider to control the stove
        createApparatusSwingControls();

        createReturnLidButton();

        // Add help items
        addHelp();
    }

    /**
     * Utility method
     *
     * @return the model
     */
    protected IdealGasModel getIdealGasModel() {
        return (IdealGasModel) getModel();
    }

    /**
     * Specifies the names of the particles used in the simulation. Can be overridden by other modules.
     *
     * @return names of species
     */
    protected String[] getSpeciesNames() {
        return new String[] { IdealGasResources.getString( "Common.Heavy_Species" ), IdealGasResources.getString( "Common.Light_Species" ) };
    }

    /**
     * Creates the pressure gauge and thermometer
     *
     * @param clock
     */
    private void createGauges( IdealGasClock clock ) {
        // Add the pressure gauge
        PressureSlice gaugeSlice = new PressureSlice( box, idealGasModel, clock );
        gaugeSlice.setTimeAveragingWindow( 2500 * ( clock.getDt() / clock.getDelay() ) );
        gaugeSlice.setUpdateContinuously( true );
        gaugeSlice.setY( box.getMinY() + 50 );
        box.setGaugeSlice( gaugeSlice );
        idealGasModel.addModelElement( gaugeSlice );
        pressureGauge = new PressureDialGauge( box, getApparatusPanel(),
                                               new Point( (int) box.getMaxX(),
                                                          (int) gaugeSlice.getY() ) );
        addGraphic( pressureGauge, IdealGasConfig.MOLECULE_LAYER );

        // Add the thermometer
        double thermometerHeight = 100;
        Point2D.Double thermometerLoc = new Point2D.Double( box.getMaxX() - 30, box.getMinY() - thermometerHeight );
        thermometer = new IdealGasThermometer( getApparatusPanel(),
                                               idealGasModel, thermometerLoc,
                                               thermometerHeight, 10, true, 0, 1000E3 );
        addGraphic( thermometer, IdealGasConfig.MOLECULE_LAYER + .1 );
    }

    /**
     * @param clock
     */
    private void createBoxAndGraphic( IdealGasClock clock ) {
        box = new PressureSensingBox( new Point2D.Double( xOrigin, yOrigin ),
                                      new Point2D.Double( xDiag, yDiag ), idealGasModel, clock );
        idealGasModel.addBox( box );
        setApparatusPanel( new BaseIdealGasApparatusPanel( this, clock, box ) );

        // Set up the box graphic
        boxGraphic = new Box2DGraphic( getApparatusPanel(), box, boxColor );
        addGraphic( boxGraphic, 10 );

        // Set up the door for the box
        boxDoorGraphic = new BoxDoorGraphic( getApparatusPanel(),
                                             IdealGasConfig.X_BASE_OFFSET + 230, IdealGasConfig.Y_BASE_OFFSET + 22,
                                             IdealGasConfig.X_BASE_OFFSET + 150, IdealGasConfig.Y_BASE_OFFSET + 220,
                                             IdealGasConfig.X_BASE_OFFSET + 230, IdealGasConfig.Y_BASE_OFFSET + 220,
                                             box,
                                             boxColor,
                                             this );
        this.addGraphic( boxDoorGraphic, 11 );
    }

    /**
     *
     */
    private void createApparatusSwingControls() {
        StoveControlPanel scp2 = new StoveControlPanel( this );
        scp2.setLocation( IdealGasConfig.X_BASE_OFFSET + IdealGasConfig.X_STOVE_OFFSET + 100,
                          IdealGasConfig.Y_BASE_OFFSET + IdealGasConfig.Y_STOVE_OFFSET - 10 );
        getApparatusPanel().addGraphic( scp2 );

        // Add buttons for selecting the species that the pump will produce
        pumpSelectorPanel = new PumpSpeciesSelectorPanel( this );
        pumpSelectorPanel.setLocation( IdealGasConfig.X_BASE_OFFSET + 630, IdealGasConfig.Y_BASE_OFFSET + 300 );
        pumpSelectorPanel.setLocation( (int) ( pumpGraphic.getLocation().getX() + pumpGraphic.getWidth() - pumpSelectorPanel.getWidth() + 10 ),
                                       (int) ( pumpGraphic.getLocation().getY() + pumpGraphic.getHeight() + 32 ) );
        getApparatusPanel().addGraphic( pumpSelectorPanel );
        getApparatusPanel().revalidate();
    }

    /*
     * Creates the "Return Lid" button, see Unfuddle #3167.
     * When the "safe" pressure in the chamber is exceeded, the lid blows off and this button appears.
     * Pressing the button empties the chamber and closes the lid.
     */
    private void createReturnLidButton() {

        // Swing button
        JButton returnLidButton = new JButton( IdealGasResources.getString( "returnLid" ) ) {{
            setFont( new PhetFont( 18 ) );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    resetChamber();
                }
            } );
        }};

        // Integration of Swing with phetgraphics.
        returnLidGraphic = PhetJComponent.newInstance( getApparatusPanel(), returnLidButton );
        returnLidGraphic.setVisible( !box.isPressureSafe() );
        getApparatusPanel().addGraphic( returnLidGraphic );

        // Position the button above the box, and to the left of the hole in the top of the box,
        // taking into account the effects of i18n on the button size.
        returnLidGraphic.setLocation( IdealGasConfig.X_BASE_OFFSET + 220 - returnLidGraphic.getWidth(),
                                      IdealGasConfig.Y_BASE_OFFSET + 230 - returnLidGraphic.getHeight() );

        // Show the button when pressure change results in the lid being blown off the box.
        box.addChangeListener( new PressureSensingBox.ChangeListener() {
            public void stateChanged( PressureSensingBox.ChangeEvent event ) {
                if ( !box.isPressureSafe() ) {
                    returnLidGraphic.setVisible( true );
                }
            }
        } );
    }

    /**
     *
     */
    private void createPumpAndGraphic() {
        pump = new Pump( this, box, getPumpingEnergyStrategy() );

        // Set up the graphics for the pump
        PhetImageGraphic handleGraphic = new PhetImageGraphic( getApparatusPanel(), handleImg );
        pumpHandleGraphic = new PumpHandleGraphic( getApparatusPanel(), pump, handleGraphic,
                                                   IdealGasConfig.X_BASE_OFFSET + 578, IdealGasConfig.Y_BASE_OFFSET + 238,
                                                   IdealGasConfig.X_BASE_OFFSET + 578, IdealGasConfig.Y_BASE_OFFSET + 100,
                                                   IdealGasConfig.X_BASE_OFFSET + 578, IdealGasConfig.Y_BASE_OFFSET + 238 );

        BufferedImage currentPumpImg = bluePumpImg;

        this.addGraphic( pumpHandleGraphic, -6 );
        pumpBaseAndHoseGraphic = new PhetImageGraphic( getApparatusPanel(), pumpBaseAndHoseImg, IdealGasConfig.X_BASE_OFFSET + 436, IdealGasConfig.Y_BASE_OFFSET + 258 );
        pumpGraphic = new PhetImageGraphic( getApparatusPanel(), currentPumpImg, IdealGasConfig.X_BASE_OFFSET + 436, IdealGasConfig.Y_BASE_OFFSET + 258 );
        pumpGraphic.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        this.addGraphic( pumpGraphic, -4 );
        this.addGraphic( pumpBaseAndHoseGraphic, -3.5 );
    }

    protected void removePumpGraphic() {
        getApparatusPanel().removeGraphic( pumpGraphic );
        getApparatusPanel().removeGraphic( pumpHandleGraphic );
        getApparatusPanel().removeGraphic( pumpSelectorPanel );
        getApparatusPanel().removeGraphic( pumpBaseAndHoseGraphic );
    }

    protected void setPumpSelectorPanelTitle( String title ) {
        pumpSelectorPanel.setTitle( title );
    }

    protected Pump.PumpingEnergyStrategy getPumpingEnergyStrategy() {
        return new Pump.ConstantEnergyStrategy( idealGasModel );
    }

    protected void addHelp() {
        addWallHelp();
        addDoorHelp();
        addStoveHelp();
    }

    protected void addStoveHelp() {
        HelpItem helpItem3 = new HelpItem( getApparatusPanel(),
                                           IdealGasResources.getString( "help.stove" ),
                                           box.getPosition().getX() + 50, box.getMaxY() + 50 );
        helpItem3.setForegroundColor( IdealGasConfig.HELP_COLOR );
        addHelpItem( helpItem3 );
    }

    protected void addDoorHelp() {
        HelpItem helpItem2 = new HelpItem( getApparatusPanel(),
                                           IdealGasResources.getString( "help.door" ),
                                           box.getPosition().getX() + 100, box.getPosition().getY() - 50 );
        helpItem2.setForegroundColor( IdealGasConfig.HELP_COLOR );
        addHelpItem( helpItem2 );
    }

    protected void addWallHelp() {
        HelpItem helpItem1 = new HelpItem( getApparatusPanel(),
                                           IdealGasResources.getString( "help.wall" ),
                                           box.getPosition().getX(), box.getPosition().getY(),
                                           HelpItem.BELOW, HelpItem.LEFT );
        helpItem1.setForegroundColor( IdealGasConfig.HELP_COLOR );
        addHelpItem( helpItem1 );
    }

    public void activate( PhetApplication app ) {
        super.activate();
//        super.activate( app );
        for ( int i = 0; i < visibleInstruments.size(); i++ ) {
            Component component = (Component) visibleInstruments.get( i );
            component.setVisible( true );
        }

        // FOR DEBUG. displays the total energy in the system
//        TotalEnergyMonitor tem = new TotalEnergyMonitor( null, idealGasModel );
//        tem.setVisible( true );
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate();
//        super.deactivate( app );
        for ( int i = 0; i < visibleInstruments.size(); i++ ) {
            Component component = (Component) visibleInstruments.get( i );
            component.setVisible( false );
        }
    }

    protected IdealGasControlPanel getIdealGasControlPanel() {
        return idealGasControlPanel;
    }

    //-----------------------------------------------------------------
    // Getters
    //-----------------------------------------------------------------
    public int getHeavySpeciesCnt() {
        return idealGasModel.getHeavySpeciesCnt();
    }

    public int getLightSpeciesCnt() {
        return idealGasModel.getLightSpeciesCnt();
    }

    //-----------------------------------------------------------------
    // Setters
    //-----------------------------------------------------------------

    public JDialog setMeasurementDlgVisible( boolean isVisible ) {
        if ( measurementDlg == null ) {
            measurementDlg = new MeasurementDialog( PhetApplication.getInstance().getPhetFrame(), this );
            JFrame frame = PhetApplication.getInstance().getPhetFrame();
            measurementDlg.setLocationRelativeTo( frame );
            measurementDlg.setLocation( (int) ( frame.getLocation().getX() + frame.getWidth() * 3 / 5 ),
                                        (int) frame.getLocation().getY() + 20 );
        }
        measurementDlg.setVisible( isVisible );
        if ( isVisible ) {
            visibleInstruments.add( measurementDlg );
        }
        else {
            visibleInstruments.remove( measurementDlg );
        }
        return measurementDlg;
    }

    public JDialog setSpeciesMonitorDlgEnabled( boolean isEnabled ) {
        if ( speciesMonitorDlg == null ) {
            speciesMonitorDlg = new SpeciesMonitorDialog( PhetApplication.getInstance().getPhetFrame(),
                                                          idealGasModel );
        }
        speciesMonitorDlg.setVisible( isEnabled );
        if ( isEnabled ) {
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
        if ( cmLines == null ) {
            cmLines = new CmLines( getApparatusPanel(), idealGasModel );
        }
        if ( cmLinesOn ) {
            addGraphic( cmLines, IdealGasConfig.MOLECULE_LAYER + 1 );
        }
        else {
            getApparatusPanel().removeGraphic( cmLines );
        }

    }

    /**
     * Sets the amount of heat that is added or removed from the system at each step in time
     *
     * @param value
     */
    public void setStove( int value ) {
        idealGasModel.setHeatSource( (double) value );
        ( (BaseIdealGasApparatusPanel) getApparatusPanel() ).setStove( value );
    }

    /**
     * Sets the value for garvity. Also sets the strategy used to determine what energy will
     * be given to molecules pumped into the box, and the way that the box reports pressure
     *
     * @param value
     */
    public void setGravity( double value ) {
        gravity.setAmt( value );
        if ( value != 0 ) {
            pump.setPumpingEnergyStrategy( new Pump.FixedEnergyStrategy() );
            box.setMultipleSlicesEnabled( false );
        }
        else {
            pump.setPumpingEnergyStrategy( new Pump.ConstantEnergyStrategy( getIdealGasModel() ) );
            box.setMultipleSlicesEnabled( true );
        }
    }

    public void pumpGasMolecules( int numMolecules ) {
        pump.pump( numMolecules );
    }

    public void pumpGasMolecules( int numMolecules, Class species ) {
        pump.pump( numMolecules, species );
    }

    public void removeGasMolecule( Class species ) {
        java.util.List bodies = idealGasModel.getBodies();

        // Randomize which end of the list of bodies we start searching from,
        // just to make sure there is no non-random effect on the temperature
        // of the system
        Object obj = null;
        while ( obj == null ) {
            boolean randomB = random.nextBoolean();
            if ( randomB ) {
                for ( int i = 0; i < bodies.size(); i++ ) {
                    obj = bodies.get( i );
                    if ( species.isInstance( obj ) ) {
                        break;
                    }
                }
            }
            else {
                for ( int i = bodies.size() - 1; i >= 0; i-- ) {
                    obj = bodies.get( i );
                    if ( species.isInstance( obj ) ) {
                        break;
                    }
                }
            }
        }
        if ( obj instanceof GasMolecule ) {
            GasMolecule molecule = (GasMolecule) obj;
            idealGasModel.removeModelElement( molecule );
        }
    }

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    protected PressureSensingBox getBox() {
        return box;
    }

    protected Box2DGraphic getBoxGraphic() {
        return boxGraphic;
    }

    protected BoxDoorGraphic getBoxDoorGraphic() {
        return boxDoorGraphic;
    }

    public Mannequin getPusher() {
        return pusher;
    }

    public Pump getPump() {
        return pump;
    }

    public Thermometer getThermometer() {
        return thermometer;
    }

    /**
     * Provided primarily so different modules can change the units on the
     * stopwatch
     *
     * @return the stopwatch panel
     */
    protected StopwatchPanel getStopwatchPanel() {
        return stopwatchPanel;
    }

    //------------------------------------------------------------------------------------
    // Measurement tools
    //------------------------------------------------------------------------------------

    /**
     * @param pressureSliceEnabled
     */
    public void setPressureSliceEnabled( boolean pressureSliceEnabled ) {

        if ( pressureSlice == null ) {
            pressureSlice = new PressureSlice( getBox(), (IdealGasModel) getModel(), clock );
            pressureSlice.setUpdateContinuously( false );
            pressureSliceGraphic = new PressureSliceGraphic( getApparatusPanel(),
                                                             pressureSlice,
                                                             getBox() );

            // Create a nonmodal dialog with controls for the averaging times of the pressure slice
            DecimalFormat tfFmt = new DecimalFormat( "#.0" );
            final double timeScale = clock.getDt() / clock.getDelay();
            final JTextField aveTimeTF = new JTextField( 3 );
            aveTimeTF.setHorizontalAlignment( JTextField.RIGHT );
            aveTimeTF.setText( tfFmt.format( pressureSlice.getTimeAveragingWindow() / ( 1000 * timeScale ) ) );
            aveTimeTF.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    double realAveTime = Double.parseDouble( aveTimeTF.getText() ) * 1000;
                    double simAveTime = realAveTime * timeScale;
                    pressureSlice.setTimeAveragingWindow( simAveTime );
                }
            } );

            JButton setTimeBtn = new JButton( IdealGasResources.getString( "IdealGasControlPanel.SetButton" ) );
            setTimeBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    double realAveTime = Double.parseDouble( aveTimeTF.getText() ) * 1000;
                    double simAveTime = realAveTime * timeScale;
                    pressureSlice.setTimeAveragingWindow( simAveTime );
                }
            } );

            JPanel ctrlPane = new JPanel();
            ctrlPane.setLayout( new GridBagLayout() );
            GridBagConstraints gbc = new GridBagConstraints( GridBagConstraints.RELATIVE, 0,
                                                             1, 1, 1, 1,
                                                             GridBagConstraints.CENTER,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );
            ctrlPane.add( aveTimeTF, gbc );
            ctrlPane.add( new JLabel( IdealGasResources.getString( "IdealGasModule.StopwatchTimeUnits" ) ), gbc );
            gbc.insets = new Insets( 0, 10, 0, 0 );
            ctrlPane.add( setTimeBtn, gbc );
            Border border = new TitledBorder( new EtchedBorder( BevelBorder.RAISED,
                                                                new Color( 40, 20, 255 ),
                                                                Color.black ),
                                              IdealGasResources.getString( "IdealGasControlPanel.AveTimePanelTitle" ) );
            ctrlPane.setBorder( border );
            Color background = new Color( 240, 230, 255 );
            ctrlPane.setBackground( background );

            pressureSlideTimeAveCtrlPane = new JPanel();
            pressureSlideTimeAveCtrlPane.setOpaque( false );
            pressureSlideTimeAveCtrlPane.add( ctrlPane );
            pressureSlideTimeAveCtrlPane.setBounds( 15,
                                                    15,
                                                    200, 100 );
            getApparatusPanel().add( pressureSlideTimeAveCtrlPane );
        }
        if ( pressureSliceEnabled ) {
            getModel().addModelElement( pressureSlice );
            addGraphic( pressureSliceGraphic, 20 );
            pressureSlideTimeAveCtrlPane.setVisible( true );
            getApparatusPanel().revalidate();
        }
        else {
            getApparatusPanel().removeGraphic( pressureSliceGraphic );
            getModel().removeModelElement( pressureSlice );
            if ( pressureSlideTimeAveCtrlPane != null ) {
                pressureSlideTimeAveCtrlPane.setVisible( false );
            }
        }
    }

    /**
     * @param rulerEnabled
     */
    public void setRulerEnabed( boolean rulerEnabled ) {
        if ( rulerGraphic == null ) {
            rulerGraphic = new RulerGraphic( getApparatusPanel() );
        }
        if ( rulerEnabled ) {
            getApparatusPanel().addGraphic( rulerGraphic, Integer.MAX_VALUE );
        }
        else {
            getApparatusPanel().removeGraphic( rulerGraphic );
        }
        getApparatusPanel().revalidate();
        getApparatusPanel().paintImmediately( 0, 0,
                                              (int) getApparatusPanel().getBounds().getWidth(),
                                              (int) getApparatusPanel().getBounds().getHeight() );
    }

    /**
     * Creates and displays a histogram dialog
     *
     * @param histogramDlgEnabled
     * @return the dialog
     */
    public JDialog setHistogramDlgEnabled( boolean histogramDlgEnabled ) {
        if ( histogramDlgEnabled ) {
            visibleInstruments.add( histogramDlg );
            histogramDlg = new EnergyHistogramDialog( PhetApplication.getInstance().getPhetFrame(),
                                                      (IdealGasModel) getModel() );
            histogramDlg.setVisible( true );
        }
        else {
            histogramDlg.setVisible( false );
            histogramDlg = null;
            visibleInstruments.remove( histogramDlg );
        }
        return histogramDlg;
    }

    public void setStopwatchEnabled( boolean stopwatchEnabled ) {
        if ( stopwatchEnabled ) {
            stopwatchPanel = new StopwatchPanel( getModel(), IdealGasResources.getString( "stopwatch.units" ), IdealGasConfig.TIME_SCALE_FACTOR );
            getClockControlPanel().add( stopwatchPanel, BorderLayout.WEST );
            getClockControlPanel().revalidate();
            visibleInstruments.add( stopwatchPanel );
        }
        else {
            getClockControlPanel().remove( stopwatchPanel );
            getClockControlPanel().revalidate();
            visibleInstruments.remove( stopwatchPanel );
        }
    }

    /**
     * Enable/disable the pressure gauge
     */
    public void setPressureGaugeVisible( boolean isVisible ) {
        if ( isVisible ) {
            getApparatusPanel().addGraphic( pressureGauge );
        }
        else {
            getApparatusPanel().removeGraphic( pressureGauge );
        }
    }

    //-----------------------------------------------------
    // Event handling
    //-----------------------------------------------------
    EventChannel resetEventChannel = new EventChannel( ResetListener.class );
    ResetListener resetListenersProxy = (ResetListener) resetEventChannel.getListenerProxy();

    public void reset() {
        resetChamber();
        box.setBounds( xOrigin, yOrigin, xDiag, yDiag );
        resetListenersProxy.resetOccurred( new ResetEvent( this ) );
        if ( stopwatchPanel != null ) {
            stopwatchPanel.reset();
        }
    }

    // Resets the chamber (aka "box") so that it contains zero molecules (aka "species") and the lid (aka "door") is closed. See #3167.
    protected void resetChamber() {
        getIdealGasModel().removeAllMolecules();
        idealGasControlPanel.resetSpeciesControls(); // these controls should be observing the model, but they aren't
        box.setOpening( new Point2D[] { new Point2D.Double(), new Point2D.Double() } );
        box.clearData(); // should be observing the model, but doesn't work properly
        boxDoorGraphic.closeDoor();
        returnLidGraphic.setVisible( false );
    }

    public void addResetListener( ResetListener listener ) {
        resetEventChannel.addListener( listener );
    }

    public void removeResetListener( ResetListener listener ) {
        resetEventChannel.removeListener( listener );
    }

    public void removeAllResetListeners() {
        resetEventChannel.removeAllListeners();
    }

    public void setCurrentPumpImage( Color color ) {
        if ( color.equals( Color.blue ) ) {
            pumpGraphic.setImage( bluePumpImg );
        }
        if ( color.equals( Color.red ) ) {
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
