package com.maxim.powergridcc.peripheral;

import dan200.computercraft.api.peripheral.IPeripheral;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.patryk3211.powergrid.circuits.circuitboard.CircuitBoardBlockEntity;
import org.patryk3211.powergrid.circuits.editor.CircuitDesignTableBlockEntity;
import org.patryk3211.powergrid.electricity.basinheater.BasinHeaterBlockEntity;
import org.patryk3211.powergrid.electricity.battery.BatteryBlockEntity;
import org.patryk3211.powergrid.electricity.battery.PotatoBatteryBlockEntity;
import org.patryk3211.powergrid.electricity.bell.AlarmBellBlockEntity;
import org.patryk3211.powergrid.electricity.carbonpile.CarbonPileBlockEntity;
import org.patryk3211.powergrid.electricity.carbonpile.CarbonPileCoilBlockEntity;
import org.patryk3211.powergrid.electricity.contactor.ContactorBlockEntity;
import org.patryk3211.powergrid.electricity.creative.CreativeResistorBlockEntity;
import org.patryk3211.powergrid.electricity.creative.CreativeSourceBlockEntity;
import org.patryk3211.powergrid.electricity.crt.CRTBlockEntity;
import org.patryk3211.powergrid.electricity.deviceconnector.DeviceConnectorBlockEntity;
import org.patryk3211.powergrid.electricity.electricswitch.HvBreakerBlockEntity;
import org.patryk3211.powergrid.electricity.electricswitch.HvSwitchBlockEntity;
import org.patryk3211.powergrid.electricity.electricswitch.SwitchBlockEntity;
import org.patryk3211.powergrid.electricity.electromagnet.ElectromagnetBlockEntity;
import org.patryk3211.powergrid.electricity.fan.ElectricFanBlockEntity;
import org.patryk3211.powergrid.electricity.fuse.FuseHolderBlockEntity;
import org.patryk3211.powergrid.electricity.gauge.GaugeBlockEntity;
import org.patryk3211.powergrid.electricity.grounding.GroundingRodBlockEntity;
import org.patryk3211.powergrid.electricity.heater.HeaterBlockEntity;
import org.patryk3211.powergrid.electricity.light.fixture.LightFixtureBlockEntity;
import org.patryk3211.powergrid.electricity.resistor.ResistorBlockEntity;
import org.patryk3211.powergrid.electricity.sparkgap.SparkGapBlockEntity;
import org.patryk3211.powergrid.electricity.transformer.TransformerBlockEntity;
import org.patryk3211.powergrid.equipment.portablebattery.PortableBatteryBlockEntity;
import org.patryk3211.powergrid.equipment.thermometer.ThermometerBlockEntity;
import org.patryk3211.powergrid.kinetics.generator.clutch.GeneratorClutchBlockEntity;
import org.patryk3211.powergrid.kinetics.generator.inductionrotor.CommutatorBlockEntity;
import org.patryk3211.powergrid.kinetics.generator.rotor.RotorBlockEntity;
import org.patryk3211.powergrid.kinetics.generator.winding.WindingBlockEntity;
import org.patryk3211.powergrid.kinetics.motor.ConstantSpeedMotorBlockEntity;
import org.patryk3211.powergrid.kinetics.motor.ElectricMotorBlockEntity;
import org.patryk3211.powergrid.kinetics.plotter.PlotterBlockEntity;
import org.patryk3211.powergrid.kinetics.punchcard.PunchCardReaderBlockEntity;
import org.patryk3211.powergrid.kinetics.rheostat.RheostatBlockEntity;
import org.patryk3211.powergrid.kinetics.servo.ServoBlockEntity;
import org.patryk3211.powergrid.kinetics.variac.VariacBlockEntity;

public class PowerGridPeripheralProvider {
   @Nullable
   public IPeripheral getPeripheral(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable BlockEntity blockEntity, @Nonnull Direction side) {
      if (blockEntity == null) {
         return null;
      }

      return getPeripheral(blockEntity);
   }

   @Nullable
   public IPeripheral getPeripheral(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Direction side) {
      return getPeripheral(level.getBlockEntity(pos));
   }

   @Nullable
   private IPeripheral getPeripheral(@Nullable BlockEntity blockEntity) {
      if (blockEntity == null) {
         return null;
      }

      if (blockEntity instanceof GaugeBlockEntity gauge) {
         return new GaugePeripheral(gauge);
      } else if (blockEntity instanceof PotatoBatteryBlockEntity battery) {
         return new PotatoBatteryPeripheral(battery);
      } else if (blockEntity instanceof BatteryBlockEntity battery) {
         return new BatteryPeripheral(battery);
      } else if (blockEntity instanceof PortableBatteryBlockEntity battery) {
         return new PortableBatteryPeripheral(battery);
      } else if (blockEntity instanceof CreativeSourceBlockEntity source) {
         return new CreativeSourcePeripheral(source);
      } else if (blockEntity instanceof ElectricFanBlockEntity fan) {
         return new ElectricFanPeripheral(fan);
      } else if (blockEntity instanceof AlarmBellBlockEntity bell) {
         return new AlarmBellPeripheral(bell);
      } else if (blockEntity instanceof LightFixtureBlockEntity fixture) {
         return new LightFixturePeripheral(fixture);
      } else if (blockEntity instanceof SparkGapBlockEntity sparkGap) {
         return new SparkGapPeripheral(sparkGap);
      } else if (blockEntity instanceof ThermometerBlockEntity thermometer) {
         return new ThermometerPeripheral(thermometer);
      } else if (blockEntity instanceof PlotterBlockEntity plotter) {
         return new PlotterPeripheral(plotter);
      } else if (blockEntity instanceof CommutatorBlockEntity commutator) {
         return new GeneratorPeripheral(commutator);
      } else if (blockEntity instanceof WindingBlockEntity winding) {
         return new WindingPeripheral(winding);
      } else if (blockEntity instanceof GeneratorClutchBlockEntity clutch) {
         return new GeneratorClutchPeripheral(clutch);
      } else if (blockEntity instanceof RotorBlockEntity rotor) {
         return new RotorPeripheral(rotor);
      } else if (blockEntity instanceof GroundingRodBlockEntity groundingRod) {
         return new GroundingRodPeripheral(groundingRod);
      } else if (blockEntity instanceof DeviceConnectorBlockEntity connector) {
         return new DeviceConnectorPeripheral(connector);
      } else if (blockEntity instanceof ServoBlockEntity servo) {
         return new ServoPeripheral(servo);
      } else if (blockEntity instanceof ElectricMotorBlockEntity motor) {
         return new MotorPeripheral(motor);
      } else if (blockEntity instanceof ConstantSpeedMotorBlockEntity motor) {
         return new ConstantSpeedMotorPeripheral(motor);
      } else if (blockEntity instanceof HvBreakerBlockEntity breaker) {
         return new HvBreakerPeripheral(breaker);
      } else if (blockEntity instanceof ContactorBlockEntity contactor) {
         return new ContactorPeripheral(contactor);
      } else if (blockEntity instanceof SwitchBlockEntity electricSwitch) {
         return new ElectricSwitchPeripheral(electricSwitch);
      } else if (blockEntity instanceof HvSwitchBlockEntity hvSwitch) {
         return new HvSwitchPeripheral(hvSwitch);
      } else if (blockEntity instanceof CreativeResistorBlockEntity resistor) {
         return new CreativeResistorPeripheral(resistor);
      } else if (blockEntity instanceof ResistorBlockEntity resistor) {
         return new ResistorPeripheral(resistor);
      } else if (blockEntity instanceof CarbonPileCoilBlockEntity coil) {
         return new CarbonPileCoilPeripheral(coil);
      } else if (blockEntity instanceof CarbonPileBlockEntity carbonPile) {
         return new CarbonPilePeripheral(carbonPile);
      } else if (blockEntity instanceof RheostatBlockEntity rheostat) {
         return new RheostatPeripheral(rheostat);
      } else if (blockEntity instanceof VariacBlockEntity variac) {
         return new VariacPeripheral(variac);
      } else if (blockEntity instanceof HeaterBlockEntity heater) {
         return new HeaterPeripheral(heater);
      } else if (blockEntity instanceof ElectromagnetBlockEntity electromagnet) {
         return new ElectromagnetPeripheral(electromagnet);
      } else if (blockEntity instanceof FuseHolderBlockEntity fuseHolder) {
         return new FuseHolderPeripheral(fuseHolder);
      } else if (blockEntity instanceof BasinHeaterBlockEntity basinHeater) {
         return new BasinHeaterPeripheral(basinHeater);
      } else if (blockEntity instanceof PunchCardReaderBlockEntity reader) {
         return new PunchCardReaderPeripheral(reader);
      } else if (blockEntity instanceof TransformerBlockEntity transformer) {
         return new TransformerPeripheral(transformer);
      } else if (blockEntity instanceof CRTBlockEntity crt) {
         return new CRTPeripheral(crt);
      } else if (blockEntity instanceof CircuitBoardBlockEntity board) {
         return new CircuitBoardPeripheral(board);
      } else if (blockEntity instanceof CircuitDesignTableBlockEntity table) {
         return new CircuitDesignTablePeripheral(table);
      } else {
         return null;
      }
   }
}
