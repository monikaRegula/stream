package com.kodilla.stream.world;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

public class WorldGetPeopleQuantityTestSuite {

   @Test
    public void testGetPeopleQuantity(){

       World world = new World();
       BigDecimal total= world.getPeopleQuantity();

      BigDecimal expected = new BigDecimal("222222222222");
      Assert.assertEquals(expected,total);
   }

}
