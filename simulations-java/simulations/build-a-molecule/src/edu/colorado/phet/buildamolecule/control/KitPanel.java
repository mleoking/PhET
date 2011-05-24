// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.KitCollection;
import edu.colorado.phet.buildamolecule.model.Molecule;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Contains the kit background and controls for switching between kits
 */
public class KitPanel extends PNode {

    private static final double KIT_LABEL_ARROW_PADDING = 8;
    private static final double KIT_LABEL_Y_OFFSET = 5;
    private static final double KIT_ARROW_Y_OFFSET = 5;

    public KitPanel( final KitCollection kitCollectionModel, PBounds availableKitBounds, ModelViewTransform mvt ) {
        assert ( mvt.getTransform().getScaleY() < 0 ); // we assume this and correct for it

        final Rectangle2D kitViewBounds = mvt.modelToViewRectangle( availableKitBounds );
        PPath background = PPath.createRectangle(
                (float) kitViewBounds.getX(),
                (float) kitViewBounds.getY(),
                (float) kitViewBounds.getWidth(),
                (float) kitViewBounds.getHeight() );

        background.setPaint( BuildAMoleculeConstants.KIT_BACKGROUND );
        background.setStrokePaint( BuildAMoleculeConstants.KIT_BORDER );
        addChild( background );

        /*---------------------------------------------------------------------------*
        * next kit
        *----------------------------------------------------------------------------*/

        final PhetPPath nextKitNode = new PhetPPath( new DoubleGeneralPath() {{
            // triangle pointing to the right
            moveTo( 17, 12 );
            lineTo( 0, 0 );
            lineTo( 0, 24 );
            closePath();
        }}.getGeneralPath() ) {{
            setPaint( BuildAMoleculeConstants.KIT_ARROW_BACKGROUND_ENABLED );
            setStrokePaint( BuildAMoleculeConstants.KIT_ARROW_BORDER_ENABLED );
            addInputEventListener( new CursorHandler() {
                @Override
                public void mouseClicked( PInputEvent event ) {
                    if ( kitCollectionModel.hasNextKit() ) {
                        kitCollectionModel.goToNextKit();
                    }
                }
            } );
            setOffset( kitViewBounds.getMaxX() - getFullBounds().getWidth() - 5, kitViewBounds.getY() + KIT_ARROW_Y_OFFSET );
            kitCollectionModel.getCurrentKitProperty().addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( kitCollectionModel.hasNextKit() );
                }
            } );
        }};
        addChild( nextKitNode );

        /*---------------------------------------------------------------------------*
        * kit number label
        *----------------------------------------------------------------------------*/

        // label the kit with the kit number
        final PText kitLabel = new PText() {{
            setFont( new PhetFont( 18, true ) );
            kitCollectionModel.getCurrentKitProperty().addObserver( new SimpleObserver() {
                public void update() {
                    setText( MessageFormat.format( BuildAMoleculeStrings.KIT_LABEL, kitCollectionModel.getCurrentKitIndex() + 1 ) );
                }
            } );
            setOffset( nextKitNode.getFullBounds().getX() - getFullBounds().getWidth() - KIT_LABEL_ARROW_PADDING, kitViewBounds.getY() + KIT_LABEL_Y_OFFSET );
        }};
        addChild( kitLabel );

        /*---------------------------------------------------------------------------*
        * previous kit
        *----------------------------------------------------------------------------*/

        final PhetPPath previousKitNode = new PhetPPath( new DoubleGeneralPath() {{
            // triangle pointing to the left
            moveTo( 0, 12 );
            lineTo( 17, 0 );
            lineTo( 17, 24 );
            closePath();
        }}.getGeneralPath() ) {{
            setPaint( BuildAMoleculeConstants.KIT_ARROW_BACKGROUND_ENABLED );
            setStrokePaint( BuildAMoleculeConstants.KIT_ARROW_BORDER_ENABLED );
            addInputEventListener( new CursorHandler() {
                @Override
                public void mouseClicked( PInputEvent event ) {
                    if ( kitCollectionModel.hasPreviousKit() ) {
                        kitCollectionModel.goToPreviousKit();
                    }
                }
            } );
            setOffset( kitLabel.getFullBounds().getX() - getFullBounds().getWidth() - KIT_LABEL_ARROW_PADDING, kitViewBounds.getY() + KIT_LABEL_Y_OFFSET );
            kitCollectionModel.getCurrentKitProperty().addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( kitCollectionModel.hasPreviousKit() );
                }
            } );
        }};
        addChild( previousKitNode );

        /*---------------------------------------------------------------------------*
        * reset kit
        *----------------------------------------------------------------------------*/

        addChild( new ButtonNode( BuildAMoleculeStrings.KIT_RESET, new PhetFont( Font.BOLD, 12 ), Color.ORANGE ) {
            private SimpleObserver observer; // makes sure that we are enabled or disabled whenever the current kit is

            {
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        kitCollectionModel.getCurrentKit().resetKit();
                    }
                } );
                observer = new SimpleObserver() {
                    public void update() {
                        setEnabled( kitCollectionModel.getCurrentKit().hasAtomsOutsideOfBuckets() );
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
