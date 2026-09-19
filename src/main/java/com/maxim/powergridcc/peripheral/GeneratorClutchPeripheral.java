package me.maxim.powergridcc.peripheral;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import dan200.computercraft.api.lua.LuaFunction;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.patryk3211.powergrid.kinetics.generator.clutch.GeneratorClutchBlockEntity;

public class GeneratorClutchPeripheral extends AbstractPowerGridPeripheral<GeneratorClutchBlockEntity> {
   public GeneratorClutchPeripheral(GeneratorClutchBlockEntity clutch) {
      super(clutch);
   }

   public String getType() {
      return "powergrid_generator_clutch";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_kinetic", "powergrid_generator");
   }

   private ScrollOptionBehaviour<?> getModeBehaviour() {
      Object value = ReflectionHelper.getField(this.target, "mode");
      ScrollOptionBehaviour var10000;
      if (value instanceof ScrollOptionBehaviour<?> behaviour) {
         var10000 = behaviour;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   private float getMotorLoadInternal() {
      Object value = ReflectionHelper.getField(this.target, "motorLoad");
      float var10000;
      if (value instanceof Number number) {
         var10000 = number.floatValue();
      } else {
         var10000 = 0.0F;
      }

      return var10000;
   }

   private int getRedstonePowerInternal() {
      Object value = ReflectionHelper.getField(this.target, "currentRedstonePower");
      int var10000;
      if (value instanceof Number number) {
         var10000 = number.intValue();
      } else {
         var10000 = 0;
      }

      return var10000;
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getMode() {
      ScrollOptionBehaviour<?> behaviour = this.getModeBehaviour();
      return behaviour != null && behaviour.get() != null ? behaviour.get().name() : "UNKNOWN";
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isGeneratorMode() {
      return "GENERATOR".equals(this.getMode());
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isMotorMode() {
      return "MOTOR".equals(this.getMode());
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getGeneratedSpeed() {
      return (double)((GeneratorClutchBlockEntity)this.target).getGeneratedSpeed();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getForceSpeed() {
      return (double)((GeneratorClutchBlockEntity)this.target).forceSpeed();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getLoad() {
      return (double)(this.target).load;
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getMotorLoad() {
      return (double)this.getMotorLoadInternal();
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getRedstonePower() {
      return this.getRedstonePowerInternal();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getStressSum() {
      return (double)((GeneratorClutchBlockEntity)this.target).stressSum();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getStressApplied() {
      return (double)((GeneratorClutchBlockEntity)this.target).calculateStressApplied();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getStressCapacity() {
      return (double)((GeneratorClutchBlockEntity)this.target).calculateAddedStressCapacity();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isRunning() {
      return Math.abs(this.getGeneratedSpeed()) > 1.0E-6 || Math.abs(this.getForceSpeed()) > 1.0E-6;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isPoweredByRedstone() {
      return this.getRedstonePower() > 0;
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = new HashMap();
      data.put("type", this.getType());
      data.put("mode", this.getMode());
      data.put("generatorMode", this.isGeneratorMode());
      data.put("motorMode", this.isMotorMode());
      data.put("generatedSpeed", this.getGeneratedSpeed());
      data.put("forceSpeed", this.getForceSpeed());
      data.put("load", this.getLoad());
      data.put("motorLoad", this.getMotorLoad());
      data.put("redstonePower", this.getRedstonePower());
      data.put("poweredByRedstone", this.isPoweredByRedstone());
      data.put("stressSum", this.getStressSum());
      data.put("stressApplied", this.getStressApplied());
      data.put("stressCapacity", this.getStressCapacity());
      data.put("running", this.isRunning());
      return data;
   }
}
