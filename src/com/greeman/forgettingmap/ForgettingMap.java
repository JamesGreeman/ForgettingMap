package com.greeman.forgettingmap;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The ForgettingMap class will store unique associations between two types specified on declaration.
 * @author jgreeman
 * @param <K> class for key
 * @param <V> class for value
 * Will take an int for maximum size on construction, if this size is reached new items will replace the least used item.
 */
public class ForgettingMap<K,V> {
    private final Map<K, V>         values      =   new HashMap<>();
    private final Map<K, Integer>   accessCount =   new HashMap<>();
    private final int               maxSize;
    
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
        synchronized(this){
            //try to remove existing key
            if (size() >= maxSize && !values.containsKey(key)){
                removeLeastAccessed();
            } 
            values.put(key, value);
            accessCount.put(key, 0);
        }
    }
    /**
     * method to get a value associated with a specific key
     * @param key to be found
     * @return a value if key exists or null
     */
    public V find(K key){
        synchronized(this){
            if (values.containsKey(key)){
                accessCount.put(key, accessCount.get(key) + 1);
                return values.get(key);
            } else {
                return null;
            }
        }
    }
    
    public void removeLeastAccessed(){
        synchronized(this){
            K toRemove  =   getLeastAccessed();
            if (toRemove != null){
                values.remove(toRemove);
                accessCount.remove(toRemove);
            }
        }
    }
    
    public K getLeastAccessed(){
        synchronized(this){
            K key   =   null;
            Integer minimum =   Integer.MAX_VALUE;
            for (Entry<K, Integer> entry : accessCount.entrySet()){
                if (minimum.compareTo(entry.getValue()) > 0){
                    key     =   entry.getKey();
                    minimum =   entry.getValue();
                }
            }
            return key;
        }
    }
    public void remove(K key){
        synchronized(this){
            values.remove(key);
            accessCount.remove(key);
        }        
    }
    
    /**
     * method to get the size of the map
     * @return the size
     * used the keys list to get the size, sizes of the array are checked here, if there is a problem a runtime error is thrown
     */
    public int size() throws BadSizeException{
        synchronized(this){
            if (accessCount.size() != values.size()){
                throw new BadSizeException("values size (" + values.size() + ") accessCount size (" + accessCount.size() + ") do not match");
            }
            if (values.size() > maxSize){
                throw new BadSizeException("The size of this map (" + values.size() + ") has exceeded the maximum size (" + maxSize + ")");
            }
            return values.size();
        }
    }
}
