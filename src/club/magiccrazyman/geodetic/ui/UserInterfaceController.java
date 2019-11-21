package club.magiccrazyman.geodetic.ui;

import club.magiccrazyman.geodetic.core.coordinatesystem.GeodeticCoordinateSystem;
import club.magiccrazyman.geodetic.core.coordinatesystem.projection.GaussKrugerProjectionCoordinateSystem;
import club.magiccrazyman.geodetic.core.logger.SimpleLogger;
import club.magiccrazyman.geodetic.core.tools.CalculationTools;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.logging.*;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

/**
 * UserInterface.fxml 所使用的 Controller
 * <br>
 * <strong>此类不提供Javadoc，且UI界面版本号不与core同步</strong>
 *
 * @author Magic Crazy Man
 * @version 0.1.0
 */
public class UserInterfaceController {

    @FXML
    public RadioButton showCalculation;
    @FXML
    public TextField precisionTextField1;
    @FXML
    public TextField longitudeTextField1;
    @FXML
    public TextField latitudeTextField1;
    @FXML
    public TextField longitudeDegreeTextField1;
    @FXML
    public TextField longitudeMinsTextField1;
    @FXML
    public TextField longitudeSecondsTextField1;
    @FXML
    public TextField latitudeDegreeTextField1;
    @FXML
    public TextField latitudeMinsTextField1;
    @FXML
    public TextField latitudeSecondsTextField1;
    @FXML
    public TextField heightTextField1;
    @FXML
    public TextField indexTextField;
    @FXML
    public TextField projectionAxisXTextField1;
    @FXML
    public TextField projectionAxisYTextField1;
    @FXML
    public TextField centralMeridianTextField;
    @FXML
    public TextField projectionAxisXTextField2;
    @FXML
    public TextField projectionAxisYTextField2;
    @FXML
    public Button proj2GeoButton;
    @FXML
    public Button geo2ProjButton;
    @FXML
    public Button clearButton1;
    @FXML
    public Label systemLabel;
    @FXML
    public TabPane selectionTabPane;
    @FXML
    public Tab coordinateTransformTab;
    @FXML
    public Tab projectionTransformTab;
    @FXML
    public Button trueOrFalseButton;
    @FXML
    public ChoiceBox NorS;
    @FXML
    public ChoiceBox EorW;
    @FXML
    public Button inputMethodButton1;
    public TextField precisionTextField2;
    public Button inputMethodButton2;
    public TextField longitudeTextField2;
    public TextField longitudeDegreeTextField2;
    public TextField longitudeMinsTextField2;
    public TextField longitudeSecondsTextField2;
    public TextField latitudeTextField2;
    public TextField latitudeDegreeTextField2;
    public TextField latitudeMinsTextField2;
    public TextField latitudeSecondsTextField2;
    public TextField angleTextField2;
    public TextField angleSecondsTextField2;
    public TextField angleMinsTextField2;
    public TextField angleDegreeTextField2;
    public TextField geodeticLineTextField;
    public Button directSolutionButton;
    public Button inverseSolutionButton;
    public TextField longitudeTextField3;
    public TextField longitudeDegreeTextField3;
    public TextField longitudeMinsTextField3;
    public TextField longitudeSecondsTextField3;
    public TextField latitudeTextField3;
    public TextField latitudeDegreeTextField3;
    public TextField latitudeMinsTextField3;
    public TextField latitudeSecondsTextField3;
    public TextField angleTextField3;
    public TextField angleSecondsTextField3;
    public TextField angleMinsTextField3;
    public TextField angleDegreeTextField3;
    public Button clearButton2;
    @FXML
    private Button spatial2GeoButton;
    @FXML
    private TextField precisionTextField;
    @FXML
    private Button geo2SpatialButton;
    @FXML
    private TextField axisZTextField;
    @FXML
    private TextField axisYTextField;
    @FXML
    private TextField axisXTextField;
    @FXML
    private TextField longitudeMinsTextField;
    @FXML
    private TextField longitudeDegreeTextField;
    @FXML
    private TextField longitudeSecondsTextField;
    @FXML
    private TextField latitudeSecondsTextField;
    @FXML
    private TextField latitudeDegreeTextField;
    @FXML
    private TextField latitudeMinsTextField;
    @FXML
    private TextField heightTextField;
    @FXML
    private TextField latitudeTextField;
    @FXML
    private TextField longitudeTextField;
    @FXML
    private Button inputMethodButton;
    @FXML
    private ComboBox systemComboBox;
    @FXML
    private Button clearButton;

    private Stage stage;
    private AnchorPane processPane;

    private Handler handler = new Handler() {
        @Override
        public void publish(LogRecord record) {
            if (processPane != null) {
                TextArea area = (TextArea) processPane.getChildren().get(0);
                area.appendText(record.getMessage());
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {

        }
    };

    private GeodeticCoordinateSystem geodeticSystem;
    private ChangeListener geodeticListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            geodeticSystem = (GeodeticCoordinateSystem) newValue;
            setHandlerForSimpleLogger(geodeticSystem.getLogger());
        }
    };

    private GaussKrugerProjectionCoordinateSystem projectionSystem;
    private ChangeListener projectionListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            projectionSystem = (GaussKrugerProjectionCoordinateSystem) newValue;
            setHandlerForSimpleLogger(projectionSystem.getLogger());
            setHandlerForSimpleLogger(projectionSystem.getGeodeticCoordinateSystem().getLogger());
            indexTextField.setText(String.valueOf(projectionSystem.getDegree()));
            centralMeridianTextField.setText(projectionSystem.getCentralMeridian() > 0 ? projectionSystem.getCentralMeridian() + " E" : projectionSystem.getCentralMeridian() + " W");
        }
    };

    private int geodeticInputMode = 0;

    private int geodeticInputMode1 = 0;
    private int trueOrFalse = 0;

    private int geodeticInputMode2 = 0;

    /**
     * 启动时必须执行一次初始化
     */
    protected void init(Stage stage) {
        this.stage = stage;
        this.initSystemComboBox();

        //初始化半球信息
        EorW.getItems().addAll("东半球", "西半球");
        EorW.getSelectionModel().select(0);
        NorS.getItems().addAll("北半球", "南半球");
        NorS.getSelectionModel().select(0);
    }

    private void initSystemComboBox() {
        //开始时默认框为坐标转换
        initCoordinateTransformTab();

        //初始化TabPane切换Tab行为
        selectionTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                int index = selectionTabPane.getSelectionModel().getSelectedIndex();
                if (index == 0) {
                    initCoordinateTransformTab();
                } else if (index == 1) {
                    initCoordinateTransformTab();
                } else if (index == 2) {
                    initProjectionTransformTab();
                }
            }
        });
    }

    private void initCoordinateTransformTab() {
        //删除原有Listener
        systemComboBox.getSelectionModel().selectedItemProperty().removeListener(projectionListener);
        systemComboBox.getSelectionModel().selectedItemProperty().removeListener(geodeticListener);

        //修改label
        systemLabel.setText("大地坐标系");
        //添加所有已知大地坐标系至ComboBox
        systemComboBox.getItems().clear();
        systemComboBox.setConverter(null);
        for (GeodeticCoordinateSystem.RecordedSystem recordedSystem : GeodeticCoordinateSystem.RecordedSystem.values()) {
            systemComboBox.getItems().add(recordedSystem.getSystem());
            //设置字符串映射表
            systemComboBox.setConverter(new StringConverter() {
                @Override
                public String toString(Object system) {
                    return ((GeodeticCoordinateSystem) system).getName();
                }

                @Override
                public Object fromString(String string) {
                    return null;
                }
            });
        }
        //默认选择第一项
        systemComboBox.getSelectionModel().select(0);
        geodeticSystem = (GeodeticCoordinateSystem) systemComboBox.getSelectionModel().getSelectedItem();
        setHandlerForSimpleLogger(geodeticSystem.getLogger());

        //设置选择项修改Listener
        systemComboBox.getSelectionModel().selectedItemProperty().addListener(geodeticListener);
    }

    private void initProjectionTransformTab() {
        //删除原有Listener
        systemComboBox.getSelectionModel().selectedItemProperty().removeListener(geodeticListener);
        systemComboBox.getSelectionModel().selectedItemProperty().removeListener(geodeticListener);

        //修改label
        systemLabel.setText("投影坐标系");
        //添加所有已知投影坐标系至comboBox
        systemComboBox.getItems().clear();
        systemComboBox.setConverter(null);
        for (GaussKrugerProjectionCoordinateSystem.RecordedSystem recordedSystem : GaussKrugerProjectionCoordinateSystem.RecordedSystem.values()) {
            systemComboBox.getItems().add(recordedSystem.getSystem());
            //设置字符串映射表
            systemComboBox.setConverter(new StringConverter() {
                @Override
                public String toString(Object system) {
                    return ((GaussKrugerProjectionCoordinateSystem) system).getName();
                }

                @Override
                public Object fromString(String string) {
                    return null;
                }
            });
        }
        //默认选择第一项
        systemComboBox.getSelectionModel().select(0);
        projectionSystem = (GaussKrugerProjectionCoordinateSystem) systemComboBox.getSelectionModel().getSelectedItem();
        setHandlerForSimpleLogger(projectionSystem.getLogger());
        setHandlerForSimpleLogger(projectionSystem.getGeodeticCoordinateSystem().getLogger());
        indexTextField.setText(String.valueOf(projectionSystem.getDegree()));
        centralMeridianTextField.setText(projectionSystem.getCentralMeridian() > 0 ? projectionSystem.getCentralMeridian() + " E" : projectionSystem.getCentralMeridian() + " W");

        //设置选择项修改Listener
        systemComboBox.getSelectionModel().selectedItemProperty().addListener(projectionListener);
    }

    private void setHandlerForSimpleLogger(SimpleLogger logger) {
        for (Handler handler : logger.getHandlers()) {
            if (handler.equals(handler)) {
                return;
            }
        }
        handler.setLevel(Level.INFO);
        for (Handler h : logger.getHandlers()) {
            if (h.equals(handler)) {
                return;
            }
        }
        logger.addHandler(handler);
    }

    private void setDegreeTextField(String longitude, String latitude, int tabNum) {
        if (tabNum == 0) {
            longitudeDegreeTextField.setText(longitude.split("°")[0]);
            longitudeMinsTextField.setText(longitude.split("°")[1].split("\'")[0]);
            longitudeSecondsTextField.setText(longitude.split("°")[1].split("\'")[1].split("\"")[0]);

            latitudeDegreeTextField.setText(latitude.split("°")[0]);
            latitudeMinsTextField.setText(latitude.split("°")[1].split("\'")[0]);
            latitudeSecondsTextField.setText(latitude.split("°")[1].split("\'")[1].split("\"")[0]);
        } else if (tabNum == 1) {
            longitudeDegreeTextField1.setText(longitude.split("°")[0]);
            longitudeMinsTextField1.setText(longitude.split("°")[1].split("\'")[0]);
            longitudeSecondsTextField1.setText(longitude.split("°")[1].split("\'")[1].split("\"")[0]);

            latitudeDegreeTextField1.setText(latitude.split("°")[0]);
            latitudeMinsTextField1.setText(latitude.split("°")[1].split("\'")[0]);
            latitudeSecondsTextField1.setText(latitude.split("°")[1].split("\'")[1].split("\"")[0]);
        }
    }


    /**
     * "清空" 按钮
     *
     * @param actionEvent Action Event
     */
    public void clearButtonActionPerformed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();
        if (btn.getId().equals("clearButton")) {
            longitudeDegreeTextField.clear();
            longitudeMinsTextField.clear();
            longitudeSecondsTextField.clear();
            latitudeDegreeTextField.clear();
            latitudeMinsTextField.clear();
            latitudeSecondsTextField.clear();

            longitudeTextField.clear();
            latitudeTextField.clear();

            heightTextField.clear();

            axisXTextField.clear();
            axisYTextField.clear();
            axisZTextField.clear();
        } else if (btn.getId().equals("clearButton1")) {
            longitudeDegreeTextField1.clear();
            longitudeMinsTextField1.clear();
            longitudeSecondsTextField1.clear();
            latitudeDegreeTextField1.clear();
            latitudeMinsTextField1.clear();
            latitudeSecondsTextField1.clear();

            longitudeTextField1.clear();
            latitudeTextField1.clear();

            projectionAxisXTextField1.clear();
            projectionAxisXTextField2.clear();
            projectionAxisYTextField1.clear();
            projectionAxisYTextField2.clear();
        } else if (btn.getId().equals("clearButton2")) {
            longitudeTextField2.clear();
            latitudeTextField2.clear();
            angleTextField2.clear();

            longitudeTextField3.clear();
            latitudeTextField3.clear();
            angleTextField3.clear();

            longitudeDegreeTextField2.clear();
            longitudeMinsTextField2.clear();
            longitudeSecondsTextField2.clear();
            latitudeDegreeTextField2.clear();
            latitudeMinsTextField2.clear();
            latitudeSecondsTextField2.clear();
            angleDegreeTextField2.clear();

            longitudeDegreeTextField3.clear();
            longitudeMinsTextField3.clear();
            longitudeSecondsTextField3.clear();
            latitudeDegreeTextField3.clear();
            latitudeMinsTextField3.clear();
            latitudeSecondsTextField3.clear();
            angleDegreeTextField3.clear();

            geodeticLineTextField.clear();
        }
        if (processPane != null) {
            ((TextArea) processPane.getChildren().get(0)).clear();
        }
    }


    /**
     * "空间直角坐标系 -> 大地坐标系" 按钮
     *
     * @param actionEvent Action Event
     */
    public void spatial2GeoButtonActionPerformed(ActionEvent actionEvent) {
        try {
            if (processPane != null) {
                TextArea area = (TextArea) processPane.getChildren().get(0);
                area.clear();
            }

            double X, Y, Z, precision;
            X = Double.parseDouble(axisXTextField.getText());
            Y = Double.parseDouble(axisYTextField.getText());
            Z = Double.parseDouble(axisZTextField.getText());
            precision = Double.parseDouble(precisionTextField.getText());

            double[] LBHC = geodeticSystem.transformToGeodeticCoordinateSystem(X, Y, Z, precision);

            longitudeTextField.setText(String.valueOf(LBHC[0]));
            latitudeTextField.setText(String.valueOf(LBHC[1]));
            heightTextField.setText(String.format("%.4f", LBHC[2]));

            String longitude = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(LBHC[0]));
            String latitude = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(LBHC[1]));
            setDegreeTextField(longitude, latitude, 0);

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * "大地坐标系 -> 空间直角坐标系" 按钮
     *
     * @param actionEvent Action Event
     */
    public void geo2SpatialButtonActionPerformed(ActionEvent actionEvent) {
        try {
            if (processPane != null) {
                TextArea area = (TextArea) processPane.getChildren().get(0);
                area.clear();
            }

            double L, B, H;
            if (geodeticInputMode == 0) {
                L = Double.parseDouble(longitudeTextField.getText());
                B = Double.parseDouble(latitudeTextField.getText());
                H = Double.parseDouble(heightTextField.getText());

                String longitude = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(L));
                String latitude = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(B));
                setDegreeTextField(longitude, latitude, 0);
            } else {
                //角度转换为弧度
                double degree1 = longitudeDegreeTextField.getText().isEmpty() ? 0 : Double.parseDouble(longitudeDegreeTextField.getText());
                double mins1 = longitudeMinsTextField.getText().isEmpty() ? 0 : Double.parseDouble(longitudeMinsTextField.getText()) / 60.0;
                double second1 = longitudeSecondsTextField.getText().isEmpty() ? 0 : Double.parseDouble(longitudeSecondsTextField.getText()) / 3600.0;
                if (degree1 < 0) {
                    System.out.println(-(Math.abs(degree1) + mins1 + second1));
                    L = CalculationTools.degree2Rad(-(Math.abs(degree1) + mins1 + second1));
                } else {
                    L = CalculationTools.degree2Rad(degree1 + mins1 + second1);
                }

                double degree2 = latitudeDegreeTextField.getText().isEmpty() ? 0 : Double.parseDouble(latitudeDegreeTextField.getText());
                double mins2 = latitudeMinsTextField.getText().isEmpty() ? 0 : Double.parseDouble(latitudeMinsTextField.getText()) / 60.0;
                double second2 = latitudeSecondsTextField.getText().isEmpty() ? 0 : Double.parseDouble(latitudeSecondsTextField.getText()) / 3600.0;
                if (degree2 < 0) {
                    B = CalculationTools.degree2Rad(-(Math.abs(degree2) + mins2 + second2));
                } else {
                    B = CalculationTools.degree2Rad(degree2 + mins2 + second2);
                }

                H = Double.parseDouble(heightTextField.getText());

                longitudeTextField.setText(String.valueOf(L));
                latitudeTextField.setText(String.valueOf(B));
            }
            double[] XYZ = geodeticSystem.transformToSpatialCoordinateSystem(L, B, H);

            axisXTextField.setText(String.format("%.8f", XYZ[0]));
            axisYTextField.setText(String.format("%.8f", XYZ[1]));
            axisZTextField.setText(String.format("%.8f", XYZ[2]));

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * "弧度 / 角度" 按钮
     *
     * @param actionEvent Action Event
     */
    public void inputMethodButtonActionPerformed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();
        if (btn.getId().equals("inputMethodButton")) {
            if (geodeticInputMode == 1) {
                geodeticInputMode = 0;

                longitudeTextField.setDisable(false);
                latitudeTextField.setDisable(false);

                longitudeDegreeTextField.setDisable(true);
                longitudeMinsTextField.setDisable(true);
                longitudeSecondsTextField.setDisable(true);
                latitudeDegreeTextField.setDisable(true);
                latitudeMinsTextField.setDisable(true);
                latitudeSecondsTextField.setDisable(true);
            } else if (geodeticInputMode == 0) {
                geodeticInputMode = 1;

                longitudeTextField.setDisable(true);
                latitudeTextField.setDisable(true);

                longitudeDegreeTextField.setDisable(false);
                longitudeMinsTextField.setDisable(false);
                longitudeSecondsTextField.setDisable(false);
                latitudeDegreeTextField.setDisable(false);
                latitudeMinsTextField.setDisable(false);
                latitudeSecondsTextField.setDisable(false);
            }
        } else if (btn.getId().equals("inputMethodButton1")) {
            if (geodeticInputMode1 == 1) {
                geodeticInputMode1 = 0;

                longitudeTextField1.setDisable(false);
                latitudeTextField1.setDisable(false);

                longitudeDegreeTextField1.setDisable(true);
                longitudeMinsTextField1.setDisable(true);
                longitudeSecondsTextField1.setDisable(true);
                latitudeDegreeTextField1.setDisable(true);
                latitudeMinsTextField1.setDisable(true);
                latitudeSecondsTextField1.setDisable(true);
            } else if (geodeticInputMode1 == 0) {
                geodeticInputMode1 = 1;

                longitudeTextField1.setDisable(true);
                latitudeTextField1.setDisable(true);

                longitudeDegreeTextField1.setDisable(false);
                longitudeMinsTextField1.setDisable(false);
                longitudeSecondsTextField1.setDisable(false);
                latitudeDegreeTextField1.setDisable(false);
                latitudeMinsTextField1.setDisable(false);
                latitudeSecondsTextField1.setDisable(false);
            }
        } else if (btn.getId().equals("inputMethodButton2")) {
            if (geodeticInputMode2 == 1) {
                geodeticInputMode2 = 0;

                longitudeTextField2.setDisable(false);
                latitudeTextField2.setDisable(false);
                angleTextField2.setDisable(false);

                longitudeTextField3.setDisable(false);
                latitudeTextField3.setDisable(false);
                angleTextField3.setDisable(false);

                longitudeDegreeTextField2.setDisable(true);
                longitudeMinsTextField2.setDisable(true);
                longitudeSecondsTextField2.setDisable(true);
                latitudeDegreeTextField2.setDisable(true);
                latitudeMinsTextField2.setDisable(true);
                latitudeSecondsTextField2.setDisable(true);
                angleDegreeTextField2.setDisable(true);
                angleMinsTextField2.setDisable(true);
                angleSecondsTextField2.setDisable(true);

                longitudeDegreeTextField3.setDisable(true);
                longitudeMinsTextField3.setDisable(true);
                longitudeSecondsTextField3.setDisable(true);
                latitudeDegreeTextField3.setDisable(true);
                latitudeMinsTextField3.setDisable(true);
                latitudeSecondsTextField3.setDisable(true);
                angleDegreeTextField3.setDisable(true);
                angleMinsTextField3.setDisable(true);
                angleSecondsTextField3.setDisable(true);
            } else if (geodeticInputMode2 == 0) {
                geodeticInputMode2 = 1;

                longitudeTextField2.setDisable(true);
                latitudeTextField2.setDisable(true);
                angleTextField2.setDisable(true);

                longitudeTextField3.setDisable(true);
                latitudeTextField3.setDisable(true);
                angleTextField3.setDisable(true);

                longitudeDegreeTextField2.setDisable(false);
                longitudeMinsTextField2.setDisable(false);
                longitudeSecondsTextField2.setDisable(false);
                latitudeDegreeTextField2.setDisable(false);
                latitudeMinsTextField2.setDisable(false);
                latitudeSecondsTextField2.setDisable(false);
                angleDegreeTextField2.setDisable(false);
                angleMinsTextField2.setDisable(false);
                angleSecondsTextField2.setDisable(false);

                longitudeDegreeTextField3.setDisable(false);
                longitudeMinsTextField3.setDisable(false);
                longitudeSecondsTextField3.setDisable(false);
                latitudeDegreeTextField3.setDisable(false);
                latitudeMinsTextField3.setDisable(false);
                latitudeSecondsTextField3.setDisable(false);
                angleDegreeTextField3.setDisable(false);
                angleMinsTextField3.setDisable(false);
                angleSecondsTextField3.setDisable(false);
            }
        }
    }

    public void showCalculationActionPerformance(ActionEvent actionEvent) {
        boolean isSelected = ((RadioButton) actionEvent.getSource()).isSelected();
        if (isSelected) {
            GridPane rootPane = (GridPane) stage.getScene().getRoot();
            //根据窗口尺寸创建一个AnchorPane并放置一个TextArea，每次都创建一个新的
            double width = 370;
            double height = stage.getScene().getHeight() - 20; //剪掉留白
            TextArea processText = new TextArea();
            processText.setPrefHeight(height);
            processText.setPrefWidth(width);
            processText.setWrapText(true); //自动换行
            processPane = new AnchorPane();
            processPane.setPrefHeight(height);
            processPane.setPrefWidth(width);
            processPane.getChildren().add(processText);

            //添加两个Column装processPane和10px的WhiteSpace
            ObservableList<ColumnConstraints> columnConstraints = rootPane.getColumnConstraints();

            //processPane
            ColumnConstraints columnProcess = new ColumnConstraints(10, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
            columnProcess.setPercentWidth(-1);
            columnProcess.setHgrow(Priority.SOMETIMES);

            //whiteSpace
            ColumnConstraints columnSpace = new ColumnConstraints(10, 10, 10);
            columnSpace.setPercentWidth(-1);
            columnSpace.setHgrow(Priority.SOMETIMES);

            //添加格子
            columnConstraints.add(columnConstraints.size(), columnProcess);
            columnConstraints.add(columnConstraints.size(), columnSpace);

            rootPane.add(processPane, columnConstraints.size() - 2, 1);

            //刷新窗口
            stage.getScene().getWindow().sizeToScene();
        } else {
            GridPane rootPane = (GridPane) stage.getScene().getRoot();
            rootPane.getChildren().remove(processPane);
            ObservableList<ColumnConstraints> columnConstraints = rootPane.getColumnConstraints();
            if (columnConstraints.size() > 3) {
                columnConstraints.remove(2, 4);
                stage.getScene().getWindow().sizeToScene();
            }
            processPane = null;
        }
    }

    public void proj2GeoButtonActionPerformed(ActionEvent actionEvent) {
        try {
            if (processPane != null) {
                TextArea area = (TextArea) processPane.getChildren().get(0);
                area.clear();
            }

            double X, Y, precision;
            double[] LB;

            precision = Double.parseDouble(precisionTextField1.getText());

            if (trueOrFalse == 0) {
                X = Double.parseDouble(projectionAxisXTextField1.getText());
                Y = Double.parseDouble(projectionAxisYTextField1.getText());
                LB = projectionSystem.backwardCalculation(X, Y, precision, true);
            } else {
                X = Double.parseDouble(projectionAxisXTextField2.getText());
                Y = Double.parseDouble(projectionAxisYTextField2.getText());
                LB = projectionSystem.backwardCalculation(X, Y, precision, false);
            }

            longitudeTextField1.setText(String.valueOf(LB[0]));
            latitudeTextField1.setText(String.valueOf(LB[1]));

            String longitude = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(LB[0]));
            String latitude = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(LB[1]));
            setDegreeTextField(longitude, latitude, 1);

            if (LB[0] > 0) {
                EorW.getSelectionModel().select(0);
            } else {
                EorW.getSelectionModel().select(1);
            }

            if (LB[1] > 0) {
                NorS.getSelectionModel().select(0);
            } else {
                NorS.getSelectionModel().select(1);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void geo2ProjButtonActionPerformed(ActionEvent actionEvent) {
        try {
            if (processPane != null) {
                TextArea area = (TextArea) processPane.getChildren().get(0);
                area.clear();
            }

            double L, B;
            if (geodeticInputMode1 == 0) {
                L = Double.parseDouble(longitudeTextField1.getText());
                B = Double.parseDouble(latitudeTextField1.getText());

                String longitude = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(L));
                String latitude = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(B));
                setDegreeTextField(longitude, latitude, 1);
            } else {
                //角度转换为弧度
                double degree1 = longitudeDegreeTextField1.getText().isEmpty() ? 0 : Double.parseDouble(longitudeDegreeTextField1.getText());
                double mins1 = longitudeMinsTextField1.getText().isEmpty() ? 0 : Double.parseDouble(longitudeMinsTextField1.getText()) / 60.0;
                double second1 = longitudeSecondsTextField1.getText().isEmpty() ? 0 : Double.parseDouble(longitudeSecondsTextField1.getText()) / 3600.0;
                if (degree1 < 0) {
                    L = CalculationTools.degree2Rad(-(Math.abs(degree1) + mins1 + second1));
                } else {
                    L = CalculationTools.degree2Rad(degree1 + mins1 + second1);
                }

                double degree2 = latitudeDegreeTextField1.getText().isEmpty() ? 0 : Double.parseDouble(latitudeDegreeTextField1.getText());
                double mins2 = latitudeMinsTextField1.getText().isEmpty() ? 0 : Double.parseDouble(latitudeMinsTextField1.getText()) / 60.0;
                double second2 = latitudeSecondsTextField1.getText().isEmpty() ? 0 : Double.parseDouble(latitudeSecondsTextField1.getText()) / 3600.0;
                if (degree2 < 0) {
                    B = CalculationTools.degree2Rad(-(Math.abs(degree2) + mins2 + second2));
                } else {
                    B = CalculationTools.degree2Rad(degree2 + mins2 + second2);
                }

                longitudeTextField1.setText(String.valueOf(L));
                latitudeTextField1.setText(String.valueOf(B));
            }

            if (L > 0) {
                EorW.getSelectionModel().select(0);
            } else {
                EorW.getSelectionModel().select(1);
            }

            if (B > 0) {
                NorS.getSelectionModel().select(0);
            } else {
                NorS.getSelectionModel().select(1);
            }

            double[] values = projectionSystem.forwardCalculation(L, B);
            projectionAxisXTextField1.setText(String.format("%.4f", values[0]));
            projectionAxisYTextField1.setText(String.format("%.4f", values[1]));
            projectionAxisXTextField2.setText(String.format("%.4f", values[2]));
            projectionAxisYTextField2.setText(String.format("%.4f", values[3]));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void trueOrFalseButtonClick(ActionEvent actionEvent) {
        if (trueOrFalse == 0) {
            trueOrFalse = 1;

            projectionAxisXTextField1.setDisable(true);
            projectionAxisYTextField1.setDisable(true);

            projectionAxisXTextField2.setDisable(false);
            projectionAxisYTextField2.setDisable(false);
        } else {
            trueOrFalse = 0;

            projectionAxisXTextField1.setDisable(false);
            projectionAxisYTextField1.setDisable(false);

            projectionAxisXTextField2.setDisable(true);
            projectionAxisYTextField2.setDisable(true);
        }
    }

    public void directSolutionButtonClick(ActionEvent actionEvent) {
        try {
            if (processPane != null) {
                TextArea area = (TextArea) processPane.getChildren().get(0);
                area.clear();
            }

            double L, B, A, S;
            if (geodeticInputMode2 == 0) {
                L = Double.parseDouble(longitudeTextField2.getText());
                B = Double.parseDouble(latitudeTextField2.getText());
                A = Double.parseDouble(angleTextField2.getText());

                String longitude = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(L));
                String latitude = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(B));
                String angle = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(A));

                String[] str = getSplitDegree(longitude);
                longitudeDegreeTextField2.setText(str[0]);
                longitudeMinsTextField2.setText(str[1]);
                longitudeSecondsTextField2.setText(str[2]);

                str = getSplitDegree(latitude);
                latitudeDegreeTextField2.setText(str[0]);
                latitudeMinsTextField2.setText(str[1]);
                latitudeSecondsTextField2.setText(str[2]);

                str = getSplitDegree(angle);
                angleDegreeTextField2.setText(str[0]);
                angleMinsTextField2.setText(str[1]);
                angleSecondsTextField2.setText(str[2]);
            } else {
                //角度转换为弧度
                double degree1 = longitudeDegreeTextField2.getText().isEmpty() ? 0 : Double.parseDouble(longitudeDegreeTextField2.getText());
                double mins1 = longitudeMinsTextField2.getText().isEmpty() ? 0 : Double.parseDouble(longitudeMinsTextField2.getText()) / 60.0;
                double second1 = longitudeSecondsTextField2.getText().isEmpty() ? 0 : Double.parseDouble(longitudeSecondsTextField2.getText()) / 3600.0;
                if (degree1 < 0) {
                    L = CalculationTools.degree2Rad(-(Math.abs(degree1) + mins1 + second1));
                } else {
                    L = CalculationTools.degree2Rad(degree1 + mins1 + second1);
                }

                degree1 = latitudeDegreeTextField2.getText().isEmpty() ? 0 : Double.parseDouble(latitudeDegreeTextField2.getText());
                mins1 = latitudeMinsTextField2.getText().isEmpty() ? 0 : Double.parseDouble(latitudeMinsTextField2.getText()) / 60.0;
                second1 = latitudeSecondsTextField2.getText().isEmpty() ? 0 : Double.parseDouble(latitudeSecondsTextField2.getText()) / 3600.0;
                if (degree1 < 0) {
                    B = CalculationTools.degree2Rad(-(Math.abs(degree1) + mins1 + second1));
                } else {
                    B = CalculationTools.degree2Rad(degree1 + mins1 + second1);
                }

                degree1 = angleDegreeTextField2.getText().isEmpty() ? 0 : Double.parseDouble(angleDegreeTextField2.getText());
                mins1 = angleMinsTextField2.getText().isEmpty() ? 0 : Double.parseDouble(angleMinsTextField2.getText()) / 60.0;
                second1 = angleSecondsTextField2.getText().isEmpty() ? 0 : Double.parseDouble(angleSecondsTextField2.getText()) / 3600.0;
                if (degree1 < 0) {
                    A = CalculationTools.degree2Rad(-(Math.abs(degree1) + mins1 + second1));
                } else {
                    A = CalculationTools.degree2Rad(degree1 + mins1 + second1);
                }

                longitudeTextField2.setText(String.valueOf(L));
                latitudeTextField2.setText(String.valueOf(B));
                angleTextField2.setText(String.valueOf(A));
            }

            S = Double.parseDouble(geodeticLineTextField.getText());

            double[] values = geodeticSystem.directSolutionOfGeodeticProblem(L, B, A, S);

            longitudeTextField3.setText(String.valueOf(values[0]));
            latitudeTextField3.setText(String.valueOf(values[1]));
            angleTextField3.setText(String.valueOf(values[2]));

            String[] str = getSplitDegree(CalculationTools.degreeFormatter(CalculationTools.rad2Degree(values[0])));
            longitudeDegreeTextField3.setText(str[0]);
            longitudeMinsTextField3.setText(str[1]);
            longitudeSecondsTextField3.setText(str[2]);

            str = getSplitDegree(CalculationTools.degreeFormatter(CalculationTools.rad2Degree(values[1])));
            latitudeDegreeTextField3.setText(str[0]);
            latitudeMinsTextField3.setText(str[1]);
            latitudeSecondsTextField3.setText(str[2]);

            str = getSplitDegree(CalculationTools.degreeFormatter(CalculationTools.rad2Degree(values[2])));
            angleDegreeTextField3.setText(str[0]);
            angleMinsTextField3.setText(str[1]);
            angleSecondsTextField3.setText(str[2]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void inverseSolutionButtonClick(ActionEvent actionEvent) {
        try {
            if (processPane != null) {
                TextArea area = (TextArea) processPane.getChildren().get(0);
                area.clear();
            }

            double L1, B1, L2, B2;
            if (geodeticInputMode2 == 0) {
                L1 = Double.parseDouble(longitudeTextField2.getText());
                B1 = Double.parseDouble(latitudeTextField2.getText());
                L2 = Double.parseDouble(longitudeTextField3.getText());
                B2 = Double.parseDouble(latitudeTextField3.getText());

                String longitude = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(L1));
                String latitude = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(B1));

                String[] str = getSplitDegree(longitude);
                longitudeDegreeTextField2.setText(str[0]);
                longitudeMinsTextField2.setText(str[1]);
                longitudeSecondsTextField2.setText(str[2]);

                str = getSplitDegree(latitude);
                latitudeDegreeTextField2.setText(str[0]);
                latitudeMinsTextField2.setText(str[1]);
                latitudeSecondsTextField2.setText(str[2]);

                longitude = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(L2));
                latitude = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(B2));

               str = getSplitDegree(longitude);
                longitudeDegreeTextField3.setText(str[0]);
                longitudeMinsTextField3.setText(str[1]);
                longitudeSecondsTextField3.setText(str[2]);

                str = getSplitDegree(latitude);
                latitudeDegreeTextField3.setText(str[0]);
                latitudeMinsTextField3.setText(str[1]);
                latitudeSecondsTextField3.setText(str[2]);
            } else {
                //角度转换为弧度
                double degree1 = longitudeDegreeTextField2.getText().isEmpty() ? 0 : Double.parseDouble(longitudeDegreeTextField2.getText());
                double mins1 = longitudeMinsTextField2.getText().isEmpty() ? 0 : Double.parseDouble(longitudeMinsTextField2.getText()) / 60.0;
                double second1 = longitudeSecondsTextField2.getText().isEmpty() ? 0 : Double.parseDouble(longitudeSecondsTextField2.getText()) / 3600.0;
                if (degree1 < 0) {
                    L1 = CalculationTools.degree2Rad(-(Math.abs(degree1) + mins1 + second1));
                } else {
                    L1 = CalculationTools.degree2Rad(degree1 + mins1 + second1);
                }

                degree1 = latitudeDegreeTextField2.getText().isEmpty() ? 0 : Double.parseDouble(latitudeDegreeTextField2.getText());
                mins1 = latitudeMinsTextField2.getText().isEmpty() ? 0 : Double.parseDouble(latitudeMinsTextField2.getText()) / 60.0;
                second1 = latitudeSecondsTextField2.getText().isEmpty() ? 0 : Double.parseDouble(latitudeSecondsTextField2.getText()) / 3600.0;
                if (degree1 < 0) {
                    B1 = CalculationTools.degree2Rad(-(Math.abs(degree1) + mins1 + second1));
                } else {
                    B1 = CalculationTools.degree2Rad(degree1 + mins1 + second1);
                }

                degree1 = longitudeDegreeTextField3.getText().isEmpty() ? 0 : Double.parseDouble(longitudeDegreeTextField3.getText());
                mins1 = longitudeMinsTextField3.getText().isEmpty() ? 0 : Double.parseDouble(longitudeMinsTextField3.getText()) / 60.0;
                second1 = longitudeSecondsTextField3.getText().isEmpty() ? 0 : Double.parseDouble(longitudeSecondsTextField3.getText()) / 3600.0;
                if (degree1 < 0) {
                    L2 = CalculationTools.degree2Rad(-(Math.abs(degree1) + mins1 + second1));
                } else {
                    L2 = CalculationTools.degree2Rad(degree1 + mins1 + second1);
                }

                degree1 = latitudeDegreeTextField3.getText().isEmpty() ? 0 : Double.parseDouble(latitudeDegreeTextField3.getText());
                mins1 = latitudeMinsTextField3.getText().isEmpty() ? 0 : Double.parseDouble(latitudeMinsTextField3.getText()) / 60.0;
                second1 = latitudeSecondsTextField3.getText().isEmpty() ? 0 : Double.parseDouble(latitudeSecondsTextField3.getText()) / 3600.0;
                if (degree1 < 0) {
                    B2 = CalculationTools.degree2Rad(-(Math.abs(degree1) + mins1 + second1));
                } else {
                    B2 = CalculationTools.degree2Rad(degree1 + mins1 + second1);
                }

                longitudeTextField2.setText(String.valueOf(L1));
                latitudeTextField2.setText(String.valueOf(B1));
                longitudeTextField3.setText(String.valueOf(L2));
                latitudeTextField3.setText(String.valueOf(B2));
            }

            double precision = Double.parseDouble(precisionTextField2.getText());

            double[] values = geodeticSystem.inverseSolutionOfGeodeticProblem(L1, B1, L2, B2, precision);

            angleTextField2.setText(String.format("%.16f", values[0]));
            angleTextField3.setText(String.format("%.16f", values[1]));
            geodeticLineTextField.setText(String.format("%.4f", values[2]));

            String[] str = getSplitDegree(CalculationTools.degreeFormatter(CalculationTools.rad2Degree(values[0])));
            angleDegreeTextField2.setText(str[0]);
            angleMinsTextField2.setText(str[1]);
            angleSecondsTextField2.setText(str[2]);

            str = getSplitDegree(CalculationTools.degreeFormatter(CalculationTools.rad2Degree(values[1])));
            angleDegreeTextField3.setText(str[0]);
            angleMinsTextField3.setText(str[1]);
            angleSecondsTextField3.setText(str[2]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private String[] getSplitDegree(String degree) {
        String[] values = new String[3];
        values[0] = degree.split("°")[0];
        values[1] = degree.split("°")[1].split("\'")[0];
        values[2] = degree.split("°")[1].split("\'")[1].split("\"")[0];
        return values;
    }
}
