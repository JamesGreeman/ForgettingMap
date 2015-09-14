/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.greeman.forgettingmap;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author jgreeman
 */
public class ForgettingMapTest {
    
    public ForgettingMapTest() {
    }

    /**
     * Test adding 100 elements of String and Integer types to a map of size 100.
     */
    @Test
    public void testAdd1() {
        ForgettingMap<String, Integer> instance  =   new ForgettingMap<>(100);
        Random  r   =   new Random();
        for (int i = 1; i <= 100; i++){
            int value       =   r.nextInt();
            String key      =   String.valueOf(i);
            instance.add(key, value);
            Assert.assertEquals(i, instance.size());
            Assert.assertEquals(value, (int) instance.find(key));
        }
    }

    /**
     * Tests using a map of Double to String, that adding an existing value replaces rather than adds
     */
    @Test
    public void testAdd2(){
        ForgettingMap<Double, String> instance  =   new ForgettingMap<>(5);
        //replacing existing value
        instance.add(9.0, "firstValue");
        Assert.assertEquals("firstValue", instance.find(9.0));
        Assert.assertEquals(1, instance.size());
        instance.add(9.0, "newValue");
        Assert.assertEquals("newValue", instance.find(9.0));
        Assert.assertEquals(1, instance.size());
    }



    /**
     * Tests a map of Integer to String, using some basic tests, and going on to test edge cases
     */
    @Test
    public void testFind1() {
        ForgettingMap<Integer, String> instance  =   new ForgettingMap<>(5);
        instance.add(1, "one");
        instance.add(2, "two");
        instance.add(3, "three");
        instance.add(4, "four");
        instance.add(5, "five");
        
        Assert.assertEquals("one", instance.find(1));
        Assert.assertEquals("two", instance.find(2));
        Assert.assertEquals("three", instance.find(3));
        Assert.assertEquals("four", instance.find(4));
        Assert.assertEquals("five", instance.find(5));

        instance.add(6, "six");
        Assert.assertEquals("six", instance.find(6));

        instance.add(Integer.MIN_VALUE, "Smallest Value");
        Assert.assertEquals("Smallest Value", instance.find(Integer.MIN_VALUE));

        instance.add(Integer.MAX_VALUE, "Max Value");
        Assert.assertEquals("Max Value", instance.find(Integer.MAX_VALUE));

        instance.add(0, "Zero");
        Assert.assertEquals("Zero", instance.find(0));

        instance.add(-1, "Negative");
        Assert.assertEquals("Negative", instance.find(-1));

        //Empty String
        instance.add(7, "");
        Assert.assertEquals("", instance.find(7));

        //Unicode
        instance.add(8, "٩(-̮̮̃-̃)۶ ٩(●̮̮̃•̃)۶ ٩(͡๏̯͡๏)۶ ٩(-̮̮̃•̃).");
        Assert.assertEquals("٩(-̮̮̃-̃)۶ ٩(●̮̮̃•̃)۶ ٩(͡๏̯͡๏)۶ ٩(-̮̮̃•̃).", instance.find(8));

    }


    /**
     * Test of remove method, of class ForgettingMap.
     */
    @Test
    public void testRemove1() {
        ForgettingMap<Short, String> instance   =   new ForgettingMap<>(1000);
        for (short i = 0; i < 1000; i++){
            instance.add(i, Short.toString(i));
        }
        for (short i = 0; i < 1000; i++){
            instance.remove(i);
            Assert.assertEquals(null, instance.find(i));
        }
    }

    /**
     * Test of remove method, of class ForgettingMap.
     */
    @Test
    public void testRemove2() {
        ForgettingMap<Long, Map<String, String>> instance   =   new ForgettingMap<>(10);
        Map<String, String> mapToAdd    =   new HashMap<>();
        mapToAdd.put("TestKey", "some test data in here");
        instance.add(1L, null);
        instance.add(2L, null);
        instance.add(3L, null);
        instance.add(4L, mapToAdd);
        instance.add(5L, null);
        instance.add(6L, null);
        
        Map<String, String> mapFound    =    instance.find(4L);
        Assert.assertEquals(mapToAdd, mapFound);
        Assert.assertEquals("some test data in here", mapFound.get("TestKey"));
        instance.remove(4L);
        Assert.assertEquals(null, instance.find(4L));
        
    }
    
    /**
     * Test automatic removal.
     */
    @Test
    public void testAutomaticRemoval1(){
        ForgettingMap<Integer, Boolean> instance  =   new ForgettingMap<>(3);
        //load in inital values
        instance.add(1, true);
        instance.add(2, true);
        instance.add(3, true);
        Assert.assertEquals(true, (boolean) instance.find(1));
        Assert.assertEquals(true, (boolean) instance.find(2));
        Assert.assertEquals(true, (boolean) instance.find(3));
        
        //access 1 and 3, ensuring 2 is least accessed and therefore replaced by 4
        instance.find(1);
        instance.find(3);
        instance.add(4, false);
        Assert.assertEquals(null, instance.find(2));
        Assert.assertEquals(false, (boolean) instance.find(4));

        //4 is now least accessed and therefore replaced by 5
        instance.add(5, false);
        Assert.assertEquals(null, instance.find(4));
        Assert.assertEquals(false, (boolean) instance.find(5));
        
        //access 5 and 1, ensuring 3 is lowest and therefore replaced by 6
        instance.find(5);
        instance.find(5);
        instance.find(1);
        instance.add(6, false);
        Assert.assertEquals(null, instance.find(3));
        Assert.assertEquals(false, (boolean) instance.find(6));
        
        //1 and 5 should not have been removed at any point so check this
        Assert.assertEquals(true, (boolean) instance.find(1));
        Assert.assertEquals(false, (boolean) instance.find(5));
    }


    /**
     * Test automatic removal.
     */
    @Test
    public void testAutomaticRemoval2(){
        ForgettingMap<Integer, Boolean> instance  =   new ForgettingMap<>(3);
        //load in inital values
        instance.add(1, true);
        instance.add(2, true);
        instance.add(3, true);
        Assert.assertEquals(true, (boolean) instance.find(1));
        Assert.assertEquals(true, (boolean) instance.find(2));
        Assert.assertEquals(true, (boolean) instance.find(3));

        //reset count for 2 to zero by adding it again, making it the lowest and therefore replaced by 4
        instance.add(2, true);
        instance.add(4, false);
        Assert.assertEquals(null, instance.find(2));
        Assert.assertEquals(false, (boolean) instance.find(4));
    }

    /**
     * Test how the class handles threading.
     */
    @Test
    public void testThreading() throws InterruptedException{
        ForgettingMap<Integer, String> instance =   new ForgettingMap<>(100);
        TestingThread thread1   =   new TestingThread(instance);
        TestingThread thread2   =   new TestingThread(instance);
        TestingThread thread3   =   new TestingThread(instance);
        TestingThread thread4   =   new TestingThread(instance);

        //populate all initial values
        for (int i = 1; i <= 100; i++){
            instance.add(i, "bad value");
        }
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        
        //while threads are running sleep for 1 second
        while (thread1.isAlive() || thread2.isAlive() || thread3.isAlive() || thread4.isAlive()){
            Thread.sleep(1000);
        }

        //check all values are present, except those modified at the end of the final thread
        for (int i = 1; i <= 100; i++){
            if (i == 41){
                Assert.assertEquals("good value", instance.find(i));                
            } else if (i == 30){
                Assert.assertEquals(null, instance.find(i));                    
            } else {
                Assert.assertEquals("bad value", instance.find(i));   
            }
        }
    }
}
