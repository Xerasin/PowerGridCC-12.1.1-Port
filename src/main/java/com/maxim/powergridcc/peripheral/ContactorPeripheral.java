package com.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.maxim.powergridcc.control.ContactorControlManager;
import org.patryk3211.powergrid.electricity.contactor.ContactorBlockEntity;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;
import org.patryk3211.powergrid.electricity.sim.SwitchedWire;

public class ContactorPeripheral extends AbstractPowerGridPeripheral<ContactorBlockEntity> {
   private final ContactorBlockEntity contactor;

   public ContactorPeripheral(ContactorBlockEntity contactor) {
      super(contactor);
      this.contactor = contactor;
   }

   public String getType() {
      return "powergrid_contactor";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_switch", "powergrid_machine");
   }

   private Object readField(String fieldName) {
      try {
         Field field = ContactorBlockEntity.class.getDeclaredField(fieldName);
         field.setAccessible(true);
         return field.get(this.contactor);
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException("Failed to read contactor field: " + fieldName, e);
      }
   }

   private ElectricWire getCoil() {
      Object value = this.readField("coil");
      if (value instanceof ElectricWire wire) {
         return wire;
      } else {
         return null;
      }
   }

   private SwitchedWire getSwitch1() {
      Object value = this.readField("switch1");
      if (value instanceof SwitchedWire wire) {
         return wire;
      } else {
         return null;
      }
   }

   private SwitchedWire getSwitch2() {
      Object value = this.readField("switch2");
      if (value instanceof SwitchedWire wire) {
         return wire;
      } else {
         return null;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isClosed() {
      return ContactorControlManager.readActualState(this.contactor);
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
      return ContactorControlManager.isEnabled(this.contactor);
   }

   @LuaFunction(
      mainThread = true
   )
   public final void setComputerControl(boolean enabled) {
      if (enabled) {
         ContactorControlManager.enable(this.contactor, this.isClosed());
      } else {
         ContactorControlManager.disable(this.contactor);
      }

   }

   @LuaFunction(
      mainThread = true
   )
   public final void close() {
      if (!this.isComputerControlled()) {
         ContactorControlManager.enable(this.contactor, true);
      } else {
         ContactorControlManager.setState(this.contactor, true);
      }

   }

   @LuaFunction(
      mainThread = true
   )
   public final void open() {
      if (!this.isComputerControlled()) {
         ContactorControlManager.enable(this.contactor, false);
      } else {
         ContactorControlManager.setState(this.contactor, false);
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
   public final double getCoilVoltage() {
      ElectricWire coil = this.getCoil();
      return coil == null ? (double)0.0F : coil.potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCoilCurrent() {
      ElectricWire coil = this.getCoil();
      return coil == null ? (double)0.0F : coil.current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCoilPower() {
      ElectricWire coil = this.getCoil();
      return coil == null ? (double)0.0F : coil.power();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCurrent1() {
      SwitchedWire wire = this.getSwitch1();
      return wire == null ? (double)0.0F : wire.current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getVoltage1() {
      SwitchedWire wire = this.getSwitch1();
      return wire == null ? (double)0.0F : wire.potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPower1() {
      SwitchedWire wire = this.getSwitch1();
      return wire == null ? (double)0.0F : wire.power();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCurrent2() {
      SwitchedWire wire = this.getSwitch2();
      return wire == null ? (double)0.0F : wire.current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getVoltage2() {
      SwitchedWire wire = this.getSwitch2();
      return wire == null ? (double)0.0F : wire.potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPower2() {
      SwitchedWire wire = this.getSwitch2();
      return wire == null ? (double)0.0F : wire.power();
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = new HashMap<>();
      data.put("closed", this.isClosed());
      data.put("open", this.isOpen());
      data.put("computerControlled", this.isComputerControlled());
      data.put("coilVoltage", this.getCoilVoltage());
      data.put("coilCurrent", this.getCoilCurrent());
      data.put("coilPower", this.getCoilPower());
      data.put("current1", this.getCurrent1());
      data.put("voltage1", this.getVoltage1());
      data.put("power1", this.getPower1());
      data.put("current2", this.getCurrent2());
      data.put("voltage2", this.getVoltage2());
      data.put("power2", this.getPower2());
      return data;
   }
}
