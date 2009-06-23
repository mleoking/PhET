
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
        
        /*
         * Ignore items that are not solutes.
         * This prevents selection of separators.
         */
        DefaultComboBoxModel model = new DefaultComboBoxModel() {
            public void setSelectedItem( Object o ) {
                if ( o instanceof SoluteItem ) {
                    super.setSelectedItem( o );
                }
            }
        };
        setModel( model );

        /*
         * NOTE: If you add a new solute choice here, also add one to SoluteFactory.
         */
        addSoluteItem( new NoSolute() );
        addSeparatorItem(); //---------
        addSoluteItem( new AceticAcid() );
        addSoluteItem( new Ammonia() );
        addSoluteItem( new ChlorousAcid() );
        addSoluteItem( new HydrochloricAcid() );
        addSoluteItem( new HydrofluoricAcid() );
        addSoluteItem( new HypochlorusAcid() );
        addSoluteItem( new PerchloricAcid() );
        addSoluteItem( new Pyridine() );
        addSoluteItem( new SodiumHydroxide() );
        addSeparatorItem(); //---------
        addSoluteItem( new CustomAcid() );
        addSoluteItem( new CustomBase() );

        // calculate max item dimensions
        int maxWidth = 0;
        int maxHeight = 0;
        for ( int i = 0; i < getItemCount(); i++ ) {
            Object item = getItemAt( i );
            if ( item instanceof SoluteItem ) {
                SoluteComponent label = new SoluteComponent( item.toString(), FONT );
                maxWidth = Math.max( maxWidth, label.getPreferredSize().width );
                maxHeight = Math.max( maxHeight, label.getPreferredSize().height );
            }
        }
        
        // make all items visible (no vertical scroll bar)
        setMaximumRowCount( getItemCount() );
        
        setPreferredSize( new Dimension( maxWidth + 60, maxHeight + 5 ) );
        setRenderer( new CustomRenderer( getPreferredSize().height ) );
    }
    
    private void addSoluteItem( Solute solute ) {
        addItem( new SoluteItem( solute ) );
    }
    
    private void addSeparatorItem() {
        addItem( new SeparatorItem() );
    }
    
    /**
     * Sets the selection based on the Solute's name.
     * @param solute
     */
    public void setSelectedSoluteName( String soluteName ) {
        
        Object item = null;
        for ( int i = 0; i < getItemCount(); i++ ) {
            Object o = getItemAt( i );
            if ( o instanceof SoluteItem ) {
                if ( ( (SoluteItem) o ).getName().equals( soluteName ) ) {
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
        if ( o instanceof SoluteItem ) {
            name = ( (SoluteItem) o ).getName();
        }
        return name;
    }
    
    //----------------------------------------------------------------------------
    // Renderer
    //----------------------------------------------------------------------------
    
    /*
     * Renderer that supports solutes and separators.
     */
    private static class CustomRenderer implements ListCellRenderer {

        private final int height;
        
        public CustomRenderer( int minHeight ) {
            super();
            this.height = minHeight;
        }

        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            Component component = null;
            if ( value instanceof SeparatorItem ) {
                component = new SeparatorComponent(); 
            }
            else if ( value instanceof SoluteItem ){
                SoluteComponent label = new SoluteComponent( value.toString(), list.getFont() );
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
            else {
                throw new UnsupportedOperationException( "unsupported value type: " + value.getClass().getName() );
            }
            return component;
        }
    }

    //----------------------------------------------------------------------------
    // Model classes
    //----------------------------------------------------------------------------
    
    /*
     * Combo box item that determines how a solute's name is represented.
     */
    private static class SoluteItem {

        private final Solute solute;

        public SoluteItem( Solute solute ) {
            this.solute = solute;
        }
        
        public String getName() {
            return solute.getName();
        }

        public String toString() {
            String name = solute.getName();
            String symbol = solute.getSymbol();
            String s = name;
            if ( symbol != null && symbol.length() > 0 ) {
                s += " (" + symbol + ")";
            }
            return HTMLUtils.toHTMLString( s );
        }
    }
    
    /*
     * Combo box item that represents a separator.
     * This is a marker class, with no functionality.
     */
    private static class SeparatorItem {}
    
    //----------------------------------------------------------------------------
    // View data
    //----------------------------------------------------------------------------
    
    /*
     * Displays a solute's name as a JLabel.
     */
    private static class SoluteComponent extends JLabel {
        
        public SoluteComponent( String text, Font font ) {
            super( text );
            setFont( font );
            setOpaque( true );
            setHorizontalAlignment( SwingConstants.LEFT );
            setVerticalAlignment( SwingConstants.TOP );
            setBorder( new EmptyBorder( 2, 4, 2, 6 ) ); // top, left, bottom, right
        }
    }
    
    /*
     * Separator between solutes.
     */
    private static class SeparatorComponent extends JSeparator {
        public SeparatorComponent() {
            super();
            setForeground( Color.BLACK );
        }
    }
}
