package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.SandwichShop;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.umd.cs.piccolox.pswing.PSwing;


public class TestSandwichModelNode extends PhetPNode {
    
    private final SandwichShop model;
    private final JLabel sandwiches, breadLeftover, meatLeftover, cheeseLeftover;
    
    public TestSandwichModelNode( final SandwichShop model ) {
        super();
        
        this.model = model;
        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        });
        
        final IntegerSpinner breadSpinner = new IntegerSpinner( SandwichShopDefaults.REACTION_BREAD_RANGE );
        breadSpinner.setIntValue( model.getBread() );
        breadSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setBread( breadSpinner.getIntValue() );
            }
        });
        
        final IntegerSpinner meatSpinner = new IntegerSpinner( SandwichShopDefaults.REACTION_MEAT_RANGE );
        meatSpinner.setIntValue( model.getMeat() );
        meatSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setMeat( meatSpinner.getIntValue() );
            }
        });
        
        final IntegerSpinner cheeseSpinner = new IntegerSpinner( SandwichShopDefaults.REACTION_CHEESE_RANGE );
        cheeseSpinner.setIntValue( model.getCheese() );
        cheeseSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setCheese( cheeseSpinner.getIntValue() );
            }
        });
        
        sandwiches = new JLabel();
        breadLeftover = new JLabel();
        meatLeftover = new JLabel();
        cheeseLeftover = new JLabel();
        
        // layout
        JPanel panel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setAnchor( GridBagConstraints.EAST );
        panel.setLayout( layout );
        int row = 0;
        int col = 0;
        layout.addComponent( new JLabel( "item"), row, col++ );
        layout.addComponent( new JLabel( "quantity"), row, col++ );
        layout.addComponent( new JLabel( "leftover"), row, col++ );
        row++;
        col = 0;
        layout.addComponent( new JLabel( "bread" ), row, col++ );
        layout.addComponent( breadSpinner, row, col++ );
        layout.addComponent( breadLeftover, row, col++ );
        row++;
        col = 0;
        layout.addComponent( new JLabel( "meat" ), row, col++ );
        layout.addComponent( meatSpinner, row, col++ );
        layout.addComponent( meatLeftover, row, col++ );
        row++;
        col = 0;
        layout.addComponent( new JLabel( "cheese" ), row, col++ );
        layout.addComponent( cheeseSpinner, row, col++ );
        layout.addComponent( cheeseLeftover, row, col++ );
        row++;
        col = 0;
        layout.addComponent( new JLabel( "sandwiches" ), row, col++ );
        layout.addComponent( sandwiches, row, col++ );
        
        addChild( new PSwing( panel ) );
        
        update();
    }
    
    private void update() {
        breadLeftover.setText( String.valueOf( model.getBreadLeftover() ) );
        meatLeftover.setText( String.valueOf( model.getMeatLeftover() ) );
        cheeseLeftover.setText( String.valueOf( model.getCheeseLeftover() ) );
        sandwiches.setText( String.valueOf( model.getSandwiches() ) );
    }
    
    private static class IntegerSpinner extends JSpinner {
        public IntegerSpinner( IntegerRange range ) {
            super();
            setModel( new SpinnerNumberModel( range.getDefault(), range.getMin(), range.getMax(), 1 ) );
            NumberEditor editor = new NumberEditor( this );
            editor.getTextField().setColumns( 2 );
            setEditor(editor );
        }
        
        public void setIntValue( int value ) {
            setValue( new Integer( value ) );
        }
        
        public int getIntValue() {
            return ((Integer)getValue()).intValue();
        }
    }

}
