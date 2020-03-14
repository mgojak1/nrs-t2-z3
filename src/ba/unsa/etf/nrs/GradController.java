package ba.unsa.etf.nrs;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class GradController {
    public TextField fieldNaziv;
    public TextField fieldBrojStanovnika;
    public TextField fieldPostanskiBroj;
    public ChoiceBox<Drzava> choiceDrzava;
    public ObservableList<Drzava> listDrzave;
    private Grad grad;
    private Thread thread;

    public GradController(Grad grad, ArrayList<Drzava> drzave) {
        this.grad = grad;
        listDrzave = FXCollections.observableArrayList(drzave);
    }

    @FXML
    public void initialize() {
        choiceDrzava.setItems(listDrzave);
        if (grad != null) {
            fieldNaziv.setText(grad.getNaziv());
            fieldBrojStanovnika.setText(Integer.toString(grad.getBrojStanovnika()));
            fieldPostanskiBroj.setText(Integer.toString(grad.getPostanskiBroj()));
            // choiceDrzava.getSelectionModel().select(grad.getDrzava());
            // ovo ne radi jer grad.getDrzava() nije identički jednak objekat kao član listDrzave
            for (Drzava drzava : listDrzave)
                if (drzava.getId() == grad.getDrzava().getId())
                    choiceDrzava.getSelectionModel().select(drzava);
        } else {
            choiceDrzava.getSelectionModel().selectFirst();
        }
    }

    public Grad getGrad() {
        return grad;
    }

    public void clickCancel(ActionEvent actionEvent) {
        grad = null;
        Stage stage = (Stage) fieldNaziv.getScene().getWindow();
        stage.close();
    }


    public void clickOk(ActionEvent actionEvent) {
        boolean sveOk = true;
        if (fieldNaziv.getText().trim().isEmpty()) {
            fieldNaziv.getStyleClass().removeAll("poljeIspravno");
            fieldNaziv.getStyleClass().add("poljeNijeIspravno");
            sveOk = false;
        } else {
            fieldNaziv.getStyleClass().removeAll("poljeNijeIspravno");
            fieldNaziv.getStyleClass().add("poljeIspravno");
        }
        int brojStanovnika = 0;
        try {
            brojStanovnika = Integer.parseInt(fieldBrojStanovnika.getText());
        } catch (NumberFormatException e) {
            // ...
        }
        if (brojStanovnika <= 0) {
            fieldBrojStanovnika.getStyleClass().removeAll("poljeIspravno");
            fieldBrojStanovnika.getStyleClass().add("poljeNijeIspravno");
            sveOk = false;
        } else {
            fieldBrojStanovnika.getStyleClass().removeAll("poljeNijeIspravno");
            fieldBrojStanovnika.getStyleClass().add("poljeIspravno");
        }
        if (!sveOk) return;
        thread = new Thread(() -> {
            try {
                int postanskiBroj = 0;
                try {
                    postanskiBroj = Integer.parseInt(fieldPostanskiBroj.getText());
                } catch (NumberFormatException e) {
                    // ...
                }
                URL test = new URL("http://c9.etf.unsa.ba/proba/postanskiBroj.php?postanskiBroj=" + postanskiBroj);
                InputStreamReader inputStreamReader = new InputStreamReader(test.openStream());
                Scanner scanner = new Scanner(inputStreamReader);
                String s = scanner.nextLine();
                if (!s.equals("OK")) {
                    fieldPostanskiBroj.getStyleClass().removeAll("poljeIspravno");
                    fieldPostanskiBroj.getStyleClass().add("poljeNijeIspravno");
                } else {
                    fieldPostanskiBroj.getStyleClass().removeAll("poljeNijeIspravno");
                    fieldPostanskiBroj.getStyleClass().add("poljeIspravno");
                    if (grad == null) grad = new Grad();
                    grad.setNaziv(fieldNaziv.getText());
                    grad.setBrojStanovnika(Integer.parseInt(fieldBrojStanovnika.getText()));
                    grad.setDrzava(choiceDrzava.getValue());
                    grad.setPostanskiBroj(postanskiBroj);
                    Platform.runLater(() -> {
                        Stage stage = (Stage) fieldNaziv.getScene().getWindow();
                        stage.close();
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}
