package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.states.aggregations.AggregateFunction;
import de.gwasch.code.escframework.states.aggregations.ComparableAggregation;
import de.gwasch.code.escframework.states.aggregations.SimpleAggregation;
import de.gwasch.code.escframework.states.utils.MaskPool;
import de.gwasch.code.escframework.states.utils.MaskSet;
import de.gwasch.code.escframework.states.utils.StateMask;


public abstract class AbstractISState<T extends Enum<T>> extends CompositeState<T> {
 
    private StateMask<T, T> startMask;
    private StateMask<T, T> stopMask;

    protected StrivingState<T> rootState;



    protected AbstractISState(Class<T> stateType, String name, State<T> strivedstate) {
    	super(stateType, name);
    	
        State<T> ss = new StrivingState<T>(stateType, name, RestrictionType.Offensive, strivedstate);
        rootState = new StrivingState<T>(stateType, name, ss);
        
        MaskSet<T> maskset = MaskPool.getInstance().getMaskSet(stateType);

        if (maskset != null) {
            startMask = maskset.getStartMask();
            stopMask = maskset.getStopMask();
        }
        else {
            if (!MaskPool.getInstance().hasMaskSet(stateType)) {
            	MaskPool.getInstance().addMaskSet(stateType);
            }
            
            startMask = null;
            stopMask = null;
        }
    }

    protected State<T> getRootState() {
        return rootState;
    }

    //todo, BrakedSate ist eigentlich nur bei sequentiellem Transitionmodus sinnvoll
    public void addDependency(ISState<T> dependency, DependencyType starttype, DependencyType stoptype)
    {
        addStartDependency(rootState, dependency, startMask);
        addStopDependency(dependency.getRootState(), this, stopMask);


        if (starttype == DependencyType.Active) {
            State<T> brakestate = new BrakedState<T>(getStateType(), this, rootState.getStrivedState());
            addStartDependency(dependency.rootState.getStrivedState(), brakestate, stopMask);
        }

        if (stoptype == DependencyType.Active) {
            State<T> brakestate = new BrakedState<T>(getStateType(), dependency, dependency.rootState.getStrivedState());
            addStopDependency(rootState.getStrivedState(), brakestate, startMask);
        }
    }


    public void addDependency(ISState<T> dependency, DependencyType type)
    {
        addDependency(dependency, type, type);
    }


    public void addDependency(ISState<T> dependency)
    {
        addDependency(dependency, DependencyType.Passive, DependencyType.Passive);
    }

    
    private void addStartDependency(State<T> strivingstate, State<T> startdependency, 
                                           StateMask<T,T> mask)
    {
        StrivingState<T> striving = (StrivingState<T>)strivingstate;

        if (striving.getStartRestriction() == null)
        {
            if (mask != null) startdependency = new MaskedState<T, T>(getStateType(), startdependency, mask);
            striving.setStartRestriction(startdependency);
        }
        else
        {
            MaskedState<T, T> masked = null;
            AggregateState<T> aggregated = null;
            State<T> singlechild = null;

            if (mask != null) {
                masked = (MaskedState<T, T>)striving.getStartRestriction();

                if (masked.getNativeState() instanceof AggregateState) {
                	aggregated = (AggregateState<T>)masked.getNativeState();
                }
                else {
                    singlechild = masked.getNativeState();
                }
            }
            else {
                if (striving.getStartRestriction() instanceof AggregateState) {
                	aggregated = (AggregateState<T>)striving.getStartRestriction();
                }
                else {
                    singlechild = striving.getStartRestriction();
                }
            }


            if (aggregated == null) {
            	
        		SimpleAggregation<T> aggregation;

                switch (striving.getRestrictionType()) {
                	case Defensive: aggregation = new ComparableAggregation<T>(AggregateFunction.Min); break;
                    case Offensive: aggregation = new ComparableAggregation<T>(AggregateFunction.Max); break;
                    default: return;
                }

                aggregated = new AggregateState<T>(getStateType(), "startdep " + strivingstate.getName(), aggregation);
                
                if (masked != null) {
                    masked.setNativeState(aggregated);
                    striving.setStartRestriction(masked);
                }
                else
                    striving.setStartRestriction(aggregated);

                aggregated.addChildState(singlechild);
            }
            
            aggregated.addChildState(startdependency);
        }
    }


    private void addStopDependency(State<T> strivingstate, State<T> stopdependency,
                                   StateMask<T, T> mask)
    {
        StrivingState<T> striving = (StrivingState<T>)strivingstate;

        if (striving.getStopRestriction() == null)
        {
            if (mask != null) stopdependency = new MaskedState<T, T>(getStateType(), stopdependency, mask);
            striving.setStopRestriction(stopdependency);
        }
        else
        {
            MaskedState<T, T> masked = null;
            AggregateState<T> aggregated = null;
            State<T> singlechild = null;

            if (mask != null) {
                masked = (MaskedState<T, T>)striving.getStopRestriction();

                if (masked.getNativeState() instanceof AggregateState) {
                	aggregated = (AggregateState<T>)masked.getNativeState();
                }
                else {
                    singlechild = masked.getNativeState();
                }
               
            }
            else
            {
            	if (striving.getStopRestriction() instanceof AggregateState) {
                	aggregated = (AggregateState<T>)striving.getStopRestriction();
                }
                else {
                    singlechild = striving.getStopRestriction();
                }
            }


            if (aggregated == null) {
            	
            	SimpleAggregation<T> aggregation;

		        switch (striving.getRestrictionType()) {
		            case Defensive: aggregation = new ComparableAggregation<T>(AggregateFunction.Max); break;
		            case Offensive: aggregation = new ComparableAggregation<T>(AggregateFunction.Min); break;
		            default: return;
		        }
              

                aggregated = new AggregateState<T>(getStateType(), "stopdep " + strivingstate.getName(), aggregation);

                if (masked != null) {
                    masked.setNativeState(aggregated);
                    striving.setStopRestriction(masked);
                }
                else {
                    striving.setStopRestriction(aggregated);
                }
                
                aggregated.addChildState(singlechild);
            }

            aggregated.addChildState(stopdependency);
        }
    }
}