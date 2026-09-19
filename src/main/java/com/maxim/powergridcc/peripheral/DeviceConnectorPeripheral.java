package me.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.Map;
import java.util.Set;
import org.patryk3211.powergrid.electricity.deviceconnector.DeviceConnectorBlockEntity;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;

public class DeviceConnectorPeripheral extends AbstractElectricPeripheral<DeviceConnectorBlockEntity> {
   public DeviceConnectorPeripheral(DeviceConnectorBlockEntity connector) {
      super(connector);
   }

   public String getType() {
      return "powergrid_device_connector";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_electric", "powergrid_connector");
   }

   private ElectricWire getConverterWire() {
      Object value = ReflectionHelper.getField(this.target, "converterWire");
      ElectricWire var10000;
      if (value instanceof ElectricWire wire) {
         var10000 = wire;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCurrent() {
      ElectricWire wire = this.getConverterWire();
      return wire == null ? (double)0.0F : wire.current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getVoltage() {
      ElectricWire wire = this.getConverterWire();
      return wire == null ? (double)0.0F : wire.potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPower() {
      ElectricWire wire = this.getConverterWire();
      return wire == null ? (double)0.0F : wire.power();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getResistance() {
      ElectricWire wire = this.getConverterWire();
      return wire == null ? (double)0.0F : wire.getResistance();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean hasConverterWire() {
      return this.getConverterWire() != null;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean hasProxyBehaviour() {
      return ReflectionHelper.getField(this.target, "proxyBehaviour") != null;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isConnected() {
      return this.hasConverterWire() && this.hasProxyBehaviour();
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
   public final Map<String, Object> getData() {
      Map<String, Object> data = this.createBaseData();
      data.put("current", this.getCurrent());
      data.put("voltage", this.getVoltage());
      data.put("power", this.getPower());
      data.put("resistance", this.getResistance());
      data.put("converterWirePresent", this.hasConverterWire());
      data.put("proxyBehaviourPresent", this.hasProxyBehaviour());
      data.put("connected", this.isConnected());
      data.put("active", this.isActive());
      return data;
   }
}
