/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function0;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;

public class EntryPanel extends PNode {
    private final static Font FONT = new PhetFont( 30 );
    private final HTMLNode label;
    private final ValueNode valueNode;
    private final Property<Boolean> editable;

    public EntryPanel( String labelText, final Property<Integer> property ) {
        label = new HTMLNode("Dummy Text") {{
            setFont( FONT );
        }};
        double spinnerHeight = label.getFullBoundsReference().getHeight() * 0.9;
        label.setHTML( labelText );
        addChild( label );
        final JSpinner spinner = new JSpinner( new SpinnerNumberModel( 0, 0, 30, 1 ) ){{
                addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        property.setValue( (Integer) getValue() );
                    }
                } );
              }};
        property.addObserver( new SimpleObserver() {
            public void update() {
                spinner.setValue( property.getValue() );
            }
        } );

        editable = new Property<Boolean>( true );
        valueNode = new ValueNode( property, 0, 30, 1, editable, ValueNode.DEFAULT_NUMBER_FORMAT, new Function0.Constant<Color>( Color.black ) );
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
}