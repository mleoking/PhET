import JSci.maths.*;
import JSci.maths.wavelet.*;
import JSci.maths.wavelet.daubechies2.*;

/****************************************
* Test the biorthogonality
* @author Daniel Lemire
*****************************************/
public class TestBiorthogonalityDau2 {
        public static void main(String arg[]) {
                int n0=8;
                int j0=0;
                new TestBiorthogonalityDau2(n0,j0);
        }

        public TestBiorthogonalityDau2(int n0, int j0) {
                double[][] mat=new double[2*n0-2][2*n0-2];
                for(int k=0;k<n0;k++) {
                        System.out.println("k="+k+" /"+(2*n0-2));
                        for(int l=0;l<n0;l++) {
                                mat[k][l]=EcheEche(k,l,n0,j0);
                        }
                        for(int l=n0;l<2*n0-2;l++) {
                                mat[k][l]=EcheOnde(k,l-n0,n0,j0);
                        }
                }
                for(int k=n0;k<2*n0-2;k++) {
                        System.out.println("k="+k+" /"+(2*n0-2));
                        for(int l=0;l<n0-2;l++) {
                                mat[k][l]=OndeEche(k-n0,l,n0,j0);
                        }
                        for(int l=n0;l<2*n0-2;l++) {
                                mat[k][l]=OndeOnde(k-n0,l-n0,n0,j0);
                        }
                }
                double[][] Id=new double[2*n0][2*n0];
                for(int k=0;k<2*n0-2;k++) {
                        for(int l=0;l<2*n0-2;l++) {
                                if(k==l) {Id[k][l]=1;}
                        }
                }
                double max=0.0;
                for(int k=0;k<2*n0-2;k++) {
                        for(int l=0;l<2*n0-2;l++) {
                                if(Math.abs(Id[k][l]-mat[k][l])>max) {
                                        max=Math.abs(Id[k][l]-mat[k][l]);
                                }
                        }
                }

                ImprimeMatrice(mat,n0);
                System.out.println("Max = "+max);
        }
        public void ImprimeMatrice(double[][] mat, int n0) {
                for(int k=0;k<2*n0-2;k++) {
                        for(int l=0;l<2*n0-2;l++) {
                                if(Math.abs(mat[k][l])>0.000001) {
                                        System.out.println(" mat [ "+k+" , "+l+" ] = "+mat[k][l]);
                                }
                        }
                }
        }
        public double EcheEche(int k, int l,int n0,int j0) {
                MultiscaleFunction Primaire=new Scaling2(n0,k);
                MultiscaleFunction Duale=new Scaling2(n0,l);
                return(DiscreteHilbertSpace.integrate(Primaire,Duale,j0));
        }
        public double EcheOnde(int k, int l,int n0, int j0) {
                MultiscaleFunction Primaire=new Scaling2(n0,k);
                MultiscaleFunction Duale=new Wavelet2(n0,l);
                return(DiscreteHilbertSpace.integrate(Primaire,Duale,j0));
        }
        public double OndeEche(int k, int l,int n0,int j0) {
                MultiscaleFunction Primaire=new Wavelet2(n0,k);
                MultiscaleFunction Duale=new Scaling2(n0,l);
                return(DiscreteHilbertSpace.integrate(Primaire,Duale,j0));
        }
        public double OndeOnde(int k, int l,int n0, int j0) {
                MultiscaleFunction Primaire=new Wavelet2(n0,k);
                MultiscaleFunction Duale=new Wavelet2(n0,l);
                return(DiscreteHilbertSpace.integrate(Primaire,Duale,j0 ));
        }
}

