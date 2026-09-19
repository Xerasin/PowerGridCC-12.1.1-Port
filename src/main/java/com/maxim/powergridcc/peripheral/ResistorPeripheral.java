package me.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import java.util.Map;
import org.patryk3211.powergrid.electricity.resistor.ResistorBlockEntity;
import org.patryk3211.powergrid.electricity.resistor.ResistorValueBehaviour;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;

public class ResistorPeripheral extends AbstractElectricPeripheral<ResistorBlockEntity> {
   public ResistorPeripheral(ResistorBlockEntity resistor) {
      super(resistor);
   }

   public String getType() {
      return "powergrid_resistor";
   }

   private ResistorValueBehaviour getValueBehaviour() {
      return (ResistorValueBehaviour)ReflectionHelper.getField(this.target, "value", ResistorValueBehaviour.class);
   }

   private ElectricWire getWire() {
      return (ElectricWire)ReflectionHelper.getField(this.target, "wire", ElectricWire.class);
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getResistance() {
      return (double)this.getValueBehaviour().getResistance();
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getControlValue() {
      return this.getValueBehaviour().value;
   }

   @LuaFunction(
      mainThread = true
   )
   public final void setControlValue(int value) throws LuaException {
      if (value >= 0 && value <= 72) {
         ResistorValueBehaviour behaviour = this.getValueBehaviour();
         behaviour.value = value;
         ElectricWire wire = this.getWire();
         wire.setResistance((double)behaviour.getResistance());
         ((ResistorBlockEntity)this.target).m_6596_();
         ((ResistorBlockEntity)this.target).notifyUpdate();
      } else {
         throw new LuaException("Value must be between 0 and 72");
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getWireResistance() {
      return this.getWire().getResistance();
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
   public final double getPower() {
      double current = this.getCurrent();
      return current * current * this.getResistance();
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = this.createBaseData();
      data.put("resistance", this.getResistance());
      data.put("wireResistance", this.getWireResistance());
      data.put("current", this.getCurrent());
      data.put("power", this.getPower());
      data.put("controlValue", this.getControlValue());
      return data;
   }
}
