// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.*;

import edu.colorado.phet.buildamolecule.control.JmolDialog;
import edu.colorado.phet.buildamolecule.model.CompleteMolecule;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 * @author Jon Olson
 */
public class MoleculeTableDialog extends JDialog {
    public MoleculeTableDialog( final Frame owner ) {
        super( owner, false );
        List<CompleteMolecule> list = CompleteMolecule.getAllCompleteMolecules();
        Collections.sort( list, new Comparator<CompleteMolecule>() {
            public int compare( CompleteMolecule o1, CompleteMolecule o2 ) {
                int sizeCompare = Double.compare( o1.getMoleculeStructure().getAtoms().size(), o2.getMoleculeStructure().getAtoms().size() );
                if ( sizeCompare == 0 ) {
                    return o1.getMoleculeStructure().getGeneralFormula().compareToIgnoreCase( o2.getMoleculeStructure().getGeneralFormula() );
                }
                return sizeCompare;
            }
        } );

        JPanel content = new JPanel( new GridBagLayout() );
        int count = 0;
        for ( final CompleteMolecule completeMolecule : list ) {
            final int finalCount = count;
            final GridBagConstraints constraints = new GridBagConstraints() {{
                gridx = RELATIVE;
                gridy = finalCount;
                anchor = WEST;
                insets = new Insets( 4, 4, 4, 4 );
            }};
            content.add( new JTextField( completeMolecule.getCommonName() ) {{
                setEditable( false );
                setBorder( null );
            }}, constraints );
            content.add( new JLabel( "<html>" + completeMolecule.getMoleculeStructure().getGeneralFormulaFragment() + "</html>" ), constraints );
            content.add( new PhetPCanvas() {{
                setBorder( null );
                setBackground( new JPanel().getBackground() );
                final PNode node = completeMolecule.createPseudo3DNode();
                final Dimension preferredSize = new Dimension( (int) node.getFullBounds().getWidth() + 1, (int) node.getFullBounds().getHeight() + 1 );
                setPreferredSize( preferredSize );
                node.centerFullBoundsOnPoint( preferredSize.getWidth() / 2, preferredSize.getHeight() / 2 );
                addScreenChild( node );
            }}, constraints );
            content.add( new JLabel( new ImageIcon( PhetCommonResources.getMaximizeButtonImage() ) ) {{
                setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
                addMouseListener( new MouseAdapter() {
                    @Override public void mousePressed( MouseEvent e ) {
                        JmolDialog.displayMolecule3D( owner, completeMolecule );
                    }
                } );
            }}, constraints );
            count++;
        }
        setContentPane( new JScrollPane( content ) );
        pack();
        setSize( new Dimension( 800, 600 ) );
        SwingUtils.centerInParent( this );
    }

    public static void main( String[] args ) {
        new MoleculeTableDialog( null ) {{
            setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        }}.setVisible( true );
    }
}
