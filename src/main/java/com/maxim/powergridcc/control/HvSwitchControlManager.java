package com.maxim.powergridcc.control;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import org.patryk3211.powergrid.electricity.electricswitch.HvSwitchBlockEntity;
import org.patryk3211.powergrid.electricity.sim.SwitchedWire;

@EventBusSubscriber(
   modid = "powergridcc"
)
public final class HvSwitchControlManager {
   private static final Map<HvSwitchBlockEntity, Boolean> CONTROLLED = new ConcurrentHashMap<HvSwitchBlockEntity, Boolean>();
   private static Field rodField;
   private static Field wireField;

   private HvSwitchControlManager() {
   }

   public static void enable(HvSwitchBlockEntity hvSwitch, boolean closed) {
      CONTROLLED.put(hvSwitch, closed);
      applyState(hvSwitch, closed);
   }

   public static void disable(HvSwitchBlockEntity hvSwitch) {
      CONTROLLED.remove(hvSwitch);
   }

   public static boolean isEnabled(HvSwitchBlockEntity hvSwitch) {
      return CONTROLLED.containsKey(hvSwitch);
   }

   public static void setState(HvSwitchBlockEntity hvSwitch, boolean closed) {
      CONTROLLED.put(hvSwitch, closed);
      applyState(hvSwitch, closed);
   }

   public static boolean getRequestedState(HvSwitchBlockEntity hvSwitch) {
      Boolean state = (Boolean)CONTROLLED.get(hvSwitch);
      return state != null ? state : hvSwitch.isClosed();
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent.Post event) {
      {
         CONTROLLED.entrySet().removeIf((entry) -> {
            HvSwitchBlockEntity hvSwitch = (HvSwitchBlockEntity)entry.getKey();
            if (hvSwitch != null && !hvSwitch.isRemoved()) {
               Level level = hvSwitch.getLevel();
               if (level != null && !level.isClientSide) {
                  applyState(hvSwitch, (Boolean)entry.getValue());
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

   private static void applyState(HvSwitchBlockEntity hvSwitch, boolean closed) {
      try {
         initializeReflection();
         LerpedFloat rod = (LerpedFloat)rodField.get(hvSwitch);
         if (rod != null) {
            rod.setValueNoUpdate(closed ? (double)1.0F : (double)0.0F);
            Object value = wireField.get(hvSwitch);
            if (value instanceof SwitchedWire) {
               SwitchedWire wire = (SwitchedWire)value;
               wire.setResistance((double)hvSwitch.getResistance());
               wire.setState(closed);
            } else if (closed) {
               rebuildCircuit(hvSwitch);
            }

            hvSwitch.notifyUpdate();
            hvSwitch.setChanged();
         }
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException("Failed to control HV switch", e);
      }
   }

   private static void rebuildCircuit(HvSwitchBlockEntity hvSwitch) {
      Class<?> current = hvSwitch.getClass();

      while(current != null) {
         try {
            Field field = current.getDeclaredField("electricBehaviour");
            field.setAccessible(true);
            Object behaviour = field.get(hvSwitch);
            if (behaviour == null) {
               return;
            }

            Method method = behaviour.getClass().getMethod("rebuildCircuit", Boolean.TYPE);
            method.invoke(behaviour, false);
            return;
         } catch (NoSuchFieldException var5) {
            current = current.getSuperclass();
         } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to rebuild HV switch circuit", e);
         }
      }

   }

   private static void initializeReflection() throws NoSuchFieldException {
      if (rodField == null) {
         rodField = HvSwitchBlockEntity.class.getDeclaredField("rod");
         rodField.setAccessible(true);
      }

      if (wireField == null) {
         wireField = HvSwitchBlockEntity.class.getDeclaredField("wire");
         wireField.setAccessible(true);
      }

   }
}
