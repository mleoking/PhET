// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.buildamolecule.model.KitCollectionModel;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Contains the kit background and controls for switching between kits
 */
public class KitPanel extends PNode {
    public KitPanel( final KitCollectionModel kitCollectionModel, ModelViewTransform mvt ) {
        assert ( mvt.getTransform().getScaleY() < 0 ); // we assume this and correct for it

        final Rectangle2D kitViewBounds = mvt.modelToViewRectangle( kitCollectionModel.getAvailableKitBounds() );
        PPath background = PPath.createRectangle(
                (float) kitViewBounds.getX(),
                (float) kitViewBounds.getY(),
                (float) kitViewBounds.getWidth(),
                (float) kitViewBounds.getHeight() );

        background.setPaint( Color.WHITE );
        background.setStrokePaint( Color.BLACK );
        addChild( background );

        // TODO: i18n
        final PSwing previousKitNode = new PSwing( new JButton( "Previous Kit (dev)" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    kitCollectionModel.previousKit();
                }
            } );
            kitCollectionModel.getCurrentKitProperty().addObserver( new SimpleObserver() {
                public void update() {
                    setEnabled( kitCollectionModel.hasPreviousKit() );
                }
            } );
        }} ) {{
            setOffset( kitViewBounds.getX(), kitViewBounds.getY() + kitViewBounds.getHeight() );
        }};
        addChild( previousKitNode );

        PSwing nextKitNode = new PSwing( new JButton( "Next Kit (dev)" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    kitCollectionModel.nextKit();
                }
            } );
            kitCollectionModel.getCurrentKitProperty().addObserver( new SimpleObserver() {
                public void update() {
                    setEnabled( kitCollectionModel.hasNextKit() );
                }
            } );
        }} ) {{
            setOffset( kitViewBounds.getX() + previousKitNode.getFullBounds().getWidth(), kitViewBounds.getY() + kitViewBounds.getHeight() );
        }};
        addChild( nextKitNode );
    }
}
