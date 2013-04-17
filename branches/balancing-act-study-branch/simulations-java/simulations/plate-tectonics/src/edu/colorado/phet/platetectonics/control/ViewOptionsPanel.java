// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.lwjglphet.utils.GLSimSharingPropertyCheckBox;
import edu.colorado.phet.lwjglphet.utils.GLSimSharingPropertyRadioButton;
import edu.colorado.phet.platetectonics.PlateTectonicsResources;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.UserComponents;
import edu.colorado.phet.platetectonics.view.ColorMode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.platetectonics.PlateTectonicsConstants.PANEL_TITLE_FONT;

/**
 * Gives the user a list of "View" options
 */
public class ViewOptionsPanel extends PNode {
    public ViewOptionsPanel( final Property<Boolean> showLabels, Property<ColorMode> colorMode ) {
        this( showLabels, false, new Property<Boolean>( false ), new Property<Boolean>( false ), colorMode );
    }

    public ViewOptionsPanel( final Property<Boolean> showLabels,
                             final boolean containsWaterOption,
                             final Property<Boolean> showWater,
                             final Property<Boolean> showWaterEnabled,
                             final Property<ColorMode> colorMode ) {
        final PNode title = new PText( PlateTectonicsResources.Strings.OPTIONS ) {{
            setFont( PANEL_TITLE_FONT );
        }};
        addChild( title );

        final Property<Double> maxWidth = new Property<Double>( title.getFullBounds().getWidth() );
        final Property<Double> y = new Property<Double>( title.getFullBounds().getMaxY() );

        // "Density" radio button
        final PSwing densityMode = new PSwing( new GLSimSharingPropertyRadioButton<ColorMode>( UserComponents.densityView, Strings.DENSITY_VIEW, colorMode, ColorMode.DENSITY ) ) {{
            setOffset( 0, y.get() + 5 );
            y.set( getFullBounds().getMaxY() );
            maxWidth.set( Math.max( maxWidth.get(), getFullBounds().getWidth() ) );
        }};
        addChild( densityMode );

        // "Temperature" radio button
        final PSwing temperatureMode = new PSwing( new GLSimSharingPropertyRadioButton<ColorMode>( UserComponents.temperatureView, Strings.TEMPERATURE_VIEW, colorMode, ColorMode.TEMPERATURE ) ) {{
            setOffset( 0, y.get() );
            y.set( getFullBounds().getMaxY() );
            maxWidth.set( Math.max( maxWidth.get(), getFullBounds().getWidth() ) );
        }};
        addChild( temperatureMode );

        // "Both" radio button
        final PSwing combinedMode = new PSwing( new GLSimSharingPropertyRadioButton<ColorMode>( UserComponents.bothView, Strings.BOTH_VIEW, colorMode, ColorMode.COMBINED ) ) {{
            setOffset( 0, y.get() );
            y.set( getFullBounds().getMaxY() );
            maxWidth.set( Math.max( maxWidth.get(), getFullBounds().getWidth() ) );
        }};
        addChild( combinedMode );

        y.set( y.get() + 5 );

        // "Show Labels" check box
        final PSwing showLabelCheckBox = new PSwing( new GLSimSharingPropertyCheckBox( UserComponents.showLabels, Strings.SHOW_LABELS, showLabels ) ) {{
            setOffset( 0, y.get() );
            y.set( getFullBounds().getMaxY() );
            maxWidth.set( Math.max( maxWidth.get(), getFullBounds().getWidth() ) );
        }};
        addChild( showLabelCheckBox );

        // "Show Seawater" check box
        if ( containsWaterOption ) {
            addChild( new PSwing( new GLSimSharingPropertyCheckBox( UserComponents.showWater, Strings.SHOW_SEAWATER, showWater ) {{
                showWaterEnabled.addObserver( new SimpleObserver() {
                    public void update() {
                        // access property in the LWJGL thread
                        final Boolean enabled = showWaterEnabled.get();

                        // and set the swing property in the Swing EDT
                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                setEnabled( enabled );
                            }
                        } );
                    }
                } );
            }} ) {{
                setOffset( 0, y.get() );
                y.set( getFullBounds().getMaxY() );
                maxWidth.set( Math.max( maxWidth.get(), getFullBounds().getWidth() ) );
            }} );
        }

        // this prevents panel resizing when the button bounds change (like when they are pressed)
        addChild( new Spacer( 0, y.get(), 1, 1 ) );

        // horizontally center title
        title.setOffset( ( maxWidth.get() - title.getFullBounds().getWidth() ) / 2, title.getYOffset() );
    }

}
