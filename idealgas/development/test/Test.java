/*
 * Class: CollisionGod
 * Package: $PACKAGE_NAME$
 *
 * Created by: Ron LeMaster
 * Date: Nov 1, 2002
 */


/**
 *
 */
public class Test {

    public static class C {
    }

    public static class SubC1 extends C {
    }

    public static class SubC2 extends C {
    }

    public void foo( C c ) {
        System.out.println( "C" );
    }

    public void foo( SubC1 c ) {
        System.out.println( "SubC1" );
    }

    public void foo( SubC2 c ) {
        System.out.println( "SubC2" );
    }

    public static void main( String[] args ) {

        C c1 = new SubC1();
        SubC1 sc1 = new SubC1();
        Test t = new Test();

        t.foo( c1 );
        t.foo( sc1 );
    }

}
