package me.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import org.patryk3211.powergrid.electricity.creative.CreativeSourceBlockEntity;
import org.patryk3211.powergrid.electricity.sim.node.CurrentSourceNode;
import org.patryk3211.powergrid.electricity.sim.node.VoltageSourceCoupling;

public class CreativeSourcePeripheral extends AbstractElectricPeripheral<CreativeSourceBlockEntity> {
   public CreativeSourcePeripheral(CreativeSourceBlockEntity source) {
      super(source);
   }

   public String getType() {
      return "powergrid_creative_source";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_electric", "powergrid_source", "powergrid_creative");
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
         Field field = findField(((CreativeSourceBlockEntity)this.target).getClass(), name);
         field.setAccessible(true);
         return field.get(this.target);
      } catch (ReflectiveOperationException exception) {
         throw new IllegalStateException("Failed to access creative source field: " + name, exception);
      }
   }

   private void setOverwriteInternal(boolean enabled) {
      try {
         Field field = findField(((CreativeSourceBlockEntity)this.target).getClass(), "overwrite");
         field.setAccessible(true);
         field.setBoolean(this.target, enabled);
         ((CreativeSourceBlockEntity)this.target).m_6596_();
         ((CreativeSourceBlockEntity)this.target).notifyUpdate();
      } catch (ReflectiveOperationException exception) {
         throw new IllegalStateException("Failed to change creative source overwrite mode", exception);
      }
   }

   private boolean isVoltageSourceInternal() {
      Object value = this.getInternalField("voltageSource");
      if (value instanceof Boolean result) {
         return result;
      } else {
         throw new IllegalStateException("Creative source voltageSource field has an unexpected type");
      }
   }

   private boolean isOverwriteInternal() {
      Object value = this.getInternalField("overwrite");
      if (value instanceof Boolean result) {
         return result;
      } else {
         throw new IllegalStateException("Creative source overwrite field has an unexpected type");
      }
   }

   private CurrentSourceNode getCurrentSourceNode() {
      Object node = this.getInternalField("currentSourceNode");
      CurrentSourceNode var10000;
      if (node instanceof CurrentSourceNode currentNode) {
         var10000 = currentNode;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   private VoltageSourceCoupling getVoltageSourceNode() {
      Object node = this.getInternalField("voltageSourceNode");
      VoltageSourceCoupling var10000;
      if (node instanceof VoltageSourceCoupling voltageNode) {
         var10000 = voltageNode;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getValue() {
      return (double)((CreativeSourceBlockEntity)this.target).getValue();
   }

   @LuaFunction(
      mainThread = true
   )
   public final void setComputerControl(boolean enabled) {
      this.setOverwriteInternal(enabled);
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isComputerControlEnabled() {
      return this.isOverwriteInternal();
   }

   @LuaFunction(
      mainThread = true
   )
   public final void setValue(double value) throws LuaException {
      if (!Double.isFinite(value)) {
         throw new LuaException("Value must be a finite number");
      } else {
         this.setOverwriteInternal(true);
         ((CreativeSourceBlockEntity)this.target).setValue((float)value);
         ((CreativeSourceBlockEntity)this.target).m_6596_();
         ((CreativeSourceBlockEntity)this.target).notifyUpdate();
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isVoltageSource() {
      return this.isVoltageSourceInternal();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isCurrentSource() {
      return !this.isVoltageSourceInternal();
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getMode() {
      return this.isVoltageSource() ? "voltage" : "current";
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isOverwriteEnabled() {
      return this.isOverwriteInternal();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getVoltage() {
      if (this.isVoltageSource()) {
         VoltageSourceCoupling node = this.getVoltageSourceNode();
         return node == null ? this.getValue() : node.getVoltage();
      } else {
         CurrentSourceNode node = this.getCurrentSourceNode();
         return node == null ? (double)0.0F : node.getVoltage();
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCurrent() {
      if (this.isCurrentSource()) {
         CurrentSourceNode node = this.getCurrentSourceNode();
         return node == null ? this.getValue() : node.getCurrent();
      } else {
         VoltageSourceCoupling node = this.getVoltageSourceNode();
         return node == null ? (double)0.0F : -node.getCurrent();
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = this.createBaseData();
      data.put("mode", this.getMode());
      data.put("value", this.getValue());
      data.put("voltage", this.getVoltage());
      data.put("current", this.getCurrent());
      data.put("overwrite", this.isOverwriteEnabled());
      data.put("computerControl", this.isComputerControlEnabled());
      return data;
   }
}
