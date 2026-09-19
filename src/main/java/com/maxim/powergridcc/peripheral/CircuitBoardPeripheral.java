package com.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.patryk3211.powergrid.circuits.circuitboard.BakedCircuit;
import org.patryk3211.powergrid.circuits.circuitboard.CircuitBoardBlockEntity;
import org.patryk3211.powergrid.circuits.components.Component;
import org.patryk3211.powergrid.circuits.schematic.CircuitSchematic;
import org.patryk3211.powergrid.circuits.schematic.PlacedComponent;
import org.patryk3211.powergrid.circuits.schematic.CircuitSchematic.Layer;

public class CircuitBoardPeripheral extends AbstractElectricPeripheral<CircuitBoardBlockEntity> {
   public CircuitBoardPeripheral(CircuitBoardBlockEntity board) {
      super(board);
   }

   public String getType() {
      return "powergrid_circuit_board";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_electric", "powergrid_circuit");
   }

   private CircuitSchematic getSchematicInternal() {
      return ((CircuitBoardBlockEntity)this.target).getSchematic();
   }

   private BakedCircuit getBakedInternal() {
      return ((CircuitBoardBlockEntity)this.target).getBaked();
   }

   private int countTraces(CircuitSchematic schematic, CircuitSchematic.Layer layer) {
      if (schematic == null) {
         return 0;
      } else {
         int count = 0;

         for(int x = 0; x < 16; ++x) {
            for(int y = 0; y < 16; ++y) {
               if (schematic.hasTrace(layer, x, y)) {
                  ++count;
               }
            }
         }

         return count;
      }
   }

   private Map<String, Object> createComponentData(PlacedComponent placed, int index) {
      Map<String, Object> data = new HashMap<>();
      Component component = placed.component;
      data.put("index", index);
      data.put("x", placed.x);
      data.put("y", placed.y);
      data.put("uuid", placed.getUUID().toString());
      data.put("destroyed", placed.destroyed);
      data.put("type", component == null ? "unknown" : component.getClass().getSimpleName());
      if (component != null) {
         try {
            data.put("model", component.getModelId(placed).toString());
         } catch (Exception var9) {
            data.put("model", "");
         }

         try {
            data.put("label", placed.getString(Component.LABEL));
         } catch (Exception var8) {
            data.put("label", "");
         }

         try {
            data.put("propertyCount", component.getProperties().size());
         } catch (Exception var7) {
            data.put("propertyCount", 0);
         }

         try {
            data.put("externalTerminals", component.emitExternalTerminals());
         } catch (Exception var6) {
            data.put("externalTerminals", false);
         }
      } else {
         data.put("model", "");
         data.put("label", "");
         data.put("propertyCount", 0);
         data.put("externalTerminals", false);
      }

      data.put("nodeCount", placed.nodes.size());
      data.put("wireCount", placed.wires.size());
      data.put("hasCustomData", placed.customData != null);
      return data;
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getSchematicName() {
      String name = ((CircuitBoardBlockEntity)this.target).getSchematicName();
      return name == null ? "" : name;
   }

   @LuaFunction(
      mainThread = true
   )
   public final void setSchematicName(String name) {
      ((CircuitBoardBlockEntity)this.target).setSchematicName(name == null ? "" : name);
      ((CircuitBoardBlockEntity)this.target).setChanged();
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
         return this.getFrontTraceCount() > 0 || this.getBackTraceCount() > 0;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isBaked() {
      return this.getBakedInternal() != null;
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isDamaged() {
      BakedCircuit baked = this.getBakedInternal();
      return baked != null && baked.isDamaged();
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
      List<Map<String, Object>> result = new ArrayList<>();
      CircuitSchematic schematic = this.getSchematicInternal();
      if (schematic == null) {
         return result;
      } else {
         List<PlacedComponent> components = schematic.components();

         for(int i = 0; i < components.size(); ++i) {
            result.add(this.createComponentData((PlacedComponent)components.get(i), i + 1));
         }

         return result;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getComponent(int index) {
      CircuitSchematic schematic = this.getSchematicInternal();
      if (schematic == null) {
         return Map.of();
      } else {
         List<PlacedComponent> components = schematic.components();
         int javaIndex = index - 1;
         return javaIndex >= 0 && javaIndex < components.size() ? this.createComponentData((PlacedComponent)components.get(javaIndex), index) : Map.of();
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getTerminalCount() {
      return ((CircuitBoardBlockEntity)this.target).terminalCount();
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getFrontTraceCount() {
      return this.countTraces(this.getSchematicInternal(), Layer.FRONT);
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getBackTraceCount() {
      return this.countTraces(this.getSchematicInternal(), Layer.BACK);
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean hasFrontTrace(int x, int y) {
      if (x >= 0 && x < 16 && y >= 0 && y < 16) {
         CircuitSchematic schematic = this.getSchematicInternal();
         return schematic != null && schematic.hasTrace(Layer.FRONT, x, y);
      } else {
         return false;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean hasBackTrace(int x, int y) {
      if (x >= 0 && x < 16 && y >= 0 && y < 16) {
         CircuitSchematic schematic = this.getSchematicInternal();
         return schematic != null && schematic.hasTrace(Layer.BACK, x, y);
      } else {
         return false;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCoolingMultiplier() {
      return (double)(this.target).totalCoolingFactorMultiplier;
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getExternalNodeCount() {
      BakedCircuit baked = this.getBakedInternal();
      return baked == null ? 0 : baked.externalNodes.size();
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getInternalNodeCount() {
      BakedCircuit baked = this.getBakedInternal();
      return baked == null ? 0 : baked.internalNodes.size();
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getWireCount() {
      BakedCircuit baked = this.getBakedInternal();
      return baked == null ? 0 : baked.wires.size();
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getThermalUnitCount() {
      BakedCircuit baked = this.getBakedInternal();
      return baked == null ? 0 : baked.thermalUnits.size();
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getTickedComponentCount() {
      BakedCircuit baked = this.getBakedInternal();
      return baked == null ? 0 : baked.tickedComponents.size();
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = this.createBaseData();
      data.put("schematicName", this.getSchematicName());
      data.put("hasSchematic", this.hasSchematic());
      data.put("baked", this.isBaked());
      data.put("damaged", this.isDamaged());
      data.put("componentCount", this.getComponentCount());
      data.put("components", this.getComponents());
      data.put("terminalCount", this.getTerminalCount());
      data.put("frontTraceCount", this.getFrontTraceCount());
      data.put("backTraceCount", this.getBackTraceCount());
      data.put("coolingMultiplier", this.getCoolingMultiplier());
      data.put("externalNodeCount", this.getExternalNodeCount());
      data.put("internalNodeCount", this.getInternalNodeCount());
      data.put("wireCount", this.getWireCount());
      data.put("thermalUnitCount", this.getThermalUnitCount());
      data.put("tickedComponentCount", this.getTickedComponentCount());
      return data;
   }
}
