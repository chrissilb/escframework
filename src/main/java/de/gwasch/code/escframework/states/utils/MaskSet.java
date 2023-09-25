package de.gwasch.code.escframework.states.utils;

public class MaskSet<T>
{
    private StateMask<T, T> startMask;
    private StateMask<T, T> stopMask;

    public MaskSet(StateMask<T, T> startmask, StateMask<T, T> stopmask)
    {
        startMask = startmask;
        stopMask = stopmask;
    }

    public StateMask<T, T> getStartMask() {
        return startMask;
    }

    public StateMask<T, T> getStopMask() {
        return stopMask; 
    }
}