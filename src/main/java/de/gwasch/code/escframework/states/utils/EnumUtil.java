package de.gwasch.code.escframework.states.utils;

public class EnumUtil {
	
    public static<T extends Enum<T>> T increment(T state) {
    	
    	Class<?> cls = state.getClass();
    	return (T)cls.getEnumConstants()[state.ordinal() + 1];
        
    	// NOTE: bad code...
//        IList<T> l = (IList<T>)Enum.GetValues(typeof(T));
//        int i = l.IndexOf(state) + 1;
//        state = (T)Enum.ToObject(typeof(T), l[i]);

        //int i = System.Convert.ToInt32(state);
        //i++;
        //state = (T)Enum.ToObject(typeof(T), i);
    }


    public static<T extends Enum<T>> T decrement(T state) {
    	
    	Class<?> cls = state.getClass();
    	return (T)cls.getEnumConstants()[state.ordinal() - 1];

        // NOTE: bad code...
//        IList<T> l = (IList<T>)Enum.GetValues(typeof(T));
//        int i = l.IndexOf(state) - 1;
//        state = (T)Enum.ToObject(typeof(T), l[i]);

        //int i = System.Convert.ToInt32(state);
        //i--;
        //state = (T)Enum.ToObject(typeof(T), i);
    }
}
