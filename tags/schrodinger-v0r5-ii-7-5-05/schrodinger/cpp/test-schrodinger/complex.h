/***********************************************************/
/*   Complex.h --- The C++ header for complex operations   */
/*   in Scientific Computing.      Li Ju. March.29,1995    */
/***********************************************************/

typedef float Dtype;

class Complex 
{
  public:
  Dtype real,img;

	Complex(Dtype a=0,Dtype b=0);
	Complex(Complex &a);

	Complex operator+ (Complex &a);
	Complex operator+ (Dtype a); /* right Complex + left float */
 friend Complex operator+ (Dtype a, Complex &b);
                                     /* right float + left Complex */
/* 
**   Be wary that in order to let the operator + to be an
**   operator for both right Complex value add and left Complex 
**   value add, you have to define twice. The same with -,*,/.
*/
	Complex operator- (Complex &a);
	Complex operator- (Dtype a);
 friend	Complex operator- (Dtype a, Complex &b);

	Complex operator* (Complex &a);
	Complex operator* (Dtype a);
 friend	Complex operator* (Dtype a, Complex &b);

	Complex operator/ (Complex &a);
	Complex operator/ (Dtype a);
 friend Complex operator/ (Dtype a,Complex &b);

	Dtype   Abs();
	Dtype   Arg();

        int operator== (Complex &);
        int operator== (Dtype );
 friend int operator== (Dtype, Complex&);

        int operator!= (Complex &);
        int operator!= (Dtype );
 friend int operator!= (Dtype, Complex&);
 
 friend ostream & operator << (ostream &a,Complex &b);
 friend istream & operator >> (istream &a,Complex &b);
};



