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
import edu.colorado.phet.molecularreactions.model.reactions.Profiles;
import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.*;

/**
 * Legend
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Legend extends JPanel {

    public Legend() {
        super( new GridBagLayout() );

        setBorder( ControlBorderFactory.createPrimaryBorder( "Legend ") );

        if( !true ) {
            setLayout( new BorderLayout( ));
            add( new JLabel( "ASEDF"));
            return;
        }
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.EAST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 3, 3, 3, 3 ), 0, 0 );
        SimpleMolecule mA = new MoleculeA();
        SimpleMolecule mB = new MoleculeB();
        SimpleMolecule mC = new MoleculeC();

        PNode pA = new SpatialSimpleMoleculeGraphic( mA, Profiles.DEFAULT );
        PNode pB = new SpatialSimpleMoleculeGraphic( mB, Profiles.DEFAULT );
        PNode pC = new SpatialSimpleMoleculeGraphic( mC, Profiles.DEFAULT );

        add( createMoleculeIconComponent( pA ), gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add( new JLabel( "A"), gbc );

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        add( createMoleculeIconComponent( pB ), gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add( new JLabel( "B"), gbc );

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        add( createMoleculeIconComponent( pC ), gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add( new JLabel( "C"), gbc );


    }

    private JPanel createMoleculeIconComponent( PNode pA ) {
        Color backgroundColor = UIManager.getColor( "Panel.background" );
        Dimension renderingSize = new Dimension( 25, 25 );
        PCanvas canvasA = new PCanvas();
        canvasA.setBackground( backgroundColor );
        canvasA.setBounds( 0,0,renderingSize.width, renderingSize.height );
        pA.setOffset( canvasA.getWidth()/2, canvasA.getHeight()/2);
        canvasA.getLayer().addChild( pA );
        JPanel jp = new JPanel( null );
        jp.setPreferredSize( renderingSize );
        jp.add( canvasA );
        return jp;
    }
}
