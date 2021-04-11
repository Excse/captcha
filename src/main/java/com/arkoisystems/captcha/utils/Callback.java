package com.arkoisystems.captcha.utils;

/**
 * A simple callback interface used for text generation etc..
 *
 * @param <R>
 *         the result type
 * @param <V>
 *         the parameter value type
 */
public interface Callback<R, V>
{
    
    /**
     * A simple method which is used to call back something.
     *
     * @param value
     *         the value provided by the method call.
     *
     * @return a return value of the callback method.
     */
    R callback(final V value);
    
}
