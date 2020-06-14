package interpreter.arithmetic;

public class Trigonometry {
	// https://en.wikipedia.org/wiki/Hyperbolic_functions
	public static double sinh(double x) {
		return (Math.exp(x)-Math.exp(-x))/2;
	}
	
	public static double cosh(double x) {
		return (Math.exp(x)+Math.exp(-x))/2;
	}
	
	public static double tanh(double x) {
		return sinh(x)/cosh(x);
	}
	
	public static double coth(double x) {
		return cosh(x)/sinh(x);
	}
	
	public static double sech(double x) {
		return 1/cosh(x);
	}
	
	public static double csch(double x) {
		return 1/sinh(x);
	}
	
	// https://en.wikipedia.org/wiki/Inverse_hyperbolic_functions
	public static double asinh(double x) {
		return Math.log(x+Math.sqrt(x*x+1));
	}
	
	public static double acosh(double x) {
		return Math.log(x+Math.sqrt(x*x-1));
	}
	
	public static double atanh(double x) {
		return 1/2*Math.log((1+x)/(1-x));
	}
	
	public static double acoth(double x) {
		return 1/2*Math.log((x+1)/(x-1));
	}
	
	public static double asech(double x) {
		return Math.log((1+Math.sqrt(1-x*x))/x);
	}
	
	public static double acsch(double x) {
		return Math.log(1/x+Math.sqrt(1/(x*x)+1));
	}
}
