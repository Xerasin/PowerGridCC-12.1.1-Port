package com.maxim.powergridcc.control;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import org.patryk3211.powergrid.kinetics.servo.ServoBlockEntity;

@EventBusSubscriber(
   modid = "powergridcc"
)
public final class ServoControlManager {
   private static final Map<ServoBlockEntity, ControlState> CONTROLLED_SERVOS = new ConcurrentHashMap<ServoBlockEntity, ControlState>();
   private static Field currentTargetField;
   private static Field avgTargetField;

   private ServoControlManager() {
   }

   public static void enable(ServoBlockEntity servo, double targetAngle) {
      CONTROLLED_SERVOS.put(servo, new ControlState(clampAngle(targetAngle)));
   }

   public static void disable(ServoBlockEntity servo) {
      CONTROLLED_SERVOS.remove(servo);
   }

   public static boolean isEnabled(ServoBlockEntity servo) {
      return CONTROLLED_SERVOS.containsKey(servo);
   }

   public static void setTargetAngle(ServoBlockEntity servo, double targetAngle) {
      double clampedAngle = clampAngle(targetAngle);
      CONTROLLED_SERVOS.compute(servo, (ignored, oldState) -> new ControlState(clampedAngle));
      applyTargetImmediately(servo, clampedAngle);
   }

   public static double getTargetAngle(ServoBlockEntity servo) {
      ControlState state = (ControlState)CONTROLLED_SERVOS.get(servo);
      return state == null ? readFloatField(servo, "currentTarget") : state.targetAngle();
   }

   public static void stopAtCurrentAngle(ServoBlockEntity servo) {
      double currentAngle = readFloatField(servo, "currentAngle");
      setTargetAngle(servo, currentAngle);
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent.Post event) {
      {
         CONTROLLED_SERVOS.entrySet().removeIf((entry) -> {
            ServoBlockEntity servo = (ServoBlockEntity)entry.getKey();
            if (servo != null && !servo.isRemoved()) {
               Level level = servo.getLevel();
               if (level != null && !level.isClientSide) {
                  applyTargetImmediately(servo, ((ControlState)entry.getValue()).targetAngle());
                  return false;
               } else {
                  return false;
               }
            } else {
               return true;
            }
         });
      }
   }

   private static void applyTargetImmediately(ServoBlockEntity servo, double targetAngle) {
      try {
         initializeFields();
         float angle = (float)clampAngle(targetAngle);
         float requiredAverageTarget = angle * 25.0F / 360.0F;
         currentTargetField.setFloat(servo, angle);
         avgTargetField.setFloat(servo, requiredAverageTarget);
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException("Failed to control Power Grid servo", e);
      }
   }

   private static void initializeFields() throws NoSuchFieldException {
      if (currentTargetField == null) {
         currentTargetField = ServoBlockEntity.class.getDeclaredField("currentTarget");
         currentTargetField.setAccessible(true);
      }

      if (avgTargetField == null) {
         avgTargetField = ServoBlockEntity.class.getDeclaredField("avgTarget");
         avgTargetField.setAccessible(true);
      }

   }

   private static double readFloatField(ServoBlockEntity servo, String fieldName) {
      try {
         Field field = ServoBlockEntity.class.getDeclaredField(fieldName);
         field.setAccessible(true);
         return (double)field.getFloat(servo);
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException("Failed to read servo field: " + fieldName, e);
      }
   }

   private static double clampAngle(double angle) {
      return Math.max((double)-360.0F, Math.min((double)360.0F, angle));
   }

   private static record ControlState(double targetAngle) {
   }
}
