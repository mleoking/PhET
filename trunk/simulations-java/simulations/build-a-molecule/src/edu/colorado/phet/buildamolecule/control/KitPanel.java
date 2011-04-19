// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import javax.swing.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.KitCollectionModel;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
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

        background.setPaint( BuildAMoleculeConstants.KIT_BACKGROUND );
        background.setStrokePaint( BuildAMoleculeConstants.KIT_BORDER );
        addChild( background );

        // label the kit with the kit number
        addChild( new PText() {{
            int kitLabelXPadding = 10;
            int kitLabelYPadding = 5;
            setOffset( kitViewBounds.getX() + kitLabelXPadding, kitViewBounds.getY() + kitLabelYPadding );
            setFont( new PhetFont( 18, true ) );
            kitCollectionModel.getCurrentKitProperty().addObserver( new SimpleObserver() {
                public void update() {
                    setText( MessageFormat.format( BuildAMoleculeStrings.KIT_LABEL, kitCollectionModel.getCurrentKitIndex() + 1 ) );
                }
            } );
        }} );

        // TODO: add reset kit

        final PSwing previousKitNode = new PSwing( new JButton( BuildAMoleculeStrings.KIT_PREVIOUS ) {{
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

        PSwing nextKitNode = new PSwing( new JButton( BuildAMoleculeStrings.KIT_NEXT ) {{
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
