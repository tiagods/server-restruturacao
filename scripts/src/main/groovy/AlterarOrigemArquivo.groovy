import com.mongodb.client.FindIterable
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.ConnectionString;
import com.mongodb.ServerAddress;
import com.mongodb.MongoCredential
import com.mongodb.client.MongoCursor;
import groovy.transform.Field

@Field String MONGO_USER
@Field String MONGO_PASSWORD
@Field String MONGO_DB
@Field String MONGO_HOST
@Field int MONGO_PORT
@Field String MONGO_URI
@Field String env = 'DEV'

def iniciar(){
    def mongoClient = criarCliente()
    def db = mongoClient.getDatabase(MONGO_DB)
    def collection = db.getCollection("arquivo_gfip")
    FindIterable fi = collection.find()
    MongoCursor cursor = fi.iterator()

    while(cursor.hasNext()) {
        println cursor.next()
    }
    mongoClient.close()
}

def criarCliente() {
    MongoClientSettings settings = MONGO_URI && env == 'DEV' ? useUri() : useCredentials()
    MongoClient mongoClient = MongoClients.create(settings)
    mongoClient
}

def useUri() {
    ConnectionString connString = new ConnectionString(MONGO_URI)
    MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connString)
            .retryWrites(true)
            .build()
    settings
}

def useCredentials() {
    MongoCredential credential = MongoCredential.createCredential(MONGO_USER, MONGO_DB, MONGO_PASSWORD.toCharArray());
    MongoClientSettings settings = MongoClientSettings.builder()
            .credential(credential)
//            .applyToSslSettings({builder -> builder.enabled(true)})
            .applyToClusterSettings({builder ->
                    builder.hosts(Arrays.asList(new ServerAddress(MONGO_HOST, MONGO_PORT)))})
            .build()
    settings
}

println 'Iniciando processo...'
iniciar()
println 'Processo finalizado...'