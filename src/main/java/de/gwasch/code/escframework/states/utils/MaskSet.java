package de.gwasch.code.escframework.states.utils;

public class MaskSet<T>
{
    private Mask<T, T> startMask;
    private Mask<T, T> stopMask;

    public MaskSet(Mask<T, T> startmask, Mask<T, T> stopmask)
    {
        startMask = startmask;
        stopMask = stopmask;
    }

    public Mask<T, T> getStartMask() {
        return startMask;
    }

    public Mask<T, T> getStopMask() {
        return stopMask; 
    }
}