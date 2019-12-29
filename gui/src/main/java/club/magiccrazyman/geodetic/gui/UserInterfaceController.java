package club.magiccrazyman.geodetic.gui;

import club.magiccrazyman.geodetic.core.coordinatesystem.GeodeticCoordinateSystem;
import club.magiccrazyman.geodetic.core.coordinatesystem.projection.GaussKrugerProjectionCoordinateSystem;
import club.magiccrazyman.geodetic.core.tools.CalculationTools;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

/**
 * UserInterface.fxml 所使用的 Controller
 * <br>
 * <strong>此类不提供Javadoc，且UI界面版本号不与core同步</strong>
 *
 * @author Magic Crazy Man
 */
public class UserInterfaceController {


    public static final String geodeticCoordinateSystemIntroduction = "大地坐标系：%s" + System.lineSeparator() +
            "   椭圆长半轴：%.4f 米" + System.lineSeparator() +
            "   椭圆短半轴：%.4f 米" + System.lineSeparator() +
            "   椭圆扁率：1/%.8f" + System.lineSeparator() +
            "   椭圆第一偏心率：%.15f" + System.lineSeparator() +
            "   椭圆第二偏心率：%.15f" + System.lineSeparator() +
            "   椭圆极点处的子午线曲率半径：%.4f 米" + System.lineSeparator();

    public static final String toGeoInputParametersIntroduction = "输入参数" + System.lineSeparator() +
            "   X轴坐标值：%.10f 米" + System.lineSeparator() +
            "   Y轴坐标值：%.10f 米" + System.lineSeparator() +
            "   Z轴坐标值：%.10f 米" + System.lineSeparator() +
            "   迭代经度：%s 弧度" + System.lineSeparator();

    public static final String toSpaInputParametersIntroduction = "输入参数" + System.lineSeparator() +
            "   大地经度L：%.10f 弧度" + System.lineSeparator() +
            "   大地纬度B：%.10f 弧度" + System.lineSeparator() +
            "   大地高H：%.10f  米" + System.lineSeparator();

    public static final String outputResultIntroduction = "计算结果" + System.lineSeparator();

    public static final String geodeticLongitudeResult = "   大地经度L：%.10f 弧度" + System.lineSeparator();

    public static final String geodeticLatitudeResult = "   大地纬度B：%.10f 弧度" + System.lineSeparator() +
            "       总迭代次数 %d" + System.lineSeparator();

    public static final String geodeticHeightResult = "   大地高 H：%.10f 米" + System.lineSeparator();

    public static final String geodeticIterationStart = "   迭代法，从空间直角坐标系推算大地纬度B" + System.lineSeparator() +
            "       迭代初始值：%.15f 弧度" + System.lineSeparator();

    public static final String geodeticLatitudeIterationCount = "       迭代 %d 次 B 值：%.15f 弧度" + System.lineSeparator();

    public static final String spatialXResult = "  X轴坐标值：%.10f 米" + System.lineSeparator();

    public static final String spatialYResult = "  Y轴坐标值：%.10f 米" + System.lineSeparator();

    public static final String spatialZResult = "  Z轴坐标值：%.10f 米" + System.lineSeparator();

    public static final String gaussProjectionCoordinateSystemIntroduction = "高斯投影坐标系：%s" + System.lineSeparator() +
            "   分度带：%d °" + System.lineSeparator() +
            "   中央子午线：%.1f 米" + System.lineSeparator() +
            "   东伪偏移值：%.1f 米" + System.lineSeparator() +
            "   北伪偏移值：%.1f 米" + System.lineSeparator() +
            "   缩放因子：%.1f" + System.lineSeparator() +
            "   纬度原点：%.1f" + System.lineSeparator() +
            "   线性单位：%s" + System.lineSeparator();

    public static final String gaussProjectionForwardInputParameters = "输入参数" + System.lineSeparator() +
            "   大地经度L：%.10f 弧度" + System.lineSeparator() +
            "   大地纬度B：%.10f 弧度" + System.lineSeparator();

    public static final String gaussProjectionBackwardInputParameters = "输入参数" + System.lineSeparator() +
            "   X轴坐标值：%.10f 米" + System.lineSeparator() +
            "   Y轴坐标值：%.10f 米" + System.lineSeparator() +
            "   是否有偏移量：%s" + System.lineSeparator() +
            "   迭代精度：%s 弧度" + System.lineSeparator();

    public static final String gaussProjectionForwardResult = "   经差：%.4f 弧度" + System.lineSeparator() +
            "   中央子午线弧长：%.4f 米" + System.lineSeparator() +
            "   卯酉圈曲率半径：%.4f 弧度" + System.lineSeparator() +
            "   X轴真坐标值：%.4f 弧度" + System.lineSeparator() +
            "   Y轴真坐标值：%.4f 弧度" + System.lineSeparator() +
            "   X轴偏移坐标值：%.4f 弧度" + System.lineSeparator() +
            "   Y轴偏移坐标值：%.4f 弧度" + System.lineSeparator();

    public static final String gaussProjectionIterationStart = "   迭代法，子午线弧长计算大地纬度Bf" + System.lineSeparator() +
            "       迭代初始值：%.15f 弧度" + System.lineSeparator();

    public static final String gaussProjectionLatitudeIterationCount = "       迭代 %d 次 Bf 值：%.15f 弧度" + System.lineSeparator();

    public static final String gaussProjectionBackwardResult = "   子午圈曲率半径：%.4f 米" + System.lineSeparator() +
            "   卯酉圈曲率半径：%.4f 米" + System.lineSeparator() +
            "   大地纬度B：%.4f 弧度" + System.lineSeparator() +
            "       迭代次数：%d" + System.lineSeparator() +
            "   大地经度L：%.4f 弧度" + System.lineSeparator() +
            "       经差l：%.4f 弧度" + System.lineSeparator();

    public RadioButton showCalculation;
    public TextField precisionTextField1;
    public TextField longitudeTextField1;
    public TextField latitudeTextField1;
    public TextField longitudeDegreeTextField1;
    public TextField longitudeMinsTextField1;
    public TextField longitudeSecondsTextField1;
    public TextField latitudeDegreeTextField1;
    public TextField latitudeMinsTextField1;
    public TextField latitudeSecondsTextField1;
    public TextField heightTextField1;
    public TextField indexTextField;
    public TextField projectionAxisXTextField1;
    public TextField projectionAxisYTextField1;
    public TextField centralMeridianTextField;
    public TextField projectionAxisXTextField2;
    public TextField projectionAxisYTextField2;
    public Button proj2GeoButton;
    public Button geo2ProjButton;
    public Button clearButton1;
    public Label systemLabel;
    public TabPane selectionTabPane;
    public Tab coordinateTransformTab;
    public Tab projectionTransformTab;
    public Button trueOrFalseButton;
    public ChoiceBox NorS;
    public ChoiceBox EorW;
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
    public Button trueOrFalseButton1;
    public TextField indexTextField1;
    public TextField centralMeridianTextField1;
    public TextField projectionAxisXTextField11;
    public TextField projectionAxisYTextField11;
    public TextField projectionAxisXTextField21;
    public TextField projectionAxisYTextField21;
    public ComboBox systemComboBox1;
    public TextField indexTextField11;
    public TextField centralMeridianTextField11;
    public TextField projectionAxisXTextField111;
    public TextField projectionAxisYTextField111;
    public TextField projectionAxisXTextField211;
    public TextField projectionAxisYTextField211;
    public Button gaussProjectionTransformButton;
    public Label systemLabel1;
    public Button spatial2GeoButton;
    public TextField precisionTextField;
    public Button geo2SpatialButton;
    public TextField axisZTextField;
    public TextField axisYTextField;
    public TextField axisXTextField;
    public TextField longitudeMinsTextField;
    public TextField longitudeDegreeTextField;
    public TextField longitudeSecondsTextField;
    public TextField latitudeSecondsTextField;
    public TextField latitudeDegreeTextField;
    public TextField latitudeMinsTextField;
    public TextField heightTextField;
    public TextField latitudeTextField;
    public TextField longitudeTextField;
    public Button inputMethodButton;
    public ComboBox systemComboBox;
    public Button clearButton;
    public TextField precisionTextField11;
    public Button clearButton11;

    private Stage stage;
    private AnchorPane processPane;
    private TextArea processTextArea;

    private GeodeticCoordinateSystem geodeticSystem;
    private ChangeListener geodeticListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            geodeticSystem = (GeodeticCoordinateSystem) newValue;
        }
    };

    private GaussKrugerProjectionCoordinateSystem projectionSystem;
    private ChangeListener projectionListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            projectionSystem = (GaussKrugerProjectionCoordinateSystem) newValue;
            int index = selectionTabPane.getSelectionModel().getSelectedIndex();
            if (index == 2) {
                indexTextField.setText(String.valueOf(projectionSystem.getDegree()));
                centralMeridianTextField.setText(projectionSystem.getCentralMeridian() > 0 ? projectionSystem.getCentralMeridian() + " E" : projectionSystem.getCentralMeridian() + " W");
            } else if (index == 3) {
                indexTextField1.setText(String.valueOf(projectionSystem.getDegree()));
                centralMeridianTextField1.setText(projectionSystem.getCentralMeridian() > 0 ? projectionSystem.getCentralMeridian() + " E" : projectionSystem.getCentralMeridian() + " W");
            }
        }
    };

    private ChangeListener projectionListener1 = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            GaussKrugerProjectionCoordinateSystem system = (GaussKrugerProjectionCoordinateSystem) newValue;
            indexTextField11.setText(String.valueOf(system.getDegree()));
            centralMeridianTextField11.setText(system.getCentralMeridian() > 0 ? system.getCentralMeridian() + " E" : system.getCentralMeridian() + " W");
        }
    };

    private int geodeticInputMode = 0;

    private int geodeticInputMode1 = 0;
    private int trueOrFalse = 0;

    private int trueOrFalse1 = 0;

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
        selectionTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int index = selectionTabPane.getSelectionModel().getSelectedIndex();
            switch (index) {
                case 0:
                case 1:
                    initCoordinateTransformTab();
                    break;
                case 2:
                    initProjectionTransformTab();
                    break;
                case 3:
                    initProjectionTransformTab();
                    for (GaussKrugerProjectionCoordinateSystem.RecordedSystem system : GaussKrugerProjectionCoordinateSystem.RecordedSystem.values()) {
                        systemComboBox1.getItems().add(system.getSystem());

                    }
                    //设置字符串映射表
                    systemComboBox1.setConverter(new StringConverter() {
                        @Override
                        public String toString(Object system) {
                            return ((GaussKrugerProjectionCoordinateSystem) system).getName();
                        }

                        @Override
                        public Object fromString(String string) {
                            return null;
                        }
                    });
                    systemComboBox1.getSelectionModel().select(0);
                    systemComboBox1.getSelectionModel().selectedItemProperty().removeListener(projectionListener1);
                    systemComboBox1.getSelectionModel().selectedItemProperty().addListener(projectionListener1);
                    indexTextField11.setText(String.valueOf(projectionSystem.getDegree()));
                    centralMeridianTextField11.setText(projectionSystem.getCentralMeridian() > 0 ? projectionSystem.getCentralMeridian() + " E" : projectionSystem.getCentralMeridian() + " W");
                    break;
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
        //设置字符串映射表
        systemComboBox.setConverter(new StringConverter() {
            @Override
            public String toString(Object system) {
                //不知道哪里来的system会是空指针，JavaFx 1.8不存在这个问题；下同
                return (system == null ? "NULL" : ((GeodeticCoordinateSystem) system).getName());
            }

            @Override
            public Object fromString(String string) {
                return null;
            }
        });
        for (GeodeticCoordinateSystem.RecordedSystem recordedSystem : GeodeticCoordinateSystem.RecordedSystem.values()) {
            systemComboBox.getItems().add(recordedSystem.getSystem());
        }

        //默认选择第一项
        systemComboBox.getSelectionModel().select(0);
        geodeticSystem = (GeodeticCoordinateSystem) systemComboBox.getSelectionModel().getSelectedItem();

        //设置选择项修改Listener
        systemComboBox.getSelectionModel().selectedItemProperty().addListener(geodeticListener);
    }

    private void initProjectionTransformTab() {
        //删除原有Listener
        systemComboBox.getSelectionModel().selectedItemProperty().removeListener(projectionListener);
        systemComboBox.getSelectionModel().selectedItemProperty().removeListener(geodeticListener);

        //修改label
        systemLabel.setText("投影坐标系");
        //添加所有已知投影坐标系至comboBox
        systemComboBox.getItems().clear();
        //设置字符串映射表
        systemComboBox.setConverter(new StringConverter() {
            @Override
            public String toString(Object system) {
                return (system == null ? "NULL" : ((GaussKrugerProjectionCoordinateSystem) system).getName());
            }

            @Override
            public Object fromString(String string) {
                return null;
            }
        });
        for (GaussKrugerProjectionCoordinateSystem.RecordedSystem recordedSystem : GaussKrugerProjectionCoordinateSystem.RecordedSystem.values()) {
            systemComboBox.getItems().add(recordedSystem.getSystem());
        }

        //默认选择第一项
        systemComboBox.getSelectionModel().select(0);
        projectionSystem = (GaussKrugerProjectionCoordinateSystem) systemComboBox.getSelectionModel().getSelectedItem();
        int index = selectionTabPane.getSelectionModel().getSelectedIndex();
        if (index == 2) {
            indexTextField.setText(String.valueOf(projectionSystem.getDegree()));
            centralMeridianTextField.setText(projectionSystem.getCentralMeridian() > 0 ? projectionSystem.getCentralMeridian() + " E" : projectionSystem.getCentralMeridian() + " W");
        } else if (index == 3) {
            indexTextField1.setText(String.valueOf(projectionSystem.getDegree()));
            centralMeridianTextField1.setText(projectionSystem.getCentralMeridian() > 0 ? projectionSystem.getCentralMeridian() + " E" : projectionSystem.getCentralMeridian() + " W");
        }

        //设置选择项修改Listener
        systemComboBox.getSelectionModel().selectedItemProperty().addListener(projectionListener);
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
            angleMinsTextField2.clear();
            angleSecondsTextField2.clear();

            longitudeDegreeTextField3.clear();
            longitudeMinsTextField3.clear();
            longitudeSecondsTextField3.clear();
            latitudeDegreeTextField3.clear();
            latitudeMinsTextField3.clear();
            latitudeSecondsTextField3.clear();
            angleDegreeTextField3.clear();
            angleMinsTextField3.clear();
            angleSecondsTextField3.clear();

            geodeticLineTextField.clear();
        } else if (btn.getId().equals("clearButton11")) {
            projectionAxisXTextField11.clear();
            projectionAxisXTextField21.clear();
            projectionAxisYTextField11.clear();
            projectionAxisYTextField21.clear();

            projectionAxisXTextField111.clear();
            projectionAxisXTextField211.clear();
            projectionAxisYTextField111.clear();
            projectionAxisYTextField211.clear();
        }
        if (processPane != null) {
            processTextArea.clear();
        }
    }


    /**
     * "空间直角坐标系 -> 大地坐标系" 按钮
     *
     * @param actionEvent Action Event
     */
    public void spatial2GeoButtonActionPerformed(ActionEvent actionEvent) {
        try {
            double X, Y, Z, precision;
            X = Double.parseDouble(axisXTextField.getText());
            Y = Double.parseDouble(axisYTextField.getText());
            Z = Double.parseDouble(axisZTextField.getText());
            precision = Double.parseDouble(precisionTextField.getText());

            ArrayList<Double> LBHC = geodeticSystem.transformToGeodeticCoordinateSystem(X, Y, Z, precision);

            longitudeTextField.setText(String.valueOf(LBHC.get(0)));
            latitudeTextField.setText(String.valueOf(LBHC.get(1)));
            heightTextField.setText(String.format("%.4f", LBHC.get(2)));

            String longitude = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(LBHC.get(0)));
            String latitude = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(LBHC.get(1)));
            setDegreeTextField(longitude, latitude, 0);

            if (processPane != null) {
                processTextArea.clear();
                processTextArea.appendText(String.format(geodeticCoordinateSystemIntroduction, geodeticSystem.getName(), geodeticSystem.getA(), geodeticSystem.getB(), 1 / geodeticSystem.getAlpha(), geodeticSystem.getE2(), geodeticSystem.getDE2(), geodeticSystem.getC()));
                processTextArea.appendText(String.format(toGeoInputParametersIntroduction, X, Y, Z, precisionTextField.getText()));
                processTextArea.appendText(outputResultIntroduction);
                processTextArea.appendText(String.format(geodeticIterationStart, LBHC.get(4)));
                for (int i = 5, j = 1; i < LBHC.size(); i++, j++) {
                    processTextArea.appendText(String.format(geodeticLatitudeIterationCount, j, LBHC.get(i)));
                }
                processTextArea.appendText(String.format(geodeticLongitudeResult, LBHC.get(0)));
                processTextArea.appendText(String.format(geodeticLatitudeResult, LBHC.get(1), (int) (double) LBHC.get(3)));
                processTextArea.appendText(String.format(geodeticHeightResult, LBHC.get(2)));
            }
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
            ArrayList<Double> XYZ = geodeticSystem.transformToSpatialCoordinateSystem(L, B, H);

            axisXTextField.setText(String.format("%.8f", XYZ.get(0)));
            axisYTextField.setText(String.format("%.8f", XYZ.get(1)));
            axisZTextField.setText(String.format("%.8f", XYZ.get(2)));

            if (processPane != null) {
                processTextArea.clear();
                processTextArea.appendText(String.format(geodeticCoordinateSystemIntroduction, geodeticSystem.getName(), geodeticSystem.getA(), geodeticSystem.getB(), 1 / geodeticSystem.getAlpha(), geodeticSystem.getE2(), geodeticSystem.getDE2(), geodeticSystem.getC()));
                processTextArea.appendText(String.format(toSpaInputParametersIntroduction, L, B, H));
                processTextArea.appendText(outputResultIntroduction);
                processTextArea.appendText(String.format(spatialXResult, XYZ.get(0)));
                processTextArea.appendText(String.format(spatialYResult, XYZ.get(1)));
                processTextArea.appendText(String.format(spatialZResult, XYZ.get(2)));
            }

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
            processTextArea = new TextArea();
            processTextArea.setPrefHeight(height);
            processTextArea.setPrefWidth(width);
            processTextArea.setWrapText(true); //自动换行
            processPane = new AnchorPane();
            processPane.setPrefHeight(height);
            processPane.setPrefWidth(width);
            processPane.getChildren().add(processTextArea);

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
            double X, Y, precision;
            boolean hasFalse;
            ArrayList<Double> LBlffC;

            precision = Double.parseDouble(precisionTextField1.getText());

            if (trueOrFalse == 0) {
                X = Double.parseDouble(projectionAxisXTextField1.getText());
                Y = Double.parseDouble(projectionAxisYTextField1.getText());
                hasFalse = true;
                LBlffC = projectionSystem.backwardCalculation(X, Y, precision, hasFalse);
            } else {
                X = Double.parseDouble(projectionAxisXTextField2.getText());
                Y = Double.parseDouble(projectionAxisYTextField2.getText());
                hasFalse = false;
                LBlffC = projectionSystem.backwardCalculation(X, Y, precision, hasFalse);
            }

            longitudeTextField1.setText(String.valueOf(LBlffC.get(0)));
            latitudeTextField1.setText(String.valueOf(LBlffC.get(1)));

            String longitude = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(LBlffC.get(0)));
            String latitude = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(LBlffC.get(1)));
            setDegreeTextField(longitude, latitude, 1);

            if (LBlffC.get(0) > 0) {
                EorW.getSelectionModel().select(0);
            } else {
                EorW.getSelectionModel().select(1);
            }

            if (LBlffC.get(1) > 0) {
                NorS.getSelectionModel().select(0);
            } else {
                NorS.getSelectionModel().select(1);
            }

            if (processPane != null) {
                processTextArea.clear();
                processTextArea.appendText(String.format(gaussProjectionCoordinateSystemIntroduction, projectionSystem.getName(), projectionSystem.getDegree(), projectionSystem.getCentralMeridian(), projectionSystem.getFalseEasting(), projectionSystem.getFalseNorthing(), projectionSystem.getScaleFactor(), projectionSystem.getLatitudeOfOrigin(), projectionSystem.getLinerUnit()));
                processTextArea.appendText(String.format(gaussProjectionBackwardInputParameters, X, Y, hasFalse ? "有" : "无", precision));
                processTextArea.appendText(outputResultIntroduction);
                processTextArea.appendText(String.format(gaussProjectionIterationStart, LBlffC.get(6)));
                for (int i = 7, j = 1; i < LBlffC.size(); i++, j++) {
                    processTextArea.appendText(String.format(gaussProjectionLatitudeIterationCount, j, LBlffC.get(i)));
                }
                processTextArea.appendText(String.format(gaussProjectionBackwardResult, LBlffC.get(3), LBlffC.get(4), LBlffC.get(1), (int) (double) LBlffC.get(5), LBlffC.get(0), LBlffC.get(2)));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void geo2ProjButtonActionPerformed(ActionEvent actionEvent) {
        try {
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

            ArrayList<Double> values = projectionSystem.forwardCalculation(L, B);
            projectionAxisXTextField1.setText(String.format("%.4f", values.get(0)));
            projectionAxisYTextField1.setText(String.format("%.4f", values.get(1)));
            projectionAxisXTextField2.setText(String.format("%.4f", values.get(2)));
            projectionAxisYTextField2.setText(String.format("%.4f", values.get(3)));

            if (processPane != null) {
                processTextArea.clear();
                processTextArea.appendText(String.format(gaussProjectionCoordinateSystemIntroduction, projectionSystem.getName(), projectionSystem.getDegree(), projectionSystem.getCentralMeridian(), projectionSystem.getFalseEasting(), projectionSystem.getFalseNorthing(), projectionSystem.getScaleFactor(), projectionSystem.getLatitudeOfOrigin(), projectionSystem.getLinerUnit()));
                processTextArea.appendText(String.format(gaussProjectionForwardInputParameters, L, B));
                processTextArea.appendText(outputResultIntroduction);
                processTextArea.appendText(String.format(gaussProjectionForwardResult, values.get(4), values.get(5), values.get(6), values.get(2), values.get(3), values.get(0), values.get(1)));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void trueOrFalseButtonClick(ActionEvent actionEvent) {
        String id = ((Button) actionEvent.getSource()).getId();
        if (id.equals("trueOrFalseButton")) {
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
        } else if (id.equals("trueOrFalseButton1")) {
            if (trueOrFalse1 == 0) {
                trueOrFalse1 = 1;

                projectionAxisXTextField11.setDisable(true);
                projectionAxisYTextField11.setDisable(true);

                projectionAxisXTextField21.setDisable(false);
                projectionAxisYTextField21.setDisable(false);
            } else {
                trueOrFalse1 = 0;

                projectionAxisXTextField11.setDisable(false);
                projectionAxisYTextField11.setDisable(false);

                projectionAxisXTextField21.setDisable(true);
                projectionAxisYTextField21.setDisable(true);
            }
        }
    }

    public void directSolutionButtonClick(ActionEvent actionEvent) {
        try {
            if (processPane != null) {
                processTextArea.clear();
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

            ArrayList<Double> values = geodeticSystem.directSolutionOfGeodeticProblem(L, B, A, S);

            longitudeTextField3.setText(String.valueOf(values.get(0)));
            latitudeTextField3.setText(String.valueOf(values.get(1)));
            angleTextField3.setText(String.valueOf(values.get(2)));

            String[] str = getSplitDegree(CalculationTools.degreeFormatter(CalculationTools.rad2Degree(values.get(0))));
            longitudeDegreeTextField3.setText(str[0]);
            longitudeMinsTextField3.setText(str[1]);
            longitudeSecondsTextField3.setText(str[2]);

            str = getSplitDegree(CalculationTools.degreeFormatter(CalculationTools.rad2Degree(values.get(1))));
            latitudeDegreeTextField3.setText(str[0]);
            latitudeMinsTextField3.setText(str[1]);
            latitudeSecondsTextField3.setText(str[2]);

            str = getSplitDegree(CalculationTools.degreeFormatter(CalculationTools.rad2Degree(values.get(2))));
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
                processTextArea.clear();
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

            ArrayList<Double> values = geodeticSystem.inverseSolutionOfGeodeticProblem(L1, B1, L2, B2, precision);

            angleTextField2.setText(String.format("%.16f", values.get(0)));
            angleTextField3.setText(String.format("%.16f", values.get(1)));
            geodeticLineTextField.setText(String.format("%.4f", values.get(2)));

            String[] str = getSplitDegree(CalculationTools.degreeFormatter(CalculationTools.rad2Degree(values.get(0))));
            angleDegreeTextField2.setText(str[0]);
            angleMinsTextField2.setText(str[1]);
            angleSecondsTextField2.setText(str[2]);

            str = getSplitDegree(CalculationTools.degreeFormatter(CalculationTools.rad2Degree(values.get(1))));
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

    public void gaussProjectionTransformButtonClick(ActionEvent actionEvent) {
        double X, Y, precision;
        boolean hasFalse;
        ArrayList<Double> XY;

        precision = Double.parseDouble(precisionTextField11.getText());
        GaussKrugerProjectionCoordinateSystem output = (GaussKrugerProjectionCoordinateSystem) systemComboBox1.getSelectionModel().getSelectedItem();

        if (trueOrFalse1 == 0) {
            X = Double.parseDouble(projectionAxisXTextField11.getText());
            Y = Double.parseDouble(projectionAxisYTextField11.getText());
            hasFalse = true;
            XY = projectionSystem.projectionTransform(output, X, Y, precision, hasFalse);
        } else {
            X = Double.parseDouble(projectionAxisXTextField21.getText());
            Y = Double.parseDouble(projectionAxisYTextField21.getText());
            hasFalse = false;
            XY = projectionSystem.projectionTransform(output, X, Y, precision, hasFalse);
        }

        projectionAxisXTextField111.setText(String.format("%.4f", XY.get(0)));
        projectionAxisYTextField111.setText(String.format("%.4f", XY.get(1)));
        projectionAxisXTextField211.setText(String.format("%.4f", XY.get(2)));
        projectionAxisYTextField211.setText(String.format("%.4f", XY.get(3)));
    }
}
