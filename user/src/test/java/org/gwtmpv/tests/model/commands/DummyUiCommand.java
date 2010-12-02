package org.gwtmpv.tests.model.commands;

import org.gwtmpv.model.commands.UiCommand;

/** Fails depending on the instance variable {@code fail}. */
public final class DummyUiCommand extends UiCommand {

  private boolean fail = false;
  private int executions = 0;

  @Override
  protected void doExecute() {
    executions++;
    if (fail) {
      error("failed!");
    }
  }

  public void setFail(boolean fail) {
    this.fail = fail;
  }

  public int getExecutions() {
    return executions;
  }

}