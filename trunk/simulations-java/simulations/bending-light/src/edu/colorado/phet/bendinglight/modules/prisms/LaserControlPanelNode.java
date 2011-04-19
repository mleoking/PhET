// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.bendinglight.BendingLightStrings;
import edu.colorado.phet.bendinglight.view.BendingLightWavelengthControl;
import edu.colorado.phet.bendinglight.view.LaserColor;
import edu.colorado.phet.bendinglight.view.ProtractorNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.bendinglight.BendingLightStrings.*;
import static edu.colorado.phet.bendinglight.view.BendingLightCanvas.labelFont;
import static edu.colorado.phet.bendinglight.view.LaserColor.WHITE_LIGHT;

/**
 * Control panel for the laser in the "prism break" tab, such as choosing whether it is white light or one color, and the wavelength.
 *
 * @author Sam Reid
 */
public class LaserControlPanelNode extends ControlPanelNode {
    public LaserControlPanelNode( final Property<Boolean> multipleRays, final Property<LaserColor> laserColor,
                                  final Property<Boolean> showReflections, final SettableProperty<Boolean> showNormal, final Property<Boolean> showProtractor, final Property<Double> wavelengthProperty ) {
        super( new PSwing( new VerticalLayoutPanel() {{
            //Add a radio button for "one color"
            add( new JRadioButton( ONE_COLOR, laserColor.getValue() != WHITE_LIGHT ) {{
                setFont( labelFont );
                final SimpleObserver updateSelected = new SimpleObserver() {
                    public void update() {
                        setSelected( laserColor.getValue() != WHITE_LIGHT );
                    }
                };
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        laserColor.setValue( new LaserColor.OneColor( wavelengthProperty.getValue() ) );
                        updateSelected.update();//make sure radio buttons don't toggle off, in case they're not in a button group
                    }
                } );
                laserColor.addObserver( updateSelected );
            }} );

            //Add the wavelength control for choosing the wavelength in "one color" mode
            add( new PhetPCanvas() {{
                final BendingLightWavelengthControl wavelengthControl = new BendingLightWavelengthControl( wavelengthProperty, laserColor );
                setBorder( null );

                //Layout
                wavelengthControl.setOffset( 17, 0 );//move it to the right so the text label doesn't go off the control panel
                PBounds bounds = wavelengthControl.getFullBounds();
                setPreferredSize( new Dimension( (int) ( bounds.getWidth() + 40 ), (int) bounds.getHeight() ) );//make it a bit bigger than its default

                //Add the wavelength control
                getLayer().addChild( wavelengthControl );
            }} );

            //Add a radio button for "white light"
            add( new MyRadioButton<LaserColor>( BendingLightStrings.WHITE_LIGHT, laserColor, WHITE_LIGHT ) );
            add( new JSeparator() );

            //Choose between single and multiple rays
            add( new MyRadioButton<Boolean>( SINGLE_RAY, multipleRays, false ) );
            add( new MyRadioButton<Boolean>( MULTIPLE_RAYS, multipleRays, true ) );
            add( new JSeparator() );

            //Checkboxes to toggle reflections, normal, protractor
            add( new PropertyCheckBox( BendingLightStrings.SHOW_REFLECTIONS, showReflections ) {{setFont( labelFont );}} );
            add( new PropertyCheckBox( BendingLightStrings.SHOW_NORMAL, showNormal ) {{setFont( labelFont );}} );
            add( new HorizontalLayoutPanel() {{
                add( new PropertyCheckBox( BendingLightStrings.SHOW_PROTRACTOR, showProtractor ) {{setFont( labelFont );}} );
                add( new JLabel( new ImageIcon( ProtractorNode.newProtractorImage( 40 ) ) ) );
            }} );
        }} ) );
    }

    //Class for creating radio buttons with the right font
    private static class MyRadioButton<T> extends PropertyRadioButton<T> {
        MyRadioButton( String text, SettableProperty<T> property, T value ) {
            super( text, property, value );
            setFont( labelFont );
        }
    }
}