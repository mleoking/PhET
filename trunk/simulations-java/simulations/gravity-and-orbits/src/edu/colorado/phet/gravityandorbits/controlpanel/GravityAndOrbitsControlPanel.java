/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.controlpanel;

import java.awt.*;
import java.awt.geom.Point2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.IsSelectedProperty;
import edu.colorado.phet.common.phetcommon.view.*;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
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

    public GravityAndOrbitsControlPanel( final GravityAndOrbitsModule module, GravityAndOrbitsModel model, GravityAndOrbitsMode mode ) {
        super();

        final LogoPanel panel = new LogoPanel();
        panel.setBackground( BACKGROUND );
        addControl( panel );

        for ( GravityAndOrbitsMode m : module.getModes() ) {
            addControlFullWidth( new GORadioButton( m.getName(), new IsSelectedProperty<GravityAndOrbitsMode>( m, module.getModeProperty() ) ) );
        }

        addControlFullWidth( new VerticalLayoutPanel() {{
            setBackground( BACKGROUND );
            setOpaque( false );
            setBorder( new PhetTitledBorder( new PhetLineBorder( Color.white ), "Show" ) {{
                setTitleColor( Color.white );
                setTitleFont( CONTROL_FONT );
            }} );
            setFillNone();
            setAnchor( GridBagConstraints.WEST );

            add( new JPanel( new GridLayout( 2, 2 ) ) {
                {
                    setBackground( BACKGROUND );
                    setOpaque( false );

                    add( new GOCheckBox( "Gravity Force", module.getShowGravityForceProperty() ) );
                    addArrow( PhetColorScheme.GRAVITATIONAL_FORCE );
                    add( new GOCheckBox( "Velocity", module.getShowVelocityProperty() ) );
                    addArrow( PhetColorScheme.VELOCITY );
                    setMaximumSize( getPreferredSize() );
                }

                private void addArrow( final Color color ) {
                    add( new JLabel( new ImageIcon( new ArrowNode( new Point2D.Double(), new Point2D.Double( 65, 0 ), 15, 15, 5, 2, true ) {{
                        setPaint( color );
                        setStrokePaint( Color.darkGray );
                    }}.toImage() ) ) );
                }
            } );
            add( new GOCheckBox( "Mass", module.getShowMassProperty() ) );
            add( new GOCheckBox( "Path", module.getShowPathProperty() ) );
        }} );
        addControlFullWidth( new BodyMassControl( model.getSun(), model.getSun().getMassProperty().getDefaultValue() / 2, model.getSun().getMassProperty().getDefaultValue() * 2, "Large", "Very Large", GravityAndOrbitsCanvas.SUN_SIZER ) );
        addControlFullWidth( new BodyMassControl( model.getPlanet(), model.getPlanet().getMassProperty().getDefaultValue() / 2, model.getPlanet().getMassProperty().getDefaultValue() * 2, "Very Small", "Small", GravityAndOrbitsCanvas.PLANET_SIZER ) );
        addControlFullWidth( new GOCheckBox( "Moon", mode.getMoonProperty() ) );

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
