// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.MessageFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PComboBox;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Control for selecting a real molecule.
 * This version is based on PComboBox, and will be used in production if #2982 isn't resolved.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MoleculeControlNode2 extends PhetPNode {

    private static final PhetFont FONT = new PhetFont( 18 );

    private final PComboBox comboBox; // keep a reference so we can add observers to ComboBoxNode.selectedItem

    public MoleculeControlNode2( final ArrayList<Molecule3D> molecules, final Property<Molecule3D> currentMolecule, PhetPCanvas canvas ) {

        // label
        PText labelNode = new PText( MessageFormat.format( MPStrings.PATTERN_0LABEL, MPStrings.MOLECULE ) ) {{
            setFont( FONT );
        }};
        addChild( labelNode );

        // combo box
        comboBox = new Molecule3DComboxBox( molecules, currentMolecule );
        PSwing comboBoxNode = new PSwing( comboBox );
        addChild( comboBoxNode );
        comboBox.setEnvironment( comboBoxNode, canvas );

        // layout
        final double heightDiff = comboBoxNode.getFullBoundsReference().getHeight() - labelNode.getFullBoundsReference().getHeight();
        labelNode.setOffset( 0, Math.max( 0, heightDiff / 2 ) );
        comboBoxNode.setOffset( labelNode.getFullBoundsReference().getMaxX() + 5, Math.min( 0, heightDiff / 2 ) );

        // when the combo box selection changes, update the current molecule
        comboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                if ( e.getItem() instanceof Molecule3D ) {
                    currentMolecule.set( (Molecule3D) comboBox.getSelectedItem() );
                }
                else {
                    throw new RuntimeException( "unsupported object type in combo box: " + e.getItem().getClass().getName() );
                }
            }
        } );

        // when the current molecule changes, update the combo box selection
        currentMolecule.addObserver( new VoidFunction1<Molecule3D>() {
            public void apply( Molecule3D molecule ) {
                comboBox.setSelectedItem( molecule );
            }
        } );
    }

    // combo box that shows molecule choices
    private static class Molecule3DComboxBox extends PComboBox {

        public Molecule3DComboxBox( final ArrayList<Molecule3D> molecules, final Property<Molecule3D> currentMolecule ) {
            super( molecules.toArray() );
            setMaximumRowCount( Math.max( molecules.size(), 25 ) );
            setFont( FONT );
            setSelectedItem( currentMolecule.get() );
            setRenderer( new Molecule3DRenderer() );
            setBorder( BorderFactory.createLineBorder( Color.BLACK, 1 ) );
            setBackground( new Color( 230, 230, 230 ) );
        }
    }

    // custom renderer for Molecule3D objects, handles text formatting so we don't have to pervert Molecule3D.toString
    private static class Molecule3DRenderer extends JLabel implements ListCellRenderer {
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            setFont( FONT );
            setOpaque( true );

            // text formatting
            if ( value instanceof Molecule3D ) {
                Molecule3D molecule = (Molecule3D) value;
                String label = MessageFormat.format( "<html>{0} ({1})</html>", molecule.getSymbol(), molecule.getName() );
                setText( label );
            }
            else {
                setText( value.getClass().getName() );
            }

            // colors
            if ( isSelected ) {
                setBackground( list.getSelectionBackground() );
                setForeground( list.getSelectionForeground() );
            }
            else {
                setBackground( list.getBackground() );
                setForeground( list.getForeground() );
            }

            return this;
        }
    }
}
