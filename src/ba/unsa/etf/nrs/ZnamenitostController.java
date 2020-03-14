package ba.unsa.etf.nrs;

import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ZnamenitostController {
    public TextField fieldNazivZnamenitost;
    public ImageView imageViewZnamenitost;
    private Znamenitost znanemitost;
    private Grad grad;
    private String slika;

    public ZnamenitostController(Grad grad) {
        this.grad = grad;
        this.slika = "";
    }

    public Znamenitost getZnanemitost() {
        return znanemitost;
    }

    public void dodajZnamenitost() {
        if(znanemitost == null)
            znanemitost = new Znamenitost();
        znanemitost.setGrad(grad);
        znanemitost.setSlika(slika);
        znanemitost.setNaziv(fieldNazivZnamenitost.getText());
        Stage stage = (Stage) fieldNazivZnamenitost.getScene().getWindow();
        stage.close();
    }

    public void dodajSliku() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Slika");
        dialog.setHeaderText("Izaberite sliku");
        dialog.setResizable(true);
        slika = dialog.showAndWait().get();
        try {
            imageViewZnamenitost.setImage(new Image(new FileInputStream(slika)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
