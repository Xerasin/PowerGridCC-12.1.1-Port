package me.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;
import org.patryk3211.powergrid.kinetics.motor.ConstantSpeedMotorBlockEntity;
import org.patryk3211.powergrid.kinetics.motor.SpeedScrollValueBehaviour;

public class ConstantSpeedMotorPeripheral extends AbstractPowerGridPeripheral<ConstantSpeedMotorBlockEntity> {
   private final ConstantSpeedMotorBlockEntity motor;

   public ConstantSpeedMotorPeripheral(ConstantSpeedMotorBlockEntity motor) {
      super(motor);
      this.motor = motor;
   }

   public String getType() {
      return "powergrid_constant_speed_motor";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_motor", "powergrid_machine");
   }

   private static Field findField(Class<?> type, String name) throws NoSuchFieldException {
      for(Class<?> current = type; current != null; current = current.getSuperclass()) {
         try {
            return current.getDeclaredField(name);
         }
      }

      throw new NoSuchFieldException(name);
   }

   private Object getInternalField(String name) {
      try {
         Field field = findField(this.motor.getClass(), name);
         field.setAccessible(true);
         return field.get(this.motor);
      } catch (ReflectiveOperationException exception) {
         throw new IllegalStateException("Failed to access constant speed motor field: " + name, exception);
      }
   }

   private ElectricWire getCoil() {
      Object value = this.getInternalField("coil");
      ElectricWire var10000;
      if (value instanceof ElectricWire wire) {
         var10000 = wire;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   private SpeedScrollValueBehaviour getScrollValue() {
      Object value = this.getInternalField("scrollValue");
      SpeedScrollValueBehaviour var10000;
      if (value instanceof SpeedScrollValueBehaviour behaviour) {
         var10000 = behaviour;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   private double getGeneratedStressRaw() {
      Object value = this.getInternalField("generatedSU");
      double var10000;
      if (value instanceof Number number) {
         var10000 = number.doubleValue();
      } else {
         var10000 = (double)0.0F;
      }

      return var10000;
   }

   private void setScrollValue(int speed) {
      SpeedScrollValueBehaviour behaviour = this.getScrollValue();
      if (behaviour == null) {
         throw new IllegalStateException("Speed control behaviour is not initialized");
      } else {
         behaviour.setValue(speed);
         this.motor.updateGeneratedRotation();
         this.motor.m_6596_();
         this.motor.notifyUpdate();
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
   public final double getSpeed() {
      return (double)this.motor.getGeneratedSpeed();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getAbsoluteSpeed() {
      return Math.abs(this.getSpeed());
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getTargetSpeed() {
      SpeedScrollValueBehaviour behaviour = this.getScrollValue();
      return behaviour == null ? (double)0.0F : (double)behaviour.getValue();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getGeneratedStress() {
      return Math.abs(this.getGeneratedStressRaw());
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getStressCapacity() {
      return (double)this.motor.calculateAddedStressCapacity();
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
   public final boolean isRunning() {
      return Math.abs(this.getSpeed()) > 0.001;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isAtTargetSpeed() {
      if (!this.isRunning()) {
         return false;
      } else {
         return Math.abs(Math.abs(this.getSpeed()) - this.getTargetSpeed()) < (double)0.5F;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getRotationDirection() {
      double speed = this.getSpeed();
      if (speed > (double)0.0F) {
         return "positive";
      } else {
         return speed < (double)0.0F ? "negative" : "stopped";
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final void setComputerControl(boolean enabled) {
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isComputerControlEnabled() {
      return true;
   }

   @LuaFunction(
      mainThread = true
   )
   public final void setTargetSpeed(double speed) throws LuaException {
      if (!Double.isFinite(speed)) {
         throw new LuaException("Speed must be a finite number");
      } else if (!(speed < (double)0.0F) && !(speed > (double)256.0F)) {
         this.setScrollValue((int)Math.round(speed));
      } else {
         throw new LuaException("Speed must be between 0 and 256 RPM");
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final void stop() {
      this.setScrollValue(0);
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = new HashMap();
      data.put("voltage", this.getVoltage());
      data.put("current", this.getCurrent());
      data.put("power", this.getPower());
      data.put("torque", this.getTorque());
      data.put("speed", this.getSpeed());
      data.put("absoluteSpeed", this.getAbsoluteSpeed());
      data.put("targetSpeed", this.getTargetSpeed());
      data.put("generatedStress", this.getGeneratedStress());
      data.put("stressCapacity", this.getStressCapacity());
      data.put("powered", this.isPowered());
      data.put("running", this.isRunning());
      data.put("atTargetSpeed", this.isAtTargetSpeed());
      data.put("computerControl", this.isComputerControlEnabled());
      data.put("rotationDirection", this.getRotationDirection());
      return data;
   }
}
