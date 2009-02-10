
package edu.colorado.phet.acidbasesolutions.control;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import edu.colorado.phet.acidbasesolutions.model.IMolecule;
import edu.colorado.phet.acidbasesolutions.model.PureWater;
import edu.colorado.phet.acidbasesolutions.model.acids.CustomWeakAcid;
import edu.colorado.phet.acidbasesolutions.model.acids.GenericStrongAcid;
import edu.colorado.phet.acidbasesolutions.model.acids.HydrochloricAcid;
import edu.colorado.phet.acidbasesolutions.model.acids.HypochlorousAcid;
import edu.colorado.phet.acidbasesolutions.model.bases.CustomWeakBase;
import edu.colorado.phet.acidbasesolutions.model.bases.GenericStrongBase;
import edu.colorado.phet.acidbasesolutions.model.bases.Methylamine;
import edu.colorado.phet.acidbasesolutions.model.bases.SodiumHydroxide;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Combo box for selecting the molecule that is added to the solution.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MoleculeComboBox extends JComboBox {

    public MoleculeComboBox() {
        super();
        setMaximumRowCount( 10 );
        
        // renderer
        setRenderer( new MoleculeComboBoxRenderer() );

        // acids
        addItem( new CustomWeakAcid() );
        addItem( new GenericStrongAcid() );
        addItem( new HydrochloricAcid() );
        addItem( new HypochlorousAcid() );
        
        // bases
        addItem( new CustomWeakBase() );
        addItem( new GenericStrongBase() );
        addItem( new Methylamine() );
        addItem( new SodiumHydroxide() );
        
        // pure water
        addItem( new PureWater() );
    }
    
    /*
     * Custom renderer, displays a molecule in the format "<html>name (symbol)</html>".
     */
    private static class MoleculeComboBoxRenderer extends BasicComboBoxRenderer {

        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
            if ( value instanceof IMolecule ) {
                setText( getMoleculeText( (IMolecule) value ) );
            }
            return this;
        }
        
        private String getMoleculeText( IMolecule molecule ) {
            return HTMLUtils.toHTMLString( molecule.getName() + " (" + molecule.getSymbol() + ")" );
        }
    }
    
    /**
     * Gets the selected molecule.
     * Returns null if the selected item is not a molecule.
     * 
     * @return
     */
    public IMolecule getSelectedMolecule() {
        IMolecule molecule = null;
        Object object = getSelectedItem();
        if ( object instanceof IMolecule ) {
            molecule = (IMolecule) object;
        }
        return molecule;
    }

    // test
    public static void main( String[] args ) {
        
        final MoleculeComboBox comboBox = new MoleculeComboBox();
        comboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                if ( e.getStateChange() == ItemEvent.SELECTED ) {
                    IMolecule molecule = comboBox.getSelectedMolecule();
                    System.out.println( "selected " + molecule.getName() );
                }
            }
        });
        
        JFrame frame = new JFrame();
        frame.getContentPane().add( comboBox );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }

}
