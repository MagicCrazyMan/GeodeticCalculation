package club.magiccrazyman.geodetic.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * 软件用户界面主类
 * <br>
 * <strong>此类不提供Javadoc，且UI界面版本号不与core同步</strong>
 *
 * @author Magic Crazy Man
 * @version 0.1.0
 */
public class UserInterface extends Application {

    /**
     * @param args parameters from cli
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UserInterface.class.getResource(("UserInterface.fxml")));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setScene(scene);
        scene.getWindow().sizeToScene(); //不刷新的话，就会出现尺寸变化的问题
        primaryStage.setTitle("大地椭球信息转换");
        primaryStage.setResizable(false);
        primaryStage.show();

        UserInterfaceController controller = fxmlLoader.getController();
        controller.init(primaryStage);
    }
}
