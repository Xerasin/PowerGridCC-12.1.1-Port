package me.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.patryk3211.powergrid.electricity.base.ElectricBlockEntity;
import org.patryk3211.powergrid.electricity.base.ThermalBehaviour;

public abstract class AbstractElectricPeripheral<T extends ElectricBlockEntity> extends AbstractPowerGridPeripheral<T> {
   protected AbstractElectricPeripheral(T target) {
      super(target);
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_electric");
   }

   protected final ThermalBehaviour getThermalBehaviour() {
      return (ThermalBehaviour)((ElectricBlockEntity)this.target).getBehaviour(ThermalBehaviour.TYPE);
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

   protected Map<String, Object> createBaseData() {
      Map<String, Object> data = new HashMap();
      data.put("type", this.getType());
      data.put("temperature", this.getTemperature());
      data.put("overheated", this.isOverheated());
      return data;
   }
}
