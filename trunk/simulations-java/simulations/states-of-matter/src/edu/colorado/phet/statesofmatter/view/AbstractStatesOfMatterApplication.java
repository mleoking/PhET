// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.phetcommon.view.menu.TeacherMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.statesofmatter.StatesOfMatterGlobalState;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.developer.DeveloperControlsMenuItem;

/**
 * Base class for States of Matter simulations.  This was created primarily to
 * avoid duplication of code between the first States of Matter flavor and
 * the newer "basics" flavor.
 *
 * @author John Blanco
 */
public class AbstractStatesOfMatterApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Sole Constructor
    //----------------------------------------------------------------------------

    public AbstractStatesOfMatterApplication( PhetApplicationConfig config ) {
        super( config );
        initMenubar();
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /**
     * Initializes the menu bar.
     */
    protected void initMenubar() {
        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu();
        final JRadioButtonMenuItem kelvinRadioButton = new JRadioButtonMenuItem( StatesOfMatterStrings.KELVIN ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    StatesOfMatterGlobalState.temperatureUnitsProperty.set( TemperatureUnits.KELVIN );
                }
            } );
        }};
        optionsMenu.add( kelvinRadioButton );

        final JRadioButtonMenuItem celsiusRadioButton = new JRadioButtonMenuItem( StatesOfMatterStrings.CELSIUS ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    StatesOfMatterGlobalState.temperatureUnitsProperty.set( TemperatureUnits.CELSIUS );
                }
            } );
        }};
        optionsMenu.add( celsiusRadioButton );
        StatesOfMatterGlobalState.temperatureUnitsProperty.addObserver( new VoidFunction1<TemperatureUnits>() {
            public void apply( TemperatureUnits temperatureUnitsValue ) {
                kelvinRadioButton.setSelected( temperatureUnitsValue == TemperatureUnits.KELVIN );
                celsiusRadioButton.setSelected( temperatureUnitsValue == TemperatureUnits.CELSIUS );
            }
        } );

        getPhetFrame().addMenu( optionsMenu );

        // Add a Teacher menu with an item to change the background to white
        // for use in making handouts, on projectors, etc.
        getPhetFrame().addMenu( new TeacherMenu() {{
            addWhiteBackgroundMenuItem( StatesOfMatterGlobalState.whiteBackground );
        }} );

        // Developer menu
        JMenu developerMenu = getPhetFrame().getDeveloperMenu();
        developerMenu.add( new DeveloperControlsMenuItem( this ) );
    }
}
