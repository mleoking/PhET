/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.application;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.*;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;

/**
 * The Module is the fundamental unit of a phet simulation.
 * It entails graphics, controls and a model.
 */

public abstract class Module {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private String name;

    private BaseModel model;
    private IClock clock;

    private boolean active = false;
    private boolean clockRunningWhenActive = true;
    private ClockAdapter moduleRunner;
    
    private ModulePanel modulePanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Initialize an emtpy module.  
     * This is for subclasses who intend something rather different than the norm.
     */
    protected Module() {
    }

    /**
     * Initialize a Module.
     * By default, the clock will be running when the module is activated.
     *
     * @param name  the name for this Module
     * @param clock a clock to model passage of time for this Module.  Should be unique to this module (non-shared).
     */
    public Module( String name, IClock clock ) {
        this( name, clock, false );
    }

    /**
     * Initializes a Module.
     * 
     * @param name
     * @param clock
     * @param startsPaused
     */
    public Module( String name, IClock clock, boolean startsPaused ) {
        this.name = name;
        this.clock = clock;
        setModel( new BaseModel() );
        SimStrings.setStrings( "localization/CommonStrings" );

        this.modulePanel = new ModulePanel();
        setClockControlPanel( new ClockControlPanel( clock ) );
        setLogoPanel( new LogoPanel() );
        setHelpPanel( new HelpPanel( this ) );
        
        moduleRunner = new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                handleClockTick( clockEvent );
            }
        };
        clock.addClockListener( moduleRunner );
        this.clockRunningWhenActive = !startsPaused;
    }
    
    //----------------------------------------------------------------------------
    // Clock
    //----------------------------------------------------------------------------
    
    /**
     * Get the clock associated with this Module.
     *
     * @return the clock
     */
    public IClock getClock() {
        return clock;
    }

    /**
     * During a clock tick, the Module will handle user input, step the model, and update the graphics.
     *
     * @param event
     */
    protected void handleClockTick( ClockEvent event ) {
        handleUserInput();
        model.update( event );
        updateGraphics( event );
    }
    
    //----------------------------------------------------------------------------
    // Model
    //----------------------------------------------------------------------------
    
    /**
     * Sets the base model.
     *
     * @param model
     */
    public void setModel( BaseModel model ) {
        this.model = model;
    }

    /**
     * Gets the base model.
     *
     * @return the model
     */
    public BaseModel getModel() {
        return model;
    }

    /**
     * Adds a ModelElement to the BaseModel of this Module.
     *
     * @param modelElement
     */
    protected void addModelElement( final ModelElement modelElement ) {
        getModel().addModelElement( modelElement );
    }
    
    //----------------------------------------------------------------------------
    // Panel Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Get the ModulePanel, which contains all the subpanels for the module.
     *
     * @return the ModulePanel.
     */
    public ModulePanel getModulePanel() {
        return modulePanel;
    }
    
    /**
     * Sets the monitor panel.
     *
     * @param panel, possibly null
     */
    public void setMonitorPanel( JComponent panel ) {
        modulePanel.setMonitorPanel( panel );
    }
    
    /**
     * Gets the monitor panel.
     * 
     * @return the monitor 
     */
    public JComponent getMonitorPanel() {
        return modulePanel.getMonitorPanel();
    }
    
    /**
     * Sets the simulation panel.
     *
     * @param panel, possibly null
     */
    protected void setSimulationPanel( JComponent panel ) {
        modulePanel.setSimulationPanel( panel );
    }
    
    /**
     * Gets the simulation panel.
     * 
     * @return the simulation panel
     */
    public JComponent getSimulationPanel() {
        return modulePanel.getSimulationPanel();
    }

    /**
     * Sets the clock control panel.
     *
     * @param panel
     */
    protected void setClockControlPanel( JComponent panel ) {
        modulePanel.setClockControlPanel( panel );
    }
    
    /**
     * Gets the clock control panel.
     * If you have replaced the standard ClockControlPanel,
     * then use getModulePanel().getClockControlPanel().
     *
     * @return ClockControlPanel
     */
    public ClockControlPanel getClockControlPanel() {
        ClockControlPanel clockControlPanel = null;
        JComponent panel = modulePanel.getClockControlPanel();
        if ( panel != null && panel instanceof ClockControlPanel ) {
            clockControlPanel = (ClockControlPanel)panel;
        }
        return clockControlPanel;
    }
    
    /**
     * Sets the logo panel.
     *
     * @param panel
     */
    protected void setLogoPanel( JComponent panel ) {
        modulePanel.setLogoPanel( panel );
    }
    
    /**
     * Gets the logo panel.
     * If you have replaced the standard LogoPanel,
     * then use getModulePanel().getLogoPanel().
     * 
     * @return LogoPanel
     */
    public LogoPanel getLogoPanel() {
        LogoPanel logoPanel = null;
        JComponent panel = modulePanel.getLogoPanel();
        if ( panel != null && panel instanceof LogoPanel ) {
            logoPanel = (LogoPanel)panel;
        }
        return logoPanel;
    }
    
    /**
     * Sets the control panel.
     *
     * @param panel
     */
    protected void setControlPanel( JComponent panel ) {
        modulePanel.setControlPanel( panel );
    }
    
    /**
     * Gets the control panel.
     * If you have replaced the standard ControlPanel,
     * then use getModulePanel().getControlPanel().
     * 
     * @return ControlPanel
     */
    public ControlPanel getControlPanel() {
        ControlPanel controlPanel = null;
        JComponent panel = modulePanel.getControlPanel();
        if ( panel != null && panel instanceof ControlPanel ) {
            controlPanel = (ControlPanel)panel;
        }
        return controlPanel;
    }
    
    /**
     * Sets the help panel.
     *
     * @param panel
     */
    protected void setHelpPanel( JComponent panel ) {
        modulePanel.setHelpPanel( panel );
    }
    
    /**
     * Gets the help panel.
     * If you have replaced the standard HelpPanel,
     * then use getModulePanel().getHelpPanel().
     * 
     * @return HelpPanel
     */
    public HelpPanel getHelpPanel() {
        HelpPanel helpPanel = null;
        JComponent panel = modulePanel.getHelpPanel();
        if ( panel != null && panel instanceof HelpPanel ) {
            helpPanel = (HelpPanel)panel;
        }
        return helpPanel;
    }
    
    //----------------------------------------------------------------------------
    // Help
    //----------------------------------------------------------------------------
    
   /**
    * Tells whether this module has on-screen help.
    *
    * @return whether this module has on-screen help
    */
   public boolean hasHelp() {
       return false;
   }
   
   /**
    * This must be overriden by subclasses that have megahelp to return true.
    *
    * @return whether there is megahelp
    */
   public boolean hasMegaHelp() {
       return false;
   }

   /**
    * Determines whether help is visible.
    * If you replace HelpPanel with a different type of help panel,
    * then you will need to override this method.
    *
    * @param enabled
    */
   public void setHelpEnabled( boolean enabled ) {
       HelpPanel helpPanel = getHelpPanel();
       if ( helpPanel != null ) {
           helpPanel.setHelpEnabled( enabled );
       }
   }

   /**
    * Gets whether help is currently visible for this Module.
    * If you replace HelpPanel with a different type of help panel,
    * then you will need to override this method.
    *
    * @return help
    */
   public boolean isHelpEnabled() {
       boolean enabled = false;
       HelpPanel helpPanel = getHelpPanel();
       if ( helpPanel != null ) {
           enabled = helpPanel.isHelpEnabled();
       }
       return enabled;
   }
   
   /**
    * This must be overrideen by subclasses that have MegaHelp.
    */
   public void showMegaHelp() {
   }
    
    //----------------------------------------------------------------------------
    // Logo
    //----------------------------------------------------------------------------
    
    /**
     * Shows/hides the PhET logo.
     * 
     * @param visible
     */
    public void setLogoPanelVisible( boolean visible ) {
        JComponent logoPanel = modulePanel.getLogoPanel();
        if ( logoPanel != null ) {
            logoPanel.setVisible( visible );
        }
    }

    /**
     * Is the PhET logo visible?
     * 
     * @return true or false
     */
    public boolean isLogoPanelVisible() {
        boolean visible = false;
        JComponent logoPanel = modulePanel.getLogoPanel();
        if ( logoPanel != null ) {
            visible = logoPanel.isVisible();
        }
        return visible;
    }
    
    //----------------------------------------------------------------------------
    // Activation
    //----------------------------------------------------------------------------

    /**
     * Activates the Module, starting it, if necessary.
     */
    public void activate() {
        if( !isWellFormed() ) {
            throw new RuntimeException( "Module missing important data, module=" + this );
        }
        if( clockRunningWhenActive ) {
            clock.start();
        }
        JComponent helpPanel = getHelpPanel();
        if( helpPanel != null ) {
            helpPanel.setVisible( hasHelp() );
        }
        active = true;
    }

    /**
     * Deactivates this Module (pausing it).
     */
    public void deactivate() {
        this.clockRunningWhenActive = getClock().isRunning();
        clock.pause();
        active = false;
    }

    /**
     * Determine if the Module is currently an active module.
     *
     * @return true or false
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Determine whether the Module has all the necessary information to run.
     *
     * @return true if the Module is ready to run.
     */
    public boolean isWellFormed() {
        boolean result = true;
        result &= this.getModel() != null;
        result &= this.getSimulationPanel() != null;
        return result;
    }

    //----------------------------------------------------------------------------
    // Misc.
    //----------------------------------------------------------------------------
    
    /**
     * Get the name of the Module.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns a String to represent the Module.
     *
     * @return a string
     */
    public String toString() {
        return "name=" + name + ", model=" + model + ", simulationPanel=" + getSimulationPanel();
    }

    /**
     * Returns a ModuleStateDescriptor for this Module.
     * <p/>
     * This method should be extended by subclasses that have state attributes.
     *
     * @return a ModuleStateDescriptor for this Module.
     */
    public ModuleStateDescriptor getModuleStateDescriptor() {
        return new ModuleStateDescriptor( this );
    }

    /**
     * Get any help for persistence.
     *
     * @return an array of Classes which can be used as transient property sources.
     */
    public Class[] getTransientPropertySources() {
        return new Class[0];
    }

    /**
     * Notifies the Module that this is the reference (default) size for rendering.
     */
    public void setReferenceSize() {
    }

    /**
     * Any specific user input code may go here (no-op in base class).
     */
    protected void handleUserInput() {
    }

    /**
     * Any module that wants to do some graphics updating that isn't handled through
     * model element/observer mechanisms can overide this method
     *
     * @param event
     */
    public void updateGraphics( ClockEvent event ) {
    }
}