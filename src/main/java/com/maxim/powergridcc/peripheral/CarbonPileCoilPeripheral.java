package me.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.Map;
import java.util.Set;
import org.patryk3211.powergrid.electricity.carbonpile.CarbonPileCoilBlockEntity;
import org.patryk3211.powergrid.electricity.sim.AbstractElectricWire;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;
import org.patryk3211.powergrid.electricity.sim.SwitchedWire;

public class CarbonPileCoilPeripheral extends AbstractElectricPeripheral<CarbonPileCoilBlockEntity> {
   public CarbonPileCoilPeripheral(CarbonPileCoilBlockEntity coil) {
      super(coil);
   }

   public String getType() {
      return "powergrid_carbon_pile_coil";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_electric", "powergrid_resistor", "powergrid_control_device");
   }

   private ElectricWire getCoilWire() {
      return (ElectricWire)ReflectionHelper.getField(this.target, "coil", ElectricWire.class);
   }

   private SwitchedWire getPileWireInternal() {
      return (SwitchedWire)ReflectionHelper.getField(this.target, "pile", SwitchedWire.class);
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getTrim() {
      return (double)((CarbonPileCoilBlockEntity)this.target).getTrim();
   }

   @LuaFunction(
      mainThread = true
   )
   public final void setTrim(double trim) {
      if (!Double.isFinite(trim)) {
         throw new IllegalArgumentException("Trim must be a finite number");
      } else {
         double clamped = Math.max((double)0.0F, Math.min((double)1.0F, trim));
         ((CarbonPileCoilBlockEntity)this.target).setTrim((float)clamped);
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getBaseResistance() {
      return ((Number)ReflectionHelper.getField(this.target, "baseResistance")).doubleValue();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCoilPull() {
      return ((Number)ReflectionHelper.getField(this.target, "coilPull")).doubleValue();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCoilCurrent() {
      return this.getCoilWire().current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCoilResistance() {
      return this.getCoilWire().getResistance();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPileCurrent() {
      return this.getPileWireInternal().current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPileResistance() {
      return this.getPileWireInternal().getResistance();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isPileConducting() {
      return this.getPileWireInternal().getState();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getEffectiveResistance() {
      AbstractElectricWire wire = ((CarbonPileCoilBlockEntity)this.target).getPileWire();
      if (wire instanceof ElectricWire electricWire) {
         return electricWire.getResistance();
      } else {
         return this.getPileResistance();
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = this.createBaseData();
      data.put("trim", this.getTrim());
      data.put("baseResistance", this.getBaseResistance());
      data.put("coilPull", this.getCoilPull());
      data.put("coilCurrent", this.getCoilCurrent());
      data.put("coilResistance", this.getCoilResistance());
      data.put("pileCurrent", this.getPileCurrent());
      data.put("pileResistance", this.getPileResistance());
      data.put("effectiveResistance", this.getEffectiveResistance());
      data.put("pileConducting", this.isPileConducting());
      return data;
   }
}
