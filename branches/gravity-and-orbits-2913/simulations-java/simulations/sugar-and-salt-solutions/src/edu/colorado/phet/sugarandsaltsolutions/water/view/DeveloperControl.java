// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.sugarandsaltsolutions.common.view.PropertySlider;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class DeveloperControl extends ControlPanelNode {

    public DeveloperControl( final WaterModel model ) {
        super( new VBox(
                //Button to add a single sodium ion
                new HTMLImageButtonNode( "Add Sodium Ion" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            model.addSodiumIon( model.getRandomX(), model.beakerHeight );
                        }
                    } );
                }},
                //Button to add a chlorine icon
                new HTMLImageButtonNode( "Add Chlorine Ion" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            model.addChlorineIon( model.getRandomX(), model.beakerHeight );
                        }
                    } );
                }},
                //button to add a water
                new HTMLImageButtonNode( "Add Water" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            model.addWater( model.getRandomX(), model.getRandomY(), 0 );
                        }
                    } );
                }},

                //Developer controls for physics settings,
                new PSwing( new JPanel() {{
                    add( new JLabel( "k" ) );
                    add( new PropertySlider( 0, 1000, model.k ) );
                }} ),
                new PSwing( new JPanel() {{
                    add( new JLabel( "pow" ) );
                    add( new PropertySlider( 0, 10, model.pow ) );
                }} ),
                new PSwing( new JPanel() {{
                    add( new JLabel( "rand" ) );
                    add( new PropertySlider( 0, 100, model.randomness ) );
                }} )
        ) {{}} );
    }
}
