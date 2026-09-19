package me.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.patryk3211.powergrid.electricity.electricswitch.SwitchBlock;
import org.patryk3211.powergrid.electricity.electricswitch.SwitchBlockEntity;
import org.patryk3211.powergrid.electricity.sim.SwitchedWire;

public class ElectricSwitchPeripheral extends AbstractPowerGridPeripheral<SwitchBlockEntity> {
   private final SwitchBlockEntity electricSwitch;

   public ElectricSwitchPeripheral(SwitchBlockEntity electricSwitch) {
      super(electricSwitch);
      this.electricSwitch = electricSwitch;
   }

   public String getType() {
      return "powergrid_electric_switch";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_switch", "powergrid_machine");
   }

   private Field findField(String name) {
      try {
         Field field = SwitchBlockEntity.class.getDeclaredField(name);
         field.setAccessible(true);
         return field;
      } catch (NoSuchFieldException e) {
         throw new IllegalStateException("Field not found: " + name, e);
      }
   }

   private boolean readBoolean(String name) {
      try {
         return this.findField(name).getBoolean(this.electricSwitch);
      } catch (IllegalAccessException e) {
         throw new IllegalStateException("Failed to read switch field: " + name, e);
      }
   }

   private Object readObject(String name) {
      try {
         return this.findField(name).get(this.electricSwitch);
      } catch (IllegalAccessException e) {
         throw new IllegalStateException("Failed to read switch field: " + name, e);
      }
   }

   private SwitchedWire getWire() {
      Object value = this.readObject("wire");
      if (value instanceof SwitchedWire wire) {
         return wire;
      } else {
         return null;
      }
   }

   private void setClosedState(boolean closed) {
      Level level = this.electricSwitch.m_58904_();
      if (level == null) {
         this.electricSwitch.setState(closed);
      } else {
         BlockPos pos = this.electricSwitch.m_58899_();
         BlockState state = level.m_8055_(pos);
         Block var6 = state.m_60734_();
         if (var6 instanceof SwitchBlock) {
            SwitchBlock switchBlock = (SwitchBlock)var6;
            boolean open = !closed;
            boolean currentOpen = (Boolean)state.m_61143_(SwitchBlock.OPEN);
            if (currentOpen != open) {
               level.m_46597_(pos, (BlockState)state.m_61124_(SwitchBlock.OPEN, open));
               this.electricSwitch.setState(closed);
               switchBlock.useSound(level, pos, open);
            } else {
               this.electricSwitch.setState(closed);
            }

         } else {
            this.electricSwitch.setState(closed);
         }
      }
   }

   private void pressButton() {
      Level level = this.electricSwitch.m_58904_();
      if (level == null) {
         this.electricSwitch.setState(true);
      } else {
         BlockPos pos = this.electricSwitch.m_58899_();
         BlockState state = level.m_8055_(pos);
         Block var5 = state.m_60734_();
         if (var5 instanceof SwitchBlock) {
            SwitchBlock switchBlock = (SwitchBlock)var5;
            boolean currentlyOpen = (Boolean)state.m_61143_(SwitchBlock.OPEN);
            if (currentlyOpen) {
               level.m_46597_(pos, (BlockState)state.m_61124_(SwitchBlock.OPEN, false));
               switchBlock.useSound(level, pos, false);
            }

            this.electricSwitch.setState(true);
         } else {
            this.electricSwitch.setState(true);
         }
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isClosed() {
      return this.readBoolean("switchState");
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isOpen() {
      return !this.isClosed();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isButton() {
      return this.readBoolean("isButton");
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isDamaged() {
      return this.readObject("overvoltResistance") != null;
   }

   @LuaFunction(
      mainThread = true
   )
   public final void close() {
      if (this.isButton()) {
         this.pressButton();
      } else {
         this.setClosedState(true);
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final void open() {
      this.setClosedState(false);
   }

   @LuaFunction(
      mainThread = true
   )
   public final void toggle() {
      if (this.isButton()) {
         this.pressButton();
      } else {
         this.setClosedState(!this.isClosed());
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final void press() {
      if (this.isButton()) {
         this.pressButton();
      } else {
         this.setClosedState(true);
      }

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
   public final double getCurrent() {
      SwitchedWire wire = this.getWire();
      return wire == null ? (double)0.0F : wire.current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPower() {
      SwitchedWire wire = this.getWire();
      return wire == null ? (double)0.0F : wire.power();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getResistance() {
      SwitchedWire wire = this.getWire();
      return wire == null ? (double)0.0F : wire.getResistance();
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = new HashMap();
      data.put("closed", this.isClosed());
      data.put("open", this.isOpen());
      data.put("button", this.isButton());
      data.put("damaged", this.isDamaged());
      data.put("voltage", this.getVoltage());
      data.put("current", this.getCurrent());
      data.put("power", this.getPower());
      data.put("resistance", this.getResistance());
      return data;
   }
}
