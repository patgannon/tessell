package org.gwtmpv.tests.presenter;

import org.gwtmpv.presenter.BasicPresenter;
import org.gwtmpv.presenter.Presenter;
import org.gwtmpv.widgets.IsWidget;
import org.junit.Test;

public class PresenterTest {

  @Test(expected = IllegalStateException.class)
  public void superOnBindMustBeCalled() {
    final NoSuperBindPresenter p = new NoSuperBindPresenter();
    p.bind();
  }

  @Test(expected = IllegalStateException.class)
  public void superOnUnbindMustBeCalled() {
    final NoSuperUnbindPresenter p = new NoSuperUnbindPresenter();
    p.bind();
    p.unbind();
  }

  @Test
  public void superBindAndUnbindCalled() {
    final BothSuperBindAndUnbindPresenter p = new BothSuperBindAndUnbindPresenter();
    p.bind();
    p.unbind();
  }

  @Test(expected = IllegalStateException.class)
  public void noRebind() {
    final Presenter p = new BasicPresenter<IsWidget>(null) {
    };
    p.bind();
    p.unbind();
    p.bind();
  }

  public class NoSuperBindPresenter extends BasicPresenter<IsWidget> {
    public NoSuperBindPresenter() {
      super(null);
    }

    @Override
    public void onBind() {
      // no super call
    }
  }

  public class NoSuperUnbindPresenter extends BasicPresenter<IsWidget> {
    public NoSuperUnbindPresenter() {
      super(null);
    }

    @Override
    public void onUnbind() {
      // no super call
    }
  }

  public class BothSuperBindAndUnbindPresenter extends BasicPresenter<IsWidget> {
    public BothSuperBindAndUnbindPresenter() {
      super(null);
    }

    @Override
    public void onBind() {
      super.onBind();
    }

    @Override
    public void onUnbind() {
      super.onUnbind();
    }
  }

}
