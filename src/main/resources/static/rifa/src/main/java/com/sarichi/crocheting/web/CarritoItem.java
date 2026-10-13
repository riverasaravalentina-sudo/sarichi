package com.sarichi.crocheting.web;

import java.io.Serializable;
import java.util.*;

/**
 * Clase que representa un item en el carrito de compras.
 * Se guarda en sesión HTTP.
 */
public class CarritoItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String productoId;
    private String nombre;
    private double precio;
    private int cantidad;
    
    public CarritoItem(String productoId, String nombre, double precio, int cantidad) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }
    
    public double getSubtotal() {
        return precio * cantidad;
    }
    
    // Getters y Setters
    public String getProductoId() { return productoId; }
    public void setProductoId(String productoId) { this.productoId = productoId; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
