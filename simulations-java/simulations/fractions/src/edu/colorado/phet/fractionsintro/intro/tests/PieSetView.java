// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.tests;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.fractionsintro.intro.model.pieset.CircularPieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.view.PieSetNode;

/**
 * Main class for creating and testing out the immutable PieSetState and PieSetNode
 *
 * @author Sam Reid
 */
public class PieSetView {
    public static void main( String[] args ) {
//        PDebug.debugRegionManagement=true;
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new JFrame() {{
                    final PieSet state = new PieSet( CircularPieSet.CircularPieSet );
                    final Property<PieSet> model = new Property<PieSet>( state );

                    //Any piece that is dragging should align with the closest open cell
                    new Timer( 30, new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            model.set( model.get().stepInTime( 1.0 ) );
                        }
                    } ).start();

                    setContentPane( new PhetPCanvas() {{
                        addScreenChild( new PieSetNode( model, getPhetRootNode() ) );
                    }} );
                    pack();
                    setSize( new Dimension( 1024, 768 ) );
                    setDefaultCloseOperation( EXIT_ON_CLOSE );
                }}.setVisible( true );
            }
        } );
    }
}