package me.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.patryk3211.powergrid.electricity.base.ThermalBehaviour;
import org.patryk3211.powergrid.electricity.carbonpile.CarbonPileBlockEntity;
import org.patryk3211.powergrid.electricity.carbonpile.CarbonPileCoilBlockEntity;
import org.patryk3211.powergrid.electricity.carbonpile.TrimValueBehaviour;

public class CarbonPilePeripheral extends AbstractPowerGridPeripheral<CarbonPileBlockEntity> {
   public CarbonPilePeripheral(CarbonPileBlockEntity carbonPile) {
      super(carbonPile);
   }

   public String getType() {
      return "powergrid_carbon_pile";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_resistor", "powergrid_thermal_device");
   }

   private ThermalBehaviour getThermalBehaviour() {
      return (ThermalBehaviour)ReflectionHelper.getField(this.target, "thermal", ThermalBehaviour.class);
   }

   private TrimValueBehaviour getTrimBehaviour() {
      return (TrimValueBehaviour)ReflectionHelper.getField(this.target, "trim", TrimValueBehaviour.class);
   }

   private CarbonPileCoilBlockEntity getCoil() {
      Object coil = ReflectionHelper.getField(this.target, "coil");
      if (coil == null) {
         return null;
      } else if (coil instanceof CarbonPileCoilBlockEntity) {
         CarbonPileCoilBlockEntity carbonPileCoil = (CarbonPileCoilBlockEntity)coil;
         return carbonPileCoil;
      } else {
         throw new IllegalStateException("Carbon pile coil has an unexpected type");
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getTemperature() {
      ThermalBehaviour thermal = this.getThermalBehaviour();
      return thermal == null ? (double)0.0F : (double)thermal.getTemperature();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isOverheated() {
      ThermalBehaviour thermal = this.getThermalBehaviour();
      return thermal != null && thermal.isOverheated();
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getSize() {
      return (Integer)ReflectionHelper.getField(this.target, "size");
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getTrimValue() {
      return this.getTrimBehaviour().getValue();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean hasCoil() {
      return this.getCoil() != null;
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCoilTrim() {
      CarbonPileCoilBlockEntity coil = this.getCoil();
      return coil == null ? (double)0.0F : (double)coil.getTrim();
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = new HashMap();
      data.put("type", this.getType());
      data.put("temperature", this.getTemperature());
      data.put("overheated", this.isOverheated());
      data.put("size", this.getSize());
      data.put("trimValue", this.getTrimValue());
      data.put("hasCoil", this.hasCoil());
      if (this.hasCoil()) {
         data.put("coilTrim", this.getCoilTrim());
      }

      return data;
   }
}
