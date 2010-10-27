package org.gwtmpv.presenter;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.gwtmpv.bus.AbstractBound;
import org.gwtmpv.widgets.IsWidget;

import com.google.gwt.event.shared.EventBus;

/** A basic presenter that tracks bound handler registrations. */
public abstract class BasicPresenter<V extends IsWidget> extends AbstractBound implements Presenter {

  protected final V view;
  protected final EventBus eventBus;
  private ArrayList<Presenter> children;

  public BasicPresenter(final V view, final EventBus eventBus) {
    this.view = view;
    this.eventBus = eventBus;
  }

  /** @return The view for the presenter. */
  public V getView() {
    if (!isBound()) {
      throw new IllegalStateException(this + " has not been bound");
    }
    return view;
  }

  @Override
  protected void onBind() {
    super.onBind();
    // bind anybody that got added before we were bound
    if (children != null) {
      for (final Presenter child : children) {
        child.bind();
      }
    }
  }

  @Override
  protected void onUnbind() {
    super.onUnbind();
    if (children != null) {
      for (final Presenter child : children) {
        child.unbind();
      }
    }
  }

  /** Adds {@code child} as a child presenter, and binds it if we're already bound. */
  protected <C extends Presenter> C addPresenter(final C child) {
    if (children().add(child)) {
      if (isBound()) {
        child.bind();
      }
    }
    return child;
  }

  /** Removes {@code child} as a child presenter, and unbinds it. */
  protected void removePresenter(final Presenter child) {
    if (children().remove(child)) {
      child.unbind();
    } else {
      throw new NoSuchElementException("Presenter was not a child of ours " + child);
    }
  }

  /** @return our list of child presenters, lazily instantiated. */
  private ArrayList<Presenter> children() {
    if (children == null) {
      children = new ArrayList<Presenter>();
    }
    return children;
  }

}
