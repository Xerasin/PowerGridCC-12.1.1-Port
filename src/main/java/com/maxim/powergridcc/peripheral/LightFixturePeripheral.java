package me.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import org.patryk3211.powergrid.electricity.light.bulb.LightBulbState;
import org.patryk3211.powergrid.electricity.light.fixture.LightFixtureBlockEntity;
import org.patryk3211.powergrid.electricity.sim.SwitchedWire;

public class LightFixturePeripheral extends AbstractElectricPeripheral<LightFixtureBlockEntity> {
   public LightFixturePeripheral(LightFixtureBlockEntity fixture) {
      super(fixture);
   }

   public String getType() {
      return "powergrid_light_fixture";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_electric", "powergrid_light");
   }

   private LightBulbState getBulb() {
      return ((LightFixtureBlockEntity)this.target).getBulbState();
   }

   private SwitchedWire getFilament() {
      return ((LightFixtureBlockEntity)this.target).getFilament();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean hasBulb() {
      return this.getBulb() != null;
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getBulbItem() {
      LightBulbState bulb = this.getBulb();
      return bulb == null ? "" : BuiltInRegistries.f_257033_.m_7981_(bulb.getItem()).toString();
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getColor() {
      LightBulbState bulb = this.getBulb();
      return bulb != null && bulb.getColor() != null ? bulb.getColor().m_41065_() : "";
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getPowerLevel() {
      LightBulbState bulb = this.getBulb();
      return bulb == null ? 0 : bulb.getPowerLevel();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isOn() {
      return this.hasBulb() && !this.isBurned() && this.getPowerLevel() > 0;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isBurned() {
      LightBulbState bulb = this.getBulb();
      return bulb != null && bulb.isBurned();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isBulbOverheated() {
      LightBulbState bulb = this.getBulb();
      return bulb != null && bulb.isOverheated();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getBulbResistance() {
      LightBulbState bulb = this.getBulb();
      return bulb == null ? (double)0.0F : (double)bulb.resistance();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCurrent() {
      SwitchedWire filament = this.getFilament();
      return filament == null ? (double)0.0F : filament.current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getVoltage() {
      SwitchedWire filament = this.getFilament();
      return filament == null ? (double)0.0F : filament.potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPower() {
      SwitchedWire filament = this.getFilament();
      return filament == null ? (double)0.0F : filament.power();
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = this.createBaseData();
      data.put("hasBulb", this.hasBulb());
      data.put("on", this.isOn());
      data.put("burned", this.isBurned());
      data.put("bulbOverheated", this.isBulbOverheated());
      data.put("powerLevel", this.getPowerLevel());
      data.put("current", this.getCurrent());
      data.put("voltage", this.getVoltage());
      data.put("power", this.getPower());
      if (this.hasBulb()) {
         data.put("bulbItem", this.getBulbItem());
         data.put("color", this.getColor());
         data.put("bulbResistance", this.getBulbResistance());
      }

      return data;
   }
}
