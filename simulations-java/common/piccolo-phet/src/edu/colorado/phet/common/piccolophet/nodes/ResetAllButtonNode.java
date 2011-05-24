// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.ResetAllDelegate;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PCanvas;

/**
 * Piccolo version of the "Reset All" button.
 * When it's pressed, requests confirmation.
 * If confirmation is affirmative, then all Resettables are reset.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ResetAllButtonNode extends HTMLImageButtonNode {

    private final ResetAllDelegate delegate; // delegate that implements Reset All behavior

    /**
     * @param resettable      thing to reset
     * @param parent          parent component for the confirmation dialog
     * @param fontSize
     * @param textColor
     * @param backgroundColor
     */
    public ResetAllButtonNode( final Resettable resettable, final Component parent, int fontSize, Color textColor, Color backgroundColor ) {
        this( new Resettable[] { resettable }, parent, fontSize, textColor, backgroundColor );
    }

    /**
     * @param resettables     things to reset
     * @param parent          parent component for the confirmation dialog
     * @param fontSize
     * @param textColor
     * @param backgroundColor
     */
    public ResetAllButtonNode( final Resettable[] resettables, final Component parent, int fontSize, Color textColor, Color backgroundColor ) {
        super( PhetCommonResources.getString( PhetCommonResources.STRING_RESET_ALL ), new PhetFont( fontSize ), textColor, backgroundColor );
        this.delegate = new ResetAllDelegate( resettables, parent );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                delegate.resetAll();
            }
        } );
    }

    public void setConfirmationEnabled( boolean confirmationEnabled ) {
        delegate.setConfirmationEnabled( confirmationEnabled );
    }

    public boolean isConfirmationEnabled() {
        return delegate.isConfirmationEnabled();
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
                System.out.println( "resettable1.reset" );
            }
        };
        Resettable resettable2 = new Resettable() {
            public void reset() {
                System.out.println( "resettable2.reset" );
            }
        };

        PCanvas canvas = new PCanvas();
        canvas.setPreferredSize( new Dimension( 300, 300 ) );

        ResetAllButtonNode buttonNode1 = new ResetAllButtonNode( resettable1, canvas, 18, Color.BLACK, Color.RED );
        buttonNode1.addResettable( resettable2 );
        buttonNode1.setOffset( 50, 100 );
        canvas.getLayer().addChild( buttonNode1 );

        ResetAllButtonNode buttonNode2 = new ResetAllButtonNode( resettable1, canvas, 18, Color.BLACK, Color.YELLOW );
        buttonNode2.setConfirmationEnabled( false ); // disable confirmation
        buttonNode2.addResettable( resettable2 );
        buttonNode2.setOffset( buttonNode1.getFullBoundsReference().getMaxX() + 10, buttonNode1.getYOffset() );
        canvas.getLayer().addChild( buttonNode2 );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
