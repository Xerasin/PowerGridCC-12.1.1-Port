package com.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.patryk3211.powergrid.kinetics.generator.inductionrotor.InductionRotorBlockEntity;
import org.patryk3211.powergrid.kinetics.generator.rotor.RotorBehaviour;
import org.patryk3211.powergrid.kinetics.generator.rotor.RotorBlockEntity;

public class RotorPeripheral extends AbstractPowerGridPeripheral<RotorBlockEntity> {
   public RotorPeripheral(RotorBlockEntity rotor) {
      super(rotor);
   }

   public String getType() {
      if (this.target instanceof InductionRotorBlockEntity) {
         return "powergrid_induction_rotor";
      } else {
         return "powergrid_rotor";
      }
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_rotor", "powergrid_generator", "powergrid_kinetic");
   }

   private RotorBehaviour getRotorBehaviour() {
      Object value = ReflectionHelper.getField(this.target, "rotorBehaviour");
      RotorBehaviour var10000;
      if (value instanceof RotorBehaviour behaviour) {
         var10000 = behaviour;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   private double readNumberField(Object object, String fieldName) {
      if (object == null) {
         return (double)0.0F;
      } else {
         Object value = ReflectionHelper.getField(object, fieldName);
         double var10000;
         if (value instanceof Number) {
            Number number = (Number)value;
            var10000 = number.doubleValue();
         } else {
            var10000 = (double)0.0F;
         }

         return var10000;
      }
   }

   private int readIntField(Object object, String fieldName) {
      if (object == null) {
         return 0;
      } else {
         Object value = ReflectionHelper.getField(object, fieldName);
         int var10000;
         if (value instanceof Number) {
            Number number = (Number)value;
            var10000 = number.intValue();
         } else {
            var10000 = 0;
         }

         return var10000;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getIndividualInertia() {
      return 0.0D;
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getInertia() {
      RotorBehaviour behaviour = this.getRotorBehaviour();
      return behaviour == null ? this.getIndividualInertia() : (double)behaviour.getInertia();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getAngularVelocity() {
      RotorBehaviour behaviour = this.getRotorBehaviour();
      return behaviour == null ? (double)0.0F : (double)behaviour.getAngularVelocity();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getOldAngularVelocity() {
      RotorBehaviour behaviour = this.getRotorBehaviour();
      return behaviour == null ? (double)0.0F : (double)behaviour.getOldAngVel();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getAngle() {
      RotorBehaviour behaviour = this.getRotorBehaviour();
      return behaviour == null ? (double)0.0F : (double)behaviour.getAngle();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPower() {
      RotorBehaviour behaviour = this.getRotorBehaviour();
      return behaviour == null ? (double)0.0F : (double)behaviour.power;
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPreviousForce() {
      RotorBehaviour behaviour = this.getRotorBehaviour();
      return behaviour == null ? (double)0.0F : (double)behaviour.prevForce;
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCurrentForce() {
      return this.readNumberField(this.getRotorBehaviour(), "totalForce");
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getSegmentCount() {
      int count = this.readIntField(this.getRotorBehaviour(), "segmentCount");
      return Math.max(1, count);
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getOverspeedTicks() {
      return this.readIntField(this.getRotorBehaviour(), "overspeedTicks");
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getMaxRotationSpeed() {
      return RotorBehaviour.getMaxRotationSpeed();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getRotorKp() {
      return (double)RotorBehaviour.getRotorKp();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getRotorKd() {
      return (double)RotorBehaviour.getRotorKd();
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getAxis() {
      RotorBehaviour behaviour = this.getRotorBehaviour();
      return behaviour != null && behaviour.getAxis() != null ? behaviour.getAxis().getName().toUpperCase() : "UNKNOWN";
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isRunning() {
      return Math.abs(this.getAngularVelocity()) > 1.0E-6;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isOverspeed() {
      return Math.abs(this.getAngularVelocity()) >= (double)this.getMaxRotationSpeed();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isInductionRotor() {
      return this.target instanceof InductionRotorBlockEntity;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isSimpleRotor() {
      return false;
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getMagneticField() {
      Object var2 = this.target;
      if (var2 instanceof InductionRotorBlockEntity induction) {
         try {
            Float value = (Float)induction.totalField.get();
            return value == null ? (double)0.0F : value.doubleValue();
         } catch (Exception var3) {
            return (double)0.0F;
         }
      } else {
         return (double)0.0F;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = new HashMap<>();
      data.put("type", this.getType());
      data.put("axis", this.getAxis());
      data.put("individualInertia", this.getIndividualInertia());
      data.put("inertia", this.getInertia());
      data.put("angularVelocity", this.getAngularVelocity());
      data.put("oldAngularVelocity", this.getOldAngularVelocity());
      data.put("angle", this.getAngle());
      data.put("power", this.getPower());
      data.put("previousForce", this.getPreviousForce());
      data.put("currentForce", this.getCurrentForce());
      data.put("segmentCount", this.getSegmentCount());
      data.put("maxRotationSpeed", this.getMaxRotationSpeed());
      data.put("overspeedTicks", this.getOverspeedTicks());
      data.put("overspeed", this.isOverspeed());
      data.put("running", this.isRunning());
      data.put("inductionRotor", this.isInductionRotor());
      data.put("simpleRotor", this.isSimpleRotor());
      data.put("magneticField", this.getMagneticField());
      return data;
   }
}
