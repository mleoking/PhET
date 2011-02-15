// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.prisms;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.lightreflectionandrefraction.view.LaserView;
import edu.colorado.phet.lightreflectionandrefraction.view.LightReflectionAndRefractionCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class LaserControlPanelNode extends ControlPanelNode {
    public LaserControlPanelNode( final Property<Boolean> multipleRays, final Property<LaserView> laserView ) {
        super( new PSwing( new VerticalLayoutPanel() {{
            class MyRadioButton<T> extends PropertyRadioButton<T> {
                MyRadioButton( String text, SettableProperty<T> property, T value ) {
                    super( text, property, value );
                    setFont( LightReflectionAndRefractionCanvas.labelFont );
                }
            }
            add( new MyRadioButton<Boolean>( "One Color", new Property<Boolean>( true ), true ) );
            add( new MyRadioButton<Boolean>( "White Light", new Property<Boolean>( true ), true ) );
            add( new JSeparator() );
            add( new MyRadioButton<Boolean>( "Single Ray", multipleRays, false ) );
            add( new MyRadioButton<Boolean>( "Multiple Rays", multipleRays, true ) );
            add( new JSeparator() );
            add( new MyRadioButton<LaserView>( "Ray", laserView, LaserView.RAY ) );
            add( new MyRadioButton<LaserView>( "Wave", laserView, LaserView.WAVE ) );
        }} ) );
    }
}
