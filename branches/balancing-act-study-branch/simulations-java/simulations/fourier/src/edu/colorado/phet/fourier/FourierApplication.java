// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.fourier;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fourier.control.FourierOptionsMenu;
import edu.colorado.phet.fourier.module.D2CModule;
import edu.colorado.phet.fourier.module.DiscreteModule;
import edu.colorado.phet.fourier.module.GameModule;
import edu.colorado.phet.fourier.persistence.FourierConfig;
import edu.colorado.phet.fourier.view.HarmonicColors;


/**
 * FourierApplication is the main application for the PhET "Fourier Analysis" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private DiscreteModule _discreteModule;
    private GameModule _gameModule;
    private D2CModule _d2cModule;
    private XMLPersistenceManager _persistenceManager; // Save/Load feature

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     */
    public FourierApplication( PhetApplicationConfig config ) {
        super( config );
        initModules();
        initMenubar();
    }

    //----------------------------------------------------------------------------
    // Modules
    //----------------------------------------------------------------------------

    /*
     * Initializes the modules.
     *
     * @param clock
     */
    private void initModules() {

        _discreteModule = new DiscreteModule();
        addModule( _discreteModule );

        _gameModule = new GameModule();
        addModule( _gameModule );

        _d2cModule = new D2CModule();
        addModule( _d2cModule );
    }

    //----------------------------------------------------------------------------
    // Menubar
    //----------------------------------------------------------------------------

    /*
     * Initializes the menubar.
     */
    private void initMenubar() {

        // File->Save/Load
        PhetFrame frame = getPhetFrame();
        frame.addFileSaveLoadMenuItems();
        if ( _persistenceManager == null ) {
            _persistenceManager = new XMLPersistenceManager( frame );
        }

        // Options menu
        FourierOptionsMenu optionsMenu = new FourierOptionsMenu( this );
        getPhetFrame().addMenu( optionsMenu );
    }

    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    @Override
    public void save() {

        FourierConfig appConfig = new FourierConfig();

        // Global config
        {
            FourierConfig.GlobalConfig globalConfig = appConfig.getGlobalConfig();
            appConfig.setGlobalConfig( globalConfig );

            // Version & build info
            globalConfig.setVersionNumber( getSimInfo().getVersion().formatForTitleBar() );

            // Harmonic colors
            int[] r = new int[HarmonicColors.getInstance().getNumberOfColors()];
            int[] g = new int[r.length];
            int[] b = new int[r.length];
            for ( int i = 0; i < r.length; i++ ) {
                Color color = HarmonicColors.getInstance().getColor( i );
                r[i] = color.getRed();
                g[i] = color.getGreen();
                b[i] = color.getBlue();
            }
            globalConfig.setHarmonicColorsRed( r );
            globalConfig.setHarmonicColorsGreen( g );
            globalConfig.setHarmonicColorsBlue( b );
        }

        // Modules
        appConfig.setDiscreteConfig( _discreteModule.save() );
        appConfig.setGameConfig( _gameModule.save() );
        appConfig.setD2CConfig( _d2cModule.save() );

        _persistenceManager.save( appConfig );
    }

    @Override
    public void load() {

        Object object = _persistenceManager.load();

        if ( object != null ) {

            if ( object instanceof FourierConfig ) {

                FourierConfig appConfig = (FourierConfig) object;

                // Globals
                {
                    // Harmonic colors
                    int[] r = appConfig.getGlobalConfig().getHarmonicColorsRed();
                    int[] g = appConfig.getGlobalConfig().getHarmonicColorsGreen();
                    int[] b = appConfig.getGlobalConfig().getHarmonicColorsBlue();
                    for ( int i = 0; i < r.length; i++ ) {
                        HarmonicColors.getInstance().setColor( i, new Color( r[i], g[i], b[i] ) );
                    }
                }

                // Modules
                _discreteModule.load( appConfig.getDiscreteConfig() );
                _gameModule.load( appConfig.getGameConfig() );
                _d2cModule.load( appConfig.getD2CConfig() );
            }
            else {
                String message = PhetCommonResources.getString( "XMLPersistenceManager.message.notAConfigFile" );
                PhetOptionPane.showErrorDialog( getPhetFrame(), message );
            }
        }
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, FourierConstants.PROJECT_NAME, FourierApplication.class );
    }
}
