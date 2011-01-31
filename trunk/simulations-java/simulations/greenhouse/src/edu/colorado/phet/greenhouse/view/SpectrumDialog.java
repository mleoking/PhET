/* Copyright 2008, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;


/**
 * This class defines a dialog window that shows a representation of the
 * electromagnetic spectrum.
 *
 */
public class SpectrumDialog extends PaintImmediateDialog {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public SpectrumDialog( Frame parentFrame ) {
        super( parentFrame, true );

        // Don't let the user resize this window.
        setResizable( false );

        // Create the panel that will contain the canvas and the "Close" button.
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) ); // top, left, bottom, right
        mainPanel.setLayout( new BoxLayout( mainPanel, BoxLayout.Y_AXIS ) );

        // Create the canvas and add it to the panel.
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setBackground( new Color( 233, 236, 174 ) );
        canvas.setPreferredSize( SpectrumDiagram.OVERALL_DIMENSIONS );
        canvas.setBorder( BorderFactory.createEtchedBorder() ); // top, left, bottom, right
        mainPanel.add( canvas );

        // Create the spectrum diagram on the canvas.
        canvas.addWorldChild( new SpectrumDiagram() );

        // Add an invisible panel that will create space between the diagram
        // and the close button.
        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize( new Dimension( 1, 15) );
        mainPanel.add( spacerPanel );

        // Add the close button.
        // TODO: i18n
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                SpectrumDialog.this.dispose();
            }
        });
        closeButton.setAlignmentX( Component.CENTER_ALIGNMENT );
        mainPanel.add( closeButton );

        // Add to the dialog
        setContentPane( mainPanel );
        pack();
    }

    /**
     * Class that contains the diagram of the EM spectrum.  This is done as a
     * PNode in order to be translatable.
     */
    private static class SpectrumDiagram extends PNode {

        private static final Dimension OVERALL_DIMENSIONS = new Dimension( 600, 400 );
        private static final double HORIZONTAL_INSET = 20;
        private static final double VERTICAL_INSET = 20;

        public SpectrumDiagram(){
            addChild( new PhetPPath(new Rectangle2D.Double(0, 0, 500, 200), Color.PINK) );
            // TODO: i18n
            addChild( new LabeledArrow( OVERALL_DIMENSIONS.getWidth() - HORIZONTAL_INSET * 2,
                    LabeledArrow.Orientation.POINTING_LEFT, "Yadda", Color.PINK ));
        }
    }

    private static class LabeledArrow extends PNode {
        private static double ARROW_HEAD_HEIGHT = 40;
        private static double ARROW_HEAD_WIDTH = 40;
        private static double ARROW_TAIL_WIDTH = 20;
        private static Font LABEL_FONT = new PhetFont( 20 );
        private static Stroke STROKE = new BasicStroke( 3 );

        public enum Orientation { POINTING_LEFT, POINTING_RIGHT };

        public LabeledArrow ( double length, Orientation orientation, String captionText, Paint arrowBackgroundPaint ){

            final ArrowNode arrowNode = new ArrowNode( new Point2D.Double(0, 0), new Point2D.Double(length, 0),
                    ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
            arrowNode.setPaint( arrowBackgroundPaint );
            arrowNode.setStroke( STROKE );
            arrowNode.setOffset( 0, ARROW_HEAD_HEIGHT / 2 );
            addChild( arrowNode );
        }
    }
}
