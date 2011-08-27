// Copyright 2002-2011, University of Colorado
package org.reid.scenic.model;

import java.util.Random;

import org.reid.scenic.TestScenicPanel;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.sugarandsaltsolutions.common.util.ImmutableList;

/**
 * @author Sam Reid
 */
public class Model {
    public final ImmutableList<Atom> atoms;
    public final ButtonModel<Model> button1;
    public final ButtonModel<Model> button2;
    public final Person person;

    public Model() {
        this( new ImmutableList<Atom>( createAtoms() ),
              new ButtonModel<Model>( new PhetFont( 16, true ), "Fly right", 100, 100, false, false, new Function1<Model, Model>() {
                  public Model apply( Model model ) {
                      return model.atoms( model.atoms.map( new Function1<Atom, Atom>() {
                          public Atom apply( Atom atom ) {
                              return atom.velocity( atom.velocity.plus( 2, 0 ) );
                          }
                      } ) );
                  }
              } ),
              new ButtonModel<Model>( new PhetFont( 16, true ), "Chop", 300, 300, false, false, new Function1<Model, Model>() {
                  public Model apply( Model model ) {
                      return model.atoms( model.atoms.map( new Function1<Atom, Atom>() {
                          public Atom apply( Atom atom ) {
                              return atom.velocity( atom.velocity.times( 0.5 ) );
                          }
                      } ) );
                  }
              } ), new Person( "Larry" ) );
    }

    public Model( ImmutableList<Atom> atoms, ButtonModel<Model> button1, ButtonModel<Model> button2, Person person ) {
        this.atoms = atoms;
        this.button1 = button1;
        this.button2 = button2;
        this.person = person;
    }

    public Model atoms( ImmutableList<Atom> atoms ) {
        return new Model( atoms, button1, button2, person );
    }

    public Model button1( ButtonModel<Model> button1 ) {
        return new Model( atoms, button1, button2, person );
    }

    public Model button2( ButtonModel<Model> button2 ) {
        return new Model( atoms, button1, button2, person );
    }

    private static Atom[] createAtoms() {
        Random random = new Random();
        Atom[] a = new Atom[500];
        for ( int i = 0; i < a.length; i++ ) {
            a[i] = new Atom( new ImmutableVector2D( random.nextDouble() * 800, random.nextDouble() * TestScenicPanel.MAX_Y ), new ImmutableVector2D( random.nextDouble() * 10 - 5, random.nextDouble() * 10 - 5 ), random.nextDouble() + 1 );
        }
        return a;
    }
}