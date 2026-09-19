package me.maxim.powergridcc.control;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.patryk3211.powergrid.electricity.electricswitch.HvBreakerBlockEntity;
import org.patryk3211.powergrid.electricity.sim.SwitchedWire;

@EventBusSubscriber(
   modid = "powergridcc",
   bus = Bus.FORGE
)
public final class HvBreakerStateManager {
   private static final Map<HvBreakerBlockEntity, BreakerState> STATES = new ConcurrentHashMap();
   private static Field stateField;
   private static Field wireField;
   private static Field settingField;

   private HvBreakerStateManager() {
   }

   public static void register(HvBreakerBlockEntity breaker) {
      STATES.computeIfAbsent(breaker, (ignored) -> new BreakerState(readClosed(breaker)));
   }

   public static void markManualOpen(HvBreakerBlockEntity breaker) {
      BreakerState state = getState(breaker);
      state.manualOperation = true;
      state.tripped = false;
      state.tripReason = "none";
   }

   public static void markManualClose(HvBreakerBlockEntity breaker) {
      BreakerState state = getState(breaker);
      state.manualOperation = true;
      state.tripped = false;
      state.tripReason = "none";
   }

   public static boolean wasTripped(HvBreakerBlockEntity breaker) {
      return getState(breaker).tripped;
   }

   public static String getTripReason(HvBreakerBlockEntity breaker) {
      return getState(breaker).tripReason;
   }

   public static double getTripCurrentAtEvent(HvBreakerBlockEntity breaker) {
      return getState(breaker).tripCurrent;
   }

   public static void resetTrip(HvBreakerBlockEntity breaker) {
      BreakerState state = getState(breaker);
      state.tripped = false;
      state.tripReason = "none";
      state.tripCurrent = (double)0.0F;
   }

   @SubscribeEvent
   public static void onServerTick(TickEvent.ServerTickEvent event) {
      if (event.phase == Phase.END) {
         STATES.entrySet().removeIf((entry) -> {
            HvBreakerBlockEntity breaker = (HvBreakerBlockEntity)entry.getKey();
            BreakerState tracked = (BreakerState)entry.getValue();
            if (breaker != null && !breaker.m_58901_()) {
               if (breaker.m_58904_() != null && !breaker.m_58904_().f_46443_) {
                  boolean currentlyClosed = readClosed(breaker);
                  if (tracked.lastClosed && !currentlyClosed) {
                     if (tracked.manualOperation) {
                        tracked.manualOperation = false;
                     } else {
                        double current = Math.abs(readCurrent(breaker));
                        int limit = readTripSetting(breaker);
                        tracked.tripped = limit > 0;
                        tracked.tripReason = tracked.tripped ? "overcurrent" : "unknown";
                        tracked.tripCurrent = current;
                     }
                  }

                  if (!tracked.lastClosed && currentlyClosed) {
                     tracked.tripped = false;
                     tracked.tripReason = "none";
                     tracked.tripCurrent = (double)0.0F;
                     tracked.manualOperation = false;
                  }

                  tracked.lastClosed = currentlyClosed;
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

   private static BreakerState getState(HvBreakerBlockEntity breaker) {
      register(breaker);
      return (BreakerState)STATES.get(breaker);
   }

   private static boolean readClosed(HvBreakerBlockEntity breaker) {
      try {
         initializeFields();
         return stateField.getBoolean(breaker);
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException("Failed to read breaker state", e);
      }
   }

   private static double readCurrent(HvBreakerBlockEntity breaker) {
      try {
         initializeFields();
         Object value = wireField.get(breaker);
         if (value instanceof SwitchedWire wire) {
            return wire.current();
         } else {
            return (double)0.0F;
         }
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException("Failed to read breaker current", e);
      }
   }

   private static int readTripSetting(HvBreakerBlockEntity breaker) {
      try {
         initializeFields();
         Object setting = settingField.get(breaker);
         if (setting == null) {
            return 0;
         } else {
            Method method = setting.getClass().getMethod("getValue");
            Object result = method.invoke(setting);
            if (result instanceof Number) {
               Number number = (Number)result;
               return number.intValue();
            } else {
               return 0;
            }
         }
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException("Failed to read breaker trip setting", e);
      }
   }

   private static void initializeFields() throws NoSuchFieldException {
      if (stateField == null) {
         stateField = HvBreakerBlockEntity.class.getDeclaredField("state");
         stateField.setAccessible(true);
      }

      if (wireField == null) {
         wireField = HvBreakerBlockEntity.class.getDeclaredField("wire");
         wireField.setAccessible(true);
      }

      if (settingField == null) {
         settingField = HvBreakerBlockEntity.class.getDeclaredField("setting");
         settingField.setAccessible(true);
      }

   }

   private static final class BreakerState {
      private boolean lastClosed;
      private boolean manualOperation;
      private boolean tripped;
      private String tripReason = "none";
      private double tripCurrent;

      private BreakerState(boolean lastClosed) {
         this.lastClosed = lastClosed;
      }
   }
}
