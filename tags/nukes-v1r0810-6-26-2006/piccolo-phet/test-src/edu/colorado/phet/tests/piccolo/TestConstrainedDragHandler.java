/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.tests.piccolo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.event.ConstrainedDragHandler;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * TestConstrainedDragHandler is a test harness for ConstrainedDragHandler.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestConstrainedDragHandler extends JFrame {

    private static final Dimension FRAME_SIZE = new Dimension( 650, 400 );
    
    private ConstrainedDragHandler _dragHandler;
    
    private JRadioButton _treatAsPointRadioButton;
    private JRadioButton _treatAsRectangeRadioButton;
    private JCheckBox _verticalLockCheckBox;
    private JCheckBox _horizontalLockCheckBox;
    
    public TestConstrainedDragHandler() {
        super( "Test ConstrainedDragHandler" );
        
        PhetPCanvas playArea = createPlayAreaPanel();
        JPanel controlPanel = createControlPanel();
        
        JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout() );
        panel.add( playArea, BorderLayout.CENTER );
        panel.add( controlPanel, BorderLayout.EAST );
        
        getContentPane().add( panel );
    }
    
    /*
     * Creates the "play area" and the ConstrainedDragHandler.
     */
    private PhetPCanvas createPlayAreaPanel() {
        PhetPCanvas canvas = new PhetPCanvas( new Dimension( 500, 500 ) );
        
        PText instructionsNode = new PText( "Drag the red circle" );
        instructionsNode.setFont( new Font( "Lucida Sans", Font.BOLD, 24 ) );
        instructionsNode.setTextPaint( Color.RED );
        canvas.addScreenChild( instructionsNode );
        instructionsNode.setOffset( 50, 20 );
        
        PPath dragAreaNode = new PPath();
        dragAreaNode.setPaint( Color.BLACK );
        dragAreaNode.setPathTo( new Rectangle( 0, 0, 350, 300 ) );
        dragAreaNode.setPickable( false );
        canvas.addScreenChild( dragAreaNode );
        dragAreaNode.setOffset( 50, 50 );
        
        PPath dragObjectNode = new PPath();
        dragObjectNode.setPaint( Color.RED );
        dragObjectNode.setPathTo( new Ellipse2D.Double( 0, 0, 50, 50 ) );
        canvas.addScreenChild( dragObjectNode );
        dragObjectNode.setOffset( 150, 100 );
       
        dragObjectNode.addInputEventListener( new CursorHandler() );
        
        Rectangle2D dragBounds = dragAreaNode.localToGlobal( dragAreaNode.getBounds() );
        _dragHandler = new ConstrainedDragHandler( dragBounds );
        // When dragging as a point, use the center of the circle as the point.
        _dragHandler.setNodeCenter( dragObjectNode.getWidth()/2, dragObjectNode.getHeight()/2 );
        dragObjectNode.addInputEventListener( _dragHandler );
        
        return canvas;
    }
    
    /*
     * Create the control panel, used to change properties of the ConstrainedDragHandler.
     */
    private JPanel createControlPanel() {
        
        _treatAsPointRadioButton = new JRadioButton( "point" );
        _treatAsPointRadioButton.setSelected( _dragHandler.isTreatAsPointEnabled() );
        _treatAsPointRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _dragHandler.setTreatAsPointEnabled( _treatAsPointRadioButton.isSelected() );
            }
        });
        
        _treatAsRectangeRadioButton = new JRadioButton( "rectangle" );
        _treatAsRectangeRadioButton.setSelected( !_dragHandler.isTreatAsPointEnabled() );
        _treatAsRectangeRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _dragHandler.setTreatAsPointEnabled( _treatAsPointRadioButton.isSelected() );
            }
        });
        
        _verticalLockCheckBox = new JCheckBox( "vertical" );
        _verticalLockCheckBox.setSelected( _dragHandler.isVerticalLockEnabled() );
        _verticalLockCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _dragHandler.setVerticalLockEnabled( _verticalLockCheckBox.isSelected() );
            }
        });
        
        _horizontalLockCheckBox = new JCheckBox( "horizontal" );
        _horizontalLockCheckBox.setSelected( _dragHandler.isHorizontalLockEnabled() );
        _horizontalLockCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _dragHandler.setHorizontalLockEnabled( _horizontalLockCheckBox.isSelected() );
            }
        });
        
        ButtonGroup treatAsButtonGroup = new ButtonGroup();
        treatAsButtonGroup.add( _treatAsPointRadioButton );
        treatAsButtonGroup.add( _treatAsRectangeRadioButton );
        
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
        controlPanel.setLayout( new BoxLayout( controlPanel, BoxLayout.Y_AXIS ) );
        controlPanel.add( new JLabel( "Treat dragged object as a:" ) );
        controlPanel.add( _treatAsPointRadioButton );
        controlPanel.add( _treatAsRectangeRadioButton );
        controlPanel.add( Box.createVerticalStrut( 10 ) );
        controlPanel.add( new JLabel( "Lock position:" ) );
        controlPanel.add( _verticalLockCheckBox );
        controlPanel.add( _horizontalLockCheckBox );
        
        return controlPanel;
    }

    public static void main( String[] args ) {
        JFrame frame = new TestConstrainedDragHandler();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setSize(  FRAME_SIZE );
        frame.setResizable( false );
        frame.show();
    }
}
    
