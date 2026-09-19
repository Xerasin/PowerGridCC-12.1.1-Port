package com.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import java.util.Set;
import org.patryk3211.powergrid.electricity.creative.CreativeResistorBlockEntity;
import org.patryk3211.powergrid.electricity.resistor.ResistorValueBehaviour;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;

public class CreativeResistorPeripheral extends ResistorPeripheral {
   private final CreativeResistorBlockEntity resistor;

   public CreativeResistorPeripheral(CreativeResistorBlockEntity resistor) {
      super(resistor);
      this.resistor = resistor;
   }

   public String getType() {
      return "powergrid_creative_resistor";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_electric", "powergrid_resistor", "powergrid_creative");
   }

   private ResistorValueBehaviour getCreativeValueBehaviour() {
      return (ResistorValueBehaviour)ReflectionHelper.getField(this.resistor, "value", ResistorValueBehaviour.class);
   }

   private ElectricWire getCreativeWire() {
      return (ElectricWire)ReflectionHelper.getField(this.resistor, "wire", ElectricWire.class);
   }

   private void applyValue(int value) {
      ResistorValueBehaviour behaviour = this.getCreativeValueBehaviour();
      behaviour.setValue(value);
      ElectricWire wire = this.getCreativeWire();
      wire.setResistance((double)behaviour.getResistance());
      this.resistor.setChanged();
      this.resistor.notifyUpdate();
   }

   private int findClosestValue(double requestedResistance) {
      ResistorValueBehaviour behaviour = this.getCreativeValueBehaviour();
      int originalValue = behaviour.getValue();
      int closestValue = originalValue;
      double closestDifference = Double.POSITIVE_INFINITY;

      for(int value = 0; value <= 72; ++value) {
         behaviour.setValue(value);
         double resistance = (double)behaviour.getResistance();
         double difference = Math.abs(resistance - requestedResistance);
         if (difference < closestDifference) {
            closestDifference = difference;
            closestValue = value;
         }
      }

      behaviour.setValue(originalValue);
      return closestValue;
   }

   @LuaFunction(
      mainThread = true
   )
   public final void setResistance(double resistance) throws LuaException {
      if (!Double.isFinite(resistance)) {
         throw new LuaException("Resistance must be a finite number");
      } else if (resistance <= (double)0.0F) {
         throw new LuaException("Resistance must be greater than zero");
      } else {
         int closestValue = this.findClosestValue(resistance);
         this.applyValue(closestValue);
      }
   }
}
