
package edu.colorado.phet.acidbasesolutions.control;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.PureWater;
import edu.colorado.phet.acidbasesolutions.model.StrongAcidSolution.HydrochloricAcidSolution;
import edu.colorado.phet.acidbasesolutions.model.StrongAcidSolution.PerchloricAcidSolution;
import edu.colorado.phet.acidbasesolutions.model.StrongBaseSolution.SodiumHydroxideSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakAcidSolution.AceticAcidSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakAcidSolution.ChlorousAcidSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakAcidSolution.HydrofluoricAcidSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakAcidSolution.HypochlorusAcidSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakBaseSolution.AmmoniaSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakBaseSolution.PyridineSolution;
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
        
        // pure water
        addItem( new PureWater() );
        // specific solutions, English alphabetical order
        addItem( new AceticAcidSolution() );
        addItem( new AmmoniaSolution() );
        addItem( new ChlorousAcidSolution() );
        addItem( new HydrochloricAcidSolution() );
        addItem( new HydrofluoricAcidSolution() );
        addItem( new HypochlorusAcidSolution() );
        addItem( new PerchloricAcidSolution() );
        addItem( new PyridineSolution() );
        addItem( new SodiumHydroxideSolution() );
        // custom
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
