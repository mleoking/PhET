
package edu.colorado.phet.acidbasesolutions.control;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.*;
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
    
    public SoluteComboBox() {
        super();
        
        Object defaultChoice = new CustomAcidChoice();
        
        // pure water
        addItem( new PureWater() );
        // specific acids & bases, English alphabetical order
        addItem( new WeakAcidSolution( new Acid.AceticAcid() ) );
        addItem( new WeakBaseSolution( new Base.Ammonia() ) );
        addItem( new WeakAcidSolution( new Acid.ChlorusAcid() ) );
        addItem( new StrongAcidSolution( new Acid.HydrochloricAcid() ) );
        addItem( new WeakAcidSolution( new Acid.HydrofluoricAcid() ) );
        addItem( new WeakAcidSolution( new Acid.HypochlorusAcid() ) );
        addItem( new StrongAcidSolution( new Acid.PerchloricAcid() ) );
        addItem( new WeakBaseSolution( new Base.Pyridine() ) );
        addItem( new StrongBaseSolution( new Base.SodiumHydroxide() ) );
        // custom
        addItem( defaultChoice );
        addItem( new CustomBaseChoice() );
        
        // default selection
        setSelectedItem( defaultChoice );
        
        // make all items visible (no vertical scroll bar)
        setMaximumRowCount( getItemCount() );
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setContentPane( new SoluteComboBox() );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
