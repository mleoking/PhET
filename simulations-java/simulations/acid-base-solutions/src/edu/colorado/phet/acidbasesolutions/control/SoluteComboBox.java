
package edu.colorado.phet.acidbasesolutions.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.acidbasesolutions.model.NoSolute;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.Acid.*;
import edu.colorado.phet.acidbasesolutions.model.Base.Ammonia;
import edu.colorado.phet.acidbasesolutions.model.Base.CustomBase;
import edu.colorado.phet.acidbasesolutions.model.Base.Pyridine;
import edu.colorado.phet.acidbasesolutions.model.Base.SodiumHydroxide;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolox.pswing.PComboBox;

/**
 * Combo box for selecting a solute.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SoluteComboBox extends PComboBox {
    
    private static final Font FONT = new PhetFont( 16 );
    
    public SoluteComboBox() {
        super();
        setBorder( BorderFactory.createLineBorder( Color.BLACK, 1 ) ); // hack for PComboBox
        setBackground( Color.WHITE ); // hack for PComboBox
        setFont( FONT );
        
        addItem( new NoSolute() );
        addItem( new SoluteSeparator() ); //---------
        addItem( new AceticAcid() );
        addItem( new Ammonia() );
        addItem( new ChlorousAcid() );
        addItem( new HydrochloricAcid() );
        addItem( new HydrofluoricAcid() );
        addItem( new HypochlorusAcid() );
        addItem( new PerchloricAcid() );
        addItem( new Pyridine() );
        addItem( new SodiumHydroxide() );
        addItem( new SoluteSeparator() ); //---------
        addItem( new CustomAcid() );
        addItem( new CustomBase() );
        
        // calculate max item dimensions
        int maxWidth = 0;
        int maxHeight = 0;
        for ( int i = 0; i < getItemCount(); i++ ) {
            Object item = getItemAt( i );
            if ( item instanceof Solute ) {
                SoluteLabel label = new SoluteLabel( (Solute) item, FONT );
                maxWidth = Math.max( maxWidth, label.getPreferredSize().width );
                maxHeight = Math.max( maxHeight, label.getPreferredSize().height );
            }
        }
        
//        DefaultComboBoxModel model = new DefaultComboBoxModel() {
//
//            public void setSelectedItem( Object o ) {
//                if ( o instanceof JSeparator )
//                    return;
//                super.setSelectedItem( o );
//            }
//        };
//        setModel( model );
        
        // make all items visible (no vertical scroll bar)
        setMaximumRowCount( getItemCount() );
        
        setPreferredSize( new Dimension( maxWidth + 60, maxHeight + 5 ) );
        setRenderer( new CustomRenderer( getPreferredSize().height ) );
    }
    
    /**
     * Sets the selection based on the Solute's name.
     * @param solute
     */
    public void setSelectedSoluteName( String soluteName ) {
        
        Object item = null;
        for ( int i = 0; i < getItemCount(); i++ ) {
            Object o = getItemAt( i );
            if ( o instanceof Solute ) {
                if ( ((Solute)o).getName().equals( soluteName ) ) {
                    item = o;
                    break;
                }
            }
        }
        
        if ( item == null ) {
            throw new IllegalArgumentException( "solute is not in the list: " + soluteName );
        }
        else {
            setSelectedItem( item );
        }
    }
    
    /**
     * Gets the name of the selected Solute.
     * @return
     */
    public String getSelectedSoluteName() {
        String name = null;
        Object o = getSelectedItem();
        if ( o instanceof Solute ) {
            name = ((Solute) o).getName();
        }
        return name;
    }
    
    
    private static class CustomRenderer implements ListCellRenderer {

        private final int height;
        
        public CustomRenderer( int minHeight ) {
            super();
            this.height = minHeight;
        }

        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            Component component = null;
            if ( value instanceof JSeparator ) {
                component = (JSeparator) value; 
            }
            else if ( value instanceof Solute ){
                SoluteLabel label = new SoluteLabel( (Solute) value, list.getFont() );
                if ( isSelected ) {
                    label.setBackground( list.getSelectionBackground() );
                    label.setForeground( list.getSelectionForeground() );
                }
                else {
                    label.setBackground( list.getBackground() );
                    label.setForeground( list.getForeground() );
                }
                label.setPreferredSize( new Dimension( label.getPreferredSize().width, Math.max( height, label.getPreferredSize().height ) ) );
                component = label;
            }
            return component;
        }
    }
    
    private static class SoluteLabel extends JLabel {
        
        public SoluteLabel( Solute solute, Font font ) {
            super( soluteToString( solute ) );
            setFont( font );
            setOpaque( true );
            setHorizontalAlignment( SwingConstants.LEFT );
            setVerticalAlignment( SwingConstants.TOP );
            setBorder( new EmptyBorder( 2, 4, 2, 6 ) ); // top, left, bottom, right
        }
        
        private static final String soluteToString( Solute solute ) {
            String name = solute.getName();
            String symbol = solute.getSymbol();
            String s = name;
            if ( symbol != null && symbol.length() > 0 ) {
                s += " (" + symbol + ")";
            }
            return HTMLUtils.toHTMLString( s );
        }
    }
    
    private static class SoluteSeparator extends JSeparator {
        public SoluteSeparator() {
            super();
            setForeground( Color.BLACK );
        }
    }
}
