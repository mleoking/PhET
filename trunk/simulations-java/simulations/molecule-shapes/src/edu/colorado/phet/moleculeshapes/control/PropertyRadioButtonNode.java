// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.jmephet.JMEPropertyRadioButton;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Wraps a property-based check box with our Molecule Shapes defaults, and adds
 * convenience methods
 */
public class PropertyRadioButtonNode<T> extends PSwing {

    public PropertyRadioButtonNode( IUserComponent userComponent, String text, Property<T> property, T value ) {
        super( new MoleculeShapesPropertyRadioButton<T>( userComponent, text, property, value, MoleculeShapesColor.CONTROL_PANEL_TEXT ) );
    }

    public void setEnabled( boolean enabled ) {
        getRadioButton().setEnabled( enabled );

        // make it somewhat transparent when disabled
        setTransparency( enabled ? 1 : 0.6f );
    }

    public MoleculeShapesPropertyRadioButton getRadioButton() {
        return (MoleculeShapesPropertyRadioButton) getComponent();
    }

    /**
     * Check box with extra styling
     */
    public static class MoleculeShapesPropertyRadioButton<T> extends JMEPropertyRadioButton<T> {
        public MoleculeShapesPropertyRadioButton( IUserComponent userComponent, String text, final SettableProperty<T> property, T value, MoleculeShapesColor msColor ) {
            super( userComponent, text, property, value );

            // default styling
            setFont( MoleculeShapesConstants.CHECKBOX_FONT );
            msColor.addColorObserver( new VoidFunction1<Color>() {
                public void apply( Color color ) {
                    setForeground( color );
                }
            } );
            setOpaque( false );
        }
    }
}
