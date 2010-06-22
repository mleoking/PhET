/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.neuron.developer.HodgkinHuxleyInternalDynamicsDlg;
import edu.colorado.phet.neuron.developer.HodgkinHuxleyInternalParamsDlg;
import edu.colorado.phet.neuron.module.NeuronModule;

/**
 * NeuronApplication is the main application for this simulation.
 *
 * @author John Blanco
 */
public class NeuronApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private NeuronModule neuronModule;
    
    // Developer window(s) and things for controlling them.
    private HodgkinHuxleyInternalDynamicsDlg hhInternalDynamicsDlg = null;
	private JCheckBoxMenuItem hhModelDynamicsControl;
    private HodgkinHuxleyInternalParamsDlg hhInternalParamsDlg = null;
	private JCheckBoxMenuItem hhModelParamsControl;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public NeuronApplication( PhetApplicationConfig config )
    {
        super( config );
        initModules();
        initMenubar( config.getCommandLineArgs() );
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /**
     * Initializes the modules.
     */
    private void initModules() {
        
        Frame parentFrame = getPhetFrame();

        neuronModule = new NeuronModule( parentFrame );
        addModule( neuronModule );
    }

    /**
     * Initializes the menu bar.
     */
    private void initMenubar( String[] args ) {

        final PhetFrame frame = getPhetFrame();

        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu();
        // add menu items here, or in a subclass on OptionsMenu
        if ( optionsMenu.getMenuComponentCount() > 0 ) {
            frame.addMenu( optionsMenu );
        }
        
        // Developer menu
        JMenu developerMenu = frame.getDeveloperMenu();
        
        hhModelDynamicsControl = new JCheckBoxMenuItem( "Show Internal HH Model Dynamics" );
        developerMenu.add( hhModelDynamicsControl );
        hhModelDynamicsControl.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setHHModelDynamicsWindowVisible(hhModelDynamicsControl.isSelected());
            }
        } );

        hhModelParamsControl = new JCheckBoxMenuItem( "Show Internal HH Model Parameters" );
        developerMenu.add( hhModelParamsControl );
        hhModelParamsControl.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setHHModelParamsWindowVisible(hhModelParamsControl.isSelected());
            }
        } );
    }
    
    private void setHHModelDynamicsWindowVisible(boolean isVisible){
    	
    	if (isVisible && hhInternalDynamicsDlg == null){
    		// The internal dynamics window has not been created yet, so create it now.
    		hhInternalDynamicsDlg = new HodgkinHuxleyInternalDynamicsDlg(getPhetFrame(),
    				neuronModule.getClock(), neuronModule.getHodgkinHuxleyModel());
    		
    		// Just hide when closed so we don't have to keep recreating it.
    		hhInternalDynamicsDlg.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    		
    		// Center the window on the screen (initially - it will retain its
    		// position if moved after that).
    		hhInternalDynamicsDlg.setLocationRelativeTo(null);
    		
    		// Clear the check box if the user closes this by closing the
    		// dialog itself.
    		hhInternalDynamicsDlg.addWindowListener(new WindowAdapter() {
    			public void windowClosing(WindowEvent e){
    				hhModelDynamicsControl.setSelected(false);
    			}
			});
    	}
    	
    	if (hhInternalDynamicsDlg != null){
    		hhInternalDynamicsDlg.setVisible(isVisible);
    	}
    }

    private void setHHModelParamsWindowVisible(boolean isVisible){
    	
    	if (isVisible && hhInternalParamsDlg == null){
    		// The internal parameters window has not been created yet, so create it now.
    		hhInternalParamsDlg = new HodgkinHuxleyInternalParamsDlg( getPhetFrame(), neuronModule.getHodgkinHuxleyModel() );
    		
    		// Just hide when closed so we don't have to keep recreating it.
    		hhInternalParamsDlg.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    		
    		// Center the window on the screen (initially - it will retain its
    		// position if moved after that.
    		hhInternalParamsDlg.setLocationRelativeTo(null);
    		
    		// Clear the check box if the user closes this by closing the
    		// dialog itself.
    		hhInternalParamsDlg.addWindowListener(new WindowAdapter() {
    			public void windowClosing(WindowEvent e){
    				hhModelParamsControl.setSelected(false);
    			}
			});
    	}
    	
    	if (hhInternalParamsDlg != null){
    		hhInternalParamsDlg.setVisible(isVisible);
    	}
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    /**
     * Main entry point for this simulation.
     */
    public static void main( final String[] args ) throws ClassNotFoundException {
        /* 
         * If you want to customize your application (look-&-feel, window size, etc) 
         * create your own PhetApplicationConfig and use one of the other launchSim methods
         */
        new PhetApplicationLauncher().launchSim( args, NeuronConstants.PROJECT_NAME, NeuronApplication.class );
    }
}
