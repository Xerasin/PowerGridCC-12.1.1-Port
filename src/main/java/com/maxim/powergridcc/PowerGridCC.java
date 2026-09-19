package me.maxim.powergridcc;

import dan200.computercraft.api.ForgeComputerCraftAPI;
import me.maxim.powergridcc.peripheral.PowerGridPeripheralProvider;
import net.minecraftforge.fml.common.Mod;

@Mod("powergridcc")
public class PowerGridCC {
   public static final String MOD_ID = "powergridcc";

   public PowerGridCC() {
      ForgeComputerCraftAPI.registerPeripheralProvider(new PowerGridPeripheralProvider());
      System.out.println("PowerGridCC loaded!");
   }
}
