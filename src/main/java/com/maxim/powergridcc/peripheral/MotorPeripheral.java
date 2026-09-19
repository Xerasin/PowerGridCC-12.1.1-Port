package com.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;
import org.patryk3211.powergrid.kinetics.motor.ElectricMotorBlockEntity;

public class MotorPeripheral extends AbstractPowerGridPeripheral<ElectricMotorBlockEntity> {
   private final ElectricMotorBlockEntity motor;

   public MotorPeripheral(ElectricMotorBlockEntity motor) {
      super(motor);
      this.motor = motor;
   }

   public String getType() {
      return "powergrid_motor";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_machine", "powergrid_electric_motor");
   }

   private ElectricWire getCoil() {
      try {
         Field field = ElectricMotorBlockEntity.class.getDeclaredField("coil");
         field.setAccessible(true);
         Object value = field.get(this.motor);
         if (value instanceof ElectricWire wire) {
            return wire;
         } else {
            return null;
         }
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException("Failed to access motor coil", e);
      }
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
   public final double getTorque() {
      return (double)this.motor.torque();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getGeneratedSpeed() {
      return (double)this.motor.getGeneratedSpeed();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getAbsoluteSpeed() {
      return Math.abs(this.getGeneratedSpeed());
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getRPM() {
      return this.getGeneratedSpeed();
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
      return Math.abs(this.getGeneratedSpeed()) > 0.001;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isPowered() {
      return Math.abs(this.getVoltage()) > 0.001;
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getRotationDirection() {
      double speed = this.getGeneratedSpeed();
      if (speed > (double)0.0F) {
         return "positive";
      } else {
         return speed < (double)0.0F ? "negative" : "stopped";
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      double voltage = this.getVoltage();
      double current = this.getCurrent();
      double power = this.getPower();
      double speed = this.getGeneratedSpeed();
      double torque = this.getTorque();
      Map<String, Object> data = new HashMap<>();
      data.put("voltage", voltage);
      data.put("current", current);
      data.put("power", power);
      data.put("torque", torque);
      data.put("generatedSpeed", speed);
      data.put("absoluteSpeed", Math.abs(speed));
      data.put("rpm", speed);
      data.put("absoluteRPM", Math.abs(speed));
      data.put("running", Math.abs(speed) > 0.001);
      data.put("powered", Math.abs(voltage) > 0.001);
      data.put("rotationDirection", this.getRotationDirection());
      return data;
   }
}
