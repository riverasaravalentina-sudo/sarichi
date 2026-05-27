// MongoDB Indexes para Sprint 5 - Ejecutar en MongoDB Atlas Console
// db.collection.createIndex() para optimizar queries

// Blog Indexes
db.articulosBlog.createIndex({ "slug": 1 }, { "unique": true });
db.articulosBlog.createIndex({ "estado": 1, "fechaPublicacion": -1 });
db.articulosBlog.createIndex({ "categorias": 1 });
db.articulosBlog.createIndex({ "estado": 1 });

// Gallery Indexes
db.coleccionesGaleria.createIndex({ "slug": 1 }, { "unique": true });
db.coleccionesGaleria.createIndex({ "estado": 1 });
db.fotosGaleria.createIndex({ "idColeccion": 1 });
db.fotosGaleria.createIndex({ "idProducto": 1 });
db.fotosGaleria.createIndex({ "fechaPublicacion": -1 });

// Challenges Indexes
db.retosMensuales.createIndex({ "fechaInicio": 1, "fechaFin": 1 });
db.retosMensuales.createIndex({ "estado": 1 });
db.participacionesReto.createIndex({ "idReto": 1 });
db.participacionesReto.createIndex({ "idReto": 1, "votos": -1 });
db.participacionesReto.createIndex({ "idReto": 1, "idUsuario": 1 }, { "unique": true });

// Analytics Indexes
db.eventosTrafico.createIndex({ "fecha": -1 });
db.eventosTrafico.createIndex({ "tipo": 1 });
db.eventosTrafico.createIndex({ "fuente": 1, "fecha": -1 });
db.eventosTrafico.createIndex({ "sessionId": 1 });
