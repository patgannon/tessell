package org.gwtmpv.tests.presenter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.gwtmpv.presenter.BasicPresenter;
import org.gwtmpv.presenter.Presenter;
import org.gwtmpv.widgets.IsWidget;
import org.junit.Test;

public class PresenterChildrenTest {

  @Test
  public void addingInConstructorDoesNotBindRightAway() {
    final ChildPresenter child = new ChildPresenter();
    final AddInConstructorPresenter p = new AddInConstructorPresenter(child);
    assertThat(child.wasBound, is(false));

    p.bind();
    assertThat(child.wasBound, is(true));
  }

  @Test
  public void addingInOnBindDoesBind() {
    final ChildPresenter child = new ChildPresenter();
    final AddInOnBindPresenter p = new AddInOnBindPresenter(child);
    assertThat(child.wasBound, is(false));

    p.bind();
    assertThat(child.wasBound, is(true));
  }

  public class AddInConstructorPresenter extends BasicPresenter<IsWidget> {
    public AddInConstructorPresenter(final Presenter child) {
      super(null);
      addPresenter(child);
    }
  }

  public class AddInOnBindPresenter extends BasicPresenter<IsWidget> {
    private final Presenter child;

    public AddInOnBindPresenter(final Presenter child) {
      super(null);
      this.child = child;
    }

    @Override
    protected void onBind() {
      super.onBind();
      addPresenter(child);
    }
  }

  public class ChildPresenter extends BasicPresenter<IsWidget> {
    public boolean wasBound = false;

    public ChildPresenter() {
      super(null);
    }

    @Override
    public void onBind() {
      super.onBind();
      wasBound = true;
    }
  }
}
