package wpds.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pathexpression.Edge;
import pathexpression.IRegEx;
import pathexpression.LabeledGraph;
import pathexpression.PathExpressionComputer;
import wpds.interfaces.Location;
import wpds.interfaces.State;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public abstract class WeightedPAutomaton<N extends Location, D extends State, W extends Weight<N>>
    implements LabeledGraph<D, N> {
  private Map<Transition<N, D>, W> transitionToWeights = new HashMap<>();
  // Set Q is implicit
  // Weighted Pushdown Systems and their Application to Interprocedural Dataflow Analysis
  protected Set<Transition<N, D>> transitions = Sets.newHashSet();
  // set F in paper [Reps2003]
  protected D finalState;
  // set P in paper [Reps2003]
  protected D initialState;
  protected Set<D> states = Sets.newHashSet();
  private final Multimap<D, Transition<N, D>> transitionsOutOf = HashMultimap.create();
  private final Multimap<D, Transition<N, D>> transitionsInto = HashMultimap.create();

  public WeightedPAutomaton(D initialState, D finalState) {
    this.initialState = initialState;
    this.finalState = finalState;
  }


  public abstract D createState(D d, N loc);


  public Set<Transition<N, D>> getTransitions() {
    return Sets.newHashSet(transitions);
  }

  public Collection<Transition<N, D>> getTransitionsOutOfEps(D state) {
    return Sets.newHashSet(transitionsOutOf.get(state));
  }

  public Collection<Transition<N, D>> getTrasitionsIntoEps(D state) {
    return Sets.newHashSet(transitionsInto.get(state));
  }

  private boolean updateWeightFor(Transition<N, D> trans, W weight) {
    W oldWeight = getWeightFor(trans);
    W newWeight = null;
    if (oldWeight == null) {
      newWeight = weight;
    } else {
      newWeight = (W) oldWeight.combineWith(weight);
    }
    transitionToWeights.put(trans, newWeight);
    return !newWeight.equals(oldWeight);
  }

  public Collection<Transition<N, D>> addTransitionWithWeight(Transition<N, D> trans, W weight) {
    D from = trans.getStart();
    D to = trans.getTarget();
    states.add(from);
    states.add(to);
    transitions.add(trans);
    System.out.println(trans);
    System.out.println(weight);
    Set<Transition<N, D>> changed = new HashSet<>();
    if (updateWeightFor(trans, weight))
      changed.add(trans);
    if (trans.getLabel().equals(epsilon())) {

      Collection<Transition<N, D>> outGoing = transitionsOutOf.get(to);
      for (Transition<N, D> t : outGoing) {
        Transition<N, D> newTrans = new Transition<N, D>(from, t.getLabel(), t.getTarget());
        transitionsOutOf.get(from).add(newTrans);
        transitions.add(newTrans);

        if (updateWeightFor(newTrans, (W) weight.extendWith(getWeightFor(t))))
          changed.add(newTrans);
      }
      Collection<Transition<N, D>> into = transitionsInto.get(from);
      for (Transition<N, D> t : into) {
        Transition<N, D> newTrans = new Transition<N, D>(t.getStart(), t.getLabel(), to);
        transitionsInto.get(to).add(newTrans);
        transitions.add(newTrans);
        if (updateWeightFor(newTrans, (W) weight.extendWith(getWeightFor(t))))
          changed.add(newTrans);
      }
    } else {
      transitionsOutOf.get(from).add(trans);
      transitionsInto.get(to).add(trans);
    }
    return changed;
  }




  public D getInitialState() {
    return initialState;
  }

  public D getFinalState() {
    return finalState;
  }



  public String toString() {
    String s = "PAutomaton\n";
    s += "\tInitialStates:" + initialState + "\n";
    s += "\tFinalStates:" + finalState + "\n";
    s += "\tWeightToTransitions:\n\t\t";
    s += Joiner.on("\n\t\t").join(transitionToWeights.entrySet());
    return s;
  }


  public abstract N epsilon();

  public IRegEx<N> extractLanguage(D from) {
    PathExpressionComputer<D, N> expr = new PathExpressionComputer<>(this);
    return expr.getExpressionBetween(from, getFinalState());
  }

  public Set<D> getStates() {
    return states;
  }

  public Set<Edge<D, N>> getEdges() {
    Set<Edge<D, N>> trans = Sets.newHashSet();
    for (Edge<D, N> tran : transitions)
      trans.add(tran);
    return trans;
  };

  public Set<D> getNodes() {
    return getStates();
  };



  public W getWeightFor(Transition<N, D> trans) {
    return transitionToWeights.get(trans);
  }




}
