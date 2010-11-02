/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.controlpanel;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.LogoPanel;
import edu.colorado.phet.common.phetcommon.view.PhetLineBorder;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsResources;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;

/**
 * Control panel template.
 */
public class GravityAndOrbitsControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param module
     * @param parentFrame parent frame, for creating dialogs
     */
    public GravityAndOrbitsControlPanel( GravityAndOrbitsModule module, Frame parentFrame, GravityAndOrbitsModel model ) {
        super();

        // Set the control panel's minimum width.
        int minimumWidth = GravityAndOrbitsResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );

        final LogoPanel panel = new LogoPanel();
        panel.setBackground( BACKGROUND );
        addControl( panel );
        addControlFullWidth( new GOCheckBox( "Forces", module.getForcesProperty() ) );
        addControlFullWidth( new GOCheckBox( "Traces", module.getTracesProperty() ) );
        addControlFullWidth( new GOCheckBox( "Velocity", module.getVelocityProperty() ) );
        addControlFullWidth( new GOCheckBox( "Show Masses", module.getShowMassesProperty() ) );
        addControlFullWidth( new GOCheckBox( "To Scale", module.getToScaleProperty() ) );
        addControlFullWidth( new BodyDiameterControl( model.getSun() ) );
        addControlFullWidth( new BodyDiameterControl( model.getPlanet() ) );
        addControlFullWidth( new GOCheckBox( "Moon", module.getMoonProperty() ) );

        addResetAllButton( module );
        setBackground( BACKGROUND );
        getContentPanel().setBackground( BACKGROUND );
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    public void closeAllDialogs() {
        //XXX close any dialogs created via the control panel
    }

    //----------------------------------------------------------------------------
    // Access to subpanels
    //----------------------------------------------------------------------------

    public static Color BACKGROUND = Color.blue;

    public static Color FOREGROUND = Color.white;
}
