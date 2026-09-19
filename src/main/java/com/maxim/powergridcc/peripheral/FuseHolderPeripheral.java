package com.maxim.powergridcc.peripheral;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import dan200.computercraft.api.lua.LuaFunction;
import java.util.Map;
import java.util.Set;
import org.patryk3211.powergrid.electricity.fuse.FuseHolderBlockEntity;
import org.patryk3211.powergrid.electricity.fuse.FuseState;
import org.patryk3211.powergrid.electricity.sim.SwitchedWire;

public class FuseHolderPeripheral extends AbstractElectricPeripheral<FuseHolderBlockEntity> {
   public FuseHolderPeripheral(FuseHolderBlockEntity fuseHolder) {
      super(fuseHolder);
   }

   public String getType() {
      return "powergrid_fuse_holder";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_electric", "powergrid_protection_device");
   }

   private FuseState getFuseState() {
      return (FuseState)ReflectionHelper.getField(this.target, "state", FuseState.class);
   }

   private SwitchedWire getFuseWire() {
      return (SwitchedWire)ReflectionHelper.getField(this.target, "fuseWire", SwitchedWire.class);
   }

   private ScrollValueBehaviour getSetting() {
      return (ScrollValueBehaviour)ReflectionHelper.getField(this.target, "setting", ScrollValueBehaviour.class);
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getState() {
      FuseState state = this.getFuseState();
      return state == null ? "unknown" : state.name().toLowerCase();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isOpen() {
      return this.getFuseState() == FuseState.OPEN;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isClosed() {
      return this.getFuseState() == FuseState.CLOSED;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isBlown() {
      return this.getFuseState() == FuseState.BLOWN;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isConducting() {
      SwitchedWire wire = this.getFuseWire();
      return wire != null && wire.getState();
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getRatedCurrent() {
      ScrollValueBehaviour setting = this.getSetting();
      return setting == null ? 0 : setting.getValue();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getVoltage() {
      SwitchedWire wire = this.getFuseWire();
      return wire == null ? (double)0.0F : wire.potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCurrent() {
      SwitchedWire wire = this.getFuseWire();
      return wire == null ? (double)0.0F : wire.current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPower() {
      SwitchedWire wire = this.getFuseWire();
      return wire == null ? (double)0.0F : wire.power();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getResistance() {
      SwitchedWire wire = this.getFuseWire();
      return wire == null ? (double)0.0F : wire.getResistance();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean resetFuse() {
      return ((FuseHolderBlockEntity)this.target).resetFuse();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean removeBlownFuse() {
      return ((FuseHolderBlockEntity)this.target).removeBlown();
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = this.createBaseData();
      data.put("state", this.getState());
      data.put("open", this.isOpen());
      data.put("closed", this.isClosed());
      data.put("blown", this.isBlown());
      data.put("conducting", this.isConducting());
      data.put("ratedCurrent", this.getRatedCurrent());
      data.put("voltage", this.getVoltage());
      data.put("current", this.getCurrent());
      data.put("power", this.getPower());
      data.put("resistance", this.getResistance());
      return data;
   }
}
