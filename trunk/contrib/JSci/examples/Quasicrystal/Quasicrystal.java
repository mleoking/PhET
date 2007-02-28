import JSci.maths.*;
import JSci.maths.matrices.DoubleTridiagonalMatrix;
import JSci.io.*;
import java.io.*;

/**
* Phonons in a Quasicrystal
* @author Mark Hale
*/
public final class Quasicrystal {
	private int N;
        private double mass_m,mass_M;
	private double eigenvalue[];
	/**
	* Instantiate class.
	*/
	public static void main(String arg[]) {
		if(arg.length==3) {
			int n=Integer.valueOf(arg[0]).intValue();
			double m1=Double.valueOf(arg[1]).doubleValue();
			double m2=Double.valueOf(arg[2]).doubleValue();
			new Quasicrystal(n,m1,m2);
		} else {
			System.out.println("Need to specify command line arguments:");
			System.out.println("<length of chain> <mass of atom 1> <mass of atom 2>");
                }
	}
	/**
	* Constructor.
	* @param n order of matrix
	* @param m1 mass
	* @param m2 mass
	*/
	public Quasicrystal(int n,double m1,double m2) {
		N=n;
                mass_M=m1;
                mass_m=m2;
                compute();
		saveResults();
	}
	/**
	* Calculate the vibrational frequencies.
	*/
	private void compute() {
		DoubleTridiagonalMatrix matrix=new DoubleTridiagonalMatrix(N);
		eigenvalue=new double[N];
// Generate sequence of masses m and M
		double mass[]=fibonacci();
// Create matrix
		matrix.setElement(0,0,2.0/mass[0]);
		matrix.setElement(0,1,-1.0/Math.sqrt(mass[0]*mass[1]));
		for(int i=1;i<N-1;i++) {
			matrix.setElement(i,i-1,-1.0/Math.sqrt(mass[i]*mass[i-1]));
			matrix.setElement(i,i,2.0/mass[i]);
			matrix.setElement(i,i+1,-1.0/Math.sqrt(mass[i]*mass[i+1]));
		}
		matrix.setElement(N-1,N-1,2.0/mass[N-1]);
		matrix.setElement(N-1,N-2,-1.0/Math.sqrt(mass[N-1]*mass[N-2]));
// Solve eigenproblem
                try {
                        eigenvalue=LinearMath.eigenvalueSolveSymmetric(matrix);
                } catch (MaximumIterationsExceededException e) {
                        System.out.println("Too many iterations.");
                }
	}
	/**
	* Creates a Fibonacci sequence.
	* @return an array containing the sequence
	*/
	private double[] fibonacci() {
		double array[];
		java.util.Vector seq=new java.util.Vector(N);
		Double adult=new Double(mass_M);
		Double child=new Double(mass_m);
		seq.addElement(child);
		while(seq.size()<N) {
			for(int i=0;i<seq.size();i++) {
				if(seq.elementAt(i).equals(adult))
					seq.insertElementAt(child,++i);
				else if(seq.elementAt(i).equals(child))
					seq.setElementAt(adult,i);
			}
		}
		array=new double[N];
		for(int i=0;i<array.length;i++) {
			array[i]=((Double)seq.elementAt(i)).doubleValue();
		}
		return array;
	}
	/**
	* Log results to disk.
	*/
	private void saveResults() {
                File file=new File("spectrum.txt");
                char filebuf[]=null;
		double data[][]=new double[1][N+1];
                data[0][0]=mass_m/mass_M;
                for(int i=1;i<data[0].length;i++) {
                        data[0][i]=Math.sqrt(eigenvalue[i-1]);
		}
// Read in existing data
                try {
                        FileReader in=new FileReader(file);
                        filebuf=new char[(int)file.length()];
                        in.read(filebuf);
                        in.close();
                } catch(Exception e) {
                        System.out.println("No previous data - new file.");
                }
// Save all to file
                try {
                        TextWriter out=new TextWriter(file,',');
                        if(filebuf!=null)
                                out.write(filebuf);
                        out.write(data);
                        out.close();
                } catch(Exception e) {
                        System.out.println("Failed to save.");
                }
	}
}

