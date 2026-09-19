package me.maxim.powergridcc.peripheral;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import dan200.computercraft.api.lua.LuaFunction;
import java.util.Map;
import java.util.Set;
import org.patryk3211.powergrid.electricity.basinheater.BasinHeaterBlockEntity;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;

public class BasinHeaterPeripheral extends AbstractElectricPeripheral<BasinHeaterBlockEntity> {
   public BasinHeaterPeripheral(BasinHeaterBlockEntity heater) {
      super(heater);
   }

   public String getType() {
      return "powergrid_basin_heater";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_electric", "powergrid_heating_device");
   }

   private ElectricWire getCoil() {
      return (ElectricWire)ReflectionHelper.getField(this.target, "coil", ElectricWire.class);
   }

   private BlazeBurnerBlock.HeatLevel getHeatLevel() {
      return (BlazeBurnerBlock.HeatLevel)ReflectionHelper.getField(this.target, "state", BlazeBurnerBlock.HeatLevel.class);
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getState() {
      BlazeBurnerBlock.HeatLevel state = this.getHeatLevel();
      return state == null ? "none" : state.name().toLowerCase();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isKindled() {
      return this.getHeatLevel() == HeatLevel.KINDLED;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isSeething() {
      return this.getHeatLevel() == HeatLevel.SEETHING;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isActive() {
      BlazeBurnerBlock.HeatLevel state = this.getHeatLevel();
      return state != null && state != HeatLevel.NONE;
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getVoltage() {
      ElectricWire coil = this.getCoil();
      return coil == null ? (double)0.0F : coil.potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCurrent() {
      ElectricWire coil = this.getCoil();
      return coil == null ? (double)0.0F : coil.current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPower() {
      ElectricWire coil = this.getCoil();
      return coil == null ? (double)0.0F : coil.power();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getResistance() {
      ElectricWire coil = this.getCoil();
      return coil == null ? (double)0.0F : coil.getResistance();
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = this.createBaseData();
      data.put("state", this.getState());
      data.put("active", this.isActive());
      data.put("kindled", this.isKindled());
      data.put("seething", this.isSeething());
      data.put("voltage", this.getVoltage());
      data.put("current", this.getCurrent());
      data.put("power", this.getPower());
      data.put("resistance", this.getResistance());
      return data;
   }
}
