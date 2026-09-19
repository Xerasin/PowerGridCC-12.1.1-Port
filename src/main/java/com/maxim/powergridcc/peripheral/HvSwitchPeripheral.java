package me.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import me.maxim.powergridcc.control.HvSwitchControlManager;
import net.createmod.catnip.animation.LerpedFloat;
import org.patryk3211.powergrid.electricity.electricswitch.HvSwitchBlockEntity;
import org.patryk3211.powergrid.electricity.sim.SwitchedWire;

public class HvSwitchPeripheral extends AbstractPowerGridPeripheral<HvSwitchBlockEntity> {
   private final HvSwitchBlockEntity hvSwitch;

   public HvSwitchPeripheral(HvSwitchBlockEntity hvSwitch) {
      super(hvSwitch);
      this.hvSwitch = hvSwitch;
   }

   public String getType() {
      return "powergrid_hv_switch";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_switch", "powergrid_machine");
   }

   private Object readField(String name) {
      Class<?> current = this.hvSwitch.getClass();

      while(current != null) {
         try {
            Field field = current.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(this.hvSwitch);
         } catch (NoSuchFieldException var4) {
            current = current.getSuperclass();
         } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to read HV switch field: " + name, e);
         }
      }

      throw new IllegalStateException("HV switch field not found: " + name);
   }

   private SwitchedWire getWire() {
      Object value = this.readField("wire");
      if (value instanceof SwitchedWire wire) {
         return wire;
      } else {
         return null;
      }
   }

   private LerpedFloat getRod() {
      Object value = this.readField("rod");
      if (value instanceof LerpedFloat rod) {
         return rod;
      } else {
         return null;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isClosed() {
      return this.hvSwitch.isClosed();
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
   public final boolean isComputerControlled() {
      return HvSwitchControlManager.isEnabled(this.hvSwitch);
   }

   @LuaFunction(
      mainThread = true
   )
   public final void setComputerControl(boolean enabled) {
      if (enabled) {
         HvSwitchControlManager.enable(this.hvSwitch, this.isClosed());
      } else {
         HvSwitchControlManager.disable(this.hvSwitch);
      }

   }

   @LuaFunction(
      mainThread = true
   )
   public final void close() {
      if (!this.isComputerControlled()) {
         HvSwitchControlManager.enable(this.hvSwitch, true);
      } else {
         HvSwitchControlManager.setState(this.hvSwitch, true);
      }

   }

   @LuaFunction(
      mainThread = true
   )
   public final void open() {
      if (!this.isComputerControlled()) {
         HvSwitchControlManager.enable(this.hvSwitch, false);
      } else {
         HvSwitchControlManager.setState(this.hvSwitch, false);
      }

   }

   @LuaFunction(
      mainThread = true
   )
   public final void toggle() {
      if (this.isClosed()) {
         this.open();
      } else {
         this.close();
      }

   }

   @LuaFunction(
      mainThread = true
   )
   public final double getRodPosition() {
      LerpedFloat rod = this.getRod();
      return rod == null ? (double)0.0F : (double)rod.getValue();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getSpeed() {
      return (double)this.hvSwitch.getSpeed();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getResistance() {
      return (double)this.hvSwitch.getResistance();
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
   public final Map<String, Object> getData() {
      Map<String, Object> data = new HashMap();
      data.put("closed", this.isClosed());
      data.put("open", this.isOpen());
      data.put("computerControlled", this.isComputerControlled());
      data.put("rodPosition", this.getRodPosition());
      data.put("speed", this.getSpeed());
      data.put("resistance", this.getResistance());
      data.put("voltage", this.getVoltage());
      data.put("current", this.getCurrent());
      data.put("power", this.getPower());
      return data;
   }
}
