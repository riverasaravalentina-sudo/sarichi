package com.sarichi.crocheting.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.sarichi.crocheting.entity.Producto;

@Repository
public interface ProductoRepository extends MongoRepository<Producto, String> {

    List<Producto> findByEstado(String estado);

    List<Producto> findByCategoriaAndEstado(String categoria, String estado);

    List<Producto> findByEstadoAndStockGreaterThan(String estado, int stock);

    @Query("{ 'estado': 'ACTIVO', 'stock': { $gt: 0 }, "
         + "$or: [ { 'nombre': { $regex: ?0, $options: 'i' } }, "
         +         "{ 'descripcion': { $regex: ?0, $options: 'i' } } ] }")
    List<Producto> buscarPorTexto(String texto);

    List<Producto> findByEstadoAndStockLessThanEqual(String estado, int stock);

    long countByEstadoAndStockLessThanEqual(String estado, int stock);
}