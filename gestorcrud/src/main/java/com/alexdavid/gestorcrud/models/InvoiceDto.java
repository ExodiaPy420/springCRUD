package com.alexdavid.gestorcrud.models;

import java.math.BigDecimal;
import java.util.Date; // Para el binding de fechas

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public class InvoiceDto {

    @NotNull(message = "La fecha de emisión es obligatoria.")
    @PastOrPresent(message = "La fecha de emisión no puede ser futura.")
    @DateTimeFormat(pattern = "yyyy-MM-dd") // Ayuda al binding desde el input type="date"
    private Date issueDate;

    @NotNull(message = "El total es obligatorio.")
    @DecimalMin(value = "0.01", message = "El total debe ser mayor que cero.") // inclusive = false se maneja mejor con 0.01
    private BigDecimal totalAmount;

    @NotEmpty(message = "El estado es obligatorio.")
    @Size(max = 20, message = "El estado no puede exceder los 20 caracteres.")
    private String status;

    @NotNull(message = "El ID del cliente es obligatorio.")
    private Integer customerId;

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
}
