/* Copyright 2010, University of Colorado */

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.ResetAllDelegate;
import edu.umd.cs.piccolo.PCanvas;

/**
 * Piccolo version of the "Reset All" button.  
 * When it's pressed, requests confirmation.
 * If confirmation is affirmative, then all Resettables are reset.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ResetAllButtonNode extends ButtonNode {
    
    private final ResetAllDelegate delegate; // delegate that implements Reset All behavior

    /**
     * 
     * @param resettable thing to reset
     * @param parent parent component for the confirmation dialog
     * @param fontSize
     * @param textColor
     * @param backgroundColor
     */
    public ResetAllButtonNode( final Resettable resettable, final Component parent, int fontSize, Color textColor, Color backgroundColor ) {
        this( new Resettable[] { resettable }, parent, fontSize, textColor, backgroundColor );
    }

    /**
     * 
     * @param resettables things to reset
     * @param parent parent component for the confirmation dialog
     * @param fontSize
     * @param textColor
     * @param backgroundColor
     */
    public ResetAllButtonNode( final Resettable[] resettables, final Component parent, int fontSize, Color textColor, Color backgroundColor  ) {
        super( PhetCommonResources.getString( PhetCommonResources.STRING_RESET_ALL ), fontSize, textColor, backgroundColor );
        this.delegate = new ResetAllDelegate( resettables, parent );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
               delegate.resetAll();
            }
        } );
    }

    public void addResettable( Resettable resettable ) {
        delegate.addResettable( resettable );
    }
    
    public void removeResettable( Resettable resettable ) {
        delegate.removeResettable( resettable );
    }
    
    public static void main( String[] args ) {
        
        Resettable resettable1 = new Resettable() {
            public void reset() {
                System.out.println( "Reset 1" );
            }
        };
        Resettable resettable2 = new Resettable() {
            public void reset() {
                System.out.println( "Reset 2" );
            }
        };
        
        PCanvas canvas = new PCanvas();
        canvas.setPreferredSize( new Dimension( 300, 300 ) );
        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( resettable1, canvas, 18, Color.BLACK, Color.RED );
        resetAllButtonNode.addResettable( resettable2 );
        resetAllButtonNode.setOffset( 100, 100 );
        canvas.getLayer().addChild( resetAllButtonNode );
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
