package me.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.Map;
import java.util.Set;
import net.minecraft.network.chat.Component;
import org.patryk3211.powergrid.electricity.sim.SwitchedWire;
import org.patryk3211.powergrid.equipment.portablebattery.PortableBatteryBlockEntity;

public class PortableBatteryPeripheral extends AbstractElectricPeripheral<PortableBatteryBlockEntity> {
   public PortableBatteryPeripheral(PortableBatteryBlockEntity battery) {
      super(battery);
   }

   public String getType() {
      return "powergrid_portable_battery";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_electric", "powergrid_battery", "powergrid_energy_storage");
   }

   private SwitchedWire getWire() {
      return (SwitchedWire)ReflectionHelper.getField(this.target, "wire", SwitchedWire.class);
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getCharge() {
      return ((PortableBatteryBlockEntity)this.target).getCharge();
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getMaxCharge() {
      return ((Number)ReflectionHelper.getField(this.target, "maxCharge")).intValue();
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getCapacityLevel() {
      return ((Number)ReflectionHelper.getField(this.target, "capacityLevel")).intValue();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getChargeLevel() {
      int maximum = this.getMaxCharge();
      return maximum <= 0 ? (double)0.0F : (double)this.getCharge() / (double)maximum;
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
   public final String getName() {
      Component name = ((PortableBatteryBlockEntity)this.target).m_5446_();
      return name == null ? "" : name.getString();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCurrent() {
      return this.getWire().current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getVoltage() {
      return this.getWire().potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPower() {
      return this.getWire().power();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isConnected() {
      return this.getWire().getState();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isCharging() {
      return this.getPower() < (double)0.0F;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isDischarging() {
      return this.getPower() > (double)0.0F;
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = this.createBaseData();
      data.put("name", this.getName());
      data.put("charge", this.getCharge());
      data.put("maxCharge", this.getMaxCharge());
      data.put("chargeLevel", this.getChargeLevel());
      data.put("chargePercent", this.getChargePercent());
      data.put("capacityLevel", this.getCapacityLevel());
      data.put("current", this.getCurrent());
      data.put("voltage", this.getVoltage());
      data.put("power", this.getPower());
      data.put("connected", this.isConnected());
      data.put("charging", this.isCharging());
      data.put("discharging", this.isDischarging());
      return data;
   }
}
