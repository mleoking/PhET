//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.view.view3d;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.MoleculeList;
import edu.colorado.phet.common.jmolphet.JmolPanel;

//REVIEW move to tests package

/**
 * @author Sam Reid
 */
public class TestJMolView {
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Hello" ) {{
            setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            setSize( 410, 410 );
        }};
        Container contentPane = frame.getContentPane();
        JmolPanel jmolPanel = new JmolPanel( MoleculeList.H2O, BuildAMoleculeStrings.JMOL_3D_LOADING );

        contentPane.add( jmolPanel );

        frame.setVisible( true );
    }
}