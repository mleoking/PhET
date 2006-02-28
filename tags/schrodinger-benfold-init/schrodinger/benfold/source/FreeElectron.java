/**
 * Free electron analogue to the Kronig-Penney model.
 */
class FreeElectron extends KronigPenney {
    public FreeElectron() {
        super( 0 );
    }

    public double solve( double ka ) {
        return ka * Math.PI;
    }
}
