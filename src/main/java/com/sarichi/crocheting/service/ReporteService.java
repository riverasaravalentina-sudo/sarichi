package com.sarichi.crocheting.service;

import com.sarichi.crocheting.dto.ReporteVentasDTO;
import com.sarichi.crocheting.dto.ReporteInventarioDTO;
import com.sarichi.crocheting.exception.ReporteException;
import com.sarichi.crocheting.repository.PedidoRepository;
import com.sarichi.crocheting.repository.ProductoRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ReporteService {
    
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public byte[] generarReporteVentasPDF(LocalDateTime desde, LocalDateTime hasta) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            
            document.add(new Paragraph("REPORTE DE VENTAS").setBold());
            document.add(new Paragraph("Período: " + desde.format(FORMATTER) + " - " + hasta.format(FORMATTER)));
            
            long totalPedidos = pedidoRepository.count();
            document.add(new Paragraph("Total de pedidos: " + totalPedidos));
            
            Table table = new Table(4);
            table.addCell(new Cell().add(new Paragraph("ID")));
            table.addCell(new Cell().add(new Paragraph("Usuario")));
            table.addCell(new Cell().add(new Paragraph("Total")));
            table.addCell(new Cell().add(new Paragraph("Fecha")));
            
            document.add(table);
            document.close();
            
            return baos.toByteArray();
        } catch (Exception e) {
            throw new ReporteException("Error generando PDF: " + e.getMessage(), e);
        }
    }
    
    public byte[] generarReporteInventarioExcel() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Inventario");
            
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Producto");
            headerRow.createCell(1).setCellValue("Stock");
            headerRow.createCell(2).setCellValue("Precio");
            headerRow.createCell(3).setCellValue("Estado");
            
            int rowNum = 1;
            long totalProductos = productoRepository.count();
            
            Row dataRow = sheet.createRow(rowNum);
            dataRow.createCell(0).setCellValue("Total de productos: " + totalProductos);
            
            workbook.write(baos);
            workbook.close();
            
            return baos.toByteArray();
        } catch (Exception e) {
            throw new ReporteException("Error generando Excel: " + e.getMessage(), e);
        }
    }
    
    public ReporteVentasDTO obtenerDatosVentas(LocalDateTime desde, LocalDateTime hasta) {
        return ReporteVentasDTO.builder()
                .periodo("Desde " + desde.format(FORMATTER) + " hasta " + hasta.format(FORMATTER))
                .totalVentas(0.0)
                .cantidadPedidos(pedidoRepository.count())
                .promedioPedido(0.0)
                .build();
    }
    
    public ReporteInventarioDTO obtenerDatosInventario() {
        return ReporteInventarioDTO.builder()
                .productosActivos(productoRepository.count())
                .productosAgotados(0L)
                .stockCritico(0L)
                .valorTotalInventario(0.0)
                .build();
    }
}
