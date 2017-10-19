package sync.pds.solver.nodes;

import wpds.interfaces.State;

public abstract class INode<Fact> extends State {
	public abstract Fact fact();
}
