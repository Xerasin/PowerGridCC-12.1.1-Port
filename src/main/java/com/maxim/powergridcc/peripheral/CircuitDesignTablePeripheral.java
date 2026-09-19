package me.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.patryk3211.powergrid.circuits.components.Component;
import org.patryk3211.powergrid.circuits.editor.CircuitDesignTableBlockEntity;
import org.patryk3211.powergrid.circuits.schematic.CircuitSchematic;
import org.patryk3211.powergrid.circuits.schematic.PlacedComponent;
import org.patryk3211.powergrid.circuits.schematic.CircuitSchematic.Layer;

public class CircuitDesignTablePeripheral extends AbstractPowerGridPeripheral<CircuitDesignTableBlockEntity> {
   public CircuitDesignTablePeripheral(CircuitDesignTableBlockEntity table) {
      super(table);
   }

   public String getType() {
      return "powergrid_circuit_design_table";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_circuit", "powergrid_inventory");
   }

   private CircuitSchematic getSchematicInternal() {
      return ((CircuitDesignTableBlockEntity)this.target).getSchematic();
   }

   private boolean getSchematicChangedInternal() {
      Object value = ReflectionHelper.getField(this.target, "schematicChanged");
      boolean var10000;
      if (value instanceof Boolean changed) {
         if (changed) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   private Map<String, Object> createItemData(ItemStack stack, int slot) {
      Map<String, Object> data = new HashMap();
      data.put("slot", slot);
      if (stack != null && !stack.m_41619_()) {
         data.put("empty", false);
         data.put("item", BuiltInRegistries.f_257033_.m_7981_(stack.m_41720_()).toString());
         data.put("count", stack.m_41613_());
         data.put("name", stack.m_41786_().getString());
         data.put("hasNbt", stack.m_41782_());
         return data;
      } else {
         data.put("empty", true);
         data.put("item", "");
         data.put("count", 0);
         data.put("name", "");
         data.put("hasNbt", false);
         return data;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getSchematicName() {
      String name = ((CircuitDesignTableBlockEntity)this.target).getSchematicName();
      return name == null ? "" : name;
   }

   @LuaFunction(
      mainThread = true
   )
   public final void setSchematicName(String name) {
      ((CircuitDesignTableBlockEntity)this.target).setSchematicName(name == null ? "" : name);
      ((CircuitDesignTableBlockEntity)this.target).m_6596_();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean hasSchematic() {
      CircuitSchematic schematic = this.getSchematicInternal();
      if (schematic == null) {
         return false;
      } else if (!schematic.components().isEmpty()) {
         return true;
      } else {
         for(int x = 0; x < 16; ++x) {
            for(int y = 0; y < 16; ++y) {
               if (schematic.hasTrace(Layer.FRONT, x, y)) {
                  return true;
               }

               if (schematic.hasTrace(Layer.BACK, x, y)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isSchematicChanged() {
      return this.getSchematicChangedInternal();
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getComponentCount() {
      CircuitSchematic schematic = this.getSchematicInternal();
      return schematic == null ? 0 : schematic.components().size();
   }

   @LuaFunction(
      mainThread = true
   )
   public final List<Map<String, Object>> getComponents() {
      List<Map<String, Object>> result = new ArrayList();
      CircuitSchematic schematic = this.getSchematicInternal();
      if (schematic == null) {
         return result;
      } else {
         List<PlacedComponent> components = schematic.components();

         for(int i = 0; i < components.size(); ++i) {
            PlacedComponent placed = (PlacedComponent)components.get(i);
            Map<String, Object> data = new HashMap();
            data.put("index", i + 1);
            data.put("x", placed.x);
            data.put("y", placed.y);
            data.put("uuid", placed.getUUID().toString());
            data.put("destroyed", placed.destroyed);
            data.put("type", placed.component == null ? "unknown" : placed.component.getClass().getSimpleName());

            try {
               data.put("label", placed.getString(Component.LABEL));
            } catch (Exception var8) {
               data.put("label", "");
            }

            result.add(data);
         }

         return result;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getInventorySize() {
      return ((CircuitDesignTableBlockEntity)this.target).getInventory().m_6643_();
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getItem(int slot) {
      Container inventory = ((CircuitDesignTableBlockEntity)this.target).getInventory();
      int javaSlot = slot - 1;
      return javaSlot >= 0 && javaSlot < inventory.m_6643_() ? this.createItemData(inventory.m_8020_(javaSlot), slot) : Map.of();
   }

   @LuaFunction(
      mainThread = true
   )
   public final List<Map<String, Object>> getInventory() {
      List<Map<String, Object>> result = new ArrayList();
      Container inventory = ((CircuitDesignTableBlockEntity)this.target).getInventory();

      for(int i = 0; i < inventory.m_6643_(); ++i) {
         result.add(this.createItemData(inventory.m_8020_(i), i + 1));
      }

      return result;
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = new HashMap();
      data.put("type", this.getType());
      data.put("schematicName", this.getSchematicName());
      data.put("hasSchematic", this.hasSchematic());
      data.put("schematicChanged", this.isSchematicChanged());
      data.put("componentCount", this.getComponentCount());
      data.put("components", this.getComponents());
      data.put("inventorySize", this.getInventorySize());
      data.put("inventory", this.getInventory());
      return data;
   }
}
