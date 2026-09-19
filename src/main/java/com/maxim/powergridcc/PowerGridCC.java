package com.maxim.powergridcc;

import dan200.computercraft.api.peripheral.PeripheralCapability;
import com.maxim.powergridcc.peripheral.PowerGridPeripheralProvider;
import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.fml.common.Mod;

@Mod("powergridcc")
public class PowerGridCC {
   public static final String MOD_ID = "powergridcc";

   public PowerGridCC(IEventBus modEventBus) {
      modEventBus.addListener(PowerGridCC::registerCapabilities);
      System.out.println("PowerGridCC loaded!");
   }

   public static void registerCapabilities(RegisterCapabilitiesEvent event) {
      PowerGridPeripheralProvider provider = new PowerGridPeripheralProvider();
      Block[] powerGridBlocks = BuiltInRegistries.BLOCK.entrySet().stream()
         .filter(Objects::nonNull)
         .filter(entry -> entry.getKey() != null)
         .filter(entry -> entry.getKey().location() != null)
            .filter(entry -> entry.getKey().location().getNamespace().equals("powergrid"))
         .map(entry -> Objects.requireNonNull(entry.getValue(), "Registered block value must not be null"))
            .toArray(Block[]::new);

      event.registerBlock(
         Objects.requireNonNull(PeripheralCapability.get(), "Peripheral capability must not be null"),
         provider::getPeripheral,
         Objects.requireNonNull(powerGridBlocks, "Power Grid block list must not be null"));
   }
}
