package me.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.patryk3211.powergrid.electricity.crt.CRTBlockEntity;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;
import org.patryk3211.powergrid.electricity.sim.SwitchedWire;

public class CRTPeripheral extends AbstractElectricPeripheral<CRTBlockEntity> {
   public CRTPeripheral(CRTBlockEntity crt) {
      super(crt);
   }

   public String getType() {
      return "powergrid_crt";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_electric", "powergrid_display", "powergrid_oscilloscope");
   }

   private ElectricWire getXDeflectWire() {
      return (ElectricWire)ReflectionHelper.getField(this.target, "xDeflect", ElectricWire.class);
   }

   private ElectricWire getYDeflectWire() {
      return (ElectricWire)ReflectionHelper.getField(this.target, "yDeflect", ElectricWire.class);
   }

   private ElectricWire getHeaterWire() {
      return (ElectricWire)ReflectionHelper.getField(this.target, "heater", ElectricWire.class);
   }

   private ElectricWire getGridCathodeWire() {
      return (ElectricWire)ReflectionHelper.getField(this.target, "gridCathode", ElectricWire.class);
   }

   private SwitchedWire getAnodeCathodeWire() {
      return (SwitchedWire)ReflectionHelper.getField(this.target, "anodeCathode", SwitchedWire.class);
   }

   private float[] getXPointsInternal() {
      Object value = ReflectionHelper.getField(this.target, "xPoints");
      float[] var10000;
      if (value instanceof float[] points) {
         var10000 = points;
      } else {
         var10000 = new float[0];
      }

      return var10000;
   }

   private float[] getYPointsInternal() {
      Object value = ReflectionHelper.getField(this.target, "yPoints");
      float[] var10000;
      if (value instanceof float[] points) {
         var10000 = points;
      } else {
         var10000 = new float[0];
      }

      return var10000;
   }

   private float[] getBrightnessInternal() {
      Object value = ReflectionHelper.getField(this.target, "brightness");
      float[] var10000;
      if (value instanceof float[] points) {
         var10000 = points;
      } else {
         var10000 = new float[0];
      }

      return var10000;
   }

   private int getHeadInternal() {
      Object value = ReflectionHelper.getField(this.target, "head");
      int var10000;
      if (value instanceof Number number) {
         var10000 = number.intValue();
      } else {
         var10000 = 0;
      }

      return var10000;
   }

   private List<Double> toLuaList(float[] values) {
      List<Double> result = new ArrayList(values.length);

      for(float value : values) {
         result.add((double)value);
      }

      return result;
   }

   private Map<String, Object> createWireData(ElectricWire wire) {
      Map<String, Object> data = new HashMap();
      if (wire == null) {
         data.put("voltage", (double)0.0F);
         data.put("current", (double)0.0F);
         data.put("power", (double)0.0F);
         data.put("resistance", (double)0.0F);
         return data;
      } else {
         data.put("voltage", wire.potentialDifference());
         data.put("current", wire.current());
         data.put("power", wire.power());
         data.put("resistance", wire.getResistance());
         return data;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getSampleCount() {
      return CRTBlockEntity.sampleCount();
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getHead() {
      return this.getHeadInternal();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getXVoltage() {
      ElectricWire wire = this.getXDeflectWire();
      return wire == null ? (double)0.0F : wire.potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getXCurrent() {
      ElectricWire wire = this.getXDeflectWire();
      return wire == null ? (double)0.0F : wire.current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getYVoltage() {
      ElectricWire wire = this.getYDeflectWire();
      return wire == null ? (double)0.0F : wire.potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getYCurrent() {
      ElectricWire wire = this.getYDeflectWire();
      return wire == null ? (double)0.0F : wire.current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getHeaterVoltage() {
      ElectricWire wire = this.getHeaterWire();
      return wire == null ? (double)0.0F : wire.potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getHeaterCurrent() {
      ElectricWire wire = this.getHeaterWire();
      return wire == null ? (double)0.0F : wire.current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getHeaterPower() {
      ElectricWire wire = this.getHeaterWire();
      return wire == null ? (double)0.0F : wire.power();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getGridCathodeVoltage() {
      ElectricWire wire = this.getGridCathodeWire();
      return wire == null ? (double)0.0F : wire.potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getGridCathodeCurrent() {
      ElectricWire wire = this.getGridCathodeWire();
      return wire == null ? (double)0.0F : wire.current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getGridCathodePower() {
      ElectricWire wire = this.getGridCathodeWire();
      return wire == null ? (double)0.0F : wire.power();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getAnodeVoltage() {
      SwitchedWire wire = this.getAnodeCathodeWire();
      return wire == null ? (double)0.0F : wire.potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getAnodeCurrent() {
      SwitchedWire wire = this.getAnodeCathodeWire();
      return wire == null ? (double)0.0F : wire.current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getAnodePower() {
      SwitchedWire wire = this.getAnodeCathodeWire();
      return wire == null ? (double)0.0F : wire.power();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isBeamActive() {
      SwitchedWire wire = this.getAnodeCathodeWire();
      return wire != null && wire.getState() && Math.abs(wire.current()) > 1.0E-6;
   }

   @LuaFunction(
      mainThread = true
   )
   public final List<Double> getXPoints() {
      return this.toLuaList(this.getXPointsInternal());
   }

   @LuaFunction(
      mainThread = true
   )
   public final List<Double> getYPoints() {
      return this.toLuaList(this.getYPointsInternal());
   }

   @LuaFunction(
      mainThread = true
   )
   public final List<Double> getBrightness() {
      return this.toLuaList(this.getBrightnessInternal());
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getTrace() {
      Map<String, Object> trace = new HashMap();
      trace.put("x", this.getXPoints());
      trace.put("y", this.getYPoints());
      trace.put("brightness", this.getBrightness());
      trace.put("head", this.getHead());
      trace.put("sampleCount", this.getSampleCount());
      return trace;
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = this.createBaseData();
      data.put("xDeflection", this.createWireData(this.getXDeflectWire()));
      data.put("yDeflection", this.createWireData(this.getYDeflectWire()));
      data.put("heater", this.createWireData(this.getHeaterWire()));
      data.put("gridCathode", this.createWireData(this.getGridCathodeWire()));
      data.put("anodeCathode", this.createWireData(this.getAnodeCathodeWire()));
      data.put("beamActive", this.isBeamActive());
      data.put("head", this.getHead());
      data.put("sampleCount", this.getSampleCount());
      data.put("trace", this.getTrace());
      return data;
   }
}
