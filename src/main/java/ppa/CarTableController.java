package ppa;

import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import javafx.beans.value.ObservableValueBase;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import ppa.component.CheckBoxEditableTableCell;
import ppa.component.CircleColorPickerTableCell;
import ppa.model.Car;

/**
 * Created by pwilkin on 21-Nov-19.
 */
public class CarTableController {

    @FXML
    protected TableView<Car> carTableView;
    protected XStream serializer;
    

    public void initialize() {
        TableColumn<Car, String> makeColumn = (TableColumn<Car, String>) carTableView.getColumns().get(0);
        makeColumn.setCellValueFactory(new PropertyValueFactory<>("make"));
        makeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        makeColumn.setOnEditCommit(edit -> {
            Car editedCar = carTableView.getEditingCell().getTableView().getItems().get(carTableView.getEditingCell().getRow());
            editedCar.setMake(edit.getNewValue());
        });

        TableColumn<Car, String> modelColumn = (TableColumn<Car, String>) carTableView.getColumns().get(1);
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        modelColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        modelColumn.setOnEditCommit(edit -> {
            Car editedCar = carTableView.getEditingCell().getTableView().getItems().get(carTableView.getEditingCell().getRow());
            editedCar.setModel(edit.getNewValue());
        });

        TableColumn<Car, Integer> yearColumn = (TableColumn<Car, Integer>) carTableView.getColumns().get(2);
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("yearOfProduction"));
        yearColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        yearColumn.setOnEditCommit(edit -> {
            Car editedCar = carTableView.getEditingCell().getTableView().getItems().get(carTableView.getEditingCell().getRow());
            editedCar.setYearOfProduction(edit.getNewValue());
        });

        TableColumn<Car, Boolean> dieselColumn = (TableColumn<Car, Boolean>) carTableView.getColumns().get(3);
        dieselColumn.setCellValueFactory(new PropertyValueFactory<>("diesel"));
        dieselColumn.setCellFactory(list -> new CheckBoxEditableTableCell<>());
        dieselColumn.setOnEditCommit(edit -> {
            Car editedCar = carTableView.getEditingCell().getTableView().getItems().get(carTableView.getEditingCell().getRow());
            editedCar.setDiesel(edit.getNewValue());
        });
        
        TableColumn<Car, Double> powerColumn = (TableColumn<Car, Double>) carTableView.getColumns().get(4);
        powerColumn.setCellValueFactory(new PropertyValueFactory<>("power"));
        powerColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        powerColumn.setOnEditCommit(edit -> {
            Car editedCar = carTableView.getEditingCell().getTableView().getItems().get(carTableView.getEditingCell().getRow());
            editedCar.setEnginePower(edit.getNewValue());
        });

        TableColumn<Car, Circle> colorColumn = (TableColumn<Car, Circle>) carTableView.getColumns().get(5);
        colorColumn.setCellValueFactory(carCircleCellDataFeatures -> {
            Car car = carCircleCellDataFeatures.getValue();
            String colorString = car.getColor();
            Circle colorCircle = new Circle(10.0);
            colorCircle.setFill(Color.web(colorString));
            return new ObservableValueBase<Circle>() {
                @Override
                public Circle getValue() {
                    return colorCircle;
                }
            };
        });

        colorColumn.setCellFactory(list -> new CircleColorPickerTableCell<>());
        colorColumn.setOnEditCommit(edit -> {
      
        	Circle circle = edit.getNewValue();
        	Car editedCar = carTableView.getEditingCell().getTableView().getItems().get(carTableView.getEditingCell().getRow());

        });
       
        /*dieselColumn.setCellFactory(CheckBoxTableCell.forTableColumn(dieselColumn));
        dieselColumn.setOnEditCommit(edit -> {
            Car editedCar = carTableView.getEditingCell().getTableView().getItems().get(carTableView.getEditingCell().getRow());
            editedCar.setDiesel(edit.getNewValue());
        });*/
        serializer = new XStream();
        try (FileInputStream fis = new FileInputStream("cars.xml")) {
            List<Car> deserializedCars = (List<Car>) serializer.fromXML(new InputStreamReader(fis));
            carTableView.getItems().addAll(deserializedCars);
        } catch (Exception e) {
            e.printStackTrace();
            Car car1 = Car.create("Ford", "Focus", 2006, true, 82.0, "#FFCC33");
            Car car2 = Car.create("Porsche", "Carrera", 1976, true, 233.0, "#000000");
            Car car3 = Car.create("Honda", "Civic", 2010, true, 112.0, "#D2A088");
            carTableView.getItems().addAll(car1, car2, car3);
        }
        carTableView.setEditable(true);
        carTableView.getSelectionModel().cellSelectionEnabledProperty().setValue(true);
        carTableView.setOnMouseClicked(click -> {
            if (click.getClickCount() > 1) {
                editFocusedCell();
            }

        }
       );
        // chcemy zapis przenieść do jakiegoś przycisku
        zapis();
    }

    private void zapis() {
        String xmlVersion = serializer.toXML(new ArrayList<>(carTableView.getItems()));
        try (FileOutputStream fos = new FileOutputStream("cars.xml")) { // try-with-resources
            try (PrintWriter pw = new PrintWriter(fos)) {
                pw.print(xmlVersion);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void editFocusedCell() {
            TablePosition<Car, ?> focusedCell = carTableView.focusModelProperty().get().focusedCellProperty().get();
            carTableView.edit(focusedCell.getRow(), focusedCell.getTableColumn());
    }



    public void save(javafx.event.ActionEvent actionEvent) {
        zapis();
    }
}
