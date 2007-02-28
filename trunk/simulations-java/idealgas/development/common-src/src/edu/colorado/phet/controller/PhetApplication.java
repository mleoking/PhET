/*
 * Class: PhetApplication
 * Package: edu.colorado.phet.controller
 *
 * User: Administrator
 * Date: Oct 25, 2002
 * Time: 9:16:15 PM
 */
package edu.colorado.phet.controller;

import edu.colorado.phet.graphics.GraphicFactory;
import edu.colorado.phet.physics.Force;
import edu.colorado.phet.physics.Law;
import edu.colorado.phet.physics.PhysicalSystem;
import edu.colorado.phet.physics.body.Particle;

import javax.swing.*;

/**
 * This is the base abstract class for all PhET applications. It acts as the central
 * controller between the physical system (the model), and the user interface (the view
 * and other user control elements ).
 * <p>
 * Each PhET application should have one concrete subclass of this class.
 * If an element in the user interface wishes to modify a single element in the physical
 * system, such as the velocity of a particle, it may act directly on that element. If,
 * however a user interface element wishes to communicate the physical system in a way that
 * may involve more than one of its elements, it should do so through a method in the
 * application-specific subclass of PhetApplication. An example would be modifying
 * the kinetic energy of every particle in the system.
 */
public abstract class PhetApplication {

    private PhetFrame phetFrame;
    private PhysicalSystem physicalSystem;
    private PhetMainPanel phetPanel;
    private boolean isRunning;

    /**
     *
     */
    public PhetApplication() {
        // NOP
    }

    /**
     * @param physicalSystem The PhysicalSystem in this application
     */
    public PhetApplication( PhysicalSystem physicalSystem ) {
        init( physicalSystem );
    }

    /**
     * Initializes the application with a specified PhysicalSystem.
     * @param physicalSystem
     */
    public void init( PhysicalSystem physicalSystem ) {

        // Insure singleton
        if( s_instance != null ) {
            throw new RuntimeException( "Attempt to instantiate second PhetApplication." );
        }
        s_instance = this;

        this.physicalSystem = physicalSystem;
        phetFrame = new PhetFrame( this, getConfig() );
        phetPanel = this.createMainPanel();
        phetPanel.init();

        // This must not be done until after the main panel has been created
        phetFrame.addMainPanel( phetPanel );
    }

    /**
     *
     */
    public void init() {
        this.getPhysicalSystem().clear();
    }

    /**
     * Starts the application. This should be called on the application in main()
     * once the application has been instantiated. It should not be called again.
     */
    public void start() {
        this.getPhetFrame().show();
    }

    /**
     * TODO: This can probably be done away with by using the Swing call that
     * gets the parent frame
     * @return
     */
    public PhetFrame getPhetFrame() {
        return phetFrame;
    }

    /**
     * Returns the PhetMainPanel for the application
     * @return The application's PhetMainPanel
     */
    public PhetMainPanel getPhetMainPanel() {
        return phetPanel;
    }

    /**
     * Adds an external force to the application's PhysicalSystem.
     * <p>
     * External forces are forces that do not arise from the interaction
     * of elements in the PhysicalSystem. An example is gravity, if the
     * Earth is not represented explicity in the PhysicalSystem.
     *
     * @param force The external force to be added
     */
    public void addExternalForce( Force force ) {
        physicalSystem.addExternalForce( force );
    }

    /**
     * Sets the PhysicalSystem's clock parameters
     *
     * @param dt The duration represented by a single step in the PhysicalSystem's clock
     * @param sleepTime The CPU sleep time between time steps
     * @param timeLimit The time at which the clock should stop. If 0, the clock will
     * run indefinitely
     */
    public void setClockParams( float  dt, int sleepTime, float  timeLimit ) {
        physicalSystem.setClockParams( dt, sleepTime, timeLimit );
    }

    /**
     * Runs the PhysicalSystem. This may be called many times
     * during one execution of the application. It is, for example, called by the
     * ApplicationControlPanel each time the "Run" button is clicked.
     */
    public void run() {
        isRunning = true;
        physicalSystem.start();

        // The following line was causing the CollimatedBeam in the Laser application
        // to be relocated to (0,0). I'm not sure if it needs to be here in the first place
//        getPhysicalSystem().reInitialize();
        phetPanel.reset();
    }

    /**
     * Stops the PhysicalSystem. This may be called many times
     * during one execution of the application. It is, for example, called by the
     * ApplicationControlPanel each time the "Stop" button is clicked.
     */
    public void stop() {
        isRunning = false;
        physicalSystem.stop();
    }

    /**
     * Clears the PhysicalSystem and the user interface.
     */
    public void clear() {
        getPhysicalSystem().clear();
        if( phetPanel != null ) {
            phetPanel.clear();
        }
    }

    /**
     * Tells if the PhysicalSystem is running
     * @return
     */
    public boolean isRunning() {
        return physicalSystem.isRunning();
    }

    /**
     * Adds a Particle to the PhysicalSystem
     * @param body The body to be added
     */
    public void addBody( Particle body ) {
        physicalSystem.addBody( body );
        phetPanel.addBody( body );
    }

    /**
     * Adds a Particle to the PhysicalSystem
     * @param body The body to be added
     */
//    public void addBodyAllApparatusPanels( Particle body ) {
//        physicalSystem.addBody( body );
//        ((TabbedMainPanel)phetPanel).addBodyAllPanels( body );
//    }

    /**
     * Adds a Particle to the PhysicalSystem, and specifies the layer to which its
     * associated PhetGraphic should be assigned in the user interface
     * @see edu.colorado.phet.graphics.PhetGraphic
     * @see edu.colorado.phet.graphics.ApparatusPanel
     * @see GraphicFactory
     * @param body The Particle to be added
     * @param level The level of the graphic layer to which the Particle's associated
     * PhetGraphic is to be added
     */
    public void addBody( edu.colorado.phet.physics.body.Particle body, int level ) {
        physicalSystem.addBody( body );
        phetPanel.addBody( body, level );
    }

    /**
     * Adds a Law to the PhysicalSystem
     * @see Law
     * @param law The Law to be added
     */
    public void addLaw( Law law ) {
        physicalSystem.addLaw( law );
    }

    /**
     * Returns the application's PhysicalSystem
     * @return The application's PhysicalSystem
     */
    public PhysicalSystem getPhysicalSystem() {
        return physicalSystem;
    }

    /**
     * Toggles the single step feature of the PhysicalSystem. This is handy
     * for debugging
     */
    public void toggleSingleStep() {
        getPhysicalSystem().setSingleStepEnabled( !getPhysicalSystem().isSingleStepEnabled() );
    }

    //
    // Abstract methods
    //

    /**
     * Creates an instance of an application-specific concrete subclass
     * of PhetMailPanel
     * @see PhetMainPanel
     * @return an instance of an application-specific concrete subclass
     * of PhetMailPanel
     */
    protected abstract PhetMainPanel createMainPanel();

    /**
     * Creates an menu of controls for the application. May return null.
     * @see PhetFrame
     * @param phetFrame The PhetFrame instance for the application
     * @return A menu of controls for the application
     */
    protected abstract JMenu createControlsMenu( PhetFrame phetFrame );

    /**
     * Creates a menu of tests for the application. May return null.
     * @return A menu of tests
     */
    protected abstract JMenu createTestMenu();

    /**
     * Returns an instance of an application-specific concrete subclass of
     * GraphicFactory
     * @see GraphicFactory
     * @return An instance of an application-specific concrete subclass of
     * GraphicFactory
     */
    public abstract GraphicFactory getGraphicFactory();

    /**
     * Returns an instance of an application-specific concrete subclass of
     * PhetAboutDialog
     * @see PhetAboutDialog
     * @return An instance of an application-specific concrete subclass of
     * GraphicFactory
     */
    protected abstract PhetAboutDialog getAboutDialog( PhetFrame phetFrame );

    /**
     * Returns an instance of an application-specific concrete subclass of
     * Config
     * @see Config
     * @return An instance of an application-specific concrete subclass of
     * Config
     */
    protected abstract Config getConfig();

    //
    // Static fields and methods
    private static PhetApplication s_instance;
    public static PhetApplication instance() {
        return s_instance;
    }
}
