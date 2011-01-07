// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.fluidpressure;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class ButtonExpander extends PNode {
    public ButtonExpander( String expandText, String collapseText, final Property<Boolean> expanded ) {
        //Show and hide expand/collapse buttons based on state
        //Expand button
        addChild( new PSwing( new JButton( expandText ) {{
            setFont( new PhetFont( 18, true ) );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    expanded.setValue( true );
                }
            } );
        }} ) {{
            expanded.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( !expanded.getValue() );
                }
            } );
        }} );
        //Collapse button
        addChild( new PSwing( new JButton( collapseText ) {{
            setFont( new PhetFont( 18, true ) );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    expanded.setValue( false );
                }
            } );
        }} ) {{
            expanded.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( expanded.getValue() );
                }
            } );
        }} );
    }
}
