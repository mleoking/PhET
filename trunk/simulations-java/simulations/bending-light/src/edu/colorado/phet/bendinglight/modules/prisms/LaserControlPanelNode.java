// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.bendinglight.model.BendingLightModel;
import edu.colorado.phet.bendinglight.view.BendingLightCanvas;
import edu.colorado.phet.bendinglight.view.LaserColor;
import edu.colorado.phet.bendinglight.view.ProtractorNode;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.WavelengthControl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class LaserControlPanelNode extends ControlPanelNode {
    public LaserControlPanelNode( final Property<Boolean> multipleRays, final Property<LaserColor> laserColor,
                                  final Property<Boolean> showReflections, final SettableProperty<Boolean> showNormal, final Property<Boolean> showProtractor ) {
        super( new PSwing( new VerticalLayoutPanel() {{
            class MyRadioButton<T> extends PropertyRadioButton<T> {
                MyRadioButton( String text, SettableProperty<T> property, T value ) {
                    super( text, property, value );
                    setFont( BendingLightCanvas.labelFont );
                }
            }

            //TODO: Wow, this is too messy.  It should be cleaned up
            final Property<Double> wavelengthProperty = new Property<Double>( BendingLightModel.WAVELENGTH_RED );
            wavelengthProperty.addObserver( new SimpleObserver() {
                public void update() {
                    laserColor.setValue( new LaserColor.OneColor( wavelengthProperty.getValue() ) );
                }
            } );
            add( new JRadioButton( "One Color", laserColor.getValue() != LaserColor.WHITE_LIGHT ) {{
                setFont( BendingLightCanvas.labelFont );
                final SimpleObserver updateSelected = new SimpleObserver() {
                    public void update() {
                        setSelected( laserColor.getValue() != LaserColor.WHITE_LIGHT );
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
            add( new PhetPCanvas() {{
                final WavelengthControl wavelengthControl = new WavelengthControl( 150, 27 ) {{
                    final PNode wc = this;
                    setWavelength( wavelengthProperty.getValue() * 1E9 );
                    laserColor.addObserver( new SimpleObserver() {
                        public void update() {
                            final boolean disabled = laserColor.getValue() == LaserColor.WHITE_LIGHT;
                            wc.setTransparency( disabled ? 0.3f : 1f );
                            setPickable( !disabled );
                            setChildrenPickable( !disabled );
                            if ( !disabled ) {
                                setWavelength( laserColor.getValue().getWavelength() * 1E9 );
                            }
                        }
                    } );
                    addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            wavelengthProperty.setValue( getWavelength() / 1E9 );
                        }
                    } );
                }};
                PBounds bounds = wavelengthControl.getFullBounds();
                wavelengthControl.translate( -bounds.getX() + 5, -bounds.getY() );
                setPreferredSize( new Dimension( (int) ( bounds.getWidth() + 40 ), (int) bounds.getHeight() ) );
                getLayer().addChild( wavelengthControl );
                setBorder( null );
            }} );
            add( new MyRadioButton<LaserColor>( "White Light", laserColor, LaserColor.WHITE_LIGHT ) );
            add( new JSeparator() );
            add( new MyRadioButton<Boolean>( "Single Ray", multipleRays, false ) );
            add( new MyRadioButton<Boolean>( "Multiple Rays", multipleRays, true ) );
            add( new JSeparator() );
            add( new PropertyCheckBox( "Show Reflections", showReflections ) {{setFont( BendingLightCanvas.labelFont );}} );
            add( new PropertyCheckBox( "Show Normal", showNormal ) {{setFont( BendingLightCanvas.labelFont );}} );
            add( new HorizontalLayoutPanel() {{
                add( new PropertyCheckBox( "Show Protractor", showProtractor ) {{setFont( BendingLightCanvas.labelFont );}} );
                add( new JLabel( new ImageIcon( ProtractorNode.newProtractorImage( 40 ) ) ) );
            }} );
        }} ) );
    }
}
