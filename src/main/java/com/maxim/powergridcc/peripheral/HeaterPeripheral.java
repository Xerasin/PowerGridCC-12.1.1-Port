package com.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.Map;
import java.util.Set;
import org.patryk3211.powergrid.electricity.heater.HeaterBlockEntity;
import org.patryk3211.powergrid.electricity.heater.HeaterBlockEntity.State;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;

public class HeaterPeripheral extends AbstractElectricPeripheral<HeaterBlockEntity> {
   public HeaterPeripheral(HeaterBlockEntity heater) {
      super(heater);
   }

   public String getType() {
      return "powergrid_heater";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_electric", "powergrid_heating_device");
   }

   private ElectricWire getWire() {
      return (ElectricWire)ReflectionHelper.getField(this.target, "wire", ElectricWire.class);
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getState() {
      return ((HeaterBlockEntity)this.target).getState().name().toLowerCase();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isCold() {
      return ((HeaterBlockEntity)this.target).getState() == State.COLD;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isSmoking() {
      return ((HeaterBlockEntity)this.target).getState() == State.SMOKING;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isBlasting() {
      return ((HeaterBlockEntity)this.target).getState() == State.BLASTING;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isActive() {
      return ((HeaterBlockEntity)this.target).getState() != State.COLD;
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
      data.put("state", this.getState());
      data.put("active", this.isActive());
      data.put("cold", this.isCold());
      data.put("smoking", this.isSmoking());
      data.put("blasting", this.isBlasting());
      data.put("voltage", this.getVoltage());
      data.put("current", this.getCurrent());
      data.put("power", this.getPower());
      data.put("resistance", this.getResistance());
      return data;
   }
}
