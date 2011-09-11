// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;

import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Images;
import edu.colorado.phet.moleculeshapes.jme.JMECursorHandler;
import edu.colorado.phet.moleculeshapes.jme.JMEUtils;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.util.Fireable;
import edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Displays a graphic showing a bonding type (single/double/triple/lone pair) where dragging the graphic
 * creates the real bond in 3D. Also has a button to remove a bond of that same type from play.
 */
public class BondTypeControlNode extends PNode {
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
        graphic.addChild( 0, new Spacer( graphic.getFullBounds() ) );

        removeButton = new PImage( Images.REMOVE );
        removeButton.setOffset( MoleculeShapesControlPanel.INNER_WIDTH - removeButton.getFullBounds().getWidth(),
                                ( graphic.getFullBounds().getHeight() - removeButton.getFullBounds().getHeight() ) / 2 );
        addChild( removeButton );

        graphic.setOffset( ( MoleculeShapesControlPanel.INNER_WIDTH - removeButton.getFullBounds().getWidth() - graphic.getFullBounds().getWidth() ) / 2, 0 );

        removeButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseClicked( PInputEvent event ) {
                JMEUtils.invoke( new Runnable() {
                    public void run() {
                        PairGroup candidate = getLastMatchingGroup();

                        // if it exists, remove it
                        if ( candidate != null ) {
                            app.removePairGroup( candidate );
                        }
                    }
                } );
            }
        } );

        app.getMolecule().onGroupChanged.addTarget( new Fireable<PairGroup>() {
            public void fire( PairGroup pairGroup ) {
                updateState();
            }
        } );
        updateState();

        // custom cursor handler for only showing hand when it is enabled
        graphic.addInputEventListener( new JMECursorHandler() {
            @Override public boolean isEnabled() {
                return enabled;
            }
        } );

        removeButton.addInputEventListener( new JMECursorHandler() {
            @Override public boolean isEnabled() {
                return showingRemoveButton;
            }
        } );

        addDragEvent( new Runnable() {
            public void run() {
                app.startNewInstanceDrag( bondOrder );
            }
        } );
    }

    /**
     * Invoke the following runnable if we are enabled AND the user presses the left mouse button down
     * on the graphic.
     *
     * @param runnable Code to run (will run in the JME3 thread)
     */
    public void addDragEvent( final Runnable runnable ) {
        graphic.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( final PInputEvent event ) {
                if ( enabled && event.getButton() == MouseEvent.BUTTON1 ) {
                    JMEUtils.invoke( runnable );
                }
            }
        } );
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
