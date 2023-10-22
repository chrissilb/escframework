package de.gwasch.code.escframework.states.utils;

/**
 * A {@code MaskPair} contains two masks.
 * @param <T> the type of the {@code Mask} values
 */
public class MaskPair<T>
{
    private Mask<T, T> startMask;
    private Mask<T, T> stopMask;

    public MaskPair(Mask<T, T> startMask, Mask<T, T> stopMask)
    {
        this.startMask = startMask;
        this.stopMask = stopMask;
    }

    public Mask<T, T> getStartMask() {
        return startMask;
    }

    public Mask<T, T> getStopMask() {
        return stopMask; 
    }
}