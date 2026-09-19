package me.maxim.powergridcc.peripheral;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
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

public class PowerGridPeripheralProvider implements IPeripheralProvider {
   public LazyOptional<IPeripheral> getPeripheral(Level level, BlockPos pos, Direction side) {
      BlockEntity blockEntity = level.m_7702_(pos);
      if (blockEntity instanceof GaugeBlockEntity gauge) {
         return LazyOptional.of(() -> new GaugePeripheral(gauge));
      } else if (blockEntity instanceof PotatoBatteryBlockEntity battery) {
         return LazyOptional.of(() -> new PotatoBatteryPeripheral(battery));
      } else if (blockEntity instanceof BatteryBlockEntity battery) {
         return LazyOptional.of(() -> new BatteryPeripheral(battery));
      } else if (blockEntity instanceof PortableBatteryBlockEntity battery) {
         return LazyOptional.of(() -> new PortableBatteryPeripheral(battery));
      } else if (blockEntity instanceof CreativeSourceBlockEntity source) {
         return LazyOptional.of(() -> new CreativeSourcePeripheral(source));
      } else if (blockEntity instanceof ElectricFanBlockEntity fan) {
         return LazyOptional.of(() -> new ElectricFanPeripheral(fan));
      } else if (blockEntity instanceof AlarmBellBlockEntity bell) {
         return LazyOptional.of(() -> new AlarmBellPeripheral(bell));
      } else if (blockEntity instanceof LightFixtureBlockEntity fixture) {
         return LazyOptional.of(() -> new LightFixturePeripheral(fixture));
      } else if (blockEntity instanceof SparkGapBlockEntity sparkGap) {
         return LazyOptional.of(() -> new SparkGapPeripheral(sparkGap));
      } else if (blockEntity instanceof ThermometerBlockEntity thermometer) {
         return LazyOptional.of(() -> new ThermometerPeripheral(thermometer));
      } else if (blockEntity instanceof PlotterBlockEntity plotter) {
         return LazyOptional.of(() -> new PlotterPeripheral(plotter));
      } else if (blockEntity instanceof CommutatorBlockEntity commutator) {
         return LazyOptional.of(() -> new GeneratorPeripheral(commutator));
      } else if (blockEntity instanceof WindingBlockEntity winding) {
         return LazyOptional.of(() -> new WindingPeripheral(winding));
      } else if (blockEntity instanceof GeneratorClutchBlockEntity clutch) {
         return LazyOptional.of(() -> new GeneratorClutchPeripheral(clutch));
      } else if (blockEntity instanceof RotorBlockEntity rotor) {
         return LazyOptional.of(() -> new RotorPeripheral(rotor));
      } else if (blockEntity instanceof GroundingRodBlockEntity groundingRod) {
         return LazyOptional.of(() -> new GroundingRodPeripheral(groundingRod));
      } else if (blockEntity instanceof DeviceConnectorBlockEntity connector) {
         return LazyOptional.of(() -> new DeviceConnectorPeripheral(connector));
      } else if (blockEntity instanceof ServoBlockEntity servo) {
         return LazyOptional.of(() -> new ServoPeripheral(servo));
      } else if (blockEntity instanceof ElectricMotorBlockEntity motor) {
         return LazyOptional.of(() -> new MotorPeripheral(motor));
      } else if (blockEntity instanceof ConstantSpeedMotorBlockEntity motor) {
         return LazyOptional.of(() -> new ConstantSpeedMotorPeripheral(motor));
      } else if (blockEntity instanceof HvBreakerBlockEntity breaker) {
         return LazyOptional.of(() -> new HvBreakerPeripheral(breaker));
      } else if (blockEntity instanceof ContactorBlockEntity contactor) {
         return LazyOptional.of(() -> new ContactorPeripheral(contactor));
      } else if (blockEntity instanceof SwitchBlockEntity electricSwitch) {
         return LazyOptional.of(() -> new ElectricSwitchPeripheral(electricSwitch));
      } else if (blockEntity instanceof HvSwitchBlockEntity hvSwitch) {
         return LazyOptional.of(() -> new HvSwitchPeripheral(hvSwitch));
      } else if (blockEntity instanceof CreativeResistorBlockEntity resistor) {
         return LazyOptional.of(() -> new CreativeResistorPeripheral(resistor));
      } else if (blockEntity instanceof ResistorBlockEntity resistor) {
         return LazyOptional.of(() -> new ResistorPeripheral(resistor));
      } else if (blockEntity instanceof CarbonPileCoilBlockEntity coil) {
         return LazyOptional.of(() -> new CarbonPileCoilPeripheral(coil));
      } else if (blockEntity instanceof CarbonPileBlockEntity carbonPile) {
         return LazyOptional.of(() -> new CarbonPilePeripheral(carbonPile));
      } else if (blockEntity instanceof RheostatBlockEntity rheostat) {
         return LazyOptional.of(() -> new RheostatPeripheral(rheostat));
      } else if (blockEntity instanceof VariacBlockEntity variac) {
         return LazyOptional.of(() -> new VariacPeripheral(variac));
      } else if (blockEntity instanceof HeaterBlockEntity heater) {
         return LazyOptional.of(() -> new HeaterPeripheral(heater));
      } else if (blockEntity instanceof ElectromagnetBlockEntity electromagnet) {
         return LazyOptional.of(() -> new ElectromagnetPeripheral(electromagnet));
      } else if (blockEntity instanceof FuseHolderBlockEntity fuseHolder) {
         return LazyOptional.of(() -> new FuseHolderPeripheral(fuseHolder));
      } else if (blockEntity instanceof BasinHeaterBlockEntity basinHeater) {
         return LazyOptional.of(() -> new BasinHeaterPeripheral(basinHeater));
      } else if (blockEntity instanceof PunchCardReaderBlockEntity reader) {
         return LazyOptional.of(() -> new PunchCardReaderPeripheral(reader));
      } else if (blockEntity instanceof TransformerBlockEntity transformer) {
         return LazyOptional.of(() -> new TransformerPeripheral(transformer));
      } else if (blockEntity instanceof CRTBlockEntity crt) {
         return LazyOptional.of(() -> new CRTPeripheral(crt));
      } else if (blockEntity instanceof CircuitBoardBlockEntity board) {
         return LazyOptional.of(() -> new CircuitBoardPeripheral(board));
      } else if (blockEntity instanceof CircuitDesignTableBlockEntity table) {
         return LazyOptional.of(() -> new CircuitDesignTablePeripheral(table));
      } else {
         return LazyOptional.empty();
      }
   }
}
