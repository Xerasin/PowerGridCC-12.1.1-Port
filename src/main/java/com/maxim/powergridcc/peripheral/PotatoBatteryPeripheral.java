package com.maxim.powergridcc.peripheral;

import org.patryk3211.powergrid.electricity.battery.PotatoBatteryBlockEntity;

public class PotatoBatteryPeripheral extends BatteryPeripheral {
   public PotatoBatteryPeripheral(PotatoBatteryBlockEntity battery) {
      super(battery);
   }

   public String getType() {
      return "powergrid_potato_battery";
   }
}
