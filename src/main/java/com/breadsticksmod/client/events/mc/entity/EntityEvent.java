package com.breadsticksmod.client.events.mc.entity;

import com.breadsticksmod.client.util.ChatUtil;
import com.breadsticksmod.core.events.BaseEvent;
import com.breadsticksmod.core.time.ChronoUnit;
import com.breadsticksmod.core.util.TempMap;
import com.wynntils.mc.event.AddEntityEvent;
import com.wynntils.mc.event.TickEvent;
import com.wynntils.models.worlds.event.WorldStateEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public abstract class EntityEvent extends BaseEvent {
   private final Entity entity;
   private final double xa;
   private final double ya;
   private final double za;

   private EntityEvent(Entity entity) {
      this.entity = entity;

      if (entity != null) {
         Vec3 vec3 = entity.getDeltaMovement();

         this.xa = (int) (Mth.clamp(vec3.x, -3.9, 3.9) * 8000.0);
         this.ya = (int) (Mth.clamp(vec3.y, -3.9, 3.9) * 8000.0);
         this.za = (int) (Mth.clamp(vec3.z, -3.9, 3.9) * 8000.0);
      } else {
         this.xa = 0;
         this.ya = 0;
         this.za = 0;
      }
   }

   public Entity getEntity() {
      return entity;
   }

   public UUID getUuid() {
      return entity.getUUID();
   }

   public EntityType<?> getType() {
      return entity.getType();
   }

   public Position getPosition() {
      return entity.position();
   }

   public double getX() {
      return entity.getX();
   }

   public double getY() {
      return entity.getY();
   }

   public double getZ() {
      return entity.getZ();
   }

   public double getXa() {
      return xa;
   }

   public double getYa() {
      return ya;
   }

   public double getZa() {
      return za;
   }

   public float getxRot() {
      return entity.getXRot();
   }

   public float getyRot() {
      return entity.getYRot();
   }

   public float getyHeadRot() {
      return entity.getYHeadRot();
   }

   public static class Spawn extends EntityEvent {
      private static final TempMap<UUID, Entity> WAITING = new TempMap<>(50, ChronoUnit.MILLISECONDS);

      public Spawn(Entity entity) {
         super(entity);
      }

      @SubscribeEvent
      private static void onEntityAdd(AddEntityEvent event) {
         WAITING.put(event.getUuid(), event.getEntity());
      }

      @SubscribeEvent
      private static void onEntitySetData(SetData data) {
         var entity = WAITING.remove(data.getEntity().getUUID());
         if (entity != null) new Spawn(entity).post();
      }

      @SubscribeEvent
      private static void onTick(TickEvent event) {
         WAITING.cleanup(e -> new Spawn(e).post());
      }

      @SubscribeEvent
      public static void onJoinWorld(WorldStateEvent event) {
         // just gonna hijack this class rq to send this message with
         if (event.isFirstJoinWorld()) {
            ChatUtil.message("There is currently a bug causing configs to not load on startup. Until it is fixed, the workaround is to open the config (/bs config), change any option, then save. After that the config should function properly until you restart Minecraft.", ChatFormatting.GRAY);
         }
      }

      @Deprecated
      public Spawn() {
         this(null);
      }
   }

   public static class Remove extends EntityEvent {
      private final Entity.RemovalReason reason;

      public Remove(Entity entity, Entity.RemovalReason reason) {
         super(entity);

         this.reason = reason;
      }

      public Entity.RemovalReason getRemovalReason() {
         return reason;
      }

      @Deprecated
      public Remove() {
         this(null, null);
      }
   }

   public static class SetData extends EntityEvent {
      public SetData(Entity entity) {
         super(entity);
      }

      @Deprecated
      public SetData() {
         this(null);
      }
   }

   @Deprecated
   public EntityEvent() {
      this(null);
   }
}
