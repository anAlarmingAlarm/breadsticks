package com.breadsticksmod.core.http.api.guild;

import com.breadsticksmod.core.http.api.Printable;

public interface GuildType extends Printable {
   String name();
   String prefix();

   @Override
   default String prettyPrint() {
      return name() + " [" + prefix() + "]";
   }

   static GuildType copyOf(GuildType type) {
      if (type instanceof GuildTypeImpl)
         return type;

      return new GuildTypeImpl(type.name(), type.prefix());
   }

   static GuildType valueOf(String name, String prefix) {
      return new GuildTypeImpl(name, prefix);
   }
}
