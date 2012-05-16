// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import edu.colorado.phet.chemicalreactions.box2d.BodyModel;
import edu.colorado.phet.chemicalreactions.model.MoleculeShape.AtomSpot;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.BOX2D_DENSITY;
import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.BOX2D_MODEL_TRANSFORM;

public class Molecule extends BodyModel {

    private final List<Atom> atoms = new ArrayList<Atom>();
    private final Map<Atom, AtomSpot> atomMap = new HashMap<Atom, AtomSpot>();

    public Molecule() {
        super( new BodyDef() {{
                   type = BodyType.DYNAMIC;
               }}, BOX2D_MODEL_TRANSFORM );
        final SimpleObserver updateObserver = new SimpleObserver() {
            public void update() {
                updatePositions();
            }
        };
        position.addObserver( updateObserver, false );
        angle.addObserver( updateObserver, false );
    }

    public Molecule( MoleculeShape shape ) {
        this();
        for ( final AtomSpot spot : shape.spots ) {
            final Atom atom = new Atom( spot.element, new Property<ImmutableVector2D>( spot.position ) );
            addAtom( atom );
            atomMap.put( atom, spot );

            addFixtureDef( new FixtureDef() {{
                density = BOX2D_DENSITY;
                restitution = 1;
                shape = new CircleShape() {{
                    m_radius = (float) BOX2D_MODEL_TRANSFORM.viewToModelDeltaX( atom.getRadius() );
                    ImmutableVector2D box2dPosition = BOX2D_MODEL_TRANSFORM.viewToModelDelta( spot.position );
                    m_p.set( (float) box2dPosition.getX(), (float) box2dPosition.getY() );
                }};
            }} );
        }
    }

    private void updatePositions() {
        for ( Atom atom : atoms ) {
            AtomSpot spot = atomMap.get( atom );
            ImmutableVector2D rotatedOffset = spot.position.getRotatedInstance( angle.get() );
            atom.position.set( Molecule.this.position.get().plus( rotatedOffset ) );
        }
    }

    public void addAtom( Atom atom ) {
        atoms.add( atom );
    }

    public List<Atom> getAtoms() {
        return atoms;
    }

    @Override public void intraStep() {
        super.intraStep();
        float multiplier = 3;
        if ( !userControlled.get() ) {
            final Vec2 v = getBody().getLinearVelocity();
            final float r1 = (float) ( Math.random() - 0.5 ) * multiplier;
            final float r2 = (float) ( Math.random() - 0.5 ) * multiplier;
            final float r3 = (float) ( Math.random() - 0.5 ) * multiplier;
            getBody().setLinearVelocity( new Vec2( v.x + r1 / 20, v.y + r2 / 20 ) );
            getBody().setAngularVelocity( getBody().getAngularVelocity() + r3 / 100 );
        }
    }
}
