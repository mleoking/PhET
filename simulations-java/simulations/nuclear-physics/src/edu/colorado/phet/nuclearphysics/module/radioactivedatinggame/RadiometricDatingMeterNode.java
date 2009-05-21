/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.module.radioactivedatinggame.ProbeTypeModel.ProbeType;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class represents a node that the user can interact with in order to
 * date various items that are available in the play area.
 * 
 * @author John Blanco
 */
public class RadiometricDatingMeterNode extends PNode {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	private static final Color BODY_COLOR = Color.DARK_GRAY;
	private static final double READOUT_WIDTH_PROPORTION = 0.75;
	private static final double READOUT_HEIGHT_PROPORTION = 0.2;
	private static final double PROBE_SIZE_SCALE_FACTOR = 0.75;  // Adjust in order to change size of probe.

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	RadiometricDatingMeter _meterModel;
	ModelViewTransform2D _mvt;
	PNode _meterBody;
	private ProbeNode _probeNode;
	private PercentageDisplayNode _percentageDisplay;
	private ElementSelectionPanel _elementSelectionPanel;
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

	public RadiometricDatingMeterNode(RadiometricDatingMeter meterModel, ProbeTypeModel probeTypeModel, double width, double height, ModelViewTransform2D mvt) {
		
		_meterModel = meterModel;
		_mvt = mvt;
		
		// Register with the model to find out when something new is being touched.
		_meterModel.addListener(new RadiometricDatingMeter.Listener(){
			public void touchedItemChanged() {
				updateMeterReading();
			}
		});
		
		_meterBody = new PNode();
		addChild(_meterBody);
		
		// Create the main body of the meter.
		PPath mainBody = new PPath(new RoundRectangle2D.Double(0, 0, width, height, width/5, width/5));
		mainBody.setPaint(BODY_COLOR);
		_meterBody.addChild(mainBody);
		
		// Add the display.
		_percentageDisplay = new PercentageDisplayNode(width * READOUT_WIDTH_PROPORTION,
				height * READOUT_HEIGHT_PROPORTION);
		_percentageDisplay.setOffset(mainBody.getFullBounds().width / 2 - _percentageDisplay.getFullBounds().width / 2,
				mainBody.getHeight() * 0.1 );
		_meterBody.addChild(_percentageDisplay);
		_percentageDisplay.setPercentage(100);
		
		// Add the selection panel.
		_elementSelectionPanel = new ElementSelectionPanel((int)Math.round(width * 0.9), (int)Math.round(height * 0.66), probeTypeModel);
		PSwing elementSelectionNode = new PSwing(_elementSelectionPanel);
		elementSelectionNode.setOffset( 
				_meterBody.getFullBounds().width / 2 - elementSelectionNode.getFullBounds().width / 2,
				_percentageDisplay.getFullBounds().getMaxY());
		_meterBody.addChild(elementSelectionNode);
		
		// Add the probe.
		_probeNode = new ProbeNode( _meterModel.getProbeModel(), _mvt );
		addChild(_probeNode);
		
		// Create the cable that visually attaches the probe to the meter.
		addChild(new ProbeCableNode(this));

		// Set the initial meter reading.
		updateMeterReading();
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

	public Point2D getProbeTailLocation(){
		return (_meterBody.localToParent(_probeNode.getTailLocation()));
	}
	
	private ProbeNode getProbeNode() {
		return _probeNode;
	}
	
	private void updateMeterReading(){
		DatableObject datableItem = _meterModel.getItemBeingTouched();
		if (datableItem == null){
			_percentageDisplay.setBlank();
		}
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
    		
    		scaleAndCenterText();
    	}
    	
    	/**
    	 * Set the display to indicate that the meter is not probing anything.
    	 */
    	public void setBlank(){
    		_percentageText.setText(" --- ");
    		scaleAndCenterText();
    	}
    	
    	private void scaleAndCenterText(){
    		_percentageText.setScale(1);
    		_percentageText.setScale(_backgroundShape.getWidth() * 0.9 / _percentageText.getFullBoundsReference().width);
    		_percentageText.setOffset(
    			_background.getFullBoundsReference().width / 2 - _percentageText.getFullBoundsReference().width / 2,
    			_background.getFullBoundsReference().height / 2 - _percentageText.getFullBoundsReference().height / 2);
    	}
    }
    
    static class ProbeNode extends PhetPNode {
        private RadiometricDatingMeter.ProbeModel _probeModel;
        private PImage imageNode;
        private PhetPPath tipPath;
        private ModelViewTransform2D _mvt;

        public ProbeNode( RadiometricDatingMeter.ProbeModel probeModel, ModelViewTransform2D mvt ) {
            _probeModel = probeModel;
            _mvt = mvt;

            imageNode = NuclearPhysicsResources.getImageNode( "probeBlack.gif" );
            imageNode.rotateAboutPoint( probeModel.getAngle(), 0.1, 0.1 );
            imageNode.scale( PROBE_SIZE_SCALE_FACTOR );
            addChild( imageNode );
            probeModel.addListener( new RadiometricDatingMeter.ProbeModel.Listener() {
                public void probeModelChanged() {
                    updateProbe();
                }
            } );

            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    PDimension pt = event.getDeltaRelativeTo( ProbeNode.this.getParent() );
                    _probeModel.translate( _mvt.viewToModelDifferentialX(pt.width),
                    		_mvt.viewToModelDifferentialY(pt.height) );
                }
            } );
            addInputEventListener( new CursorHandler() );

            tipPath = new PhetPPath( Color.lightGray );
            addChild( tipPath );

            updateProbe();
        }

        private void updateProbe() {
            double dx = imageNode.getImage().getWidth( null ) * PROBE_SIZE_SCALE_FACTOR * Math.cos( _probeModel.getAngle() ) / 2;
            double dy = imageNode.getImage().getWidth( null ) * PROBE_SIZE_SCALE_FACTOR * Math.sin( _probeModel.getAngle() ) / 2;
            imageNode.setOffset( _mvt.modelToViewXDouble(_probeModel.getTipLocation().getX()) - dx, 
            		_mvt.modelToViewYDouble(_probeModel.getTipLocation().getY()) - dy);

            tipPath.setPathTo( _probeModel.getTipShape() );
        }

        /**
         * Get the location of the tail of the probe.  Note that this WILL NOT
         * WORK for all orientations of the probe, so if a more general
         * implementation is needed, feel free to expand on this.
         * 
         * @return
         */
        public Point2D getTailLocation() {
            Point2D pt = new Point2D.Double( imageNode.getWidth() / 2, imageNode.getHeight() );
            imageNode.localToParent( pt );
            localToParent( pt );
            return new Point2D.Double( pt.getX(), pt.getY() );
        }
    }

	public void setMeterBodyOffset(double x, double y) {
		_meterBody.setOffset(x, y);
	}
	
	private PNode getMeterBodyNode() {
		return _meterBody;
	}
	
	public double getMeterBodyWidth(){
		return _meterBody.getFullBounds().width;
	}

    //------------------------------------------------------------------------
    // Inner Classes
    //------------------------------------------------------------------------
	
	/**
	 * This class represents a node that visually depicts the cable or cord that
	 * connects the body of the meter to the probe.  This is highly leveraged
	 * from a class that was found in the Wave Interference sim.
	 */
	private static class ProbeCableNode extends PhetPNode {
	    private final ProbeNode _probeNode;
	    private final PNode _meterBodyNode;
	    private PPath _cable;

	    public ProbeCableNode( RadiometricDatingMeterNode meterNode ) {
	        _probeNode = meterNode.getProbeNode();
	        _meterBodyNode = meterNode.getMeterBodyNode();
	        
	        // Create the path that represents the cord.
	        _cable = new PPath();
	        _cable.setStroke( new BasicStroke( 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
	        addChild( _cable );
	        
	        // Register for changes in location or size of the two nodes that
	        // the cable connects together.
	        _probeNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
	            public void propertyChange( PropertyChangeEvent evt ) {
	                update();
	            }
	        } );
	        _meterBodyNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
	            public void propertyChange( PropertyChangeEvent evt ) {
	                update();
	            }
	        } );
	        update();
	    }

	    private void update() {
	        Point2D dstLoc = new Point2D.Double( _probeNode.getTailLocation().getX(), 
	        		_probeNode.getTailLocation().getY() );
	        Point2D srcLoc = new Point2D.Double(_meterBodyNode.getFullBoundsReference().getCenterX(),
	        		_meterBodyNode.getFullBoundsReference().getMinY());
	        double dist = srcLoc.distance( dstLoc );
	        if ( dist > 0 ) {
	            DoubleGeneralPath path = new DoubleGeneralPath( srcLoc );
	            AbstractVector2D parallel = new Vector2D.Double( srcLoc, dstLoc ).getNormalizedInstance();
	            AbstractVector2D perp = parallel.getRotatedInstance( Math.PI / 2 ).getNormalizedInstance();
	            lineToDst( path, parallel, perp, dist / 5 );
	            curveToDst( path, parallel, perp, dist / 5 );
	            lineToDst( path, parallel, perp, dist / 5 );
	            curveToDst( path, parallel, perp, dist / 5 );
	            path.lineTo( dstLoc );
	            _cable.setPathTo( path.getGeneralPath() );
	        }
	        else {
	        	_cable.setPathTo( new Rectangle() );
	        }
	    }

	    private void curveToDst( DoubleGeneralPath path, AbstractVector2D par, AbstractVector2D perp, double segmentLength ) {
	        double pegDist = segmentLength;
	        if ( pegDist < 7 ) {
	            pegDist = 7;
	        }
	        if ( pegDist > 20 ) {
	            pegDist = 20;
	        }
	        double width = 15;
	        Point2D peg1 = path.getCurrentPoint();
	        Point2D peg2 = par.getInstanceOfMagnitude( pegDist ).getDestination( peg1 );
	        Point2D dst = par.getInstanceOfMagnitude( pegDist / 2 ).getAddedInstance( perp.getInstanceOfMagnitude( width ) ).getDestination( peg1 );
	        path.quadTo( peg2.getX(), peg2.getY(), dst.getX(), dst.getY() );
	        path.quadTo( peg1.getX(), peg1.getY(), peg2.getX(), peg2.getY() );
	    }

	    private void lineToDst( DoubleGeneralPath path, AbstractVector2D a, AbstractVector2D b, double segmentLength ) {
	        path.lineToRelative( a.getInstanceOfMagnitude( segmentLength ) );
	    }
	}
	
	private static class ElementSelectionPanel extends VerticalLayoutPanel {
		
		public ElementSelectionPanel(int width, int height, final ProbeTypeModel probeTypeModel){
			
			setPreferredSize(new Dimension(width, height));
			setBackground(BODY_COLOR);
			
			// TODO - JPB TBD: Need to make strings into resources once finalized.
			
            // Add the border around the panel.
            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                    "Probe Type",
                    TitledBorder.CENTER,
                    TitledBorder.TOP,
                    new PhetFont( Font.BOLD, 18 ),
                    Color.WHITE );
            
            setBorder( titledBorder );
            
            // Create the radio buttons, one for each possible probe type.
            for (final ProbeType probeType : ProbeTypeModel.POSSIBLE_PROBE_TYPES){
                final JRadioButton radioButton = new JRadioButton(probeType.getName(), 
                		probeTypeModel.getProbeType().equals(probeType));
                radioButton.setBackground(BODY_COLOR);
                radioButton.setForeground(Color.WHITE);
                radioButton.setFont(new PhetFont(18));
                radioButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						probeTypeModel.setProbeType(probeType);
					}
                });
                add(radioButton);
                probeTypeModel.addObserver(new SimpleObserver(){
					public void update() {
		                radioButton.setSelected( probeTypeModel.getProbeType().equals(probeType));
					}
                });
            }
		}		
	}
}
