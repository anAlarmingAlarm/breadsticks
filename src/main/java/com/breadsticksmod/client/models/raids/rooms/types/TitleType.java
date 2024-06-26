package com.breadsticksmod.client.models.raids.rooms.types;

import com.breadsticksmod.client.models.raids.rooms.BaseBuilder;
import com.breadsticksmod.client.models.raids.rooms.Room;
import com.wynntils.core.text.PartStyle;
import com.wynntils.core.text.StyledText;
import com.wynntils.mc.event.TitleSetTextEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TitleType extends Room {
   private final Object title;
   private final PartStyle.StyleType styleType;

   public TitleType(String title, Object title1, PartStyle.StyleType styleType) {
      super(title);
      this.title = title1;
      this.styleType = styleType;
   }

   private boolean equals(StyledText text) {
      if (title instanceof String string) {
         return text.equalsString(string, styleType);
      } else return text.equals(title);
   }

   @SubscribeEvent
   public void onTitle(TitleSetTextEvent event) {
      var text = StyledText.fromComponent(event.getComponent());

      if (equals(text)) complete();
   }

   public static class Builder extends BaseBuilder {
      private Object title1;
      private PartStyle.StyleType styleType = PartStyle.StyleType.DEFAULT;

      public Builder(String title) {
         super(title);
      }

      public Builder string(String title) {
         return string(title, PartStyle.StyleType.NONE);
      }

      public Builder string(String title, PartStyle.StyleType type) {
         this.title1 = title;
         this.styleType = type;

         return this;
      }

      public Builder text(StyledText title) {
         this.title1 = title;

         return this;
      }
      @Override
      public Room build() {
         return new TitleType(title, title1, styleType);
      }
   }

   public TitleType() {
      this(null, null, null);
   }
}
