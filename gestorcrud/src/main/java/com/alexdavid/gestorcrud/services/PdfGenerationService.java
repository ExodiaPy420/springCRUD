package com.alexdavid.gestorcrud.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.stereotype.Service;

import com.alexdavid.gestorcrud.models.Invoice;

@Service
public class PdfGenerationService {

    private static final String TEMPLATE_PATH = "invoiceTemplate.pdf"; 

    public ByteArrayInputStream generateInvoicePdf(Invoice invoice) throws IOException {
        // Cargar la plantilla PDF desde el classpath
        InputStream templateStream = getClass().getClassLoader().getResourceAsStream(TEMPLATE_PATH);
        if (templateStream == null) {
            throw new IOException("No se pudo encontrar la plantilla PDF en la ruta: " + TEMPLATE_PATH);
        }

        PDDocument pdfDocument = null;
        try {
            byte[] templateBytes = templateStream.readAllBytes();
            pdfDocument = Loader.loadPDF(templateBytes); 
            
            PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();

            if (acroForm == null) {
                throw new IOException("La plantilla PDF ('" + TEMPLATE_PATH + "') no contiene campos de formulario (AcroForm) editables.");
            }

            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.of("es", "ES"));
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.of("es", "ES"));


            setField(acroForm, "pdf_id_factura", String.valueOf(invoice.getId()));
            setField(acroForm, "pdf_fecha_factura", dateFormatter.format(invoice.getIssueDate()));
            
            if (invoice.getCustomer() != null) {
                setField(acroForm, "pdf_nombre_cliente", invoice.getCustomer().getFullName());
                if (invoice.getCustomer().getSalesrep() != null) {
                    setField(acroForm, "pdf_nombre_comercial", invoice.getCustomer().getSalesrep().getFullName());
                } else {
                    // Si el comercial puede ser nulo, decide qué poner
                    setField(acroForm, "pdf_nombre_comercial", "N/A"); 
                }
            } else {
                // Si el cliente puede ser nulo (no debería si es obligatorio en la BD), decide qué poner
                setField(acroForm, "pdf_nombre_cliente", "N/A"); 
                setField(acroForm, "pdf_nombre_comercial", "N/A");
            }
            
            setField(acroForm, "pdf_monto_factura", currencyFormatter.format(invoice.getTotalAmount()));

            acroForm.flatten(); 

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            pdfDocument.save(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());

        } finally {
            if (pdfDocument != null) {
                pdfDocument.close();
            }
            // El InputStream debemos cerrarlo.
            // La comprobación de nulidad es redundante aquí debido al if anterior.
            templateStream.close(); 
        }
    }

    
    private void setField(PDAcroForm acroForm, String fieldName, String value) throws IOException {
        PDField field = acroForm.getField(fieldName);
        if (field != null) {
            try {
                // Asegurarse de no pasar null a setValue, ya que puede causar errores.
                // Pasar una cadena vacía si el valor es null.
                field.setValue(value != null ? value : ""); 
            } catch (IllegalArgumentException e) {
                // Esto puede ocurrir si el tipo de campo no es compatible con setValue(String) o con restricciones de campo
                System.err.println("Error al establecer el valor para el campo PDF '" + fieldName + "' con valor '" + value + "': " + e.getMessage());
            }
        } else {
            // log de errores
            System.err.println("Advertencia: Campo PDF no encontrado en la plantilla: '" + fieldName + "'. No se rellenará.");
        }
    }
}
