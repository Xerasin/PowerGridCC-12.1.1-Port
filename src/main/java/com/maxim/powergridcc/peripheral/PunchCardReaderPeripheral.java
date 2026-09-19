package me.maxim.powergridcc.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
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
      return card != null && !card.m_41619_();
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getItemId() {
      return !this.hasCard() ? "" : BuiltInRegistries.f_257033_.m_7981_(this.getCard().m_41720_()).toString();
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getCardName() {
      return !this.hasCard() ? "" : this.getCard().m_41786_().getString();
   }

   @LuaFunction(
      mainThread = true
   )
   public final int getCount() {
      return !this.hasCard() ? 0 : this.getCard().m_41613_();
   }

   @LuaFunction(
      mainThread = true
   )
   public final boolean hasData() {
      return this.hasCard() && this.getCard().m_41782_();
   }

   @LuaFunction(
      mainThread = true
   )
   public final String getRawData() {
      if (!this.hasData()) {
         return "";
      } else {
         CompoundTag tag = this.getCard().m_41783_();
         return tag == null ? "" : tag.toString();
      }
   }

   @LuaFunction(
      mainThread = true
   )
   public final Map<String, Object> getData() {
      Map<String, Object> data = new HashMap();
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
