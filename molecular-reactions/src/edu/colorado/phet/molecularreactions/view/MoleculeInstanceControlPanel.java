/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.common.model.ModelElement;

import javax.swing.*;
import java.awt.*;

/**
 * MoleculeInstanceControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeInstanceControlPanel extends JPanel {

    public MoleculeInstanceControlPanel( MRModel model ) {
        super( new GridBagLayout() );

        JLabel aLabel = new JLabel( "A");
        JLabel bLabel = new JLabel( "B");
        JLabel cLabel = new JLabel( "C");

        JTextField aTF = new MoleculeCounter( 8, MoleculeA.class, model );
        JTextField bTF = new MoleculeCounter( 8, MoleculeB.class, model );
        JTextField cTF = new MoleculeCounter( 8, MoleculeC.class, model );

        GridBagConstraints gbc = new GridBagConstraints( 0,
                                                         GridBagConstraints.RELATIVE,
                                                         1,1,1,1,
                                                         GridBagConstraints.EAST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0,0,0,0),0,0 );
        add( aLabel, gbc );
        add( bLabel, gbc );
        add( cLabel, gbc );

        gbc.gridx = 1;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.WEST;
        add( aTF, gbc );
        add( bTF, gbc );
        add( cTF, gbc );
    }

    private class MoleculeCounter extends JTextField implements PublishingModel.ModelListener {
        private Class moleculeClass;

        public MoleculeCounter( int columns, Class moleculeClass, MRModel model  ) {
            super( columns );
            this.moleculeClass = moleculeClass;
            model.addListener( this );
            setText("0");
        }

        public void modelElementAdded( ModelElement element ) {
            if( moleculeClass.isInstance( element ) ) {
                int cnt = Integer.parseInt( getText() );
                setText( Integer.toString( ++cnt ) );
            }
        }

        public void modelElementRemoved( ModelElement element ) {
            if( moleculeClass.isInstance( element ) ) {
                int cnt = Integer.parseInt( getText() );
                setText( Integer.toString( --cnt ) );
            }
        }
    }
}
