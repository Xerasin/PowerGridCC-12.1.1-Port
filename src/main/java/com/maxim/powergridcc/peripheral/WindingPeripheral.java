package com.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import org.patryk3211.powergrid.electricity.sim.special.LRSeriesWire;
import org.patryk3211.powergrid.kinetics.generator.winding.WindingBlockEntity;

public class WindingPeripheral extends AbstractElectricPeripheral<WindingBlockEntity> {
   public WindingPeripheral(WindingBlockEntity winding) {
      super(winding);
   }

   public String getType() {
      return "powergrid_generator_winding";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_electric", "powergrid_generator", "powergrid_winding");
   }

   private WindingBlockEntity getMainWinding() {
      Object value = ReflectionHelper.getField(this.target, "mainBE");
      if (value instanceof WindingBlockEntity main) {
         return main;
      } else {
         return this.target;
      }
   }

   private LRSeriesWire getCoilWire() {
      WindingBlockEntity main = this.getMainWinding();
      Object value = ReflectionHelper.getField(main, "coilWire");
      LRSeriesWire var10000;
      if (value instanceof LRSeriesWire wire) {
         var10000 = wire;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   private double readNumberField(Object object, String fieldName) {
      if (object == null) {
         return (double)0.0F;
      } else {
         Object value = ReflectionHelper.getField(object, fieldName);
         double var10000;
         if (value instanceof Number) {
            Number number = (Number)value;
            var10000 = number.doubleValue();
         } else {
            var10000 = (double)0.0F;
         }

         return var10000;
      }
   }

   private int readIntField(Object object, String fieldName) {
      if (object == null) {
         return 0;
      } else {
         Object value = ReflectionHelper.getField(object, fieldName);
         int var10000;
         if (value instanceof Number) {
            Number number = (Number)value;
            var10000 = number.intValue();
         } else {
            var10000 = 0;
         }

         return var10000;
      }
   }

   private int getSetSize(Object object, String fieldName) {
      if (object == null) {
         return 0;
      } else {
         Object value = ReflectionHelper.getField(object, fieldName);
         int var10000;
         if (value instanceof Set) {
            Set<?> set = (Set<?>)value;
            var10000 = set.size();
         } else {
            var10000 = 0;
         }

         return var10000;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isMain() {
      return this.getMainWinding() == this.target;
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getCoilCount() {
      WindingBlockEntity main = this.getMainWinding();
      int total = this.readIntField(main, "totalCoilCount");
      if (total > 0) {
         return total;
      } else {
         try {
            return main.getCoilCount();
         } catch (Exception var4) {
            return 0;
         }
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getTotalCoilCount() {
      WindingBlockEntity main = this.getMainWinding();
      int reflected = this.readIntField(main, "totalCoilCount");
      return reflected > 0 ? reflected : main.getCoilCount();
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getParallelCount() {
      WindingBlockEntity main = this.getMainWinding();
      int parallelPositions = this.getSetSize(main, "parallelPositions");
      return Math.max(1, parallelPositions + 1);
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getCollectedPartCount() {
      WindingBlockEntity main = this.getMainWinding();
      int collected = this.getSetSize(main, "collectedBEs");
      return Math.max(1, collected);
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getResistance() {
      LRSeriesWire wire = this.getCoilWire();
      return wire != null ? this.readNumberField(wire, "resistance") : this.readNumberField(this.getMainWinding(), "resistance");
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getInductance() {
      return this.readNumberField(this.getCoilWire(), "inductance");
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCurrent() {
      WindingBlockEntity main = this.getMainWinding();
      return (double)main.windingCurrent();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getVoltage() {
      LRSeriesWire wire = this.getCoilWire();
      return wire == null ? (double)0.0F : wire.potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPower() {
      return this.getVoltage() * this.getCurrent();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getResistivePower() {
      double current = this.getCurrent();
      return current * current * this.getResistance();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getFieldStrength() {
      WindingBlockEntity main = this.getMainWinding();

      try {
         Float value = (Float)main.fieldStrengthCalc().get();
         return value == null ? (double)0.0F : value.doubleValue();
      } catch (Exception var3) {
         return (double)0.0F;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCoilConstant() {
      return (double)WindingBlockEntity.coilConstant();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean hasCoilWire() {
      return this.getCoilWire() != null;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isActive() {
      return Math.abs(this.getCurrent()) > 1.0E-6 || Math.abs(this.getVoltage()) > 1.0E-6;
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getMainPosition() {
      WindingBlockEntity main = this.getMainWinding();
      BlockPos position = main.getBlockPos();
      Map<String, Object> result = new HashMap<>();
      result.put("x", position.getX());
      result.put("y", position.getY());
      result.put("z", position.getZ());
      return result;
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = this.createBaseData();
      data.put("main", this.isMain());
      data.put("mainPosition", this.getMainPosition());
      data.put("coilCount", this.getCoilCount());
      data.put("totalCoilCount", this.getTotalCoilCount());
      data.put("parallelCount", this.getParallelCount());
      data.put("collectedPartCount", this.getCollectedPartCount());
      data.put("resistance", this.getResistance());
      data.put("inductance", this.getInductance());
      data.put("current", this.getCurrent());
      data.put("voltage", this.getVoltage());
      data.put("power", this.getPower());
      data.put("resistivePower", this.getResistivePower());
      data.put("fieldStrength", this.getFieldStrength());
      data.put("coilConstant", this.getCoilConstant());
      data.put("coilWirePresent", this.hasCoilWire());
      data.put("active", this.isActive());
      return data;
   }
}
