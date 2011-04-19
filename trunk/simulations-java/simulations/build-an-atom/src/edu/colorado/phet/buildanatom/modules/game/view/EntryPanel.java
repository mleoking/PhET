// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;

/**
 * A PNode that consists of a label and a Swing spinner (JSpinner), generally
 * used for allowing the user to enter data.
 *
 * @author John Blanco
 */
public class EntryPanel extends PNode {

    private static final int DEFAULT_MIN = 0;
    private static final int DEFAULT_MAX = 30;
    private final static Font FONT = new PhetFont( 30 );
    private final HTMLNode label;
    private final ValueNode valueNode;
    private final Property<Boolean> editable;

    public EntryPanel( String labelText, final Property<Integer> property ) {
        this( labelText, property, DEFAULT_MIN, DEFAULT_MAX );
    }

    public EntryPanel( String labelText, final Property<Integer> property, int min, int max ) {
        this( labelText, property, min, max, ValueNode.DEFAULT_NUMBER_FORMAT );
    }

    public EntryPanel( String labelText, final Property<Integer> property, int min, int max, NumberFormat numberFormat ) {

        label = new HTMLNode("Dummy Text") {{
            setFont( FONT );
        }};
        double spinnerHeight = label.getFullBoundsReference().getHeight() * 0.9;
        label.setHTML( labelText );
        addChild( label );

        editable = new Property<Boolean>( true );
        valueNode = new ValueNode( property, min, max, 1, editable, numberFormat, ValueNode.DEFAULT_COLOR_FUNCTION );
        valueNode.setScale( spinnerHeight / valueNode.getFullBoundsReference().height * 0.9 );
        valueNode.setOffset( label.getFullBoundsReference().width + 15,
                label.getFullBounds().getHeight() - valueNode.getFullBounds().getHeight() );
        addChild( valueNode );
    }

    public double getLabelWidth() {
        return label.getFullBounds().getWidth();
    }

    /**
     * Set the X position of the spinner.  This exists so that the spinners
     * can be lined up when several of these entry panels are shown on the
     * same screen and their labels differ in length.
     *
     * @param x
     */
    public void setSpinnerX( double x ) {
        valueNode.setOffset( x, valueNode.getOffset().getY() );
    }

    public void setEditable( boolean b ) {
        editable.setValue( b );
    }

    /**
     * Set a color function for the portion of this panel that displays the
     * values (often in a spinner, but also sometimes as simple text).  This
     * function will control how the value is colorized.
     *
     * @param colorFunction
     */
    public void setValueColorFunction( Function0<Color> colorFunction ){
        // Just pass this through to the value node.
        valueNode.setColorFunction( colorFunction );
    }
}