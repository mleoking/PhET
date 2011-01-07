// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.membranechannels.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.membranechannels.MembraneChannelsResources;
import edu.colorado.phet.membranechannels.model.InjectionMotionStrategy;
import edu.colorado.phet.membranechannels.model.MembraneChannelsModel;
import edu.colorado.phet.membranechannels.model.Particle;
import edu.colorado.phet.membranechannels.model.ParticleType;
import edu.colorado.phet.membranechannels.model.PotassiumIon;
import edu.colorado.phet.membranechannels.model.SodiumIon;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Node that represents the thing with which the user interacts in order to
 * inject particles (generally ions) into the chamber.
 * 
 * @author John Blanco
 */
public class ParticleInjectorNode extends PNode {

	//------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	// Height of the injector prior to rotation.  This is in world size, which
	// is close to pixels (but not quite exactly due to all that transform
	// craziness).
	private static final double INJECTOR_HEIGHT = 130;
	
	// Offset of button within this node.  This was determined by trial and
	// error and will need to be tweaked if the images change.
	private static final Point2D BUTTON_OFFSET = new Point2D.Double(-100, -65);
	
	// Velocity at which particles are injected in to the model.
	private static double NOMINAL_ION_INJECTION_VELOCITY = 30;
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	private final PNode injectorBodyImageNode;
	private final PImage buttonImageNode;
	private final BufferedImage unpressedButtonImage;
	private final BufferedImage pressedButtonImage;
	private final PNode particleTypeSelector;
	private final MembraneChannelsModel model;
	private final ModelViewTransform2D mvt;
	private ParticleType particleTypeToInject;
	private Dimension2D injectionPointOffset = new PDimension();
	private Point2D injectionPointInModelCoords = new Point2D.Double();
	private Vector2D nominalInjectionVelocityVector = new Vector2D(NOMINAL_ION_INJECTION_VELOCITY, 0);
    
    // Count is incremented every time the simulation clock ticks, so that we can inject particles every ITERATIONS_BETWEEN_INJECTION steps 
    private int count;
    
    // Time the user has been holding down the injection button, or Long.MAX_VALUE if the button is not currently depressed 
    private long buttonPressedTime=Long.MAX_VALUE;
    
    // The time in milliseconds that the user must hold down the button before particles are continuously autoinjected
    private static final long TIME_BEFORE_AUTOINJECT = 500;
    
    // The number of iterations between each injected particle, see 'count' above. 
    private static final int ITERATIONS_BETWEEN_INJECTION = 7;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    /**
     * Constructs a particle injection node.
     * @param initialParticleType - Particle type to inject until and unless changed by user.
     * @param model - The model into which the particles will be injected.
     * @param mvt - Model-view transform for relating view space to model space.
     * @param rotationAngle - Angle of rotation for the injection bulb.
     */
	public ParticleInjectorNode(ParticleType initialParticleType, final MembraneChannelsModel model,
			ModelViewTransform2D mvt, double rotationAngle) {
		
		this.particleTypeToInject = initialParticleType;
		this.model = model;
		this.mvt = mvt;

        //When the model steps in time, inject a particle if the user has been holding down the button for long enough.
        model.getClock().addClockListener( new ClockAdapter() {
            @Override
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                count++;
                if ( buttonPressedTime != Long.MAX_VALUE && ( System.currentTimeMillis() - buttonPressedTime ) > TIME_BEFORE_AUTOINJECT && count % ITERATIONS_BETWEEN_INJECTION == 0 ) {
                    injectParticle();
                }
            }
        } );
		
		nominalInjectionVelocityVector.rotate(rotationAngle);

		// Create the base node to which the various constituent parts can be
		// added.
		PNode injectorNode = new PNode();
		
		// Load the graphic images for this device.  These are offset in order
		// to make the center of rotation be the center of the bulb.
		BufferedImage injectorBodyImage = MembraneChannelsResources.getImage( "squeezer_background.png" );
        injectorBodyImageNode = new PImage( injectorBodyImage );
        Rectangle2D originalBodyBounds = injectorBodyImageNode.getFullBounds();
        injectorBodyImageNode.setOffset(-originalBodyBounds.getWidth() / 2, -originalBodyBounds.getHeight() / 2);
        injectorNode.addChild(injectorBodyImageNode);
        pressedButtonImage = MembraneChannelsResources.getImage( "button_pressed.png" );
        unpressedButtonImage = MembraneChannelsResources.getImage( "button_unpressed.png" );
        buttonImageNode = new PImage(unpressedButtonImage);
        buttonImageNode.setOffset(BUTTON_OFFSET);
        injectorNode.addChild(buttonImageNode);
        
        // Rotate and scale the image node as a whole.
        double scale = INJECTOR_HEIGHT / injectorBodyImageNode.getFullBoundsReference().height;
        injectorNode.rotate(-rotationAngle);
        injectorNode.scale(scale);
        
        // Add the node that allows control of the type of particle to inject.
        particleTypeSelector = new ParticleTypeSelectorNode(INJECTOR_HEIGHT * 0.6, initialParticleType, this);
        particleTypeSelector.setOffset(
        	injectorNode.getFullBoundsReference().getMinX() - particleTypeSelector.getFullBoundsReference().width / 2 - 5,
        	injectorNode.getFullBoundsReference().getCenterY() - particleTypeSelector.getFullBoundsReference().height / 2);
        addChild(particleTypeSelector);
        
        // Add the injector image node.  Note that the position has to be
        // tweaked in order to account for the rotation of the node image,
        // since the rotation of the square image enlarges the bounds.
        injectorNode.setOffset(-Math.abs(Math.sin(rotationAngle * 2)) * 30, 0);
        addChild(injectorNode);
        
        // Set up the injection point offset. This makes some assumptions
        // about the nature of the image, and will need to be updated if the
        // image is changed.
        double distanceCenterToTip = 0.7 * INJECTOR_HEIGHT;
        double centerOffsetX = 0.4 * INJECTOR_HEIGHT;
        injectionPointOffset.setSize(distanceCenterToTip * Math.cos(rotationAngle) + centerOffsetX,
        		distanceCenterToTip * Math.sin(-rotationAngle));
        
        // Set the point for automatic injection to be at the tip of the
        // injector.
        updateInjectionPoint();
        
        // Set up the button handling.
        injectorBodyImageNode.setPickable(false);
        buttonImageNode.setPickable(true);
        buttonImageNode.addInputEventListener(new MyCursorHandler());
        buttonImageNode.addInputEventListener(new PBasicInputEventHandler(){
        	@Override
            public void mousePressed( PInputEvent event ) {
                buttonPressedTime = System.currentTimeMillis();
        	    // Only inject another particle if the model can support it.
                if (model.getRemainingParticleCapacity() > 0 ){
                    buttonImageNode.setImage(pressedButtonImage);
                    injectParticle();
                }
            }
        	
        	@Override
            public void mouseReleased( PInputEvent event ) {
        	    buttonImageNode.setImage(unpressedButtonImage);
                buttonPressedTime = Long.MAX_VALUE;
            }
        });
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    private void injectParticle(){
		Particle particleToInject = Particle.createParticle(particleTypeToInject);
		particleToInject.setPosition(injectionPointInModelCoords);
		Rectangle2D particleMotionBounds;
		if (model.getUpperParticleChamberRect().contains( injectionPointInModelCoords )){
		    particleMotionBounds = model.getUpperParticleChamberRect();
		}
		else if (model.getLowerParticleChamberRect().contains( injectionPointInModelCoords )){
		    particleMotionBounds = model.getLowerParticleChamberRect();
		}
		else{
		    // Should never get here, debug it if it does.
		    System.err.println();
		    assert false;
		    System.err.println( getClass().getName() + " - Error: Particle not in reasonable location." );
            particleMotionBounds = model.getLowerParticleChamberRect();
		}
		
		particleToInject.setMotionStrategy(new InjectionMotionStrategy(injectionPointInModelCoords, particleMotionBounds, 0));
		model.injectParticle(particleToInject);
	}
	
	private void updateInjectionPoint(){
		double injectionPointX = mvt.viewToModelX(getFullBoundsReference().getCenter2D().getX() + injectionPointOffset.getWidth());
		double injectionPointY = mvt.viewToModelY(getFullBoundsReference().getCenter2D().getY() + injectionPointOffset.getHeight());
		injectionPointInModelCoords.setLocation(injectionPointX, injectionPointY);
	}
	
	protected void setParticleTypeToInject(ParticleType particleType){
	    if (this.particleTypeToInject != particleType){
	        this.particleTypeToInject = particleType;
	    }
	}
	
	@Override
	public void setOffset(double x, double y) {
		super.setOffset(x, y);
		updateInjectionPoint();
	}

	/**
	 * Class that defines the box that sits off to the side of the injector
	 * and that controls what type of particle is injected.
	 *
	 */
	private static class ParticleTypeSelectorNode extends PNode {
		
		private static final Color BACKGROUND_COLOR = new Color(234, 228, 77);
		private static final Stroke BORDER_STROKE = new BasicStroke(1f);
		private static final Color BORDER_COLOR = BACKGROUND_COLOR;
		private static final ModelViewTransform2D PARTICLE_MVT = new ModelViewTransform2D(
				new Rectangle2D.Double(-1.0, -1.0, 2.0, 2.0), new Rectangle2D.Double(-10, -10, 20, 20));
		
		private final ParticleInjectorNode particleInjectorNode;
        private final JRadioButton sodiumButton;
        private final JRadioButton potassiumButton;
		
		/**
		 * Constructor.  The height is specified, and the width is then
		 * determined as a function of the height and the string lengths.
		 * 
		 * @param model
		 * @param height
		 * @param particleInjectorNode - The particle injector node for which
		 * this panel will control the type of particle injected.
		 */
		public ParticleTypeSelectorNode(double height, ParticleType initialParticleType,
		        final ParticleInjectorNode particleInjectorNode){
		    
		    this.particleInjectorNode = particleInjectorNode;
		    
		    // Create the selector for sodium.
		    ButtonIconPanel sodiumButtonPanel = createAndAddParticleSelectorButton( ParticleType.SODIUM_ION );
		    sodiumButton = sodiumButtonPanel.getRadioButton();
		    
		    // Create the selector for Potassium.
            ButtonIconPanel potassiumButtonPanel = createAndAddParticleSelectorButton( ParticleType.POTASSIUM_ION );
            potassiumButton = potassiumButtonPanel.getRadioButton();
            
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( sodiumButton );
            buttonGroup.add( potassiumButton );
            
		    JPanel buttonPanel = new JPanel();
		    buttonPanel.setLayout( new GridLayout(2, 2 ) );
		    buttonPanel.add( sodiumButtonPanel );
		    JPanel spacerPanel1 = new JPanel();
		    spacerPanel1.setBackground( BACKGROUND_COLOR );
		    buttonPanel.add( spacerPanel1 );
		    buttonPanel.add( potassiumButtonPanel );
            JPanel spacerPanel2 = new JPanel();
            spacerPanel2.setBackground( BACKGROUND_COLOR );
		    buttonPanel.add( spacerPanel2 );
		    
		    PSwing buttonPanelPSwing = new PSwing( buttonPanel );
		    
		    buttonPanelPSwing.setScale( height * 0.8 / buttonPanelPSwing.getFullBoundsReference().height );
		    
			// Create the body of the node.
			double bodyHeight = height;
			double bodyWidth = buttonPanelPSwing.getFullBoundsReference().width * 1.2;
            PhetPPath body = new PhetPPath(new RoundRectangle2D.Double(0, 0, bodyWidth,
                    bodyHeight, 30, 30), BACKGROUND_COLOR, BORDER_STROKE, BORDER_COLOR);
			
			// Put the particle selection buttons on the tag.
            buttonPanelPSwing.setOffset( 
                    body.getFullBoundsReference().getCenterX() - buttonPanelPSwing.getFullBoundsReference().getWidth() / 2, 
                    body.getFullBoundsReference().getCenterY() - buttonPanelPSwing.getFullBoundsReference().getHeight() / 2);
			body.addChild(buttonPanelPSwing);
			
			// Add the body to the injector node.
			addChild(body);
			
			// Do the final initialization.
			updateParticleSelectionButtons();
		}
		
		/**
		 * Convenience method - avoids code duplication.
		 * 
		 * @param particleType
		 * @return
		 */
		private ButtonIconPanel createAndAddParticleSelectorButton(final ParticleType particleType){
		    Image particleImage;
		    switch (particleType){
		    case SODIUM_ION:
		        particleImage = new ParticleNode(new SodiumIon(), PARTICLE_MVT).toImage();
		        break;
            case POTASSIUM_ION:
                particleImage = new ParticleNode(new PotassiumIon(), PARTICLE_MVT).toImage();
                break;
            default:
                System.err.println(getClass().getName() + "- Error: Unrecognized particle type.");
                assert false;
                particleImage = new ParticleNode(new PotassiumIon(), PARTICLE_MVT).toImage();
                break;
		    }
            final JRadioButton radioButton = new JRadioButton();
            radioButton.setBackground( BACKGROUND_COLOR );
            radioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    particleInjectorNode.setParticleTypeToInject( particleType );
                }
            });
            JPanel buttonPanel = new HorizontalLayoutPanel();
            buttonPanel.setBackground( BACKGROUND_COLOR );
            buttonPanel.add( radioButton );
            JLabel iconLabel = new JLabel( new ImageIcon( particleImage ) ); 
            buttonPanel.add( iconLabel );
            return new ButtonIconPanel( radioButton, particleImage );
		}
		
		private void updateParticleSelectionButtons(){
		    switch (particleInjectorNode.particleTypeToInject){
		    case SODIUM_ION:
		        if (!sodiumButton.isSelected()){
		            sodiumButton.setSelected( true );
		        }
		        break;
		    case POTASSIUM_ION:
                if (!potassiumButton.isSelected()){
                    potassiumButton.setSelected( true );
                }
                break;
            default:
                System.err.println(getClass().getName() + " - Error: Unknown particle type.");
                assert false;
                break;
		    }
        }
	}
	
	/**
	 * A class that contains a radio button and an icon.  This is needed
	 * because Java, for some odd reason, replaces the radio button when one
	 * tries to set an icon for it.
	 *
	 * @author John Blanco
	 */
	private static class ButtonIconPanel extends HorizontalLayoutPanel {
	    private final JRadioButton radioButton;
	    
        public ButtonIconPanel( final JRadioButton radioButton, Image image ) {
            this.radioButton = radioButton;
            setBackground( ParticleTypeSelectorNode.BACKGROUND_COLOR );
            add(radioButton);
            JLabel iconImageLabel = new JLabel( new ImageIcon( image ) ); 
            add( iconImageLabel );
            // Add a listener to the image that essentially makes it so that
            // clicking on the image is the same as clicking on the button.
            iconImageLabel.addMouseListener( new MouseAdapter(){
                public void mouseReleased(MouseEvent e) {
                    radioButton.doClick();
                }
            });

        }

        protected JRadioButton getRadioButton() {
            return radioButton;
        }
	}
}
