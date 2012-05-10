// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants;
import edu.colorado.phet.chemicalreactions.ChemicalReactionsResources.Strings;
import edu.colorado.phet.chemicalreactions.model.Kit;
import edu.colorado.phet.chemicalreactions.model.KitCollection;
import edu.colorado.phet.chemicalreactions.model.Molecule;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.NextPreviousNavigationNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.MODEL_VIEW_TRANSFORM;

/**
 * Contains the kit background and controls for switching between kits
 */
public class KitPanel extends PNode {

    private static final double KIT_ARROW_Y_OFFSET = 5; // vertical offset of the kit arrows from the top of the kit

    public KitPanel( final KitCollection kitCollectionModel, PBounds availableKitBounds ) {

        assert ( MODEL_VIEW_TRANSFORM.getTransform().getScaleY() < 0 ); // we assume this and correct for it

        final Rectangle2D kitViewBounds = MODEL_VIEW_TRANSFORM.modelToViewRectangle( availableKitBounds );
        PPath background = PPath.createRectangle(
                (float) kitViewBounds.getX(),
                (float) kitViewBounds.getY(),
                (float) kitViewBounds.getWidth(),
                (float) kitViewBounds.getHeight() );

        background.setPaint( ChemicalReactionsConstants.KIT_BACKGROUND );
        background.setStrokePaint( ChemicalReactionsConstants.KIT_BORDER );
        addChild( background );

        /*---------------------------------------------------------------------------*
        * label and next/previous
        *----------------------------------------------------------------------------*/

        addChild( new NextPreviousNavigationNode( new PText() {{
            // this is our label
            setFont( new PhetFont( 18, true ) );
            kitCollectionModel.getCurrentKitProperty().addObserver( new SimpleObserver() {
                public void update() {
                    setText( MessageFormat.format( Strings.KIT__LABEL, kitCollectionModel.getCurrentKitIndex() + 1 ) );
                }
            } );
        }}, ChemicalReactionsConstants.KIT_ARROW_BACKGROUND_ENABLED, ChemicalReactionsConstants.KIT_ARROW_BORDER_ENABLED, 17, 24 ) {
            {
                // hook up listeners
                kitCollectionModel.getCurrentKitProperty().addObserver( new SimpleObserver() {
                    public void update() {
                        hasNext.set( kitCollectionModel.hasNextKit() );
                        hasPrevious.set( kitCollectionModel.hasPreviousKit() );
                    }
                } );

                setOffset( kitViewBounds.getMaxX() - getFullBounds().getWidth() - 5, kitViewBounds.getY() + KIT_ARROW_Y_OFFSET );
            }

            @Override protected void next() {
                kitCollectionModel.goToNextKit();
            }

            @Override protected void previous() {
                kitCollectionModel.goToPreviousKit();
            }
        } );

        /*---------------------------------------------------------------------------*
        * refill kit
        *----------------------------------------------------------------------------*/

        addChild( new HTMLImageButtonNode( Strings.KIT__RESET_KIT, new PhetFont( Font.BOLD, 12 ), Color.ORANGE ) {
            private SimpleObserver observer; // makes sure that we are enabled or disabled whenever the current kit is

            {
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        kitCollectionModel.getCurrentKit().resetKit();
                    }
                } );
                observer = new SimpleObserver() {
                    public void update() {
                        setEnabled( kitCollectionModel.getCurrentKit().hasMoleculesOutsideOfBuckets() );
                    }
                };
                for ( Kit kit : kitCollectionModel.getKits() ) {
                    kit.addMoleculeListener( new Kit.MoleculeListener() {
                        public void addedMolecule( Molecule molecule ) {
                            observer.update();
                        }

                        public void removedMolecule( Molecule molecule ) {
                            observer.update();
                        }
                    } );
                }
                kitCollectionModel.getCurrentKitProperty().addObserver( observer );
                setOffset( kitViewBounds.getMinX() + 5, kitViewBounds.getMinY() + 5 );
            }
        } );
    }
}