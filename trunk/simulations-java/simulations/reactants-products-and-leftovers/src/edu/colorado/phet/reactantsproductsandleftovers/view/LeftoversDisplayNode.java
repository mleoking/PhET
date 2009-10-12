package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.SandwichShop;
import edu.umd.cs.piccolox.pswing.PSwing;


public class LeftoversDisplayNode extends PhetPNode {
    
    private final SandwichShop model;
    private final JLabel bread, meat, cheese;
    
    public LeftoversDisplayNode( final SandwichShop model ) {
        super();
        
        this.model = model;
        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        });
        
        bread = new JLabel();
        meat = new JLabel();
        cheese = new JLabel();
        
        // layout
        JPanel panel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setAnchor( GridBagConstraints.EAST );
        panel.setLayout( layout );
        int row = 0;
        int col = 0;
        layout.addComponent( new JLabel( "Leftovers" ), row, col++ );
        row++;
        col = 0;
        layout.addComponent( new JLabel( "bread:" ), row, col++ );
        layout.addComponent( bread, row, col++ );
        row++;
        col = 0;
        layout.addComponent( new JLabel( "meat:" ), row, col++ );
        layout.addComponent( meat, row, col++ );
        row++;
        col = 0;
        layout.addComponent( new JLabel( "cheese:" ), row, col++ );
        layout.addComponent( cheese, row, col++ );
        
        addChild( new PSwing( panel ) );
        
        update();
    }
    
    private void update() {
        bread.setText( String.valueOf( model.getBreadLeftover() ) );
        meat.setText( String.valueOf( model.getMeatLeftover() ) );
        cheese.setText( String.valueOf( model.getCheeseLeftover() ) );
    }
}
