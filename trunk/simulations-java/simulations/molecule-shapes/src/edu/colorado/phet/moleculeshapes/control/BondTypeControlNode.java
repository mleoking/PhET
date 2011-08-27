// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Images;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel.AnyChangeAdapter;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * TODO: doc and cleanup
 */
class BondTypeControlNode extends PNode {
    private final MoleculeJMEApplication app;
    private final PNode graphic;
    private final int bondOrder;
    private final PNode removeButton;
    protected boolean enabled = true;
    protected boolean showingRemoveButton = false;

    public BondTypeControlNode( final MoleculeJMEApplication app, final PNode graphic, final int bondOrder ) {
        this.app = app;
        this.graphic = graphic;
        this.bondOrder = bondOrder;

        addChild( graphic );

        // add a blank background that will allow the user to click on this
        graphic.addChild( 0, new PhetPPath( graphic.getFullBounds(), new Color( 0, 0, 0, 0 ) ) );

        removeButton = new PImage( Images.REMOVE );
        removeButton.setOffset( MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH - removeButton.getFullBounds().getWidth(),
                                ( graphic.getFullBounds().getHeight() - removeButton.getFullBounds().getHeight() ) / 2 );
        addChild( removeButton );

        graphic.setOffset( ( MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH - removeButton.getFullBounds().getWidth() - graphic.getFullBounds().getWidth() ) / 2, 0 );

        removeButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseClicked( PInputEvent event ) {
                PairGroup candidate = getLastMatchingGroup();

                // if it exists, remove it
                if ( candidate != null ) {
                    app.removePairGroup( candidate );
                }
            }
        } );

        app.getMolecule().addListener( new AnyChangeAdapter() {
            @Override public void onGroupChange( PairGroup group ) {
                updateState();
            }
        } );
        updateState();

        // custom cursor handler for only showing hand when it is enabled
        graphic.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseEntered( PInputEvent event ) {
                if ( enabled ) {
                    ( (JComponent) event.getComponent() ).setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
                }
            }

            @Override public void mouseExited( PInputEvent event ) {
                ( (JComponent) event.getComponent() ).setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
            }
        } );

        removeButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseEntered( PInputEvent event ) {
                if ( showingRemoveButton ) {
                    ( (JComponent) event.getComponent() ).setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
                }
            }

            @Override public void mouseExited( PInputEvent event ) {
                ( (JComponent) event.getComponent() ).setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
            }
        } );
    }

    public void addDragListener( PBasicInputEventHandler dragListener ) {
        graphic.addInputEventListener( dragListener );
    }

    private boolean hasMatchingGroup() {
        return getLastMatchingGroup() != null;
    }

    private PairGroup getLastMatchingGroup() {
        // find the last pair group that has the desired bond order
        java.util.List<PairGroup> groups = new ArrayList<PairGroup>( app.getMolecule().getGroups() );

        Collections.reverse( groups ); // reverse it so we pick the last, not the 1st

        for ( PairGroup group : groups ) {
            if ( group.bondOrder == bondOrder ) {
                return group;
            }
        }
        return null;
    }

    protected boolean isEnabled() {
        return app.getMolecule().wouldAllowBondOrder( bondOrder );
    }

    protected void updateState() {
        enabled = isEnabled();
        showingRemoveButton = hasMatchingGroup();

        graphic.setTransparency( enabled ? 1 : 0.7f );
        removeButton.setVisible( showingRemoveButton );

        repaint();
    }
}
