package com.maxim.powergridcc.control;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import org.patryk3211.powergrid.kinetics.base.TunedBlockEntity;
import org.patryk3211.powergrid.kinetics.rheostat.RheostatBlockEntity;
import org.patryk3211.powergrid.kinetics.variac.VariacBlockEntity;

@EventBusSubscriber(
   modid = "powergridcc"
)
public final class TunedControlManager {
   private static final Map<TunedBlockEntity, ControlState> CONTROLLED_BLOCKS = new ConcurrentHashMap<>();

   private TunedControlManager() {
   }

   public static void enable(TunedBlockEntity blockEntity, double ratio) {
      double clampedRatio = clampRatio(blockEntity, ratio);
      CONTROLLED_BLOCKS.put(blockEntity, new ControlState(clampedRatio));
      applyRatioImmediately(blockEntity, clampedRatio);
   }

   public static void disable(TunedBlockEntity blockEntity) {
      CONTROLLED_BLOCKS.remove(blockEntity);
   }

   public static boolean isEnabled(TunedBlockEntity blockEntity) {
      return CONTROLLED_BLOCKS.containsKey(blockEntity);
   }

   public static void setRatio(TunedBlockEntity blockEntity, double ratio) {
      double clampedRatio = clampRatio(blockEntity, ratio);
      CONTROLLED_BLOCKS.compute(blockEntity, (ignored, previous) -> new ControlState(clampedRatio));
      applyRatioImmediately(blockEntity, clampedRatio);
   }

   public static double getTargetRatio(TunedBlockEntity blockEntity) {
      ControlState state = (ControlState)CONTROLLED_BLOCKS.get(blockEntity);
      return state != null ? state.ratio() : readCurrentRatio(blockEntity);
   }

   public static void holdCurrentRatio(TunedBlockEntity blockEntity) {
      setRatio(blockEntity, readCurrentRatio(blockEntity));
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent.Post event) {
      {
         CONTROLLED_BLOCKS.entrySet().removeIf((entry) -> {
            TunedBlockEntity blockEntity = (TunedBlockEntity)entry.getKey();
            if (blockEntity != null && !blockEntity.isRemoved()) {
               Level level = blockEntity.getLevel();
               if (level != null && !level.isClientSide) {
                  applyRatioImmediately(blockEntity, ((ControlState)entry.getValue()).ratio());
                  return false;
               } else {
                  return false;
               }
            } else {
               return true;
            }
         });
      }
   }

   private static void applyRatioImmediately(TunedBlockEntity blockEntity, double ratio) {
      double armValue = ratioToArmValue(blockEntity, ratio);
      blockEntity.arm.setValue(armValue);
      blockEntity.arm.updateChaseTarget((float)armValue);
      blockEntity.arm.forceNextSync();
      blockEntity.refreshParameters();
      blockEntity.setChanged();
      blockEntity.notifyUpdate();
   }

   private static double readCurrentRatio(TunedBlockEntity blockEntity) {
      if (blockEntity instanceof RheostatBlockEntity rheostat) {
         return (double)rheostat.getRatio();
      } else if (blockEntity instanceof VariacBlockEntity variac) {
         return (double)variac.getRatio();
      } else {
         throw new IllegalArgumentException("Unsupported tuned block: " + blockEntity.getClass().getName());
      }
   }

   private static double ratioToArmValue(TunedBlockEntity blockEntity, double ratio) {
      if (blockEntity instanceof RheostatBlockEntity) {
         return clamp01((ratio - 0.01) / 0.98);
      } else if (blockEntity instanceof VariacBlockEntity) {
         return clamp01((ratio - 0.01) / 0.99);
      } else {
         throw new IllegalArgumentException("Unsupported tuned block: " + blockEntity.getClass().getName());
      }
   }

   private static double clampRatio(TunedBlockEntity blockEntity, double ratio) {
      if (!Double.isFinite(ratio)) {
         throw new IllegalArgumentException("Ratio must be a finite number");
      } else if (blockEntity instanceof RheostatBlockEntity) {
         return Math.max(0.01, Math.min(0.99, ratio));
      } else if (blockEntity instanceof VariacBlockEntity) {
         return Math.max(0.01, Math.min((double)1.0F, ratio));
      } else {
         throw new IllegalArgumentException("Unsupported tuned block: " + blockEntity.getClass().getName());
      }
   }

   private static double clamp01(double value) {
      return Math.max((double)0.0F, Math.min((double)1.0F, value));
   }

   private static record ControlState(double ratio) {
   }
}
