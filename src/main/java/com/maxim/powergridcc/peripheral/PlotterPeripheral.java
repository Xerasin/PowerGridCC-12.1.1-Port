package com.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.patryk3211.powergrid.kinetics.plotter.PlotterBlockEntity;

public class PlotterPeripheral implements IPeripheral {
   private static final Field SAMPLE_BUFFER_FIELD;
   private static final Field HEAD_FIELD;
   private static final Field MAX_VALUE_FIELD;
   private final PlotterBlockEntity plotter;

   public PlotterPeripheral(PlotterBlockEntity plotter) {
      this.plotter = plotter;
   }

   public String getType() {
      return "powergrid_plotter";
   }

   private float[] readRawBuffer() {
      try {
         float[] buffer = (float[])SAMPLE_BUFFER_FIELD.get(this.plotter);
         return buffer.clone();
      } catch (IllegalAccessException e) {
         throw new IllegalStateException("Failed to read plotter sample buffer", e);
      }
   }

   private int readHead() {
      try {
         return HEAD_FIELD.getInt(this.plotter);
      } catch (IllegalAccessException e) {
         throw new IllegalStateException("Failed to read plotter head", e);
      }
   }

   private double readMaxValue() {
      try {
         return (double)MAX_VALUE_FIELD.getFloat(this.plotter);
      } catch (IllegalAccessException e) {
         throw new IllegalStateException("Failed to read plotter voltage range", e);
      }
   }

   private double[] readOrderedSamples() {
      float[] raw = this.readRawBuffer();
      double maxValue = this.readMaxValue();
      int head = this.readHead();
      double[] samples = new double[raw.length];
      if (raw.length == 0) {
         return samples;
      } else {
         head = Math.floorMod(head, raw.length);

         for(int i = 0; i < raw.length; ++i) {
            int index = (head + i) % raw.length;
            samples[i] = (double)raw[index] * maxValue;
         }

         return samples;
      }
   }

   private List<Double> toList(double[] samples) {
      List<Double> result = new ArrayList<>(samples.length);

      for(double sample : samples) {
         result.add(sample);
      }

      return result;
   }

   @LuaFunction(
      mainThread = true
   )
   public final List<Double> getSamples() {
      return this.toList(this.readOrderedSamples());
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCurrent() {
      double[] samples = this.readOrderedSamples();
      return samples.length == 0 ? (double)0.0F : samples[samples.length - 1];
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getMinimum() {
      double[] samples = this.readOrderedSamples();
      if (samples.length == 0) {
         return (double)0.0F;
      } else {
         double minimum = samples[0];

         for(double sample : samples) {
            minimum = Math.min(minimum, sample);
         }

         return minimum;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getMaximum() {
      double[] samples = this.readOrderedSamples();
      if (samples.length == 0) {
         return (double)0.0F;
      } else {
         double maximum = samples[0];

         for(double sample : samples) {
            maximum = Math.max(maximum, sample);
         }

         return maximum;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getAverage() {
      double[] samples = this.readOrderedSamples();
      if (samples.length == 0) {
         return (double)0.0F;
      } else {
         double total = (double)0.0F;

         for(double sample : samples) {
            total += sample;
         }

         return total / (double)samples.length;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPeakToPeak() {
      return this.getMaximum() - this.getMinimum();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getAmplitude() {
      double[] samples = this.readOrderedSamples();
      if (samples.length == 0) {
         return (double)0.0F;
      } else {
         double average = this.getAverage();
         double amplitude = (double)0.0F;

         for(double sample : samples) {
            amplitude = Math.max(amplitude, Math.abs(sample - average));
         }

         return amplitude;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getSampleCount() {
      return this.readRawBuffer().length;
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getHistoryLength() {
      return (double)this.getSampleCount() / (double)20.0F;
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getVoltageRange() {
      return this.readMaxValue();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getSpeed() {
      return (double)this.plotter.getSpeed();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isRunning() {
      return this.plotter.isSpeedRequirementFulfilled();
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getUnit() {
      return "V";
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      double[] samples = this.readOrderedSamples();
      Map<String, Object> data = new HashMap<>();
      data.put("current", samples.length == 0 ? (double)0.0F : samples[samples.length - 1]);
      data.put("minimum", this.getMinimum());
      data.put("maximum", this.getMaximum());
      data.put("average", this.getAverage());
      data.put("amplitude", this.getAmplitude());
      data.put("peakToPeak", this.getPeakToPeak());
      data.put("sampleCount", samples.length);
      data.put("historyLength", (double)samples.length / (double)20.0F);
      data.put("voltageRange", this.readMaxValue());
      data.put("speed", (double)this.plotter.getSpeed());
      data.put("running", this.plotter.isSpeedRequirementFulfilled());
      data.put("unit", "V");
      data.put("samples", this.toList(samples));
      return data;
   }

   public Object getTarget() {
      return this.plotter;
   }

   public boolean equals(IPeripheral other) {
      boolean var10000;
      if (other instanceof PlotterPeripheral peripheral) {
         if (peripheral.plotter == this.plotter) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   static {
      try {
         SAMPLE_BUFFER_FIELD = PlotterBlockEntity.class.getDeclaredField("sampleBuffer");
         SAMPLE_BUFFER_FIELD.setAccessible(true);
         HEAD_FIELD = PlotterBlockEntity.class.getDeclaredField("head");
         HEAD_FIELD.setAccessible(true);
         MAX_VALUE_FIELD = PlotterBlockEntity.class.getDeclaredField("maxValue");
         MAX_VALUE_FIELD.setAccessible(true);
      } catch (NoSuchFieldException e) {
         throw new ExceptionInInitializerError(e);
      }
   }
}
