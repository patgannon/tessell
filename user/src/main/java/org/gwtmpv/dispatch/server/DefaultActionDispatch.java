package org.gwtmpv.dispatch.server;

import org.gwtmpv.dispatch.server.handlers.ActionHandler;
import org.gwtmpv.dispatch.server.handlers.ActionHandlerRegistry;
import org.gwtmpv.dispatch.shared.Action;
import org.gwtmpv.dispatch.shared.Result;

/** Stock server-side implementation of {@link ActionDispatch}. */
public class DefaultActionDispatch implements ActionDispatch {

  protected final ActionHandlerRegistry handlers = new ActionHandlerRegistry();

  /** Executes {@code action}. */
  @Override
  public <A extends Action<R>, R extends Result> R execute(final A action, final ExecutionContext context) {
    return findHandler(action).execute(action, context);
  }

  /** Adds {@code handler}. */
  public void addHandler(final ActionHandler<?, ?> handler) {
    handlers.addHandler(handler);
  }

  /** @return the handler for {@code action} or throws {@code IllegalStateException} */
  protected <A extends Action<R>, R extends Result> ActionHandler<A, R> findHandler(final A action) {
    final ActionHandler<A, R> handler = getHandlerRegistry().findHandler(action);
    if (handler == null) {
      throw new IllegalStateException("No handler for " + action);
    }
    return handler;
  }

  protected ActionHandlerRegistry getHandlerRegistry() {
    return handlers;
  }

}
