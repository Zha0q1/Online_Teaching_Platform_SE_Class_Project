package shared;

import java.io.Serializable;

public class Pair<F, S> implements Serializable{ // from http://stackoverflow.com/questions/6044923/generic-pair-class
	private static final long serialVersionUID = 3025;
	private F first; //first member of pair
    private S second; //second member of pair

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public void setKey(F first) {
        this.first = first;
    }

    public void setValue(S second) {
        this.second = second;
    }

    public F getKey() {
        return first;
    }

    public S getValue() {
        return second;
    }
}
