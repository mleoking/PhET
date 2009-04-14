
package edu.colorado.phet.acidbasesolutions.control;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import edu.colorado.phet.acidbasesolutions.model.ISolute;
import edu.colorado.phet.acidbasesolutions.model.PureWater;
import edu.colorado.phet.acidbasesolutions.model.acids.CustomStrongAcid;
import edu.colorado.phet.acidbasesolutions.model.acids.CustomWeakAcid;
import edu.colorado.phet.acidbasesolutions.model.acids.HydrochloricAcid;
import edu.colorado.phet.acidbasesolutions.model.acids.HypochlorousAcid;
import edu.colorado.phet.acidbasesolutions.model.bases.CustomStrongBase;
import edu.colorado.phet.acidbasesolutions.model.bases.CustomWeakBase;
import edu.colorado.phet.acidbasesolutions.model.bases.Methylamine;
import edu.colorado.phet.acidbasesolutions.model.bases.SodiumHydroxide;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Combo box for selecting the solute.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SoluteComboBox extends JComboBox {
    
    private static final String SEPARATOR = "separator";

    public SoluteComboBox() {
        super();
        setMaximumRowCount( 10 );
        
        // renderer
        setRenderer( new SoluteComboBoxRenderer() );

        // pure water
        addItem( new PureWater() );
        addItem( SEPARATOR );
        
        // custom
        addItem( new CustomWeakAcid() );
        addItem( new CustomStrongAcid() );
        addItem( new CustomWeakBase() );
        addItem( new CustomStrongBase() );
        addItem( SEPARATOR );
        
        // specific acids
        addItem( new HydrochloricAcid() );
        addItem( new HypochlorousAcid() );
        addItem( SEPARATOR );
        
        // specific bases
        addItem( new Methylamine() );
        addItem( new SodiumHydroxide() );
        addItem( SEPARATOR );
        
        // default state
        setSelectedIndex( 0 );
    }
    
    /*
     * Custom renderer, displays a solute in the format "<html>name (symbol)</html>".
     */
    private static class SoluteComboBoxRenderer extends BasicComboBoxRenderer {
        
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
            if ( SEPARATOR.equals( value ) ) {
                return new JSeparator();
            }
            if ( value instanceof ISolute ) {
                setText( getSoluteText( (ISolute) value ) );
            }
            else if ( value instanceof PureWater ) {
                setText( getPureWaterText( (PureWater) value ) );
            }
            return this;
        }
        
        private String getSoluteText( ISolute solute ) {
            return HTMLUtils.toHTMLString( solute.getName() + " (" + solute.getSymbol() + ")" );
        }
        
        private String getPureWaterText( PureWater solvent ) {
            return HTMLUtils.toHTMLString( solvent.getName() + " (" + solvent.getSymbol() + ")" );
        }
    }
    
    /**
     * Gets the selected solute.
     * Returns null if the selected item is not a solute.
     * 
     * @return
     */
    public ISolute getSelectedSolute() {
        ISolute solute = null;
        Object object = getSelectedItem();
        if ( object instanceof ISolute ) {
            solute = (ISolute) object;
        }
        return solute;
    }

    // test
    public static void main( String[] args ) {
        
        final SoluteComboBox comboBox = new SoluteComboBox();
        comboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                if ( e.getStateChange() == ItemEvent.SELECTED ) {
                    ISolute solute = comboBox.getSelectedSolute();
                    System.out.println( "selected " + solute.getName() );
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
