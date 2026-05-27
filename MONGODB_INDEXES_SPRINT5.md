# MongoDB Indexes Creation Script - Sprint 5
# Run this in MongoDB Atlas Console to optimize performance

## Blog Indexes
```javascript
db.articulosBlog.createIndex({ "slug": 1 }, { "unique": true });
db.articulosBlog.createIndex({ "estado": 1, "fechaPublicacion": -1 });
db.articulosBlog.createIndex({ "categorias": 1 });
```

## Gallery Indexes
```javascript
db.coleccionesGaleria.createIndex({ "slug": 1 }, { "unique": true });
db.coleccionesGaleria.createIndex({ "estado": 1 });
db.fotosGaleria.createIndex({ "idColeccion": 1 });
db.fotosGaleria.createIndex({ "idProducto": 1 });
db.fotosGaleria.createIndex({ "fechaPublicacion": -1 });
```

## Challenges Indexes
```javascript
db.retosMensuales.createIndex({ "fechaInicio": 1, "fechaFin": 1 });
db.retosMensuales.createIndex({ "estado": 1 });
db.participacionesReto.createIndex({ "idReto": 1 });
db.participacionesReto.createIndex({ "idReto": 1, "votos": -1 });
db.participacionesReto.createIndex({ "idReto": 1, "idUsuario": 1 }, { "unique": true });
```

## Analytics Indexes
```javascript
db.eventosTrafico.createIndex({ "fecha": -1 });
db.eventosTrafico.createIndex({ "tipo": 1 });
db.eventosTrafico.createIndex({ "fuente": 1, "fecha": -1 });
db.eventosTrafico.createIndex({ "sessionId": 1 });
```

## How to Run
1. Go to MongoDB Atlas Console (Data > Tools > Aggregation Pipeline)
2. Copy and paste the relevant index creation commands
3. Execute each command separately
4. Verify indexes were created: `db.collection.getIndexes()`
