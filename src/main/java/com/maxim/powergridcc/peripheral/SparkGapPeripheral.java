package me.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.Map;
import java.util.Set;
import org.patryk3211.powergrid.electricity.sim.SwitchedWire;
import org.patryk3211.powergrid.electricity.sparkgap.SparkGapBlockEntity;
import org.patryk3211.powergrid.electricity.sparkgap.SparkGapValueBehaviour;

public class SparkGapPeripheral extends AbstractElectricPeripheral<SparkGapBlockEntity> {
   public SparkGapPeripheral(SparkGapBlockEntity sparkGap) {
      super(sparkGap);
   }

   public String getType() {
      return "powergrid_spark_gap";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_electric", "powergrid_protection_device");
   }

   private SparkGapValueBehaviour getSetting() {
      return (SparkGapValueBehaviour)ReflectionHelper.getField(this.target, "setting", SparkGapValueBehaviour.class);
   }

   private SwitchedWire getPlasmaChannel() {
      return (SwitchedWire)ReflectionHelper.getField(this.target, "plasmaChannel", SwitchedWire.class);
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getTriggerVoltage() {
      SparkGapValueBehaviour setting = this.getSetting();
      return setting == null ? 0 : setting.getVoltage();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getExtinguishCurrent() {
      SparkGapValueBehaviour setting = this.getSetting();
      return setting == null ? (double)0.0F : (double)setting.getCurrent();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getMaximumCurrent() {
      return this.getExtinguishCurrent();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isArcing() {
      SwitchedWire channel = this.getPlasmaChannel();
      return channel != null && channel.getState();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getVoltage() {
      SwitchedWire channel = this.getPlasmaChannel();
      return channel == null ? (double)0.0F : channel.potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCurrent() {
      SwitchedWire channel = this.getPlasmaChannel();
      return channel == null ? (double)0.0F : channel.current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPower() {
      SwitchedWire channel = this.getPlasmaChannel();
      return channel == null ? (double)0.0F : channel.power();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getResistance() {
      SwitchedWire channel = this.getPlasmaChannel();
      return channel == null ? (double)0.0F : channel.getResistance();
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = this.createBaseData();
      data.put("triggerVoltage", this.getTriggerVoltage());
      data.put("extinguishCurrent", this.getExtinguishCurrent());
      data.put("maximumCurrent", this.getMaximumCurrent());
      data.put("arcing", this.isArcing());
      data.put("voltage", this.getVoltage());
      data.put("current", this.getCurrent());
      data.put("power", this.getPower());
      data.put("resistance", this.getResistance());
      return data;
   }
}
