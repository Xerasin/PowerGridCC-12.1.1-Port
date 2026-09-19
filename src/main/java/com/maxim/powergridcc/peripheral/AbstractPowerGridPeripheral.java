package com.maxim.powergridcc.peripheral;

import dan200.computercraft.api.peripheral.IPeripheral;
import java.util.Objects;

public abstract class AbstractPowerGridPeripheral<T> implements IPeripheral {
   protected final T target;

   protected AbstractPowerGridPeripheral(T target) {
      this.target = (T)Objects.requireNonNull(target);
   }

   public final Object getTarget() {
      return this.target;
   }

   public final boolean equals(IPeripheral other) {
      if (other != null && other.getClass() == this.getClass()) {
         AbstractPowerGridPeripheral<?> peripheral = (AbstractPowerGridPeripheral<?>)other;
         return peripheral.target == this.target;
      } else {
         return false;
      }
   }
}
