// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This Piccolo control allows the user to toggle between viewing the electron
 * structure of the atom by showing the orbitals or by showing an electron
 * cloud.
 *
 * @author Sam Reid
 */
public class OrbitalViewControl extends PNode {
    public OrbitalViewControl( final OrbitalViewProperty orbitalViewProperty ) {
        final PText textTitle = new PText( BuildAnAtomStrings.ELECTRON_MODEL ) {
            {
                setFont( BuildAnAtomConstants.WINDOW_TITLE_FONT );
            }
        };
        addChild( textTitle );

        // Create the panel and add the buttons.
        JPanel panel = new VerticalLayoutPanel() {
            {
                setOpaque( false );
                final ButtonGroup buttonGroup = new ButtonGroup();//Fixes the problem that clicking on a radio button twice causes it to become de-selected
                add( new JRadioButton( BuildAnAtomStrings.ELECTRON_MODEL_ORBITS, orbitalViewProperty.get() == OrbitalView.PARTICLES ) {
                    {
                        setOpaque( false );
                        buttonGroup.add( this );
                        setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );
                        setFont( BuildAnAtomConstants.ITEM_FONT );
                        addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                orbitalViewProperty.set( OrbitalView.PARTICLES );
                            }
                        } );
                        orbitalViewProperty.addObserver( new SimpleObserver() {
                            public void update() {
                                setSelected( orbitalViewProperty.get() == OrbitalView.PARTICLES );
                            }
                        } );
                    }
                } );
                add( new JRadioButton( BuildAnAtomStrings.ELECTRON_MODEL_CLOUD, orbitalViewProperty.get() == OrbitalView.RESIZING_CLOUD) {
                    {
                        setOpaque( false );
                        buttonGroup.add( this );
                        setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );
                        setFont( BuildAnAtomConstants.ITEM_FONT );
                        addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                orbitalViewProperty.set( OrbitalView.RESIZING_CLOUD );
                            }
                        } );
                        orbitalViewProperty.addObserver( new SimpleObserver() {
                            public void update() {
                                setSelected( orbitalViewProperty.get() == OrbitalView.RESIZING_CLOUD );
                            }
                        } );
                    }
                } );
            }
        };

        // Wrap the panel in a PSwing and add it as a child.
        addChild( new PSwing( panel ) {
            {
                setOffset( textTitle.getFullBounds().getX(), textTitle.getFullBounds().getMaxY() );
            }
        } );
    }
}
