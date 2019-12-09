package sample;

import com.jfoenix.controls.*;
import com.prolink.config.ClienteData;
import com.prolink.model.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Controller extends Operacao implements Initializable{
    @FXML
    private JFXTextField txLocalizacao;

    @FXML
    private JFXButton btDefinir;

    @FXML
    private JFXComboBox<Raiz> cbRaiz;

    @FXML
    private JFXComboBox<Obrigacao> cbDocumento;

    @FXML
    private JFXComboBox<String> cbAno;

    @FXML
    private JFXComboBox<String> cbMes;

    @FXML
    private JFXRadioButton rbCNPJNome;

    @FXML
    private JFXRadioButton rbIdNome;

    @FXML
    private VBox pnIDNome;

    @FXML
    private JFXTextField txDelimitadorNome;

    @FXML
    private JFXComboBox<Integer> cbIndiceNome;

    @FXML
    private JFXButton btMover;

    @FXML
    private JFXRadioButton rbSubstituirCaracteres;

    @FXML
    private JFXRadioButton rbInserir0;

    @FXML
    private VBox pnSubstituirCaracteres;

    @FXML
    private JFXTextField txSVOld;

    @FXML
    private JFXTextField txSPNew;

    @FXML
    private JFXButton btRenomear;

    @FXML
    private JFXButton btVazias;

    @FXML
    private JFXCheckBox ckDelimitador;

    @FXML
    private JFXTextField txPesquisaRapida;

    @FXML
    private TableView<Cliente> tbClientes;

    ClienteData data = ClienteData.getInstance();
    Set<Cliente> clientesData;
    //Set<String> meses = new HashSet<>();

    boolean travar = false;

    private void combos(){
        txLocalizacao.setPromptText("Arraste uma pasta ou arquivo ate aqui para definir o local");
        txLocalizacao.setOnDragExited(this::dragExit);
        txLocalizacao.setOnDragOver(this::dragOver);

        clientesData = data.getClientes();
        txPesquisaRapida.textProperty().addListener((observable, oldValue, newValue) -> {
            tbClientes.getItems().clear();

            if(newValue.trim().length()>0){
                int id = 0;
                try{
                    id = Integer.parseInt(newValue);
                }catch (Exception e){}
                int finalId = id;
                List<Cliente> list = clientesData
                        .stream()
                        .filter(c->
                                c.getId()==finalId || c.getIdFormatado().startsWith(newValue)
                                        || c.getStatus().toUpperCase().startsWith(newValue.toUpperCase())
                                        || c.getNome().toUpperCase().startsWith(newValue.toUpperCase())
                                        || c.getCnpjFormatado().toUpperCase().startsWith(newValue.toUpperCase())
                                        || c.getCnpj().toUpperCase().startsWith(newValue.toUpperCase())
                        )
                        .collect(Collectors.toList());
                Collections.sort(list, Comparator.comparingInt(Cliente::getId));
                tbClientes.getItems().addAll(list);
            }
            else{
                tbClientes.getItems().addAll(clientesData);
            }
        });
        ToggleGroup groupMover = new ToggleGroup();
        groupMover.getToggles().addAll(rbCNPJNome,rbIdNome);
        rbIdNome.setSelected(true);
        ckDelimitador.setSelected(true);
        txDelimitadorNome.setDisable(true);
        cbIndiceNome.setDisable(true);

        ToggleGroup groupRename = new ToggleGroup();
        groupRename.getToggles().addAll(rbSubstituirCaracteres,rbInserir0);
        rbSubstituirCaracteres.setSelected(true);

        ChangeListener change1 = ((observable, oldValue, newValue) -> {
            String valor = "O nome do arquivo começa pelo ID no padrao 4 digitos (0000)";
            if(rbCNPJNome.isSelected()) valor = "O nome do arquivo começa pelo CNPJ no padrao 14 digitos (00000000000000)";
            ckDelimitador.setText(valor);
        }
        );
        ChangeListener change2 = (observable, oldValue, newValue) ->
                pnSubstituirCaracteres.setVisible(rbSubstituirCaracteres.isSelected());

        ChangeListener change3 = ((observable, oldValue, newValue) -> {
            txDelimitadorNome.setDisable(ckDelimitador.isSelected());
            cbIndiceNome.setDisable(ckDelimitador.isSelected());
        });

        rbIdNome.selectedProperty().addListener(change1);
        rbCNPJNome.selectedProperty().addListener(change1);
        rbSubstituirCaracteres.selectedProperty().addListener(change2);
        rbInserir0.selectedProperty().addListener(change2);
        ckDelimitador.selectedProperty().addListener(change3);

        btMover.setOnAction(this::mover);
        btRenomear.setOnAction(this::renomear);
        btVazias.setOnAction(this::limparVazios);
        String raiz = "Raiz";
        Raiz r = new Raiz(raiz);
        cbRaiz.getItems().add(r);

        Set<Raiz> raizSet = new HashSet<>();
        raizSet.add(new Raiz("Contabil"));
        raizSet.add(new Raiz("Contratos"));
        raizSet.add(new Raiz("Financeiro"));
        raizSet.add(new Raiz("Folha"));
        raizSet.add(new Raiz("Obrigacoes"));
        raizSet.add(new Raiz("Geral"));
        cbRaiz.getItems().addAll(raizSet);

        cbRaiz.setValue(r);

        Obrigacao o = new Obrigacao("Obrigacao/Documento");
        List<Obrigacao> obrigacaoSet = new ArrayList<>();
        obrigacaoSet.add(new Obrigacao("DIRF"));
        obrigacaoSet.add(new Obrigacao("DAS"));
        obrigacaoSet.add(new Obrigacao("GPS"));
        obrigacaoSet.add(new Obrigacao("DIPJ"));
        obrigacaoSet.add(new Obrigacao("DIMOB"));
        obrigacaoSet.add(new Obrigacao("DACON"));
        obrigacaoSet.add(new Obrigacao("RAIS"));
        obrigacaoSet.add(new Obrigacao("DCTF"));
        obrigacaoSet.add(new Obrigacao("SIMPLES PAULISTA"));
        obrigacaoSet.add(new Obrigacao("DES"));
        obrigacaoSet.add(new Obrigacao("PJSI"));
        obrigacaoSet.add(new Obrigacao("DMED"));
        obrigacaoSet.add(new Obrigacao("DITR"));
        obrigacaoSet.add(new Obrigacao("SPED CONTABIL"));
        obrigacaoSet.add(new Obrigacao("PJ INATIVA"));
        obrigacaoSet.add(new Obrigacao("STDA SIMPLES NACIONAL"));
        obrigacaoSet.add(new Obrigacao("COAF"));
        obrigacaoSet.add(new Obrigacao("DSUP"));
        obrigacaoSet.add(new Obrigacao("FCONT"));
        obrigacaoSet.add(new Obrigacao("GIA-ICMS"));
        obrigacaoSet.add(new Obrigacao("IRPF"));
        obrigacaoSet.add(new Obrigacao("PERD COMP"));
        obrigacaoSet.add(new Obrigacao("SISCOSERV"));
        obrigacaoSet.add(new Obrigacao("SPED ICMS IPI"));
        obrigacaoSet.add(new Obrigacao("SPED PIS COFINS"));
        obrigacaoSet.add(new Obrigacao("DAI-PMSP"));
        obrigacaoSet.add(new Obrigacao("ECF CONTABIL"));
        obrigacaoSet.add(new Obrigacao("SEDIF-DESTDA"));
        obrigacaoSet.add(new Obrigacao("IBGE"));
        obrigacaoSet.add(new Obrigacao("SIMPLES NACIONAL-DEFIS"));
        obrigacaoSet.add(new Obrigacao("SIMPLES NACIONAL-PGDASD"));
        obrigacaoSet.add(new Obrigacao("DECLAN-IPM"));

        Collections.sort(obrigacaoSet, Comparator.comparing(Obrigacao::getNome));
        cbDocumento.getItems().add(o);
        cbDocumento.getItems().addAll(obrigacaoSet);
        cbDocumento.setValue(o);

        LocalDate localDate = LocalDate.now();
        int inicial = 2000;
        String ano = "Ano";
        int fim = localDate.getYear()+2;
        cbAno.getItems().add(ano);
        while(inicial<=fim){
            cbAno.getItems().add(String.valueOf(inicial));
            inicial++;
        }
        cbAno.setValue(null);

        String mes = "Mes";
        cbMes.getItems().add(mes);
        cbMes.getItems().addAll(Arrays.asList("01","02","03","04","05","06","07","08","09","10","11","12"));
        cbMes.setValue(mes);

        cbIndiceNome.getItems().addAll(Arrays.asList(1,2,3,4,5,6));
        cbIndiceNome.getSelectionModel().selectFirst();

    }
    private void cancelarProcesso(Stage stage){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Deseja cancelar o processamento?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get()==ButtonType.OK) stage.close();
    }
    private void genericMessage(Alert.AlertType type, String title, String header, String message){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void iniciar(Path origem, Path novaEstrutura, OrdemBusca ordem,String regex, int index){
        verificarEstrutura(novaEstrutura);
        final JFXTextArea area = dialogo();
        Runnable run = () -> {
            StringBuilder builder = new StringBuilder();
            try {
                processar(area, builder, novaEstrutura, origem, ordem, regex,index);
                salvarRelatorio(builder.toString());
                Platform.runLater(() ->genericMessage(Alert.AlertType.INFORMATION,"Pronto","Processo concluido","O processamento foi concuido com sucesso!"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        new Thread(run).start();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        combos();
        tabela();
        tbClientes.getItems().addAll(clientesData);
    }

    @FXML
    void limparVazios(ActionEvent event) {
        if (validarPasta("Caminho não informado ou não existe",
                Paths.get(txLocalizacao.getText()))) {
                removerVazios(Paths.get(txLocalizacao.getText()));
                genericMessage(Alert.AlertType.INFORMATION,"OK","Concluido!", "Processo foi concluido!");
        }
    }
    @FXML
    void mover(ActionEvent event){
        if(cbRaiz.getValue().getNome().equals("Raiz")){
            genericMessage(Alert.AlertType.ERROR,"Erro","Campo obrigatorio!","Informe um valor para a raiz");
            return;
        }
        if(!ckDelimitador.isSelected() && txDelimitadorNome.getText().length()==0){
            genericMessage(Alert.AlertType.ERROR,"Erro","Campo obrigatorio!","Informe um separador para continuar");
            return;
        }
        if(validarPasta("Caminho não informado ou não existe",Paths.get(txLocalizacao.getText())) &&
                validarPasta("Pasta de clientes não foi encontrada",Paths.get(txLocalizacao.getText()))) {
            String est = cbRaiz.getValue()+
                    (cbDocumento.getValue().getNome().equals("Obrigacao/Documento")?"":"\\"+cbDocumento.getValue())+
                    (cbAno.getValue().equals("Ano")?"":"\\"+cbAno.getValue())+
                    (cbMes.getValue().equals("Mes")?"":"\\"+cbMes.getValue());
            Path estrutura = Paths.get(est);
            Path path = Paths.get(txLocalizacao.getText());
            iniciar(path,estrutura, rbIdNome.isSelected()?OrdemBusca.ID:OrdemBusca.CNPJ,
                    ckDelimitador.isSelected()?null:txDelimitadorNome.getText(),
                    cbIndiceNome.getValue()-1);
        }
    }

    @FXML
    void renomear(ActionEvent event){
            if (validarPasta("Caminho não informado ou não existe", Paths.get(txLocalizacao.getText()))) {
               if (rbSubstituirCaracteres.isSelected()) {
                    if (txSVOld.getText().length() == 0) {
                        genericMessage(Alert.AlertType.ERROR,"Erro","Novo valor invalido", "Informe um valor que deseja substituir");
                        return;
                    } else {
                        try {
                            renomear(Paths.get(txLocalizacao.getText()), txSVOld.getText(), txSPNew.getText());
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                    }
               } else {
                   try {
                       addNome(Paths.get(txLocalizacao.getText()));
                   }catch (IOException e){
                       e.printStackTrace();
                   }
               }
               genericMessage(Alert.AlertType.INFORMATION,"OK","Concluido!", "Processo foi concluido!");
            }
    }

    private void processar(JFXTextArea textArea, StringBuilder builder, Path estrutura, Path origem, OrdemBusca ordemBusca, String regex, int index) {
        try {
            Iterator<Path> files = Files.list(origem).iterator();
            while (files.hasNext()) {
                Path arquivo = files.next();
                if (Files.isDirectory(arquivo)) {
                    processar(textArea,builder,estrutura, arquivo, ordemBusca, regex, index);
                } else {
                    Path pathCli = buscarIdOrCnpj(arquivo,clientesData,ordemBusca,regex,index);
//                   if(ordemBusca.equals(OrdemBusca.CNPJ)) pathCli = buscarPorCnpj(arquivo,clientesData,Ordem.INICIO);
//                   else pathCli = buscarPorId(arquivo,clientesData,regex,index);
                    if (pathCli != null){
                        Path fileFinal = mover(estrutura, arquivo, pathCli);
                        if(fileFinal!=null){
                            builder.append(arquivo).append(";").append(fileFinal);
                            builder.append(System.getProperty("line.separator"));
                            Platform.runLater(()-> textArea.setText(textArea.getText()+arquivo+"\t->"+fileFinal+"\n"));
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void salvarRelatorio(String text) throws IOException{
        Path path = Paths.get("result.csv");
        if(Files.notExists(path)) Files.createFile(path);
        FileWriter fw = new FileWriter(path.toFile(),true);
        fw.write(text);
        fw.write(System.getProperty("line.separator"));
        fw.flush();
        fw.close();
    }

    @FXML
    void dragExit(DragEvent event){
        txLocalizacao.setStyle("");
    }
    @FXML
    void dragOver(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if(dragboard.hasFiles()){
            Optional<File> file = dragboard.getFiles().stream().findFirst();
            if(file.isPresent()) {
                Path f = file.get().toPath();
                Path path = Paths.get(String.valueOf(Files.isDirectory(f)?f:f.getParent()));
                txLocalizacao.setText(path.toString());
            }
            txLocalizacao.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
        }
    }
    private JFXTextArea dialogo(){
        VBox vBox= new VBox();
        vBox.setPadding(new Insets(20,10,10,10));
        JFXTextArea area = new JFXTextArea();
        area.setMinHeight(400);
        vBox.getChildren().add(area);
        JFXButton buttonCancel = new JFXButton("Cancelar");
        buttonCancel.setStyle("-jfx-button-type: RAISED;");
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.setMinHeight(100);
        hBox.getChildren().add(buttonCancel);
        vBox.getChildren().add(hBox);

        Stage stage = new Stage();
        JFXDecorator decorator2 = new JFXDecorator(stage,vBox);
        decorator2.setCustomMaximize(true);
        Scene scene2 = new Scene(decorator2, 1080, 600);
        stage.setScene(scene2);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();

        //buttonCancel.setOnAction(event1 -> cancelarProcesso(stage));
        //decorator2.setOnCloseButtonAction(() -> cancelarProcesso(stage));

        return area;
    }

    private void tabela(){
        TableColumn<Cliente,Number> colunaId = new TableColumn("Apelido");
        TableColumn<Cliente, String> colunaStatus = new TableColumn("Status");
        TableColumn<Cliente, String> colunaCnpj = new TableColumn("Cnpj");
        TableColumn<Cliente, String> colunaNome = new TableColumn("Nome");

        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colunaCnpj.setCellValueFactory(new PropertyValueFactory<>("cnpj"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        tbClientes.getColumns().addAll(colunaId,colunaStatus,colunaCnpj,colunaNome);
    }

    private boolean validarPasta(String mensagem, Path path){
        boolean exists = false;
        if(path.toString().equals("")) exists = false;
        else exists = Files.exists(path);
        if(exists) return true;
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Caminho incorreto");
            alert.setContentText(mensagem);
            alert.showAndWait();
            return false;
        }
    }

}
