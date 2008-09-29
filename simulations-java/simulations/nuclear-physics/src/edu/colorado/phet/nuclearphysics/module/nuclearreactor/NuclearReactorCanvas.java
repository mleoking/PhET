/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.nuclearreactor;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.dialog.ReactorPictureDialog;
import edu.colorado.phet.nuclearphysics.view.NuclearReactorNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the canvas upon which the view of the model is
 * displayed for the nuclear reactor tab of this simulation.
 *
 * @author John Blanco
 */
public class NuclearReactorCanvas extends PhetPCanvas{
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Canvas dimensions.
    private final double CANVAS_WIDTH = 700;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * 0.87;

    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 2.0;
    private final double HEIGHT_TRANSLATION_FACTOR = 2.5;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private NuclearReactorModel _nuclearReactorModel;
    private NuclearReactorNode  _nuclearReactorNode;
    private GradientButtonNode _fireNeutronsButtonNode;
    private GradientButtonNode _showReactorImageButtonNode;
    private Frame _parentFrame;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public NuclearReactorCanvas(NuclearReactorModel nuclearReactorModel, Frame parentFrame) {

        _nuclearReactorModel = nuclearReactorModel;
        _parentFrame = parentFrame;
        
        // Set the transform strategy in such a way that the center of the
        // visible canvas will be at 0,0.
        setWorldTransformStrategy( new RenderingSizeStrategy(this, 
                new PDimension(CANVAS_WIDTH, CANVAS_HEIGHT) ){
            protected AffineTransform getPreprocessedTransform(){
                return AffineTransform.getTranslateInstance( getWidth()/WIDTH_TRANSLATION_FACTOR, 
                        getHeight()/HEIGHT_TRANSLATION_FACTOR );
            }
        });
        
        // Set the background color.
        setBackground( NuclearPhysicsConstants.CANVAS_BACKGROUND );
        
        // Add the reactor node to the canvas.
        _nuclearReactorNode = new NuclearReactorNode(_nuclearReactorModel, this);
        addWorldChild( _nuclearReactorNode );
        
        // Add the button for firing neutrons into the reactor.
        _fireNeutronsButtonNode = new GradientButtonNode(NuclearPhysicsStrings.FIRE_NEUTRONS_BUTTON_LABEL, 16, 
                new Color(0xff9900));
        addWorldChild( _fireNeutronsButtonNode );
        _fireNeutronsButtonNode.setOffset( _nuclearReactorNode.getFullBounds().getCenterX() - 
                _fireNeutronsButtonNode.getFullBounds().width / 2, _nuclearReactorNode.getFullBounds().getMinY() -
                _fireNeutronsButtonNode.getFullBounds().height);
        
        _fireNeutronsButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent event){
                _nuclearReactorModel.fireNeutrons();
            }
        });
        
        // Add the button for showing the reactor photo.
        _showReactorImageButtonNode = new GradientButtonNode(NuclearPhysicsStrings.SHOW_REACTOR_IMAGE, 16, new Color(0x88ff00));
        addWorldChild(_showReactorImageButtonNode);
        _showReactorImageButtonNode.setOffset( -(CANVAS_WIDTH/2), CANVAS_HEIGHT / 2 );
        
        _showReactorImageButtonNode.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                // Show the image dialog.
                ReactorPictureDialog reactorCorePictureDlg = new ReactorPictureDialog( _parentFrame );
                if ( reactorCorePictureDlg != null ) {
                    SwingUtils.centerDialogInParent( reactorCorePictureDlg );
                }
                reactorCorePictureDlg.setVisible( true );
            }
        });
    }

    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------

}
