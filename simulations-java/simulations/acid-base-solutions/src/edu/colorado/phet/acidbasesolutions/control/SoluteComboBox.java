
package edu.colorado.phet.acidbasesolutions.control;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import edu.colorado.phet.acidbasesolutions.model.NoSolute;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.Acid.*;
import edu.colorado.phet.acidbasesolutions.model.Base.Ammonia;
import edu.colorado.phet.acidbasesolutions.model.Base.CustomBase;
import edu.colorado.phet.acidbasesolutions.model.Base.Pyridine;
import edu.colorado.phet.acidbasesolutions.model.Base.SodiumHydroxide;


public class SoluteComboBox extends JComboBox {

    private final Object defaultChoice;
    
    public SoluteComboBox() {
        super();
        
        defaultChoice = new CustomAcid();
        
        // no solute
        addItem( new NoSolute() );
        
        // specific solutes, English alphabetical order
        addItem( new AceticAcid() );
        addItem( new Ammonia() );
        addItem( new ChlorousAcid() );
        addItem( new HydrochloricAcid() );
        addItem( new HydrofluoricAcid() );
        addItem( new HypochlorusAcid() );
        addItem( new PerchloricAcid() );
        addItem( new Pyridine() );
        addItem( new SodiumHydroxide() );
        
        // custom solutes
        addItem( defaultChoice );
        addItem( new CustomBase() );
        
        // default selection
        setSelectedItem( defaultChoice );
        
        // make all items visible (no vertical scroll bar)
        setMaximumRowCount( getItemCount() );
    }
    
    public void setSolute( Solute solute ) {
        setSelectedItem( solute );
        if ( getSelectedItem() != solute ) {
            throw new IllegalArgumentException( "solute is not in the list: " + solute.toString() );
        }
    }
    
    public Solute getSolute() {
        return (Solute)getSelectedItem();
    }
    
    public void reset() {
        setSelectedItem( defaultChoice );
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setContentPane( new SoluteComboBox() );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
