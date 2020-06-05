package views;

import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.Priority;
import javafx.geometry.HPos;
import javafx.geometry.VPos;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.NamedArg;

/**
 * This class subclasses the GridPane layout class.
 * It manages its child nodes by arranging them in rows of equal number of tiles.
 * Their order in the grid corresponds to their indexes in the list of children
 * in the following fashion (similarly to how FlowPane works):
 * <p>
 * +---+---+---+---+
 * | 0 | 1 | 2 | 3 |
 * +---+---+---+---+
 * | 4 | 5 | 6 | 7 |
 * +---+---+---+---+
 * | 8 | 9 | … |   |
 * +---+---+---+---+
 * <p>
 * It observes its internal list of children and it automatically reflows them
 * if the number of columns changes or if you add/remove some children from the list.
 * All the tiles of the grid are of the same size and stretch accordingly with the control.
 */
public class FlowGridPane extends GridPane {
    // Properties for managing the number of rows & columns.
    private IntegerProperty rowsCount;
    private IntegerProperty colsCount;

    public final IntegerProperty colsCountProperty() {
        return colsCount;
    }

    public final Integer getColsCount() {
        return colsCountProperty().get();
    }

    public final void setColsCount(final Integer cols) {
        // Recreate column constraints so that they will resize properly.
        ObservableList<ColumnConstraints> constraints = getColumnConstraints();
        constraints.clear();
        for (int i = 0; i < cols; ++i) {
            ColumnConstraints c = new ColumnConstraints();
            c.setHalignment(HPos.CENTER);
            c.setHgrow(Priority.ALWAYS);
            c.setMinWidth(60);
            constraints.add(c);
        }
        colsCountProperty().set(cols);
        reflowAll();
    }

    public final IntegerProperty rowsCountProperty() {
        return rowsCount;
    }

    public final Integer getRowsCount() {
        return rowsCountProperty().get();
    }

    public final void setRowsCount(final Integer rows) {
        // Recreate column constraints so that they will resize properly.
        ObservableList<RowConstraints> constraints = getRowConstraints();
        constraints.clear();
        for (int i = 0; i < rows; ++i) {
            RowConstraints r = new RowConstraints();
            r.setValignment(VPos.CENTER);
            r.setVgrow(Priority.ALWAYS);
            r.setMinHeight(20);
            constraints.add(r);
        }
        rowsCountProperty().set(rows);
        reflowAll();
    }

    /// Constructor. Takes the number of columns and rows of the grid (can be changed later).
    public FlowGridPane(@NamedArg("cols") int cols, @NamedArg("rows") int rows) {
        super();
        colsCount = new SimpleIntegerProperty();
        setColsCount(cols);
        rowsCount = new SimpleIntegerProperty();
        setRowsCount(rows);
        getChildren().addListener(new ListChangeListener<Node>() {
            public void onChanged(ListChangeListener.Change<? extends Node> change) {
                reflowAll();
            }
        });
    }

    // Helper functions for coordinate conversions.
    private int coordsToOffset(int col, int row) {
        return row * colsCount.get() + col;
    }

    private int offsetToCol(int offset) {
        return offset % colsCount.get();
    }

    private int offsetToRow(int offset) {
        return offset / colsCount.get();
    }

    private void reflowAll() {
        ObservableList<Node> children = getChildren();
        for (Node child : children) {
            int offs = children.indexOf(child);
            GridPane.setConstraints(child, offsetToCol(offs), offsetToRow(offs));
        }
    }
}
