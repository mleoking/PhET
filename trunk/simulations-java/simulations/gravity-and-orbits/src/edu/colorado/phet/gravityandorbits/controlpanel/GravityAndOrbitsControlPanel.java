/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.controlpanel;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.IsSelectedProperty;
import edu.colorado.phet.common.phetcommon.view.LogoPanel;
import edu.colorado.phet.common.phetcommon.view.PhetLineBorder;
import edu.colorado.phet.common.phetcommon.view.PhetTitledBorder;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;
import edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsCanvas;

/**
 * Control panel template.
 */
public class GravityAndOrbitsControlPanel extends VerticalLayoutPanel {
    public static Color BACKGROUND = new Color( 3, 0, 133 );
    public static Color FOREGROUND = Color.white;
    public static final Font CONTROL_FONT = new PhetFont( 18, true );

    public GravityAndOrbitsControlPanel( final GravityAndOrbitsModule module, GravityAndOrbitsModel model ) {
        super();

        final LogoPanel panel = new LogoPanel();
        panel.setBackground( BACKGROUND );
        addControl( panel );

        for ( GravityAndOrbitsMode mode : module.getModes() ) {
            addControlFullWidth( new GORadioButton( mode.getName(), new IsSelectedProperty<GravityAndOrbitsMode>( mode, module.getModeProperty() ) ) );
        }

        addControlFullWidth( new VerticalLayoutPanel() {{
            setBackground( BACKGROUND );
            setOpaque( false );
            setBorder( new PhetTitledBorder( new PhetLineBorder( Color.white ), "Show" ) {{
                setTitleColor( Color.white );
                setTitleFont( CONTROL_FONT );
            }} );
            add( new GOCheckBox( "GravityForce", module.getShowGravityForceProperty() ) );
            add( new GOCheckBox( "Velocity", module.getShowVelocityProperty() ) );
            add( new GOCheckBox( "Mass", module.getShowMassProperty() ) );
            add( new GOCheckBox( "Path", module.getShowPathProperty() ) );
        }} );
        addControlFullWidth( new BodyMassControl( model.getSun(), model.getSun().getMassProperty().getDefaultValue() / 2, model.getSun().getMassProperty().getDefaultValue() * 2, "Large", "Very Large", GravityAndOrbitsCanvas.SUN_SIZER ) );
        addControlFullWidth( new BodyMassControl( model.getPlanet(), model.getPlanet().getMassProperty().getDefaultValue() / 2, model.getPlanet().getMassProperty().getDefaultValue() * 2, "Very Small", "Small", GravityAndOrbitsCanvas.PLANET_SIZER ) );
        addControlFullWidth( new GOCheckBox( "Moon", module.getMoonProperty() ) );

        setBackground( BACKGROUND );
//        getContentPanel().setBackground( BACKGROUND );
    }

    private void addControlFullWidth( JComponent component ) {
        add( component );
    }

    private void addControl( JComponent panel ) {
        add( panel );
    }

    private double earthMassesToSI( double v ) {
        return v * 5.9742E24;
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    public void closeAllDialogs() {
        //XXX close any dialogs created via the control panel
    }

}
