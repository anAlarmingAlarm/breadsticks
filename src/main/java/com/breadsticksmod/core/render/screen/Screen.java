package com.breadsticksmod.core.render.screen;

import com.breadsticksmod.core.events.EventListener;
import com.breadsticksmod.core.render.TextureInfo;
import com.breadsticksmod.core.render.screen.elements.RectElement;
import com.breadsticksmod.core.render.screen.elements.TextElement;
import com.breadsticksmod.core.render.screen.elements.TextureElement;
import com.breadsticksmod.core.render.screen.widgets.ItemStackWidget;
import com.breadsticksmod.core.render.screen.widgets.SearchBoxWidget;
import com.breadsticksmod.core.render.screen.widgets.VerticalScrollbarWidget;
import com.wynntils.core.text.StyledText;
import com.wynntils.screens.base.TextboxScreen;
import com.wynntils.screens.base.widgets.TextInputBoxWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public @interface Screen {
   abstract class Element extends net.minecraft.client.gui.screens.Screen implements TextboxScreen, EventListener, Closeable {
      final Deque<ScreenElement<?>> elements = new LinkedList<>();
      final List<Widget<?>> widgets = new ArrayList<>();

      private TextInputBoxWidget ACTIVE_SEARCH = null;

      protected Element(Component component) {
         super(component);
      }

      @Override
      public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
         elements.clear();

         MultiBufferSource.BufferSource buffer = graphics.bufferSource();

         onRender(
                 graphics,
                 buffer,
                 mouseX,
                 mouseY,
                 partialTick
         );

         elements.forEach(e -> e.render(graphics, buffer, mouseX, mouseY, partialTick));

         elements.clear();

         widgets.forEach(widget -> widget.render(graphics, buffer, mouseX, mouseY, partialTick));
         elements.forEach(e -> e.render(graphics, buffer, mouseX, mouseY, partialTick));

         super.render(graphics, mouseX, mouseY, partialTick);

         buffer.endBatch();
      }

      @Override
      public void renderBackground(@NotNull GuiGraphics guiGraphics, int i, int j, float f) {

      }

      @Override
      protected void init() {
         super.init();

         REGISTER_EVENTS();
      }

      @Override
      public void removed() {
         UNREGISTER_EVENTS();

         try {
            close();
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }

      protected abstract void onRender(@NotNull GuiGraphics graphics, MultiBufferSource.BufferSource bufferSource, int mouseX, int mouseY, float partialTick);

      @SuppressWarnings("unchecked")
      protected void addWidget(Widget<?> widget) {
         ((List<GuiEventListener>) this.children()).add(widget);
         widgets.add(widget);
      }

      protected List<Widget<?>> getWidgets() {
         return widgets;
      }

      public void clear() {
         children().clear();
         widgets.clear();
      }

      @Override
      public TextInputBoxWidget getFocusedTextInput() {
         return ACTIVE_SEARCH;
      }

      @Override
      public void setFocusedTextInput(TextInputBoxWidget textInputBoxWidget) {
         this.ACTIVE_SEARCH = textInputBoxWidget;
      }

      public class Background extends ScreenElement<Background> {
         @Override
         public Element getElement() {
            return Element.this;
         }

         @Override
         public void render(@NotNull GuiGraphics graphics, MultiBufferSource.BufferSource bufferSource, int mouseX, int mouseY, float partialTick) {
            Element.super.renderBackground(graphics, mouseX, mouseY, partialTick);
         }
      }

      public class Text extends TextElement<Text> {

         public Text(StyledText text, float x, float y) {
            super(text, x, y);
         }

         public Text(Component text, float x, float y) {
            super(text, x, y);
         }

         public Text(String string, float x, float y) {
            super(string, x, y);
         }

         @Override
         public Element getElement() {
            return Element.this;
         }
      }

      public class Texture extends TextureElement<Texture> {
         public Texture() {}

         public Texture(TextureInfo texture) {
            setTexture(texture);
         }

         @Override
         public Element getElement() {
            return Element.this;
         }
      }

      public class Rect extends RectElement<Rect> {

         @Override
         public Element getElement() {
            return Element.this;
         }
      }

      public class Item extends ItemStackWidget<Item> {
         public Item() {
            super();
         }

         public Item(ItemStack item) {
            setItem(item);
         }

         @Override
         public Element getElement() {
            return Element.this;
         }
      }

      public class ItemTooltip extends ItemStackWidget.Tooltip<ItemTooltip> {
         public ItemTooltip() {}

         public ItemTooltip(ItemStack item) {
            setItem(item);
         }

         public ItemTooltip(Item item) {
            super(item);
         }

         @Override
         public Element getElement() {
            return Element.this;
         }
      }

      public class SearchBox extends SearchBoxWidget<SearchBox> {
         public SearchBox(int x, int y, int width, int height, Consumer<String> onUpdateConsumer, TextboxScreen textboxScreen) {
            super(x, y, width, height, onUpdateConsumer, textboxScreen);
         }

         @Override
         public Element getElement() {
            return Element.this;
         }
      }

      public class VerticalScrollbar extends VerticalScrollbarWidget<VerticalScrollbar> {
         public VerticalScrollbar() {}

         @Override
         public Element getElement() {
            return Element.this;
         }
      }
   }

   interface Widget<This extends Widget<This>> extends Object<This, Widget<?>>, GuiEventListener {
      void render(@NotNull GuiGraphics graphics, MultiBufferSource.BufferSource bufferSource, int mouseX, int mouseY, float partialTick);

      default This build() {
         getElement().addWidget(this);
         return getThis();
      }
   }

   interface Object<This extends Object<This, Type>, Type extends Object<?, Type>> {
      Element getElement();

      @SuppressWarnings("unchecked")
      default This getThis() {
         return (This) this;
      }

      default This perform(Consumer<This> consumer) {
         consumer.accept(getThis());

         return getThis();
      }

      This build();

      default <T extends Type> T then(Supplier<T> next) {
         build();

         return next.get();
      };
   }
}
