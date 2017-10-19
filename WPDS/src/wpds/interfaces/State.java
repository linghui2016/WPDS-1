package wpds.interfaces;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import wpds.impl.Transition;
import wpds.impl.Weight;

public abstract class State {
	private Set<WPAStateListener> stateListeners = Sets.newHashSet();
	private final Set<Transition> transitionsOutOf = Sets.newHashSet();
	private final Set<Transition> transitionsInto = Sets.newHashSet();
	private Map<Transition, Weight> transitionToWeights = new HashMap<>();
	
	public void registerListener(WPAStateListener l) {
		if (!stateListeners.add(l)) {
			return;
		}
		for (Transition t : Lists.newArrayList(transitionsOutOf)) {
			l.onOutTransitionAdded(t,transitionToWeights.get(t));
		}
		for (Transition  t : Lists.newArrayList(transitionsInto)) {
			l.onInTransitionAdded(t,transitionToWeights.get(t));
		}
	}
	
	public void addInTransition(Transition trans, Weight weight){
		transitionToWeights.put(trans, weight);
		transitionsInto.add(trans);
		for (WPAStateListener l : Lists.newArrayList(stateListeners)) {
			l.onInTransitionAdded(trans, weight);
		}
	}
	public void addOutTransition(Transition trans, Weight weight){
		transitionToWeights.put(trans, weight);
		transitionsOutOf.add(trans);
		for (WPAStateListener l : Lists.newArrayList(stateListeners)) {
			l.onOutTransitionAdded(trans, weight);
		}
	}
}
