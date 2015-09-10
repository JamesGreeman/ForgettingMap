package com.greeman.forgettingmap;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The ForgettingMap class will store unique associations between two types specified on declaration.
 * @author jgreeman
 * @param <K> class for key
 * @param <V> class for value
 */
public class ForgettingMap<K,V> {
    private final Map<K, V>         values      =   new HashMap<>();
    private final Map<K, Integer>   accessCount =   new HashMap<>();
    private final int               maxSize;
    
    /**
     * Constructor for ForgettingMap.
     * @param maxSize the maximum size this map can be
     */
    public ForgettingMap(int maxSize) {
        this.maxSize = maxSize;
    }
    
    /**
     * Adds a new association to the map.
     * If the map is full and does not contain the key being added the least accessed
     * element is removed.  Regardless of this the new value is added under the key
     * and an access count of 0 is set for the key.
     * @param key key to be added
     * @param value value to be added
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
     * Returns the value corresponding to a given key.
     * If the map contains the key the access count is incremented and the value
     * is returned, otherwise null is returned
     * @param key to be found
     * @return corresponding value if exists else null
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
    
    /**
     * Removes the least accessed element.
     */
    private void removeLeastAccessed(){
        synchronized(this){
            K toRemove  =   getLeastAccessed();
            if (toRemove != null){
                remove(toRemove);
            }
        }
    }
    
    /**
     * Returns the key that has been accessed the least.
     * Checks each entry in the access count, storing the smallest element.
     * @return key corresponding to smallest element
     */
    private K getLeastAccessed(){
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
    
    /**
     * Removes a specified key from the map.
     * Removes both the value and the access count from the internal data structures
     * @param key key to be removed
     */
    public void remove(K key){
        synchronized(this){
            values.remove(key);
            accessCount.remove(key);
        }        
    }
    
    /**
     * Returns the size of the map.
     * The method will check the size of internal data structures are in line and
     * that the maximum size parameter has not been exceeded.  If it either of these
     * are in error then a BadSizeException is throw.  Otherwise the size of the values
     * map is returned.
     * @return the size of values map
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
