/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.greeman.forgettingmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The ForgettingMap class will store unique associations between two types specified on declaration.
 * @author jgreeman
 * @param <K> class for key
 * @param <V> class for value
 * Will take an int for maximum size on construction, if this size is reached new items will replace the least used item.
 */
public class ForgettingMap<K,V> {
    private final List<K>       keys        =   new ArrayList<>();
    private final List<V>       values      =   new ArrayList<>();
    private final List<Integer> accessCount =   new ArrayList<>();
    private final int           maxSize;
    
    /**
     * Constructor for ForgettingMap
     * @param maxSize the maximum size this map can be
     */
    public ForgettingMap(int maxSize) {
        this.maxSize = maxSize;
    }
    
    /**
     * method to add a new association to the map
     * @param key key to be added
     * @param value value to be added
     * Will first remove the key (does nothing if it does not exists) not synchronized as the function has its own thread protection
     * Next checks the size of the keys, if it is already at the max size then the least accessed is removed
     * finally the new values are added
     */
    public void add(K key, V value){
        //try to remove existing key
        remove(key);
        synchronized(this){
            // if at max size remove least accessed
            if (size() >= maxSize){
                // Collections.min() will find the lowest number in a list, index of will find the first index of that number 
                // this index will represent the first item added with the lowest access count
                int index   =   accessCount.indexOf(Collections.min(accessCount));
                removeIndex(index);
            }
            keys.add(key);
            values.add(value);
            accessCount.add(0);
        }
    }
    /**
     * method to get a value associated with a specific key
     * @param key to be found
     * @return a value if key exists or null
     */
    public V find(K key){
        synchronized(this){
            int index   =   keys.indexOf(key);
            if (index != -1){
                accessCount.set(index, accessCount.get(index) + 1);
                return values.get(index);
            } else {
                return null;
            }
        }
    }
    
    /**
     * method to remove a key
     * @param key key to be removed
     * removes a key and value and count if the key exists
     */
    public void remove(K key){
        synchronized(this){
            int index = keys.indexOf(key);
            //if key exists
            if (index != -1){
                removeIndex(index);
            }
        }
    }
    /**
     * method to get the size of the map
     * @return the size
     * used the keys list to get the size, sizes of the array are checked here, if there is a problem a runtime error is thrown
     */
    public int size() throws BadSizeException{
        synchronized(this){
            if (keys.size() != values.size() || keys.size() != accessCount.size()){
                throw new BadSizeException("keys size (" + keys.size() + ") values size (" + values.size() + ") sccessCount size (" + accessCount.size() + ") do not match");
            }
            if (keys.size() > maxSize){
                throw new BadSizeException("The size of this map (" + keys.size() + ") has exceeded the maximum size (" + maxSize + ")");
            }
            return keys.size();
        }
    }
    
    /**
     * private method to remove a specific indexed item from all lists
     * @param i index to be removed
     * removes the key, value and access count for a specified index.
     * This method is not protected and should not be used by other methods.
     */
    private void removeIndex(int i){
        keys.remove(i);
        values.remove(i);
        accessCount.remove(i);
    }
    
}
