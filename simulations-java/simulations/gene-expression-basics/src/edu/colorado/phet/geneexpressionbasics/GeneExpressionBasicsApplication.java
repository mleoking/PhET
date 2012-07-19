// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import edu.colorado.phet.common.phetcommon.application.ModuleEvent;
import edu.colorado.phet.common.phetcommon.application.ModuleObserver;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.menu.TeacherMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.ManualGeneExpressionModule;
import edu.colorado.phet.geneexpressionbasics.mrnaproduction.MessengerRnaProductionModule;
import edu.colorado.phet.geneexpressionbasics.multiplecells.MultipleCellsModule;

import static edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsResources.Strings.*;

/**
 * Main application class for this simulation.
 *
 * @author John Blanco
 */
public class GeneExpressionBasicsApplication extends PiccoloPhetApplication {

    // Project name.
    private static final String NAME = "gene-expression-basics";

    // Modules that need to be kept around for reference.
    private final ManualGeneExpressionModule manualGeneExpressionModule = new ManualGeneExpressionModule( TAB__CELL_GENE_EXPRESSION );

    /**
     * Constructor.
     */
    public GeneExpressionBasicsApplication( PhetApplicationConfig config ) {
        super( config );

        // module(s)
        addModule( manualGeneExpressionModule );
        addModule( new MessengerRnaProductionModule( TAB__MESSENGER_RNA_PRODUCTION ) );
        addModule( new MultipleCellsModule( TAB__MULTIPLE_CELLS, getPhetFrame() ) );

        initMenuBar();
    }

    /**
     * Initializes the menu bar.
     */
    protected void initMenuBar() {

        // Create the teacher menu that will contain the zoom in/out buttons.
        final TeacherMenu teacherMenu = new TeacherMenu();

        // Create menu items that can be used to control the zoom in/out state
        // of the manual gene expression canvas.
        final JMenuItem zoomIn = new JMenuItem( GeneExpressionBasicsResources.Strings.ZOOM_IN ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    manualGeneExpressionModule.setCanvasZoomedIn( true );
                }
            } );
        }};
        final JMenuItem zoomOut = new JMenuItem( GeneExpressionBasicsResources.Strings.ZOOM_OUT ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    manualGeneExpressionModule.setCanvasZoomedIn( false );
                }
            } );
        }};
        manualGeneExpressionModule.getCanvasZoomedInProperty().addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean zoomedIn ) {
                if ( zoomedIn ) {
                    teacherMenu.remove( zoomIn );
                    teacherMenu.add( zoomOut );
                }
                else {
                    teacherMenu.add( zoomIn );
                    teacherMenu.remove( zoomOut );
                }
            }
        } );

        // Only display this teacher menu for the manual gene expression tab,
        // since it is irrelevant to the other tabs.
        addModuleObserver( new ModuleObserverAdapter() {
            @Override public void activeModuleChanged( ModuleEvent event ) {
                if ( event.getModule() == manualGeneExpressionModule ) {
                    getPhetFrame().addMenu( teacherMenu );
                }
                else {
                    getPhetFrame().removeMenu( teacherMenu );
                }
            }
        } );

        // Developer menu items go here if needed.
    }

    private static class ModuleObserverAdapter implements ModuleObserver {
        public void moduleAdded( ModuleEvent event ) {}

        public void activeModuleChanged( ModuleEvent event ) {}

        public void moduleRemoved( ModuleEvent event ) {}
    }

    /**
     * Main entry point for this simulation.
     */
    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, NAME, GeneExpressionBasicsApplication.class );
    }
}
