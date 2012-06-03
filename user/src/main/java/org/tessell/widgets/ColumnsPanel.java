package org.tessell.widgets;

import java.util.ArrayList;
import java.util.Iterator;

import org.tessell.gwt.dom.client.GwtElement;
import org.tessell.gwt.dom.client.IsElement;
import org.tessell.gwt.dom.client.IsStyle;
import org.tessell.gwt.user.client.ui.IsColumnsPanel;
import org.tessell.gwt.user.client.ui.IsWidgetIteratorAdaptor;
import org.tessell.gwt.user.client.ui.StubColumnsPanel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/** Adds displays to consecutive {@link FlowPanel}s as needed. */
@OtherTypes(intf = IsColumnsPanel.class, stub = StubColumnsPanel.class)
public class ColumnsPanel extends FlowPanel implements IsColumnsPanel {

  private final ArrayList<FlowPanel> columns = new ArrayList<FlowPanel>();
  private String columnStyleName;

  @Override
  public void add(final int column, final IsWidget isWidget) {
    while (column >= columns.size()) {
      final FlowPanel p = new FlowPanel();
      if (columnStyleName != null) {
        p.addStyleName(columnStyleName);
      }
      columns.add(p);
      this.add(p);
    }
    columns.get(column).add(isWidget.asWidget());
  }

  public String getColumnStyleName() {
    return columnStyleName;
  }

  public void setColumnStyleName(final String columnStyleName) {
    this.columnStyleName = columnStyleName;
  }

  @Override
  public void add(final IsWidget isWidget) {
    add(isWidget.asWidget());
  }

  @Override
  public boolean remove(final IsWidget isWidget) {
    return remove(isWidget.asWidget());
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public IsElement getIsElement() {
    return new GwtElement(getElement());
  }

  @Override
  public IsStyle getStyle() {
    return getIsElement().getStyle();
  }

  @Override
  public Iterator<IsWidget> iteratorIsWidgets() {
    return new IsWidgetIteratorAdaptor(iterator());
  }

  @Override
  public void insert(IsWidget widget, int beforeIndex) {
    insert(widget.asWidget(), beforeIndex);
  }

  @Override
  public IsWidget getIsWidget(int index) {
    return (IsWidget) getWidget(index);
  }

  @Override
  public int getWidgetIndex(IsWidget child) {
    return getWidgetIndex(child.asWidget());
  }

}
