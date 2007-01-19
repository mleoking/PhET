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

import edu.colorado.phet.common.application.PhetGraphicsModule;
import edu.colorado.phet.common.model.clock.Clock;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.dischargelamps.control.AtomTypeChooser;
import edu.colorado.phet.dischargelamps.control.BatterySlider;
import edu.colorado.phet.dischargelamps.control.CurrentSlider;
import edu.colorado.phet.dischargelamps.control.SlowMotionCheckBox;
import edu.colorado.phet.dischargelamps.model.*;
import edu.colorado.phet.dischargelamps.view.*;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.view.AtomGraphic;
import edu.colorado.phet.lasers.view.TubeGraphic;
import edu.colorado.phet.lasers.view.PhotonGraphic;
import edu.colorado.phet.quantum.model.*;
import edu.colorado.phet.quantum.view.AnnotatedAtomGraphic;
import edu.colorado.phet.quantum.view.PlateGraphic;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.util.ArrayList;
import java.util.Random;

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
    private double maxCurrent = 150;
    protected double currentDisplayFactor = 300;
    private ElementProperties[] elementProperties;
    private ConfigurableElementProperties configurableElement;

    // The scale to apply to graphics created in external applications so they appear properly
    // on the screen
    private double externalGraphicsScale;
    // AffineTransformOp that will scale graphics created in external applications so they appear
    // properly on the screen
    private AffineTransformOp externalGraphicScaleOp;
    private CurrentSlider currentSlider;
    private DischargeLampEnergyMonitorPanel2 energyLevelsMonitorPanel;
    private SpectrometerGraphic spectrometerGraphic;
    private HeatingElementGraphic[] heatingElementGraphics = new HeatingElementGraphic[2];
    private JCheckBox squiggleCB;
    private JPanel electronProductionControlPanel;
    private JPanel optionsPanel;

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
        model.setMaxCurrent( maxCurrent / currentDisplayFactor );
        leftHandPlate = model.getLeftHandPlate();
        rightHandPlate = model.getRightHandPlate();
        model.setVoltage( DEFAULT_VOLTAGE );
        setModel( model );
        setControlPanel( new ControlPanel( this ) );

        // Create the element properties we will use
        configurableElement = new ConfigurableElementProperties( 2, model );
        elementProperties = new ElementProperties[]{
                new HydrogenProperties(),
                new NeonProperties(),
                new SodiumProperties(),
                new MercuryProperties(),
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

    /**
     * Places a slider and digital readout on the battery graphic
     */
    private void addGraphicBatteryControls() {
        Battery battery = model.getBattery();
        final BatterySlider bSl = new BatterySlider( getApparatusPanel(), 80 /* track length */, battery,
                                                     DischargeLampsConfig.VOLTAGE_CALIBRATION_FACTOR );
        bSl.setMinimum( (int)-( battery.getMaxVoltage() ) );
        bSl.setMaximum( (int)( battery.getMaxVoltage() ) );
        bSl.setValue( (int)( DEFAULT_VOLTAGE ) );
        bSl.addTick( bSl.getMinimum() );
        bSl.addTick( bSl.getMaximum() );
        bSl.addTick( 0 );
        bSl.setLocation( (int)DischargeLampsConfig.CATHODE_LOCATION.getX() + 174, 60 );
        getApparatusPanel().addGraphic( bSl, DischargeLampsConfig.CIRCUIT_LAYER + 1 );

        final PhetGraphic batteryReadout = new BatteryReadout( getApparatusPanel(),
                                                               battery,
                                                               new Point( (int)DischargeLampsConfig.CATHODE_LOCATION.getX() + 194,
                                                                          78 ),
                                                               35 );
        addGraphic( batteryReadout, VOLTAGE_VALUE_LAYER );
    }

    /**
     * Adds the graphics for the heating elements
     */
    private void addHeatingElementGraphics() {
        HeatingElementGraphic leftHeatingElementGraphic = new HeatingElementGraphic( getApparatusPanel(), true );
        getApparatusPanel().addGraphic( leftHeatingElementGraphic );
        Point leftHandGraphicLocation = new Point( (int)model.getLeftHandHeatingElement().getPosition().getX()
                                                   - leftHeatingElementGraphic.getImage().getWidth() - 30,
                                                   (int)model.getLeftHandHeatingElement().getPosition().getY() - 80 );
        leftHeatingElementGraphic.setLocation( leftHandGraphicLocation );

        HeatingElementGraphic rightHeatingElementGraphic = new HeatingElementGraphic( getApparatusPanel(), false );
        getApparatusPanel().addGraphic( rightHeatingElementGraphic );
        Point rightHandGraphicLocation = new Point( (int)model.getRightHandHeatingElement().getPosition().getX() + 30,
                                                    (int)model.getRightHandHeatingElement().getPosition().getY() - 80 );
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
        anodeGraphic.setRegistrationPoint( (int)anodeGraphic.getBounds().getWidth(),
                                           (int)anodeGraphic.getBounds().getHeight() / 2 );
        anodeGraphic.setRegistrationPoint( 0, (int)anodeGraphic.getBounds().getHeight() / 2 );
        anodeGraphic.setLocation( DischargeLampsConfig.ANODE_LOCATION );
        apparatusPanel.addGraphic( anodeGraphic, DischargeLampsConfig.CIRCUIT_LAYER );
    }

    /**
     * @param apparatusPanel
     */
    private void addCathodeGraphic( ApparatusPanel apparatusPanel ) {
        PlateGraphic cathodeGraphic = new PlateGraphic( getApparatusPanel(), DischargeLampsConfig.CATHODE_LENGTH );
        model.getLeftHandHeatingElement().addChangeListener( cathodeGraphic );
        cathodeGraphic.setRegistrationPoint( (int)cathodeGraphic.getBounds().getWidth(),
                                             (int)cathodeGraphic.getBounds().getHeight() / 2 );
        cathodeGraphic.setLocation( DischargeLampsConfig.CATHODE_LOCATION );
        apparatusPanel.addGraphic( cathodeGraphic, DischargeLampsConfig.CIRCUIT_LAYER );
    }

    /**
     * @param apparatusPanel
     */
    private void addCircuitGraphic( ApparatusPanel apparatusPanel ) {
        CircuitGraphic circuitGraphic = new CircuitGraphic( apparatusPanel, getExternalGraphicScaleOp() );
        model.addChangeListener( circuitGraphic );
        circuitGraphic.setRegistrationPoint( (int)( 124 * externalGraphicsScale ), (int)( 340 * externalGraphicsScale ) );
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
        spectrometerGraphic.setLocation( centerX, 430 );
        spectrometerGraphic.setRegistrationPoint( spectrometerGraphic.getWidth() / 2, 0 );
    }

    /**
     * Returns an AffineTransformOp that will scale BufferedImages to the dimensions of the
     * apparatus panel
     *
     * @return
     */
    private AffineTransformOp getExternalGraphicScaleOp() {
        if( externalGraphicScaleOp == null ) {
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
        ControlPanel controlPanel = (ControlPanel)getControlPanel();

        // Set the minimum width
        controlPanel.addControlFullWidth( Box.createHorizontalStrut( 300)); /*, new GridBagConstraints( 0,0,1,1,1,1,
                                                                     GridBagConstraints.NORTH,
                                                                     GridBagConstraints.NONE,
                                                                     new Insets( 0,0,0,0),0,0 ) );*/

        // The legend
        controlPanel.addControlFullWidth( new Legend() );

        // A combo box for atom types
        {
            JComponent atomTypeComboBox = new AtomTypeChooser( model, elementProperties );
            controlPanel.addControlFullWidth( atomTypeComboBox );
        }

        // A slider for the battery current
        {
            electronProductionControlPanel = new JPanel();
            electronProductionControlPanel.setBorder( new TitledBorder( SimStrings.get( "Controls.ElectronProduction" ) ) );
            controlPanel.addControlFullWidth( electronProductionControlPanel );

            currentSlider = new CurrentSlider( maxCurrent );
            electronProductionControlPanel.add( currentSlider );
            currentSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    model.setCurrent( currentSlider.getValue(), 1 / currentDisplayFactor );
                }
            } );
            currentSlider.setValue( 10 );
        }

        // Options
        {
            optionsPanel = new JPanel( new GridBagLayout() );
            GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, //0,
                                                             1, 1, 1, 1,
                                                             GridBagConstraints.WEST,
                                                             GridBagConstraints.NONE,
                                                             new Insets( -2, 40, -2, 0 ), 0, 0 );
            optionsPanel.setBorder( new TitledBorder( SimStrings.get( "Controls.Options" ) ) );
            JPanel cbPanel = new JPanel( new GridLayout( 2, 1 ) );

            // Add a button to show/hide the spectrometer
            final JCheckBox spectrometerCB = new JCheckBox( SimStrings.get( "ControlPanel.SpectrometerButtonLabel" ) );
            spectrometerCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    spectrometerGraphic.setVisible( spectrometerCB.isSelected() );
                    model.getSpectrometer().reset();
                    model.getSpectrometer().start();
                }
            } );
            spectrometerGraphic.setVisible( spectrometerCB.isSelected() );

            squiggleCB = new JCheckBox( SimStrings.get( "Controls.Squiggles" ) );
            squiggleCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    energyLevelsMonitorPanel.setSquigglesEnabled( squiggleCB.isSelected() );
                }
            } );
            cbPanel.add( squiggleCB );
            optionsPanel.add( spectrometerCB, gbc );
            optionsPanel.add( squiggleCB, gbc );
            optionsPanel.add( new SlowMotionCheckBox( (Clock)getClock() ), gbc );
            controlPanel.addControlFullWidth( optionsPanel );
        }

        // Add an energy level monitor panel.
        {
            energyLevelsMonitorPanel = new DischargeLampEnergyMonitorPanel2( model,
                                                                             model.getAtomicStates(),
                                                                             200,
                                                                             300,
                                                                             configurableElement );
            controlPanel.addControl( energyLevelsMonitorPanel );
        }
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

        for( int i = 0; i < numAtoms; i++ ) {
            atom = new DischargeLampAtom( (LaserModel)getModel(), atomicStates );
            atom.setPosition( ( tubeBounds.getX() + ( Math.random() ) * ( tubeBounds.getWidth() - atom.getRadius() * 4 ) + atom.getRadius() * 2 ),
                              ( tubeBounds.getY() + ( Math.random() ) * ( tubeBounds.getHeight() - atom.getRadius() * 4 ) ) + atom.getRadius() * 2 );
            atom.setVelocity( (float)( Math.random() - 0.5 ) * maxSpeed,
                              (float)( Math.random() - 0.5 ) * maxSpeed );
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
        if( random.nextBoolean() ) {
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

    protected JPanel getElectronProductionControlPanel() {
        return electronProductionControlPanel;
    }

    protected JPanel getOptionsPanel() {
        return optionsPanel;
    }

    /**
     * @param isVisible
     */
    protected void setHeatingElementsVisible( boolean isVisible ) {
        for( int i = 0; i < heatingElementGraphics.length; i++ ) {
            HeatingElementGraphic graphic = heatingElementGraphics[i];
            graphic.setVisible( isVisible );
        }
    }

    /**
     * Returns a typed reference to the model
     */
    protected DischargeLampModel getDischargeLampModel() {
        return (DischargeLampModel)getModel();
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
    protected CurrentSlider getCurrentSlider() {
        return currentSlider;
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
            addGraphic( pg, LaserConfig.PHOTON_LAYER );
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
