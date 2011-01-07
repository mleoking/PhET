// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.view.piccolo;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.quantumwaveinterference.QWILookAndFeel;
import edu.colorado.phet.quantumwaveinterference.model.potentials.RectangularPotential;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:54:38 PM
 */

public class RectangularPotentialGraphic extends RectangleGraphic {
    private RectangularPotential potential;
    private PText potDisplay;
    private PSwing closeGraphic;

    public RectangularPotentialGraphic( QWIPanel component, final RectangularPotential potential ) {
        super( component, potential, new Color( 255, 30, 0, 45 ) );
        this.potential = potential;

        potDisplay = new PText( "" );
        potDisplay.setFont( new PhetFont( Font.BOLD, 14 ) );
        potDisplay.setTextPaint( Color.blue );
        potDisplay.setPickable( false );
        potDisplay.setChildrenPickable( false );
        addChild( potDisplay );
        addInputEventListener( new PDragEventHandler() {
            protected void drag( PInputEvent event ) {
                updateGraphics();
            }

        } );
        potential.addObserver( new SimpleObserver() {
            public void update() {
                RectangularPotentialGraphic.this.update();
            }
        } );
        JButton closeButton = QWILookAndFeel.createCloseButton();
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                remove();
            }
        } );
        closeGraphic = new PSwing( closeButton );
        addChild( closeGraphic );
        closeGraphic.setOffset( -closeGraphic.getWidth() - 2, 0 );
        update();
        updateGraphics();
    }

    protected void disableCloseGraphic() {
        closeGraphic.setVisible( false );
        closeGraphic.setPickable( false );
        closeGraphic.setChildrenPickable( false );
    }

    protected void disablePotentialDisplayGraphic() {
        potDisplay.setVisible( false );
        potDisplay.setPickable( false );
        potDisplay.setChildrenPickable( false );
    }

    private void updateGraphics() {
        super.getSchrodingerPanel().updateWaveGraphic();
    }

    private void remove() {
        super.getSchrodingerPanel().getSchrodingerModule().removePotential( this );
    }

    protected void update() {
        super.update();
        if( potential != null ) {
            Rectangle modelRect = potential.getBounds();
            Rectangle viewRect = super.getViewRectangle( modelRect );
            potDisplay.setText( "" );
            potDisplay.setOffset( (int)viewRect.getX(), (int)viewRect.getY() );
            closeGraphic.setOffset( (int)viewRect.getX() - closeGraphic.getWidth() - 2, (int)viewRect.getY() );
        }
    }

    public RectangularPotential getPotential() {
        return potential;
    }

}
