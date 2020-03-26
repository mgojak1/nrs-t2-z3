package ba.unsa.etf.nrs;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class GlavnaController {

    public TableView<Grad> tableViewGradovi;
    public TableColumn<Grad, Integer> colGradId;
    public TableColumn<Grad, String> colGradNaziv;
    public TableColumn<Grad, Integer> colGradStanovnika;
    public TableColumn<Grad, String> colGradDrzava;
    private GeografijaDAO dao;
    private ObservableList<Grad> listGradovi;
    private ResourceBundle bundle;
    public GlavnaController() {
        dao = GeografijaDAO.getInstance();
        listGradovi = FXCollections.observableArrayList(dao.gradovi());
    }

    @FXML
    public void initialize() {
        bundle = ResourceBundle.getBundle("Translation");
        tableViewGradovi.setItems(listGradovi);
        colGradId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colGradNaziv.setCellValueFactory(new PropertyValueFactory<>("naziv"));
        colGradStanovnika.setCellValueFactory(new PropertyValueFactory<>("brojStanovnika"));
        colGradDrzava.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDrzava().getNaziv()));
    }

    public void actionDodajGrad(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader( getClass().getResource(
                    "/fxml/grad.fxml" ), bundle);
            GradController gradController = new GradController(null, dao.drzave());
            loader.setController(gradController);
            root = loader.load();
            stage.setTitle(bundle.getString("grad"));
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(true);
            stage.show();

            stage.setOnHiding(event -> {
                Grad grad = gradController.getGrad();
                if (grad != null) {
                    dao.dodajGrad(grad);
                    listGradovi.setAll(dao.gradovi());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void actionDodajDrzavu(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader( getClass().getResource(
                    "/fxml/drzava.fxml" ), bundle);
            DrzavaController drzavaController = new DrzavaController(null, dao.gradovi());
            loader.setController(drzavaController);
            root = loader.load();
            stage.setTitle(bundle.getString("drzavaT"));
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(true);
            stage.show();

            stage.setOnHiding(event -> {
                Drzava drzava = drzavaController.getDrzava();
                if (drzava != null) {
                    dao.dodajDrzavu(drzava);
                    listGradovi.setAll(dao.gradovi());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionIzmijeniGrad(ActionEvent actionEvent) {
        Grad grad = tableViewGradovi.getSelectionModel().getSelectedItem();
        if (grad == null) return;

        Stage stage = new Stage();
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader( getClass().getResource(
                    "/fxml/grad.fxml" ), bundle);
            GradController gradController = new GradController(grad, dao.drzave());
            loader.setController(gradController);
            root = loader.load();
            stage.setTitle(bundle.getString("grad"));
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(true);
            stage.show();

            stage.setOnHiding(event -> {
                Grad noviGrad = gradController.getGrad();
                if (noviGrad != null) {
                    dao.izmijeniGrad(noviGrad);
                    listGradovi.setAll(dao.gradovi());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionObrisiGrad(ActionEvent actionEvent) {
        Grad grad = tableViewGradovi.getSelectionModel().getSelectedItem();
        if (grad == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(bundle.getString("potvrda_brisanja"));
        alert.setHeaderText(bundle.getString("brisanje_grada") + grad.getNaziv());
        alert.setContentText(bundle.getString("da_li_ste_sigurni") + grad.getNaziv() + "?");
        alert.setResizable(true);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            dao.obrisiGrad(grad);
            listGradovi.setAll(dao.gradovi());
        }
    }

    // Metoda za potrebe testova, vraća bazu u polazno stanje
    public void resetujBazu() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();
        dao = GeografijaDAO.getInstance();
    }

    public void prikaziIzvjestaj(ActionEvent actionEvent) {
        try {
            new GradoviReport().showReport(dao.getConnection());
        } catch (JRException e1) {
            e1.printStackTrace();
        }
    }

    public void izaberiJezik(){
        String[] opcije = {bundle.getString("engleski"), bundle.getString("bosanski")};
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(opcije[1], opcije);
        choiceDialog.setTitle(bundle.getString( "izaberi_jezik"));
        Optional<String> result = choiceDialog.showAndWait();
        if(result.get().equals(bundle.getString("engleski"))) {
            Locale.setDefault(new Locale("en", "US"));
            bundle = ResourceBundle.getBundle("Translation");
        } else if(result.get().equals(bundle.getString("bosanski"))) {
            Locale.setDefault(new Locale("bs", "BA"));
            bundle = ResourceBundle.getBundle("Translation");
        }
        try {
            FXMLLoader loader = new FXMLLoader( getClass().getResource(
                    "/fxml/glavna.fxml" ), bundle);
            GlavnaController ctrl = new GlavnaController();
            loader.setController(ctrl);
            Parent root = loader.load();
            Stage primaryStage = new Stage();
            primaryStage.setTitle(bundle.getString("gradovi_svijeta"));
            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.show();
            Stage stage = (Stage) tableViewGradovi.getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
