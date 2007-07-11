/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.opticaltweezers.persistence.OTConfig;


/**
 * AbstractModule is the base class for all modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractModule extends PiccoloModule {
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param title the module title
     * @param clock the simulation clock
     * @param startsPaused initial clock state
     */
    public AbstractModule( String title, IClock clock, boolean startsPaused ) {
        super( title, clock, startsPaused );
        setLogoPanel( null );
    }
    
    //----------------------------------------------------------------------------
    // Abstract
    //----------------------------------------------------------------------------

    /**
     * Resets the module to its initial state.
     */
    public abstract void resetAll();
    
    /**
     * Saves the module's configuration by writing it to a provided configuration object.
     * 
     * @param appConfig
     */
    public abstract void save( OTConfig appConfig );
    
    /**
     * Loads the module's configuration by reading it from a provided configuration object.
     * 
     * @param appConfig
     */
    public abstract void load( OTConfig appConfig );
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets this module's frame.
     * 
     * @return JFrame
     */
    public JFrame getFrame() {
        return PhetApplication.instance().getPhetFrame();
    }
    
    /**
     * Adds a listener to the module's clock.
     * 
     * @param listener
     */
    public void addClockListener( ClockListener listener ) {
        getClock().addClockListener( listener );
    }
    
    /**
     * Removes a listener from the module's clock.
     * 
     * @param listener
     */
    public void removeClockListener( ClockListener listener ) {
        getClock().removeClockListener( listener );
    }
    
    /**
     * Sets the background color of the control panel and clock control panel.
     * 
     * @param color
     */
    public void setControlPanelBackground( Color color ) {
        
        Class[] excludedClasses = { JTextField.class };
        boolean processChildrenOfExcludedContainers = false;
        
        Component controlPanel = getControlPanel();
        setComponentTreeColor( controlPanel, color, excludedClasses, processChildrenOfExcludedContainers );
        
        Component clockControlPanel = getClockControlPanel();
        setComponentTreeColor( clockControlPanel, color, excludedClasses, processChildrenOfExcludedContainers );
    }
    
    /**
     * Gets the background color of the control panel and clock control panel.
     * 
     * @return Color
     */
    public Color getControlPanelBackground() {
        Component controlPanel = getControlPanel();
        return controlPanel.getBackground();
    }
    
    /*
     * Sets the background of all components in a container hierachy.
     * Any component that is an instance of one of the classes in 
     * excludedClasses will not have its background color changed.
     * The children of excluded containers will be processed based
     * on the value of processChildrenOfExcludedContainers.
     * 
     * @param component
     * @param color
     * @param excludeClasses
     * @param processChildrenOfExcludedContainers
     */
    private void setComponentTreeColor( Component component, Color color, Class[] excludedClasses, boolean processChildrenOfExcludedContainers ) {

        // If this component one that should be excluded?
        boolean excluded = false;
        if ( excludedClasses != null ) {
            for ( int i = 0; i < excludedClasses.length && !excluded; i++ ) {
                if ( excludedClasses[i].isInstance( component ) ) {
                    excluded = true;
                }
            }
        }

        // If not exluded, set the component's background.
        if ( !excluded ) {
            component.setBackground( color );
        }
        
        // Recursively process the contents of containers.
        if ( ( !excluded || processChildrenOfExcludedContainers ) && ( component instanceof Container ) ) {
            Container container = (Container) component;
            Component[] children = container.getComponents();
            if ( children != null ) {
                for ( int i = 0; i < children.length; i++ ) {
                    setComponentTreeColor( children[i], color, excludedClasses, processChildrenOfExcludedContainers );
                }
            }
        }
    }
}
