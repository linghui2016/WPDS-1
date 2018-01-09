package boomerang.customize;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import soot.SootMethod;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import sync.pds.solver.nodes.Node;
import wpds.interfaces.State;

public class ForwardEmptyCalleeFlow extends EmptyCalleeFlow {


	@Override
	protected Collection<? extends State> systemArrayCopyFlow(SootMethod caller, Stmt callSite, Val value,
			Stmt returnSite) {
		if(value.equals(new Val(callSite.getInvokeExpr().getArg(0), caller))){
			Value arg = callSite.getInvokeExpr().getArg(2);
			return Collections.singleton(new Node<Statement, Val>(new Statement(returnSite, caller), new Val(arg,caller)));
		}
		return Collections.emptySet();
	}

	@Override
	protected Collection<? extends State> stringBuilderFlow(SootMethod caller, Stmt callSite, Val value,
			Stmt returnSite) {
		InvokeExpr ie = callSite.getInvokeExpr();
		if(callSite.getInvokeExpr().getMethod().isConstructor() && ie instanceof InstanceInvokeExpr && ie.getArgCount() > 0){
			InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
			Value base = iie.getBase();
			if(value.equals(new Val(ie.getArg(0), caller))){
				return Collections.singleton(new Node<Statement, Val>(new Statement(returnSite, caller), new Val(base,caller)));
			}
		}
		if(callSite.getInvokeExpr().getMethod().getName().contains("append") && ie instanceof InstanceInvokeExpr  && ie.getArgCount() > 0){
			InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
			Value base = iie.getBase();
			if(value.equals(new Val(ie.getArg(0), caller))){
				Set<State> out = Sets.newHashSet();
				out.add(new Node<Statement, Val>(new Statement(returnSite, caller), new Val(base,caller)));
				if(callSite instanceof AssignStmt){
					out.add(new Node<Statement, Val>(new Statement(returnSite, caller), new Val(((AssignStmt)callSite).getLeftOp(),caller)));
				} 
				return out;
			}
		}

		
		if(callSite.getInvokeExpr().getMethod().getName().contains("toString") && ie instanceof InstanceInvokeExpr){
			InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
			Value base = iie.getBase();
			if(value.equals(new Val(base, caller)) && callSite instanceof AssignStmt){
				AssignStmt as = (AssignStmt) callSite;
				return Collections.singleton(new Node<Statement, Val>(new Statement(returnSite, caller), new Val(as.getLeftOp(),caller)));
			}
		}
		return Collections.emptySet();
	}

}
