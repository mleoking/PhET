package electron.man;


public class Man {
    Node neck;
    double headHeight;
    double headWidth;

    public Man( Node neck, double w, double h ) {
        this.neck = neck;
        this.headWidth = w;
        this.headHeight = h;
    }

    public double getHeadWidth() {
        return headWidth;
    }

    public double getHeadHeight() {
        return headHeight;
    }

    public Node getLeftEar() {
        return neck.childAt( 3 );
    }

    public Node getRightEar() {
        return neck.childAt( 5 );
    }

    public Node getHair() {
        return neck.childAt( 4 );
    }

    public Node getNeck() {
        return neck;
    }

    public Node getLeftShoulder() {
        return neck.childAt( 0 );
    }

    public Node getRightShoulder() {
        return neck.childAt( 1 );
    }

    public Node getHip() {
        return neck.childAt( 2 );
    }

    public Node getLeftHip() {
        return getHip().childAt( 0 );
    }

    public Node getRightHip() {
        return getHip().childAt( 1 );
    }

}
