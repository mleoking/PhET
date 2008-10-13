/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetgraphics.application.PhetGraphicsModule;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.quantum.model.*;
import edu.colorado.phet.dischargelamps.control.AtomTypeChooser;
import edu.colorado.phet.dischargelamps.control.BatterySlider;
import edu.colorado.phet.dischargelamps.control.ElectronProductionControl;
import edu.colorado.phet.dischargelamps.control.SlowMotionCheckBox;
import edu.colorado.phet.dischargelamps.model.*;
import edu.colorado.phet.dischargelamps.quantum.model.ElectronSource;
import edu.colorado.phet.dischargelamps.quantum.model.Plate;
import edu.colorado.phet.dischargelamps.quantum.view.PlateGraphic;
import edu.colorado.phet.dischargelamps.view.*;
import edu.colorado.phet.lasers.controller.LasersConfig;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.view.AnnotatedAtomGraphic;
import edu.colorado.phet.lasers.view.AtomGraphic;
import edu.colorado.phet.lasers.view.PhotonGraphic;
import edu.colorado.phet.lasers.view.TubeGraphic;

/**
 * DischargeLampModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DischargeLampModule extends PhetGraphicsModule {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    //    public static boolean DEBUG = true;
    public static boolean DEBUG = false;
    private static final double SPECTROMETER_LAYER = 1000;
    private static double VOLTAGE_VALUE_LAYER = DischargeLampsConfig.CIRCUIT_LAYER + 1;
    private static final double DEFAULT_VOLTAGE = 23.0 * DischargeLampsConfig.VOLTAGE_CALIBRATION_FACTOR;

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------

    private Tube tube;
    // The states in which the atoms can be
    private Random random = new Random();
    private DischargeLampModel model;
    private Plate leftHandPlate;
    private Plate rightHandPlate;
    private ElementProperties[] elementProperties;
    private ConfigurableElementProperties configurableElement;

    // The scale to apply to graphics created in external applications so they appear properly
    // on the screen
    private double externalGraphicsScale;
    // AffineTransformOp that will scale graphics created in external applications so they appear
    // properly on the screen
    private AffineTransformOp externalGraphicScaleOp;
    private DischargeLampEnergyMonitorPanel2 energyLevelsMonitorPanel;
    private SpectrometerGraphic spectrometerGraphic;
    private HeatingElementGraphic[] heatingElementGraphics = new HeatingElementGraphic[2];
    private JCheckBox squiggleCB;
    private JPanel optionsPanel;
    private ElectronProductionControl electronProductionControl;

    //----------------------------------------------------------------
    // Constructors and initialization
    //----------------------------------------------------------------

    /**
     * Constructor
     *
     * @param clock
     */
    protected DischargeLampModule( String name, IClock clock ) {
        super( name, clock );

        // Set up the basic stuff
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setPaintStrategy( ApparatusPanel2.OFFSCREEN_BUFFER_STRATEGY );
        apparatusPanel.setBackground( Color.white );
        setApparatusPanel( apparatusPanel );

        // Set up the model
        model = new DischargeLampModel();
        model.setElectronProductionMode( ElectronSource.CONTINUOUS_MODE );
        model.setMaxCurrent( getMaxCurrent() );
        leftHandPlate = model.getLeftHandPlate();
        rightHandPlate = model.getRightHandPlate();
        model.setVoltage( DEFAULT_VOLTAGE );
        setModel( model );
        setControlPanel( new ControlPanel( this ) );

        // Create the element properties we will use
        configurableElement = new ConfigurableElementProperties( 2, model );
        elementProperties = new ElementProperties[]{
                new HydrogenProperties(),
                new MercuryProperties(),
                new SodiumProperties(),
                new NeonProperties(),
                configurableElement
        };

        // Add graphics
        addCircuitGraphic( apparatusPanel );
        addCathodeGraphic( apparatusPanel );
        addAnodeGraphic( apparatusPanel );
        addSpectrometerGraphic();
        addHeatingElementGraphics();
        addTubeGraphic( apparatusPanel );

        // Set up the control panel
        addGraphicBatteryControls();
        addControls();

        // Needed to handle something that is inherited from the Lasers simulation. When
        // the code is properly decoupled, this can go away.
        StimulatedPhoton.setStimulationBounds( model.getTube().getBounds() );
    }

    public void activate() {
        super.activate();
        electronProductionControl.setVisible( true );
    }

    public void deactivate() {
        super.deactivate();
        electronProductionControl.setVisible( false );
    }

    /**
     * Places a slider and digital readout on the battery graphic
     */
    private void addGraphicBatteryControls() {
        Battery battery = model.getBattery();
        final BatterySlider bSl = new BatterySlider( getApparatusPanel(), 80 /* track length */, battery,
                                                     DischargeLampsConfig.VOLTAGE_CALIBRATION_FACTOR );
        bSl.setMinimum( (int) -( battery.getMaxVoltage() ) );
        bSl.setMaximum( (int) ( battery.getMaxVoltage() ) );
        bSl.setValue( (int) ( DEFAULT_VOLTAGE ) );
        bSl.addTick( bSl.getMinimum() );
        bSl.addTick( bSl.getMaximum() );
        bSl.addTick( 0 );
        bSl.setLocation( (int) DischargeLampsConfig.CATHODE_LOCATION.getX() + 174, (int) DischargeLampsConfig.CATHODE_LOCATION.getY() - 240 );
        getApparatusPanel().addGraphic( bSl, DischargeLampsConfig.CIRCUIT_LAYER + 1 );

        final PhetGraphic batteryReadout = new BatteryReadout( getApparatusPanel(),
                                                               battery,
                                                               new Point( (int) DischargeLampsConfig.CATHODE_LOCATION.getX() + 194,
                                                                          (int) bSl.getY() + 17 ),
                                                               35 );
        addGraphic( batteryReadout, VOLTAGE_VALUE_LAYER );
    }

    /**
     * Adds the graphics for the heating elements
     */
    private void addHeatingElementGraphics() {
        int yOffset = -173;
        int xOffset = 45;
        HeatingElementGraphic leftHeatingElementGraphic = new HeatingElementGraphic( getApparatusPanel(), true );
        getApparatusPanel().addGraphic( leftHeatingElementGraphic );
        Point leftHandGraphicLocation = new Point( (int) model.getLeftHandHeatingElement().getPosition().getX()
                                                   - leftHeatingElementGraphic.getImage().getWidth() + xOffset,
                                                   (int) model.getLeftHandHeatingElement().getPosition().getY() + yOffset );
        leftHeatingElementGraphic.setLocation( leftHandGraphicLocation );

        HeatingElementGraphic rightHeatingElementGraphic = new HeatingElementGraphic( getApparatusPanel(), false );
        getApparatusPanel().addGraphic( rightHeatingElementGraphic );
        Point rightHandGraphicLocation = new Point( (int) model.getRightHandHeatingElement().getPosition().getX() - xOffset,
                                                    (int) model.getRightHandHeatingElement().getPosition().getY() + yOffset );
        rightHeatingElementGraphic.setLocation( rightHandGraphicLocation );
        heatingElementGraphics[0] = leftHeatingElementGraphic;
        heatingElementGraphics[1] = rightHeatingElementGraphic;

        model.getLeftHandHeatingElement().addChangeListener( leftHeatingElementGraphic );
        model.getRightHandHeatingElement().addChangeListener( rightHeatingElementGraphic );

        leftHandPlate.addElectronProductionListener( new ElectronGraphicManager( getApparatusPanel() ) );
        rightHandPlate.addElectronProductionListener( new ElectronGraphicManager( getApparatusPanel() ) );
    }

    /**
     * Creates the tube, adds it to the model and creates a graphic for it
     *
     * @param apparatusPanel
     */
    private void addTubeGraphic( ApparatusPanel apparatusPanel ) {
        Tube tube = model.getTube();
        TubeGraphic tubeGraphic = new TubeGraphic( getApparatusPanel(), tube );
        apparatusPanel.addGraphic( tubeGraphic, DischargeLampsConfig.TUBE_LAYER );
        this.tube = tube;
    }

    /**
     * @param apparatusPanel
     */
    private void addAnodeGraphic( ApparatusPanel apparatusPanel ) {
        PlateGraphic anodeGraphic = new PlateGraphic( getApparatusPanel(), DischargeLampsConfig.CATHODE_LENGTH );
        model.getRightHandHeatingElement().addChangeListener( anodeGraphic );
        anodeGraphic.setRegistrationPoint( (int) anodeGraphic.getBounds().getWidth(),
                                           (int) anodeGraphic.getBounds().getHeight() / 2 );
        anodeGraphic.setRegistrationPoint( 0, (int) anodeGraphic.getBounds().getHeight() / 2 );
        anodeGraphic.setLocation( DischargeLampsConfig.ANODE_LOCATION );
        apparatusPanel.addGraphic( anodeGraphic, DischargeLampsConfig.CIRCUIT_LAYER );
    }

    /**
     * @param apparatusPanel
     */
    private void addCathodeGraphic( ApparatusPanel apparatusPanel ) {
        PlateGraphic cathodeGraphic = new PlateGraphic( getApparatusPanel(), DischargeLampsConfig.CATHODE_LENGTH );
        model.getLeftHandHeatingElement().addChangeListener( cathodeGraphic );
        cathodeGraphic.setRegistrationPoint( (int) cathodeGraphic.getBounds().getWidth(),
                                             (int) cathodeGraphic.getBounds().getHeight() / 2 );
        cathodeGraphic.setLocation( DischargeLampsConfig.CATHODE_LOCATION );
        apparatusPanel.addGraphic( cathodeGraphic, DischargeLampsConfig.CIRCUIT_LAYER );
    }

    /**
     * @param apparatusPanel
     */
    private void addCircuitGraphic( ApparatusPanel apparatusPanel ) {
        CircuitGraphic circuitGraphic = new CircuitGraphic( apparatusPanel, getExternalGraphicScaleOp() );
        model.addChangeListener( circuitGraphic );
        circuitGraphic.setRegistrationPoint( (int) ( 124 * externalGraphicsScale ), (int) ( 340 * externalGraphicsScale ) );
        circuitGraphic.setLocation( DischargeLampsConfig.CATHODE_LOCATION );
        apparatusPanel.addGraphic( circuitGraphic, DischargeLampsConfig.CIRCUIT_LAYER );
    }

    /**
     * Adds the spectrometer graphic
     */
    private void addSpectrometerGraphic() {
        spectrometerGraphic = new SpectrometerGraphic( getApparatusPanel(), model.getSpectrometer() );
        addGraphic( spectrometerGraphic, SPECTROMETER_LAYER );
        int centerX = ( DischargeLampsConfig.ANODE_LOCATION.x + DischargeLampsConfig.CATHODE_LOCATION.x ) / 2;
        int centerY = DischargeLampsConfig.CATHODE_LOCATION.y + 125;
        spectrometerGraphic.setLocation( centerX, centerY );
        spectrometerGraphic.setRegistrationPoint( spectrometerGraphic.getWidth() / 2, 0 );
    }

    /**
     * Returns an AffineTransformOp that will scale BufferedImages to the dimensions of the
     * apparatus panel
     *
     * @return
     */
    private AffineTransformOp getExternalGraphicScaleOp() {
        if ( externalGraphicScaleOp == null ) {
            int cathodeAnodeScreenDistance = 550;
            determineExternalGraphicScale( DischargeLampsConfig.ANODE_LOCATION,
                                           DischargeLampsConfig.CATHODE_LOCATION,
                                           cathodeAnodeScreenDistance );
            AffineTransform scaleTx = AffineTransform.getScaleInstance( externalGraphicsScale, externalGraphicsScale );
            externalGraphicScaleOp = new AffineTransformOp( scaleTx, AffineTransformOp.TYPE_BILINEAR );
        }
        return externalGraphicScaleOp;
    }

    /**
     * Computes the scale to be applied to externally created graphics.
     * <p/>
     * Scale is determined by specifying a distance in the external graphics that should
     * be the same as the distance between two point on the screen.
     *
     * @param p1
     * @param p2
     * @param externalGraphicDist
     */
    private void determineExternalGraphicScale( Point p1, Point p2, int externalGraphicDist ) {
        externalGraphicsScale = p1.distance( p2 ) / externalGraphicDist;
    }

    /**
     * Sets up the control panel
     */
    private void addControls() {
        ControlPanel controlPanel = getControlPanel();

        // Set the minimum width
        controlPanel.addControlFullWidth( Box.createHorizontalStrut( 300 ) ); /*, new GridBagConstraints( 0,0,1,1,1,1,
                                                                     GridBagConstraints.NORTH,
                                                                     GridBagConstraints.NONE,
                                                                     new Insets( 0,0,0,0),0,0 ) );*/

        // The legend
        controlPanel.addControlFullWidth( new DischargeLampsLegend() );

        // A combo box for atom types
        {
            JComponent atomTypeComboBox = new AtomTypeChooser( model, elementProperties );
            controlPanel.addControlFullWidth( atomTypeComboBox );
        }

        // Add an energy level monitor panel.
        energyLevelsMonitorPanel = new DischargeLampEnergyMonitorPanel2( model,
                                                                         model.getAtomicStates(),
                                                                         200,
                                                                         300,
                                                                         configurableElement );
        controlPanel.addControl( energyLevelsMonitorPanel );

        // Widget to control electron production
        {
            electronProductionControl = new ElectronProductionControl( this, getMaxCurrent() );
            electronProductionControl.setProductionMode( ElectronProductionControl.CONTINUOUS );

            // It seems that we have to make this dialog to get the electronProductionControl to layout. If we don't
            // nothing appears when we add the electronProductionControl to the LayeredPane
            JDialog jd = new JDialog( PhetUtilities.getPhetFrame(), false );
            jd.getContentPane().add( electronProductionControl );
            jd.pack();
            jd.setVisible( false );
            PhetUtilities.getPhetFrame().getLayeredPane().add( electronProductionControl, JLayeredPane.DRAG_LAYER );
            int x = DischargeLampsConfig.BEAM_CONTROL_CENTER_PT.x - electronProductionControl.getWidth() / 2;
            int y = DischargeLampsConfig.BEAM_CONTROL_CENTER_PT.y - electronProductionControl.getHeight() / 2 - 25;//XXX -25 needs to be generalized
            electronProductionControl.setLocation( x, y );
        }

        // Options
        {
            optionsPanel = new JPanel( new GridBagLayout() );
            GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, //0,
                                                             1, 1, 1, 1,
                                                             GridBagConstraints.WEST,
                                                             GridBagConstraints.NONE,
                                                             new Insets( -2, 40, -2, 0 ), 0, 0 );
            optionsPanel.setBorder( new TitledBorder( SimStrings.getInstance().getString( "Controls.Options" ) ) );
            JPanel cbPanel = new JPanel( new GridLayout( 2, 1 ) );

            // Add a button to show/hide the spectrometer
            final JCheckBox spectrometerCB = new JCheckBox( SimStrings.getInstance().getString( "ControlPanel.SpectrometerButtonLabel" ) );
            spectrometerCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    spectrometerGraphic.setVisible( spectrometerCB.isSelected() );
                    model.getSpectrometer().reset();
                    model.getSpectrometer().start();
                }
            } );
            spectrometerGraphic.setVisible( spectrometerCB.isSelected() );

            squiggleCB = new JCheckBox( SimStrings.getInstance().getString( "Controls.Squiggles" ) );
            squiggleCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    energyLevelsMonitorPanel.setSquigglesEnabled( squiggleCB.isSelected() );
                }
            } );
            cbPanel.add( squiggleCB );

            optionsPanel.add( spectrometerCB, gbc );
            optionsPanel.add( squiggleCB, gbc );
            optionsPanel.add( new SlowMotionCheckBox( (Clock) getClock() ), gbc );
            controlPanel.addControlFullWidth( optionsPanel );
        }
    }

    /**
     * Adds a button to the clock control panel that will pop up an image of real lamps
     */
    protected JComponent createClockControlPanel( IClock clock ) {
        JComponent superpanel = super.createClockControlPanel( clock );
        JPanel newPanel = new JPanel();
        newPanel.setLayout( new BorderLayout() );
        newPanel.add( superpanel, BorderLayout.CENTER );
        JPanel leftPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        leftPanel.add( new ShowActualButton() );
        newPanel.add( leftPanel, BorderLayout.WEST );
        return newPanel;

    }

    /**
     * Adds some atoms and their graphics
     *
     * @param tube
     * @param numAtoms
     */
    protected void addAtoms( Tube tube, int numAtoms, double maxSpeed ) {
        DischargeLampAtom atom = null;
        ArrayList atoms = new ArrayList();
        Rectangle2D tubeBounds = tube.getBounds();

        AtomicState[] atomicStates = model.getAtomicStates();

        for ( int i = 0; i < numAtoms; i++ ) {
            atom = new DischargeLampAtom( (LaserModel) getModel(), getDischargeLampModel().getElementProperties() );
//            atom = new DischargeLampAtom( (LaserModel)getModel(), atomicStates );
            atom.setPosition( ( tubeBounds.getX() + ( Math.random() ) * ( tubeBounds.getWidth() - atom.getRadius() * 4 ) + atom.getRadius() * 2 ),
                              ( tubeBounds.getY() + ( Math.random() ) * ( tubeBounds.getHeight() - atom.getRadius() * 4 ) ) + atom.getRadius() * 2 );
            atom.setVelocity( (float) ( Math.random() - 0.5 ) * maxSpeed,
                              (float) ( Math.random() - 0.5 ) * maxSpeed );
            atoms.add( atom );
            addAtom( atom );
        }
        energyLevelsMonitorPanel.reset();
    }

    /**
     * Extends parent behavior to place half the atoms in a layer above the electrodes, and half below
     */
    protected AtomGraphic addAtom( Atom atom ) {
        energyLevelsMonitorPanel.addAtom( atom );
//        AtomGraphic graphic = super.addAtom( atom );

        getModel().addModelElement( atom );
        AtomGraphic graphic = new AnnotatedAtomGraphic( getApparatusPanel(), atom );
//        addGraphic( atomGraphic, LaserConfig.ATOM_LAYER );

        // Add a listener to the atom that will create a photon graphic if the atom
        // emits a photon, and another to deal with an atom leaving the system
        atom.addPhotonEmittedListener( new InternalPhotonEmittedListener() );
        atom.addLeftSystemListener( new AtomGraphicManager( graphic ) );

        // Put some of the atoms in a layer above the circuit, and some below
        getApparatusPanel().addGraphic( graphic, DischargeLampsConfig.CIRCUIT_LAYER - 1 );
        if ( random.nextBoolean() ) {
            getApparatusPanel().removeGraphic( graphic );
            getApparatusPanel().addGraphic( graphic, DischargeLampsConfig.CIRCUIT_LAYER + 1 );
        }
        return graphic;
    }


    public boolean hasHelp() {
        return false;
    }
    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    protected JPanel getOptionsPanel() {
        return optionsPanel;
    }

    protected double getMaxCurrent() {
        return 150;
    }

    /**
     * Returns a typed reference to the model
     */
    protected DischargeLampModel getDischargeLampModel() {
        return (DischargeLampModel) getModel();
    }

    /**
     * @return
     */
    protected Tube getTube() {
        return tube;
    }

    /**
     * @return
     */
    protected DischargeLampEnergyMonitorPanel2 getEneregyLevelsMonitorPanel() {
        return energyLevelsMonitorPanel;
    }

    /**
     * @param enabled
     */
    public void setSquigglesEnabled( boolean enabled ) {
        squiggleCB.setSelected( true );
        energyLevelsMonitorPanel.setSquigglesEnabled( squiggleCB.isSelected() );
    }

    public void setProductionType( ElectronProductionControl.ProductionMode type ) {
        if ( type == ElectronProductionControl.CONTINUOUS ) {
            getEneregyLevelsMonitorPanel().setShowElectrons( false );
        }
        if ( type == ElectronProductionControl.SINGLE_SHOT ) {
            getEneregyLevelsMonitorPanel().setShowElectrons( true );
        }
    }

    protected void setElectronProductionMode( ElectronProductionControl.ProductionMode mode ) {
        electronProductionControl.setProductionMode( mode );
    }

    //-------------------------------------------------------------------------------------------------
    // Event handling
    //-------------------------------------------------------------------------------------------------

    public class InternalPhotonEmittedListener implements PhotonEmissionListener {

        public void photonEmitted( PhotonEmittedEvent event ) {

            final Photon photon = event.getPhoton();
            getModel().addModelElement( photon );

            // Create a photon graphic, add it to the appratus panel and attach a
            // listener to the photon that will remove the graphic if and when the
            // photon goes away. Set it's visibility based on the state of the simulation
            final PhotonGraphic pg = PhotonGraphic.getInstance( getApparatusPanel(), photon );
            pg.setVisible( true );
            addGraphic( pg, LasersConfig.PHOTON_LAYER );
            photon.addLeftSystemListener( new PhotonGraphicManager( photon, pg ) );
        }
    }

    /**
     * Handles cleanup when an atom is removed from the system
     */
    public class AtomGraphicManager implements Atom.LeftSystemListener {
        private AtomGraphic atomGraphic;

        public AtomGraphicManager( AtomGraphic atomGraphic ) {
            this.atomGraphic = atomGraphic;
        }

        public void leftSystem( Atom.LeftSystemEvent leftSystemEvent ) {
            getApparatusPanel().removeGraphic( atomGraphic );
        }
    }

    /**
     * Handles cleanup when a photon leaves the system. Takes care of removing the photon's
     * associated graphic
     */
    public class PhotonGraphicManager implements Photon.LeftSystemEventListener {
        private PhotonGraphic graphic;

        public PhotonGraphicManager( Photon photon, PhotonGraphic graphic ) {
            this.graphic = graphic;
        }

        public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
            // Get rid of the graphic
            getApparatusPanel().removeGraphic( graphic );
            getApparatusPanel().repaint( graphic.getBounds() );
            graphic = null;
        }
    }
}
