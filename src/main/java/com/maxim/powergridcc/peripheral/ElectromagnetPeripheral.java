package com.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.Map;
import java.util.Set;
import org.patryk3211.powergrid.electricity.electromagnet.ElectromagnetBlockEntity;
import org.patryk3211.powergrid.electricity.electromagnet.MagnetizingBehaviour;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;

public class ElectromagnetPeripheral extends AbstractElectricPeripheral<ElectromagnetBlockEntity> {
   public ElectromagnetPeripheral(ElectromagnetBlockEntity electromagnet) {
      super(electromagnet);
   }

   public String getType() {
      return "powergrid_electromagnet";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_electric", "powergrid_magnetic_device");
   }

   private ElectricWire getWire() {
      return (ElectricWire)ReflectionHelper.getField(this.target, "wire", ElectricWire.class);
   }

   private MagnetizingBehaviour getMagnetizingBehaviour() {
      return ((ElectromagnetBlockEntity)this.target).getMagnetizingBehaviour();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getFieldStrength() {
      return (double)((ElectromagnetBlockEntity)this.target).getFieldStrength();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isActive() {
      return Math.abs(this.getFieldStrength()) > 1.0E-6;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isRunning() {
      MagnetizingBehaviour behaviour = this.getMagnetizingBehaviour();
      if (behaviour == null) {
         return false;
      } else {
         Object running = ReflectionHelper.getField(behaviour, "running");
         boolean var10000;
         if (running instanceof Boolean) {
            Boolean value = (Boolean)running;
            if (value) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getVoltage() {
      ElectricWire wire = this.getWire();
      return wire == null ? (double)0.0F : wire.potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCurrent() {
      ElectricWire wire = this.getWire();
      return wire == null ? (double)0.0F : wire.current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPower() {
      ElectricWire wire = this.getWire();
      return wire == null ? (double)0.0F : wire.power();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getResistance() {
      ElectricWire wire = this.getWire();
      return wire == null ? (double)0.0F : wire.getResistance();
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = this.createBaseData();
      data.put("fieldStrength", this.getFieldStrength());
      data.put("active", this.isActive());
      data.put("running", this.isRunning());
      data.put("voltage", this.getVoltage());
      data.put("current", this.getCurrent());
      data.put("power", this.getPower());
      data.put("resistance", this.getResistance());
      return data;
   }
}
