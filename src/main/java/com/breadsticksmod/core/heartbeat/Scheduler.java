package com.breadsticksmod.core.heartbeat;

import com.breadsticksmod.core.util.Reflection;

public interface Scheduler {
   default boolean SHOULD_EXECUTE(Task task) {
      return true;
   }

   default void REGISTER_TASKS() {
      Heartbeat.getTasks(getClass(), method -> !Reflection.isStatic(method)).forEach(method -> Heartbeat.register(method, this, this::SHOULD_EXECUTE));
   }

   default void UNREGISTER_TASKS() {
      Heartbeat.getTasks(getClass(), method -> !Reflection.isStatic(method)).forEach(method -> Heartbeat.remove(method, this));
   }
}
