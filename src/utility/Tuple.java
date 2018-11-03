package utility;

public class Tuple<X, Y> {
	public final X First;
	public final Y Second;

	public Tuple(X x, Y y) {
		this.First = x;
		this.Second = y;
	}
}
