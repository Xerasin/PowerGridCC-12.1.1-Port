package me.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import me.maxim.powergridcc.control.ServoControlManager;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;
import org.patryk3211.powergrid.kinetics.servo.ServoBlockEntity;

public class ServoPeripheral extends AbstractPowerGridPeripheral<ServoBlockEntity> {
   private final ServoBlockEntity servo;

   public ServoPeripheral(ServoBlockEntity servo) {
      super(servo);
      this.servo = servo;
   }

   public String getType() {
      return "powergrid_servo";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_machine", "powergrid_actuator");
   }

   private double readFloat(String fieldName) {
      try {
         Field field = ServoBlockEntity.class.getDeclaredField(fieldName);
         field.setAccessible(true);
         return (double)field.getFloat(this.servo);
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException("Failed to read servo field: " + fieldName, e);
      }
   }

   private ElectricWire readWire(String fieldName) {
      try {
         Field field = ServoBlockEntity.class.getDeclaredField(fieldName);
         field.setAccessible(true);
         Object value = field.get(this.servo);
         if (value instanceof ElectricWire wire) {
            return wire;
         } else {
            return null;
         }
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException("Failed to read servo wire: " + fieldName, e);
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getAngle() {
      return this.readFloat("currentAngle");
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getTargetAngle() {
      return ServoControlManager.getTargetAngle(this.servo);
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getGeneratedSpeed() {
      return this.readFloat("generatedSpeed");
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
   public final double getMaxSpeed() {
      return this.readFloat("maxSpeed");
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getTorque() {
      return (double)this.servo.torque();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getVoltage() {
      ElectricWire coil = this.readWire("coil");
      return coil == null ? (double)0.0F : coil.potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getControlVoltage() {
      ElectricWire control = this.readWire("control");
      return control == null ? (double)0.0F : control.potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCurrent() {
      ElectricWire coil = this.readWire("coil");
      return coil == null ? (double)0.0F : coil.current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPower() {
      ElectricWire coil = this.readWire("coil");
      return coil == null ? (double)0.0F : coil.power();
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
   public final boolean isAtTarget() {
      return Math.abs(this.getTargetAngle() - this.getAngle()) < 3.7;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isComputerControlled() {
      return ServoControlManager.isEnabled(this.servo);
   }

   @LuaFunction(
      mainThread = true
   )
   public final void setComputerControl(boolean enabled) {
      if (enabled) {
         ServoControlManager.enable(this.servo, this.getAngle());
      } else {
         ServoControlManager.disable(this.servo);
      }

   }

   @LuaFunction(
      mainThread = true
   )
   public final void setTargetAngle(double angle) throws LuaException {
      if (!Double.isFinite(angle)) {
         throw new LuaException("Angle must be a finite number");
      } else if (!(angle < (double)-360.0F) && !(angle > (double)360.0F)) {
         if (!ServoControlManager.isEnabled(this.servo)) {
            throw new LuaException("Computer control is disabled. Call setComputerControl(true) first");
         } else {
            ServoControlManager.setTargetAngle(this.servo, angle);
         }
      } else {
         throw new LuaException("Angle must be between -360 and 360 degrees");
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final void stop() {
      if (!ServoControlManager.isEnabled(this.servo)) {
         ServoControlManager.enable(this.servo, this.getAngle());
      }

      ServoControlManager.stopAtCurrentAngle(this.servo);
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = new HashMap();
      data.put("angle", this.getAngle());
      data.put("targetAngle", this.getTargetAngle());
      data.put("generatedSpeed", this.getGeneratedSpeed());
      data.put("absoluteSpeed", this.getAbsoluteSpeed());
      data.put("maxSpeed", this.getMaxSpeed());
      data.put("voltage", this.getVoltage());
      data.put("controlVoltage", this.getControlVoltage());
      data.put("current", this.getCurrent());
      data.put("power", this.getPower());
      data.put("torque", this.getTorque());
      data.put("running", this.isRunning());
      data.put("atTarget", this.isAtTarget());
      data.put("computerControlled", this.isComputerControlled());
      return data;
   }
}
