package me.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.Direction;
import org.patryk3211.powergrid.electricity.fan.ElectricFanBlockEntity;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;

public class ElectricFanPeripheral extends AbstractElectricPeripheral<ElectricFanBlockEntity> {
   public ElectricFanPeripheral(ElectricFanBlockEntity fan) {
      super(fan);
   }

   public String getType() {
      return "powergrid_electric_fan";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_electric", "powergrid_fan");
   }

   private ElectricWire getMotorWire() {
      return (ElectricWire)ReflectionHelper.getField(this.target, "motor", ElectricWire.class);
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getSpeed() {
      return (double)((ElectricFanBlockEntity)this.target).getSpeed();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean isRunning() {
      return Math.abs(this.getSpeed()) > 1.0E-6;
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getAirFlowDirection() {
      Direction direction = ((ElectricFanBlockEntity)this.target).getAirFlowDirection();
      return direction == null ? "unknown" : direction.m_122433_();
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getAirflowOriginSide() {
      Direction direction = ((ElectricFanBlockEntity)this.target).getAirflowOriginSide();
      return direction == null ? "unknown" : direction.m_122433_();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getCurrent() {
      return this.getMotorWire().current();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getVoltage() {
      return this.getMotorWire().potentialDifference();
   }

   @LuaFunction(
      mainThread = true
   )
   public final double getPower() {
      return this.getMotorWire().power();
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = this.createBaseData();
      data.put("speed", this.getSpeed());
      data.put("running", this.isRunning());
      data.put("airFlowDirection", this.getAirFlowDirection());
      data.put("airflowOriginSide", this.getAirflowOriginSide());
      data.put("current", this.getCurrent());
      data.put("voltage", this.getVoltage());
      data.put("power", this.getPower());
      return data;
   }
}
