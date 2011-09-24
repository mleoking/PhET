// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.jmephet.JMEPropertyCheckBox;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Wraps a property-based check box with our Molecule Shapes defaults, and adds
 * convenience methods
 */
public class PropertyCheckBoxNode extends PSwing {
    public PropertyCheckBoxNode( String text, Property<Boolean> property ) {
        this( text, property, MoleculeShapesColor.CONTROL_PANEL_TEXT );
    }

    public PropertyCheckBoxNode( String text, Property<Boolean> property, MoleculeShapesColor msColor ) {
        super( new MoleculeShapesPropertyCheckBox( text, property, msColor ) );
    }

    public void setEnabled( boolean enabled ) {
        getCheckBox().setEnabled( enabled );

        // make it somewhat transparent when disabled
        setTransparency( enabled ? 1 : 0.6f );

        // sanity check to make sure we repaint TODO: can we remove this?
        repaint();
    }

    public MoleculeShapesPropertyCheckBox getCheckBox() {
        return (MoleculeShapesPropertyCheckBox) getComponent();
    }

    /**
     * Check box with extra styling
     */
    public static class MoleculeShapesPropertyCheckBox extends JMEPropertyCheckBox {
        public MoleculeShapesPropertyCheckBox( String text, final SettableProperty<Boolean> property, MoleculeShapesColor msColor ) {
            super( text, property );

            // default styling
            setFont( MoleculeShapesConstants.CHECKBOX_FONT );
            msColor.addColorObserver( new VoidFunction1<Color>() {
                public void apply( Color color ) {
                    setForeground( color );
                    repaint();
                }
            } );
            setOpaque( false );
        }
    }
}
