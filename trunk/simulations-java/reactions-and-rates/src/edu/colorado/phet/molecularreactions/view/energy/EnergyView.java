/* Copyright 2003-2007, University of Colorado */

package edu.colorado.phet.molecularreactions.view.energy;

import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.util.Resetable;
import edu.colorado.phet.molecularreactions.view.ReactionGraphic;
import edu.colorado.phet.piccolo.nodes.PNodeShowHideControl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * EnergyView
 * <p/>
 * A view of the MRModel that shows the potential energy of two individual molecules,
 * or their composite molecule. This is a fairly abstract view.
 * <p/>
 * The diagram below shows the basic layout of this view, with the names of fields
 * corresponding to its main elements
 * <p/>
 * -------------------------------------------
 * |                                         |
 * |                                         |
 * |           upperPane                     |
 * |                                         |
 * |                                         |
 * -------------------------------------------
 * |          curvePane                      |
 * |  .....................................  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .       curveArea                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .....................................  |
 * |  .       legendPane                  .  |
 * |  .....................................  |
 * -------------------------------------------
 */
public class EnergyView extends PNode implements Resetable {
    private volatile MoleculeSeparationPane moleculeSeparationPane;
    private volatile UpperEnergyPane upperPane;
    private volatile CurvePane curvePane;
    private volatile PPath legendNode;
    private volatile MRModule module;

    private volatile PNodeShowHideControl curvePaneCloser;
    private volatile PNodeShowHideControl moleculeSeparationCloser;

    private volatile PNode upperPaneContent;

    public EnergyView() {
    }

    public boolean isInitialized() {
        return upperPane != null;
    }

    public void initialize( MRModule module, Dimension upperPaneSize ) {
        if (isInitialized()) return;
        
        this.module = module;

        removeAllChildren();

        addCurvePane( module, upperPaneSize );
        addUpperPane( upperPaneSize );
        addMolecularSeparationPane( module, upperPaneSize );

        addLegend( upperPaneSize, module );
        addEnergyViewBorder();
    }

    private void addEnergyViewBorder() {
        // Put a border around the energy view
        Rectangle2D bRect = new Rectangle2D.Double( 0, 0,
                                                    curvePane.getFullBounds().getWidth(),
                                                    curvePane.getFullBounds().getHeight() + legendNode.getFullBounds().getHeight() );
        PPath border = new PPath( bRect );
        border.setOffset( curvePane.getOffset() );
        addChild( border );
    }

    private void addLegend( Dimension upperPaneSize, MRModule module ) {
        MRModel mrModel = module.getMRModel();

        // The graphic that shows the reaction mechanics. It appears below the profile pane.
        legendNode = new PPath( new Rectangle2D.Double( 0, 0,
                                                                     MRConfig.ENERGY_VIEW_REACTION_LEGEND_SIZE.width,
                                                              MRConfig.ENERGY_VIEW_REACTION_LEGEND_SIZE.height ) );
        legendNode.setPaint( MRConfig.ENERGY_PANE_BACKGROUND );
        legendNode.setStrokePaint( new Color( 0, 0, 0, 0 ) );
        legendNode.setOffset( 0, upperPaneSize.getHeight() + curvePane.getSize().getHeight() );
        ReactionGraphic reactionGraphic = new ReactionGraphic( mrModel.getReaction(),
                                                               MRConfig.ENERGY_PANE_TEXT_COLOR,
                                                               module.getMRModel() );
        legendNode.addChild( reactionGraphic );
        reactionGraphic.setOffset( legendNode.getWidth() / 2, legendNode.getHeight() - 20 );

        addChild( legendNode );
    }

    private void addMolecularSeparationPane( MRModule module, Dimension upperPaneSize ) {
        removeUpperPaneCloser();

        if (moleculeSeparationPane != null) {
            moleculeSeparationPane.terminate();
        }

        // The pane that has the molecules
        moleculeSeparationPane = new MoleculeSeparationPane( module, upperPaneSize, curvePane );

        addChild( moleculeSeparationPane );

        addUpperPaneCloser();
    }

    private void removeMolecularSeparationPane() {
         removeUpperPaneCloser();

        if (moleculeSeparationPane != null) {
            if (getChildrenReference().contains( moleculeSeparationPane )) {
                removeChild( moleculeSeparationPane );
            }
            
            moleculeSeparationPane.terminate();
            moleculeSeparationPane = null;
        }
    }

    private void addUpperPaneCloser() {
        if ( moleculeSeparationCloser == null) {
            moleculeSeparationCloser = new PNodeShowHideControl( moleculeSeparationPane, MRConfig.CONFIG.getString( "SeparationView.restoreViewName" ) );
        }
    }

    private void removeUpperPaneCloser() {
        if (moleculeSeparationCloser != null) {
            moleculeSeparationCloser.uninstall();
            moleculeSeparationCloser = null;
        }
    }

    private void addUpperPane( Dimension upperPaneSize ) {
        upperPane = new UpperEnergyPane(upperPaneSize);

        addChild( upperPane );
    }

    private void addCurvePane( MRModule module, Dimension upperPaneSize ) {
        disableCurvePaneCloser();

        if (curvePane != null) {
            curvePane.terminate();
        }

        // The pane that has the curve and cursor
        curvePane = new CurvePane(module, upperPaneSize);

        addChild( curvePane );

        enableCurvePaneCloser();
    }

    private void enableCurvePaneCloser() {
        if (curvePaneCloser == null) {
            curvePaneCloser = new PNodeShowHideControl( curvePane, MRConfig.CONFIG.getString( "EnergyView.restoreViewName" ) );
        }
    }

    private void disableCurvePaneCloser() {
        if (curvePaneCloser != null) {
            curvePaneCloser.uninstall();
            curvePaneCloser = null;
        }
    }

    public void reset() {
        if (!isInitialized()) throw new InternalError();

        initialize( module,  getUpperPaneSize() );
    }

    /*
     *
     */
    public Dimension getUpperPaneSize() {
        return upperPane.getSize();
    }

    /*
     * Adds a pNode to the upper pane
     *
     * @param pNode
     */
    public void setUpperPaneContent( PNode pNode ) {
        clearUpperPaneContent();

        upperPane.addChild( pNode );
        upperPaneContent = pNode;

        upperPaneContent.setVisible ( true );

        upperPane.setVisible( true );
    }

    public void clearUpperPaneContent() {
        clearUpperPaneContent( getUpperPaneContent() );
    }

    /*
     * Removes a pNode from the upper pane
     *
     * @param pNode
     */
    public void clearUpperPaneContent( PNode node ) {
        removeMolecularSeparationPane();
        
        if( node != null && upperPane.getChildrenReference().contains( node ) ) {
            upperPane.removeChild( upperPaneContent );

            upperPane.setVisible( false );

            upperPaneContent = null;
        }
    }

    public void setSeparationViewVisible( boolean visible ) {
        if ( visible ) {
            moleculeSeparationCloser.show();
        }
        else {
            moleculeSeparationCloser.hide();
        }
    }

    public void setEnergyViewVisible( boolean visible ) {
        if ( visible ) {
            curvePaneCloser.show();
        }
        else {
            curvePaneCloser.hide();
        }
    }

    public void setEnergyLineLabel( String propertyName ) {
        curvePane.setEnergyLineLabel( propertyName );
    }
    
    public void setProfileManipulable( boolean manipulable ) {
        curvePane.setProfileManipulable( manipulable );
    }

    public PNode getUpperPaneContent() {
        return upperPaneContent;
    }
}
