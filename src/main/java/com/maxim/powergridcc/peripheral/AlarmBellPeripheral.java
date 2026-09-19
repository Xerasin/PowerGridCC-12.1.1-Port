package me.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.Map;
import java.util.Set;
import org.patryk3211.powergrid.electricity.bell.AlarmBellBlockEntity;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;

public class AlarmBellPeripheral extends AbstractElectricPeripheral<AlarmBellBlockEntity> {
   public AlarmBellPeripheral(AlarmBellBlockEntity bell) {
      super(bell);
   }

   public String getType() {
      return "powergrid_alarm_bell";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_electric", "powergrid_alarm");
   }

   private ElectricWire getWire() {
      return (ElectricWire)ReflectionHelper.getField(this.target, "wire", ElectricWire.class);
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getVolume() {
      return (double)((AlarmBellBlockEntity)this.target).getVolume();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPitch() {
      return (double)((AlarmBellBlockEntity)this.target).getPitch();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isRinging() {
      return this.getVolume() > 1.0E-6;
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
      data.put("volume", this.getVolume());
      data.put("pitch", this.getPitch());
      data.put("ringing", this.isRinging());
      data.put("voltage", this.getVoltage());
      data.put("current", this.getCurrent());
      data.put("power", this.getPower());
      data.put("resistance", this.getResistance());
      return data;
   }
}
