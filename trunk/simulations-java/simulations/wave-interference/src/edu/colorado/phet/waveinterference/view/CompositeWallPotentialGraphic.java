// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.waveinterference.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.event.PopupMenuHandler;
import edu.colorado.phet.waveinterference.WallPotential;
import edu.colorado.phet.waveinterference.model.CompositePotential;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Created by: Sam
 * Feb 7, 2008 at 1:21:18 PM
 */
public class CompositeWallPotentialGraphic extends PNode {
    private JComponent panel;
    private CompositePotential wallPotentialGraphic;
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private RotationWaveGraphic rotationWaveGraphic;

    public CompositeWallPotentialGraphic( JComponent panel, CompositePotential wallPotentialGraphic, LatticeScreenCoordinates latticeScreenCoordinates, final RotationWaveGraphic rotationWaveGraphic ) {
        this.rotationWaveGraphic = rotationWaveGraphic;
        this.panel = panel;
        this.wallPotentialGraphic = wallPotentialGraphic;
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        wallPotentialGraphic.addListener( new CompositePotential.Listener() {
            public void potentialAdded() {
                update();
            }

            public void potentialRemoved() {
                update();
            }
        } );
        update();
        rotationWaveGraphic.addListener( new RotationWaveGraphic.Listener() {
            public void rotationChanged() {
                updateVisible();
            }
        } );
        updateVisible();
    }

    private void updateVisible() {
        setVisible( rotationWaveGraphic.getRotation() == 0 );
    }

    public void reset() {
        removeAllChildren();
    }

    private void update() {
        removeAllChildren();
        for ( int i = 0; i < wallPotentialGraphic.numPotentials(); i++ ) {
            final WallPotentialGraphic child = new WallPotentialGraphic( (WallPotential) wallPotentialGraphic.getPotential( i ), latticeScreenCoordinates );
            final int i1 = i;
            JPopupMenu itemMenu = new JPopupMenu();
            final JMenuItem menuItem = new JMenuItem( "Remove" );
            menuItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    doDelete( i1 );
                }
            } );
            itemMenu.add( menuItem );
            child.addInputEventListener( new PopupMenuHandler( panel, itemMenu ) );
            child.addInputEventListener( new PBasicInputEventHandler() {//todo: why isn't this working?

                public void keyReleased( PInputEvent event ) {
                    super.keyReleased( event );
                    if ( event.getKeyCode() == KeyEvent.VK_DELETE ) {
                        doDelete( i1 );
                    }
                }
            } );
            addChild( child );
        }
    }

    private void doDelete( int i1 ) {
        wallPotentialGraphic.removePotential( wallPotentialGraphic.getPotential( i1 ) );
    }
}
