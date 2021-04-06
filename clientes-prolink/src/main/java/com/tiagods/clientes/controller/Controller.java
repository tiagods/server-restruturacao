package com.tiagods.clientes.controller;

import com.tiagods.clientes.dao.ClientesDAO;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import com.tiagods.clientes.model.*;
import com.tiagods.clientes.*;

public class Controller implements Initializable {
    @FXML
    ComboBox cbComboBox1;
    @FXML
    ComboBox cbComboBox2;
    @FXML
    ComboBox cbComboBox3;
    @FXML
    TextField txText1;
    @FXML
    TextField txText2;
    @FXML
    TextField txText3;
    @FXML
    TableView tbTable;
    @FXML
    TableView<ControleStatus> tbStatus;

    ClientesDAO dao = new ClientesDAO();
    DataResult data;
    DataResult dataPesquisa;
    List<String> colunasVisiveis = new LinkedList<>();
    List<String> colunasOrdem = new LinkedList<>();
    List<ControleStatus> controleStatuses = new LinkedList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableStatus();
        //loadCache();
        Platform.runLater(()->{
            filtrar();
            table();
        });
    }

    List<String> getTbColumns(ObservableList<TableColumn> tbColumns) {
        return tbColumns.stream()
                .map(c-> c.getText())
                .collect(Collectors.toList());
    }

    void loadCache() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("cache.properties"));
            String stringColunas = properties.getProperty("colunas");
            String stringVisiveis = properties.getProperty("visiveis");

            colunasVisiveis.clear();
            colunasOrdem.clear();

            if (stringColunas != null && !stringColunas.equals("")) {
                if (stringVisiveis != null && !stringVisiveis.equals("")) {
                    colunasVisiveis = Arrays.asList(stringVisiveis.split(","));
                }
                colunasOrdem = Arrays.asList(stringColunas.split(","));
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void exportar(ActionEvent event) {
        if(!dataPesquisa.getData().isEmpty()){
            StringBuilder builder = new StringBuilder();

            String header = dataPesquisa.getColumns()
                    .stream()
                    .collect(Collectors.joining(";"));

            builder.append(header);
            builder.append(System.getProperty("line.separator"));
            for(List<Object> obj : dataPesquisa.getData()) {
                String line = obj.stream()
                        .map(m-> m==null? "" : m.toString().replace(";", ","))
                        .collect(Collectors.joining(";"));
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }

            try {
                String fileName = System.getProperty("java.io.tmpdir") + "/cli_export_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy-HHmmss")) + ".csv";
                FileWriter fileWriter = new FileWriter(fileName);
                fileWriter.write(builder.toString());
                fileWriter.flush();
                fileWriter.close();

                Desktop.getDesktop().open(new File(fileName));
            } catch (IOException exception) {
                exception.printStackTrace();

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Não foi possivel iniciar o programa");
                alert.setContentText("Falha ao abrir o aplicativo\n" + exception);
                alert.showAndWait();
            }
        }
    }

    @FXML
    void exportarConf(ActionEvent event) {
        colunasOrdem = getTbColumns(tbTable.getColumns());
        colunasVisiveis = getTbColumns(tbTable.getVisibleLeafColumns());

        Properties properties = new Properties();
        properties.setProperty("colunas", colunasOrdem.stream().collect(Collectors.joining(",")));
        properties.setProperty("visiveis", colunasVisiveis.stream().collect(Collectors.joining(",")));

        try {
            properties.store(new FileWriter("cache.properties"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        data = dao.listarClientes(colunasOrdem);
        filtrar();
    }

    void filtrar() {
        List<String> novasColunas = new LinkedList<>();
        if(colunasOrdem != null && !colunasOrdem.isEmpty()) {
            List<String> colunasExistentes = dao.obterColunasDoBanco();
            List<String> colunas = new LinkedList<>();
            for(String s : colunasOrdem){
                if(colunasExistentes.contains(s)){
                    colunas.add(s);
                }
            }
            for(String s : colunasExistentes) {
                if(!colunas.contains(s)){
                    novasColunas.add(s);
                    colunas.add(s);
                }
            }
            colunasOrdem = colunas;
        }
        if(colunasVisiveis != null && !colunasVisiveis.isEmpty()) {
            List<String> colunasExistentes = dao.obterColunasDoBanco();
            novasColunas.forEach(c->{
               if(!colunasVisiveis.contains(c)) {
                   colunasVisiveis.add(c);
               }
           });

            List<String> colunas = new LinkedList<>();
            for(String s : colunasVisiveis){
                if(colunasExistentes.contains(s)){
                    colunas.add(s);
                }
            }
            for(String s : colunasVisiveis) {
                if(!colunas.contains(s)){
                    colunas.add(s);
                }
            }
        }
        if(data == null || data.getData().isEmpty() || data.getColumns().isEmpty() || !novasColunas.isEmpty()) {
            data = dao.listarClientes(colunasOrdem);

            cbComboBox1.getItems().clear();
            cbComboBox2.getItems().clear();
            cbComboBox3.getItems().clear();
            int size = data.getColumns().size();

            if(size > 0) {
                cbComboBox1.getItems().addAll(data.getColumns());
                cbComboBox2.getItems().addAll(data.getColumns());
                cbComboBox3.getItems().addAll(data.getColumns());
                cbComboBox1.getSelectionModel().selectFirst();
                cbComboBox2.getSelectionModel().select(size > 1? 1 : 0);
                cbComboBox3.getSelectionModel().select(size > 2? 2 : 0);
            }
        }
        dataPesquisa = data;

        puxarFiltro(txText1, cbComboBox1);
        puxarFiltro(txText2, cbComboBox2);
        puxarFiltro(txText3, cbComboBox3);
        refreshTabelaStatus();

        tbTable.getItems().setAll(dataPesquisa.getData());
    }

    void refreshTabelaStatus() {
        String status = "status";
        int index = dataPesquisa.getColumnIndex(status);
        if(index!=-1) {

            Map<String, Long> stringMap = data.getData()
                    .stream()
                    .map(s -> s.get(index).toString())
                    .collect(Collectors.groupingBy(String::toString, Collectors.counting()));


            controleStatuses = stringMap.entrySet()
                    .stream()
                    .map(c-> {
                        ControleStatus controleStatus = new ControleStatus(c.getKey(), c.getValue());
                        controleStatus.setStyle(TableUtils.getColor(c.getKey()));

                        int indexStatus = dataPesquisa.getColumnIndex(status);
                        if(indexStatus!=-1) {
                            long totalFiltro = dataPesquisa.getData()
                                    .stream()
                                    .map(m-> m.get(indexStatus).toString())
                                    .filter(f-> f.equals(c.getKey()))
                                    .count();
                            controleStatus.setTotalFiltro(totalFiltro);
                        }
                        return controleStatus;
                    })
                    .collect(Collectors.toList());

            tbStatus.getItems().setAll(controleStatuses);
        } else {
            tbStatus.getItems().clear();
        }
    }

    void puxarFiltro(TextField textField, ComboBox comboBox){
        String tx = textField.getText();
        String valorCombo = comboBox.getSelectionModel().getSelectedItem().toString();
        List<List<Object>> linhasPesquisa = new LinkedList<>();

        if(tx != null && !tx.equals("") && valorCombo != null && !valorCombo.equals("")) {
            int index = dataPesquisa.getColumnIndex(valorCombo);

            if(index!=-1){
                linhasPesquisa = dataPesquisa.getData()
                        .stream()
                        .filter(obj-> {
                            String value = obj.get(index).toString();
                            return value.toLowerCase().contains(tx.toLowerCase());
                        })
                        .collect(Collectors.toList());
            } else {
                linhasPesquisa.addAll(dataPesquisa.getData());
            }
        } else {
            linhasPesquisa.addAll(dataPesquisa.getData());
        }

        dataPesquisa = new DataResult(dataPesquisa.getColumns(), linhasPesquisa);
    }

    @FXML
    void pesquisar(ActionEvent event) {
        System.out.println("Valor do texto1:"+ txText1.getText() + " - Valor do combo: "+ cbComboBox1.getSelectionModel().getSelectedItem());
        System.out.println("Valor do texto2:"+ txText2.getText() + " - Valor do combo: "+ cbComboBox2.getSelectionModel().getSelectedItem());
        System.out.println("Valor do texto3:"+ txText3.getText() + " - Valor do combo: "+ cbComboBox3.getSelectionModel().getSelectedItem());

        filtrar();
    }
    @FXML
    void limpar(ActionEvent event) {
        txText1.clear();
        txText2.clear();
        txText3.clear();
    }

    void tableStatus() {
        TableColumn<ControleStatus, String> colunaNome = new TableColumn<>("Nome");
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        TableColumn<ControleStatus, String> colunaTotal = new TableColumn<>("Filtro Autual");
        colunaTotal.setCellValueFactory(new PropertyValueFactory<>("totalFiltro"));
        TableColumn<ControleStatus, String> colunaFiltro = new TableColumn<>("Total");
        colunaFiltro.setCellValueFactory(new PropertyValueFactory<>("total"));

        tbStatus.setRowFactory(row -> new TableRow<ControleStatus>() {
            @Override
            public void updateItem(ControleStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setStyle("");
                } else {
                    setStyle(item.getStyle());
                }
            }
        });

        tbStatus.getColumns().addAll(colunaNome, colunaTotal, colunaFiltro);
    }

    void table() {
        int indexColor = -1;

        for (int i = 0 ; i < dataPesquisa.getNumColumns() ; i++) {
            String columnName = dataPesquisa.getColumnName(i);

            if(columnName.equalsIgnoreCase("status")) {
                indexColor = i;
            }

            TableColumn<List<Object>, Object> column = new TableColumn<>(columnName);
            int columnIndex = i ;
            column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get(columnIndex)));

            if(colunasVisiveis!=null && !colunasVisiveis.isEmpty() && !colunasVisiveis.contains(columnName)) {
                column.setVisible(false);
            }
            tbTable.getColumns().add(column);
        }

        int finalIndexColor = indexColor;
        tbTable.setRowFactory(row -> new TableRow<List<Object>>() {
            @Override
            public void updateItem(List<Object> item, boolean empty) {
                super.updateItem(item, empty);
                Tooltip tooltip = new Tooltip();
                tooltip.setText("Clique com o mouse para copiar a celula (ou Crtl + C)");
                setTooltip(tooltip);
                if (item == null || finalIndexColor==-1) {
                    setStyle("");
                } else if (item.get(finalIndexColor) != null) {
                    String value = item.get(finalIndexColor).toString();
                    setStyle(TableUtils.getColor(value));
                }
            }
        });

        System.out.println(String.format("Total de colunas da tabela %d", tbTable.getColumns().size()));
        tbTable.setTableMenuButtonVisible(true);
        System.out.println(String.format("Total de linhas da tabela %d", tbTable.getItems().size()));
        TableUtils.installCopyPasteHandler(tbTable);
    }
}
