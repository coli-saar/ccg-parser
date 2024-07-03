package com.codemelon.util;

/**
 * Used to pass a method to decreaseKey for modifying an item
 * in place
 *
 * @author Marshall Farrier
 * @since Sep 24, 2013
 */
public interface ItemChanger<T> {
    public void decreaseKey(T item);
}
