package com.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.Map;
import java.util.Set;
import org.patryk3211.powergrid.electricity.grounding.GroundingRodBlockEntity;
import org.patryk3211.powergrid.electricity.sim.SwitchedWire;

public class GroundingRodPeripheral extends AbstractElectricPeripheral<GroundingRodBlockEntity> {
   public GroundingRodPeripheral(GroundingRodBlockEntity groundingRod) {
      super(groundingRod);
   }

   public String getType() {
      return "powergrid_grounding_rod";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_electric", "powergrid_ground");
   }

   private SwitchedWire getWire() {
      Object value = ReflectionHelper.getField(this.target, "wire");
      SwitchedWire var10000;
      if (value instanceof SwitchedWire wire) {
         var10000 = wire;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   private int readIntField(String fieldName) {
      Object value = ReflectionHelper.getField(this.target, fieldName);
      int var10000;
      if (value instanceof Number number) {
         var10000 = number.intValue();
      } else {
         var10000 = 0;
      }

      return var10000;
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getGroundCount() {
      return this.readIntField("groundCount");
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getConductiveCount() {
      return this.readIntField("conductiveCount");
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getDamageTickCounter() {
      return this.readIntField("damageTickCounter");
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCurrent() {
      SwitchedWire wire = this.getWire();
      return wire == null ? (double)0.0F : wire.current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getVoltage() {
      SwitchedWire wire = this.getWire();
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
   public final boolean hasGroundConnection() {
      return this.getGroundCount() > 0;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean hasConductiveGround() {
      return this.getConductiveCount() > 0;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isConnected() {
      return this.getWire() != null;
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
      data.put("groundCount", this.getGroundCount());
      data.put("conductiveCount", this.getConductiveCount());
      data.put("damageTickCounter", this.getDamageTickCounter());
      data.put("current", this.getCurrent());
      data.put("voltage", this.getVoltage());
      data.put("power", this.getPower());
      data.put("groundConnection", this.hasGroundConnection());
      data.put("conductiveGround", this.hasConductiveGround());
      data.put("connected", this.isConnected());
      data.put("active", this.isActive());
      return data;
   }
}
