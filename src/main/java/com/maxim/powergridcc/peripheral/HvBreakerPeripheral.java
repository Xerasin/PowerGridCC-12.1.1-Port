package me.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import me.maxim.powergridcc.control.HvBreakerStateManager;
import org.patryk3211.powergrid.electricity.electricswitch.HvBreakerBlockEntity;
import org.patryk3211.powergrid.electricity.sim.SwitchedWire;

public class HvBreakerPeripheral extends AbstractPowerGridPeripheral<HvBreakerBlockEntity> {
   private final HvBreakerBlockEntity breaker;

   public HvBreakerPeripheral(HvBreakerBlockEntity breaker) {
      super(breaker);
      this.breaker = breaker;
      HvBreakerStateManager.register(breaker);
   }

   public String getType() {
      return "powergrid_hv_breaker";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_breaker", "powergrid_switch", "powergrid_machine");
   }

   private Field findField(String name) {
      try {
         Field field = HvBreakerBlockEntity.class.getDeclaredField(name);
         field.setAccessible(true);
         return field;
      } catch (NoSuchFieldException e) {
         throw new IllegalStateException("Field not found: " + name, e);
      }
   }

   private boolean readBoolean(String name) {
      try {
         return this.findField(name).getBoolean(this.breaker);
      } catch (IllegalAccessException e) {
         throw new IllegalStateException("Failed to read field: " + name, e);
      }
   }

   private void writeBoolean(String name, boolean value) {
      try {
         this.findField(name).setBoolean(this.breaker, value);
      } catch (IllegalAccessException e) {
         throw new IllegalStateException("Failed to write field: " + name, e);
      }
   }

   private Object readObject(String name) {
      try {
         return this.findField(name).get(this.breaker);
      } catch (IllegalAccessException e) {
         throw new IllegalStateException("Failed to read field: " + name, e);
      }
   }

   private SwitchedWire getWire() {
      Object value = this.readObject("wire");
      if (value instanceof SwitchedWire wire) {
         return wire;
      } else {
         return null;
      }
   }

   private Object getSettingBehaviour() {
      return this.readObject("setting");
   }

   private void rebuildCircuit() {
      Class<?> currentClass = this.breaker.getClass();

      while(currentClass != null) {
         try {
            Field field = currentClass.getDeclaredField("electricBehaviour");
            field.setAccessible(true);
            Object behaviour = field.get(this.breaker);
            if (behaviour == null) {
               throw new IllegalStateException("Electric behaviour is not initialized");
            }

            Method method = behaviour.getClass().getMethod("rebuildCircuit", Boolean.TYPE);
            method.invoke(behaviour, false);
            return;
         } catch (NoSuchFieldException var5) {
            currentClass = currentClass.getSuperclass();
         } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to rebuild breaker circuit", e);
         }
      }

      throw new IllegalStateException("Field not found: electricBehaviour");
   }

   private int getSettingValue() {
      Object setting = this.getSettingBehaviour();
      if (setting == null) {
         return 0;
      } else {
         try {
            Method method = setting.getClass().getMethod("getValue");
            Object result = method.invoke(setting);
            if (result instanceof Number) {
               Number number = (Number)result;
               return number.intValue();
            } else {
               return 0;
            }
         } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to read breaker setting", e);
         }
      }
   }

   private void setSettingValue(int value) {
      Object setting = this.getSettingBehaviour();
      if (setting == null) {
         throw new IllegalStateException("Breaker setting is not initialized");
      } else {
         try {
            Method method = setting.getClass().getMethod("setValue", Integer.TYPE);
            method.invoke(setting, value);
            this.breaker.notifyUpdate();
            this.breaker.m_6596_();
         } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to change breaker setting", e);
         }
      }
   }

   private void applyState(boolean closed) {
      this.writeBoolean("state", closed);
      SwitchedWire wire = this.getWire();
      if (wire != null) {
         wire.setState(closed);
      } else if (closed) {
         this.rebuildCircuit();
      }

      try {
         Object charge = this.readObject("charge");
         if (charge != null) {
            Method method = charge.getClass().getMethod("setValueNoUpdate", Double.TYPE);
            method.invoke(charge, closed ? (double)1.0F : (double)0.0F);
         }
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException("Failed to update breaker animation", e);
      }

      this.breaker.notifyUpdate();
      this.breaker.m_6596_();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isClosed() {
      return this.readBoolean("state");
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isOpen() {
      return !this.isClosed();
   }

   @LuaFunction(
      mainThread = true
   )
   public final void close() {
      HvBreakerStateManager.markManualClose(this.breaker);
      this.applyState(true);
   }

   @LuaFunction(
      mainThread = true
   )
   public final void open() {
      HvBreakerStateManager.markManualOpen(this.breaker);
      this.applyState(false);
   }

   @LuaFunction(
      mainThread = true
   )
   public final void toggle() {
      if (this.isClosed()) {
         this.open();
      } else {
         this.close();
      }

   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCurrent() {
      SwitchedWire wire = this.getWire();
      return wire == null ? (double)0.0F : wire.current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getVoltage() {
      SwitchedWire wire = this.getWire();
      return wire == null ? (double)0.0F : wire.potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPower() {
      SwitchedWire wire = this.getWire();
      return wire == null ? (double)0.0F : wire.power();
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getTripCurrent() {
      return this.getSettingValue();
   }

   @LuaFunction(
      mainThread = true
   )
   public final void setTripCurrent(double current) throws LuaException {
      if (!Double.isFinite(current)) {
         throw new LuaException("Trip current must be a finite number");
      } else if (!(current < (double)0.0F) && !(current > (double)10000.0F)) {
         this.setSettingValue((int)Math.round(current));
      } else {
         throw new LuaException("Trip current must be between 0 and 10000 A");
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isProtectionEnabled() {
      return this.getTripCurrent() > 0;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isTripped() {
      return HvBreakerStateManager.wasTripped(this.breaker);
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getTripReason() {
      return HvBreakerStateManager.getTripReason(this.breaker);
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getTripCurrentAtEvent() {
      return HvBreakerStateManager.getTripCurrentAtEvent(this.breaker);
   }

   @LuaFunction(
      mainThread = true
   )
   public final void resetTrip() {
      HvBreakerStateManager.resetTrip(this.breaker);
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = new HashMap();
      data.put("closed", this.isClosed());
      data.put("open", this.isOpen());
      data.put("voltage", this.getVoltage());
      data.put("current", this.getCurrent());
      data.put("power", this.getPower());
      data.put("tripCurrent", this.getTripCurrent());
      data.put("protectionEnabled", this.isProtectionEnabled());
      data.put("tripped", this.isTripped());
      data.put("tripReason", this.getTripReason());
      data.put("tripCurrentAtEvent", this.getTripCurrentAtEvent());
      return data;
   }
}
