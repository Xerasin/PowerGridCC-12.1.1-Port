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
import org.patryk3211.powergrid.electricity.contactor.ContactorBlockEntity;

@EventBusSubscriber(
   modid = "powergridcc",
   bus = Bus.FORGE
)
public final class ContactorControlManager {
   private static final Map<ContactorBlockEntity, Boolean> CONTROLLED = new ConcurrentHashMap();
   private static Field stateField;
   private static Method setStateMethod;

   private ContactorControlManager() {
   }

   public static void enable(ContactorBlockEntity contactor, boolean closed) {
      CONTROLLED.put(contactor, closed);
      applyState(contactor, closed);
   }

   public static void disable(ContactorBlockEntity contactor) {
      CONTROLLED.remove(contactor);
   }

   public static boolean isEnabled(ContactorBlockEntity contactor) {
      return CONTROLLED.containsKey(contactor);
   }

   public static boolean getRequestedState(ContactorBlockEntity contactor) {
      Boolean value = (Boolean)CONTROLLED.get(contactor);
      return value != null ? value : readActualState(contactor);
   }

   public static void setState(ContactorBlockEntity contactor, boolean closed) {
      CONTROLLED.put(contactor, closed);
      applyState(contactor, closed);
   }

   public static boolean readActualState(ContactorBlockEntity contactor) {
      try {
         initializeReflection();
         return stateField.getBoolean(contactor);
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException("Failed to read contactor state", e);
      }
   }

   @SubscribeEvent
   public static void onServerTick(TickEvent.ServerTickEvent event) {
      if (event.phase == Phase.END) {
         CONTROLLED.entrySet().removeIf((entry) -> {
            ContactorBlockEntity contactor = (ContactorBlockEntity)entry.getKey();
            if (contactor != null && !contactor.m_58901_()) {
               if (contactor.m_58904_() != null && !contactor.m_58904_().f_46443_) {
                  applyState(contactor, (Boolean)entry.getValue());
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

   private static void applyState(ContactorBlockEntity contactor, boolean closed) {
      try {
         initializeReflection();
         setStateMethod.invoke(contactor, closed);
         contactor.m_6596_();
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException("Failed to control contactor", e);
      }
   }

   private static void initializeReflection() throws ReflectiveOperationException {
      if (stateField == null) {
         stateField = ContactorBlockEntity.class.getDeclaredField("state");
         stateField.setAccessible(true);
      }

      if (setStateMethod == null) {
         setStateMethod = ContactorBlockEntity.class.getDeclaredMethod("setState", Boolean.TYPE);
         setStateMethod.setAccessible(true);
      }

   }
}
