package com.arkoisystems.captcha.utils;

public interface Callback<R, V>
{
    
    R callback(final V value);
    
}
