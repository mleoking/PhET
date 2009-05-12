
package edu.colorado.phet.acidbasesolutions.control;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.NullSolute;
import edu.colorado.phet.acidbasesolutions.model.Acid.*;
import edu.colorado.phet.acidbasesolutions.model.Base.Ammonia;
import edu.colorado.phet.acidbasesolutions.model.Base.Pyridine;
import edu.colorado.phet.acidbasesolutions.model.Base.SodiumHydroxide;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;


public class SoluteComboBox extends JComboBox {

    public abstract static class CustomSolutionChoice {

        private final String name;
        private final String symbol;

        protected CustomSolutionChoice( String name, String symbol ) {
            this.name = name;
            this.symbol = symbol;
        }

        public String toString() {
            return HTMLUtils.toHTMLString( name + " (" + symbol + ")" );
        }
    }
    
    public static class CustomAcidChoice extends CustomSolutionChoice {
        public CustomAcidChoice() {
            super( ABSStrings.CUSTOM_ACID, ABSSymbols.HA );
        }
    }
    
    public static class CustomBaseChoice extends CustomSolutionChoice {
        public CustomBaseChoice() {
            super( ABSStrings.CUSTOM_BASE, ABSSymbols.B );
        }
    }
    
    private final Object defaultChoice;
    
    public SoluteComboBox() {
        super();
        
        defaultChoice = new CustomAcidChoice();
        
        // no solute
        addItem( new NullSolute() );
        
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
        addItem( new CustomBaseChoice() );
        
        // default selection
        setSelectedItem( defaultChoice );
        
        // make all items visible (no vertical scroll bar)
        setMaximumRowCount( getItemCount() );
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
