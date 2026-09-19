package com.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.patryk3211.powergrid.electricity.sim.node.VoltageSourceCoupling;
import org.patryk3211.powergrid.electricity.sim.special.GeneratorCoupling;
import org.patryk3211.powergrid.kinetics.generator.inductionrotor.CommutatorBlockEntity;
import org.patryk3211.powergrid.kinetics.generator.rotor.RotorBehaviour;

public class GeneratorPeripheral extends AbstractPowerGridPeripheral<CommutatorBlockEntity> {
   private final CommutatorBlockEntity commutator;

   public GeneratorPeripheral(CommutatorBlockEntity commutator) {
      super(commutator);
      this.commutator = commutator;
   }

   public String getType() {
      return "powergrid_generator";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_commutator", "powergrid_machine");
   }

   private VoltageSourceCoupling getSource() {
      return this.commutator.getAssemblySource();
   }

   private GeneratorCoupling getGeneratorSource() {
      VoltageSourceCoupling source = this.getSource();
      if (source instanceof GeneratorCoupling generatorSource) {
         return generatorSource;
      } else {
         return null;
      }
   }

   private RotorBehaviour getRotorBehaviour() {
      Class<?> currentClass = this.commutator.getClass();

      while(currentClass != null) {
         try {
            Field field = currentClass.getDeclaredField("rotorBehaviour");
            field.setAccessible(true);
            Object value = field.get(this.commutator);
            if (value instanceof RotorBehaviour rotorBehaviour) {
               return rotorBehaviour;
            }

            return null;
         } catch (NoSuchFieldException var5) {
            currentClass = currentClass.getSuperclass();
         } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to access rotorBehaviour", e);
         }
      }

      return null;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isAssembled() {
      return this.getSource() != null;
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getVoltage() {
      VoltageSourceCoupling source = this.getSource();
      return source == null ? (double)0.0F : source.getVoltage();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getEMF() {
      GeneratorCoupling source = this.getGeneratorSource();
      return source == null ? (double)0.0F : source.getEmfValue();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCurrent() {
      return (double)this.commutator.getCurrent();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPower() {
      return (double)this.commutator.getPower();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getAngularVelocity() {
      RotorBehaviour rotor = this.getRotorBehaviour();
      return rotor == null ? (double)0.0F : (double)rotor.getAngularVelocity();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getAbsoluteAngularVelocity() {
      return Math.abs(this.getAngularVelocity());
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getRPM() {
      return this.getAngularVelocity() * (double)60.0F / (Math.PI * 2D);
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getAbsoluteRPM() {
      return Math.abs(this.getRPM());
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isRunning() {
      return Math.abs(this.getAngularVelocity()) > 0.001;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isGenerating() {
      return this.getPower() > 1.0E-4;
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getRotationDirection() {
      double angularVelocity = this.getAngularVelocity();
      if (angularVelocity > (double)0.0F) {
         return "positive";
      } else {
         return angularVelocity < (double)0.0F ? "negative" : "stopped";
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      VoltageSourceCoupling source = this.getSource();
      GeneratorCoupling generatorSource = this.getGeneratorSource();
      double voltage = source == null ? (double)0.0F : source.getVoltage();
      double emf = generatorSource == null ? (double)0.0F : generatorSource.getEmfValue();
      double current = (double)this.commutator.getCurrent();
      double power = (double)this.commutator.getPower();
      double angularVelocity = this.getAngularVelocity();
      double rpm = angularVelocity * (double)60.0F / (Math.PI * 2D);
      Map<String, Object> data = new HashMap<>();
      data.put("assembled", source != null);
      data.put("voltage", voltage);
      data.put("emf", emf);
      data.put("current", current);
      data.put("power", power);
      data.put("angularVelocity", angularVelocity);
      data.put("absoluteAngularVelocity", Math.abs(angularVelocity));
      data.put("rpm", rpm);
      data.put("absoluteRPM", Math.abs(rpm));
      data.put("running", Math.abs(angularVelocity) > 0.001);
      data.put("generating", power > 1.0E-4);
      data.put("rotationDirection", this.getRotationDirection());
      return data;
   }
}
