package org.gwtmpv.dispatch.server.servlet;

import static org.gwtmpv.util.ObjectUtils.eq;

import javax.servlet.GenericServlet;

import org.gwtmpv.dispatch.server.ActionDispatch;
import org.gwtmpv.dispatch.server.ExecutionContext;
import org.gwtmpv.dispatch.server.SessionIdValidator;
import org.gwtmpv.dispatch.shared.Action;
import org.gwtmpv.dispatch.shared.ActionException;
import org.gwtmpv.dispatch.shared.DispatchService;
import org.gwtmpv.dispatch.shared.Result;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Provides a basic {@link DispatchService} implementation that defers to subclasses
 * for the {@link SessionIdValidator} and {@link ActionDispatch} instances.
 */
public abstract class AbstractDispatchServiceServlet extends RemoteServiceServlet implements DispatchService {

  private static final long serialVersionUID = 1L;

  public Result execute(final String sessionId, final Action<?> action) throws ActionException {
    try {
      final ExecutionContext context = new ExecutionContext(getThreadLocalRequest(), getThreadLocalResponse());
      if (getSessionValidator() != null && !eq(sessionId, getSessionValidator().get(context))) {
        throw new IllegalStateException("Invalid session");
      }
      ActionDispatch d = getActionDispatch();
      if (d == null) {
        throw new IllegalStateException("Null ActionDispatch, ensure the server started correctly");
      }
      return d.execute(action, context);
    } catch (final ActionException ae) {
      // assume the user has already logged the ActionException appropriately
      throw ae;
    } catch (final Exception e) {
      log(e);
      throw wrapInActionException(e);
    }
  }

  /** Allows subclasses to override exception logging. By default uses {@link GenericServlet#log}. */
  protected void log(Exception e) {
    log(e.getMessage(), e);
  }

  /** Allows subclasses to create their own "runtime exception" subclass of {@link ActionException}. */
  protected ActionException wrapInActionException(Exception e) {
    return new ActionException("A server error occured."); // don't leak the raw exception message
  }

  protected abstract SessionIdValidator getSessionValidator();

  protected abstract ActionDispatch getActionDispatch();

}
