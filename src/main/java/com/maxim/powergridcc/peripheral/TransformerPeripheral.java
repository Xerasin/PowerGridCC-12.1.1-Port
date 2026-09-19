package com.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import org.patryk3211.powergrid.electricity.sim.ElectricWire;
import org.patryk3211.powergrid.electricity.sim.node.TransformerCoupling;
import org.patryk3211.powergrid.electricity.sim.special.SplitTransformerControllerWire;
import org.patryk3211.powergrid.electricity.transformer.TransformerBlockEntity;
import org.patryk3211.powergrid.electricity.transformer.TransformerCoilParameters;
import org.patryk3211.powergrid.electricity.transformer.TransformerMediumBlockEntity;
import org.patryk3211.powergrid.electricity.transformer.TransformerSmallBlockEntity;

public class TransformerPeripheral extends AbstractElectricPeripheral<TransformerBlockEntity> {
   public TransformerPeripheral(TransformerBlockEntity transformer) {
      super(transformer);
   }

   public String getType() {
      if (this.target instanceof TransformerMediumBlockEntity) {
         return "powergrid_transformer_medium";
      } else {
         return this.target instanceof TransformerSmallBlockEntity ? "powergrid_transformer_small" : "powergrid_transformer";
      }
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_electric", "powergrid_transformer");
   }

   private ElectricWire getPrimaryStray() {
      Object value = ReflectionHelper.getField(this.target, "primaryStray");
      ElectricWire var10000;
      if (value instanceof ElectricWire wire) {
         var10000 = wire;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   private ElectricWire getMutualInductance() {
      Object value = ReflectionHelper.getField(this.target, "mutualInductance");
      ElectricWire var10000;
      if (value instanceof ElectricWire wire) {
         var10000 = wire;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   private TransformerCoupling getCoupling() {
      Object value = ReflectionHelper.getField(this.target, "coupling");
      TransformerCoupling var10000;
      if (value instanceof TransformerCoupling coupling) {
         var10000 = coupling;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   private SplitTransformerControllerWire getCouplingI1() {
      Object value = ReflectionHelper.getField(this.target, "couplingI1");
      SplitTransformerControllerWire var10000;
      if (value instanceof SplitTransformerControllerWire wire) {
         var10000 = wire;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   private SplitTransformerControllerWire getCouplingI2() {
      Object value = ReflectionHelper.getField(this.target, "couplingI2");
      SplitTransformerControllerWire var10000;
      if (value instanceof SplitTransformerControllerWire wire) {
         var10000 = wire;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   private TransformerCoilParameters getPrimaryCoil() {
      return ((TransformerBlockEntity)this.target).getPrimary();
   }

   private TransformerCoilParameters getSecondaryCoil() {
      return ((TransformerBlockEntity)this.target).getSecondary();
   }

   private Map<String, Object> createCoilData(TransformerCoilParameters coil) {
      Map<String, Object> data = new HashMap<>();
      boolean defined = coil != null && coil.isDefined();
      data.put("defined", defined);
      if (coil == null || !coil.isDefined()) {
         data.put("turns", 0);
         data.put("terminal1", -1);
         data.put("terminal2", -1);
         data.put("item", "");
         return data;
      } else {
         data.put("turns", coil.getTurns());
         data.put("terminal1", coil.getTerminal1());
         data.put("terminal2", coil.getTerminal2());
         var item = coil.getItem();
         if (item != null) {
            data.put("item", BuiltInRegistries.ITEM.getKey(item).toString());
         } else {
            data.put("item", "");
         }

         return data;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean hasPrimary() {
      return ((TransformerBlockEntity)this.target).hasPrimary();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean hasSecondary() {
      return ((TransformerBlockEntity)this.target).hasSecondary();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isComplete() {
      return this.hasPrimary() && this.hasSecondary();
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getPrimaryTurns() {
      TransformerCoilParameters coil = this.getPrimaryCoil();
      return coil != null && coil.isDefined() ? coil.getTurns() : 0;
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getSecondaryTurns() {
      TransformerCoilParameters coil = this.getSecondaryCoil();
      return coil != null && coil.isDefined() ? coil.getTurns() : 0;
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getPrimaryTerminal1() {
      TransformerCoilParameters coil = this.getPrimaryCoil();
      return coil != null && coil.isDefined() ? coil.getTerminal1() : -1;
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getPrimaryTerminal2() {
      TransformerCoilParameters coil = this.getPrimaryCoil();
      return coil != null && coil.isDefined() ? coil.getTerminal2() : -1;
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getSecondaryTerminal1() {
      TransformerCoilParameters coil = this.getSecondaryCoil();
      return coil != null && coil.isDefined() ? coil.getTerminal1() : -1;
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getSecondaryTerminal2() {
      TransformerCoilParameters coil = this.getSecondaryCoil();
      return coil != null && coil.isDefined() ? coil.getTerminal2() : -1;
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getPrimaryItem() {
      TransformerCoilParameters coil = this.getPrimaryCoil();
      Item item = coil != null && coil.isDefined() ? coil.getItem() : null;
      return coil != null && coil.isDefined() && item != null ? BuiltInRegistries.ITEM.getKey(item).toString() : "";
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getSecondaryItem() {
      TransformerCoilParameters coil = this.getSecondaryCoil();
      Item item = coil != null && coil.isDefined() ? coil.getItem() : null;
      return coil != null && coil.isDefined() && item != null ? BuiltInRegistries.ITEM.getKey(item).toString() : "";
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getRatio() {
      int primaryTurns = this.getPrimaryTurns();
      return primaryTurns <= 0 ? (double)0.0F : (double)this.getSecondaryTurns() / (double)primaryTurns;
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCoreAl() {
      return ((TransformerBlockEntity)this.target).coreAl();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCouplingFactor() {
      return ((TransformerBlockEntity)this.target).couplingFactor();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getLastCurrent() {
      return (double)(this.target).lastCurrent;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isActive() {
      return Math.abs(this.getLastCurrent()) > 1.0E-6;
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPrimaryStrayCurrent() {
      ElectricWire wire = this.getPrimaryStray();
      return wire == null ? (double)0.0F : wire.current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPrimaryStrayVoltage() {
      ElectricWire wire = this.getPrimaryStray();
      return wire == null ? (double)0.0F : wire.potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPrimaryStrayPower() {
      ElectricWire wire = this.getPrimaryStray();
      return wire == null ? (double)0.0F : wire.power();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPrimaryStrayResistance() {
      ElectricWire wire = this.getPrimaryStray();
      return wire == null ? (double)0.0F : wire.getResistance();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getMutualCurrent() {
      ElectricWire wire = this.getMutualInductance();
      return wire == null ? (double)0.0F : wire.current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getMutualVoltage() {
      ElectricWire wire = this.getMutualInductance();
      return wire == null ? (double)0.0F : wire.potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getMutualPower() {
      ElectricWire wire = this.getMutualInductance();
      return wire == null ? (double)0.0F : wire.power();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getMutualResistance() {
      ElectricWire wire = this.getMutualInductance();
      return wire == null ? (double)0.0F : wire.getResistance();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isSplitMode() {
      return this.getCouplingI1() != null && this.getCouplingI2() != null;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean hasStandardCoupling() {
      return this.getCoupling() != null;
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCouplingResistance() {
      TransformerCoupling coupling = this.getCoupling();
      return coupling == null ? (double)0.0F : (double)coupling.getResistance();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isMediumTransformer() {
      return this.target instanceof TransformerMediumBlockEntity;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isSmallTransformer() {
      return this.target instanceof TransformerSmallBlockEntity;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isMain() {
      Object var2 = this.target;
      if (var2 instanceof TransformerMediumBlockEntity medium) {
         return medium.isMain();
      } else {
         return true;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getPrimaryData() {
      return this.createCoilData(this.getPrimaryCoil());
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getSecondaryData() {
      return this.createCoilData(this.getSecondaryCoil());
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = this.createBaseData();
      data.put("primary", this.getPrimaryData());
      data.put("secondary", this.getSecondaryData());
      data.put("complete", this.isComplete());
      data.put("ratio", this.getRatio());
      data.put("coreAl", this.getCoreAl());
      data.put("couplingFactor", this.getCouplingFactor());
      data.put("lastCurrent", this.getLastCurrent());
      data.put("active", this.isActive());
      data.put("primaryStrayCurrent", this.getPrimaryStrayCurrent());
      data.put("primaryStrayVoltage", this.getPrimaryStrayVoltage());
      data.put("primaryStrayPower", this.getPrimaryStrayPower());
      data.put("primaryStrayResistance", this.getPrimaryStrayResistance());
      data.put("mutualCurrent", this.getMutualCurrent());
      data.put("mutualVoltage", this.getMutualVoltage());
      data.put("mutualPower", this.getMutualPower());
      data.put("mutualResistance", this.getMutualResistance());
      data.put("splitMode", this.isSplitMode());
      data.put("standardCoupling", this.hasStandardCoupling());
      data.put("couplingResistance", this.getCouplingResistance());
      data.put("main", this.isMain());
      data.put("small", this.isSmallTransformer());
      data.put("medium", this.isMediumTransformer());
      return data;
   }
}
