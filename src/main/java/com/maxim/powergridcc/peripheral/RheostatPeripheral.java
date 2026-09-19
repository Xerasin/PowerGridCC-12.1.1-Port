package com.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.maxim.powergridcc.control.TunedControlManager;
import org.patryk3211.powergrid.electricity.base.ThermalBehaviour;
import org.patryk3211.powergrid.kinetics.base.TunedBlockEntity;
import org.patryk3211.powergrid.kinetics.rheostat.RheostatBlockEntity;

public class RheostatPeripheral extends AbstractPowerGridPeripheral<RheostatBlockEntity> {
   public RheostatPeripheral(RheostatBlockEntity rheostat) {
      super(rheostat);
   }

   public String getType() {
      return "powergrid_rheostat";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_electric", "powergrid_tuned");
   }

   private ThermalBehaviour getThermalBehaviour() {
      return (ThermalBehaviour)((RheostatBlockEntity)this.target).getBehaviour(ThermalBehaviour.TYPE);
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getRatio() {
      return (double)((RheostatBlockEntity)this.target).getRatio();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getResistance() {
      return (double)((RheostatBlockEntity)this.target).resistance();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isComputerControlled() {
      return TunedControlManager.isEnabled((TunedBlockEntity)this.target);
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getTargetRatio() {
      return TunedControlManager.getTargetRatio((TunedBlockEntity)this.target);
   }

   @LuaFunction(
      mainThread = true
   )
   public final void setComputerControl(boolean enabled) {
      if (enabled) {
         TunedControlManager.enable((TunedBlockEntity)this.target, this.getRatio());
      } else {
         TunedControlManager.disable((TunedBlockEntity)this.target);
      }

   }

   @LuaFunction(
      mainThread = true
   )
   public final void setRatio(double ratio) throws LuaException {
      if (!Double.isFinite(ratio)) {
         throw new LuaException("Ratio must be a finite number");
      } else if (!(ratio < 0.01) && !(ratio > 0.99)) {
         if (!this.isComputerControlled()) {
            throw new LuaException("Computer control is disabled. Call setComputerControl(true) first");
         } else {
            TunedControlManager.setRatio((TunedBlockEntity)this.target, ratio);
         }
      } else {
         throw new LuaException("Rheostat ratio must be between 0.01 and 0.99");
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final void hold() {
      TunedControlManager.enable((TunedBlockEntity)this.target, this.getRatio());
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
   public final Map<String, Object> getData() {
      Map<String, Object> data = new HashMap<>();
      data.put("type", this.getType());
      data.put("ratio", this.getRatio());
      data.put("resistance", this.getResistance());
      data.put("temperature", this.getTemperature());
      data.put("overheated", this.isOverheated());
      data.put("computerControlled", this.isComputerControlled());
      data.put("targetRatio", this.getTargetRatio());
      return data;
   }
}
