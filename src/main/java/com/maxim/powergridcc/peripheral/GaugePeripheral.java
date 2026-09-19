package com.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.Set;
import org.patryk3211.powergrid.electricity.gauge.CurrentGaugeBlockEntity;
import org.patryk3211.powergrid.electricity.gauge.GaugeBlockEntity;
import org.patryk3211.powergrid.electricity.gauge.PowerGaugeBlockEntity;
import org.patryk3211.powergrid.electricity.gauge.VoltageGaugeBlockEntity;

public class GaugePeripheral extends AbstractPowerGridPeripheral<GaugeBlockEntity> {
   public GaugePeripheral(GaugeBlockEntity gauge) {
      super(gauge);
   }

   public String getType() {
      if (this.target instanceof VoltageGaugeBlockEntity) {
         return "powergrid_voltmeter";
      } else if (this.target instanceof CurrentGaugeBlockEntity) {
         return "powergrid_ammeter";
      } else {
         return this.target instanceof PowerGaugeBlockEntity ? "powergrid_wattmeter" : "powergrid_gauge";
      }
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_gauge");
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getValue() {
      return (double)((GaugeBlockEntity)this.target).getValue();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getMaxValue() {
      return (double)((GaugeBlockEntity)this.target).getMaxValue();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getProgress() {
      return (double)((GaugeBlockEntity)this.target).getProgress();
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getUnit() {
      return ((GaugeBlockEntity)this.target).getUnit().string();
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getMeterType() {
      if (this.target instanceof VoltageGaugeBlockEntity) {
         return "voltage";
      } else if (this.target instanceof CurrentGaugeBlockEntity) {
         return "current";
      } else {
         return this.target instanceof PowerGaugeBlockEntity ? "power" : "unknown";
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getVoltage() {
      if (!(this.target instanceof VoltageGaugeBlockEntity)) {
         throw new IllegalStateException("This peripheral is not a voltmeter");
      } else {
         return (double)((GaugeBlockEntity)this.target).getValue();
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCurrent() {
      if (!(this.target instanceof CurrentGaugeBlockEntity)) {
         throw new IllegalStateException("This peripheral is not an ammeter");
      } else {
         return (double)((GaugeBlockEntity)this.target).getValue();
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPower() {
      if (!(this.target instanceof PowerGaugeBlockEntity)) {
         throw new IllegalStateException("This peripheral is not a wattmeter");
      } else {
         return (double)((GaugeBlockEntity)this.target).getValue();
      }
   }
}
