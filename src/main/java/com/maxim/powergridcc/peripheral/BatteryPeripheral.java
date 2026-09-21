package com.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.util.HashMap;
import java.util.Map;
import org.patryk3211.powergrid.electricity.battery.BatteryBlockEntity;
import org.patryk3211.powergrid.electricity.battery.MultiBlockBatteryEntity;

public class BatteryPeripheral implements IPeripheral {
   private final BatteryBlockEntity battery;

   public BatteryPeripheral(BatteryBlockEntity battery) {
      this.battery = battery;
   }

   private BatteryBlockEntity getBattery() {
      if (this.battery instanceof MultiBlockBatteryEntity multiBlockBattery) {
         return multiBlockBattery.getControllerBE();
      }

      return this.battery;
   }

   public String getType() {
      return "powergrid_battery";
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getEnergy() {
      return this.getBattery().getEnergy();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCapacity() {
      return this.getBattery().getCapacity();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getChargeLevel() {
      BatteryBlockEntity battery = this.getBattery();
      double capacity = battery.getCapacity();
      return capacity <= (double)0.0F ? (double)0.0F : battery.getEnergy() / capacity;
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getChargePercent() {
      return this.getChargeLevel() * (double)100.0F;
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPower() {
      return (double)this.getBattery().calculatePower();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isCharging() {
      return this.getBattery().calculatePower() < 0.0F;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isDischarging() {
      return this.getBattery().calculatePower() > 0.0F;
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = new HashMap<>();
      BatteryBlockEntity battery = this.getBattery();
      double energy = battery.getEnergy();
      double capacity = battery.getCapacity();
      double level = capacity > (double)0.0F ? energy / capacity : (double)0.0F;
      double power = (double)battery.calculatePower();
      data.put("energy", energy);
      data.put("capacity", capacity);
      data.put("chargeLevel", level);
      data.put("chargePercent", level * (double)100.0F);
      data.put("power", power);
      data.put("charging", power < (double)0.0F);
      data.put("discharging", power > (double)0.0F);
      return data;
   }

   public Object getTarget() {
      return this.battery;
   }

   public boolean equals(IPeripheral other) {
      boolean var10000;
      if (other instanceof BatteryPeripheral peripheral) {
         if (peripheral.battery == this.battery) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }
}
