package me.maxim.powergridcc.peripheral;

import java.lang.reflect.Field;

final class ReflectionHelper {
   private ReflectionHelper() {
   }

   static Object getField(Object instance, String fieldName) {
      if (instance == null) {
         throw new IllegalArgumentException("Instance cannot be null");
      } else {
         Class<?> currentClass = instance.getClass();

         while(currentClass != null) {
            try {
               Field field = currentClass.getDeclaredField(fieldName);
               field.setAccessible(true);
               return field.get(instance);
            } catch (NoSuchFieldException var4) {
               currentClass = currentClass.getSuperclass();
            } catch (IllegalAccessException exception) {
               throw new IllegalStateException("Cannot access field '" + fieldName + "'", exception);
            }
         }

         throw new IllegalStateException("Field '" + fieldName + "' was not found in " + instance.getClass().getName());
      }
   }

   static <T> T getField(Object instance, String fieldName, Class<T> fieldType) {
      Object value = getField(instance, fieldName);
      if (!fieldType.isInstance(value)) {
         throw new IllegalStateException("Field '" + fieldName + "' is not " + fieldType.getName());
      } else {
         return (T)fieldType.cast(value);
      }
   }
}
