package de.gwasch.code.escframework.states.states;

public class NumberCastState<T extends Number, U extends Number> extends AbstractConversionState<T, U> {

    public NumberCastState(Class<T> stateType, String name, State<U> paramState) {
    	super(stateType, name, paramState);
    	
    	updateValue(paramState.getValue());
    }

	@SuppressWarnings("unchecked")
    protected void updateValue(U paramValue) {
    	
    	if (getStateType().equals(Integer.class)) {
			setStateValue((T)Integer.valueOf(paramValue.intValue()));
		} else if (getStateType().equals(Double.class)) {
			setStateValue((T)Double.valueOf(paramValue.doubleValue()));
		} else if (getStateType().equals(Byte.class)) {
			setStateValue((T)Byte.valueOf(paramValue.byteValue()));
		} else if (getStateType().equals(Short.class)) {
			setStateValue((T)Short.valueOf(paramValue.shortValue()));
		} else if (getStateType().equals(Long.class)) {
			setStateValue((T)Long.valueOf(paramValue.longValue()));
		} else /*if (getStateType().equals(Float.class))*/ {
			setStateValue((T)Float.valueOf(paramValue.floatValue()));
		}
    }
}
