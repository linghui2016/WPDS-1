package wpds.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import wpds.interfaces.IPushdownSystem;
import wpds.interfaces.Location;
import wpds.interfaces.State;

import com.google.common.collect.Lists;

public class PostStar<N extends Location, D extends State, W extends Weight<N>> {
  private LinkedList<Transition<N, D>> worklist = Lists.newLinkedList();
  private IPushdownSystem<N, D, W> pds;
  private WeightedPAutomaton<N, D, W> fa;
  private int iterationCount;

  public void poststar(IPushdownSystem<N, D, W> pds, WeightedPAutomaton<N, D, W> initialAutomaton) {
    this.pds = pds;
    worklist = Lists.newLinkedList(initialAutomaton.getTransitions());
    fa = initialAutomaton;


    saturate();

  }

  private void saturate() {
    // PHASE 1: Is done automatically
    System.out.println(pds);
    while (!worklist.isEmpty()) {
      iterationCount++;
      Transition<N, D> t = worklist.removeFirst();
      D p = t.getStart();
      N gamma = t.getLabel();
      Set<Rule<N, D, W>> rules = pds.getRulesStarting(p, gamma);
      System.out.println("Worklist choose " + t);
      W currWeight = getOrCreateWeight(t);
      for (Rule<N, D, W> rule : rules) {
        System.out.println("Appropriate rule choose " + rule);
        D pdash = rule.getS2();
        W newWeight = (W) currWeight.extendWithIn(rule.getWeight());
        if (rule instanceof PopRule) {
          Collection<Transition<N, D>> trans = fa.getTransitionsOutOfEps(p);
          for (Transition<N, D> tq : trans) {
            if (tq.getStart().equals(p) && tq.getLabel().equals(gamma)) {
              D q = tq.getTarget();
              update(rule, new Transition<N, D>(pdash, fa.epsilon(), q), newWeight);
            }
          }
        } else if (rule instanceof NormalRule) {
          Collection<Transition<N, D>> trans = fa.getTransitionsOutOfEps(p);
          for (Transition<N, D> tq : trans) {
            if (tq.getStart().equals(p) && tq.getLabel().equals(gamma)) {
              D q = tq.getTarget();
              N gammadash = rule.getL2();
              update(rule, new Transition<N, D>(pdash, gammadash, q), newWeight);
            }
          }
        } else if (rule instanceof PushRule) {
          PushRule<N, D, W> pushRule = (PushRule<N, D, W>) rule;
          Collection<Transition<N, D>> trans = fa.getTransitionsOutOfEps(p);
          for (Transition<N, D> tq : trans) {
            if (tq.getStart().equals(p) && tq.getLabel().equals(gamma)) {
              D q = tq.getTarget();
              N gammadash = rule.getL2();
              N gammadashdash = pushRule.getCallSite();
              D pdashgammadash = fa.createState(p, gammadash);
              update(rule, new Transition<N, D>(pdashgammadash, gammadashdash, q), newWeight);
              update(rule, new Transition<N, D>(pdash, gammadash, pdashgammadash), pds.getOne());
            }
          }
        }
      }

    }
  }

  private void update(Rule<N, D, W> triggeringRule, Transition<N, D> trans, W weight) {
    System.out.println(trans + "\t as of \t" + triggeringRule + " \t and " + weight);
    Collection<Transition<N, D>> newly = fa.addTransitionWithWeight(trans, weight);

    System.out.println("Adding to WL: " + newly);
    worklist.addAll(newly);
  }

  private W getOrCreateWeight(Transition<N, D> trans) {
    W w = fa.getWeightFor(trans);
    if (w != null)
      return w;
    return pds.getZero();
  }

}
