package com.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.patryk3211.powergrid.equipment.thermometer.ThermometerBlockEntity;

public class ThermometerPeripheral implements IPeripheral {
   private static final Method TEMPERATURE_METHOD;
   private final ThermometerBlockEntity thermometer;

   public ThermometerPeripheral(ThermometerBlockEntity thermometer) {
      this.thermometer = thermometer;
   }

   public String getType() {
      return "powergrid_thermometer";
   }

   private double readTemperature() {
      try {
         return ((Number)TEMPERATURE_METHOD.invoke(this.thermometer)).doubleValue();
      } catch (InvocationTargetException | IllegalAccessException e) {
         throw new IllegalStateException("Failed to read thermometer", e);
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getTemperature() {
      return this.readTemperature();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getMaxTemperature() {
      return (double)this.thermometer.maxTemperature;
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getProgress() {
      return (double)this.thermometer.dialTarget;
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getRedstoneOutput() {
      return this.thermometer.redstoneOutput;
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getUnit() {
      return "C";
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isOverheating() {
      return this.readTemperature() > (double)150.0F;
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      double temperature = this.readTemperature();
      Map<String, Object> data = new HashMap<>();
      data.put("temperature", temperature);
      data.put("maxTemperature", (double)this.thermometer.maxTemperature);
      data.put("progress", (double)this.thermometer.dialTarget);
      data.put("redstoneOutput", this.thermometer.redstoneOutput);
      data.put("unit", "C");
      data.put("overheating", temperature > (double)150.0F);
      return data;
   }

   public Object getTarget() {
      return this.thermometer;
   }

   public boolean equals(IPeripheral other) {
      boolean var10000;
      if (other instanceof ThermometerPeripheral peripheral) {
         if (peripheral.thermometer == this.thermometer) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   static {
      try {
         TEMPERATURE_METHOD = ThermometerBlockEntity.class.getDeclaredMethod("temperature");
         TEMPERATURE_METHOD.setAccessible(true);
      } catch (NoSuchMethodException e) {
         throw new ExceptionInInitializerError(e);
      }
   }
}
