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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jgreeman
 */
public class ForgettingMapTest {
    
    public ForgettingMapTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of add method, of class ForgettingMap.
     */
    @Test
    public void testAdd() {
        ForgettingMap<String, Integer> instance  =   new ForgettingMap<>(100);
        Random  r   =   new Random();
        for (int i = 1; i <= 100; i++){
            int value       =   r.nextInt();
            String key      =   String.valueOf(i);
            instance.add(key, value);
            Assert.assertEquals(i, instance.size());
            Assert.assertEquals(value, (int) instance.find(key));   //cast as we always expect an int and it removes the ambiguity in assertEquals methods
        }
    }

    /**
     * Test of find method, of class ForgettingMap.
     */
    @Test
    public void testFind() {
        ForgettingMap<Integer, String> instance  =   new ForgettingMap<>(5);
        instance.add(1, "one");
        instance.add(2, "two");
        instance.add(3, "three");
        instance.add(4, "four");
        instance.add(5, "five");
        
        Assert.assertEquals("one", instance.find(1));
        Assert.assertEquals("two", instance.find(2));
        Assert.assertEquals("four", instance.find(4));
        Assert.assertEquals("five", instance.find(5));
        
        instance.add(6, "six");
        
        Assert.assertEquals(null, instance.find(3));
        Assert.assertEquals("six", instance.find(6));
    }

    /**
     * Test of remove method, of class ForgettingMap.
     */
    @Test
    public void testRemove() {
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
    public void testAutomaticRemoval(){
        ForgettingMap<Integer, Boolean> instance  =   new ForgettingMap<>(3);
        instance.add(1, true);
        instance.add(2, true);
        instance.add(3, true);
        
        
        Assert.assertEquals(true, (boolean) instance.find(1));
        Assert.assertEquals(true, (boolean) instance.find(2));
        Assert.assertEquals(true, (boolean) instance.find(3));
        
        //increment 1 and 3 so 2 is lowest
        instance.find(1);
        instance.find(3);
        instance.add(4, false);
        
        //2 should have been replaced by 4
        Assert.assertEquals(null, instance.find(2));
        Assert.assertEquals(false, (boolean) instance.find(4));
        
        instance.add(5, false);
        
        //4 should have been replaced by 5
        Assert.assertEquals(null, instance.find(4));
        Assert.assertEquals(false, (boolean) instance.find(5));
        
        //increment 5 and 1 so 3 is lowest
        instance.find(5);
        instance.find(5);
        instance.find(1);
        instance.add(6, false);
        
        
        //3 should have been replaced by 6
        Assert.assertEquals(null, instance.find(3));
        Assert.assertEquals(false, (boolean) instance.find(6));
        
        //check 1 and 5 are still present too
        Assert.assertEquals(true, (boolean) instance.find(1));
        Assert.assertEquals(false, (boolean) instance.find(5));
    }
    /**
     * Test how the class handles threading.
     */
    @Test
    public void testThreading() throws InterruptedException{
        ForgettingMap<Integer, String> instance =   new ForgettingMap<>(100);
        Object lock                             =   new Object();
        TestingThread thread1   =   new TestingThread(instance, lock);
        TestingThread thread2   =   new TestingThread(instance, lock);
        TestingThread thread3   =   new TestingThread(instance, lock);
        TestingThread thread4   =   new TestingThread(instance, lock);
        
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        
        //while threads are running sleep for 1 second
        while (thread1.isAlive() || thread2.isAlive() || thread3.isAlive() || thread4.isAlive()){
            Thread.sleep(1000);
        }
        
        for (int i = 1; i <= 100; i++){
            System.out.println(i);
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
