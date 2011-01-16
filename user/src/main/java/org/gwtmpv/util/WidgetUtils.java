package org.gwtmpv.util;

import static org.gwtmpv.widgets.Widgets.newAnimation;

import org.gwtmpv.widgets.AnimationLogic;
import org.gwtmpv.widgets.HasCss;
import org.gwtmpv.widgets.IsFocusWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.AttachEvent;

public class WidgetUtils {

  public static void fadeOut(final HasCss css) {
    fadeOut(css, 300);
  }

  public static void fadeOut(final HasCss css, int duration) {
    newAnimation(new AnimationLogic() {
      @Override
      public void onUpdate(double progress) {
        css.getStyle().setOpacity(1 - progress);
      }

      @Override
      public void onComplete() {
        css.getStyle().setDisplay(Display.NONE);
      }
    }).run(duration);
  }

  public static void fadeIn(final HasCss css) {
    fadeIn(css, 300);
  }

  public static void fadeIn(final HasCss css, int duration) {
    newAnimation(new AnimationLogic() {
      @Override
      public void onStart() {
        css.getStyle().clearDisplay();
        css.getStyle().setOpacity(0.0);
      }

      @Override
      public void onUpdate(double progress) {
        css.getStyle().setOpacity(progress);
      }
    }).run(duration);
  }

  /** Sets {@code display=none} on each element. */
  public static void hide(HasCss... csses) {
    for (HasCss css : csses) {
      css.getStyle().setDisplay(Display.NONE);
    }
  }

  /** Clears {@code display} on each element. */
  public static void show(HasCss... csses) {
    for (HasCss css : csses) {
      css.getStyle().clearDisplay();
    }
  }

  /** Sets focus on {@code w}, potentially waiting until it's {@link AttachEvent}. */
  public static void focus(final IsFocusWidget w) {
    if (w.isAttached() || !GWT.isClient()) {
      w.setFocus(true);
    } else {
      w.addAttachHandler(new AttachEvent.Handler() {
        public void onAttachOrDetach(AttachEvent event) {
          if (event.isAttached()) {
            w.setFocus(true);
          }
        }
      });
    }
  }

}
