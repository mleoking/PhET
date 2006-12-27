package electron.man;


public class ManMaker {
    public Man newMan() {
        Node neck = new Node( 200, 200 );

        neck.addChildRelative( -10 * scale, 0 * scale );
        neck.childAt( 0 ).addChildRelative( -20 * scale, 20 * scale );
        neck.childAt( 0 ).childAt( 0 ).addChildRelative( 0 * scale, 30 * scale );

        neck.addChildRelative( 10 * scale, 0 * scale );
        neck.childAt( 1 ).addChildRelative( 20 * scale, 20 * scale );
        neck.childAt( 1 ).childAt( 0 ).addChildRelative( 0 * scale, 30 * scale );

        neck.addChildRelative( 0 * scale, 80 * scale );
        neck.childAt( 2 ).addChildRelative( -12 * scale, 0 * scale );
        neck.childAt( 2 ).addChildRelative( 12 * scale, 0 * scale );

        neck.childAt( 2 ).childAt( 0 ).addChildRelative( -10 * scale, 50 * scale );
        neck.childAt( 2 ).childAt( 0 ).childAt( 0 ).addChildRelative( 0 * scale, 60 * scale );

        neck.childAt( 2 ).childAt( 1 ).addChildRelative( 10 * scale, 50 * scale );
        neck.childAt( 2 ).childAt( 1 ).childAt( 0 ).addChildRelative( 0 * scale, 60 * scale );

        neck.addChildRelative( -20 * scale, -20 * scale );//left ear
        neck.addChildRelative( 0 * scale, -30 * scale );//hair
        neck.addChildRelative( 20 * scale, -20 * scale );//right ear

        double headWidth = 50 * scale;
        double headHeight = 50 * scale;
        return new Man( neck, headWidth, headHeight );
    }

    double scale;

    public ManMaker( double scale ) {
        this.scale = scale;
    }
}
