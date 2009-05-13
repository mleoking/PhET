/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents a node that the user can interact with in order to
 * date various items that are available in the play area.
 * 
 * @author John Blanco
 */
public class RadiometricDatingMeterNode extends PNode {

	private static final Color BODY_COLOR = Color.DARK_GRAY;
	private static final double READOUT_WIDTH_PROPORTION = 0.75;
	private static final double READOUT_HEIGHT_PROPORTION = 0.2;
	private static final double PROBE_SIZE_SCALE_FACTOR = 0.75;  // Adjust in order to change size of probe.
	
	RadiometricDatingMeter _meterModel;
	
	public RadiometricDatingMeterNode(RadiometricDatingMeter meterModel, double width, double height) {
		
		_meterModel = meterModel;
		
		// Create the main body of the meter.
		PPath mainBody = new PPath(new RoundRectangle2D.Double(0, 0, width, height, width/5, width/5));
		mainBody.setPaint(BODY_COLOR);
		addChild(mainBody);
		
		// Create the percentage readout.
		PercentageDisplayNode percentageDisplay = new PercentageDisplayNode(width * READOUT_WIDTH_PROPORTION,
				height * READOUT_HEIGHT_PROPORTION);
		percentageDisplay.setOffset(mainBody.getFullBounds().width / 2 - percentageDisplay.getFullBounds().width / 2,
				mainBody.getHeight() * 0.1 );
		addChild(percentageDisplay);
		percentageDisplay.setPercentage(100);
		
		// Create the probe.
		ProbeNode probe = new ProbeNode( _meterModel.getProbeModel() );
		addChild(probe);
		probe.setOffset(0,0);
	}
	
    /**
     * Class that represents the percentage display readout.
     */
    private class PercentageDisplayNode extends PNode{
    	
    	private final Color BACKGROUND_COLOR = new Color(255, 255, 255);
        private final Font  TIME_FONT = new PhetFont( Font.BOLD, 26 );
        
    	private PPath _background;
    	private RoundRectangle2D _backgroundShape;
    	private double _currentPercentage;
    	private PText _percentageText;
        private DecimalFormat _percentageFormatterOneDecimal = new DecimalFormat( "##0.0" );
    	
        PercentageDisplayNode(double width, double height){
    		_backgroundShape = new RoundRectangle2D.Double(0, 0, width, height, 8, 8);
    		_background = new PPath(_backgroundShape);
    		_background.setPaint(BACKGROUND_COLOR);
    		addChild(_background);
    		_percentageText = new PText();
    		_percentageText.setFont(TIME_FONT);
    		addChild(_percentageText);
    	}
    	
    	public void setPercentage(double percentage){

    		_currentPercentage = percentage;
    		
    		_percentageText.setText(_percentageFormatterOneDecimal.format(_currentPercentage) + " %");
    		
    		_percentageText.setScale(1);
    		_percentageText.setScale(_backgroundShape.getWidth() * 0.9 / _percentageText.getFullBoundsReference().width);
    		_percentageText.setOffset(
    			_background.getFullBoundsReference().width / 2 - _percentageText.getFullBoundsReference().width / 2,
    			_background.getFullBoundsReference().height / 2 - _percentageText.getFullBoundsReference().height / 2);
    	}
    }
    
    private class ProbeNode extends PhetPNode {
        private RadiometricDatingMeter.ProbeModel _probeModel;
        private PImage imageNode;
        private PhetPPath tipPath;

        public ProbeNode( RadiometricDatingMeter.ProbeModel probeModel ) {
            _probeModel = probeModel;

            imageNode = NuclearPhysicsResources.getImageNode( "probeBlack.gif" );
            imageNode.rotateAboutPoint( probeModel.getAngle(), 0.1, 0.1 );
            imageNode.scale( PROBE_SIZE_SCALE_FACTOR );
            addChild( imageNode );
            probeModel.addListener( new RadiometricDatingMeter.ProbeModel.Listener() {
                public void probeModelChanged() {
                    updateLead();
                }
            } );

            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    PDimension pt = event.getDeltaRelativeTo( ProbeNode.this.getParent() );
                    _probeModel.translate( pt.width, pt.height );
                }
            } );
            addInputEventListener( new CursorHandler() );

            tipPath = new PhetPPath( Color.lightGray );
            addChild( tipPath );

            updateLead();
        }

        private void updateLead() {
            double dx = imageNode.getImage().getWidth( null ) * PROBE_SIZE_SCALE_FACTOR * Math.cos( _probeModel.getAngle() ) / 2;
            double dy = imageNode.getImage().getWidth( null ) * PROBE_SIZE_SCALE_FACTOR * Math.sin( _probeModel.getAngle() ) / 2;
            imageNode.setOffset( _probeModel.getTipLocation().getX() - dx, _probeModel.getTipLocation().getY() - dy );

            tipPath.setPathTo( _probeModel.getTipShape() );
        }

        public Point2D getTailLocation() {
            Point2D pt = new Point2D.Double( imageNode.getWidth() / 2, imageNode.getHeight() );
            imageNode.localToParent( pt );
            localToParent( pt );
            return new Point2D.Double( pt.getX(), pt.getY() );
        }
    }
}
