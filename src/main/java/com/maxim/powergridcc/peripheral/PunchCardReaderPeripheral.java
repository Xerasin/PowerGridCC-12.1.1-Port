package com.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import org.patryk3211.powergrid.kinetics.punchcard.PunchCardReaderBlockEntity;

public class PunchCardReaderPeripheral extends AbstractPowerGridPeripheral<PunchCardReaderBlockEntity> {
   public PunchCardReaderPeripheral(PunchCardReaderBlockEntity reader) {
      super(reader);
   }

   public String getType() {
      return "powergrid_punch_card_reader";
   }

   public Set<String> getAdditionalTypes() {
      return Set.of("powergrid_card_reader", "inventory");
   }

   private ItemStack getCard() {
      return ((PunchCardReaderBlockEntity)this.target).currentItem();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean hasCard() {
      ItemStack card = this.getCard();
      return card != null && !card.isEmpty();
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getItemId() {
      ItemStack card = this.getCard();
      Item item = card.getItem();
   
      if (item == null || BuiltInRegistries.ITEM.getKey(item) == null) {
         return "";
      } else {
         return BuiltInRegistries.ITEM.getKey(item).toString();
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getCardName() {
      return !this.hasCard() ? "" : this.getCard().getHoverName().getString();
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getCount() {
      return !this.hasCard() ? 0 : this.getCard().getCount();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean hasData() {
      final DataComponentType<CustomData> custom_DATA2 = DataComponents.CUSTOM_DATA;
      if (custom_DATA2 != null) {
         return this.hasCard() && this.getCard().has(custom_DATA2);
      } else {
         return false;
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getRawData() {
      if (!this.hasData()) {
         return "";
      } else {
         final DataComponentType<CustomData> custom_DATA2 = DataComponents.CUSTOM_DATA;
         if (custom_DATA2 == null) {
            return "";
         } else {
            ItemStack card = this.getCard();
            if (card == null || card.isEmpty()) {
               return "";
            }
            CustomData tag = this.getCard().get(custom_DATA2);
            if (tag == null) {
               return "";
            }

            CompoundTag cTag = tag.copyTag();
            return cTag == null ? "" : cTag.toString();
         }
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = new HashMap<>();
      data.put("type", this.getType());
      data.put("hasCard", this.hasCard());
      if (this.hasCard()) {
         data.put("item", this.getItemId());
         data.put("name", this.getCardName());
         data.put("count", this.getCount());
         data.put("hasData", this.hasData());
         data.put("rawData", this.getRawData());
      }

      return data;
   }
}
